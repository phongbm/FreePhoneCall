package com.phongbm.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.phongbm.common.CommonMethod;
import com.phongbm.common.CommonValue;
import com.phongbm.freephonecall.MainActivity;
import com.phongbm.freephonecall.R;
import com.phongbm.image.ImageActivity;
import com.phongbm.image.ImageControl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_UPLOAD_PHOTO = 1;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private View view;
    private EditText edtBirthday, edtFirstName, edtLastName, edtEmail;
    private Button btnOK;
    private RadioButton radioMale, radioFemale;
    private boolean isFillFirstName, isFillLastName, isFillEmail;
    private Pattern pattern;
    private Matcher matcher;
    private boolean gender = true;
    private Bitmap bitmapAvatar;
    private CircleImageView imgAvatar;

    public ProfileFragment() {
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
            // activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeComponent() {
        view.findViewById(R.id.upload_photo).setOnClickListener(this);
        view.findViewById(R.id.take_photo).setOnClickListener(this);
        imgAvatar = (CircleImageView) view.findViewById(R.id.img_avatar);
        btnOK = (Button) view.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(this);
        edtBirthday = (EditText) view.findViewById(R.id.edt_birthday);
        edtBirthday.setOnClickListener(this);
        edtFirstName = (EditText) view.findViewById(R.id.edt_first_name);
        edtLastName = (EditText) view.findViewById(R.id.edt_last_name);
        edtEmail = (EditText) view.findViewById(R.id.edt_email);
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillFirstName = true;
                    ProfileFragment.this.enabledButtonOK();
                } else {
                    isFillFirstName = false;
                    btnOK.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillLastName = true;
                    ProfileFragment.this.enabledButtonOK();
                } else {
                    isFillLastName = false;
                    btnOK.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillEmail = true;
                    ProfileFragment.this.enabledButtonOK();
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
        radioMale = (RadioButton) view.findViewById(R.id.radioMale);
        radioFemale = (RadioButton) view.findViewById(R.id.radioFemale);
        radioMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = true;
                } else {
                    gender = false;
                }
            }
        });
        radioFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = false;
                } else {
                    gender = true;
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
                Intent intent = new Intent(this.getActivity(), MainActivity.class);
                this.getActivity().startActivity(intent);
                this.getActivity().finish();
                break;
            case R.id.upload_photo:
                Intent intentUpload = new Intent();
                intentUpload.setClass(this.getActivity(), ImageActivity.class);
                this.startActivityForResult(intentUpload, REQUEST_UPLOAD_PHOTO);
                break;
            case R.id.take_photo:
                Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intentTakePhoto.resolveActivity(getActivity().getPackageManager()) != null) {
                    /*@SuppressLint("SimpleDateFormat")
                    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String fileName = "AHIHI_" + date;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE, fileName);
                    capturedImageURI = getActivity().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);*/
                    this.startActivityForResult(intentTakePhoto, REQUEST_TAKE_PHOTO);
                } else {
                    Snackbar.make(view, "Device does not support camera", Snackbar.LENGTH_LONG)
                            .setAction("ACTION", null)
                            .show();
                }
                break;
        }
    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtBirthday.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" +
                        ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "/" +
                        year);
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

    public String getFullName() {
        return edtFirstName.getText().toString().trim() + " "
                + edtLastName.getText().toString().trim();
    }

    public String getEmail() {
        return edtEmail.getText().toString();
    }

    public String getBirthday() {
        return edtBirthday.getText().toString();
    }

    public boolean getSex() {
        return gender;
    }

    public boolean validate(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_UPLOAD_PHOTO:
                    byte[] bytes = data.getByteArrayExtra(CommonValue.BYTE_AVATAR);
                    bitmapAvatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgAvatar.setImageBitmap(bitmapAvatar);
                    break;
                case REQUEST_TAKE_PHOTO:
                    String capturedImageFilePath = CommonMethod.getInstance().getPathFromUri(getActivity()
                            .getBaseContext(), data.getData());
                    Intent intentCropImage = new Intent();
                    intentCropImage.setClass(getActivity(), ImageControl.class);
                    intentCropImage.putExtra(ImageControl.EXTRA_IMAGE, capturedImageFilePath);
                    this.startActivityForResult(intentCropImage, REQUEST_UPLOAD_PHOTO);
                    break;
            }
        }
    }

}