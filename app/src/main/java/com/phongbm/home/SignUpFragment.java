package com.phongbm.home;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.phongbm.common.CommonValue;
import com.phongbm.freephonecall.R;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignUpFragment";
    private static final int REQUEST_COUNTRY_CODE = 0;

    private View view;
    private EditText edtPhoneNumber, edtPassword, edtConfirmPassword, edtCode;
    private Button btnSignUp;
    private CheckBox checkBoxAgree;
    private boolean isFillPhoneNumber, isFillPassword, isFillConfirmPassword, isCheckBoxChecked;
    private String countryCode, phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, null);
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
        edtPhoneNumber = (EditText) view.findViewById(R.id.edt_phone_number);
        edtPassword = (EditText) view.findViewById(R.id.edt_password);
        edtConfirmPassword = (EditText) view.findViewById(R.id.edt_confirm_password);
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
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillPhoneNumber = true;
                    SignUpFragment.this.enabledButtonSignUp();
                } else {
                    isFillPhoneNumber = false;
                    btnSignUp.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillPassword = true;
                    SignUpFragment.this.enabledButtonSignUp();
                } else {
                    isFillPassword = false;
                    SignUpFragment.this.enabledButtonSignUp();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPassword.getText().toString().length() > 0
                        && edtConfirmPassword.getText().toString().length() > 0) {
                    if (!edtPassword.getText().toString()
                            .equals(edtConfirmPassword.getText().toString())) {
                        edtPassword.setError("'Password' and 'Confirm Password' do not match");
                    } else {
                        edtPassword.setError(null);
                        edtConfirmPassword.setError(null);
                    }
                } else {
                    edtPassword.setError(null);
                    edtConfirmPassword.setError(null);
                }
            }
        });
        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillConfirmPassword = true;
                    SignUpFragment.this.enabledButtonSignUp();
                } else {
                    isFillConfirmPassword = false;
                    SignUpFragment.this.enabledButtonSignUp();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPassword.getText().toString().length() > 0
                        && edtConfirmPassword.getText().toString().length() > 0) {
                    if (!edtConfirmPassword.getText().toString()
                            .equals(edtPassword.getText().toString())) {
                        edtConfirmPassword.setError("'Confirm Password' and 'Password' do not match");
                    } else {
                        edtConfirmPassword.setError(null);
                        edtPassword.setError(null);
                    }
                } else {
                    edtConfirmPassword.setError(null);
                    edtPassword.setError(null);
                }
            }
        });
        edtCode = (EditText) view.findViewById(R.id.edt_country_code);
        edtCode.setOnClickListener(this);
        edtCode.setText("United States (+1)");
        countryCode = "(+1)";
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
            case R.id.btn_sign_up:
                ((MainFragment) SignUpFragment.this.getActivity())
                        .showProfileInformationFragment();
                /*
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
                newUser.setUsername(countryCode + " " + phoneNumber);
                newUser.setPassword(edtPassword.getText().toString().trim());
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
                                                    .showProfileInformationFragment();
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
                */
                break;
            case R.id.edt_country_code:
                Intent intent = new Intent(this.getActivity(), CountryCodeActivity.class);
                this.startActivityForResult(intent, REQUEST_COUNTRY_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COUNTRY_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(CommonValue.COUNTRY_CODE);
                countryCode = content.substring(content.indexOf("(+"));
                edtCode.setText(content);
            }
        }
    }

}