package src.Model.User;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

public class UsersManager {
    private static final String FILE_PATH = "users.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private HashMap<String, User> userCache = new HashMap<>();

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

    public UsersManager() {
            loadUsers();
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
    }


    public void writeUsers() {
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
        if (!nickname.trim().equals(nickname)) {
            return "Invalid nickname: Leading or trailing whitespaces are not allowed.";
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

}
