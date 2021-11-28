package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.cti.smartwallet.model.MonthlyExpenses;
import com.upt.cti.smartwallet.model.Payment;
import com.upt.cti.smartwallet.ui.PaymentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    // firebase
    private DatabaseReference databaseReference;
    private int currentMonth;
    private List<Payment> payments = new ArrayList<>();
    private TextView tStatus;
    private Button bPrevious;
    private Button bNext;
    private FloatingActionButton fabAdd;
    private ListView listPayments;
    PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tStatus = (TextView) findViewById(R.id.tStatus2);
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        adapter =  new PaymentAdapter(this, R.layout.item_payment, payments);
        listPayments = (ListView) findViewById(R.id.listPayments);
        listPayments.setAdapter(adapter);

        // setup firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-e2c24-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Payment payment = snapshot.getValue(Payment.class);
                payment.timestamp = snapshot.getKey();
                payments.add(payment);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {
                Payment payment = snapshot.getValue(Payment.class);
                payment.timestamp = snapshot.getKey();
                for (int i = 0; i < payments.size(); i++) {
                    if (payments.get(i).timestamp.equals(payment.timestamp)) {
                        payments.set(i, payment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Payment payment = snapshot.getValue(Payment.class);
                payment.timestamp = snapshot.getKey();
                payments.remove(payment);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void clicked(View view){
        switch (view.getId()) {
            case R.id.fabAdd:
                Intent myIntent = new Intent(MainActivity2.this, AddPaymentActivity.class);
                MainActivity2.this.startActivity(myIntent);
                break;
        }
    }
}