package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.FriendsAdapter;
import com.sctw.bonniedraw.bean.FriendBean;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.SimpleItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements FriendsAdapter.OnFriendClick, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ImageButton mImgBtnBack;
    private RecyclerView mRv;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences prefs;
    private FriendsAdapter mAdapter;
    private ArrayList<FriendBean> mList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvHint;
    private Toolbar mToolbar;
    private android.support.v7.widget.SearchView mSearchView;
    private ProgressBar mProgressBar;
    private int miType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
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
        mTvHint = (TextView) view.findViewById(R.id.textView_friends_hint);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_friends);
        mProgressBar=view.findViewById(R.id.progressBar_friends);
        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipeLayout_friend);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        miType = prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0);
        getFriendsListThird(miType);
        setOnClick();
        mRv.addItemDecoration(new SimpleItemDecoration(getContext()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFriendsListThird(miType);
            }
        });
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
                mTvHint.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case GlobalVariable.THIRD_LOGIN_TWITTER:
                break;
            default:
                //沒有社群帳號連結
                mTvHint.setText(getText(R.string.only_fb_or_twitter_friends_list));
                mTvHint.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (android.support.v7.widget.SearchView) item.getActionView();
        mSearchView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_searchview_bg));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint(getString(R.string.find_someone_user));
        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
    }

    private void getFacebookFriends() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        transformFacebook(response);
                    }
                }
        ).executeAsync();
    }

    private void getGoogleFriends() {
    }

    private void getTwitterFriends() {

    }

    private void transformFacebook(final GraphResponse response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray responeArray = response.getJSONObject().getJSONArray("data");
                    if (responeArray.length() > 0) {
                        JSONArray friendsArray = new JSONArray();
                        for (int x = 0; x < responeArray.length(); x++) {
                            List<String> friendObj = new ArrayList<>();
                            friendsArray.put(responeArray.getJSONObject(x).getString("id"));
                        }
                        getSuggestFriend(friendsArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getSuggestFriend(JSONArray friendsArray) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryFriends(prefs, friendsArray);
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
                                    mProgressBar.setVisibility(View.GONE);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    try {
                                        refresh(responseJSON.getJSONArray("friendList"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refresh(JSONArray data) {
        mList = new ArrayList<>();
        if(data.length()==0) {
            mTvHint.setVisibility(View.VISIBLE);
        }else {
            mTvHint.setVisibility(View.GONE);
        }
        try {
            for (int x = 0; x < data.length(); x++) {
                FriendBean bean = new FriendBean(
                        data.getJSONObject(x).getString("userName"),
                        data.getJSONObject(x).getInt("userId"),
                        data.getJSONObject(x).getString("profilePicture")
                );
                mList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new FriendsAdapter(getContext(), mList, this);
        mRv.setAdapter(mAdapter);
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

    @Override
    public void onClickFollow(final int position, int uid) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.setFollow(prefs, 1, uid);
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
                                    mAdapter.setFollow(position,true);
                                } else {
                                    //點讚失敗或刪除失敗
                                    mAdapter.setFollow(position,false);
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
    public void onnClickUser(int uid) {
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
}
