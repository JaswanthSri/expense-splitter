package com.example.expensesplitter.service;

import java.math.BigDecimal;

public record MemberBalance(
        Long friendId,
        String friendName,
        BigDecimal netBalance
) {
}