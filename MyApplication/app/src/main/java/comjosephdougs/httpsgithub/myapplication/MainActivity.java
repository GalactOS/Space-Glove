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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geoMeasured = false;
        curGeo = null;

        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://fiery-inferno-6630.firebaseio.com/");


        toggle = (Button)findViewById(R.id.Toggle);
        up = (Button)findViewById(R.id.up);
        down = (Button)findViewById(R.id.down);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFirebaseRef.child("selection").setValue("Toggle");
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

        acc = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyr = sMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mag = sMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sMgr.registerListener(this,
                sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        sMgr.registerListener(this,
                sMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);
        sMgr.registerListener(this,
                sMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);


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
        //Log.d(TAG, "" + (event.sensor.getType() == Sensor.TYPE_GYROSCOPE));
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && geoMeasured) {
            accel(values, curGeo);
            geoMeasured = false;
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gyros(values);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            curGeo = values;
            geoMeasured = true;
        }
    }

    private void accel(float[] values, float[] geoValues) {
        float x = values[0];
        float y = values[1];
        float z = values[2] - SensorManager.STANDARD_GRAVITY;

        float[] R = new float[9];
        float[] I = new float[9];


        SensorManager.getRotationMatrix(R, null, values, geoValues);

        float[] result = new float[3];

        SensorManager.getOrientation(R, result);

        Log.d(TAG, Arrays.toString(result));

        /*
        if (x > 4 || x < -4) {
            Log.d(TAG, "acc_x " + x);
            myFirebaseRef.child("acc_x").setValue(x);
        }
        if (y > 4 || y < -4) {
            Log.d(TAG, "acc_y " + y);
            myFirebaseRef.child("acc_y").setValue(y);
        }
        if (z > 4 || z < -4) {
            Log.d(TAG, "acc_z " + z);
            myFirebaseRef.child("acc_z").setValue(z);
        }*/
    }

    private void gyros(float[] values){
        //Log.d(TAG, "values: " + Arrays.toString(values));
        float x = values[0];
        float y = values[1];
        float z = values[2];
        /*
        if (x > 4 || x < -4) {
            Log.d(TAG, "gyr_x " + x);
            myFirebaseRef.child("gyr_x").setValue(x);
        }
        if (y > 4 || y < -4) {
            Log.d(TAG, "gyr_y " + y);
            myFirebaseRef.child("gyr_y").setValue(y);
        }
        if (z > 4 || z < -4) {
            Log.d(TAG, "gyr_z " + z);
            myFirebaseRef.child("gyr_z").setValue(z);
        }*/
    }
}
