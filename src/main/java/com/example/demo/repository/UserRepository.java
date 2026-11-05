package com.example.demo.repository; // Change this if your folder is named differently

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.User; 

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // This custom method will let us check if a user typed the right name when logging in later!
    User findByUsername(String username); 
    List<User> findByUsernameContaining(String keyword);
}