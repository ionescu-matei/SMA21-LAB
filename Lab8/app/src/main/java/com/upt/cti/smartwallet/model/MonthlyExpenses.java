package com.upt.cti.smartwallet.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MonthlyExpenses {
    public String month;
    private float income, expenses;

    public MonthlyExpenses() {
    }

    public MonthlyExpenses(String month, float income, float expenses) {
        this.month = month;
        this.income = income;
        this.expenses = expenses;
    }

    public String getMonth() {
        return month;
    }

    public float getIncome() {
        return income;
    }

    public float getExpenses() {
        return expenses;
    }
}
