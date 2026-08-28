package com.test1.PlantsVsZombies.src.Model.User;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test1.PlantsVsZombies.src.Enums.*;
import com.test1.PlantsVsZombies.src.Model.Greenhouse.GreenhousePlant;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;
import com.test1.PlantsVsZombies.src.Network.Client.ServerConnection;
import com.test1.PlantsVsZombies.src.Network.MessageType;
import com.test1.PlantsVsZombies.src.Network.NetworkMessage;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Pattern;

/**
 * Client-side facade over the game's user data. Every public method
 * here keeps the exact same signature -- and, as far as any caller can
 * tell, the exact same synchronous behavior -- it always had. The
 * hundreds of call sites across the game that mutate coins/gems/plant
 * unlocks/quest progress/etc through this class and its delegate
 * UserProgressManager are completely unchanged.
 *
 * What changed underneath: this class no longer touches users.json (or
 * any user data) directly. Only the server does that now (see
 * Network.Server.UserDatabase) -- a user can only run the game once a
 * GameServer is already running, and two game processes can safely run
 * against it at once with no race on the shared file, since the server
 * is the sole owner of it. Operations that need the server's
 * authoritative data -- register, login, "stay logged in", leaderboard,
 * username change, forgot/reset password -- go over the network and
 * block the caller until the server responds, exactly like a normal
 * synchronous call.
 *
 * The one exception is updateUser(), the single choke-point nearly
 * every mutation (addCoins, unlockPlant, quest progress, ...) already
 * funneled through even before this change. Since it can fire many
 * times per frame during active gameplay, it does NOT block the caller
 * on a network round trip: it takes an immediate, fully-detached
 * snapshot of the current user (so it never races with further
 * in-place mutation happening on the caller's thread) and hands it to a
 * dedicated background thread to actually send, coalescing down to just
 * the latest snapshot if several saves queue up faster than the network
 * can keep up.
 */
