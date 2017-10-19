package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.WorkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkFragment extends Fragment {
    TextView mTextViewName, mTextViewGoodTotal, mTextViewCreateTime, mTextViewClass;
    ImageView imgViewUserPhoto, mImgViewWorkImage;
    ImageButton worksUserExtra, worksUserGood, worksUserMsg, worksUserShare, worksUserFollow;
    SharedPreferences prefs;
    int wid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            wid = bundle.getInt("wid");
        }
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mTextViewName = view.findViewById(R.id.textView_works_user_name);
        mTextViewGoodTotal = view.findViewById(R.id.textView_works_good_total);
        mTextViewCreateTime = view.findViewById(R.id.textView_works_create_time);
        mTextViewClass = view.findViewById(R.id.textView_works_user_class);
        imgViewUserPhoto = view.findViewById(R.id.imgView_works_user_photo);
        mImgViewWorkImage = view.findViewById(R.id.imgView_works_work_img);
        worksUserExtra = view.findViewById(R.id.imgBtn_works_extra);
        worksUserGood = view.findViewById(R.id.imgBtn_works_good);
        worksUserMsg = view.findViewById(R.id.imgBtn_works_msg);
        worksUserShare = view.findViewById(R.id.imgBtn_works_share);
        worksUserFollow = view.findViewById(R.id.imgBtn_works_follow);
        getWorksList();
    }

    public void getWorksList() {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("wid",wid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGIN JSON: ", json.toString());

        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = FormBody.create(mediaType, json.toString());
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
                                        System.out.println(responseJSON.toString());
                                        getWork(responseJSON.getJSONArray("workList"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(getActivity(), "Download work successful", Toast.LENGTH_SHORT).show();
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

    public void getWork(JSONArray data) {
        try {
            WorkInfo workInfo = new WorkInfo();
            workInfo.setWorkId(data.getJSONObject(0).getString("worksId"));
            workInfo.setUserId(data.getJSONObject(0).getString("userId"));
            workInfo.setUserName(data.getJSONObject(0).getString("userName"));
            workInfo.setTitle(data.getJSONObject(0).getString("title"));
            workInfo.setImagePath(data.getJSONObject(0).getString("imagePath"));
            workInfo.setIsFollowing(data.getJSONObject(0).getString("isFollowing"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
