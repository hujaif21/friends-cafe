package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.FriendshipWall;
import java.util.List;

public interface FriendshipWallRepository extends JpaRepository<FriendshipWall, Long> {
    // Fetch the 20 most recent messages for the wall
    List<FriendshipWall> findTop20ByOrderByCreatedAtDesc();
    List<FriendshipWall> findBySenderNameContaining(String keyword);
}