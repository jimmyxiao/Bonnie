package com.sctw.bonniedraw.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.activity.SingleWorkActivity;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.WorkInfo;
import com.sctw.bonniedraw.adapter.WorkAdapterGrid;
import com.sctw.bonniedraw.adapter.WorkAdapterList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment {
    private TextView mTvMemberId, mTvMemberName, mTvMemberDescription, mTvMemberWorks, mTvMemberFans, mTvMemberFollows;
    private Button mBtnFollow;
    private CircleImageView mCircleImg;
    private ImageButton mBtnBack, mBtnGrid, mBtnList, mBtnAdd, mBtnExtra;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRv;
    private SharedPreferences prefs;
    private List<WorkInfo> workInfoList;
    private int miUserId;
    private WorkAdapterGrid mAdapterGrid;
    private WorkAdapterList mAdapterList;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager layoutManager;
    private boolean mbFist = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        miUserId=getArguments().getInt("userId");
        mTvMemberId = view.findViewById(R.id.textView_member_user_id);
        mTvMemberName = view.findViewById(R.id.textView_member_userName);
        mTvMemberDescription = view.findViewById(R.id.textView_member_user_description);
        mTvMemberWorks = view.findViewById(R.id.textView_member_userworks);
        mTvMemberFans = view.findViewById(R.id.textView_member_fans);
        mTvMemberFollows = view.findViewById(R.id.textView_member_follows);
        mBtnFollow = view.findViewById(R.id.btn_member_follow);
        mCircleImg = view.findViewById(R.id.circleImg_member_photo);
        mBtnBack = view.findViewById(R.id.imgBtn_member_back);
        mBtnGrid = view.findViewById(R.id.imgBtn_member_grid);
        mBtnList = view.findViewById(R.id.imgBtn_member_list);
        mBtnAdd = view.findViewById(R.id.imgBtn_member_add);
        mBtnExtra = view.findViewById(R.id.imgBtn_member_extra);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout_member);
        mRv = view.findViewById(R.id.recyclerview_member);
        setOnClickEvent();
        getMemberInfo();
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getWorksList();
    }

    private void getMemberInfo() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        JSONObject json = ConnectJson.queryOtherUserInfoJson(prefs, miUserId);
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        final Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_USER_INFO_QUERY)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject responseJSON = new JSONObject(responseStr);
                                if (responseJSON.getInt("res") == 1) {
                                    //Successful
                                    mTvMemberName.setText(responseJSON.getString("userName"));

                                    if (responseJSON.has("description") && !responseJSON.isNull("description")) {
                                        mTvMemberDescription.setText(responseJSON.getString("description"));
                                    } else {
                                        mTvMemberDescription.setText("");
                                    }

                                    if (responseJSON.has("nickName") && !responseJSON.isNull("nickName")) {
                                        mTvMemberId.setText(responseJSON.getString("nickName"));
                                    } else {
                                        mTvMemberId.setText("");
                                    }

                                    if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                        //URL profilePicUrl = new URL(responseJSON.getString("profilePicture"));
                                        ImageLoader.getInstance().displayImage(prefs.getString(GlobalVariable.userImgUrlStr, "null"), mCircleImg);
                                    } else {
                                        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.photo_round, mCircleImg);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void getWorksList() {
        JSONObject json = ConnectJson.queryListWork(prefs, 6, 0, 100);
        Log.d("LOGIN JSON: ", json.toString());
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_LIST)
                .post(body)
                .build();
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
                                        refreshWorks(responseJSON.getJSONArray("workList"));
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
    public void refreshWorks(JSONArray data) {
        workInfoList = WorkInfo.generateInfoList(data);

        mAdapterGrid = new WorkAdapterGrid(workInfoList, new WorkAdapterGrid.WorkGridOnClickListener() {
            @Override
            public void onWorkClick(int wid) {
                Log.d("POSTION CLICK", "POSTION=" + String.valueOf(wid));
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("wid", wid);
                intent.setClass(getActivity(), SingleWorkActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mAdapterList = new WorkAdapterList(workInfoList, new WorkAdapterList.WorkListOnClickListener() {
            @Override
            public void onWorkImgClick(int wid) {
                Log.d("POSTION CLICK", "POSTION=" + String.valueOf(wid));
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("wid", wid);
                intent.setClass(getActivity(), SingleWorkActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onWorkExtraClick(final int wid) {

            }

            @Override
            public void onWorkGoodClick(int wid) {

            }

            @Override
            public void onWorkMsgClick(int wid) {

            }

            @Override
            public void onWorkShareClick(int wid) {

            }

            @Override
            public void onUserClick(int wid) {

            }
        });

        if (mbFist) {
            mRv.setLayoutManager(gridLayoutManager);
            mRv.setAdapter(mAdapterGrid);
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            mRv.setLayoutManager(layoutManager);
            mRv.setAdapter(mAdapterList);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setOnClickEvent() {
        mBtnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
