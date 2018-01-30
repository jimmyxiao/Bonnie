package com.sctw.bonniedraw.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.WorkAdapterList;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.DiffCallBack;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.LinearLayoutManagerWithSmoothScroller;
import com.sctw.bonniedraw.utility.LoadMoreFooter;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.MessageDialog;
import com.sctw.bonniedraw.widget.ToastUtil;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

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
public class HomeAndHotFragment extends Fragment implements WorkAdapterList.WorkListOnClickListener, SwipeRefreshLayout.OnRefreshListener, LoadMoreFooter.OnLoadMoreListener, PlayFragment.OnPlayFragmentListener {
    private static final int GET_WORKS_LIST = 1;
    private static final int REFRESH_WORKS_LIST = 2;
    private HeaderAndFooterRecyclerView mRecyclerViewHome;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private MenuItem mMenuItem;
    private SharedPreferences prefs;
    private List<WorkInfoBean> workInfoBeanList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ProgressBar mProgressBar;
    private WorkAdapterList mAdapter;
    private LinearLayoutManagerWithSmoothScroller mLayoutManager;
    private TextView mTvHint;
    private int miWt, miStn = 1, miRc = 20; //STN=起始筆數 RC=需求筆數
    private int interWt;  // 1=追蹤 , 2= 熱門
    private String mStrQuery;
    private LoadMoreFooter mLoadMoreFooter;
    private Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mContext = getContext();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        interWt = getArguments().getInt("page");
        mStrQuery = getArguments().getString("query", "");
        miWt = interWt;
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout_home);
        mRecyclerViewHome = view.findViewById(R.id.recyclerView_home);
        mRecyclerViewHome.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManagerWithSmoothScroller(getContext(), LinearLayoutManager.VERTICAL, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_home);
        mTvHint = (TextView) view.findViewById(R.id.textView_home_hint);
        fragmentManager = getFragmentManager();
        if (interWt != 1) {
            mToolbar.setNavigationIcon(R.drawable.title_bar_menu);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DrawerLayout) getActivity().findViewById(R.id.main_actitivy_drawlayout)).openDrawer(Gravity.START);
                }
            });
        } else {
            mToolbar.setNavigationIcon(null);
        }

        mRecyclerViewHome.setLayoutManager(mLayoutManager);
        if (interWt == 8 && !mStrQuery.isEmpty()) {
            getQueryWorksList(mStrQuery);
        } else {
            getWorksList(GET_WORKS_LIST);
        }
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLoadMoreFooter = new LoadMoreFooter(getContext(), mRecyclerViewHome, this);
        mLoadMoreFooter.setState(LoadMoreFooter.STATE_DISABLED);

        Intent intent = this.getActivity().getIntent();
        int fn = intent.getIntExtra("fn", 0);
        if (fn == 5) {
            int iUid = intent.getIntExtra("uid", 0);
            this.getActivity().getIntent().removeExtra("fn");
            this.getActivity().getIntent().removeExtra("uid");
            openMemberFragment(iUid);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        mMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mMenuItem.getActionView();
        mSearchView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_searchview_bg));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint(getString(R.string.u02_05_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mStrQuery = query;
                mProgressBar.setVisibility(View.VISIBLE);
                miWt = 9;
                miStn = 1;
                miRc = 10;
                getQueryWorksList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        mMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (interWt != miWt) {
                    miWt = interWt;
                    miStn = 1;
                    miRc = 20;
                    getWorksList(GET_WORKS_LIST);
                    return true;
                }
                return true;
            }
        });
    }

    public void showViewToTop() {
        mRecyclerViewHome.smoothScrollToPosition(0);
    }

    public void getWorks(int select, JSONArray data) {
        switch (select) {
            case GET_WORKS_LIST:
                workInfoBeanList = WorkInfoBean.generateInfoList(data);
                if (workInfoBeanList.size() == 0) {
                    mTvHint.setVisibility(View.VISIBLE);
                    if (miWt == 1) {
                        mTvHint.setText(R.string.m01_01_not_follow_anyone);
                    } else if (miWt != 2) {
                        mTvHint.setText(R.string.u02_05_not_match_work);
                    }
                } else {
                    mTvHint.setVisibility(View.INVISIBLE);
                }
                if (interWt == 1) {
                    mAdapter = new WorkAdapterList(mContext, workInfoBeanList, this, false);
                } else {
                    mAdapter = new WorkAdapterList(mContext, workInfoBeanList, this, true);
                }
                mProgressBar.setVisibility(View.GONE);
                mRecyclerViewHome.setAdapter(mAdapter);
                break;
            case REFRESH_WORKS_LIST:
                workInfoBeanList = WorkInfoBean.generateInfoList(data);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(mAdapter.getData(), workInfoBeanList), false);
                diffResult.dispatchUpdatesTo(mAdapter);
                mAdapter.setData(workInfoBeanList);
                break;
        }
        mLoadMoreFooter.setState(mAdapter.getItemCount() == miRc ? LoadMoreFooter.STATE_ENDLESS : LoadMoreFooter.STATE_FINISHED);
    }

    public void setLike(final int position, final int fn, int wid) {
        // fn = 1 點讚, 0 取消讚
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.setLike(prefs, fn, wid);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
                                            mAdapter.setLike(position, false);
                                            break;
                                        case 1:
                                            mAdapter.setLike(position, true);
                                            break;
                                    }
                                } else {
                                    //點讚失敗或刪除失敗
                                    mAdapter.notifyItemChanged(position, 0);
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
        // fn = 1 設定追蹤, 0 取消追蹤
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.setFollow(prefs, fn, followId);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
                                    //點成功或失敗
                                    switch (fn) {
                                        case 0:
                                            mAdapter.setFollow(position, 0);
                                            break;
                                        case 1:
                                            mAdapter.setFollow(position, 1);
                                            break;
                                    }
                                } else {
                                    mAdapter.notifyItemChanged(position, 1);
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
                                            mAdapter.setCollection(position, false);
                                            break;
                                        case 1:
                                            mAdapter.setCollection(position, true);
                                            break;
                                    }
                                } else {
                                    mAdapter.notifyItemChanged(position, 2);
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
                                    ToastUtil.createToastIsCheck(mContext, getString(R.string.u02_02_report_successful), true, PxDpConvert.getSystemHight(mContext) / 3);
                                } else {
                                    ToastUtil.createToastIsCheck(mContext, getString(R.string.u02_02_report_fail), false, PxDpConvert.getSystemHight(mContext) / 3);
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

    public void getQueryWorksList(String input) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryListWorkAdvanced(prefs, miWt, miStn, miRc, input);
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
                                        getWorks(GET_WORKS_LIST, responseJSON.getJSONArray("workList"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getWorksList(final int select) {
        //select 1=get ,2=refresh,3=add
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryListWork(prefs, miWt, miStn, miRc);
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
                                        getWorks(select, responseJSON.getJSONArray("workList"));
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

    @Override
    public void onWorkImgClick(int wid) {
        Bundle bundle = new Bundle();
        bundle.putInt("wid", wid);
        PlayFragment playFragment = new PlayFragment();
        playFragment.setArguments(bundle);
        playFragment.setTargetFragment(this, 0);
        playFragment.show(getFragmentManager(), "TAG");
    }

    @Override
    public void onWorkExtraClick(int uid, final int wid) {
        final FullScreenDialog extraDialog = new FullScreenDialog(getActivity(), R.layout.dialog_work_extra);
        Button extraCopyLink = extraDialog.findViewById(R.id.btn_extra_copylink);
        Button extraReport = extraDialog.findViewById(R.id.btn_extra_report);
        Button extraCancel = extraDialog.findViewById(R.id.btn_extra_cancel);
        LinearLayout reportLayout = extraDialog.findViewById(R.id.ll_item_work_report);

        extraCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("text", GlobalVariable.API_LINK_SHARE_LINK + wid);
                clipboard.setPrimaryClip(clip);
                ToastUtil.createToastIsCheck(mContext, getString(R.string.m01_01_copylink_successful), true, PxDpConvert.getSystemHight(mContext) / 3);
                extraDialog.dismiss();
            }
        });

        extraReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extraDialog.dismiss();
                final FullScreenDialog reportDialog = new FullScreenDialog(mContext, R.layout.dialog_work_report);
                final Spinner spinner = reportDialog.findViewById(R.id.spinner_report);
                final EditText editText = reportDialog.findViewById(R.id.editText_report);
                Button btnCancel = reportDialog.findViewById(R.id.btn_report_cancel);
                Button btnCommit = reportDialog.findViewById(R.id.btn_report_commit);
                ArrayAdapter<CharSequence> nAdapter = ArrayAdapter.createFromResource(
                        mContext, R.array.report, R.layout.item_spinner);
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
                        if (editText.getText().toString().isEmpty()) {
                            ToastUtil.createToastIsCheck(mContext, getString(R.string.u02_02_report_reason_empty), false, PxDpConvert.getSystemHight(mContext) / 3);
                        } else {
                            int type = 0;
                            switch (spinner.getSelectedItemPosition()) {
                                case 0:
                                    type = 1;
                                    break;
                                case 1:
                                    type = 2;
                                    break;
                                case 2:
                                    type = 99;
                                    break;
                            }
                            setReport(wid, type, editText.getText().toString());
                            reportDialog.dismiss();
                        }
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
        int ownUid = Integer.valueOf(prefs.getString(GlobalVariable.API_UID, "null"));
        if (ownUid == uid) {
            reportLayout.setVisibility(View.GONE);
        }
        extraDialog.show();
    }

    @Override
    public void onWorkGoodClick(int position, boolean like, int wid) {
        //點讚 1 , 不點讚 0
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
    public void onShopInfoClick(int position) {
        String url = null;
        try {
            url = mAdapter.getData().get(position).getCommodityUrl();
            if(url!=null && !url.equals("")) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUserClick(int uid) {
        openMemberFragment(uid);
    }

    public void openMemberFragment(int uid) {

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
        //點FOLLOW   0代表沒有追蹤，點下去要追蹤
        if (isFollow == 0) {
            setFollow(position, 1, uid);
        } else {
            setFollow(position, 0, uid);
        }
    }

    @Override
    public void onRefresh() {

        if(miWt == 9 && mSearchView !=null)
        {
            if(mMenuItem !=null){

                MenuItemCompat.collapseActionView(mMenuItem);
            }
            /*
            if (!mSearchView.isIconified()) {
                mSearchView.setIconified(true);
            }
            */
        }

        getWorksList(REFRESH_WORKS_LIST);
        /*
        if (miWt != 9) {
            getWorksList(REFRESH_WORKS_LIST);
        } else {
            miStn = 1;
            miRc = 20;
            getQueryWorksList(mStrQuery);
        }
        */
    }

    @Override
    public void onLoadMore() {
        if (mLayoutManager.findLastVisibleItemPosition() == miRc) {
            miRc += 10;
            getWorksList(REFRESH_WORKS_LIST);
        }
    }

    @Override
    public void onDeleteWorkSuccess() {
        // reload works
        ToastUtil.createToastIsCheck(mContext, getString(R.string.u02_04_delete_successful), true, PxDpConvert.getSystemHight(mContext) / 3);
        getWorksList(REFRESH_WORKS_LIST);

    }
}
