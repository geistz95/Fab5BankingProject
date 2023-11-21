package com.fab5.bankingapp.model;

import com.fab5.bankingapp.enums.AccountType;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.List;

@Entity
public class Account {
    /**
     * Variables include
     *
     * Long accountID
     * Enum type
     * String nickname
     * Integer rewards
     * Double balance
     * Customer customer
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column
    private String nickname;

    @Column
    private String name;
    @Column
    @Value("0")
    private Integer rewards;
    @Column
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "account")
    private List<Bill> bills;

    public Account(){

    }

//    public Account(Long id, AccountType type, String nickname, Integer rewards, Double balance, Customer customer, String name) {
//        this.id = id;
//        this.type = type;
//        this.nickname = nickname;
//        this.rewards = rewards;
//        this.balance = balance;
//        this.customer = customer;
//        this.name = name;
//    }


    public Account(Long id, AccountType type, String nickname, String name, Integer rewards, Double balance, Customer customer, List<Bill> bills) {
        this.id = id;
        this.type = type;
        this.nickname = nickname;
        this.name = name;
        this.rewards = rewards;
        this.balance = balance;
        this.customer = customer;
        this.bills = bills;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getRewards() {
        return rewards;
    }

    public void setRewards(Integer rewards) {
        this.rewards = rewards;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}
