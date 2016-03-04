package judy.represent;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class CongressionalActivity extends Activity {

    private Button backButton;
    private LinearLayout extraRep;
    private Button infoButton1;
    private Button infoButton2;
    private Button infoButton3;
    private Button infoButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        extraRep = (LinearLayout) findViewById(R.id.extra_rep);

        Intent fromMain = getIntent();
        int fromZIP = fromMain.getIntExtra("EXTRA_REP", 0);

        if (fromZIP == 1) {
            extraRep.setVisibility(View.VISIBLE);
        } else {
            extraRep.setVisibility(View.GONE);
        }

        backButton = (Button) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(toMain);
            }
        });

        infoButton1 = (Button) findViewById(R.id.info_btn1);
        infoButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetailed1 = new Intent(getApplicationContext(), DetailedActivity.class);
                startActivity(toDetailed1);
            }
        });

        infoButton2 = (Button) findViewById(R.id.info_btn2);
        infoButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetailed2 = new Intent(getApplicationContext(), DetailedActivity.class);
                startActivity(toDetailed2);
            }
        });

        infoButton3 = (Button) findViewById(R.id.info_btn3);
        infoButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetailed3 = new Intent(getApplicationContext(), DetailedActivity.class);
                startActivity(toDetailed3);
            }
        });

        infoButton4 = (Button) findViewById(R.id.info_btn4);
        infoButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetailed4 = new Intent(getApplicationContext(), DetailedActivity.class);
                startActivity(toDetailed4);
            }
        });
    }
}
