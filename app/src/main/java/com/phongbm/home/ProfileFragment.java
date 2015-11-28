package com.phongbm.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.parse.ParseUser;
import com.phongbm.common.CommonMethod;
import com.phongbm.common.CommonValue;
import com.phongbm.common.Profile;
import com.phongbm.freephonecall.MainActivity;
import com.phongbm.freephonecall.R;
import com.phongbm.image.ImageActivity;
import com.phongbm.image.ImageControl;
import com.phongbm.libraries.CircleImageView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_UPLOAD_PHOTO = 1;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private View view;
    private EditText edtBirthday;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtEmail;
    private Button btnOK;
    private boolean isFillFirstName;
    private boolean isFillLastName;
    private boolean isFillEmail;
    private boolean gender = true;
    private Pattern pattern;
    private Bitmap avatarBitmap;
    private CircleImageView imgAvatar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.initializeToolbar();
        this.initializeComponent();
        return view;
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (MainFragment) this.getActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("PROFILE INFORMATION");
        }
    }

    private void initializeComponent() {
        view.findViewById(R.id.upload_photo).setOnClickListener(this);
        view.findViewById(R.id.take_photo).setOnClickListener(this);
        view.findViewById(R.id.default_avatar).setOnClickListener(this);

        imgAvatar = (CircleImageView) view.findViewById(R.id.img_avatar);
        avatarBitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();

        btnOK = (Button) view.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(this);

        edtBirthday = (EditText) view.findViewById(R.id.edt_birthday);
        edtBirthday.setOnClickListener(this);
        edtFirstName = (EditText) view.findViewById(R.id.edt_first_name);
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    isFillFirstName = true;
                    enabledButtonOK();
                } else {
                    isFillFirstName = false;
                    btnOK.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtLastName = (EditText) view.findViewById(R.id.edt_last_name);
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    isFillLastName = true;
                    enabledButtonOK();
                } else {
                    isFillLastName = false;
                    btnOK.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtEmail = (EditText) view.findViewById(R.id.edt_email);
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    isFillEmail = true;
                    enabledButtonOK();
                } else {
                    isFillEmail = false;
                    btnOK.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validate(s.toString())) {
                    edtEmail.setError("Email is not valid");
                } else {
                    edtEmail.setError(null);
                }
            }
        });

        RadioGroup radioGroupGender = (RadioGroup) view.findViewById(R.id.radio_group_gender);
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_male:
                        gender = true;
                        break;
                    case R.id.radio_female:
                        gender = false;
                        break;
                }
            }
        });
    }

    private void enabledButtonOK() {
        if (isFillFirstName && isFillLastName && isFillEmail) {
            btnOK.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_birthday:
                this.showDatePickerDialog();
                break;

            case R.id.btn_ok:
                final String fullName = edtFirstName.getText().toString().trim() + " "
                        + edtLastName.getText().toString().trim();
                final String email = edtEmail.getText().toString().trim();
                String birthday = edtBirthday.getText().toString().trim();
                boolean gender = this.gender;

                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.put("fullName", fullName);
                currentUser.setEmail(email);
                currentUser.put("birthday", birthday);
                currentUser.put("gender", gender);
                currentUser.saveInBackground();
                CommonMethod.uploadAvatar(currentUser, avatarBitmap);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Profile.getInstance().setFullName(fullName);
                        Profile.getInstance().setEmail(email);
                        Profile.getInstance().setAvatar(avatarBitmap);
                    }
                });

                final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ProfileFragment.this.getActivity().startActivity(
                                new Intent(ProfileFragment.this.getActivity(), MainActivity.class));
                        ProfileFragment.this.getActivity().finish();

                        /*AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFragment.this.getActivity());
                        builder.setCancelable(false);
                        builder.setTitle("Sign up successfully");
                        builder.setMessage("Sign up successfully. Please confirm your email to continue");
                        builder.setPositiveButton("Continue", null);
                        final AlertDialog confirmDialog = builder.create();
                        confirmDialog.getWindow().setWindowAnimations(R.style.AppTheme_Dialog_Animate);
                        confirmDialog.show();
                        Button positiveButton = confirmDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(
                                ProfileFragment.this.getActivity().getBaseContext(), R.color.blue_500));
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i(TAG, "" + ParseUser.getCurrentUser().getEmail());
                                Log.i(TAG, "" + ParseUser.getCurrentUser().getBoolean("emailVerified"));
                                Log.i(TAG, "" + ParseUser.getCurrentUser().getString("emailVerified"));

                                boolean isVerified = ParseUser.getCurrentUser().getBoolean("emailVerified");
                                Log.i(TAG, "isVerified: " + isVerified);
                                if (isVerified) {
                                    ProfileFragment.this.getActivity().startActivity(
                                            new Intent(ProfileFragment.this.getActivity(), MainActivity.class));
                                    confirmDialog.cancel();
                                    ProfileFragment.this.getActivity().finish();
                                }
                            }
                        });*/
                    }
                }, 3000);
                break;

            case R.id.upload_photo:
                Intent intentUpload = new Intent();
                intentUpload.setClass(this.getActivity(), ImageActivity.class);
                this.startActivityForResult(intentUpload, REQUEST_UPLOAD_PHOTO);
                break;

            case R.id.take_photo:
                Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intentTakePhoto.resolveActivity(getActivity().getPackageManager()) != null) {
                    this.startActivityForResult(intentTakePhoto, REQUEST_TAKE_PHOTO);
                } else {
                    Snackbar.make(view, "Device does not support camera", Snackbar.LENGTH_LONG)
                            .setAction("ACTION", null)
                            .show();
                }
                break;

            case R.id.default_avatar:
                imgAvatar.setImageResource(R.drawable.ic_avatar_default);
                avatarBitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
                break;
        }
    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String day = String.valueOf(dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                String month = String.valueOf((monthOfYear + 1) < 10
                        ? "0" + (monthOfYear + 1) : (monthOfYear + 1));
                String date = day + "/" + month + "/" + year;
                edtBirthday.setText(date);
            }
        };
        String date = edtBirthday.getText().toString();
        String dates[] = date.split("/");
        int day = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]) - 1;
        int year = Integer.parseInt(dates[2]);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getActivity(),
                onDateSetListener, year, month, day);
        datePickerDialog.show();
    }

    public boolean validate(final String hex) {
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_UPLOAD_PHOTO:
                    byte[] bytes = data.getByteArrayExtra(CommonValue.BYTE_AVATAR);
                    avatarBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgAvatar.setImageBitmap(avatarBitmap);
                    break;

                case REQUEST_TAKE_PHOTO:
                    String capturedImageFilePath = CommonMethod.getInstance().getPathFromUri(
                            this.getActivity().getBaseContext(), data.getData());
                    Intent intentCropImage = new Intent();
                    intentCropImage.setClass(this.getActivity(), ImageControl.class);
                    intentCropImage.putExtra(ImageControl.EXTRA_IMAGE, capturedImageFilePath);
                    this.startActivityForResult(intentCropImage, REQUEST_UPLOAD_PHOTO);
                    break;
            }
        }
    }

}