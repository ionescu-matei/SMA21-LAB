package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upt.cti.smartwallet.model.MonthlyExpenses;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView tStatus;
    private EditText eSearch, eIncome, eExpenses;
    // firebase
    private DatabaseReference databaseReference;
    private String currentMonth;
    private ValueEventListener databaseListener;

    final List<String> monthNames = new ArrayList<>();
    final List<MonthlyExpenses> monthlyExpenses = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Spinner sSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tStatus = (TextView) findViewById(R.id.tStatus);
//        eSearch = (EditText) findViewById(R.id.eSearch);
        eIncome = (EditText) findViewById(R.id.eIncome);
        eExpenses = (EditText) findViewById(R.id.eExpenses);


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-e2c24-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

//        createNewDBListener();

        sSearch = (Spinner) findViewById(R.id.monthsSpinner);
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, monthNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSearch.setAdapter(adapter);
        sSearch.setOnItemSelectedListener(this);

        databaseReference.child("calendar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MonthlyExpenses monthExpenses = snapshot.getValue(MonthlyExpenses.class);
                monthExpenses.month = snapshot.getKey();
                monthlyExpenses.add(monthExpenses);
                monthNames.add(monthExpenses.getMonth());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MonthlyExpenses newMonthExpenses = snapshot.getValue(MonthlyExpenses.class);
                newMonthExpenses.month = snapshot.getKey();
                for (int i = 0; i < monthlyExpenses.size(); i++) {
                    if (monthlyExpenses.get(i).getMonth().equals(newMonthExpenses.month)) {
                        monthlyExpenses.set(i, newMonthExpenses);
                        if (sSearch.getSelectedItem().toString().equals(newMonthExpenses.month)) {
                            refresh_expenses(newMonthExpenses);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                MonthlyExpenses monthExpenses = snapshot.getValue(MonthlyExpenses.class);
                monthExpenses.month = snapshot.getKey();
                monthNames.remove(monthExpenses.getMonth());
                monthlyExpenses.remove(monthExpenses);
                adapter.notifyDataSetChanged();
                for (MonthlyExpenses monthExpensesIt : monthlyExpenses) {
                    if (monthExpensesIt.getMonth().equals(sSearch.getSelectedItem().toString())) {
                        refresh_expenses(monthExpensesIt);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clicked(View view) {
        switch (view.getId()) {
//            case R.id.bSearch:
//                if (!eSearch.getText().toString().isEmpty()) {
//                    // save text to lower case (all our months are stored online in lower case)
//                    currentMonth = eSearch.getText().toString().toLowerCase();
//                    tStatus.setText("Searching ...");
//                    createNewDBListener();
//                } else {
//                    Toast.makeText(this, "Search field may not be empty", Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.bUpdate:
                if (databaseReference != null) {
                    float income;
                    float expenses;
                    currentMonth = sSearch.getSelectedItem().toString();
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

    private void refresh_expenses(MonthlyExpenses monthExpenses) {
        eIncome.setText(String.valueOf(monthExpenses.getIncome()));
        eExpenses.setText(String.valueOf(monthExpenses.getExpenses()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String monthName = parent.getItemAtPosition(position).toString();
        for (MonthlyExpenses monthExpenses : monthlyExpenses) {
            if (monthExpenses.getMonth().equals(monthName)) {
                refresh_expenses(monthExpenses);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}