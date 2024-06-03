package org.example.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.core.enums.UserRole;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserCreateDto {
    private String name;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("user_role")
    private UserRole userRole;

    private List<UUID> supplies;

    public UserCreateDto() {
    }

    public UserCreateDto(String name, String phoneNumber, UserRole userRole, List<UUID> supplies) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
        this.supplies = supplies;
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

    public List<UUID> getSupplies() {
        return supplies;
    }

    public void setSupplies(List<UUID> supplies) {
        this.supplies = supplies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCreateDto that = (UserCreateDto) o;
        return Objects.equals(name, that.name) && Objects.equals(phoneNumber, that.phoneNumber) && userRole == that.userRole && Objects.equals(supplies, that.supplies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber, userRole, supplies);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserCreateDto{");
        sb.append("name='").append(name).append('\'');
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append(", userRole=").append(userRole);
        sb.append(", supplies=").append(supplies);
        sb.append('}');
        return sb.toString();
    }
}
