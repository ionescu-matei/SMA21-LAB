package com.upt.cti.smartwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upt.cti.smartwallet.model.MonthlyExpenses;

public class MainActivity extends AppCompatActivity {
    private TextView tStatus;
    private EditText eSearch, eIncome, eExpenses;
    // firebase
    private DatabaseReference databaseReference;
    private String currentMonth;
    private ValueEventListener databaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tStatus = (TextView) findViewById(R.id.tStatus);
        eSearch = (EditText) findViewById(R.id.eSearch);
        eIncome = (EditText) findViewById(R.id.eIncome);
        eExpenses = (EditText) findViewById(R.id.eExpenses);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-e2c24-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bSearch:
                if (!eSearch.getText().toString().isEmpty()) {
                    // save text to lower case (all our months are stored online in lower case)
                    currentMonth = eSearch.getText().toString().toLowerCase();
                    tStatus.setText("Searching ...");
                    createNewDBListener();
                } else {
                    Toast.makeText(this, "Search field may not be empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bUpdate:
                if (databaseReference != null) {
                    float income;
                    float expenses;
                    if (!eSearch.getText().toString().isEmpty()) {
                        // save text to lower case (all our months are stored online in lower case)
                        currentMonth = eSearch.getText().toString().toLowerCase();
                        if (!eIncome.getText().toString().isEmpty()) {
                            String eIncomeText = eIncome.getText().toString();
                            try {
                                income = Float.parseFloat(eIncomeText);
                            } catch (NumberFormatException exception) {
                                Toast.makeText(this, "Income field must contain a number", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } else {
                            Toast.makeText(this, "Income field may not be empty", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (!eExpenses.getText().toString().isEmpty()) {
                            String eExpensesText = eExpenses.getText().toString();
                            try {
                                expenses = Float.parseFloat(eExpensesText);
                            } catch (NumberFormatException exception) {
                                Toast.makeText(this, "Income field must contain a number", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } else {
                            Toast.makeText(this, "Income field may not be empty", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        databaseReference.child("calendar").child(currentMonth).child("income").setValue(income);
                        databaseReference.child("calendar").child(currentMonth).child("expenses").setValue(expenses);
                        tStatus.setText("Database updated");

                    } else {
                        Toast.makeText(this, "Month field may not be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No database connected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void createNewDBListener() {
        // remove previous databaseListener

        if (databaseReference != null && currentMonth != null && databaseListener != null)
            databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MonthlyExpenses monthlyExpense = dataSnapshot.getValue(MonthlyExpenses.class);
                // explicit mapping of month name from entry key
                if (dataSnapshot.getValue() != null) {
                    monthlyExpense.month = dataSnapshot.getKey();
                    eIncome.setText(String.valueOf(monthlyExpense.getIncome()));
                    eExpenses.setText(String.valueOf(monthlyExpense.getExpenses()));
                    tStatus.setText("Found entry for " + currentMonth);
                } else {
                    tStatus.setText("Month not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        // set new databaseListener
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }
}