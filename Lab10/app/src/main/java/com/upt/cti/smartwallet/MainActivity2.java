package com.upt.cti.smartwallet;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.cti.smartwallet.enums.Month;
import com.upt.cti.smartwallet.model.Payment;
import com.upt.cti.smartwallet.ui.AddPaymentActivity;
import com.upt.cti.smartwallet.ui.AppState;
import com.upt.cti.smartwallet.ui.PaymentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    private final static String PREFS_FILE = "prefs";
    private final static String TAG_MONTH = "MONTH";

    // firebase
    private DatabaseReference databaseReference;
    private List<Payment> payments = new ArrayList<>();
    private TextView tStatus;
    private Button bPrevious;
    private Button bNext;
    private FloatingActionButton fabAdd;
    private ListView listPayments;
    private int currentMonth;
    private SharedPreferences prefs;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        prefs = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        tStatus = (TextView) findViewById(R.id.tStatus2);
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        adapter = new PaymentAdapter(this, R.layout.item_payment, payments);
        listPayments = (ListView) findViewById(R.id.listPayments);
        listPayments.setAdapter(adapter);

        currentMonth = prefs.getInt(TAG_MONTH, -1);
        if (currentMonth == -1)
            currentMonth = Month.monthFromTimestamp(AppState.getCurrentTimeDate());

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            loadLocalStorage();
                        }
                    }
                });

        fabAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_MOVE:
                                view.setX(event.getRawX() - 120);
                                view.setY(event.getRawY() - 425);
                                break;
                            case MotionEvent.ACTION_UP:
                                view.setOnTouchListener(null);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                return true;
            }
        });

        if (!AppState.isNetworkAvailable(this)) {
            loadLocalStorage();
        } else {
            // setup firebase
            final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-e2c24-default-rtdb.europe-west1.firebasedatabase.app/");
            databaseReference = database.getReference();
            AppState.get().setDatabaseReference(databaseReference);
            for(Month month : Month.values()){
                List<Payment> localPayments = AppState.get().loadFromLocalBackup(MainActivity2.this, Month.monthNameToInt(month));
                for(Payment paymentIt : localPayments){
                    databaseReference.child("wallet").child(paymentIt.timestamp).setValue(paymentIt);
                }
            }
            databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Payment payment = snapshot.getValue(Payment.class);
                    payment.timestamp = snapshot.getKey();
                    AppState.get().updateLocalBackup(MainActivity2.this, payment, true);
                    if (currentMonth == Month.monthFromTimestamp(snapshot.getKey())) {
                        payments.add(payment);
                        adapter.notifyDataSetChanged();
                    }
                    if (payments.isEmpty()) {
                        tStatus.setText(String.format("No payment for %s", Month.intToMonthName(currentMonth)));
                    } else {
                        tStatus.setText(String.format("Payments for month %s", Month.intToMonthName(currentMonth)));
                    }
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
                    AppState.get().updateLocalBackup(MainActivity2.this, payment, false);
                    AppState.get().updateLocalBackup(MainActivity2.this, payment, true);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Payment payment = snapshot.getValue(Payment.class);
                    payment.timestamp = snapshot.getKey();
                    for (int i = 0; i < payments.size(); i++) {
                        if (payments.get(i).timestamp.equals(payment.timestamp)) {
                            payments.remove(i);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    AppState.get().updateLocalBackup(MainActivity2.this, payment, false);
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
    }

    private void loadLocalStorage() {
        // has local storage already
        if (AppState.get().hasLocalStorage(this)) {
            payments = AppState.get().loadFromLocalBackup(MainActivity2.this, currentMonth);
            tStatus.setText("Found " + payments.size() + " payments for " +
                    Month.intToMonthName(currentMonth) + ".");
            adapter.clear();
            adapter.addAll(payments);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "This app needs an internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                Intent myIntent = new Intent(MainActivity2.this, AddPaymentActivity.class);
                myIntent.putExtra("ACTION", "ADD");
                someActivityResultLauncher.launch(myIntent);
                break;
            case R.id.bPrevious:
                if (currentMonth == 0) {
                    currentMonth = 11;
                } else {
                    currentMonth--;
                }
                prefs.edit().putInt(TAG_MONTH, currentMonth).apply();
                recreate();
                break;
            case R.id.bNext:
                if (currentMonth == 11) {
                    currentMonth = 0;
                } else {
                    currentMonth++;
                }
                prefs.edit().putInt(TAG_MONTH, currentMonth).apply();
                recreate();
                break;
        }
    }
}