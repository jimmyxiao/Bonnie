package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.WorkAdapterList;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.widget.MessageDialog;
import com.sctw.bonniedraw.widget.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
public class HomeAndHotFragment extends Fragment implements WorkAdapterList.WorkListOnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int GET_WORKS_LIST = 1;
    private static final int REFRESH_WORKS_LIST = 2;
    private static final int ADD_WORKS_LIST = 3;
    private RecyclerView mRecyclerViewHome;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private SharedPreferences prefs;
    private List<WorkInfoBean> workInfoBeanList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ProgressBar mProgressBar;
    private WorkAdapterList mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int miWt, miStn = 1, miRc = 20; //STN=起始筆數 RC=需求筆數
    private int interWt;  // 1= home , 2=hot
    private String mStrQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        interWt = getArguments().getInt("page");
        miWt = interWt;
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout_home);
        mRecyclerViewHome = (RecyclerView) view.findViewById(R.id.recyclerView_home);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_home);
        fragmentManager = getFragmentManager();
        mToolbar.setNavigationIcon(R.drawable.title_bar_menu);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout) getActivity().findViewById(R.id.main_actitivy_drawlayout)).openDrawer(Gravity.START);
            }
        });
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewHome.setLayoutManager(mLayoutManager);
        getWorksList(GET_WORKS_LIST);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerViewHome.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                Log.d("lastVisibleItemPosition", "COUNT" + lastVisibleItemPosition);
                if (!recyclerView.canScrollVertically(1)) {
                    miStn += 10;
                    miRc += 10;
                    getWorksList(ADD_WORKS_LIST);
                    System.out.println("頂到肺了");
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.dashborad, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_searchview_bg));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mStrQuery = query;
                mProgressBar.setVisibility(View.VISIBLE);
                miWt = 9;
                getQueryWorksList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                miWt = interWt;
                miStn = 1;
                miRc = 20;
                getWorksList(GET_WORKS_LIST);
                return true;
            }
        });
    }

    public void getWorks(int select, JSONArray data) {
        switch (select) {
            case GET_WORKS_LIST:
                workInfoBeanList = WorkInfoBean.generateInfoList(data);
                mAdapter = new WorkAdapterList(getContext(), workInfoBeanList, this);
                mProgressBar.setVisibility(View.GONE);
                mRecyclerViewHome.setAdapter(mAdapter);
                break;
            case REFRESH_WORKS_LIST:
                workInfoBeanList = WorkInfoBean.generateInfoList(data);
                mAdapter.refreshData(workInfoBeanList);
                break;
            case ADD_WORKS_LIST:
                ArrayList<WorkInfoBean> tempList = WorkInfoBean.generateInfoList(data);
                workInfoBeanList.addAll(tempList);
                mAdapter.addData(workInfoBeanList);
                break;
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
                                            mAdapter.setLike(position, false);
                                            break;
                                        case 1:
                                            mAdapter.setLike(position, true);
                                            break;
                                    }
                                    mAdapter.notifyItemChanged(position);
                                } else {
                                    //點讚失敗或刪除失敗
                                    mAdapter.notifyItemChanged(position);
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
                                    //點成功或失敗
                                    switch (fn) {
                                        case 0:
                                            mAdapter.setFollow(position, 0);
                                            break;
                                        case 1:
                                            mAdapter.setFollow(position, 1);
                                            break;
                                    }
                                    mAdapter.notifyItemChanged(position);
                                    System.out.println(responseJSON.toString());
                                } else {
                                    //點讚失敗或刪除失敗
                                    mAdapter.notifyItemChanged(position);
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
                                            mAdapter.setCollection(position, false);
                                            break;
                                        case 1:
                                            mAdapter.setCollection(position, true);
                                            break;
                                    }
                                    mAdapter.notifyItemChanged(position);
                                } else {
                                    //點讚失敗或刪除失敗
                                    mAdapter.notifyItemChanged(position);
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
                                    ToastUtil.createToastIsCheck(getContext(), "檢舉成功", true, 0);
                                } else {
                                    ToastUtil.createToastIsCheck(getContext(), "檢舉失敗，請再試一次", false, 0);
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

    public void getQueryWorksList(String input) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryListWorkAdvanced(prefs, miWt, miStn, miRc, input);
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
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_actitivy, playFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
        //點FOLLOW   0代表沒有追蹤，點下去要追蹤
        if (isFollow == 0) {
            setFollow(position, 1, uid);
        } else {
            setFollow(position, 0, uid);
        }
    }

    @Override
    public void onRefresh() {
        miStn = 1;
        miRc = 20;
        if (miWt != 9) {
            getWorksList(REFRESH_WORKS_LIST);
            System.out.println("刷新");
        } else {
            getQueryWorksList(mStrQuery);
        }
    }
}
