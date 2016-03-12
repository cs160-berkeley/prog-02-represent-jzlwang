package judy.represent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Random;

public class VoteActivity extends Activity {

    private LinearLayout voteFace;
    private TextView democratVote;
    private TextView republicanVote;
    private MainTextView county;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        democratVote = (TextView) findViewById(R.id.democrat_vote);
        republicanVote = (TextView) findViewById(R.id.republican_vote);
        county = (MainTextView) findViewById(R.id.county);

//        Random randomno = new Random();
//        float randomPercent = (float) randomno.nextInt(100);
//        randomPercent += randomno.nextFloat();
//
//        String demoVote = String.format("%.1f", randomPercent);
//        String repubVote = String.format("%.1f", 100.0f - randomPercent);

        Intent fromMain = getIntent();
        Bundle extras = fromMain.getExtras();
        String demoVote = extras.getString("OBAMA");
        String repubVote = extras.getString("ROMNEY");
        String countyString = extras.getString("COUNTY");
        final String infoString = extras.getString("INFOSTRING");

        democratVote.setText("Obama\n" + demoVote + "%");
        republicanVote.setText("Romney\n" + repubVote + "%");
        county.setText(countyString);

        voteFace = (LinearLayout) findViewById(R.id.voteface);

        voteFace.setOnTouchListener(new OnSwipeTouchListener(VoteActivity.this) {
            public void onSwipeTop() {
                Intent toMain = new Intent(getBaseContext(), MainActivity.class);
                toMain.putExtra("INFOSTRING", infoString);
                startActivity(toMain);
            }
        });
    }
}
