package src.Model.User;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Enums.WalletType;
import src.Enums.ZombieType;
import src.Model.Greenhouse.GreenhousePlant;
import src.Model.Quests.QuestManager;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class UsersManager {
    private static UsersManager instance;
    private static final String FILE_PATH = "users.json";
    private static final String STATE_FILE = "loginstate.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private HashMap<String, User> userCache = new HashMap<>();
    private User loggedInUser = null;

    private static final int PLANT_PURCHASE_COST = 2000;

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
        if (instance == null) {
            instance = new UsersManager();
        }
        return instance;
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
            throw new RuntimeException("Failed to read or parse users.json", e);
        }
    }

    public void addUser(User user){
        userCache.put(user.getUserName(), user);
        writeUsers();
    }

    public String validateAndChangeNickname(String newNickname) {
        if(loggedInUser.getNickName().equals(newNickname)){
            return "you are already using this nickname.";
        }

        if (loggedInUser == null) {
            return "No logged in user found.";
        }

        if (newNickname.length() < 3 || newNickname.length() > 30) {
            return "Invalid nickname length: Must be between 3 and 30 characters.";
        }

        loggedInUser.setNickName(newNickname);
        updateUser();

        return null;
    }

    public String validateAndChangePassword(String newPassword, String oldPassword) {
        if (loggedInUser == null) {
            return "No logged in user found.";
        }


        if (!loggedInUser.getPassword().equals(oldPassword)) {
            return "Invalid password: Old password does not match.";
        }

        if(loggedInUser.getPassword().equals(newPassword)){
            return "you are already using this password.";
        }

        if (newPassword.contains(" ")) {
            return "Weak password: Spaces are not allowed within password strings.";
        }

        if (!PASSWORD_COMPLEXITY_REGEX.matcher(newPassword).matches()) {
            return "Weak password: Must be at least 8 characters long and include numbers, " +
                    "uppercase/lowercase letters, and special characters.";
        }

        loggedInUser.setPassword(newPassword);
        updateUser();

        return null;
    }

    public String validateAndChangeEmail(String newEmail) {
        if (loggedInUser == null) {
            return "No logged in user found.";
        }

        if (loggedInUser.getEmail().equals(newEmail)) {
            return "you are already using this email.";
        }

        if (newEmail.length() > 200) {
            return "Invalid email: Length cannot exceed 200 characters.";
        }

        String[] emailParts = newEmail.split("@");
        if (emailParts.length != 2 || emailParts[0].isEmpty() || emailParts[1].isEmpty()) {
            return "Invalid email structure: Must contain exactly one '@' symbol.";
        }

        if (!EMAIL_USERNAME_REGEX.matcher(emailParts[0]).matches()) {
            return "Invalid email username: Must start/end with alphanumeric characters and contain no consecutive dots.";
        }

        if (!EMAIL_DOMAIN_REGEX.matcher(emailParts[1]).matches()) {
            return "Invalid email domain: Must include a valid extension layout (minimum 2 characters).";
        }

        loggedInUser.setEmail(newEmail);
        updateUser();

        return null;
    }

