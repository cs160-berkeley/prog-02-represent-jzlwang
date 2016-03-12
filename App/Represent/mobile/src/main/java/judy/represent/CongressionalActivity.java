package judy.represent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import io.fabric.sdk.android.Fabric;

public class CongressionalActivity extends Activity {

    private static final String GOOGLE_SERVER_KEY = "AIzaSyCCkvG9j-x70S2AldN7OupsS1amuFA-_-g";
    private static final String GOOGLE_ANDROID_KEY = "AIzaSyCzpsWxFtylovhyruQIJTucHP9Y2l_pEMk";

    private Button backButton;
    private LinearLayout extraRep;
    private Button infoButton1;
    private Button infoButton2;
    private Button infoButton3;
    private Button infoButton4;
    private ArrayList<String> repNames = new ArrayList<String>();
    private ArrayList<String> repParties = new ArrayList<String>();
    private ArrayList<String> imageURLs = new ArrayList<String>();
    private String jsonResults;
    private String county;
    private Integer obama;
    private Integer romney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);

        backButton = (Button) findViewById(R.id.back_btn);

        Typeface tf = Typeface.createFromAsset(this.getResources().getAssets(), "perpetua.ttf");
        backButton.setTypeface(tf);

        extraRep = (LinearLayout) findViewById(R.id.extra_rep);

        Intent fromMain = getIntent();
        int fromZIP = fromMain.getIntExtra("EXTRA_REP", 0);
        final String latitude = fromMain.getStringExtra("LAT");
        final String longitude = fromMain.getStringExtra("LONG");
        final String zipCode = fromMain.getStringExtra("ZIP");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (zipCode != null || (latitude != null && longitude != null)) {
                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    HttpGet httppost = null;
                    if (zipCode != null) {
                        httppost = new HttpGet("http://congress.api.sunlightfoundation.com/legislators/locate?zip="+zipCode+"&apikey=62925f0400f349228a3fbb28c2e34f49");
                    } else if (latitude != null && longitude != null) {
                        httppost = new HttpGet("http://congress.api.sunlightfoundation.com/legislators/locate?latitude="+latitude+"&longitude="+longitude+"&apikey=62925f0400f349228a3fbb28c2e34f49");
                    }
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

                    jsonResults = result;

//      End dumb HTTP stuff. Now I have a JSON called result.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jObject = new JSONObject(jsonResults);
                                JSONArray jArray = jObject.getJSONArray("results");
                                int i;
                                for (i=0; i < jArray.length(); i++) {
                                    final JSONObject rep = jArray.getJSONObject(i);
                                    RoundedImageView repImage = null;
                                    MainTextView repName = null;
                                    MainTextView party = null;
                                    MainTextView email = null;
                                    MainTextView website = null;
                                    MainTextView twitter = null;
                                    MainTextView tweet = null;
                                    switch (i) {
                                        case 0:
                                            repImage = (RoundedImageView) findViewById(R.id.rep1);
                                            repName = (MainTextView) findViewById(R.id.repname1);
                                            party = (MainTextView) findViewById(R.id.repparty1);
                                            email = (MainTextView) findViewById(R.id.repemail1);
                                            website = (MainTextView) findViewById(R.id.repwebsite1);
                                            twitter = (MainTextView) findViewById(R.id.reptwitter1);
                                            tweet = (MainTextView) findViewById(R.id.reptweet1);
                                            break;
                                        case 1:
                                            repImage = (RoundedImageView) findViewById(R.id.rep2);
                                            repName = (MainTextView) findViewById(R.id.repname2);
                                            party = (MainTextView) findViewById(R.id.repparty2);
                                            email = (MainTextView) findViewById(R.id.repemail2);
                                            website = (MainTextView) findViewById(R.id.repwebsite2);
                                            twitter = (MainTextView) findViewById(R.id.reptwitter2);
                                            tweet = (MainTextView) findViewById(R.id.reptweet2);
                                            break;
                                        case 2:
                                            repImage = (RoundedImageView) findViewById(R.id.rep3);
                                            repName = (MainTextView) findViewById(R.id.repname3);
                                            party = (MainTextView) findViewById(R.id.repparty3);
                                            email = (MainTextView) findViewById(R.id.repemail3);
                                            website = (MainTextView) findViewById(R.id.repwebsite3);
                                            twitter = (MainTextView) findViewById(R.id.reptwitter3);
                                            tweet = (MainTextView) findViewById(R.id.reptweet3);

                                            break;
                                        case 3:
                                            repImage = (RoundedImageView) findViewById(R.id.rep4);
                                            extraRep.setVisibility(View.VISIBLE);
                                            repName = (MainTextView) findViewById(R.id.repname4);
                                            party = (MainTextView) findViewById(R.id.repparty4);
                                            email = (MainTextView) findViewById(R.id.repemail4);
                                            website = (MainTextView) findViewById(R.id.repwebsite4);
                                            twitter = (MainTextView) findViewById(R.id.reptwitter4);
                                            tweet = (MainTextView) findViewById(R.id.reptweet4);
                                            break;
                                    }
                                    String firstName = rep.getString("first_name");
                                    String lastName = rep.getString("last_name");
                                    repName.setText(firstName + " " + lastName);
                                    repNames.add(firstName + " " + lastName);
                                    party.setText(rep.getString("party"));
                                    repParties.add(rep.getString("party"));
                                    email.setText(rep.getString("oc_email"));
                                    website.setText(rep.getString("website"));
                                    twitter.setText(rep.getString("twitter_id"));
                                    final MainTextView twitTweet = tweet;
                                    final RoundedImageView profilePic = repImage;
                                    // Get the user profile image shit
                                    Thread t1 = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(Twitter.getSessionManager().getActiveSession());
                                                UsersService usersService = myTwitterApiClient.getUsersService();
                                                usersService.show(null, rep.getString("twitter_id"), true, new Callback<User>() {
                                                    @Override
                                                    public void success(Result<User> result) {
                                                        String imageURL = result.data.profileImageUrlHttps;
                                                        imageURL = imageURL.replace("_normal", "");
                                                        imageURLs.add(imageURL);
                                                        new DownloadImageTask((RoundedImageView) profilePic).execute(imageURL);
                                                    }

                                                    @Override
                                                    public void failure(TwitterException e) {

                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    t1.start();

                                    try {
                                        t1.join();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // Get the Tweet
                                    Thread t2 = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                TwitterApiClient twitterApiClient = Twitter.getApiClient();
                                                StatusesService statusesService = twitterApiClient.getStatusesService();
                                                statusesService.userTimeline(null, rep.getString("twitter_id"), 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                                                    @Override
                                                    public void success(Result<List<Tweet>> result) {
                                                        List<Tweet> tweets = result.data;
                                                        Tweet firstTweet = tweets.get(0);
                                                        twitTweet.setText(firstTweet.text);
                                                    }

                                                    @Override
                                                    public void failure(TwitterException e) {
                                                        Log.d("TwitterKit", "Load Tweet failure", e);
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    t2.start();
                                    try {
                                        t2.join();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (i == 3) extraRep.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
        t.start();

        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        backButton = (Button) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(toMain);
            }
        });

        String urlString = "";
        for (String s : imageURLs) {
            urlString += s + "...";
        }

        if (latitude == null || longitude == null) {
            getCountyFromZip(zipCode);
        } else {
            getCountyFromLatLong(latitude, longitude);
        }
        Log.d("T", "obama and romeny: " + Integer.toString(obama) + ", " + Integer.toString(romney));
        Log.d("T", "lat: " + latitude);
        Log.d("T", "long: " + longitude);
        Log.d("T", "zip code: " + zipCode);
        String mExtra = longitude + "jooby" + latitude + "jooby" + zipCode + "jooby" + jsonResults + "jooby" + Integer.toString(obama) + "jooby" + Integer.toString(romney) + "jooby" + county;
        Intent sendLocIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendLocIntent.putExtra("INFOSTRING", mExtra);
        startService(sendLocIntent);

        infoButton1 = (Button) findViewById(R.id.info_btn1);
        infoButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetailed1 = new Intent(getApplicationContext(), DetailedActivity.class);
                toDetailed1.putExtra("REP_NUM", 0);
                toDetailed1.putExtra("IMAGE_URL", imageURLs.get(0));
                toDetailed1.putExtra("JSON", jsonResults);
                toDetailed1.putExtra("LONG", longitude);
                toDetailed1.putExtra("LAT", latitude);
                toDetailed1.putExtra("ZIP", zipCode);
                startActivity(toDetailed1);
            }
        });

        infoButton2 = (Button) findViewById(R.id.info_btn2);
        infoButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetailed2 = new Intent(getApplicationContext(), DetailedActivity.class);
                toDetailed2.putExtra("REP_NUM", 1);
                toDetailed2.putExtra("IMAGE_URL", imageURLs.get(1));
                toDetailed2.putExtra("JSON", jsonResults);
                toDetailed2.putExtra("LONG", longitude);
                toDetailed2.putExtra("LAT", latitude);
                toDetailed2.putExtra("ZIP", zipCode);
                startActivity(toDetailed2);
            }
        });

        infoButton3 = (Button) findViewById(R.id.info_btn3);
        infoButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetailed3 = new Intent(getApplicationContext(), DetailedActivity.class);
                toDetailed3.putExtra("REP_NUM", 2);
                toDetailed3.putExtra("IMAGE_URL", imageURLs.get(2));
                toDetailed3.putExtra("JSON", jsonResults);
                toDetailed3.putExtra("LONG", longitude);
                toDetailed3.putExtra("LAT", latitude);
                toDetailed3.putExtra("ZIP", zipCode);
                startActivity(toDetailed3);
            }
        });

        infoButton4 = (Button) findViewById(R.id.info_btn4);
        infoButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetailed4 = new Intent(getApplicationContext(), DetailedActivity.class);
                toDetailed4.putExtra("REP_NUM", 3);
                toDetailed4.putExtra("IMAGE_URL", imageURLs.get(3));
                toDetailed4.putExtra("JSON", jsonResults);
                toDetailed4.putExtra("LONG", longitude);
                toDetailed4.putExtra("LAT", latitude);
                toDetailed4.putExtra("ZIP", zipCode);
                startActivity(toDetailed4);
            }
        });
    }

    private void getCountyFromLatLong(final String latitude, final String longitude) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key="+GOOGLE_SERVER_KEY);
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
                        JSONObject body = new JSONObject(result);
                        JSONArray addrComponents = body.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                        String county1 = addrComponents.getJSONObject(4).getString("long_name");
                        String state = addrComponents.getJSONObject(5).getString("short_name");
                        String key = county1 + ", " + state;
                        Log.d("T", key);
                        county = key;
                        Log.d("T", "LAT LONG The county SHOULD be: " + county);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = null;
        try {
            InputStream is = this.getResources().getAssets().open("elections.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("T", result);
        try {
            JSONObject jBody = new JSONObject(result);
            Log.d("T", "The county is: " + county);
            obama = jBody.getJSONObject(county).getInt("obama");
            romney = jBody.getJSONObject(county).getInt("romney");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getCountyFromZip(final String zip) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?address="+zip+"&region=us&key="+GOOGLE_SERVER_KEY);
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
                        JSONObject body = new JSONObject(result);
                        JSONArray addrComponents = body.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                        String county1 = addrComponents.getJSONObject(2).getString("long_name");
                        String state = addrComponents.getJSONObject(3).getString("short_name");
                        String key = county1 + ", " + state;
                        Log.d("T", key);
                        county = key;
                        Log.d("T", "The county SHOULD be: " + county);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = null;
        try {
            InputStream is = this.getResources().getAssets().open("elections.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("T", result);
        try {
            JSONObject jBody = new JSONObject(result);
            Log.d("T", "The county is: " + county);
            obama = jBody.getJSONObject(county).getInt("obama");
            romney = jBody.getJSONObject(county).getInt("romney");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
