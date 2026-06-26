package src.Model.User;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;

public class UsersManager {
    private static final String FILE_PATH = "users.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private HashMap<String, User> userCache = new HashMap<>();

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


    public void writeUsers() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), userCache);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write users.json", e);
        }
    }
}
