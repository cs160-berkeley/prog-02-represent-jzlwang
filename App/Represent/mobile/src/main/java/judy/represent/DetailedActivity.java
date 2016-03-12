package judy.represent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;

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
import java.util.List;

public class DetailedActivity extends Activity {

    private Button backButton;
    private RoundedImageView image;
    private MainTextView name;
    private MainTextView party;
    private MainTextView email;
    private MainTextView website;
    private MainTextView twitter;
    private MainTextView currentTerm;
    private MainTextView committees;
    private MainTextView bills;
    private String bioguideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        backButton = (Button) findViewById(R.id.back_btn);
        Typeface tf = Typeface.createFromAsset(this.getResources().getAssets(), "perpetua.ttf");
        backButton.setTypeface(tf);

        image = (RoundedImageView) findViewById(R.id.image);
        name = (MainTextView) findViewById(R.id.name);
        party = (MainTextView) findViewById(R.id.party);
        email = (MainTextView) findViewById(R.id.email);
        website = (MainTextView) findViewById(R.id.website);
        twitter = (MainTextView) findViewById(R.id.twitter);
        currentTerm = (MainTextView) findViewById(R.id.current_term);
        committees = (MainTextView) findViewById(R.id.committees);
        bills = (MainTextView) findViewById(R.id.bills);

        Intent fromCongress = getIntent();
        final String longitude = fromCongress.getStringExtra("LONG");
        final String latitude = fromCongress.getStringExtra("LAT");
        final String zipCode = fromCongress.getStringExtra("ZIP");
        final String imageURL = fromCongress.getStringExtra("IMAGE_URL");
        final int repNum = fromCongress.getIntExtra("REP_NUM", -1);
        final String JSON = fromCongress.getStringExtra("JSON");

        try {
            JSONObject jObject = new JSONObject(JSON);
            JSONArray jArray = jObject.getJSONArray("results");
            if (jArray == null) {
                Log.d("T", "dis shit is NULL AS FUCK!!!!");
            }

            JSONObject rep = jArray.getJSONObject(repNum);

            if (imageURL != null) {
                new DownloadImageTask(image).execute(imageURL);
            }

            name.setText(rep.getString("first_name") + " " + rep.getString("last_name"));
            if (rep.getString("party").equals("D")) {
                party.setText("Democrat");
                party.setTextColor(Color.parseColor("#6666FF"));
            } else if (rep.getString("party").equals("R")) {
                party.setText("Republican");
                party.setTextColor(Color.parseColor("#FF6666"));
            } else {
                party.setText("Independent");
            }
            email.setText(rep.getString("oc_email"));
            website.setText(rep.getString("website"));
            twitter.setText(rep.getString("twitter_id"));
            currentTerm.setText(rep.getString("term_end"));
            bioguideId = rep.getString("bioguide_id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("http://congress.api.sunlightfoundation.com/committees?member_ids=" + bioguideId + "&apikey=62925f0400f349228a3fbb28c2e34f49");
                httppost.setHeader("Content-type", "application/json");
                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (Exception squish) {
                    }
                }

                final String jsonResults = result;

//      End dumb HTTP stuff. Now I have a JSON called result.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(jsonResults);
                            JSONArray jArray = jObject.getJSONArray("results");
                            if (jArray == null) {
                                Log.d("T", "dis shit is NULL AS FUCK!!!!");
                            }
                            for (int i = 0; i < jArray.length(); i++) {
                                String commName = jArray.getJSONObject(i).getString("name");
                                committees.setText(committees.getText()+"\n - "+commName);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        t.start();

        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /** Retrieve bills **/
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("http://congress.api.sunlightfoundation.com/bills?sponsor_id=" + bioguideId + "&apikey=62925f0400f349228a3fbb28c2e34f49");
                httppost.setHeader("Content-type", "application/json");
                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (Exception squish) {
                    }
                }

                final String jsonResults = result;

//      End dumb HTTP stuff. Now I have a JSON called result.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(jsonResults);
                            JSONArray jArray = jObject.getJSONArray("results");
                            if (jArray == null) {
                                Log.d("T", "dis shit is NULL AS FUCK!!!!");
                            }
                            for (int i = 0; i < jArray.length(); i++) {
                                String introDate = jArray.getJSONObject(i).getString("introduced_on");
                                String title = jArray.getJSONObject(i).getString("short_title");
                                bills.setText(bills.getText()+"\n - "+introDate+": "+title);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        t2.start();

        try {
            t2.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCongress = new Intent(getApplicationContext(), CongressionalActivity.class);
                toCongress.putExtra("LONG", longitude);
                toCongress.putExtra("LAT", latitude);
                toCongress.putExtra("ZIP", zipCode);
                startActivity(toCongress);
            }
        });

    }
}
