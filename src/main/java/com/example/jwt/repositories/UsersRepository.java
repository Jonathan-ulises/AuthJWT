package com.example.jwt.repositories;

import com.example.jwt.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<User, Long> {

//    @Query("SELECT * FROM User WHERE username = :name")
    Optional<User> findByUsername(String name);
}
