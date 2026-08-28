package com.test1.PlantsVsZombies.src.Network.Server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test1.PlantsVsZombies.src.Model.User.User;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * The single, exclusive owner of users.json. Nothing outside this class
 * -- and specifically nothing running in a client process -- ever reads
 * or writes that file. Clients only ever see User data that travels
 * back over the network as part of a NetworkMessage response.
 *
 * This is the server-side home of the validation/business logic that
 * used to live directly in the client's UsersManager (registration
 * rules, credential checks, etc) -- moved here rather than rewritten,
 * since only the server has the authoritative, complete user list a lot
 * of it depends on (e.g. the duplicate-username check).
 *
 * Every method that touches userCache and/or the file takes the
 * ReadWriteLock appropriately: read-only lookups (leaderboard, login
 * credential check) take a read lock so many can run concurrently;
 * anything that mutates the cache and/or writes the file takes the
 * write lock for the whole operation, so it's atomic with respect to
 * every other client's concurrent request -- this is what makes it safe
 * for many clients (potentially from separate machines/processes) to
 * hit the same server at once without racing on the JSON file.
 */
public class UserDatabase {
    private static final String FILE_PATH = "assets/jsonFiles/users.json";

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

    private final ObjectMapper mapper = new ObjectMapper();
    private final ReadWriteLock lock = new ReadWriteLock();
    private final Map<String, User> userCache;
    /** username -> currently-valid session token, issued at login. */
    private final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    public UserDatabase() {
        mapper.registerModule(new JavaTimeModule());
        userCache = loadFromDisk();
        System.out.println("[Server] Loaded " + userCache.size() + " user(s) from " + FILE_PATH);
    }

