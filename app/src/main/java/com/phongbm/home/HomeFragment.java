package com.phongbm.home;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phongbm.freephonecall.R;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView...");
        view = inflater.inflate(R.layout.fragment_home, null);
        this.initializeComponent();
        return view;
    }

    private void initializeComponent() {
        view.findViewById(R.id.btn_sign_in).setOnClickListener(this);
        view.findViewById(R.id.btn_sign_up).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                ((MainFragment) this.getActivity()).showSignInFragment();
                break;
            case R.id.btn_sign_up:
                ((MainFragment) this.getActivity()).showSigUpFragment();
                break;
        }
    }

}