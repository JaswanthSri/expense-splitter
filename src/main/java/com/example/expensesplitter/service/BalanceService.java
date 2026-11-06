package com.example.expensesplitter.service;

import com.example.expensesplitter.entity.Expense;
import com.example.expensesplitter.entity.ExpenseGroup;
import com.example.expensesplitter.entity.Friend;
import com.example.expensesplitter.repository.ExpenseRepository;
import com.example.expensesplitter.repository.GroupRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BalanceService {

    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;

    public BalanceService(
            GroupRepository groupRepository,
            ExpenseRepository expenseRepository) {

        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberBalance> calculateBalances(Long groupId) {

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Group not found."));

        List<Friend> members = group.getMembers().stream()
                .sorted(Comparator.comparing(Friend::getId))
                .toList();

        Map<Long, Long> balancesInCents = new LinkedHashMap<>();

        for (Friend member : members) {
            balancesInCents.put(member.getId(), 0L);
        }

        if (members.isEmpty()) {
            return List.of();
        }

        List<Expense> expenses =
                expenseRepository.findByGroupIdOrderByCreatedAtDesc(groupId);

        for (Expense expense : expenses) {

            long totalCents = expense.getAmount()
                    .movePointRight(2)
                    .longValueExact();

            long baseShare = totalCents / members.size();
            long remainder = totalCents % members.size();

            Long payerId = expense.getPaidBy().getId();

            balancesInCents.merge(
                    payerId,
                    totalCents,
                    Long::sum
            );

            for (int i = 0; i < members.size(); i++) {

                Friend member = members.get(i);

                long share = baseShare + (i < remainder ? 1 : 0);

                balancesInCents.merge(
                        member.getId(),
                        -share,
                        Long::sum
                );
            }
        }

        List<MemberBalance> result = new ArrayList<>();

        for (Friend member : members) {

            BigDecimal netBalance = BigDecimal.valueOf(
                    balancesInCents.get(member.getId()), 2);

            result.add(new MemberBalance(
                    member.getId(),
                    member.getName(),
                    netBalance
            ));
        }

        return result;
    }
}