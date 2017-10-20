package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.WorkInfo;
import com.sctw.bonniedraw.works.WorkAdapterList;
import com.sctw.bonniedraw.works.WorkListOnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerViewHome;
    private Toolbar mToolbar;
    private ImageButton mImgBtnDrawer;
    private FragmentManager fragmentManager;
    private SearchView mSearchView;
    private SharedPreferences prefs;
    private List<WorkInfo> workInfoList;

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
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
        mToolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mImgBtnDrawer = (ImageButton) view.findViewById(R.id.toolbar_switch);
        mImgBtnDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.main_actitivy_drawlayout)).openDrawer(Gravity.START);
            }
        });

        mRecyclerViewHome = (RecyclerView) view.findViewById(R.id.recyclerView_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewHome.setLayoutManager(layoutManager);
        getWorksList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.dashborad, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
    }

    public void getWorksList() {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("wid", 0);
            json.put("wt", 4);
            json.put("stn", 0);
            json.put("rc", 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                                    Toast.makeText(getActivity(), "Download list successful", Toast.LENGTH_SHORT).show();
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
        try {
            workInfoList=new ArrayList<>();
            for (int x = 0; x < data.length(); x++) {
                WorkInfo workInfo=new WorkInfo();
                workInfo.setWorkId(data.getJSONObject(x).getString("worksId"));
                workInfo.setUserId(data.getJSONObject(x).getString("userId"));
                workInfo.setUserName(data.getJSONObject(x).getString("userName"));
                workInfo.setTitle(data.getJSONObject(x).getString("title"));
                workInfo.setImagePath(data.getJSONObject(x).getString("imagePath"));
                workInfo.setIsFollowing(data.getJSONObject(x).getString("isFollowing"));
                workInfoList.add(workInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WorkAdapterList mAdapter = new WorkAdapterList(workInfoList, new WorkListOnClickListener() {
            @Override
            public void onWorkImgClick(int wid) {
                Log.d("POSTION CLICK", "POSTION=" + String.valueOf(wid));
                fragmentManager = getChildFragmentManager();
                WorkFragment workFragment=new WorkFragment();
                Bundle bundle=new Bundle();
                bundle.putInt("wid",wid);
                workFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_home, workFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onWorkExtraClick(final int wid) {
                final FullScreenDialog extraDialog = new FullScreenDialog(getActivity(), R.layout.item_work_extra_dialog);
                Button extraShare = extraDialog.findViewById(R.id.btn_extra_share);
                Button extraCopyLink = extraDialog.findViewById(R.id.btn_extra_copylink);
                Button extraReport = extraDialog.findViewById(R.id.btn_extra_report);
                Button extraCancel = extraDialog.findViewById(R.id.btn_extra_cancel);
                extraDialog.getWindow().getAttributes().windowAnimations = R.style.FullScreenDialogStyle;
                extraShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("POSTION CLICK", "extraShare="+wid);
                        extraDialog.dismiss();
                    }
                });

                extraCopyLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("POSTION CLICK", "extraCopyLink="+wid);
                        extraDialog.dismiss();
                    }
                });

                extraReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("POSTION CLICK", "extraReport"+wid);
                        extraDialog.dismiss();
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
            public void onWorkGoodClick(int wid) {

            }

            @Override
            public void onWorkMsgClick(int wid) {

            }

            @Override
            public void onWorkShareClick(int wid) {

            }
        });

        mRecyclerViewHome.setAdapter(mAdapter);
    }
}
