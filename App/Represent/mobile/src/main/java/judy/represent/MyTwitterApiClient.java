package judy.represent;

import android.util.Log;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Judy on 3/10/2016.
 */
public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public UsersService getUsersService() {
        return getService(UsersService.class);
    }
}

interface UsersService {
    @GET("/1.1/users/show.json")
    void show(@Query("user_id") Log userId,
              @Query("screen_name") String screenName,
              @Query("include_entities") Boolean includeEntities,
              Callback<User> cb);
}
