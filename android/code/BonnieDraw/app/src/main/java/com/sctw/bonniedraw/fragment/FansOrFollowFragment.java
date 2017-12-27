package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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
public class FansOrFollowFragment extends Fragment implements FansOfFollowAdapter.OnFansOfFollowClick {
    private ImageButton mImgBtnBack;
    private TextView mTvTitle;
    private RecyclerView mRv;
    private LinearLayoutManager linearLayoutManager;
    private int miFn, miUserId;
    private SharedPreferences prefs;
    private FansOfFollowAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<FansOfFollowBean> mList;
    private Toolbar mToolbar;
    private android.support.v7.widget.SearchView mSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_fans_or_follow, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        miUserId = getArguments().getInt("uid");
        miFn = getArguments().getInt("fn");
        mImgBtnBack = view.findViewById(R.id.imgBtn_fof_back);
        mTvTitle = view.findViewById(R.id.textView_fof_title);
        mRv = view.findViewById(R.id.recyclerView_fof);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout_fof);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(linearLayoutManager);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_fof);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        //miFn  2=fans, 1= follow
        if (miFn == 2) {
            mTvTitle.setText(R.string.uc_fans);
        } else {
            mTvTitle.setText(R.string.uc_following);
        }
        setOnClick();
        getFansOrFollow();
        mRv.addItemDecoration(new SimpleItemDecoration(getContext()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFansOrFollow();
            }
        });
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
                mAdapter.getFilter().filter(s);
                return false;
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

    public void getFansOrFollow() {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryFansOrFollow(prefs, miUserId, miFn);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
                                    mSwipeRefreshLayout.setRefreshing(false);
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
}
