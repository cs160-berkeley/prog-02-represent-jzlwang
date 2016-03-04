package judy.represent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class VoteActivity extends Activity {

    private LinearLayout voteFace;
    private TextView democratVote;
    private TextView republicanVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        democratVote = (TextView) findViewById(R.id.democrat_vote);
        republicanVote = (TextView) findViewById(R.id.republican_vote);

        Random randomno = new Random();
        float randomPercent = (float) randomno.nextInt(100);
        randomPercent += randomno.nextFloat();

        String demoVote = String.format("%.1f", randomPercent);
        String repubVote = String.format("%.1f", 100.0f - randomPercent);

        democratVote.setText("Obama\n" + demoVote + "%");
        republicanVote.setText("Romney\n" + repubVote + "%");

        voteFace = (LinearLayout) findViewById(R.id.voteface);

        voteFace.setOnTouchListener(new OnSwipeTouchListener(VoteActivity.this) {
            public void onSwipeTop() {
                Intent toMain = new Intent(getBaseContext(), MainActivity.class);
                startActivity(toMain);
            }
        });
    }
}
