package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.FriendshipWall;
import com.example.demo.repository.FriendshipWallRepository;

@Controller
public class FriendshipWallController {

    @Autowired
    private FriendshipWallRepository wallRepo;

    @GetMapping("/friendship-wall")
    public String showWall(Model model) {
        model.addAttribute("messages", wallRepo.findTop20ByOrderByCreatedAtDesc());
        model.addAttribute("newNote", new FriendshipWall());
        return "friendship-wall";
    }

    @PostMapping("/friendship-wall/post")
    public String postNote(@ModelAttribute FriendshipWall note) {
        wallRepo.save(note);
        return "redirect:/friendship-wall";
    }
    @GetMapping("/admin/friends_list")
    public String friendsList(String keyword, Model model) {
        List<FriendshipWall> listFriends;
        
        if (keyword != null && !keyword.isEmpty()) {
            // Searches the database for matching sender names
            listFriends = wallRepo.findBySenderNameContaining(keyword);
        } else {
            // If no search keyword, show everyone
            listFriends = wallRepo.findAll();
        }
        
        model.addAttribute("friendsData", listFriends);
        return "admin/friends_list";
    }
    @GetMapping("/admin/delete_friend/{id}")
    public String deleteFriend(@PathVariable("id") Long id) { // Change Long to int if your ID is an int
        wallRepo.deleteById(id);
        return "redirect:/admin/friends_list";
    }

    @GetMapping("/admin/edit_friend/{id}")
    public String editFriend(@PathVariable("id") Long id, Model model) {
        // Fetch the existing note to populate the edit form
        model.addAttribute("friendNote", wallRepo.findById(id).orElse(null));
        return "admin/edit_friends";
    }

    @PostMapping("/admin/update_friend")
    public String updateFriend(@ModelAttribute("friendNote") FriendshipWall note) {
        wallRepo.save(note);
        return "redirect:/admin/friends_list";
    }
}