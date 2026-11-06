package com.example.expensesplitter.service;

import com.example.expensesplitter.entity.Expense;
import com.example.expensesplitter.entity.ExpenseGroup;
import com.example.expensesplitter.entity.Friend;
import com.example.expensesplitter.repository.ExpenseRepository;
import com.example.expensesplitter.repository.FriendRepository;
import com.example.expensesplitter.repository.GroupRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final FriendRepository friendRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            FriendRepository friendRepository,
            GroupRepository groupRepository) {

        this.expenseRepository = expenseRepository;
        this.friendRepository = friendRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public Expense addExpense(
            String title,
            BigDecimal amount,
            Long groupId,
            Long payerId) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Expense title is required.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Group not found."));

        Friend payer = friendRepository.findById(payerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Friend not found."));

        if (group.getMembers().isEmpty()) {
            throw new IllegalArgumentException(
            "The group must have members.");
        }

        boolean isMember = group.getMembers().stream()
            .anyMatch(member -> member.getId().equals(payer.getId()));

        if (!isMember) {
            throw new IllegalArgumentException(
                "The payer must belong to the selected group.");
        }

        Expense expense = new Expense();

        expense.setTitle(title.trim());
        expense.setAmount(amount.setScale(2,
                java.math.RoundingMode.HALF_UP));
        expense.setPaidBy(payer);
        expense.setGroup(group);

        return expenseRepository.save(expense);
    }
}