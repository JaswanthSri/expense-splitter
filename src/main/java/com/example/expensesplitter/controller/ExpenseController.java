package com.example.expensesplitter.controller;

import com.example.expensesplitter.repository.ExpenseRepository;
import com.example.expensesplitter.repository.FriendRepository;
import com.example.expensesplitter.repository.GroupRepository;
import com.example.expensesplitter.service.ExpenseService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final FriendRepository friendRepository;

    public ExpenseController(
            ExpenseService expenseService,
            ExpenseRepository expenseRepository,
            GroupRepository groupRepository,
            FriendRepository friendRepository) {

        this.expenseService = expenseService;
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.friendRepository = friendRepository;
    }

    @GetMapping
    public String expenses(Model model) {

        model.addAttribute("expenses",
                expenseRepository.findAllByOrderByCreatedAtDesc());

        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("friends", friendRepository.findAll());

        return "expenses";
    }

    @PostMapping
    public String addExpense(
            @RequestParam String title,
            @RequestParam BigDecimal amount,
            @RequestParam Long groupId,
            @RequestParam Long payerId,
            RedirectAttributes redirectAttributes) {

        try {

            expenseService.addExpense(
                    title,
                    amount,
                    groupId,
                    payerId
            );

            redirectAttributes.addFlashAttribute(
                    "success", "Expense added successfully.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());
        }

        return "redirect:/expenses";
    }
}