// file: src/Model/User/UsersManager.java
// Add these methods inside UsersManager


    // Refactor purchasePlant to use subtractCoins
    public String purchasePlant(String plantName) {
        User loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            return "No logged in user found.";
        }

        PlantType plantType = PlantType.fromName(plantName);
        if (plantType == null) {
            return "Plant not found!";
        }

        if (loggedInUser.getUserProgress() == null) {
            return "User progress data is missing.";
        }

        HashMap<PlantType, Integer> unlockedPlants = loggedInUser.getUserProgress().getUnlockedPlantsAndTheirLevels();
        if (unlockedPlants.containsKey(plantType)) {
            return "You already own this plant!";
        }

        // Use subtractCoins
        String error = subtractCoins(PLANT_PURCHASE_COST);
        if (error != null) {
            return error; // e.g., "Insufficient coins..."
        }

        loggedInUser.unlockPlant(plantType);
        updateUser();
        return null;
    }


    public String validateAndChangeUsername(String newUsername) {
        if (loggedInUser == null) {
            return "No logged in user found.";
        }

        if (loggedInUser.getUserName().equals(newUsername)) {
            return "you are already using this username.";
        }

        if (userCache.containsKey(newUsername)) {
            return "Duplicate username: User already exists in the system.";
        }

        if (newUsername.length() < 3 || newUsername.length() > 20) {
            return "Invalid username length: Must be between 3 and 20 characters.";
        }

        if (!USERNAME_CHAR_REGEX.matcher(newUsername).matches()) {
            return "Invalid username characters: Only letters, numbers, and underscores are allowed.";
        }

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


    private void writeUsers() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), userCache);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write users.json", e);
        }
    }

    public String validateRegistration(String username, String password, String passwordConfirm,
                                       String nickname, String email, String gender) {

        if (userCache.containsKey(username)) {
            return "Duplicate username: User already exists in the system.";
        }
        if (username.length() < 3 || username.length() > 20) {
            return "Invalid username length: Must be between 3 and 20 characters.";
        }
        if (!USERNAME_CHAR_REGEX.matcher(username).matches()) {
            return "Invalid username characters: Only letters, numbers, and underscores are allowed.";
        }

        if (password.contains(" ")) {
            return "Weak password: Spaces are not allowed as they break command parsing limits.";
        }
        if (!PASSWORD_COMPLEXITY_REGEX.matcher(password).matches()) {
            return "Weak password: Must be at least 8 characters and contain lowercase, " +
                    "uppercase, numeric digits, and special symbols.";
        }
        if (!password.equals(passwordConfirm)) {
            return "Password confirmation mismatch: Passwords do not match.";
        }

        if (nickname.length() < 3 || nickname.length() > 30) {
            return "Invalid nickname length: Must be between 3 and 30 characters.";
        }

        if (email.length() > 200) {
            return "Invalid email: Length cannot exceed 200 characters.";
        }
        String[] emailParts = email.split("@");
        if (emailParts.length != 2 || emailParts[0].isEmpty() || emailParts[1].isEmpty()) {
            return "Invalid email structure: Must contain exactly one '@' symbol.";
        }
        if (!EMAIL_USERNAME_REGEX.matcher(emailParts[0]).matches()) {
            return "Invalid email username: Must start/end with alphanumeric characters and contain no consecutive dots.";
        }
        if (!EMAIL_DOMAIN_REGEX.matcher(emailParts[1]).matches()) {
            return "Invalid email domain: Must include a valid extension layout (minimum 2 characters).";
        }

        if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female")) {
            return "Invalid gender: Choice must be exactly 'Male' or 'Female'.";
        }

        return null;
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
                throw new RuntimeException("Failed to save login state data.", e);
            }
        }
        return null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void logoutCurrentUser() {
        this.loggedInUser = null;
        File file = new File(STATE_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    public String updateUserPassword(String username, String newPassword) {
        if (newPassword.contains(" ")) {
            return "Weak password: Spaces are not allowed within password strings.";
        }
        if (!PASSWORD_COMPLEXITY_REGEX.matcher(newPassword).matches()) {
            return "Weak password: Must be at least 8 characters long and include numbers, " +
                    "uppercase/lowercase letters, and special characters.";
        }

        User user = userCache.get(username);
        if (user != null) {
            user.setPassword(newPassword);
            writeUsers();
        }
        return null;
    }

    public String validateForgetPasswordRequest(String username, String email, String answer) {
        if (!userCache.containsKey(username)) {
            return "Error: Entered username does not exist in the system.";
        }
        User user = userCache.get(username);
        if (!user.getEmail().equalsIgnoreCase(email)) {
            return "Error: Provided email does not match registered user profile.";
        }
        if (user.getSecurityAnswer() == null || !user.getSecurityAnswer().equalsIgnoreCase(answer)) {
            return "Error: Security challenge answer is incorrect.";
        }
        return null;
    }

    public String cheat(int amount, WalletType walletType) {
        if (loggedInUser == null || loggedInUser.getUserProgress() == null) {
            return "No logged in user found.";
        }
        if (amount <= 0) {
            return "Cheat amount must be positive.";
        }

        UserProgress userProgress = loggedInUser.getUserProgress();
        if (walletType == WalletType.COIN) {
            userProgress.addCoins(amount);
        } else if (walletType == WalletType.DIAMOND) {
            userProgress.addGems(amount);
        } else {
            return "Invalid wallet type.";
        }

        updateUser();
        return null;
    }

    public String changeDifficulty(String difficultyLevel) {
        if (loggedInUser == null || loggedInUser.getUserProgress() == null) {
            return "No logged in user found.";
        }

        int difficulty;
        try {
            difficulty = Integer.parseInt(difficultyLevel);
        } catch (NumberFormatException e) {
            return "Invalid difficulty level: Must be a number between 1 and 5.";
        }

        if (difficulty < 1 || difficulty > 5) {
            return "Invalid difficulty level: Must be between 1 and 5.";
        }

        loggedInUser.getUserProgress().setGameDifficulty(difficulty);
        updateUser();
        return null;
    }

    private void updateUser() {
        if (loggedInUser != null) {
            userCache.put(loggedInUser.getUserName(), loggedInUser);
            writeUsers();
        }
    }

    /**
     * Called when a level is completed successfully.
     * This method:
     * 1. Unlocks the next level in the current chapter (if not level 4)
     * 2. Unlocks the next chapter (if level 4 is completed)
     * 3. Unlocks all reward plants and zombies from the level
     * 4. Saves all changes to the user JSON file
     *
     * @param chapterType The chapter that was completed
     * @param currentLevel The level number that was completed (1-4)
     * @param plantRewards ArrayList of PlantType rewards for this level
     * @param zombieRewards ArrayList of ZombieType rewards for this level
     */
    public void handleLevelWin(ChapterType chapterType, int currentLevel,
                               ArrayList<PlantType> plantRewards,
                               ArrayList<ZombieType> zombieRewards) {


        UserProgress userProgress = loggedInUser.getUserProgress();

        // Get the currently unlocked level for this chapter
        int currentUnlockedLevel = userProgress.getUnlockedChaptersAndLevels()
                .getOrDefault(chapterType, 1);

        // Only unlock next level if the completed level is the currently highest unlocked
        if (currentLevel >= currentUnlockedLevel) {
            if (currentLevel < 4) {
                // Unlock the next level in this chapter
                unlockLevel(chapterType, currentLevel + 1);
            } else if (currentLevel == 4) {
                // Level 4 completed: unlock the next chapter
                ChapterType nextChapter = getNextChapter(chapterType);
                if (nextChapter != null) {
                    unlockChapter(nextChapter);
                }
            }
        }

        // Unlock all reward plants for this level
        if (plantRewards != null && !plantRewards.isEmpty()) {
            for (PlantType plantType : plantRewards) {
                unlockPlant(plantType);
            }
        }

        // Unlock all reward zombies for this level
        if (zombieRewards != null && !zombieRewards.isEmpty()) {
            for (ZombieType zombieType : zombieRewards) {
                unlockZombie(zombieType);
            }
        }

        // Increment games played counter
        userProgress.setGamesPlayed(userProgress.getGamesPlayed() + 1);

        // Save all changes to JSON file
        updateUser();
    }



    public void addPlantFood(int amount) {
        if (loggedInUser == null) return;
        UserProgress progress = loggedInUser.getUserProgress();
        int newCount = Math.max(0, progress.getPlantFoodCount() + amount);
        progress.setPlantFoodCount(newCount);
        updateUser();
    }


    public void addSeedPackets(PlantType plant, int amount) {
        if (loggedInUser == null || amount <= 0) return;
        UserProgress progress = loggedInUser.getUserProgress();
        progress.addSeedPackets(plant, amount);
        updateUser();
    }
    public void markDailyOfferPurchased() {
        if (loggedInUser == null) return;
        UserProgress progress = loggedInUser.getUserProgress();
        progress.setDailyOfferPurchaseDate(java.time.LocalDate.now());
        updateUser();
    }

    /**
     * Checks if the daily offer was already bought today.
     */
    public boolean isDailyOfferBoughtToday() {
        if (loggedInUser == null) return false;
        return loggedInUser.getUserProgress().isDailyOfferBoughtToday();
    }



    /**
     * Helper method to determine the next chapter after the current one.
     * Chapter progression: ANCIENT_EGYPT → DARK_AGE → FROSTBITE_CAVES → BIG_WAVE_BEACH
     */
    private ChapterType getNextChapter(ChapterType currentChapter) {
        switch (currentChapter) {
            case ANCIENT_EGYPT:
                return ChapterType.DARK_AGE;
            case DARK_AGE:
                return ChapterType.FROSTBITE_CAVES;
            case FROSTBITE_CAVES:
                return ChapterType.BIG_WAVE_BEACH;
            case BIG_WAVE_BEACH:
                return null; // No chapter after the last one
            default:
                return null;
        }
    }

    public void unlockChapter(ChapterType chapterType) {
        if (loggedInUser != null) {
            loggedInUser.unlockChapter(chapterType);
            updateUser();
        }
    }

    public ArrayList<String> getUnreadNews() {
        if (loggedInUser == null) return new ArrayList<>();
        ArrayList<String> news = loggedInUser.getNewsManager().extractUnreadNews();
        updateUser();
        return news;
    }



    public void unlockZombie(ZombieType zombieType) {
        if (loggedInUser != null) {
            loggedInUser.unlockZombie(zombieType);
            updateUser();
        }
    }

    public void unlockLevel(ChapterType chapterType, int level) {
        if (loggedInUser != null) {
            loggedInUser.unlockLevel(level, chapterType);
            updateUser();
        }
    }

    public void unlockPlant(PlantType plantType) {
        if (loggedInUser != null) {
            loggedInUser.unlockPlant(plantType);
            updateUser();
        }
    }

    public ArrayList<String> getAllNews() {
        if (loggedInUser == null) return new ArrayList<>();
        ArrayList<String> news = loggedInUser.getNewsManager().extractAllNews();
        updateUser();
        return news;
    }











    public void unlockPot(int x, int y) {
        if (loggedInUser == null) return;
        loggedInUser.getUserProgress().unlockPot(x, y);
        updateUser();
    }

    public void plantInPot(int x, int y, GreenhousePlant plant) {
        if (loggedInUser == null) return;
        loggedInUser.getUserProgress().plantInPot(x, y, plant);
        updateUser();
    }

    public void removePlantFromPot(int x, int y) {
        if (loggedInUser == null) return;
        loggedInUser.getUserProgress().removePlantFromPot(x, y);
        updateUser();
    }

    public void addGreenhouseBoost(PlantType plant) {
        if (loggedInUser == null) return;
        loggedInUser.getUserProgress().addGreenhouseBoost(plant);
        updateUser();
    }

    public boolean hasGreenhouseBoost(PlantType plant) {
        if (loggedInUser == null) return false;
        return loggedInUser.getUserProgress().hasGreenhouseBoost(plant);
    }

    public void consumeGreenhouseBoost(PlantType plant) {
        if (loggedInUser == null) return;
        loggedInUser.getUserProgress().consumeGreenhouseBoost(plant);
        updateUser();
    }

    public void acceleratePlant(int x, int y) {
        if (loggedInUser == null) return;
        GreenhousePlant plant = loggedInUser.getUserProgress().getPotPlants()[y-1][x-1];
        if (plant != null) {
            plant.forceReady();
            updateUser();
        }
    }

    /**
     * Adds pots – unlocks the next locked pots in row‑major order.
     */
    public void addPots(int amount) {
        if (loggedInUser == null || amount <= 0) return;
        UserProgress progress = loggedInUser.getUserProgress();
        for (int i = 0; i < amount; i++) {
            progress.unlockNextPot();
        }
        updateUser();
    }

    // ----- Currency subtraction (no try-catch) -----
    public String subtractCoins(int amount) {
        if (loggedInUser == null) return "No logged in user.";
        UserProgress progress = loggedInUser.getUserProgress();
        if (amount < 0) return "Cannot subtract negative amount.";
        if (progress.getCoinsCount() < amount)
            return "Insufficient coins. You have " + progress.getCoinsCount() + ", need " + amount + ".";
        progress.subtractCoins(amount);
        updateUser();
        return null;
    }

    public String subtractGems(int amount) {
        if (loggedInUser == null) return "No logged in user.";
        UserProgress progress = loggedInUser.getUserProgress();
        if (amount < 0) return "Cannot subtract negative amount.";
        if (progress.getGemsCount() < amount)
            return "Insufficient gems. You have " + progress.getGemsCount() + ", need " + amount + ".";
        progress.subtractGems(amount);
        updateUser();
        return null;
    }

    // ----- Plant upgrade (no try-catch) -----
    public String upgradePlant(String plantName) {
        if (loggedInUser == null) return "No logged in user.";
        PlantType plant = PlantType.fromName(plantName);
        if (plant == null) return "Invalid plant name.";
        UserProgress progress = loggedInUser.getUserProgress();

        if (!progress.getUnlockedPlantsAndTheirLevels().containsKey(plant))
            return "Plant not unlocked.";

        int currentLevel = progress.getUnlockedPlantsAndTheirLevels().get(plant);
        int requiredCoins = currentLevel * 1000;
        int requiredSeedPackets = currentLevel * 5;

        // Check coins
        if (progress.getCoinsCount() < requiredCoins)
            return "Insufficient coins. Need " + requiredCoins + ".";
        // Check seed packets
        if (!progress.hasEnoughSeedPackets(plant, requiredSeedPackets)) {
            int available = progress.getSeedPackets().getOrDefault(plant, 0);
            return "Not enough seed packets. Need " + requiredSeedPackets + ", have " + available + ".";
        }

        // Perform deductions
        progress.subtractCoins(requiredCoins);
        progress.deductSeedPackets(plant, requiredSeedPackets);
        progress.upgradePlant(plant);
        updateUser();
        return null;
    }

    // ----- Other methods (unchanged but we note addCoins/addGems only add positive) -----
    public void addCoins(int amount) {
        if (loggedInUser == null || amount <= 0) return;
        loggedInUser.getUserProgress().addCoins(amount);
        updateUser();
    }

    public void addGems(int amount) {
        if (loggedInUser == null || amount <= 0) return;
        loggedInUser.getUserProgress().addGems(amount);
        updateUser();
    }



}
