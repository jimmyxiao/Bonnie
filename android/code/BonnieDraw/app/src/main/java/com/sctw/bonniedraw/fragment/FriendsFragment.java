package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.FansOfFollowAdapter;
import com.sctw.bonniedraw.bean.FansOfFollowBean;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.SimpleItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements FansOfFollowAdapter.OnFansOfFollowClick, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ImageButton mImgBtnBack;
    private RecyclerView mRv;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences prefs;
    private FansOfFollowAdapter mAdapter;
    private ArrayList<FansOfFollowBean> mList;
    private JSONArray mFriendsArray;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvHint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mImgBtnBack = view.findViewById(R.id.imgBtn_friend_back);
        mRv = view.findViewById(R.id.recyclerView_friend);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(linearLayoutManager);
        mTvHint=(TextView) view.findViewById(R.id.textView_friends_hint);

        int type = prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0);
        getFriendsListThird(type);
        setOnClick();
        mRv.addItemDecoration(new SimpleItemDecoration(getContext()));
    }

    private void setOnClick() {
        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void getFriendsListThird(int type) {

        switch (type) {
            case GlobalVariable.THIRD_LOGIN_FACEBOOK:
                //FB的好友列表
                getFacebookFriends();
                break;
            case GlobalVariable.THIRD_LOGIN_GOOGLE:
                //Google 連結
                mTvHint.setText(getText(R.string.only_fb_or_twitter_friends_list));
                break;
            case GlobalVariable.THIRD_LOGIN_TWITTER:
                break;
            default:
                //沒有社群帳號連結
                mTvHint.setText(getText(R.string.only_fb_or_twitter_friends_list));
                break;
        }
    }

    private void getFacebookFriends() {
        String userId = prefs.getString(GlobalVariable.USER_THIRD_ID_STR, "");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userId + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        System.out.println("RES:" + response.toString());
                    }
                }
        ).executeAsync();
    }

    private void getGoogleFriends() {
    }

    private void getTwitterFriends() {

    }

    private void getSuggestFriend() {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryFriends(prefs, mFriendsArray);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Get List Works", "Fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    if (responseJSON.getInt("res") == 1) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //下載資料
                                    try {
                                        refresh(responseJSON.getJSONArray("userList"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    System.out.println(responseJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refresh(JSONArray data) {
        mList = new ArrayList<>();
        try {
            for (int x = 0; x < data.length(); x++) {
                FansOfFollowBean bean = new FansOfFollowBean();
                bean.setUserId(data.getJSONObject(x).getInt("userId"));
                bean.setUserName(data.getJSONObject(x).getString("userName"));
                bean.setProfilePicture(data.getJSONObject(x).getString("profilePicture"));
                bean.setFollowing(data.getJSONObject(x).getBoolean("following"));
                mList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new FansOfFollowAdapter(getContext(), mList, this);
        mRv.setAdapter(mAdapter);
    }

    @Override
    public void onFansOfFollowOnClickFollow(final int position, final int fn, int uid) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.setFollow(prefs, fn, uid);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Get List Works", "Fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (responseJSON.getInt("res") == 1) {
                                    //點成功或失敗
                                    switch (fn) {
                                        case 0:
                                            mAdapter.setFollow(position, false);
                                            break;
                                        case 1:
                                            mAdapter.setFollow(position, true);
                                            break;
                                    }
                                    mAdapter.notifyItemChanged(position);
                                    System.out.println(responseJSON.toString());
                                } else {
                                    //點讚失敗或刪除失敗
                                    mAdapter.notifyItemChanged(position);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onFansOfFollowOnClickUser(int uid) {
        MemberFragment memberFragment = new MemberFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("userId", uid);
        memberFragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout_actitivy, memberFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
