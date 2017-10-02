package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.sctw.bonniedraw.works.WorkAdapterList;
import com.sctw.bonniedraw.works.WorkAdapterGrid;
import com.sctw.bonniedraw.works.WorkGridOnClickListener;
import com.sctw.bonniedraw.works.WorkListOnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

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
    private ImageView profilePhoto;
    private TextView profileUserName, profileUserId, profileWorks, profileUserFans, profileUserFollow;
    private ImageButton profileSettingBtn, profileGridBtn, profileListBtn;
    Button profileEditBtn;
    RecyclerView profileRecyclerView;
    ArrayList<String> myDataset;
    WorkAdapterGrid mAdapterGrid;
    WorkAdapterList mAdapterList;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager layoutManager;
    SharedPreferences prefs;
    FragmentManager fm;
    FragmentTransaction ft;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        profilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
        profileUserName = (TextView) view.findViewById(R.id.profile_userName);
        profileUserId = (TextView) view.findViewById(R.id.profile_userId);
        profileWorks = (TextView) view.findViewById(R.id.profile_userWorks);
        profileUserFollow = (TextView) view.findViewById(R.id.profile_userFans);
        profileUserFans = (TextView) view.findViewById(R.id.profile_userFollow);
        profileSettingBtn = (ImageButton) view.findViewById(R.id.profile_setting_btn);
        profileGridBtn = (ImageButton) view.findViewById(R.id.profile_grid_btn);
        profileListBtn = (ImageButton) view.findViewById(R.id.profile_list_btn);
        profileEditBtn = view.findViewById(R.id.btn_edit_profile);
        updateProfileInfo();
        fm = getFragmentManager();


        profileSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
            }
        });

        profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = fm.beginTransaction();
                ft.replace(R.id.main_actitivy_layout, new EditProfileFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        myDataset = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            myDataset.add(Integer.toString(i));
        }
        profileRecyclerView = (RecyclerView) view.findViewById(R.id.profile_recyclerview);
        mAdapterGrid = new WorkAdapterGrid(myDataset, new WorkGridOnClickListener() {
            @Override
            public void onWorkClick(int postion) {
                Log.d("POSTION CLICK", "No." + String.valueOf(postion));
            }
        });
        profileRecyclerView.setLayoutManager(gridLayoutManager);
        profileRecyclerView.setAdapter(mAdapterGrid);

        profileGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterGrid = new WorkAdapterGrid(myDataset, new WorkGridOnClickListener() {
                    @Override
                    public void onWorkClick(int postion) {
                        Log.d("POSTION CLICK", "No." + String.valueOf(postion));
                    }
                });
                profileRecyclerView.setLayoutManager(gridLayoutManager);
                profileRecyclerView.setAdapter(mAdapterGrid);
            }
        });

        profileListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterList = new WorkAdapterList(myDataset, new WorkListOnClickListener() {
                    @Override
                    public void onWorkClick(int postion) {
                        Log.d("POSTION CLICK","No."+String.valueOf(postion));
                    }

                    @Override
                    public void onWorkExtraClick(int postion) {

                    }
                });
                profileRecyclerView.setLayoutManager(layoutManager);
                profileRecyclerView.setAdapter(mAdapterList);
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
                .url("https://www.bonniedraw.com/bonniedraw_service/BDService/userInfoQuery")
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

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseStr);
                            if (responseJSON.getInt("res") == 1) {
                                //Successful
                                profileUserName.setText(responseJSON.getString("userName"));

                                if (responseJSON.has("nickName") && !responseJSON.isNull("nickName")) {
                                    profileUserId.setText(responseJSON.getString("nickName"));
                                } else {
                                    profileUserId.setText("");
                                }

                                if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                    try {
                                        //URL profilePicUrl = new URL(responseJSON.getString("profilePicture"));
                                        URL profilePicUrl = new URL(prefs.getString(GlobalVariable.userImgUrlStr, "null"));
                                        Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                                        profilePhoto.setImageBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    profilePhoto.setBackgroundColor(Color.BLACK);
                                }

                                Log.d("RESTFUL API : ", responseJSON.toString());
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
        });
    }

}
