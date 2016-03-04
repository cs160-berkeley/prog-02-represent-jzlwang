package judy.represent;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private Button mDetBtn;
    private LinearLayout watchFace;
    private TextView detText;
    int index = 0;

    private float mLastX, mLastY, mLastZ;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 2.0;
    private boolean mInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        mDetBtn = (Button) findViewById(R.id.det_btn);
        watchFace = (LinearLayout) findViewById(R.id.watchface);
        detText = (TextView) findViewById(R.id.det_txt);

        final Integer[] reps = {R.drawable.chris_van_hollen, R.drawable.barbara_a_mikulski, R.drawable.benjamin_l_cardin, R.drawable.john_k_delaney};
        final String[] repnames = {"Chris Van Hollen\nDemocrat", "Barbara Mikulski\nRepublican", "Benjamin Cardin\nDemocrat", "John Delaney\nRepublican"};
        watchFace.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeLeft() {
                mDetBtn.setVisibility(View.VISIBLE);
                int numReps = reps.length;
                index = (index + 1)%numReps;
                watchFace.setBackgroundResource(reps[index]);
                detText.setText(repnames[index]);
            }

            public void onSwipeBottom() {
                Intent toVote = new Intent(getBaseContext(), VoteActivity.class);
                startActivity(toVote);
            }

            public void onSwipeTop() {
                Intent toMain = new Intent(getBaseContext(), MainActivity.class);
                startActivity(toMain);
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            String locInfo = extras.getString("INFO");
            Toast.makeText(MainActivity.this, "ZIP: " + locInfo, Toast.LENGTH_SHORT).show();
        }

        mDetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("T", "BLOOP BLOOP");
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("RANDLOC", "None");
                startService(sendIntent);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            if (deltaX > NOISE || deltaY > NOISE || deltaZ > NOISE) {
                Location randLoc = generateRandomLoc();
                String locString = "Loc: "+String.format("%.2f", randLoc.getLatitude())+", "+String.format("%.2f", randLoc.getLongitude());
                Toast.makeText(MainActivity.this, locString, Toast.LENGTH_SHORT).show();

                Intent sendLoc = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendLoc.putExtra("RANDLOC", locString);
                startService(sendLoc);
            }
            mLastX = x;
            mLastY = y;
            mLastZ = z;
        }
    }

    public Location generateRandomLoc() {

        double minLat = 24.31;
        double maxLat = 49.23;
        double latitude = minLat + (double)(Math.random() * ((maxLat - minLat) + 1));
        double minLon = 66.57;
        double maxLon = 124.46;
        double longitude = minLon + (double)(Math.random() * ((maxLon - minLon) + 1));

        Location rl =  new Location("");
        rl.setLatitude(latitude);
        rl.setLongitude(longitude);
        return rl;
    }
}
