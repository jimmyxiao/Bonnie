package com.sctw.bonniedraw.widget;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.MsgAdapter;
import com.sctw.bonniedraw.bean.MsgBean;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Fatorin on 2017/11/7.
 */

public class MessageDialog extends DialogFragment implements View.OnClickListener, MsgAdapter.OnClickMsgPublish {
    ImageButton mBtnBack;
    RecyclerView mRv;
    EditText mEidtMsg;
    TextView mTtextPublish;
    SharedPreferences prefs;
    ArrayList<MsgBean> msgBeanArrayList;
    FrameLayout mFrameLayout;
    SwipeRefreshLayout mSwipeRefreshLayout;
    MsgAdapter mAdapter;
    int miWid;

    public static MessageDialog newInstance(int wid) {
        MessageDialog f = new MessageDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("wid", wid);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
        miWid = getArguments().getInt("wid");
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View rootView = inflater.inflate(R.layout.fragment_dialog_message, container, false);
        //Do something
        final Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.Transparent);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBtnBack = view.findViewById(R.id.imgBtn_message_back);
        mEidtMsg = view.findViewById(R.id.editText_write_msg);
        mTtextPublish = view.findViewById(R.id.textView_publish_msg);
        mBtnBack.setOnClickListener(this);
        mTtextPublish.setOnClickListener(this);
        mFrameLayout = view.findViewById(R.id.frameLayout_message_empty);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout_message);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMsgList();
            }
        });
        mRv = view.findViewById(R.id.recyclerview_message);
        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(lm);
        getMsgList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtn_message_back:
                this.dismiss();
                break;
            case R.id.textView_publish_msg:
                if (!mEidtMsg.getText().toString().isEmpty()) {
                    onPublishMsg();
                }
                break;
        }
    }

    private void publishMsg() {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.leaveMsg(prefs, miWid, mEidtMsg.getText().toString());
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
                                    mEidtMsg.setText("");
                                    getMsgList();
                                }
                            });
                        } else {
                            ToastUtil.createToastWindow(getContext(), getString(R.string.u02_03_add_comment_fail), PxDpConvert.getSystemHight(getContext()) / 4);
                        }

                    }
                    System.out.println(responseJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getMsgList() {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.querySingleWork(prefs, miWid);
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
                                        JSONArray data = responseJSON.getJSONObject("work").getJSONArray("msgList");
                                        if (data != null && data.length() > 0) {
                                            refreshWorks(data);
                                            mFrameLayout.setVisibility(View.GONE);
                                        } else {
                                            mFrameLayout.setVisibility(View.VISIBLE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(responseJSON.toString());
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

    private void refreshWorks(JSONArray data) {
        mSwipeRefreshLayout.setRefreshing(false);
        msgBeanArrayList = new ArrayList<>();
        try {
            for (int x = 0; x < data.length(); x++) {
                MsgBean msgBean = new MsgBean();
                msgBean.setUserId(data.getJSONObject(x).getInt("userId"));
                msgBean.setWorksMsgId(data.getJSONObject(x).getInt("worksMsgId"));
                msgBean.setMessage(data.getJSONObject(x).getString("message"));
                msgBean.setCreationDate(data.getJSONObject(x).getString("creationDate"));
                msgBean.setUserName(data.getJSONObject(x).getString("userName"));
                msgBean.setProfilePicture(data.getJSONObject(x).getString("profilePicture"));
                msgBeanArrayList.add(msgBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new MsgAdapter(getContext(), msgBeanArrayList, this);
        mRv.setAdapter(mAdapter);
        mRv.setVisibility(View.VISIBLE);
    }

    private void onPublishMsg() {
        publishMsg();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEidtMsg.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public void onClickExtra(final int position, final int msgId) {
        final FullScreenDialog dialog = new FullScreenDialog(getContext(), R.layout.dialog_message_extra);
        RelativeLayout layout = dialog.findViewById(R.id.relativeLayout_msg_extra);
        Button btnCancel = dialog.findViewById(R.id.btn_extra_msg_cancel);
        Button btnDelete = dialog.findViewById(R.id.btn_extra_msg_delete);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMsgDelete(position, miWid, msgId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setMsgDelete(final int position, int wid, int msgId) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.deleteLeaveMsg(prefs, wid, msgId);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.createToastWindow(getContext(), getString(R.string.uc_connect_failed_title), PxDpConvert.getSystemHight(getContext()) / 4);
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
                                    mAdapter.deleteMsg(position);
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
}
