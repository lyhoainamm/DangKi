package repo;
import model.User;

public interface UserRepository {
    User findByUsername(String username);
    void save(User u);
}
