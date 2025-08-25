package service;

import java.util.Objects;

import model.User;
import repo.InMemoryUserRepository;

public class AuthService {
    private final InMemoryUserRepository users = InMemoryUserRepository.getInstance();

    public User login(String username, String password) {
        var u = users.findByUsername(username);
        if (u != null && Objects.equals(u.getPassword(), password)) return u;
        return null;
    }
}
