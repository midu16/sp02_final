package com.example.android.pulseoximeter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String MEASURE = "MEASURE";
    private static final String PATIENT_LIST = "PATIENT LIST";
    private static final String NEW_USER = "NEW USER";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    public CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        /*
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
                */


        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment myFragment;
        switch(position){
            case 0:
                //if the fragment has been displayed before, don't create it, just update the view
                myFragment = fragmentManager.findFragmentByTag(DESCRIPTION);
                if(myFragment == null){
                    transaction.replace(R.id.container, DescriptionFragment.newInstance(position+1), DESCRIPTION);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.addToBackStack(DESCRIPTION);
                    transaction.commit();
                } else if(myFragment.isVisible() == false){
                    transaction.replace(R.id.container, myFragment, DESCRIPTION);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.addToBackStack(DESCRIPTION);
                    transaction.commit();
                    //update the title as well
                    mTitle = getString(R.string.title_section1_description);
                }
                break;
            case 1:
                //if the fragment has been displayed before, don't create, just update the view
                myFragment = fragmentManager.findFragmentByTag(MEASURE);
                if(myFragment == null){
                    transaction.replace(R.id.container, MeasureFragment.newInstance(position+1), MEASURE);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.addToBackStack(MEASURE);
                    transaction.commit();
                } else if(myFragment.isVisible() == false){
                    transaction.replace(R.id.container, myFragment, MEASURE);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.addToBackStack(MEASURE);
                    transaction.commit();
                    //update the title as well
                    mTitle = getString(R.string.title_section2_measure);
                }
                break;
            case 2:
                //if the fragment has been displayed before, don't create, just update the view
                myFragment = fragmentManager.findFragmentByTag(PATIENT_LIST);
                if(myFragment == null){
                    transaction.replace(R.id.container, PatientListFragment.newInstance(position+1), PATIENT_LIST);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.addToBackStack(PATIENT_LIST);
                    transaction.commit();
                } else if(myFragment.isVisible() == false){
                    transaction.replace(R.id.container, myFragment, PATIENT_LIST);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.addToBackStack(PATIENT_LIST);
                    transaction.commit();
                    mTitle = "Patient List";
                }
                break;
            case 3:
                myFragment = fragmentManager.findFragmentByTag(NEW_USER);
                if(myFragment == null){
                    transaction.replace(R.id.container, NewUserFragment.newInstance(position+1), NEW_USER);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.addToBackStack(NEW_USER);
                    transaction.commit();
                } else if(myFragment.isVisible() == false){
                    transaction.replace(R.id.container, myFragment, NEW_USER);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.addToBackStack(NEW_USER);
                    transaction.commit();
                    mTitle=" New User";
                }
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1_description);
                break;
            case 2:
                mTitle = getString(R.string.title_section2_measure);
                break;
            case 3:
                mTitle = getString(R.string.title_section3_patient_list);
                break;
            case 4:
                mTitle = getString(R.string.title_section4_new_user);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DescriptionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DescriptionFragment newInstance(int sectionNumber) {
            DescriptionFragment fragment = new DescriptionFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public DescriptionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    public void onClickSearch(View view){
        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);
    }

    public void onClickStart(View view) {

        //mRegisterDialog.show(getFragmentManager(), TAG);

    }


}
