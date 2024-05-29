package org.example.core.entity;

import org.example.core.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {

    private UUID uuid;

    private String name;

    private String phoneNumber;

    private UserRole userRole;

    private LocalDateTime dtCreate;

    private List<Supply> supplies;

    private LocalDateTime dtUpdate;

    public User() {
    }

    public User(UUID uuid, String name, String phoneNumber, UserRole userRole, LocalDateTime dtCreate, List<Supply> supplies, LocalDateTime dtUpdate) {
        this.uuid = uuid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
        this.dtCreate = dtCreate;
        this.supplies = supplies;
        this.dtUpdate = dtUpdate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public LocalDateTime getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(LocalDateTime dtCreate) {
        this.dtCreate = dtCreate;
    }

    public List<Supply> getSupplies() {
        return supplies;
    }

    public void setSupplies(List<Supply> supplies) {
        this.supplies = supplies;
    }

    public LocalDateTime getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(LocalDateTime dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uuid, user.uuid) && Objects.equals(name, user.name) && Objects.equals(phoneNumber, user.phoneNumber) && userRole == user.userRole && Objects.equals(dtCreate, user.dtCreate) && Objects.equals(supplies, user.supplies) && Objects.equals(dtUpdate, user.dtUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, phoneNumber, userRole, dtCreate, supplies, dtUpdate);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("uuid=").append(uuid);
        sb.append(", name='").append(name).append('\'');
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append(", userRole=").append(userRole);
        sb.append(", dtCreate=").append(dtCreate);
        sb.append(", supplies=").append(supplies);
        sb.append(", dtUpdate=").append(dtUpdate);
        sb.append('}');
        return sb.toString();
    }
}
