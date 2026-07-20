package src.Model.User;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Enums.WalletType;
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

        if(loggedInUser.getEmail().equals(newEmail)){
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


    public String validateAndChangeUsername(String newUsername) {
        if (loggedInUser == null) {
            return "No logged in user found.";
        }

        if(loggedInUser.getUserName().equals(newUsername)){
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

    private void updateUser(){
        userCache.put(loggedInUser.getUserName(), loggedInUser);
        writeUsers();
    }

    public void unlockChapter(ChapterType chapterType){
        loggedInUser.unlockChapter(chapterType);
        updateUser();
    }

    public ArrayList<String > getUnreadNews(){
        ArrayList<String > news = loggedInUser.getNewsManager().extractUnreadNews();
        updateUser();
        return news;
    }

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

        if (loggedInUser.getUserProgress().getCoinsCount() < PLANT_PURCHASE_COST) {
            return "Not enough coins! Purchasing a plant costs 2000 coins.";
        }

        loggedInUser.getUserProgress().addCoins(-PLANT_PURCHASE_COST);
        unlockedPlants.put(plantType, 1);

        updateUser();

        return null;
    }


    public ArrayList<String > getAllNews(){
        ArrayList<String > news = loggedInUser.getNewsManager().extractAllNews();
        updateUser();
        return news;
    }

}
