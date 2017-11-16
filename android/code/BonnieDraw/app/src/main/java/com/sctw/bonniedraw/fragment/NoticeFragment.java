package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.NoticeAdapter;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.NoticeInfoBean;
import com.sctw.bonniedraw.utility.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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
public class NoticeFragment extends Fragment {
    SharedPreferences prefs;
    RecyclerView mRv;
    NoticeAdapter mAdapter;
    ArrayList<NoticeInfoBean> noticeInfoList;
    SwipeRefreshLayout mSwipeLayout;
    FrameLayout mFrameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notice, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mRv = view.findViewById(R.id.recyclerView_notice);
        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(lm);
        noticeInfoList = new ArrayList<>();
        mSwipeLayout = view.findViewById(R.id.swipeLayout_notice);
        mFrameLayout=view.findViewById(R.id.frameLayout_notice);
        getNoticeList();
    }

    private void getNoticeList() {
        // 0 取全部訊息
        JSONObject json = ConnectJson.getNotice(prefs, 0);
        Log.d("LOGIN JSON: ", json.toString());
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_NOTICE_MESSAGE)
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
                                        mFrameLayout.setVisibility(View.GONE);
                                        refreshNoice(responseJSON.getJSONArray("notiMsgList"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mSwipeLayout.setRefreshing(false);
                                    System.out.println(responseJSON.toString());
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

    private void refreshNoice(JSONArray data) {
        try {
            for (int x = 0; x < data.length(); x++) {
                NoticeInfoBean bean = new NoticeInfoBean();
                bean.setNotMsgId(data.getJSONObject(x).getInt("notiMsgId"));
                bean.setMsg(data.getJSONObject(x).getString("message"));
                bean.setNotiMsgType(data.getJSONObject(x).getInt("notiMsgType"));
                bean.setUserIdFollow(data.getJSONObject(x).getInt("userIdFollow"));
                bean.setUserNameFollow(data.getJSONObject(x).getString("userNameFollow"));
                bean.setProfilePicture(data.getJSONObject(x).getString("profilePicture"));
                bean.setWorkId(data.getJSONObject(x).getInt("worksId"));
                bean.setWorkMsg(data.getJSONObject(x).getString("worksMsg"));
                bean.setTitle(data.getJSONObject(x).getString("title"));
                bean.setImagePath(data.getJSONObject(x).getString("imagePath"));
                bean.setWorkMsgId(data.getJSONObject(x).getString("worksMsgId"));
                bean.setCreationDate(data.getJSONObject(x).getString("creationDate"));
                noticeInfoList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new NoticeAdapter(getContext(),noticeInfoList);
        mRv.setAdapter(mAdapter);
    }
}
