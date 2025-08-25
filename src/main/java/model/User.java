package model;

import model.Role;

public class User {
    private final String id;
    private final String username;
    private String password;
    private String name;
    private final Role role;

    public User(String id, String username, String password, String name, Role role) {
        this.id = id; this.username = username; this.password = password; this.name = name; this.role = role;
    }
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Role getRole() { return role; }
}
