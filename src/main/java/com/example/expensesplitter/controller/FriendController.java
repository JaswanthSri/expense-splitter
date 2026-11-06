package com.example.expensesplitter.controller;

import com.example.expensesplitter.entity.Friend;
import com.example.expensesplitter.repository.FriendRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/friends")
public class FriendController {

    private final FriendRepository friendRepository;

    public FriendController(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @GetMapping
    public String friends(Model model) {

        model.addAttribute("friends", friendRepository.findAll());

        return "friends";
    }

    @PostMapping
    public String addFriend(
            @RequestParam String name,
            RedirectAttributes redirectAttributes) {

        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Friend name cannot be empty.");

            return "redirect:/friends";
        }

        Friend friend = new Friend(name.trim());

        friendRepository.save(friend);

        redirectAttributes.addFlashAttribute(
                "success", "Friend added successfully.");

        return "redirect:/friends";
    }
}