package org.example.core.enums;

public enum UserRole {

    ADMIN("ADMIN"), USER("USER"), MASTER("MASTER");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static UserRole fromString(String name) {
        for (UserRole role : UserRole.values()) {
            if (role.getName().equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Не существует Enum с именем " + name);
    }
}

