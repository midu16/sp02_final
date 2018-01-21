package com.example.m16142.pulseoximeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewUserFragment extends Fragment implements TextWatcher {

    private EditText mTxtId;
    private EditText mTxtName;
    private EditText mTxtAge;


    public static User patient;
    public static boolean registerNew = false;

    public static List<User> patientList = new ArrayList<User>();

    private static final String ARG_SECTION_NUMBER = "section_number";

    private boolean no_reg = false;


    public NewUserFragment(){
    }

    public static NewUserFragment newInstance(int sectionNumber){
        NewUserFragment fragment = new NewUserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_user, container, false);
        mTxtId = (EditText) rootView.findViewById(R.id.editText_id);
        mTxtName = (EditText) rootView.findViewById(R.id.editText_name);
        mTxtAge = (EditText) rootView.findViewById(R.id.editText_age);


        Button buttonReg = (Button) rootView.findViewById(R.id.button_register);


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTxtId.getText().toString().isEmpty() ||
                        mTxtName.getText().toString().isEmpty() ||
                        mTxtAge.getText().toString().isEmpty()){

                    if(mTxtId.getText().toString().isEmpty()){
                        mTxtId.setError("ID for Registration field can't be empty!");
                    }

                    if(mTxtName.getText().toString().isEmpty()){
                        mTxtName.setError("Name field can't be empty!");
                    }

                    if(mTxtAge.getText().toString().isEmpty()){
                        mTxtAge.setError("Age field can't be empty!");
                    }

                    Toast.makeText(getActivity(), "Empty fields!", Toast.LENGTH_SHORT).show();
                    hideKeyboard();
                    return;
                }

                    //Create the user and add it to the list
                    String idUser = mTxtId.getText().toString();
                    String nameUser = mTxtName.getText().toString();
                    int ageUser = Integer.parseInt(mTxtAge.getText().toString());
                    patient = new User(idUser, nameUser, ageUser);

                    if (patientList.size() >= 1) {
                        for (User u : patientList) {
                            if (u.getId().equals(patient.getId())) {
                                no_reg = true;
                                Toast.makeText(getActivity(), "This id is already used.", Toast.LENGTH_SHORT).show();
                                hideKeyboard();
                                return;
                            }
                        }

                        if(no_reg == true){
                            PatientListFragment nextFrag = new PatientListFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, nextFrag);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else{
                            patientList.add(patient);
                            Toast.makeText(getActivity(), "Successfully registration", Toast.LENGTH_SHORT).show();

                            registerNew = true;
                            HistoryActivity.userSelected = false;

                            //Clear text fields
                            mTxtId.setText("");
                            mTxtName.setText("");
                            mTxtAge.setText("");

                            Intent intent = new Intent(getActivity(), DeviceScanActivity.class);
                            startActivity(intent);
                        }

                    } else {
                        patientList.add(patient);
                        Toast.makeText(getActivity(), "Successfully registration", Toast.LENGTH_SHORT).show();

                        registerNew = true;
                        HistoryActivity.userSelected = false;

                        //Clear text fields
                        mTxtId.setText("");
                        mTxtName.setText("");
                        mTxtAge.setText("");

                        Intent intent = new Intent(getActivity(), DeviceScanActivity.class);
                        startActivity(intent);
                    }
                }

        });
        return rootView;

    }

/*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
   }
*/
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    public void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mTxtAge.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().getActionBar().setTitle(R.string.title_section4_new_user);
    }
}
