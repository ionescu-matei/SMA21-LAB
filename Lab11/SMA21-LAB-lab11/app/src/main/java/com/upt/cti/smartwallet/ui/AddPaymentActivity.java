package com.upt.cti.smartwallet.ui;

import static com.upt.cti.smartwallet.ui.AppState.getCurrentTimeDate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.cti.smartwallet.R;
import com.upt.cti.smartwallet.model.Payment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AddPaymentActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private EditText eCost;
    private EditText ePaymentName;
    private Spinner sPaymentType;
    private Button bSubmit;
    private ArrayAdapter adapter;
    private String uid;

    private enum Action{
        EDIT,
        ADD
    }
    private Action action;
    private List<String> paymentTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        Intent intent = getIntent();
        String action = intent.getStringExtra("ACTION");
        uid = AppState.get().getUserID();
        databaseReference = AppState.get().getDatabaseReference();
        eCost = findViewById(R.id.eCost);
        ePaymentName = findViewById(R.id.ePaymentName);
        sPaymentType = findViewById(R.id.sPaymentType);
        bSubmit = findViewById(R.id.bSubmit);
        paymentTypes = Arrays.asList("entertainment", "food", "travel", "taxes");
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, paymentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPaymentType.setAdapter(adapter);
        if (action.equals("EDIT")) {
            this.action = Action.EDIT;
            bSubmit.setText("Update payment");
            Payment payment = AppState.get().getCurrentPayment();
            ePaymentName.setText(payment.getName());
            eCost.setText(String.format("%.2f", payment.getCost()));
            for (int i = 0; i < paymentTypes.size(); i++) {
                if (paymentTypes.get(i).equals(payment.getType())) {
                    sPaymentType.setSelection(i);
                }
            }
        } else if (action.equals("ADD")) {
            this.action = Action.ADD;
            bSubmit.setText("Add payment");
        }
//        sPaymentType.setOnItemSelectedListener(this);
    }

    private Payment getNewPayment() {
        String paymentName;
        float cost;
        String paymentType;
        if (!ePaymentName.getText().toString().isEmpty()) {
            paymentName = ePaymentName.getText().toString();
        } else {
            Toast.makeText(this, "Payment name field may not be empty", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!eCost.getText().toString().isEmpty()) {
            String eCostText = eCost.getText().toString();
            try {
                cost = Float.parseFloat(eCostText);
            } catch (NumberFormatException exception) {
                Toast.makeText(this, "Cost field must contain a number", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            Toast.makeText(this, "Cost field may not be empty", Toast.LENGTH_SHORT).show();
            return null;
        }
        paymentType = sPaymentType.getSelectedItem().toString();
        Payment payment = new Payment(null, cost, paymentName, paymentType);
        return payment;
    }

    private Payment getUpdatedPayment(Payment payment){
        String paymentName;
        float cost;
        String paymentType;
        if (!ePaymentName.getText().toString().isEmpty()) {
            paymentName = ePaymentName.getText().toString();
        } else {
            Toast.makeText(this, "Payment name field may not be empty", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!eCost.getText().toString().isEmpty()) {
            String eCostText = eCost.getText().toString();
            try {
                cost = Float.parseFloat(eCostText);
            } catch (NumberFormatException exception) {
                Toast.makeText(this, "Cost field must contain a number", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            Toast.makeText(this, "Cost field may not be empty", Toast.LENGTH_SHORT).show();
            return null;
        }
        paymentType = sPaymentType.getSelectedItem().toString();
        payment.setName(paymentName);
        payment.setCost(cost);
        payment.setType(paymentType);
        return payment;
    }

    private void addPayment() {
        if (databaseReference != null) {
            Payment payment = getNewPayment();
            String dateTime = getCurrentTimeDate();
            if(payment != null){
                databaseReference.child("wallet").child(uid).child(dateTime).setValue(payment);
                finish();
                Toast.makeText(this, "Payment added", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No database connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void editPayment(){
        if (databaseReference != null) {
            Payment payment = getUpdatedPayment(AppState.get().getCurrentPayment());
            if(payment != null){
                databaseReference.child("wallet").child(uid).child(payment.timestamp).setValue(payment);
                finish();
                Toast.makeText(this, "Payment updated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No database connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bSubmit:
                if(action == Action.ADD) {
                    addPayment();
                }
                else if(action == Action.EDIT){
                    editPayment();
                }
                break;
        }
    }
}