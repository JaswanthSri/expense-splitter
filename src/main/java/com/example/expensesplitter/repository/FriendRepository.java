package com.example.expensesplitter.repository;

import com.example.expensesplitter.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}