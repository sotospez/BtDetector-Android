package com.sotosgr.btdetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Settings extends AppCompatActivity {

    private static final String TAG = "Settings";
    public static final int REQUEST_SETTINGS = 405;  //for the Request from main maybe set the settings values on exit
    //zone settings to save
    public static final String ZONE_1 = "ZONE_1";
    public static final String ZONE_2 = "ZONE_2";
    public static final String ZONE_3 = "ZONE_3";
    public static final String ZONE_4 = "ZONE_4";
    public static final String ZONE_5 = "ZONE_5";

    Integer Zone01 =20;
    Integer Zone02 =40;
    Integer Zone03 =60;
    Integer Zone04 =80;
    Integer Zone05 =100;

    Spinner spinnerZone01;
    Spinner spinnerZone02;
    Spinner spinnerZone03;
    Spinner spinnerZone04;
    Spinner spinnerZone05;

    Button buttonSave;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        //for save the settings and get from main
        SharedPreferences sharedPref = getSharedPreferences("BTDETECTOR", Context.MODE_PRIVATE);
        editor = sharedPref.edit();



        //load the GUI
        spinnerZone01 = (Spinner) findViewById(R.id.spinnerZone01);
        spinnerZone02 = (Spinner) findViewById(R.id.spinnerZone02);
        spinnerZone03 = (Spinner) findViewById(R.id.spinnerZone03);
        spinnerZone04 = (Spinner) findViewById(R.id.spinnerZone04);
        spinnerZone05 = (Spinner) findViewById(R.id.spinnerZone05);

        //set the listener to the spinners
        spinnerZone01.setOnItemSelectedListener(new MyOnItemSelectedListener());
        spinnerZone02.setOnItemSelectedListener(new MyOnItemSelectedListener());
        spinnerZone03.setOnItemSelectedListener(new MyOnItemSelectedListener());
        spinnerZone04.setOnItemSelectedListener(new MyOnItemSelectedListener());
        spinnerZone05.setOnItemSelectedListener(new MyOnItemSelectedListener());

        //Save Button
        buttonSave = (Button)  findViewById(R.id.buttonSave);


        //load the previous selected value
        Zone01 = sharedPref.getInt(ZONE_1,Zone01);
        Zone02 = sharedPref.getInt(ZONE_2,Zone02);
        Zone03 = sharedPref.getInt(ZONE_3,Zone03);
        Zone04 = sharedPref.getInt(ZONE_4,Zone04);
        Zone05 = sharedPref.getInt(ZONE_5,Zone05);


        initializeUI();
        initBtnSave();


    }



    private void initBtnSave() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(ZONE_1,Zone01);
                editor.putInt(ZONE_2,Zone02);
                editor.putInt(ZONE_3,Zone03);
                editor.putInt(ZONE_4,Zone04);
                editor.putInt(ZONE_5,Zone05);
                editor.commit();
                Intent intent = new Intent();
              //  intent.putExtra(Settings.REQUEST_SETTINGS,);

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }



    private void initializeUI() {


        // list array from Spinner item type not used any more
        /*
            ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
                for (int i = 0; i < 19; i++) {
                  spinnerItems.add(new SpinnerItem( i*10,  i));
                }
        */

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.distance));
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinnerZone01.setAdapter(adapter);
        spinnerZone02.setAdapter(adapter);
        spinnerZone03.setAdapter(adapter);
        spinnerZone04.setAdapter(adapter);
        spinnerZone05.setAdapter(adapter);


        spinnerZone01.setSelection(adapter.getPosition(Zone01.toString()));
        spinnerZone02.setSelection(adapter.getPosition(Zone02.toString()));
        spinnerZone03.setSelection(adapter.getPosition(Zone03.toString()));
        spinnerZone04.setSelection(adapter.getPosition(Zone04.toString()));
        spinnerZone05.setSelection(adapter.getPosition(Zone05.toString()));



        //:TODO save on each zone different tone sound make spinner adapter with android available sounds and select



    }


    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            String selectedItem = parent.getItemAtPosition(pos).toString();
            Log.i(TAG, "selectedItem:"+selectedItem + "id"+id +" parent "+parent.getId());

            // check the zones if some is 0 stay to 0
            // if less than previous then +10 and save
            // the bigger zone must bigger than previous

            switch (parent.getId()) {

                case R.id.spinnerZone01:
                    Zone01 = Integer.parseInt(selectedItem);

                break;

                case R.id.spinnerZone02:

                    if(Zone01>=Integer.parseInt(selectedItem)){
                        spinnerZone02.setSelection((Zone01/10)+1);
                        Zone02=Zone01+10;
                    }else{
                        Zone02 = Integer.parseInt(selectedItem);
                    }
                break;
                case R.id.spinnerZone03:

                    if(Zone02>=Integer.parseInt(selectedItem)){
                        spinnerZone03.setSelection((Zone02/10)+1);
                        Zone03=Zone02+10;
                    }else{
                        Zone03 = Integer.parseInt(selectedItem);
                    }
                    break;
                case R.id.spinnerZone04:
                    if(Integer.parseInt(selectedItem)==0){
                        Zone04 = Integer.parseInt(selectedItem);
                    }else {
                    if(Zone03>=Integer.parseInt(selectedItem)){
                        spinnerZone04.setSelection((Zone03/10)+1);
                        Zone04=Zone03+10;
                    }else{
                        Zone04 = Integer.parseInt(selectedItem);
                    }
                    }
                    break;
                case R.id.spinnerZone05:

                    if(Integer.parseInt(selectedItem)==0){
                        Zone05 = Integer.parseInt(selectedItem);
                    }else {
                        if (Zone04 >= Integer.parseInt(selectedItem)) {
                            spinnerZone05.setSelection((Zone04 / 10) + 1);
                            Zone05 = Zone04 + 10;
                        } else {
                            Zone05 = Integer.parseInt(selectedItem);
                        }
                    }
                    break;
            }


        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

}
