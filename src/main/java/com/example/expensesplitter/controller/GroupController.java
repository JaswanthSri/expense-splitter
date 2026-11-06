package com.example.expensesplitter.controller;

import com.example.expensesplitter.entity.ExpenseGroup;
import com.example.expensesplitter.entity.Friend;
import com.example.expensesplitter.repository.FriendRepository;
import com.example.expensesplitter.repository.GroupRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashSet;
import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupController {

    private final GroupRepository groupRepository;
    private final FriendRepository friendRepository;

    public GroupController(
            GroupRepository groupRepository,
            FriendRepository friendRepository) {

        this.groupRepository = groupRepository;
        this.friendRepository = friendRepository;
    }

    @GetMapping
    public String groups(Model model) {

        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("friends", friendRepository.findAll());

        return "groups";
    }

    @PostMapping
    public String createGroup(
            @RequestParam String name,
            @RequestParam(required = false) List<Long> memberIds,
            RedirectAttributes redirectAttributes) {

        if (name == null || name.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error", "Group name cannot be empty.");

            return "redirect:/groups";
        }

        if (memberIds == null || memberIds.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error", "Select at least one friend.");

            return "redirect:/groups";
        }

        List<Friend> selectedFriends =
                friendRepository.findAllById(memberIds);

        if (selectedFriends.size() != memberIds.stream().distinct().count()) {

            redirectAttributes.addFlashAttribute(
                    "error", "One or more selected friends are invalid.");

            return "redirect:/groups";
        }

        ExpenseGroup group = new ExpenseGroup(name.trim());

        group.setMembers(new LinkedHashSet<>(selectedFriends));

        groupRepository.save(group);

        redirectAttributes.addFlashAttribute(
                "success", "Group created successfully.");

        return "redirect:/groups";
    }
}