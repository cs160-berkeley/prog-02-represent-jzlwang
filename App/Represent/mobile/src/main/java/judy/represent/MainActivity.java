package judy.represent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.fabric.sdk.android.Fabric;

public class MainActivity
        extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "wzDckZQFRaN9ixxuGignJsd48";
    private static final String TWITTER_SECRET = "McRiayynz4sHNcdIlgVbWfSQlGaGho0SZPJztIx7YNsMKGHtXb";

    private static final String GOOGLE_SERVER_KEY = "AIzaSyCCkvG9j-x70S2AldN7OupsS1amuFA-_-g";
    private static final String GOOGLE_ANDROID_KEY = "AIzaSyCzpsWxFtylovhyruQIJTucHP9Y2l_pEMk";


    private Button currentLocButton;
    private Button goButton;
    private EditText zipCode;
    private TwitterLoginButton loginButton;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double latitude;
    private double longitude;
    private String zip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        currentLocButton = (Button) findViewById(R.id.curr_loc_btn);
        goButton = (Button) findViewById(R.id.go_btn);
        zipCode = (EditText) findViewById(R.id.zip);
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);

        Typeface tf = Typeface.createFromAsset(this.getResources().getAssets(), "perpetua.ttf");
        zipCode.setTypeface(tf);
        currentLocButton.setTypeface(tf);
        goButton.setTypeface(tf);

        zipCode.getBackground().clearColorFilter();

        /** Authenticate with Twitter first. **/
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                loginButton.setVisibility(View.GONE);
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(getBaseContext(), "Could not authenticate.", Toast.LENGTH_SHORT).show();
            }
        });

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        currentLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                getZipFromLatLong(latitude, longitude);

                Intent toCongress = new Intent(getApplicationContext(), CongressionalActivity.class);
                toCongress.putExtra("LAT", Double.toString(latitude));
                toCongress.putExtra("LONG", Double.toString(longitude));
                toCongress.putExtra("ZIP", zip);
                startActivity(toCongress);
            }
        });

        zipCode = (EditText) findViewById(R.id.zip);
        goButton = (Button) findViewById(R.id.go_btn);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String zip = zipCode.getText().toString();

                // render the congressional activity
                Intent toCongress2 = new Intent(getApplicationContext(), CongressionalActivity.class);
                toCongress2.putExtra("ZIP", zip);
                startActivity(toCongress2);

                // render main activity on wear
//                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
//                sendIntent.putExtra("ZIP", zip);
//                sendIntent.putExtra("LOC", "None");
//                startService(sendIntent);
            }
        });
    }

    /**
     * Method to display the location on UI
     * */
    private void getLocation() {

        try {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Toast.makeText(getBaseContext(), latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
//            lblLocation.setText(latitude + ", " + longitude);

        } else {
            Toast.makeText(getBaseContext(), "Couldn't get the location.", Toast.LENGTH_SHORT).show();
//            lblLocation
//                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void getZipFromLatLong(final double lat, final double lon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String latitude = Double.toString(lat);
                String longitude = Double.toString(lon);
                Log.d("poop", "lat and lon: " + latitude + ", " + longitude);
                Log.d("poop", "HTTP Request: " + "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&result_type=postal_code&key="+GOOGLE_SERVER_KEY);
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&result_type=postal_code&key="+GOOGLE_SERVER_KEY);
                httppost.setHeader("Content-type", "application/json");
                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    Log.d("T", Integer.toString(response.getStatusLine().getStatusCode()));

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                    e.printStackTrace();
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }

                if (result == null) {
                    Log.d("T", "GET request failed");
                } else {
                    try {
                        Log.d("poop", result);
                        JSONObject body = new JSONObject(result);
                        JSONArray addrComponents = body.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                        String zipCode = addrComponents.getJSONObject(0).getString("short_name");
                        Log.d("T", zipCode);
                        zip = zipCode;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
}
