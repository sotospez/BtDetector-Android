package com.sotosgr.btdetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class Main extends Activity implements OnClickListener {
    BluetoothSPP bt;
    ToneGenerator toneGen;
    TextToSpeech tts;


    //label KEY for saving on shared pref
    public static final String SOUND_FROM_DETECTOR = "SOUND_FROM_DETECTOR";
    public static final String SOUND_FROM_ANDROID = "SOUND_FROM_ANDROID";
    public static final String SOUND_TTS = "SOUND_TTS";


    //init zone settings
    Integer Zone01 =20;
    Integer Zone02 =40;
    Integer Zone03 =60;
    Integer Zone04 =80;
    Integer Zone05 =100;

    //get the shared pref for save and load settings
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    //init sound settings
    Boolean SoundDetecor =false;
    Boolean SoundAndroid =false;
    Boolean SoundTTS =false;

    //init status text and distance global
    TextView txtStatus;
    TextView txtDistance;

    private static final String TAG = "Main";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        Button btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        Button btnConnect = (Button)findViewById(R.id.btnConnectMain);
        btnConnect.setOnClickListener(this);

        //global shared pref
        sharedPref = getSharedPreferences("BTDETECTOR", Context.MODE_PRIVATE);

        //global edit for shared pref
        editor = sharedPref.edit();

        //global tone generator for make sound from android
        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        txtStatus = (TextView)findViewById(R.id.textViewStatus);
        txtDistance = (TextView)findViewById(R.id.textViewDistance);


        //inti TTS
        tts = new TextToSpeech(Main.this, null);
        tts.setLanguage(Locale.US);




        initBT();
        initSettings();
        loadZones();



    }




    public void sTTS(String text){
        if(SoundTTS) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    public void sendBtDetectorSettigns(){


        if(bt.isBluetoothEnabled()) {
            try {
                Thread.sleep(150);

                bt.send("ZONE01:" + Zone01, true);
                bt.send("ZONE02:" + Zone02, true);
                bt.send("ZONE03:" + Zone03, true);
                bt.send("ZONE04:" + Zone04, true);
                bt.send("ZONE05:" + Zone05, true);
                bt.send("SOUND:" + (SoundDetecor ? 1 : 0), true);


            } catch (InterruptedException e) {
                // Process exception
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        }
    }

    public void initBT(){
        //set context BT
        bt = new BluetoothSPP(getApplicationContext());

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_not_available), Toast.LENGTH_SHORT).show();
            sTTS(getString(R.string.bluetooth_not_available) );
            finish();
        }






        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                String strState="";
                if (state == BluetoothState.STATE_CONNECTED) {
                    strState =getString(R.string.connected) ;

                    //on Connected send the init setting to BT detector
                    sendBtDetectorSettigns();

                } else if (state == BluetoothState.STATE_CONNECTING) {
                    strState =getString(R.string.connecting) ;
                } else if (state == BluetoothState.STATE_LISTEN) {
                    strState =getString(R.string.listen) ;
                } else if (state == BluetoothState.STATE_NONE) {
                    strState =getString(R.string.none) ;
                }

                if (!strState.isEmpty()) {
                    String nState = getString(R.string.state) + " : " + strState;
                    Log.i(TAG, nState);
                    txtStatus.setText(nState);
                    sTTS(nState );

                }
            }
        });


        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.i(TAG, "Message : " + message);
                if (message.contains("distance")) {
                    txtDistance.setText(message);

                    String[] parts = message.split("\\:");
                    String part1 = parts[0];
                    Integer part2Distance = Integer.parseInt(parts[1]);

                    sTTS(part2Distance.toString());
                    Integer tone =50;


                    if(SoundAndroid) {
                        if (part2Distance < Zone01) {
                            Log.i(TAG, "part2Distance " + Zone01 + " -" + part2Distance);
                            toneGen.startTone(ToneGenerator.TONE_CDMA_CALLDROP_LITE, part2Distance + 50);
                        } else if (part2Distance < Zone02) {
                            Log.i(TAG, "part2Distance " + Zone02 + " -" + part2Distance);
                            toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, part2Distance + 80);
                        } else if (part2Distance < Zone03) {
                            Log.i(TAG, "part2Distance " + Zone03 + " -" + part2Distance);
                            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, part2Distance + 100);
                        } else if (part2Distance < Zone04) {
                            Log.i(TAG, "part2Distance " + Zone04 + " -" + part2Distance);
                            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, part2Distance + 150);
                        } else if (part2Distance < Zone05) {
                            Log.i(TAG, "part2Distance " + Zone05 + " -" + part2Distance);
                            toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, part2Distance + 200);
                        }
                    }

                }

            }
        });


        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name, Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Connection lost" ,Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Log.i(TAG, "Unable to connect");
            }
        });

        bt.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                Log.i(TAG, "New Connection - " + name + " - " + address);
            }

            public void onAutoConnectionStarted() {
                Log.i(TAG, "Auto menu_connection started");
            }
        });


    }






    public void initSettings() {
        Log.i(TAG, "initSettings");


        SoundAndroid = sharedPref.getBoolean(SOUND_FROM_ANDROID,SoundAndroid);
        SoundDetecor = sharedPref.getBoolean(SOUND_FROM_DETECTOR,SoundDetecor);
        SoundTTS = sharedPref.getBoolean(SOUND_TTS,SoundTTS);

        Switch sFromDetector = (Switch)findViewById(R.id.switchSoundFromDetector);
        sFromDetector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SoundDetecor = isChecked;
                editor.putBoolean(SOUND_FROM_DETECTOR,isChecked);
                editor.commit();
                bt.send("SOUND:" + (SoundDetecor ? 1 : 0), true);
            }
        });

        Switch sFromAndroid = (Switch)findViewById(R.id.switchSoundFromAndroid);
        sFromAndroid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SoundAndroid = isChecked;
                editor.putBoolean(SOUND_FROM_ANDROID,isChecked);
                editor.commit();

            }
        });

        Switch sFromTTS = (Switch)findViewById(R.id.switchSoundFromTTS);
        sFromTTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SoundTTS =isChecked;
                editor.putBoolean(SOUND_TTS,isChecked);
                editor.commit();
            ;
            }
        });


        sFromAndroid.setChecked(SoundAndroid);
        sFromDetector.setChecked(SoundDetecor);
        sFromTTS.setChecked(SoundTTS);





    }


    /**
     * init zone data values from shared pref
     */
    public void loadZones() {
        Log.i(TAG, "loadZones");

        Zone01 = sharedPref.getInt(Settings.ZONE_1,Zone01);
        Zone02 = sharedPref.getInt(Settings.ZONE_2,Zone02);
        Zone03 = sharedPref.getInt(Settings.ZONE_3,Zone03);
        Zone04 = sharedPref.getInt(Settings.ZONE_4,Zone04);
        Zone05 = sharedPref.getInt(Settings.ZONE_5,Zone05);
        sendBtDetectorSettigns();
    }






    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
            case R.id.btnSettings:
                intent = new Intent(getApplicationContext(), Settings.class);
                startActivityForResult(intent, Settings.REQUEST_SETTINGS);

            case R.id.btnConnectMain:
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                     intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
                break;
        }
    }



    public void onDestroy() {
        super.onDestroy();
        //stop the bt
        bt.stopService();
        //Close the Text to Speech Library
        if(tts != null) {
            tts.stop();
            tts.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
    }

    public void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , getString(R.string.bluetooth_not_enabled)
                        , Toast.LENGTH_SHORT).show();
                tts.speak(getString(R.string.bluetooth_not_enabled) , TextToSpeech.QUEUE_FLUSH, null);
                finish();
            }
        }else if(requestCode == Settings.REQUEST_SETTINGS) {
            if(resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "settings.REQUEST_SETTINGS : RESULT_OK");
                Log.i(TAG, "Data");
              //  Log.i(TAG, data.toString());
                loadZones();
                //data.getExtras().getString
            } else {

            }
        }
    }

    public void setup() {
        Button btnSend = (Button)findViewById(R.id.btnSendMain);
        btnSend.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                bt.send("BEEP", true);

            }
        });

        bt.autoConnect("HC05");
    }
}
