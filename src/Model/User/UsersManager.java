package src.Model.User;

import src.Enums.*;
import src.Model.Greenhouse.GreenhousePlant;
import src.Model.Quests.QuestManager;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class UsersManager {
    private static UsersManager instance;
    private static final String FILE_PATH = "users.json";
    private static final String STATE_FILE = "loginstate.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private HashMap<String, User> userCache = new HashMap<>();
    private User loggedInUser = null;

    
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

    private UsersManager() {
        loadUsers();
    }

    public static UsersManager getInstance() {
        if (instance == null) instance = new UsersManager();
        return instance;
    }


    public void setQuestVariablesForCurrentUser(Map<String, String> variables) {
        progressManager.setQuestVariablesForCurrentUser(variables);
    }


    private void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            userCache = new HashMap<>();
            return;
        }
        try {
            userCache = mapper.readValue(file, new TypeReference<HashMap<String, User>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to read users.json", e);
        }
    }

    private void writeUsers() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), userCache);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write users.json", e);
        }
    }

    
    void updateUser() {
        if (loggedInUser != null) {
            userCache.put(loggedInUser.getUserName(), loggedInUser);
            writeUsers();
        }
    }

    
    public void addUser(User user) {
        userCache.put(user.getUserName(), user);
        writeUsers();
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public Collection<User> getAllUsers() {
        return userCache.values();
    }

    public boolean checkAndLoadStayLoggedIn() {
        File file = new File(STATE_FILE);
        if (!file.exists() || file.length() == 0) return false;
        try {
            String savedUsername = mapper.readValue(file, String.class);
            if (userCache.containsKey(savedUsername)) {
                this.loggedInUser = userCache.get(savedUsername);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public String authenticateUser(String username, String password, boolean stayLoggedIn) {
        if (!userCache.containsKey(username)) {
            return "Entered username does not exist in the system.";
        }
        User user = userCache.get(username);
        if (!user.getPassword().equals(password)) {
            return "Invalid password credentials.";
        }
        this.loggedInUser = user;
        QuestManager.getInstance().loadProgress();
        if (stayLoggedIn) {
            try {
                mapper.writeValue(new File(STATE_FILE), username);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save login state.", e);
            }
        }
        return null;
    }

    public int getMiniGameLevel(MiniGameType type) {
        return progressManager.getMiniGameLevel(type);
    }

    
    public void handleMiniGameWin(MiniGameType type, int levelCompleted) {
        progressManager.handleMiniGameWin(type, levelCompleted);
    }


    public void logoutCurrentUser() {
        this.loggedInUser = null;
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

    public String validateAndChangePassword(String newPassword, String oldPassword) {
        if (loggedInUser == null) return "No logged in user.";
        if (!loggedInUser.getPassword().equals(oldPassword))
            return "Invalid password: Old password does not match.";
        if (loggedInUser.getPassword().equals(newPassword))
            return "you are already using this password.";
        if (newPassword.contains(" "))
            return "Weak password: Spaces are not allowed within password strings.";
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
        if (userCache.containsKey(newUsername))
            return "Duplicate username: User already exists in the system.";
        if (newUsername.length() < 3 || newUsername.length() > 20)
            return "Invalid username length: Must be between 3 and 20 characters.";
        if (!USERNAME_CHAR_REGEX.matcher(newUsername).matches())
            return "Invalid username characters: Only letters, numbers, and underscores are allowed.";

        String oldUsername = loggedInUser.getUserName();
        userCache.remove(oldUsername);
        loggedInUser.setUserName(newUsername);
        userCache.put(newUsername, loggedInUser);
        writeUsers();

        
        File stateFile = new File(STATE_FILE);
        if (stateFile.exists()) {
            try {
                mapper.writeValue(stateFile, newUsername);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update login state data.", e);
            }
        }
        return null;
    }

    
    public String validateRegistration(String username, String password, String passwordConfirm,
                                       String nickname, String email, String gender) {
        if (userCache.containsKey(username))
            return "Duplicate username: User already exists in the system.";
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
        if (!userCache.containsKey(username))
            return "Error: Entered username does not exist in the system.";
        User user = userCache.get(username);
        if (!user.getEmail().equalsIgnoreCase(email))
            return "Error: Provided email does not match registered user profile.";
        if (user.getSecurityAnswer() == null || !user.getSecurityAnswer().equalsIgnoreCase(answer))
            return "Error: Security challenge answer is incorrect.";
        return null;
    }

    public String updateUserPassword(String username, String newPassword) {
        if (newPassword.contains(" "))
            return "Weak password: Spaces are not allowed within password strings.";
        if (!PASSWORD_COMPLEXITY_REGEX.matcher(newPassword).matches())
            return "Weak password: Must be at least 8 characters long and include numbers, " +
                    "uppercase/lowercase letters, and special characters.";
        User user = userCache.get(username);
        if (user != null) {
            user.setPassword(newPassword);
            writeUsers();
        }
        return null;
    }

    
    private final UserProgressManager progressManager = UserProgressManager.getInstance();

    public void addCoins(int amount) { progressManager.addCoins(amount); }
    public void addGems(int amount) { progressManager.addGems(amount); }
    public String subtractCoins(int amount) { return progressManager.subtractCoins(amount); }
    public String subtractGems(int amount) { return progressManager.subtractGems(amount); }

    public void addSeedPackets(PlantType plant, int amount) { progressManager.addSeedPackets(plant, amount); }
    public void addPlantFood(int amount) { progressManager.addPlantFood(amount); }

    public void unlockPlant(PlantType plantType) { progressManager.unlockPlant(plantType); }
    public void unlockZombie(ZombieType zombieType) { progressManager.unlockZombie(zombieType); }
    public void unlockChapter(ChapterType chapterType) { progressManager.unlockChapter(chapterType); }
    public void unlockLevel(ChapterType chapterType, int level) { progressManager.unlockLevel(chapterType, level); }

    public String purchasePlant(String plantName) { return progressManager.purchasePlant(plantName); }
    public String upgradePlant(String plantName) { return progressManager.upgradePlant(plantName); }

    public void unlockPot(int x, int y) { progressManager.unlockPot(x, y); }
    public void addPots(int amount) { progressManager.addPots(amount); }
    public void plantInPot(int x, int y, GreenhousePlant plant) { progressManager.plantInPot(x, y, plant); }
    public void removePlantFromPot(int x, int y) { progressManager.removePlantFromPot(x, y); }
    public void addGreenhouseBoost(PlantType plant) { progressManager.addGreenhouseBoost(plant); }
    public boolean hasGreenhouseBoost(PlantType plant) { return progressManager.hasGreenhouseBoost(plant); }
    public void consumeGreenhouseBoost(PlantType plant) { progressManager.consumeGreenhouseBoost(plant); }
    public void acceleratePlant(int x, int y) { progressManager.acceleratePlant(x, y); }

    public void markDailyOfferPurchased() { progressManager.markDailyOfferPurchased(); }
    public boolean isDailyOfferBoughtToday() { return progressManager.isDailyOfferBoughtToday(); }

    public void setQuestProgressForCurrentUser(Map<String, Integer> progress) { progressManager.setQuestProgressForCurrentUser(progress); }
    public void setCompletedQuestIdsForCurrentUser(List<String> completed) { progressManager.setCompletedQuestIdsForCurrentUser(completed); }
    public void setClaimedQuestIdsForCurrentUser(List<String> claimed) { progressManager.setClaimedQuestIdsForCurrentUser(claimed); }
    public void setLastDailyResetForCurrentUser(LocalDate date) { progressManager.setLastDailyResetForCurrentUser(date); }

    public void incrementMiniGamesCompleted() { progressManager.incrementMiniGamesCompleted(); }
    public void incrementDailyQuestsCompleted() { progressManager.incrementDailyQuestsCompleted(); }
    public void incrementNonDailyQuestsCompleted() { progressManager.incrementNonDailyQuestsCompleted(); }

    public String cheat(int amount, WalletType walletType) { return progressManager.cheat(amount, walletType); }

    public void handleLevelWin(ChapterType chapterType, int currentLevel,
                               ArrayList<PlantType> plantRewards) {
        progressManager.handleLevelWin(chapterType, currentLevel, plantRewards);
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
}