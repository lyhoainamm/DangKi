package repo;
import model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private static final InMemoryUserRepository INSTANCE = new InMemoryUserRepository();
    public static InMemoryUserRepository getInstance(){ return INSTANCE; }
    private final Map<String, User> byUsername = new ConcurrentHashMap<>();
    @Override public User findByUsername(String username){ return byUsername.get(username); }
    @Override public void save(User u){ byUsername.put(u.getUsername(), u); }
}
