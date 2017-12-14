package com.sctw.bonniedraw.utility;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.Arrays;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Fatorin on 2017/12/14.
 */

public class FriendTwitterApiClient extends TwitterApiClient {
    public FriendTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public FriendsService  getFriendsService() {
        return getService(FriendsService.class);
    }

    public interface FriendsService {
        @GET("/1.1/friends/ids.json")
        retrofit2.Call<Ids> getFriendIds(@Query("user_id") Long userId,
                                    @Query("screen_name") String screenName,
                                    @Query("cursor") Long cursor,
                                    @Query("stringify_ids") Boolean stringifyIds,
                                    @Query("count") Integer count,
                                    Callback<Ids> cb);

        @GET("/1.1/friends/ids.json")
        retrofit2.Call<Ids> idsByUserId(@Query("user_id") Long userId);
    }

    public class Ids {
        @SerializedName("previous_cursor")
        public final Long previousCursor;

        @SerializedName("ids")
        public final Long[] ids;

        @SerializedName("next_cursor")
        public final Long nextCursor;


        public Ids(Long previousCursor, Long[] ids, Long nextCursor) {
            this.previousCursor = previousCursor;
            this.ids = ids;
            this.nextCursor = nextCursor;
        }

        @Override
        public String toString() {
            return "Ids{" +
                    "previousCursor=" + previousCursor +
                    ", ids=" + Arrays.toString(ids) +
                    ", nextCursor=" + nextCursor +
                    '}';
        }
    }
}
