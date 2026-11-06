package com.example.expensesplitter.controller;

import com.example.expensesplitter.entity.ExpenseGroup;
import com.example.expensesplitter.repository.GroupRepository;
import com.example.expensesplitter.service.BalanceService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/balances")
public class BalanceController {

    private final BalanceService balanceService;
    private final GroupRepository groupRepository;

    public BalanceController(
            BalanceService balanceService,
            GroupRepository groupRepository) {

        this.balanceService = balanceService;
        this.groupRepository = groupRepository;
    }

    @GetMapping
    public String balances(
            @RequestParam(required = false) Long groupId,
            Model model) {

        List<ExpenseGroup> groups = groupRepository.findAll();

        model.addAttribute("groups", groups);
        model.addAttribute("selectedGroupId", groupId);

        if (groupId != null) {

            ExpenseGroup selectedGroup = groupRepository.findById(groupId)
                    .orElse(null);

            model.addAttribute("selectedGroup", selectedGroup);

            if (selectedGroup != null) {
                model.addAttribute(
                        "balances",
                        balanceService.calculateBalances(groupId)
                );
            }
        }

        return "balances";
    }
}