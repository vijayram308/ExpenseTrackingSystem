package com.example.srivi.expensetrackingsystem;

public class Debt {
    protected String name;
    protected String ph_no;
    protected String amount;

    public Debt(String name, String amount, String ph_no) {
        this.name = name;
        this.amount = amount;
        this.ph_no = ph_no;
    }
    public String toString() {
        return "Name : " + name + "\nAmount : " + amount + "\nPhone Number : " + ph_no;
    }
}
