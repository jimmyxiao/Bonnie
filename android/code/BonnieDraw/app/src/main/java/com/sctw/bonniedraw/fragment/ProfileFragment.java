package com.sctw.bonniedraw.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
public class ProfileFragment extends Fragment {
    private CircleImageView imgPhoto;
    private TextView mTextViewUserName, mTextViewUserId, mTextViewUserdescription, mTextViewWorks, mTextViewFans, mTextViewFollows;
    private ImageButton mImgBtnSetting, mImgBtnGrid, mImgBtnList;
    private Button mBtnEdit;
    private SwipeRefreshLayout mSwipeLayoutProfile;
    private RecyclerView mRecyclerViewProfile;
    private WorkAdapterGrid mAdapterGrid;
    private WorkAdapterList mAdapterList;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager layoutManager;
    private SharedPreferences prefs;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private List<WorkInfo> workInfoList;
    private boolean mbFist = true;

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
        imgPhoto = (CircleImageView) view.findViewById(R.id.circleImg_profile_photo);
        mSwipeLayoutProfile = view.findViewById(R.id.swipeLayout_profile);
        mTextViewUserName = (TextView) view.findViewById(R.id.textView_profile_userName);
        mTextViewUserdescription = view.findViewById(R.id.textView_profile_user_description);
        mTextViewUserId = (TextView) view.findViewById(R.id.textView_profile_user_id);
        mTextViewWorks = (TextView) view.findViewById(R.id.textView_profile_userworks);
        mTextViewFollows = (TextView) view.findViewById(R.id.textView_profile_follows);
        mTextViewFans = (TextView) view.findViewById(R.id.textView_profile_fans);
        mImgBtnSetting = (ImageButton) view.findViewById(R.id.imgBtn_profile_setting);
        mImgBtnGrid = (ImageButton) view.findViewById(R.id.imgBtn_profile_grid);
        mImgBtnList = (ImageButton) view.findViewById(R.id.imgBtn_profile_list);
        mBtnEdit = view.findViewById(R.id.btn_profile_edit);
        mRecyclerViewProfile = (RecyclerView) view.findViewById(R.id.recyclerview_profile);
        updateProfileInfo();
        setOnClick();
        fragmentManager = getFragmentManager();
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        getWorksList();
    }

    private void setOnClick() {
        mImgBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, new ProfileSettingFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, new EditProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mImgBtnGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerViewProfile.setLayoutManager(gridLayoutManager);
                mRecyclerViewProfile.setAdapter(mAdapterGrid);
                mbFist = true;
            }
        });

        mImgBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerViewProfile.setLayoutManager(layoutManager);
                mRecyclerViewProfile.setAdapter(mAdapterList);
                mbFist = false;
            }
        });

        mSwipeLayoutProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //nothing
            }
        });
    }

    void updateProfileInfo() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        JSONObject json = ConnectJson.queryUserInfoJson(prefs);
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
                                    mTextViewUserName.setText(responseJSON.getString("userName"));

                                    if (responseJSON.has("description") && !responseJSON.isNull("description")) {
                                        mTextViewUserdescription.setText(responseJSON.getString("description"));
                                    } else {
                                        mTextViewUserdescription.setText("");
                                    }

                                    if (responseJSON.has("nickName") && !responseJSON.isNull("nickName")) {
                                        mTextViewUserId.setText(responseJSON.getString("nickName"));
                                    } else {
                                        mTextViewUserId.setText("");
                                    }

                                    if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                        //URL profilePicUrl = new URL(responseJSON.getString("profilePicture"));
                                        ImageLoader.getInstance().displayImage(prefs.getString(GlobalVariable.userImgUrlStr, "null"), imgPhoto);
                                    } else {
                                        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.photo_round, imgPhoto);
                                    }

                                    /*
                                    if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                        //暫時無作用
                                    }*/
                                } else {
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
        JSONObject json = ConnectJson.queryListWork(prefs, 5, 0, 100);
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
                                    mSwipeLayoutProfile.setRefreshing(false);
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
            public void onWorkGoodClick(int position,boolean like, int wid) {
                if (like) {
                    setLike(position, 1, wid);
                } else {
                    setLike(position, 0, wid);
                }
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
            mRecyclerViewProfile.setLayoutManager(gridLayoutManager);
            mRecyclerViewProfile.setAdapter(mAdapterGrid);
            mSwipeLayoutProfile.setRefreshing(false);
        } else {
            mRecyclerViewProfile.setLayoutManager(layoutManager);
            mRecyclerViewProfile.setAdapter(mAdapterList);
            mSwipeLayoutProfile.setRefreshing(false);
        }
    }

    public void setLike(final int position, final int fn, int wid) {
        // fn = 1 點讚, 0 取消讚
        JSONObject json = ConnectJson.setLike(prefs, fn, wid);
        Log.d("LOGIN JSON: ", json.toString());
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_SET_LIKE)
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (responseJSON.getInt("res") == 1) {
                                    //點讚成功或刪除成功
                                    switch (fn) {
                                        case 0:
                                            mAdapterList.setLike(position, false);
                                            break;
                                        case 1:
                                            mAdapterList.setLike(position, true);
                                            break;
                                    }
                                    mAdapterList.notifyItemChanged(position);
                                } else {
                                    //點讚失敗或刪除失敗
                                    mAdapterList.notifyItemChanged(position);
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
