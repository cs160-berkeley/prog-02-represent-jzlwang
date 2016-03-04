package judy.represent;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    private Button currentLocButton;
    private Button goButton;
    private EditText zipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        currentLocButton = (Button) findViewById(R.id.curr_loc_btn);
        currentLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCongress = new Intent(getApplicationContext(), CongressionalActivity.class);
                startActivity(toCongress);

                Intent sendLocIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendLocIntent.putExtra("ZIP", "None");
                sendLocIntent.putExtra("LOC", "CurrLoc");
                startService(sendLocIntent);
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
                toCongress2.putExtra("EXTRA_REP", 1);
                startActivity(toCongress2);

                // render main activity on wear
                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("ZIP", zip);
                sendIntent.putExtra("LOC", "None");
                startService(sendIntent);
            }
        });
    }
}
