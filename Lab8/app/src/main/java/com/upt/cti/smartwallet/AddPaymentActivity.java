package com.upt.cti.smartwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private ArrayAdapter adapter;
    private List<String> paymentTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-e2c24-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();
        eCost = findViewById(R.id.eCost);
        ePaymentName = findViewById(R.id.ePaymentName);
        sPaymentType = findViewById(R.id.sPaymentType);
        paymentTypes = Arrays.asList("entertainment", "food", "travel", "taxes");
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, paymentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPaymentType.setAdapter(adapter);
//        sPaymentType.setOnItemSelectedListener(this);
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bSubmit:
                if (databaseReference != null) {
                    String paymentName;
                    float cost;
                    String paymentType;
                    if (!ePaymentName.getText().toString().isEmpty()) {
                        paymentName = ePaymentName.getText().toString();
                    } else {
                        Toast.makeText(this, "Payment name field may not be empty", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (!eCost.getText().toString().isEmpty()) {
                        String eCostText = eCost.getText().toString();
                        try {
                            cost = Float.parseFloat(eCostText);
                        } catch (NumberFormatException exception) {
                            Toast.makeText(this, "Cost field must contain a number", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } else {
                        Toast.makeText(this, "Cost field may not be empty", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    paymentType = sPaymentType.getSelectedItem().toString();
                    String dateTime = getCurrentTimeDate();
                    Payment payment = new Payment(null, cost, paymentName, paymentType);
                    databaseReference.child("wallet").child(dateTime).setValue(payment);
                } else {
                    Toast.makeText(this, "No database connected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static String getCurrentTimeDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }
}