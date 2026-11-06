
package com.example.expensesplitter.service;

import com.example.expensesplitter.entity.Expense;
import com.example.expensesplitter.entity.ExpenseGroup;
import com.example.expensesplitter.entity.Friend;

import com.example.expensesplitter.repository.ExpenseRepository;
import com.example.expensesplitter.repository.GroupRepository;

import org.springframework.test.util.ReflectionTestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private BalanceService balanceService;

    private Friend jaswanth;
    private Friend rahul;
    private Friend shreya;
    private ExpenseGroup group;

    @BeforeEach
    void setUp() {

        jaswanth = new Friend();
        ReflectionTestUtils.setField(jaswanth, "id", 1L);
        jaswanth.setName("Jaswanth");

        rahul = new Friend();
        ReflectionTestUtils.setField(rahul, "id", 2L);
        rahul.setName("Rahul");

        shreya = new Friend();
        ReflectionTestUtils.setField(shreya, "id", 3L);
        shreya.setName("Shreya");

        group = new ExpenseGroup();
        ReflectionTestUtils.setField(group, "id", 1L);
        group.setName("Goa Trip");

        group.getMembers().add(jaswanth);
        group.getMembers().add(rahul);
        group.getMembers().add(shreya);
    }

    @Test
    void testEqualExpenseSplitting() {

        when(groupRepository.findById(1L))
                .thenReturn(Optional.of(group));

        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("3000.00"));
        expense.setPaidBy(rahul);

        when(expenseRepository
                .findByGroupIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(expense));

        List<MemberBalance> balances =
                balanceService.calculateBalances(1L);

        assertEquals(3, balances.size());

        assertEquals("Jaswanth", balances.get(0).friendName());
        assertEquals(
                new BigDecimal("-1000.00"),
                balances.get(0).netBalance()
        );

        assertEquals("Rahul", balances.get(1).friendName());
        assertEquals(
                new BigDecimal("2000.00"),
                balances.get(1).netBalance()
        );

        assertEquals("Shreya", balances.get(2).friendName());
        assertEquals(
                new BigDecimal("-1000.00"),
                balances.get(2).netBalance()
        );
    }

    @Test
    void testMultipleExpenses() {

        when(groupRepository.findById(1L))
                .thenReturn(Optional.of(group));

        Expense expense1 = new Expense();
        expense1.setAmount(new BigDecimal("3000.00"));
        expense1.setPaidBy(rahul);

        Expense expense2 = new Expense();
        expense2.setAmount(new BigDecimal("1500.00"));
        expense2.setPaidBy(jaswanth);

        when(expenseRepository
                .findByGroupIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(expense1, expense2));

        List<MemberBalance> balances =
                balanceService.calculateBalances(1L);

        assertEquals(
                new BigDecimal("0.00"),
                balances.get(0).netBalance()
        );

        assertEquals(
                new BigDecimal("1500.00"),
                balances.get(1).netBalance()
        );

        assertEquals(
                new BigDecimal("-1500.00"),
                balances.get(2).netBalance()
        );
    }

    @Test
    void testRemainderWhenAmountCannotBeSplitEqually() {

        when(groupRepository.findById(1L))
                .thenReturn(Optional.of(group));

        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("10.00"));
        expense.setPaidBy(jaswanth);

        when(expenseRepository
                .findByGroupIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(expense));

        List<MemberBalance> balances =
                balanceService.calculateBalances(1L);

        assertEquals(
                new BigDecimal("6.66"),
                balances.get(0).netBalance()
        );

        assertEquals(
                new BigDecimal("-3.33"),
                balances.get(1).netBalance()
        );

        assertEquals(
                new BigDecimal("-3.33"),
                balances.get(2).netBalance()
        );
    }

    @Test
    void testGroupWithNoExpenses() {

        when(groupRepository.findById(1L))
                .thenReturn(Optional.of(group));

        when(expenseRepository
                .findByGroupIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());

        List<MemberBalance> balances =
                balanceService.calculateBalances(1L);

        assertEquals(3, balances.size());

        for (MemberBalance balance : balances) {
            assertEquals(
                    new BigDecimal("0.00"),
                    balance.netBalance()
            );
        }
    }

    @Test
    void testGroupNotFound() {

        when(groupRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> balanceService.calculateBalances(99L)
        );

        verify(expenseRepository, never())
                .findByGroupIdOrderByCreatedAtDesc(anyLong());
    }
}
