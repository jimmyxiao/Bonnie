package com.sctw.bonniedraw.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.activity.LoginActivity;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdatePasswordFragment extends DialogFragment {
    Button updatePasswordCancel, updatePasswordDone;
    SharedPreferences prefs;
    EditText mEditTextOldPwd, mEditTextNewPwd, mTextViewCheckPwd;
    final static int SUCCESSFUL_UPDATE_PASSWORD = 1;
    final static int ERROR_OLD_PASSWORD = 4;
    final static int ERROR_CONNECT = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        updatePasswordCancel = view.findViewById(R.id.btn_update_pwd_cancel);
        updatePasswordDone = view.findViewById(R.id.btn_update_pwd_done);
        mEditTextOldPwd = view.findViewById(R.id.editText_old_pwd);
        mEditTextNewPwd = view.findViewById(R.id.editText_new_password);
        mTextViewCheckPwd = view.findViewById(R.id.editText_check_pwd);
        updatePasswordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePasswordFragment.this.dismiss();
            }
        });

        updatePasswordDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPwd = mEditTextOldPwd.getText().toString();
                String newPwd = mEditTextNewPwd.getText().toString();
                String checkPwd = mTextViewCheckPwd.getText().toString();
                if (newPwd.equals(checkPwd) && oldPwd.trim().length() != 0) {
                    updatePassword();
                } else {
                    checkPwdEmpty();
                }
            }
        });

        mEditTextOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextOldPwd.getText().toString().isEmpty()) {
                    mEditTextOldPwd.setError(getString(R.string.u06_05_old_password_empty));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEditTextNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextNewPwd.getText().toString().isEmpty()) {
                    mEditTextNewPwd.setError(getString(R.string.u06_05_new_password_empty));
                } else if (mEditTextNewPwd.getText().toString().length() < 6) {
                    mEditTextNewPwd.setError(getString(R.string.uc_password_invalid));
                } else {
                    mEditTextNewPwd.setError(null);
                }
                checkPwd();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mTextViewCheckPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPwd();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    void checkPwd() {
        if (mEditTextNewPwd.getText().toString().equals(mTextViewCheckPwd.getText().toString())) {
            mTextViewCheckPwd.setError(null);
        } else {
            mTextViewCheckPwd.setError(getString(R.string.uc_password_unmatch));
        }
    }

    void checkPwdEmpty() {
        if (mEditTextOldPwd.getText().toString().isEmpty()) {
            mEditTextOldPwd.setError(getString(R.string.u06_05_old_password_empty));
        }
        if (mEditTextNewPwd.getText().toString().isEmpty()) {
            mEditTextNewPwd.setError(getString(R.string.u06_05_new_password_empty));
        }
        if (mTextViewCheckPwd.getText().toString().isEmpty()) {
            mTextViewCheckPwd.setError(getString(R.string.u06_05_new_password_empty));
        }
    }

    public void updatePassword() {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("oldPwd", mEditTextOldPwd.getText().toString());
            json.put("newPwd", mEditTextNewPwd.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_UPDATE_PWD)
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
                                if (responseJSON.getInt("res") == SUCCESSFUL_UPDATE_PASSWORD) {
                                    msg(SUCCESSFUL_UPDATE_PASSWORD);
                                } else if (responseJSON.getInt("res") == ERROR_OLD_PASSWORD) {
                                    msg(ERROR_OLD_PASSWORD);
                                } else {
                                    msg(ERROR_CONNECT);
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

    public void msg(final int type) {
        final FullScreenDialog dialog=new FullScreenDialog(getContext(),R.layout.dialog_base);
        FrameLayout layout=dialog.findViewById(R.id.frameLayout_dialog_base);
        TextView title=dialog.findViewById(R.id.textView_dialog_base_title);
        TextView msg=dialog.findViewById(R.id.textView_dialog_base_msg);
        Button btnYes=dialog.findViewById(R.id.btn_dialog_base_yes);
        title.setText(getString(R.string.uc_update_fail));
        switch (type) {
            case SUCCESSFUL_UPDATE_PASSWORD:
                msg.setText(getString(R.string.u06_05_password_update_successful));
                dialog.setCancelable(false);
                break;
            case ERROR_OLD_PASSWORD:
                msg.setText(getString(R.string.u06_05_old_password_unmatch));
                break;
            case ERROR_CONNECT:
                msg.setText(getString(R.string.u06_05_password_update_fail));
                break;
        }
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==SUCCESSFUL_UPDATE_PASSWORD){
                    dialog.dismiss();
                    cleanValue();
                }else {
                    dialog.dismiss();
                }
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==SUCCESSFUL_UPDATE_PASSWORD){
                    dialog.dismiss();
                    cleanValue();
                }else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void cleanValue() {
        prefs.edit().clear().apply();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