    private Map<String, User> loadFromDisk() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<HashMap<String, User>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to read users.json", e);
        }
    }

    /** Caller must already hold the write lock. */
    private void persistToDisk() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), userCache);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write users.json", e);
        }
    }

    public record LoginResult(boolean success, String errorMessage, User user, String sessionToken) {
        static LoginResult ok(User user, String token) {
            return new LoginResult(true, null, user, token);
        }

        static LoginResult error(String message) {
            return new LoginResult(false, message, null, null);
        }
    }

    // ==========================================================
    // REGISTER
    // ==========================================================
    public String register(User pendingUser) {
        if (pendingUser == null || pendingUser.getUserName() == null) {
            return "Malformed registration request.";
        }
        try {
            lock.lockWrite();
            try {
                String error = validateNewAccount(pendingUser);
                if (error != null) return error;

                userCache.put(pendingUser.getUserName(), pendingUser);
                persistToDisk();
                return null;
            } finally {
                lock.unlockWrite();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Server interrupted while processing request.";
        }
    }

    /** Caller must already hold the write lock (needs the authoritative userCache for the duplicate check). */
    private String validateNewAccount(User user) {
        String username = user.getUserName();
        String password = user.getPassword();
        String nickname = user.getNickName();
        String email = user.getEmail();

        if (userCache.containsKey(username))
            return "Duplicate username: User already exists in the system.";
        if (username.length() < 3 || username.length() > 20)
            return "Invalid username length: Must be between 3 and 20 characters.";
        if (!USERNAME_CHAR_REGEX.matcher(username).matches())
            return "Invalid username characters: Only letters, numbers, and underscores are allowed.";

        if (password == null || password.contains(" "))
            return "Weak password: Spaces are not allowed as they break command parsing limits.";
        if (!PASSWORD_COMPLEXITY_REGEX.matcher(password).matches())
            return "Weak password: Must be at least 8 characters and contain lowercase, " +
                "uppercase, numeric digits, and special symbols.";

        if (nickname == null || nickname.length() < 3 || nickname.length() > 30)
            return "Invalid nickname length: Must be between 3 and 30 characters.";

        if (email == null || email.length() > 200)
            return "Invalid email: Length cannot exceed 200 characters.";
        String[] emailParts = email.split("@");
        if (emailParts.length != 2 || emailParts[0].isEmpty() || emailParts[1].isEmpty())
            return "Invalid email structure: Must contain exactly one '@' symbol.";
        if (!EMAIL_USERNAME_REGEX.matcher(emailParts[0]).matches())
            return "Invalid email username: Must start/end with alphanumeric characters and contain no consecutive dots.";
        if (!EMAIL_DOMAIN_REGEX.matcher(emailParts[1]).matches())
            return "Invalid email domain: Must include a valid extension layout (minimum 2 characters).";

        if (user.getGenderType() == null)
            return "Invalid gender: Choice must be exactly 'Male' or 'Female'.";

        if (user.getSecurityQuestion() == null)
            return "Invalid choice! Please select a valid number from the listed options.";
        if (user.getSecurityAnswer() == null || user.getSecurityAnswer().isEmpty())
            return "You must enter an answer.";

        return null;
    }

    // ==========================================================
    // LOGIN / SESSION
    // ==========================================================
    public LoginResult login(String username, String password) {
        try {
            lock.lockRead();
            try {
                User user = userCache.get(username);
                if (user == null) return LoginResult.error("Entered username does not exist in the system.");
                if (!user.getPassword().equals(password)) return LoginResult.error("Invalid password credentials.");

                String token = UUID.randomUUID().toString();
                activeSessions.put(username, token);
                return LoginResult.ok(user, token);
            } finally {
                lock.unlockRead();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return LoginResult.error("Server interrupted while processing request.");
        }
    }

    public LoginResult restoreSession(String username, String token) {
        if (username == null || token == null) return LoginResult.error("Malformed session restore request.");
        try {
            lock.lockRead();
            try {
                User user = userCache.get(username);
                if (user == null) return LoginResult.error("User no longer exists.");
                String activeToken = activeSessions.get(username);
                if (activeToken == null || !activeToken.equals(token)) {
                    return LoginResult.error("Session expired. Please log in again.");
                }
                return LoginResult.ok(user, token);
            } finally {
                lock.unlockRead();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return LoginResult.error("Server interrupted while processing request.");
        }
    }

    public void logout(String username, String token) {
        activeSessions.remove(username, token);
    }

    /** True if the given username/token pair is currently a valid, logged-in session. */
    private boolean isSessionValid(String username, String token) {
        String activeToken = activeSessions.get(username);
        return activeToken != null && activeToken.equals(token);
    }

    // ==========================================================
    // CHANGE USERNAME
    // ==========================================================
    public String changeUsername(String currentUsername, String sessionToken, String newUsername) {
        if (!isSessionValid(currentUsername, sessionToken)) {
            return "Session expired. Please log in again.";
        }
        try {
            lock.lockWrite();
            try {
                User user = userCache.get(currentUsername);
                if (user == null) return "User no longer exists.";
                if (currentUsername.equals(newUsername)) return "you are already using this username.";
                if (userCache.containsKey(newUsername)) return "Duplicate username: User already exists in the system.";
                if (newUsername.length() < 3 || newUsername.length() > 20)
                    return "Invalid username length: Must be between 3 and 20 characters.";
                if (!USERNAME_CHAR_REGEX.matcher(newUsername).matches())
                    return "Invalid username characters: Only letters, numbers, and underscores are allowed.";

                userCache.remove(currentUsername);
                user.rename(newUsername);
                userCache.put(newUsername, user);
                persistToDisk();

                String token = activeSessions.remove(currentUsername);
                if (token != null) activeSessions.put(newUsername, token);
                return null;
            } finally {
                lock.unlockWrite();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Server interrupted while processing request.";
        }
    }

    // ==========================================================
    // FORGOT / RESET PASSWORD
    // ==========================================================
    public String checkForgotPassword(String username, String email, String answer) {
        try {
            lock.lockRead();
            try {
                User user = userCache.get(username);
                if (user == null) return "Entered username does not exist in the system.";
                if (!user.getEmail().equalsIgnoreCase(email))
                    return "Provided email does not match registered user profile.";
                if (user.getSecurityAnswer() == null || !user.getSecurityAnswer().equalsIgnoreCase(answer))
                    return "Security challenge answer is incorrect.";
                return null;
            } finally {
                lock.unlockRead();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Server interrupted while processing request.";
        }
    }

    public String resetPassword(String username, String newPassword) {
        if (newPassword == null || newPassword.contains(" "))
            return "Weak password: Spaces are not allowed within password strings.";
        if (!PASSWORD_COMPLEXITY_REGEX.matcher(newPassword).matches())
            return "Weak password: Must be at least 8 characters long and include numbers, " +
                "uppercase/lowercase letters, and special characters.";
        try {
            lock.lockWrite();
            try {
                User user = userCache.get(username);
                if (user == null) return "Entered username does not exist in the system.";
                user.changePassword(newPassword);
                persistToDisk();
                return null;
            } finally {
                lock.unlockWrite();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Server interrupted while processing request.";
        }
    }

    // ==========================================================
    // SAVE PROGRESS (general sync point for all in-game mutations)
    // ==========================================================
    public String saveProgress(String username, String sessionToken, User updatedUser) {
        if (!isSessionValid(username, sessionToken)) {
            return "Session expired. Please log in again.";
        }
        try {
            lock.lockWrite();
            try {
                userCache.put(username, updatedUser);
                persistToDisk();
                return null;
            } finally {
                lock.unlockWrite();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Server interrupted while processing request.";
        }
    }

    // ==========================================================
    // LEADERBOARD
    // ==========================================================
    public List<User> getAllUsersSanitized() {
        try {
            lock.lockRead();
            try {
                List<User> result = new ArrayList<>();
                for (User u : userCache.values()) {
                    result.add(u.toPublicSummary());
                }
                return result;
            } finally {
                lock.unlockRead();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }
}
