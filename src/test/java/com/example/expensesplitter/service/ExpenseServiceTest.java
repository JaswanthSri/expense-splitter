
package com.example.expensesplitter.service;

import com.example.expensesplitter.entity.Expense;
import com.example.expensesplitter.entity.ExpenseGroup;
import com.example.expensesplitter.entity.Friend;

import com.example.expensesplitter.repository.ExpenseRepository;
import com.example.expensesplitter.repository.FriendRepository;
import com.example.expensesplitter.repository.GroupRepository;

import org.springframework.test.util.ReflectionTestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Friend rahul;
    private Friend jaswanth;
    private ExpenseGroup group;

    @BeforeEach
    void setUp() {

        rahul = new Friend();
        ReflectionTestUtils.setField(rahul, "id", 1L);
        rahul.setName("Rahul");

        jaswanth = new Friend();
        ReflectionTestUtils.setField(jaswanth, "id", 2L);
        jaswanth.setName("Jaswanth");

        group = new ExpenseGroup();
        ReflectionTestUtils.setField(group, "id", 1L);
        group.setName("Goa Trip");

        group.getMembers().add(rahul);
        group.getMembers().add(jaswanth);
    }

    @Test
    void testAddExpenseSuccessfully() {

        when(groupRepository.findById(1L))
                .thenReturn(Optional.of(group));

        when(friendRepository.findById(1L))
                .thenReturn(Optional.of(rahul));

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Expense result = expenseService.addExpense(
                " Hotel Booking ",
                new BigDecimal("1500"),
                1L,
                1L
        );

        assertNotNull(result);
        assertEquals("Hotel Booking", result.getTitle());
        assertEquals(
                new BigDecimal("1500.00"),
                result.getAmount()
        );
        assertEquals(rahul, result.getPaidBy());
        assertEquals(group, result.getGroup());

        verify(expenseRepository, times(1))
                .save(any(Expense.class));
    }

    @Test
    void testRejectBlankExpenseTitle() {

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        "   ",
                        new BigDecimal("100"),
                        1L,
                        1L
                )
        );

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void testRejectNullExpenseTitle() {

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        null,
                        new BigDecimal("100"),
                        1L,
                        1L
                )
        );
    }

    @Test
    void testRejectZeroAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        "Food",
                        BigDecimal.ZERO,
                        1L,
                        1L
                )
        );
    }

    @Test
    void testRejectNegativeAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        "Food",
                        new BigDecimal("-100"),
                        1L,
                        1L
                )
        );
    }

    @Test
    void testRejectNullAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        "Food",
                        null,
                        1L,
                        1L
                )
        );
    }

    @Test
    void testRejectNonExistingGroup() {

        when(groupRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        "Food",
                        new BigDecimal("100"),
                        99L,
                        1L
                )
        );
    }

    @Test
    void testRejectNonExistingFriend() {

        when(groupRepository.findById(1L))
                .thenReturn(Optional.of(group));

        when(friendRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        "Food",
                        new BigDecimal("100"),
                        1L,
                        99L
                )
        );
    }

    @Test
    void testRejectPayerNotInGroup() {

        when(groupRepository.findById(1L))
                .thenReturn(Optional.of(group));

        when(friendRepository.findById(1L))
                .thenReturn(Optional.of(rahul));

        group.getMembers().remove(rahul);

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        "Food",
                        new BigDecimal("100"),
                        1L,
                        1L
                )
        );
    }

    @Test
    void testRejectGroupWithoutMembers() {

        when(groupRepository.findById(1L))
                .thenReturn(Optional.of(group));

        when(friendRepository.findById(1L))
                .thenReturn(Optional.of(rahul));

        group.getMembers().clear();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.addExpense(
                        "Food",
                        new BigDecimal("100"),
                        1L,
                        1L
                )
        );

        assertEquals("The group must have members.",
                exception.getMessage());
    }
}
