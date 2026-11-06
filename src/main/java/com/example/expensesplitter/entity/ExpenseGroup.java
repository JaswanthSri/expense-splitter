package com.example.expensesplitter.entity;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "expense_groups")
public class ExpenseGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Friend> members = new LinkedHashSet<>();

    public ExpenseGroup() {
    }

    public ExpenseGroup(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Friend> getMembers() {
        return members;
    }

    public void setMembers(Set<Friend> members) {
        this.members = members;
    }
}