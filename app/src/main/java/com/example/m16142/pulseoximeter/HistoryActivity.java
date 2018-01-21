package com.example.m16142.pulseoximeter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class HistoryActivity extends ActionBarActivity {

    TextView spO2Txt;
    TextView heartRateTxt;
    TextView idTxt, nameTxt, ageTxt;
    public static User userS = PatientListFragment.userSelected;
    public static boolean userSelected = false;
    public static final String MyPREFERENCES = "MyPrefs" ;

    /** Customized dialog fragment to clear the SpO2 field. */
    private ClearDialogFragment mClearOutputFieldSpO2;
    /** Customized dialog fragment to clear the Pulse field. */
    private ClearDialogFragment mClearOutputFieldPulse;
    /** TAG to be used for logcat. */
    protected static final String TAG = " History Activity ";

    private User user;

    //colors
    private static int RED = Color.rgb(242, 86, 86);
    private static int GREEN = Color.rgb(102, 169, 24);
    private static int BLUE = Color.rgb(148, 148, 247);
    private static int ORANGE = Color.rgb(239, 168, 61);
    private static int PURPLE = Color.rgb(107, 48, 158);


    public static int WHICH_FRAGMENT;


    public static final String PREFS_NAME = "AOP_PREFS";
    public static final String PREFS_KEY = "AOP_PREFS_String";

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        idTxt = (TextView) findViewById(R.id.textViewId);
        nameTxt = (TextView) findViewById(R.id.textViewName);
        ageTxt = (TextView) findViewById(R.id.textViewAge);

        idTxt.setText(userS.getId());
        nameTxt.setText(userS.getName());
        ageTxt.setText(Integer.toString(userS.getAge()));


        spO2Txt = (TextView) findViewById(R.id.textViewSpO2);
        spO2Txt.setMovementMethod(new ScrollingMovementMethod());
        heartRateTxt = (TextView) findViewById(R.id.textViewPulse);
        heartRateTxt.setMovementMethod(new ScrollingMovementMethod());

        mClearOutputFieldSpO2 = new ClearDialogFragment(spO2Txt ,
                getString(R.string.dialog_clear_spO2));

        mClearOutputFieldPulse = new ClearDialogFragment(heartRateTxt ,
                getString(R.string.dialog_clear_pulse));



        spO2Txt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClearOutputFieldSpO2.show(getFragmentManager(), TAG);
                return false;
            }
        });


        heartRateTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClearOutputFieldPulse.show(getFragmentManager(), TAG);
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_new_user:
                Intent intent1 = new Intent(this, MainActivity.class);
                WHICH_FRAGMENT = 1;
                startActivity(intent1);
                break;
            case R.id.new_measure:
                userSelected = true;
                NewUserFragment.registerNew = false;
                Intent intent2 = new Intent(this, DeviceScanActivity.class);
                startActivity(intent2);
                break;
            case R.id.delete_user:
                List<User> userList = NewUserFragment.patientList;
                for(User user:userList){
                    if(user.getId().equals((userS.getId()))){
                        userList.remove(user);
                    }
                }
                NewUserFragment.patientList = userList;
                Intent intent3 = new Intent(this, MainActivity.class);
                WHICH_FRAGMENT = 2;
                startActivity(intent3);
                break;
        }

       return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(MeasureActivity.userM != null && userS.getId().equals(MeasureActivity.userM.getId())){
            if(MeasureActivity.lastSaturationVal != null && MeasureActivity.lastPulseVal != null){

                StringBuilder spO2Builder = new StringBuilder();
                StringBuilder pulseBuilder = new StringBuilder();


                //split every last 2 characters of string in one character
                char c1 = MeasureActivity.lastSaturationVal.charAt(3);
                char c2 = MeasureActivity.lastSaturationVal.charAt(4);

                //append these characters to a StringBuilder
                spO2Builder = spO2Builder.append(c1).append(c2);
                //put StringBuilder into a String
                String spO2String = new String(spO2Builder);
                //convert string into integer
                int spO2Val = Integer.parseInt(spO2String);
                String spO2Edit = spO2String + "%" + "\n";
                if(spO2Val >= 95){
                    appendColorText(spO2Txt, spO2Edit, RED);
                } else if(spO2Val <95 && spO2Val >= 90){
                    appendColorText(spO2Txt, spO2Edit, GREEN);
                } else if(spO2Val < 90 && spO2Val >= 80){
                    appendColorText(spO2Txt, spO2Edit, ORANGE);
                } else if(spO2Val < 80){
                    appendColorText(spO2Txt, spO2Edit, PURPLE);
                }



                //check if lastPulseVal has 3 digits
                if(MeasureActivity.lastPulseVal.charAt(2) != '0'){
                    //split every last 3 characters of string in one character
                    char c1P = MeasureActivity.lastPulseVal.charAt(2);
                    char c2P = MeasureActivity.lastPulseVal.charAt(3);
                    char c3P = MeasureActivity.lastPulseVal.charAt(4);

                    //append these characters to a StringBuilder
                    pulseBuilder = pulseBuilder.append(c1P).append(c2P).append(c3P);
                    //put StringBuilder into a String
                    String pulseString = new String(pulseBuilder);
                    //convert String into Integer
                    int pulseVal = Integer.parseInt(pulseString);
                    String pulseEdit = pulseString + "bpm" + "\n";
                    if(pulseVal < 60){
                        appendColorText(heartRateTxt, pulseEdit, ORANGE);
                    } else if(pulseVal > 90){
                        appendColorText(heartRateTxt, pulseEdit, GREEN);
                    } else if(pulseVal <= 90 && pulseVal >= 60){
                        appendColorText(heartRateTxt, pulseEdit, BLUE);
                    }


                } else {
                    //split every last 3 characters of string in one character
                    char c1P = MeasureActivity.lastPulseVal.charAt(3);
                    char c2P = MeasureActivity.lastPulseVal.charAt(4);

                    //append these characters to a StringBuilder
                    pulseBuilder = pulseBuilder.append(c1P).append(c2P);
                    //put StringBuilder into a String
                    String pulseString = new String(pulseBuilder);
                    //convert String into Integer
                    int pulseVal = Integer.parseInt(pulseString);
                    String pulseEdit = pulseString + "bpm" + "\n";
                    if(pulseVal < 60){
                        appendColorText(heartRateTxt, pulseEdit, ORANGE);
                    } else if(pulseVal > 90){
                        appendColorText(heartRateTxt, pulseEdit, GREEN);
                    } else if(pulseVal <= 90 && pulseVal >= 60){
                        appendColorText(heartRateTxt, pulseEdit, BLUE);
                    }
                }
                }


            }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    public static void appendColorText(TextView txtView, String valString, int color){
        int start = txtView.getText().length();
        txtView.append(valString);
        int end = txtView.getText().length();

        Spannable spannableText = (Spannable) txtView.getText();
        spannableText.setSpan(new ForegroundColorSpan(color), start, end, 0);
    }

    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        WHICH_FRAGMENT = 2;
        startActivity(intent);
    }
    */



}
