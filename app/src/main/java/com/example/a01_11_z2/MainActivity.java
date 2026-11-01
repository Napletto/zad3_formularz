package com.example.a01_11_z2;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etAddress;
    private Spinner spPizza;
    private RadioGroup rgSize;
    private CheckBox cbCheese, cbMushrooms, cbOlives;
    private SeekBar sbQuantity;
    private TextView tvQuantityValue, tvSummary;
    private MaterialSwitch swDelivery;
    private LinearLayout llAddress;
    private Button btnOrder, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        spPizza = findViewById(R.id.spPizza);
        rgSize = findViewById(R.id.rgSize);
        cbCheese = findViewById(R.id.cbCheese);
        cbMushrooms = findViewById(R.id.cbMushrooms);
        cbOlives = findViewById(R.id.cbOlives);
        sbQuantity = findViewById(R.id.sbQuantity);
        tvQuantityValue = findViewById(R.id.tvQuantityValue);
        tvSummary = findViewById(R.id.tvSummary);
        swDelivery = findViewById(R.id.swDelivery);
        llAddress = findViewById(R.id.llAddress);
        btnOrder = findViewById(R.id.btnOrder);
        btnClear = findViewById(R.id.btnClear);

        setUpPizzaSpinner();
        setUpListeners();
        tvQuantityValue.setText(String.valueOf(sbQuantity.getProgress()));
    }

    private void setUpPizzaSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.pizzas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPizza.setAdapter(adapter);
    }

    private void setUpListeners() {
        // zależność: przełącznik dostawy pokazuje i ukrywa pole adresu
        swDelivery.setOnCheckedChangeListener((buttonView, isChecked) ->
                llAddress.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        // suwak - trzy metody, więc klasa anonimowa zamiast lambdy
        sbQuantity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvQuantityValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        btnOrder.setOnClickListener(v -> {
            if (validateForm()) {
                askForConfirmation();
            }
        });
        btnClear.setOnClickListener(v -> askForClear());
    }
    private void askForClear() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_title)
                .setMessage(R.string.clear_message)
                .setPositiveButton(R.string.clear_yes, (dialog, which) -> clearForm())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void clearForm() {
        etName.setText("");
        etAddress.setText("");
    }
    private void askForConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> submitOrder())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private boolean validateForm() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_name));
            etName.requestFocus();
            return false;
        }

        if (spPizza.getSelectedItemPosition() == 0) {       // pozycja 0 = brak wyboru
            Toast.makeText(this, R.string.error_pizza, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rgSize.getCheckedRadioButtonId() == -1) {       // -1 = nic nie zaznaczono
            Toast.makeText(this, R.string.error_size, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sbQuantity.getProgress() == 0) {
            Toast.makeText(this, R.string.error_quantity, Toast.LENGTH_SHORT).show();
            return false;
        }

        // adres sprawdzamy tylko, gdy włączona jest dostawa
        if (swDelivery.isChecked()
                && etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError(getString(R.string.error_address));
            etAddress.requestFocus();
            return false;
        }

        return true;
    }

    private void submitOrder() {
        tvSummary.setText(buildSummary());

        Snackbar.make(findViewById(R.id.main), R.string.ordered, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> tvSummary.setText(""))
                .show();
    }

    private String buildSummary() {
        RadioButton checkedSize = findViewById(rgSize.getCheckedRadioButtonId());

        String delivery = swDelivery.isChecked()
                ? getString(R.string.delivery_to, etAddress.getText().toString().trim())
                : getString(R.string.pickup);

        return getString(R.string.summary_format,
                etName.getText().toString().trim(),
                spPizza.getSelectedItem().toString(),
                checkedSize.getText().toString(),
                getToppings(),
                sbQuantity.getProgress(),
                delivery);
    }

    private String getToppings() {
        StringBuilder sb = new StringBuilder();
        if (cbCheese.isChecked()) {
            sb.append(cbCheese.getText()).append(", ");
        }
        if (cbMushrooms.isChecked()) {
            sb.append(cbMushrooms.getText()).append(", ");
        }
        if (cbOlives.isChecked()) {
            sb.append(cbOlives.getText()).append(", ");
        }

        if (sb.length() == 0) {
            return getString(R.string.none);
        }
        return sb.substring(0, sb.length() - 2);    // obcinamy ostatni ", "
    }

}
