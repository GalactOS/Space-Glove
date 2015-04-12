package comjosephdougs.httpsgithub.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
    public Firebase myFirebaseRef;
    public SensorManager sMgr;
    public Button toggle;
    public Button up;
    public Button down;
    public Button go;
    public Button record;
    public String TAG = "Main";
    public Sensor acc;
    public Sensor gyr;
    public Sensor mag;
    public boolean geoMeasured;
    public int flagx;

    public boolean goFlag;

    public final static String username = "89652cbc-06d6-4e8b-9679-06f40381aaaf";
    public final static String password = "cyI6zx2rKS14";
    public final static String apiURL = "https://stream.watsonplatform.net/speech-to-text-beta/api";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geoMeasured = false;

        goFlag = false;

        flagx = 0;



        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://fiery-inferno-6630.firebaseio.com/");


        toggle = (Button)findViewById(R.id.Toggle);
        up = (Button)findViewById(R.id.up);
        down = (Button)findViewById(R.id.down);
        go = (Button)findViewById(R.id.go);
        record = (Button)findViewById(R.id.stop);

        toggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myFirebaseRef.child("selection").setValue("Toggle");
                //new CallAPI().execute(apiURL );
            }
        });


        up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Send up");
                myFirebaseRef.child("selection").setValue("up");
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Send down");
                myFirebaseRef.child("selection").setValue("down");
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send go");
                goFlag = !goFlag;
                myFirebaseRef.child("go").setValue(goFlag);
            }
        });

        sMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

        acc = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyr = sMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //mag = sMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sMgr.registerListener(this,
                sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this,
                sMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        //sMgr.registerListener(this,
          //      sMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            //    SensorManager.SENSOR_DELAY_NORMAL);


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //myFirebaseRef.child("message").setValue("Sensor Changed");
        //Log.d(TAG, "Sensor Changed");

        //SensorManager.getOrientation();

        float[] values = event.values;

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            accel(values);
        } else {
            gyros(values);
        }

    }

    private void accel(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];


        Log.d(TAG, Arrays.toString(values));

        /*if (flagx == 0 ) {


        }*/

        myFirebaseRef.child("acc_x").setValue(x);
        myFirebaseRef.child("acc_y").setValue(y);
        myFirebaseRef.child("acc_z").setValue(z);

    }

    private void gyros(float[] values){
        float x = values[0];
        float y = values[1];
        float z = values[2];

        myFirebaseRef.child("gyr_x").setValue(x);
        myFirebaseRef.child("gyr_y").setValue(y);
        myFirebaseRef.child("gyr_z").setValue(z);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech not supported :(",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    myFirebaseRef.child("message").setValue(result.get(0));
                }
                break;
            }

        }
    }

}
