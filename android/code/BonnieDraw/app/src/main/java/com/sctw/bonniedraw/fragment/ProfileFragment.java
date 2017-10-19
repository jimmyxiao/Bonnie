package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.WorkInfo;
import com.sctw.bonniedraw.works.WorkAdapterGrid;
import com.sctw.bonniedraw.works.WorkAdapterList;
import com.sctw.bonniedraw.works.WorkGridOnClickListener;
import com.sctw.bonniedraw.works.WorkListOnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private ImageView imgPhoto;
    private TextView mTextViewUserName, mTextViewUserId, mTextViewWorks, mTextViewFans, mTextViewFollows;
    private ImageButton mImgBtnSetting, mImgBtnGrid, mImgBtnList;
    Button mBtnEdit;
    RecyclerView mRecyclerViewProfile;
    ArrayList<WorkInfo> myDataset;
    List<String> myDatasetStr;
    WorkAdapterGrid mAdapterGrid;
    WorkAdapterList mAdapterList;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager layoutManager;
    SharedPreferences prefs;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        imgPhoto = (ImageView) view.findViewById(R.id.circleImg_profile_photo);
        mTextViewUserName = (TextView) view.findViewById(R.id.textView_profile_userName);
        mTextViewUserId = (TextView) view.findViewById(R.id.textView_profile_user_id);
        mTextViewWorks = (TextView) view.findViewById(R.id.textView_profile_userworks);
        mTextViewFollows = (TextView) view.findViewById(R.id.textView_profile_follows);
        mTextViewFans = (TextView) view.findViewById(R.id.textView_profile_fans);
        mImgBtnSetting = (ImageButton) view.findViewById(R.id.imgBtn_profile_setting);
        mImgBtnGrid = (ImageButton) view.findViewById(R.id.imgBtn_profile_grid);
        mImgBtnList = (ImageButton) view.findViewById(R.id.imgBtn_profile_list);
        mBtnEdit = view.findViewById(R.id.btn_edit_profile);
        updateProfileInfo();
        fragmentManager = getFragmentManager();


        mImgBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                ProfileSettingFragment fragment = new ProfileSettingFragment();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                EditProfileFragment fragment = new EditProfileFragment();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        myDataset = new ArrayList<WorkInfo>();
        myDatasetStr = new ArrayList<>();
        mRecyclerViewProfile = (RecyclerView) view.findViewById(R.id.recyclerview_profile);
        mAdapterGrid = new WorkAdapterGrid(myDatasetStr, new WorkGridOnClickListener() {
            @Override
            public void onWorkClick(int postion) {
                Log.d("POSTION CLICK", "No." + String.valueOf(postion));
            }
        });
        mRecyclerViewProfile.setLayoutManager(gridLayoutManager);
        mRecyclerViewProfile.setAdapter(mAdapterGrid);

        mImgBtnGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterGrid = new WorkAdapterGrid(myDatasetStr, new WorkGridOnClickListener() {
                    @Override
                    public void onWorkClick(int postion) {
                        Log.d("POSTION CLICK", "No." + String.valueOf(postion));
                    }
                });
                mRecyclerViewProfile.setLayoutManager(gridLayoutManager);
                mRecyclerViewProfile.setAdapter(mAdapterGrid);
            }
        });

        mImgBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterList = new WorkAdapterList(myDataset, new WorkListOnClickListener() {
                    @Override
                    public void onWorkImgClick(int wid) {
                        Log.d("onWorkImgClick", "No." + String.valueOf(wid));
                    }

                    @Override
                    public void onWorkExtraClick(int wid) {
                        Log.d("onWorkExtraClick", "No." + String.valueOf(wid));
                    }

                    @Override
                    public void onWorkGoodClick(int wid) {
                        Log.d("onWorkGoodClick", "No." + String.valueOf(wid));
                    }

                    @Override
                    public void onWorkMsgClick(int wid) {
                        Log.d("onWorkMsgClick", "No." + String.valueOf(wid));
                    }

                    @Override
                    public void onWorkShareClick(int wid) {
                        Log.d("onWorkShareClick", "No." + String.valueOf(wid));
                    }
                });
                mRecyclerViewProfile.setLayoutManager(layoutManager);
                mRecyclerViewProfile.setAdapter(mAdapterList);
            }
        });
    }

    void updateProfileInfo() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, json.toString());
        final Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_USER_INFO_QUERY)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "連線錯誤", Toast.LENGTH_SHORT).show();
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
                                    mTextViewUserName.setText(responseJSON.getString("userName"));

                                    if (responseJSON.has("nickName") && !responseJSON.isNull("nickName")) {
                                        mTextViewUserId.setText(responseJSON.getString("nickName"));
                                    } else {
                                        mTextViewUserId.setText("");
                                    }

                                    if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                        try {
                                            //URL profilePicUrl = new URL(responseJSON.getString("profilePicture"));
                                            URL profilePicUrl = new URL(prefs.getString(GlobalVariable.userImgUrlStr, "null"));
                                            Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                                            imgPhoto.setImageBitmap(bitmap);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "連線失敗", Toast.LENGTH_SHORT).show();
                                }
                                Log.d("RESTFUL API : ", responseJSON.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
