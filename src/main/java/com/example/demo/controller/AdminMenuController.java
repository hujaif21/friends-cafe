package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Menu;
import com.example.demo.repository.MenuRepository;

@Controller
@RequestMapping("/admin/menu")
public class AdminMenuController {

    // 🔹 Upload directory
    private static String uploadDir =
            System.getProperty("user.dir") + "/src/main/resources/static/images";

    @Autowired
    private MenuRepository menuRepository;

    // ===============================
    // 1️⃣ View All Menu Items
    // ===============================
    @GetMapping
    public String viewMenu(String keyword, Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            // Searches for menu items by name matching the keyword
            model.addAttribute("menuList", menuRepository.findByNameContaining(keyword));
        } else {
            // Default behavior: load all menu items
            model.addAttribute("menuList", menuRepository.findAll());
        }
        return "admin/menu_list";
    }

    // ===============================
    // 2️⃣ Show Add Form
    // ===============================
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("menu", new Menu());
        return "admin/menu_form";
    }
    @GetMapping("/category/{category}")
    public String viewMenuByCategory(@PathVariable String category, Model model) {

        model.addAttribute("menuList", menuRepository.findByCategory(category));
        model.addAttribute("selectedCategory", category);

        return "admin/menu_list";
    }

   
    @PostMapping("/save")
    public String saveMenu(@ModelAttribute Menu menu) {

        try {
            // Create folder if not exists
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Did the user upload a NEW image?
            if (menu.getImageFile() != null && !menu.getImageFile().isEmpty()) {
                String fileName = menu.getImageFile().getOriginalFilename();
                Path path = Paths.get(uploadDir, fileName);
                Files.write(path, menu.getImageFile().getBytes());

                // Save NEW filename in DB
                menu.setImage_url(fileName);
            } else {
                // If they are EDITING and left the image blank, KEEP the old image URL!
                if (menu.getMenu_id() != null) {
                    Menu existingMenu = menuRepository.findById(menu.getMenu_id()).orElse(null);
                    if (existingMenu != null) {
                        menu.setImage_url(existingMenu.getImage_url());
                    }
                }
            }

            menuRepository.save(menu);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/menu";
    }

 @GetMapping("/edit/{id}")
 public String showEditForm(@PathVariable Integer id, Model model) {

     Menu menu = menuRepository.findById(id).orElse(null);

     model.addAttribute("menu", menu);

     return "admin/menu_form";
 }
    
  
    @GetMapping("/delete/{id}")
    public String deleteMenu(@PathVariable Integer id) {
        menuRepository.deleteById(id);
        return "redirect:/admin/menu";
    }

}