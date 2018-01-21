package com.example.android.pulseoximeter;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class PatientListFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this fragment
     */

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int NEW_USER_FRAGMENT_POSITION = 4;
    private static final String NEW_USER = "NEW USER";


    public PatientListFragment(){

    }

    public static PatientListFragment newInstance(int sectionNumber){
        PatientListFragment fragment = new PatientListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_list, container, false);
        Button buttonAdd = (Button) rootView.findViewById(R.id.button_addUser);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().setTitle(R.string.title_activity_new_user);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, NewUserFragment.newInstance(NEW_USER_FRAGMENT_POSITION), NEW_USER);
                transaction.addToBackStack("MEASURE");
                transaction.commit();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Patient List");
    }
}
