package org.security.sample.to_do_list_app.repository;

import org.security.sample.to_do_list_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Final Project - Step #5 - created user repo to enable CRUD & custom operations on db

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
