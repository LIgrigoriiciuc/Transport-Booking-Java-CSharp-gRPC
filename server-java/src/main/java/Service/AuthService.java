package Service;
import Domain.User;
import Repository.Filter;
import Repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AuthService extends GenericService<Long, User> {

    private static final int BCRYPT_ROUNDS = 12;

    public AuthService(UserRepository repository) {
        super(repository);
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    public static boolean checkPassword(String plainPassword, String hashed) {
        return BCrypt.checkpw(plainPassword, hashed);
    }
    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        Filter f = new Filter();
        f.addFilter("username", username);
        List<User> matches = repository.filter(f);
        if (matches.isEmpty()) {
            throw new RuntimeException("Incorrect credentials.");
        }
        User user = matches.get(0);
        String stored = user.getPassword();
        boolean valid;
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            valid = checkPassword(password, stored);
        } else {
            valid = stored.equals(password);
            if (valid) {
                user.setPassword(hashPassword(password));
                repository.update(user);
            }
        }
        if (!valid) {
            throw new RuntimeException("Incorrect credentials.");
        }
        return user;
    }
}
