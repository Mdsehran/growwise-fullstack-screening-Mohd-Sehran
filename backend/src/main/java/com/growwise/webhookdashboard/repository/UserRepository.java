package com.growwise.webhookdashboard.repository;

import com.growwise.webhookdashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
