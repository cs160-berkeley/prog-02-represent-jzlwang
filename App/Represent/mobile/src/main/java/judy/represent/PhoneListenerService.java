package judy.represent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String DETAILS = "/get_details";
    private static final String RANDLOC = "/random_location";
    private static final String INFOSTRING = "/INFOSTRING";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(INFOSTRING) ) {

            // Value contains the String we sent over in WatchToPhoneService, "Test value received!"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent intent = new Intent(getBaseContext(), DetailedActivity.class );
            ArrayList<String> mExtra = new ArrayList<String>(Arrays.asList(value.split("jooby")));
            Log.d("POOP", mExtra.get(0) + ", " + mExtra.get(1) + ", " + mExtra.get(2) + ", " + mExtra.get(3) + ", " + mExtra.get(4));
            intent.putExtra("REP_NUM", Integer.parseInt(mExtra.get(0)));
            intent.putExtra("LONG", mExtra.get(1));
            intent.putExtra("LAT", mExtra.get(2));
            intent.putExtra("ZIP", mExtra.get(3));
            intent.putExtra("JSON", mExtra.get(4));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}