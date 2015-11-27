package com.phongbm.freephonecall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.phongbm.common.CommonValue;
import com.phongbm.countrycode.CountryCodeActivity;

public class AdditionFriend extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ADDITION_FRIEND = 0;
    private static final int MIN_LENGTH_PHONE_NUMBER = 8;
    private static final int MAX_LENGTH_PHONE_NUMBER = 15;

    private InputMethodManager inputMethodManager;
    private EditText edtCountryCode, edtPhoneNumber;
    private Button btnAddFriend;
    private String countryCode, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_addition_friend);
        this.initializeComponent();
    }

    private void initializeComponent() {
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        edtCountryCode = (EditText) findViewById(R.id.edt_country_code);
        edtCountryCode.setOnClickListener(this);
        edtCountryCode.setText("United States (+1)");
        countryCode = "(+1)";

        btnAddFriend = (Button) findViewById(R.id.btn_add_friend);
        btnAddFriend.setOnClickListener(this);

        edtPhoneNumber = (EditText) findViewById(R.id.edt_phone_number);
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= MIN_LENGTH_PHONE_NUMBER && s.length() <= MAX_LENGTH_PHONE_NUMBER) {
                    edtPhoneNumber.setError(null);
                    btnAddFriend.setEnabled(true);
                } else {
                    if (s.length() == 0) {
                        edtPhoneNumber.setError(null);
                    } else {
                        edtPhoneNumber.setError("Phone number must contain between 8 and 15 characters");
                    }
                    btnAddFriend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_country_code:
                inputMethodManager.hideSoftInputFromWindow(edtCountryCode.getWindowToken(), 0);
                Intent intent = new Intent(this, CountryCodeActivity.class);
                this.startActivityForResult(intent, REQUEST_ADDITION_FRIEND);
                break;
            case R.id.btn_add_friend:
                phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (phoneNumber.charAt(0) == '0') {
                    phoneNumber = phoneNumber.substring(1);
                }
                phoneNumber = countryCode + " " + phoneNumber;
                /*if (phoneNumber.equals(((GlobalApplication) this.getApplication())
                        .getPhoneNumber())) {
                    Toast.makeText(this, "You can not make friends with yourself", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                inputMethodManager.hideSoftInputFromWindow(edtCountryCode.getWindowToken(), 0);
                this.finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDITION_FRIEND && resultCode == Activity.RESULT_OK & data != null) {
            String content = data.getStringExtra(CommonValue.COUNTRY_CODE);
            countryCode = content.substring(content.indexOf("(+"));
            edtCountryCode.setText(content);
        }
    }

    @Override
    public void finish() {
        if (phoneNumber == null) {
            this.setResult(Activity.RESULT_OK);
        } else {
            Intent intent = new Intent();
            intent.putExtra("PHONE_NUMBER", phoneNumber);
            this.setResult(Activity.RESULT_OK, intent);
        }
        super.finish();
    }

}