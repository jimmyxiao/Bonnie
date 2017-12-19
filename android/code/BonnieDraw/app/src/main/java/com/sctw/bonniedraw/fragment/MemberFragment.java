package com.sctw.bonniedraw.fragment;


import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.WorkAdapterGrid;
import com.sctw.bonniedraw.adapter.WorkAdapterList;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.MessageDialog;
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
public class MemberFragment extends Fragment implements WorkAdapterList.WorkListOnClickListener, WorkAdapterGrid.WorkGridOnClickListener {
    private static final int GET_AND_REFRESH_WORK_LIST = 1;
    private static final int ADD_WORK_LIST = 3;
    private TextView mTvMemberName, mTvMemberDescription, mTvMemberWorks, mTvMemberFans, mTvMemberFollows;
    private Button mBtnFollow;
    private CircleImageView mCircleImg;
    private ImageButton mBtnBack, mBtnGrid, mBtnList, mBtnAdd, mBtnExtra;
    private LinearLayout mLlFans, mLlFollow;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRv;
    private SharedPreferences prefs;
    private List<WorkInfoBean> workInfoBeanList;
    private int miUserId;
    private WorkAdapterGrid mAdapterGrid;
    private WorkAdapterList mAdapterList;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager layoutManager;
    private boolean mbFirst = true, mbFollow = false;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int miStn = 1, miRc = 15;

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
        miUserId = getArguments().getInt("userId");
        mTvMemberName = view.findViewById(R.id.textView_member_userName);
        mTvMemberDescription = view.findViewById(R.id.textView_member_user_description);
        mTvMemberWorks = view.findViewById(R.id.textView_member_userworks);
        mTvMemberFans = view.findViewById(R.id.textView_member_fans);
        mTvMemberFollows = view.findViewById(R.id.textView_member_follows);
        mLlFans = view.findViewById(R.id.ll_member_fans);
        mLlFollow = view.findViewById(R.id.ll_member_follow);
        mBtnFollow = view.findViewById(R.id.btn_member_follow);
        mCircleImg = view.findViewById(R.id.circleImg_member_photo);
        mBtnBack = view.findViewById(R.id.imgBtn_member_back);
        mBtnGrid = view.findViewById(R.id.imgBtn_member_grid);
        mBtnList = view.findViewById(R.id.imgBtn_member_list);
        mBtnAdd = view.findViewById(R.id.imgBtn_member_add_friend);
        mBtnExtra = view.findViewById(R.id.imgBtn_member_extra);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout_member);
        mRv = view.findViewById(R.id.recyclerview_member);
        fragmentManager = getFragmentManager();
        setOnClickEvent();
        getMemberInfo();
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getWorksList(GET_AND_REFRESH_WORK_LIST);
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    if (layoutManager.findLastVisibleItemPosition() + 1 == miRc) {
                        miRc += 10;
                        getWorksList(GET_AND_REFRESH_WORK_LIST);
                    } else if (gridLayoutManager.findLastVisibleItemPosition() + 1 == miRc) {
                        miRc += 10;
                        getWorksList(GET_AND_REFRESH_WORK_LIST);
                    }
                }
            }
        });
    }

    private void getMemberInfo() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryOtherUserInfoJson(prefs, miUserId);
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
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
                                    try {
                                        //Successful
                                        mTvMemberName.setText(responseJSON.getString("userName"));

                                        if (responseJSON.has("description") && !responseJSON.isNull("description")) {
                                            mTvMemberDescription.setText(responseJSON.getString("description"));
                                        } else {
                                            mTvMemberDescription.setText("");
                                        }

                                        if (responseJSON.getBoolean("follow")) {
                                            mbFollow = true;
                                            mBtnFollow.setText(getString(R.string.following));
                                        } else {
                                            mbFollow = false;
                                            mBtnFollow.setText(getString(R.string.follow));
                                        }

                                        mTvMemberWorks.setText(responseJSON.getString("worksNum"));
                                        mTvMemberFans.setText(responseJSON.getString("fansNum"));
                                        mTvMemberFollows.setText(responseJSON.getString("followNum"));

                                        String profileUrl = "";
                                        if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                            profileUrl = GlobalVariable.API_LINK_GET_FILE + responseJSON.getString("profilePicture");
                                        }
                                        Glide.with(getContext())
                                                .load(profileUrl)
                                                .apply(GlideAppModule.getUserOptions())
                                                .into(mCircleImg);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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

    public void getWorksList(final int select) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryListWorkOther(prefs, 6, miStn, miRc, miUserId);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Get List Works", "Fail");
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    if (responseJSON.getInt("res") == 1) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //下載資料
                                    try {
                                        switch (select) {
                                            case GET_AND_REFRESH_WORK_LIST:
                                                getWorks(responseJSON.getJSONArray("workList"));
                                                break;
                                            case ADD_WORK_LIST:
                                                addWork(responseJSON.getJSONArray("workList"));
                                                break;
                                        }
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
        workInfoBeanList = WorkInfoBean.generateInfoList(data);

        mAdapterGrid = new WorkAdapterGrid(getContext(), workInfoBeanList, this);

        mAdapterList = new WorkAdapterList(getContext(), workInfoBeanList, this,true);

        if (mbFirst) {
            mRv.setLayoutManager(gridLayoutManager);
            mRv.setAdapter(mAdapterGrid);
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            mRv.setLayoutManager(layoutManager);
            mRv.setAdapter(mAdapterList);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void addWork(JSONArray data) {
        List<WorkInfoBean> temp = WorkInfoBean.generateInfoList(data);
        mAdapterList.addData(temp);
        mAdapterGrid.addData(temp);
    }

    private void setOnClickEvent() {
        mBtnExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialog dialog = new FullScreenDialog(getContext(), R.layout.dialog_member_extra);
            }
        });

        mLlFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                // 2=fans   1=follow
                bundle.putInt("fn", 1);
                bundle.putInt("uid", miUserId);
                FansOrFollowFragment fansOrFollowFragment = new FansOrFollowFragment();
                fansOrFollowFragment.setArguments(bundle);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fansOrFollowFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mLlFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                // 1=fans   2=follow
                bundle.putInt("fn", 2);
                bundle.putInt("uid", miUserId);
                FansOrFollowFragment fansOrFollowFragment = new FansOrFollowFragment();
                fansOrFollowFragment.setArguments(bundle);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fansOrFollowFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWorksList(GET_AND_REFRESH_WORK_LIST);
            }
        });

        mBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRv.setLayoutManager(layoutManager);
                mRv.setAdapter(mAdapterList);
                mbFirst = false;
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mBtnGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRv.setLayoutManager(gridLayoutManager);
                mRv.setAdapter(mAdapterGrid);
                mbFirst = true;
            }
        });

        mBtnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mbFollow) {
                    //IF
                    setFollow(1, miUserId);
                }
            }
        });
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

    public void setFollow(final int fn, int followId) {
        // fn = 1 點追蹤, 0 取消追蹤
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
                                            mbFollow = false;
                                            mBtnFollow.setText(getString(R.string.follow));
                                            break;
                                        case 1:
                                            mbFollow = true;
                                            mBtnFollow.setText(getString(R.string.following));
                                            break;
                                    }
                                } else {
                                    //點讚失敗或刪除失敗
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
                                    ToastUtil.createToastIsCheck(getContext(), getString(R.string.report_successful), true, 0);
                                } else {
                                    ToastUtil.createToastIsCheck(getContext(), getString(R.string.report_fail), false, 0);
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

    private void showWork(int wid){
        Bundle bundle = new Bundle();
        bundle.putInt("wid", wid);
        PlayFragment playFragment = new PlayFragment();
        playFragment.setArguments(bundle);
        playFragment.show(getFragmentManager(),"TAG");
    }

    //覆寫interface事件
    @Override
    public void onWorkImgClick(int wid) {
        showWork(wid);
    }

    @Override
    public void onWorkExtraClick(final int wid) {
        final FullScreenDialog extraDialog = new FullScreenDialog(getActivity(), R.layout.dialog_work_extra);
        Button extraCopyLink = extraDialog.findViewById(R.id.btn_extra_copylink);
        Button extraReport = extraDialog.findViewById(R.id.btn_extra_report);
        Button extraCancel = extraDialog.findViewById(R.id.btn_extra_cancel);

        extraCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("text", GlobalVariable.API_LINK_SHARE_LINK + wid);
                clipboard.setPrimaryClip(clip);
                ToastUtil.createToastIsCheck(getContext(), getString(R.string.copylink_successful), true, PxDpConvert.getSystemHight(getContext()) / 4);
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

    @Override
    public void onResume() {
        miStn = 1;
        miRc = 15;
        getWorksList(GET_AND_REFRESH_WORK_LIST);
        super.onResume();
    }

    @Override
    public void onGridWorkClick(int wid) {
        showWork(wid);
    }
}