public class UsersManager {
    private static final String STATE_FILE = "jsonFiles/loginstate.json";
    private static final Pattern USERNAME_CHAR_REGEX = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final Pattern PASSWORD_COMPLEXITY_REGEX = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+={}\\[\\]|\\\\:;\"',<>?])\\S{8,}$"
    );
    private static final Pattern EMAIL_USERNAME_REGEX = Pattern.compile(
        "^[a-zA-Z0-9]$|^[a-zA-Z0-9](?!.*\\.\\.)[a-zA-Z0-9._-]*[a-zA-Z0-9]$"
    );
    private static final Pattern EMAIL_DOMAIN_REGEX = Pattern.compile(
        "^[a-zA-Z0-9](?:[a-zA-Z0-9.-]*[a-zA-Z0-9])?\\.[a-zA-Z0-9]{2,}$"
    );

    private static UsersManager instance;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserProgressManager progressManager = UserProgressManager.getInstance();
    private final ArrayBlockingQueue<User> progressSyncQueue = new ArrayBlockingQueue<>(1);
    private User loggedInUser = null;
    private String sessionToken = null;

    private UsersManager() {
        mapper.registerModule(new JavaTimeModule());
        startProgressSyncThread();
    }

    public static UsersManager getInstance() {
        if (instance == null) instance = new UsersManager();
        return instance;
    }

    // ==========================================================
    // Background progress-sync thread
    // ==========================================================
    private void startProgressSyncThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                User snapshot;
                try {
                    snapshot = progressSyncQueue.take(); // blocks until there's something new to send
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                String token = sessionToken;
                if (token == null) continue; // logged out before this snapshot went out; drop it
                NetworkMessage request = NetworkMessage.request(0, MessageType.SAVE_PROGRESS)
                    .put("username", snapshot.getUserName())
                    .put("sessionToken", token)
                    .put("user", snapshot);
                NetworkMessage response = ServerConnection.getInstance().sendRequest(request);
                if (!response.isSuccess()) {
                    System.err.println("[Client] Failed to save progress: " + response.getErrorMessage());
                }
            }
        }, "progress-sync-thread");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Queues the current logged-in user's full progress to be sent to
     * the server in the background. Never blocks the caller on network
     * I/O -- only on a cheap, synchronous, in-memory JSON round-trip that
     * produces a fully detached copy, so it stays correct even though
     * the background thread might still be mid-send of a previous
     * snapshot while gameplay keeps mutating loggedInUser.
     */
    void updateUser() {
        if (loggedInUser == null) return;
        try {
            User snapshot = mapper.readValue(mapper.writeValueAsString(loggedInUser), User.class);
            progressSyncQueue.poll();          // drop a stale, not-yet-sent snapshot if there is one
            progressSyncQueue.offer(snapshot); // queue the latest
        } catch (Exception e) {
            System.err.println("[Client] Failed to prepare progress sync: " + e.getMessage());
        }
    }

    public void setQuestVariablesForCurrentUser(Map<String, String> variables) {
        progressManager.setQuestVariablesForCurrentUser(variables);
    }

    /** Registers a new account. Returns null on success, or an error message. */
    public String addUser(User user) {
        NetworkMessage request = NetworkMessage.request(0, MessageType.REGISTER).put("user", user);
        NetworkMessage response = ServerConnection.getInstance().sendRequest(request);
        return response.isSuccess() ? null : response.getErrorMessage();
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public Collection<User> getAllUsers() {
        NetworkMessage request = NetworkMessage.request(0, MessageType.GET_ALL_USERS);
        NetworkMessage response = ServerConnection.getInstance().sendRequest(request);
        if (!response.isSuccess()) {
            System.err.println("[Client] Failed to fetch leaderboard: " + response.getErrorMessage());
            return new ArrayList<>();
        }
        Object raw = response.getData().get("users");
        return mapper.convertValue(raw, new TypeReference<List<User>>() {
        });
    }

    public boolean checkAndLoadStayLoggedIn() {
        File file = new File(STATE_FILE);
        if (!file.exists() || file.length() == 0) return false;
        try {
            Map<String, String> saved = mapper.readValue(file, new TypeReference<HashMap<String, String>>() {
            });
            String savedUsername = saved.get("username");
            String savedToken = saved.get("sessionToken");
            if (savedUsername == null || savedToken == null) return false;

            NetworkMessage request = NetworkMessage.request(0, MessageType.RESTORE_SESSION)
                .put("username", savedUsername)
                .put("sessionToken", savedToken);
            NetworkMessage response = ServerConnection.getInstance().sendRequest(request);
            if (!response.isSuccess()) return false;

            this.loggedInUser = mapper.convertValue(response.getData().get("user"), User.class);
            this.sessionToken = savedToken;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String authenticateUser(String username, String password, boolean stayLoggedIn) {
        NetworkMessage request = NetworkMessage.request(0, MessageType.LOGIN)
            .put("username", username)
            .put("password", password);
        NetworkMessage response = ServerConnection.getInstance().sendRequest(request);
        if (!response.isSuccess()) {
            return response.getErrorMessage();
        }

        this.loggedInUser = mapper.convertValue(response.getData().get("user"), User.class);
        this.sessionToken = (String) response.getData().get("sessionToken");
        QuestManager.getInstance().loadProgress();

        if (stayLoggedIn) {
            saveRememberedLogin(username, sessionToken);
        }
        return null;
    }

    private void saveRememberedLogin(String username, String token) {
        try {
            Map<String, String> toSave = new HashMap<>();
            toSave.put("username", username);
            toSave.put("sessionToken", token);
            mapper.writeValue(new File(STATE_FILE), toSave);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save login state.", e);
        }
    }

    public int getMiniGameLevel(MiniGameType type) {
        return progressManager.getMiniGameLevel(type);
    }

    public void handleMiniGameWin(MiniGameType type, int levelCompleted) {
        progressManager.handleMiniGameWin(type, levelCompleted);
    }

    public void logoutCurrentUser() {
        if (loggedInUser != null && sessionToken != null) {
            NetworkMessage request = NetworkMessage.request(0, MessageType.LOGOUT)
                .put("username", loggedInUser.getUserName())
                .put("sessionToken", sessionToken);
            ServerConnection.getInstance().sendRequest(request);
        }
        this.loggedInUser = null;
        this.sessionToken = null;
        File file = new File(STATE_FILE);
        if (file.exists()) file.delete();
    }

    public String validateAndChangeNickname(String newNickname) {
        if (loggedInUser == null) return "No logged in user.";
        if (loggedInUser.getNickName().equals(newNickname))
            return "you are already using this nickname.";
        if (newNickname.length() < 3 || newNickname.length() > 30)
            return "Invalid nickname length: Must be between 3 and 30 characters.";
        loggedInUser.setNickName(newNickname);
        updateUser();
        return null;
    }

    public String validateAndChangePassword(String newPassword, String newPasswordConfirmed, String oldPassword) {
        if (loggedInUser == null) return "No logged in user.";
        if (!loggedInUser.getPassword().equals(oldPassword))
            return "Invalid password: Old password does not match.";
        if (loggedInUser.getPassword().equals(newPassword))
            return "you are already using this password.";
        if (newPassword.contains(" "))
            return "Weak password: Spaces are not allowed within password strings.";
        if (!newPassword.equals(newPasswordConfirmed))
            return "Password and its confirmation do not match.";
        if (!PASSWORD_COMPLEXITY_REGEX.matcher(newPassword).matches())
            return "Weak password: Must be at least 8 characters long and include numbers, " +
                "uppercase/lowercase letters, and special characters.";
        loggedInUser.setPassword(newPassword);
        updateUser();
        return null;
    }

    public String validateAndChangeEmail(String newEmail) {
        if (loggedInUser == null) return "No logged in user.";
        if (loggedInUser.getEmail().equals(newEmail))
            return "you are already using this email.";
        if (newEmail.length() > 200)
            return "Invalid email: Length cannot exceed 200 characters.";
        String[] emailParts = newEmail.split("@");
        if (emailParts.length != 2 || emailParts[0].isEmpty() || emailParts[1].isEmpty())
            return "Invalid email structure: Must contain exactly one '@' symbol.";
        if (!EMAIL_USERNAME_REGEX.matcher(emailParts[0]).matches())
            return "Invalid email username: Must start/end with alphanumeric characters and contain no consecutive dots.";
        if (!EMAIL_DOMAIN_REGEX.matcher(emailParts[1]).matches())
            return "Invalid email domain: Must include a valid extension layout (minimum 2 characters).";
        loggedInUser.setEmail(newEmail);
        updateUser();
        return null;
    }

    public String validateAndChangeUsername(String newUsername) {
        if (loggedInUser == null) return "No logged in user.";
        if (loggedInUser.getUserName().equals(newUsername))
            return "you are already using this username.";
        if (newUsername.length() < 3 || newUsername.length() > 20)
            return "Invalid username length: Must be between 3 and 20 characters.";
        if (!USERNAME_CHAR_REGEX.matcher(newUsername).matches())
            return "Invalid username characters: Only letters, numbers, and underscores are allowed.";

        NetworkMessage request = NetworkMessage.request(0, MessageType.CHANGE_USERNAME)
            .put("username", loggedInUser.getUserName())
            .put("sessionToken", sessionToken)
            .put("newUsername", newUsername);
        NetworkMessage response = ServerConnection.getInstance().sendRequest(request);
        if (!response.isSuccess()) return response.getErrorMessage();

        loggedInUser.setUserName(newUsername); // same package as User, so this package-private setter is reachable
        if (new File(STATE_FILE).exists()) {
            saveRememberedLogin(newUsername, sessionToken);
        }
        return null;
    }

    /**
     * Format-only validation the client can do without the server (no
     * network round trip needed just to reject an obviously-too-short
     * username or a weak password). The one check that genuinely needs
     * the server -- is this username already taken -- happens
     * server-side in UserDatabase.register(), since only the server has
     * the authoritative, complete user list.
     */
    public String validateRegistration(String username, String password, String passwordConfirm,
                                       String nickname, String email, String gender) {
        if (username.length() < 3 || username.length() > 20)
            return "Invalid username length: Must be between 3 and 20 characters.";
        if (!USERNAME_CHAR_REGEX.matcher(username).matches())
            return "Invalid username characters: Only letters, numbers, and underscores are allowed.";

        if (password.contains(" "))
            return "Weak password: Spaces are not allowed as they break command parsing limits.";
        if (!PASSWORD_COMPLEXITY_REGEX.matcher(password).matches())
            return "Weak password: Must be at least 8 characters and contain lowercase, " +
                "uppercase, numeric digits, and special symbols.";
        if (!password.equals(passwordConfirm))
            return "Password confirmation mismatch: Passwords do not match.";

        if (nickname.length() < 3 || nickname.length() > 30)
            return "Invalid nickname length: Must be between 3 and 30 characters.";

        if (email.length() > 200)
            return "Invalid email: Length cannot exceed 200 characters.";
        String[] emailParts = email.split("@");
        if (emailParts.length != 2 || emailParts[0].isEmpty() || emailParts[1].isEmpty())
            return "Invalid email structure: Must contain exactly one '@' symbol.";
        if (!EMAIL_USERNAME_REGEX.matcher(emailParts[0]).matches())
            return "Invalid email username: Must start/end with alphanumeric characters and contain no consecutive dots.";
        if (!EMAIL_DOMAIN_REGEX.matcher(emailParts[1]).matches())
            return "Invalid email domain: Must include a valid extension layout (minimum 2 characters).";

        if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female"))
            return "Invalid gender: Choice must be exactly 'Male' or 'Female'.";

        return null;
    }

    public String validateForgetPasswordRequest(String username, String email, String answer) {
        NetworkMessage request = NetworkMessage.request(0, MessageType.FORGOT_PASSWORD)
            .put("username", username)
            .put("email", email)
            .put("answer", answer);
        NetworkMessage response = ServerConnection.getInstance().sendRequest(request);
        return response.isSuccess() ? null : response.getErrorMessage();
    }

    public String updateUserPassword(String username, String newPassword) {
        NetworkMessage request = NetworkMessage.request(0, MessageType.RESET_PASSWORD)
            .put("username", username)
            .put("newPassword", newPassword);
        NetworkMessage response = ServerConnection.getInstance().sendRequest(request);
        return response.isSuccess() ? null : response.getErrorMessage();
    }

    public void addCoins(int amount) {
        progressManager.addCoins(amount);
    }

    public void addGems(int amount) {
        progressManager.addGems(amount);
    }

    public String subtractCoins(int amount) {
        return progressManager.subtractCoins(amount);
    }

    public String subtractGems(int amount) {
        return progressManager.subtractGems(amount);
    }

    public void addSeedPackets(PlantType plant, int amount) {
        progressManager.addSeedPackets(plant, amount);
    }

    public void addPlantFood(int amount) {
        progressManager.addPlantFood(amount);
    }

    public void reducePlantFood(int amount){
        progressManager.reducePlantFood(amount);
    }

    public void unlockPlant(PlantType plantType) {
        progressManager.unlockPlant(plantType);
    }

    public void unlockZombie(ZombieType zombieType) {
        progressManager.unlockZombie(zombieType);
    }

    public void unlockChapter(ChapterType chapterType) {
        progressManager.unlockChapter(chapterType);
    }

    public void markLevelCompleted(ChapterType chapterType, int level) {
        progressManager.markLevelCompleted(chapterType, level);
    }

    public String purchasePlant(String plantName) {
        return progressManager.purchasePlant(plantName);
    }

    public String upgradePlant(String plantName) {
        return progressManager.upgradePlant(plantName);
    }

    public void unlockPot(int x, int y) {
        progressManager.unlockPot(x, y);
    }

    public void addPots(int amount) {
        progressManager.addPots(amount);
    }

    public void plantInPot(int x, int y, GreenhousePlant plant) {
        progressManager.plantInPot(x, y, plant);
    }

    public void removePlantFromPot(int x, int y) {
        progressManager.removePlantFromPot(x, y);
    }

    public void addGreenhouseBoost(PlantType plant) {
        progressManager.addGreenhouseBoost(plant);
    }

    public boolean hasGreenhouseBoost(PlantType plant) {
        return progressManager.hasGreenhouseBoost(plant);
    }

    public void consumeGreenhouseBoost(PlantType plant) {
        progressManager.consumeGreenhouseBoost(plant);
    }

    public Set<PlantType> takeAndClearGreenhouseBoosts() {
        return progressManager.takeAndClearGreenhouseBoosts();
    }

    public void acceleratePlant(int x, int y) {
        progressManager.acceleratePlant(x, y);
    }

    public void markDailyOfferPurchased() {
        progressManager.markDailyOfferPurchased();
    }

    public boolean isDailyOfferBoughtToday() {
        return progressManager.isDailyOfferBoughtToday();
    }

    public void saveDailyOffer(PlantType plantType, int price, int seedPacketCount, LocalDate generatedDate) {
        progressManager.saveDailyOffer(plantType, price, seedPacketCount, generatedDate);
    }

    public void setQuestProgressForCurrentUser(Map<String, Integer> progress) {
        progressManager.setQuestProgressForCurrentUser(progress);
    }

    public void setCompletedQuestIdsForCurrentUser(List<String> completed) {
        progressManager.setCompletedQuestIdsForCurrentUser(completed);
    }

    public void setClaimedQuestIdsForCurrentUser(List<String> claimed) {
        progressManager.setClaimedQuestIdsForCurrentUser(claimed);
    }

    public void setLastDailyResetForCurrentUser(LocalDate date) {
        progressManager.setLastDailyResetForCurrentUser(date);
    }

    public void incrementMiniGamesCompleted() {
        progressManager.incrementMiniGamesCompleted();
    }

    public void incrementDailyQuestsCompleted() {
        progressManager.incrementDailyQuestsCompleted();
    }

    public void incrementNonDailyQuestsCompleted() {
        progressManager.incrementNonDailyQuestsCompleted();
    }

    public String cheat(int amount, WalletType walletType) {
        return progressManager.cheat(amount, walletType);
    }

    public void handleLevelWin(ChapterType chapterType, int currentLevel,
                               ArrayList<PlantType> plantRewards) {
        progressManager.handleLevelWin(chapterType, currentLevel, plantRewards);
    }

    public void addGamesPlayed(){
        progressManager.addGamesPlayed();
    }

    public ArrayList<String> getUnreadNews() {
        if (loggedInUser == null) return new ArrayList<>();
        ArrayList<String> news = loggedInUser.getNewsManager().extractUnreadNews();
        updateUser();
        return news;
    }

    public ArrayList<String> getAllNews() {
        if (loggedInUser == null) return new ArrayList<>();
        ArrayList<String> news = loggedInUser.getNewsManager().extractAllNews();
        updateUser();
        return news;
    }

    public String changeDifficulty(String difficultyLevel) {
        if (loggedInUser == null) return "No logged in user.";
        int difficulty;
        try {
            difficulty = Integer.parseInt(difficultyLevel);
        } catch (NumberFormatException e) {
            return "Invalid difficulty level: Must be a number between 1 and 5.";
        }
        if (difficulty < 1 || difficulty > 5)
            return "Invalid difficulty level: Must be between 1 and 5.";
        loggedInUser.getUserProgress().setGameDifficulty(difficulty);
        updateUser();
        return null;
    }

    public String setGameSpeed(int speed) {
        if (loggedInUser == null) return "No logged in user.";
        if (speed < 1 || speed > 3) return "Speed must be 1, 2, or 3.";
        loggedInUser.getUserProgress().setGameSpeed(speed);
        updateUser();
        return null;
    }

    public int getGameSpeed() {
        if (loggedInUser == null) return 1;
        return loggedInUser.getUserProgress().getGameSpeed();
    }

    public String setShowTileGrid(boolean show) {
        if (loggedInUser == null) return "No logged in user.";
        loggedInUser.getUserProgress().setShowTileGrid(show);
        updateUser();
        return null;
    }

    public boolean isShowTileGrid() {
        if (loggedInUser == null) return false;
        return loggedInUser.getUserProgress().isShowTileGrid();
    }

    public void setDebugMode(boolean debug) {
        if (loggedInUser != null) {
            loggedInUser.setDebugMode(debug);
            updateUser();
        }
    }

    public boolean isDebugMode() {
        return loggedInUser != null && loggedInUser.isDebugMode();
    }
}
