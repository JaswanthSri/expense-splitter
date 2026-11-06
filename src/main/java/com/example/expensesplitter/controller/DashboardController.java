package com.example.expensesplitter.controller;

import com.example.expensesplitter.repository.ExpenseRepository;
import com.example.expensesplitter.repository.FriendRepository;
import com.example.expensesplitter.repository.GroupRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final FriendRepository friendRepository;
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardController(
            FriendRepository friendRepository,
            GroupRepository groupRepository,
            ExpenseRepository expenseRepository) {

        this.friendRepository = friendRepository;
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute("friendCount", friendRepository.count());
        model.addAttribute("groupCount", groupRepository.count());
        model.addAttribute("expenseCount", expenseRepository.count());

        return "index";
    }
}