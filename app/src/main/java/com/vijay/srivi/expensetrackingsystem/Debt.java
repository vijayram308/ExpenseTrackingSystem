package com.vijay.srivi.expensetrackingsystem;

public class Debt {
    public String name;
    public String ph_no;
    public String amount;

    public Debt(String name, String amount, String ph_no) {
        this.name = name;
        this.amount = amount;
        this.ph_no = ph_no;
    }

    public String toString() {
        return "Name : " + name + "\nAmount : " + amount + "\nPhone Number : " + ph_no;
    }
}
