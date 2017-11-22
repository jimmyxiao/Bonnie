package com.sctw.bonniedraw.fragment;


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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.WorkAdapterGrid;
import com.sctw.bonniedraw.adapter.WorkAdapterList;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.widget.MessageDialog;
import com.sctw.bonniedraw.widget.PlayDialog;
import com.sctw.bonniedraw.widget.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements WorkAdapterList.WorkListOnClickListener {
    private CircleImageView imgPhoto;
    private TextView mTextViewUserName, mTextViewUserId, mTextViewUserdescription, mTextViewWorks, mTextViewFans, mTextViewFollows;
    private ImageButton mImgBtnBookmark, mImgBtnSetting, mImgBtnGrid, mImgBtnList;
    private Button mBtnEdit;
    private SwipeRefreshLayout mSwipeLayoutProfile;
    private RecyclerView mRecyclerViewProfile;
    private WorkAdapterGrid mAdapterGrid;
    private WorkAdapterList mAdapterList;
    private GridLayoutManager gridLayoutManager;
    private LinearLayout mLlFans, mLlFollow;
    private LinearLayoutManager layoutManager;
    private SharedPreferences prefs;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private List<WorkInfoBean> workInfoBeanList;
    private boolean mbFist = true;
    private int miUserId;

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
        miUserId=Integer.valueOf(prefs.getString(GlobalVariable.API_UID,"null"));
        imgPhoto = (CircleImageView) view.findViewById(R.id.circleImg_profile_photo);
        mSwipeLayoutProfile = view.findViewById(R.id.swipeLayout_profile);
        mLlFans = (LinearLayout) view.findViewById(R.id.ll_profile_fans);
        mLlFollow = (LinearLayout) view.findViewById(R.id.ll_profile_follow);
        mTextViewUserName = (TextView) view.findViewById(R.id.textView_profile_userName);
        mTextViewUserdescription = view.findViewById(R.id.textView_profile_user_description);
        mTextViewUserId = (TextView) view.findViewById(R.id.textView_profile_user_id);
        mTextViewWorks = (TextView) view.findViewById(R.id.textView_profile_userworks);
        mTextViewFollows = (TextView) view.findViewById(R.id.textView_profile_follows);
        mTextViewFans = (TextView) view.findViewById(R.id.textView_profile_fans);
        mImgBtnBookmark = (ImageButton) view.findViewById(R.id.imgBtn_profile_bookmark);
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
        mLlFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                // 1=fans   2=follow
                bundle.putInt("fn",1);
                bundle.putInt("uid",miUserId);
                FansOrFollowFragment fansOrFollowFragment=new FansOrFollowFragment();
                fansOrFollowFragment.setArguments(bundle);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fansOrFollowFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mLlFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("fn",2);
                bundle.putInt("uid",miUserId);
                FansOrFollowFragment fansOrFollowFragment=new FansOrFollowFragment();
                fansOrFollowFragment.setArguments(bundle);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fansOrFollowFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mImgBtnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, new CollectionFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

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
                getWorksList();
            }
        });
    }

    void updateProfileInfo() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryUserInfoJson(prefs);
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

                                    //if (responseJSON.has("nickName") && !responseJSON.isNull("nickName")) {
                                    if (responseJSON.has("email") && !responseJSON.isNull("email")) {
                                        String temp = responseJSON.getString("email");
                                        mTextViewUserId.setText(temp.substring(0, temp.indexOf("@")));
                                    } else {
                                        mTextViewUserId.setText("");
                                    }
                                    mTextViewWorks.setText(responseJSON.getString("worksNum"));
                                    mTextViewFans.setText(responseJSON.getString("fansNum"));
                                    ;
                                    mTextViewFollows.setText(responseJSON.getString("followNum"));
                                    ;

                                    String profileUrl = "";
                                    if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                        profileUrl = GlobalVariable.API_LINK_GET_FILE + responseJSON.getString("profilePicture");
                                    }
                                    Glide.with(getContext())
                                            .load(profileUrl)
                                            .apply(GlideAppModule.getUserOptions())
                                            .into(imgPhoto);
                                } else {
                                    Toast.makeText(getContext(), "連線失敗", Toast.LENGTH_SHORT).show();
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
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryListWork(prefs, 5, 0, 100);
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
                    System.out.println(responseJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refreshWorks(JSONArray data) {
        workInfoBeanList = WorkInfoBean.generateInfoList(data);

        mAdapterGrid = new WorkAdapterGrid(getContext(), workInfoBeanList, new WorkAdapterGrid.WorkGridOnClickListener() {
            @Override
            public void onWorkClick(int wid) {
                PlayDialog playDialog = PlayDialog.newInstance(wid);
                playDialog.show(fragmentManager, "TAG");
            }
        });

        mAdapterList = new WorkAdapterList(getContext(), workInfoBeanList, this);

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
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.setLike(prefs, fn, wid);
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

    public void setCollection(final int position, final int fn, int wid) {
        // fn = 1 收藏, 0 取消收藏
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.setCollection(prefs, fn, wid);
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
                                            mAdapterList.setCollection(position, false);
                                            break;
                                        case 1:
                                            mAdapterList.setCollection(position, true);
                                            break;
                                    }
                                    mAdapterList.notifyItemChanged(position);
                                } else {
                                    //點讚失敗或刪除失敗
                                    mAdapterList.notifyItemChanged(position);
                                }
                                System.out.println(responseJSON.toString());
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

    public void setFollow(final int position, final int fn, int followId) {
        // fn = 1 點讚, 0 取消讚
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.setFollow(prefs, fn, followId);
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
                                            mAdapterList.setFollow(position, 0);
                                            break;
                                        case 1:
                                            mAdapterList.setFollow(position, 1);
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

    public void setReport(int workId, int turnInType, String description) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.reportWork(prefs, workId, turnInType, description);
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
                                    ToastUtil.createToastIsCheck(getContext(), "檢舉成功", true);
                                } else {
                                    ToastUtil.createToastIsCheck(getContext(), "檢舉失敗，請再試一次", false);
                                }
                                System.out.println(responseJSON.toString());
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
    public void onWorkImgClick(int wid) {
        PlayDialog playDialog = PlayDialog.newInstance(wid);
        playDialog.show(fragmentManager, "TAG");
    }

    @Override
    public void onWorkExtraClick(final int wid) {
        final FullScreenDialog extraDialog = new FullScreenDialog(getActivity(), R.layout.dialog_work_extra);
        Button extraShare = extraDialog.findViewById(R.id.btn_extra_share);
        Button extraCopyLink = extraDialog.findViewById(R.id.btn_extra_copylink);
        Button extraReport = extraDialog.findViewById(R.id.btn_extra_report);
        Button extraCancel = extraDialog.findViewById(R.id.btn_extra_cancel);
        extraDialog.getWindow().getAttributes().windowAnimations = R.style.FullScreenDialogAnim;
        extraShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("POSTION CLICK", "extraShare=" + wid);
                extraDialog.dismiss();
            }
        });

        extraCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("POSTION CLICK", "extraCopyLink=" + wid);
                extraDialog.dismiss();
            }
        });

        extraReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extraDialog.dismiss();
                final FullScreenDialog reportDialog = new FullScreenDialog(getContext(), R.layout.dialog_work_report);
                final Spinner spinner = reportDialog.findViewById(R.id.spinner_report);
                final EditText editText = reportDialog.findViewById(R.id.editText_report);
                Button btnCancel = reportDialog.findViewById(R.id.btn_report_cancel);
                Button btnCommit = reportDialog.findViewById(R.id.btn_report_commit);
                ArrayAdapter<CharSequence> nAdapter = ArrayAdapter.createFromResource(
                        getContext(), R.array.report, android.R.layout.simple_spinner_item);
                nAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                spinner.setAdapter(nAdapter);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportDialog.dismiss();
                    }
                });
                btnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setReport(wid, spinner.getSelectedItemPosition() + 1, editText.getText().toString());
                        reportDialog.dismiss();
                    }
                });
                reportDialog.show();
            }
        });

        extraCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extraDialog.dismiss();
            }
        });

        extraDialog.findViewById(R.id.relativeLayout_works_extra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extraDialog.dismiss();
            }
        });

        extraDialog.show();
    }

    @Override
    public void onWorkGoodClick(int position, boolean like, int wid) {
        if (like) {
            setLike(position, 1, wid);
        } else {
            setLike(position, 0, wid);
        }
    }

    @Override
    public void onWorkMsgClick(int wid) {
        MessageDialog messageDialog = MessageDialog.newInstance(wid);
        messageDialog.show(fragmentManager, "TAG");
    }

    @Override
    public void onWorkShareClick(int wid) {

    }

    @Override
    public void onUserClick(int uid) {
        MemberFragment memberFragment = new MemberFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("userId", uid);
        memberFragment.setArguments(bundle);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_actitivy, memberFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onWorkCollectionClick(int position, boolean isCollection, int wid) {
        if (isCollection) {
            setCollection(position, 1, wid);
        } else {
            setCollection(position, 0, wid);
        }
    }

    @Override
    public void onFollowClick(int position, int isFollow, int uid) {
        if (isFollow == 0) {
            setFollow(position, 1, uid);
        } else {
            setFollow(position, 0, uid);
        }
    }
}
