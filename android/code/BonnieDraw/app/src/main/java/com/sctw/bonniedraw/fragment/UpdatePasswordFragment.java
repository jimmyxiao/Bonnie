package com.sctw.bonniedraw.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.activity.LoginActivity;
import com.sctw.bonniedraw.utility.ConnectJson;
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
public class UpdatePasswordFragment extends Fragment {
    Button updatePasswordCancel, updatePasswordDone;
    SharedPreferences prefs;
    EditText mEditTextOldPwd, mEditTextNewPwd, mTextViewCheckPwd;
    final static int SUCCESSFUL_UPDATE_PASSWORD =1;
    final static int ERROR_OLD_PASSWORD =4;
    final static int ERROR_CONNECT =0;
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
                getActivity().onBackPressed();
            }
        });

        updatePasswordDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPwd = mEditTextOldPwd.getText().toString();
                String newPwd = mEditTextNewPwd.getText().toString();
                String checkPwd = mTextViewCheckPwd.getText().toString();
                if (newPwd.equals(checkPwd)&&oldPwd.trim().length()!=0) {
                    updatePassword();
                } else {
                    checkPwdEmpty();
                }
            }
        });

        mEditTextNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextNewPwd.getText().toString().length() < 6) {
                    mEditTextNewPwd.setError(getString(R.string.update_password_need_length));
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

    void checkPwd(){
        if (mEditTextNewPwd.getText().toString().equals(mTextViewCheckPwd.getText().toString())) {
            mTextViewCheckPwd.setError(null);
        } else {
            mTextViewCheckPwd.setError(getString(R.string.update_password_not_equal));
        }
    }

    void checkPwdEmpty(){
        if (mEditTextOldPwd.getText().toString().isEmpty()) {
            mEditTextOldPwd.setError(getString(R.string.update_password_old_need));
        }
        if (mEditTextNewPwd.getText().toString().isEmpty()) {
            mEditTextNewPwd.setError(getString(R.string.update_password_new_need));
        }
        if (mTextViewCheckPwd.getText().toString().isEmpty()) {
            mTextViewCheckPwd.setError(getString(R.string.update_password_check_need));
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
            Log.d("JSON DATA", json.toString());
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
                Toast.makeText(getActivity(), "連線失敗", Toast.LENGTH_SHORT).show();
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
                                } else if(responseJSON.getInt("res") == ERROR_OLD_PASSWORD) {
                                    msg(ERROR_OLD_PASSWORD);
                                }else{
                                    msg(ERROR_CONNECT);
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

    public void msg(int type){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        switch (type){
            case SUCCESSFUL_UPDATE_PASSWORD:
                builder.setMessage(R.string.update_password_successful);
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        cleanValue();
                    }
                });
                break;
            case ERROR_OLD_PASSWORD:
                builder.setMessage(R.string.update_password_old_error);
                builder.setPositiveButton(R.string.public_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                break;
            case ERROR_CONNECT:
                builder.setMessage(R.string.update_password_other_error);
                builder.setPositiveButton(R.string.public_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                break;
        }
        builder.create().show();
    }

    public void cleanValue() {
        prefs.edit().clear().apply();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
