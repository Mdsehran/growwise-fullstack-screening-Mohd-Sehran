package com.growwise.webhookdashboard.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

/**
 * Represents a simulated platform user. This entity is NOT used to perform
 * authentication or authorization — those decisions are made purely from
 * the incoming X-User-Id / X-Role request headers (see
 * security.AdminAuthInterceptor). It exists so the seeded admin/instructor/
 * student accounts have a real, queryable, documented identity, which is
 * useful for the README's seed matrix and for any future feature that
 * needs to look up "who is this user" beyond their role.
 */
@Entity
@Table(name = "app_user")
public class User {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User() {
    }

    public User(String id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
