package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.WorkAdapterGrid;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
public class CollectionFragment extends DialogFragment implements WorkAdapterGrid.WorkGridOnClickListener {
    private ImageButton mImgBtnBack;
    private SharedPreferences prefs;
    private ProgressBar mProgressBar;
    private RecyclerView mRv;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvHInt;
    private GridLayoutManager mGridLayoutManager;
    private WorkAdapterGrid mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mImgBtnBack = (ImageButton) view.findViewById(R.id.imgBtn_collection_back);
        mRv = view.findViewById(R.id.recyclerView_collection);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout_collection);
        mProgressBar = view.findViewById(R.id.progressBar_collection);
        mTvHInt = view.findViewById(R.id.textView_collection);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWorksList();
            }
        });
        setOnClick();
        getWorksList();
    }

    private void setOnClick() {
        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionFragment.this.dismiss();
            }
        });
    }

    private void showWork(int wid) {
        Bundle bundle = new Bundle();
        bundle.putInt("wid", wid);
        PlayFragment playFragment = new PlayFragment();
        playFragment.setArguments(bundle);
        playFragment.show(getFragmentManager(), "TAG");
    }

    public void getWorksList() {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryListWork(prefs, 7, 1, 1999);
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
                                        getWorks(responseJSON.getJSONArray("workList"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mSwipeRefreshLayout.setRefreshing(false);
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

    public void getWorks(JSONArray data) {
        List<WorkInfoBean> workInfoBeanList = WorkInfoBean.generateInfoList(data);
        if (workInfoBeanList.size() == 0) mTvHInt.setVisibility(View.VISIBLE);
        mAdapter = new WorkAdapterGrid(getContext(), workInfoBeanList, this);
        mProgressBar.setVisibility(View.GONE);
        mRv.setLayoutManager(mGridLayoutManager);
        mRv.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onGridWorkClick(int wid) {
        showWork(wid);
    }
}
