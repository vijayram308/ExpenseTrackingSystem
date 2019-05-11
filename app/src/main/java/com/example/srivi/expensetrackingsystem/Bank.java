package com.example.srivi.expensetrackingsystem;

/**
 * Created by srivi on 23-04-2019.
 */

public class Bank {
    protected String bank_name;
    protected String balance;

    public Bank(String bank_name, String balance) {
        this.bank_name = bank_name;
        this.balance = balance;
    }
    public String toString() {
        return "Bank Name : " + bank_name + "\nBalance : " + balance;
    }
}
