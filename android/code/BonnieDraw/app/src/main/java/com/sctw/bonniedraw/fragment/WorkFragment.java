package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.TSnackbarCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
public class WorkFragment extends Fragment {
    private TextView mTextViewUserName, mTextViewWorkDescription, mTextViewWorkName, mTextViewGoodTotal, mTextViewCreateTime, mTextViewClass;
    private ImageView imgViewUserPhoto, mImgViewWorkImage;
    private ImageButton worksUserExtra, worksUserGood, worksUserMsg, worksUserShare, worksUserFollow;
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
        mTextViewUserName = view.findViewById(R.id.textView_work_username);
        mTextViewWorkName = view.findViewById(R.id.textView_work_title);
        mTextViewWorkDescription = view.findViewById(R.id.textView_work_description);
        mTextViewGoodTotal = view.findViewById(R.id.textView_work_good_total);
        mTextViewCreateTime = view.findViewById(R.id.textView_work_create_time);
        mTextViewClass = view.findViewById(R.id.textView_work_user_class);
        imgViewUserPhoto = view.findViewById(R.id.imgView_work_user_photo);
        mImgViewWorkImage = view.findViewById(R.id.imgView_work_img);
        worksUserExtra = view.findViewById(R.id.imgBtn_work_extra);
        worksUserGood = view.findViewById(R.id.imgBtn_work_good);
        worksUserMsg = view.findViewById(R.id.imgBtn_work_msg);
        worksUserShare = view.findViewById(R.id.imgBtn_work_share);
        worksUserFollow = view.findViewById(R.id.imgBtn_work_follow);
        getSingleWork();
    }

    public void getSingleWork() {
        JSONObject json = ConnectJson.querySingleWork(prefs, wid);
        Log.d("LOGIN JSON: ", json.toString());

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_LIST)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                TSnackbarCall.showTSnackbar(getView().findViewById(R.id.coordinatorLayout_work), "Fail Load");
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
                                        getWork(responseJSON.getJSONObject("work"));
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

    public void getWork(JSONObject data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN);
            Date date = new Date(Long.valueOf(data.getString("updateDate")));
            mTextViewUserName.setText(data.getString("userName"));
            mTextViewWorkName.setText(data.getString("title"));
            mTextViewWorkDescription.setText(data.getString("description"));
            mTextViewGoodTotal.setText(String.format(getString(R.string.work_good_total), data.getString("isFollowing")));
            mTextViewCreateTime.setText(String.format(getString(R.string.work_release_time), sdf.format(date)));
            //imgViewUserPhoto data.getString("imagePath")
            try {
                URL url = new URL(GlobalVariable.API_LINK_GET_PHOTO +data.getString("imagePath"));
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                mImgViewWorkImage.setImageBitmap(bitmap);
                mImgViewWorkImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
