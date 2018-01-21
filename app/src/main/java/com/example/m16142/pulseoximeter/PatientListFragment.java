package com.example.m16142.pulseoximeter;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentTransaction;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class PatientListFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this fragment
     */

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String NEW_USER = "NEW USER";
    public List<User> listUser = new ArrayList<User>();
    ArrayAdapter<String> adapter = null;

    public static User userSelected = new User();

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
        final View rootView = inflater.inflate(R.layout.fragment_patient_list, container, false);
        listUser = NewUserFragment.patientList;
        final FragmentManager fragmentManager = this.getFragmentManager();


        final ListView list =(ListView) rootView.findViewById(R.id.listView_patient);
        List<String> userIdList = new ArrayList<String>();
        for(User user:listUser){
            userIdList.add(user.getId());
        }
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userIdList );
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String idSelected = adapter.getItem(i);

                for(User user:listUser){
                    if(idSelected.equals(user.getId())){

                        userSelected.setId(idSelected);
                        userSelected.setName(user.getName());
                        userSelected.setAge(user.getAge());

                    }
                }

                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);

            }
        });

        Button buttonAdd = (Button) rootView.findViewById(R.id.button_addUser);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewUserFragment nextFrag = new NewUserFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, nextFrag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        Button buttonStart = (Button) rootView.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //No user are selected or registered
                NewUserFragment.registerNew =false;

                Intent intent = new Intent(getActivity(), DeviceScanActivity.class);
                startActivity(intent);
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

}
