package com.phongbm.home;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.phongbm.common.CommonValue;
import com.phongbm.freephonecall.R;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SignUpFragment.class.getSimpleName();
    private static final int REQUEST_COUNTRY_CODE = 0;
    private static final int MIN_LENGTH_PHONE_NUMBER = 8;
    private static final int MAX_LENGTH_PHONE_NUMBER = 15;
    private static final int MIN_LENGTH_PASSWORD = 8;

    private View view;
    private EditText edtPhoneNumber;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private EditText edtCountryCode;
    private Button btnSignUp;
    private CheckBox checkBoxAgree;
    private boolean isFillPhoneNumber;
    private boolean isFillPassword;
    private boolean isFillConfirmPassword;
    private boolean isCheckBoxChecked;
    private String countryCode;
    private String phoneNumber;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate...");
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        this.initializeToolbar();
        this.initializeComponent();
        return view;
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (MainFragment) this.getActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.sign_up);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeComponent() {
        btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(this);

        checkBoxAgree = (CheckBox) view.findViewById(R.id.checkbox_agree);
        checkBoxAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxAgree.isChecked()) {
                    isCheckBoxChecked = true;
                    SignUpFragment.this.enabledButtonSignUp();
                } else {
                    isCheckBoxChecked = false;
                    btnSignUp.setEnabled(false);
                }
            }
        });

        edtCountryCode = (EditText) view.findViewById(R.id.edt_country_code);
        edtCountryCode.setOnClickListener(this);
        edtCountryCode.setText("United States (+1)");
        countryCode = "(+1)";

        edtPhoneNumber = (EditText) view.findViewById(R.id.edt_phone_number);
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= MIN_LENGTH_PHONE_NUMBER && s.length() <= MAX_LENGTH_PHONE_NUMBER) {
                    edtPhoneNumber.setError(null);
                    isFillPhoneNumber = true;
                    SignUpFragment.this.enabledButtonSignUp();
                } else {
                    if (s.length() == 0) {
                        edtPhoneNumber.setError(null);
                    } else {
                        edtPhoneNumber.setError("Phone number must contain between 8 and 15 characters");
                    }
                    isFillPhoneNumber = false;
                    btnSignUp.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtPassword = (EditText) view.findViewById(R.id.edt_password);
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 || s.length() >= MIN_LENGTH_PASSWORD) {
                    edtPassword.setError(null);
                } else {
                    edtPassword.setError("Password must be least 8 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPassword.getText().toString().length() >= MIN_LENGTH_PASSWORD
                        && edtConfirmPassword.getText().toString().length() >= MIN_LENGTH_PASSWORD) {
                    if (!edtPassword.getText().toString()
                            .equals(edtConfirmPassword.getText().toString())) {
                        edtPassword.setError("'Password' and 'Confirm Password' do not match");
                        isFillPassword = false;
                        btnSignUp.setEnabled(false);
                    } else {
                        isFillPassword = true;
                        isFillConfirmPassword = true;
                        SignUpFragment.this.enabledButtonSignUp();
                        edtPassword.setError(null);
                        edtConfirmPassword.setError(null);
                    }
                }
            }
        });

        edtConfirmPassword = (EditText) view.findViewById(R.id.edt_confirm_password);
        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtConfirmPassword.getText().toString().length() > 0
                        && edtPassword.getText().toString().length() >= MIN_LENGTH_PASSWORD) {
                    if (!edtConfirmPassword.getText().toString()
                            .equals(edtPassword.getText().toString())) {
                        edtConfirmPassword.setError("'Confirm Password' and 'Password' do not match");
                        isFillConfirmPassword = false;
                        btnSignUp.setEnabled(false);
                    } else {
                        isFillConfirmPassword = true;
                        isFillPassword = true;
                        SignUpFragment.this.enabledButtonSignUp();
                        edtConfirmPassword.setError(null);
                        edtPassword.setError(null);
                    }
                }
            }
        });
    }

    private void enabledButtonSignUp() {
        if (isFillPhoneNumber && isFillPassword && isFillConfirmPassword && isCheckBoxChecked
                && edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            btnSignUp.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainFragment) this.getActivity()).showHomeFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.edt_country_code:
                Intent intent = new Intent(this.getActivity(), CountryCodeActivity.class);
                this.startActivityForResult(intent, REQUEST_COUNTRY_CODE);
                break;
            case R.id.btn_sign_up:
                final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
                progressDialog.setTitle("Signing up");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                final ParseUser newUser = new ParseUser();
                phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (phoneNumber.charAt(0) == '0') {
                    phoneNumber = phoneNumber.substring(1);
                }
                phoneNumber = countryCode + " " + phoneNumber;
                password = edtPassword.getText().toString().trim();

                newUser.setUsername(phoneNumber);
                newUser.setPassword(password);
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            newUser.put("online", true);
                            newUser.saveInBackground();
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(view, "Registered successfully",
                                    Snackbar.LENGTH_LONG)
                                    .setAction("ACTION", null)
                                    .setCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            super.onDismissed(snackbar, event);
                                            ((MainFragment) SignUpFragment.this.getActivity())
                                                    .showProfileFragment();
                                        }
                                    });
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.parseColor("#4caf50"));
                            snackbar.show();
                        } else {
                            progressDialog.dismiss();
                            Snackbar.make(view, "There was an error signing up", Snackbar.LENGTH_LONG)
                                    .setAction("ACTION", null)
                                    .show();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COUNTRY_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String content = data.getStringExtra(CommonValue.COUNTRY_CODE);
            countryCode = content.substring(content.indexOf("(+"));
            edtCountryCode.setText(content);
        }
    }

}