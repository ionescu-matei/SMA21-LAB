package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    // Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int REQ_SIGNIN = 3;

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

        // setup authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    TextView tLoginDetail = (TextView) findViewById(R.id.tLoginDetail);
                    TextView tUser = (TextView) findViewById(R.id.tUser);
                    tLoginDetail.setText("Firebase ID: " + user.getUid());
                    tUser.setText("Email: " + user.getEmail());

                    AppState.get().setUserId(user.getUid());
                    attachDBListener(user.getUid());
                } else {
                    startActivityForResult(new Intent(getApplicationContext(),

                            SignupActivity.class), REQ_SIGNIN);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void attachDBListener(String uid) {
        // setup firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-e2c24-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);

        databaseReference.child("wallet").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (currentMonth == Month.monthFromTimestamp(snapshot.getKey())) {
                    Payment payment = snapshot.getValue(Payment.class);
                    payment.timestamp = snapshot.getKey();
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

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                Intent myIntent = new Intent(MainActivity2.this, AddPaymentActivity.class);
                myIntent.putExtra("ACTION", "ADD");
                MainActivity2.this.startActivity(myIntent);
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
            case R.id.bSignOut:
                mAuth.signOut();
        }
    }
}