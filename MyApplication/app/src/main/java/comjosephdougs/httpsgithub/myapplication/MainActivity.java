package comjosephdougs.httpsgithub.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
    public Firebase myFirebaseRef;
    public SensorManager sMgr;
    public Button toggle;
    public Button up;
    public Button down;
    public String TAG = "Main";
    public Sensor acc;
    public Sensor gyr;
    public Sensor mag;
    public boolean geoMeasured;
    public float[] curGeo;

    public final static String username = "89652cbc-06d6-4e8b-9679-06f40381aaaf";
    public final static String password = "cyI6zx2rKS14";
    public final static String apiURL = "https://stream.watsonplatform.net/speech-to-text-beta/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geoMeasured = false;




        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://fiery-inferno-6630.firebaseio.com/");


        toggle = (Button)findViewById(R.id.Toggle);
        up = (Button)findViewById(R.id.up);
        down = (Button)findViewById(R.id.down);

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
}
