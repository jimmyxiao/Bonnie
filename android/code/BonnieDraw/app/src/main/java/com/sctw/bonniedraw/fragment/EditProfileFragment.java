package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.TSnackbarCall;

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
public class EditProfileFragment extends Fragment {
    Button mBtnDone, mBtnCancel;
    TextView mTextViewChangePhoto;
    ImageView mImgViewPhoto;
    EditText mEditTextName, mEditTextNickName, mEditTextProfile, mEditTextEmail, mEditPhone;
    RadioGroup mRadioGroupGender;
    SharedPreferences prefs;
    Integer mIntGender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mBtnCancel = view.findViewById(R.id.btn_edit_form_cancel);
        mBtnDone = view.findViewById(R.id.btn_edit_form_done);
        mTextViewChangePhoto = view.findViewById(R.id.textView_edit_user_photo);
        mImgViewPhoto = view.findViewById(R.id.imgView_edit_user_photo);
        mEditTextName = view.findViewById(R.id.editText_edit_name);
        mEditTextNickName = view.findViewById(R.id.editText_edit_user_nickname);
        mEditTextProfile = view.findViewById(R.id.editText_edit_profile);
        mEditTextEmail = view.findViewById(R.id.editText_edit_email);
        mEditPhone = view.findViewById(R.id.editText_edit_phone);
        mRadioGroupGender = view.findViewById(R.id.radioGroup_edit_gender);

        //連線抓資料 取代資料完成後覆蓋資料
        mRadioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.radioBtn_edit_gender_m:
                        mIntGender = 1;
                        break;
                    case R.id.radioBtn_edit_gender_f:
                        mIntGender = 2;
                        break;
                    case R.id.radioBtn_edit_gender_o:
                        mIntGender = 0;
                        break;
                }
            }
        });

        getUserInfo();
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    void getUserInfo() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        JSONObject json = ConnectJson.queryUserInfoJson(prefs);
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_USER_INFO_QUERY)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                TSnackbarCall.showTSnackbar(getView().findViewById(R.id.coordinatorLayout_edit_profile),"連線失敗");
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                final String responseStr = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseStr);
                            if (responseJSON.getInt("res") == 1) {
                                //Successful
                                mEditTextName.setText(responseJSON.getString("userName"));

                                if (responseJSON.has("nickName") && !responseJSON.isNull("nickName")) {
                                    mEditTextNickName.setText(responseJSON.getString("nickName"));
                                } else {
                                    mEditTextNickName.setHint("");
                                }

                                if (responseJSON.has("description") && !responseJSON.isNull("description")) {
                                    mEditTextProfile.setText(responseJSON.getString("description"));
                                } else {
                                    mEditTextProfile.setHint("");
                                }

                                mEditTextEmail.setText(responseJSON.getString("email"));

                                if (responseJSON.has("phoneNo") && !responseJSON.isNull("phoneNo")) {
                                    mEditPhone.setText(responseJSON.getString("phoneNo"));
                                } else {
                                    mEditPhone.setHint("");
                                }

                                if (responseJSON.has("gender") && !responseJSON.isNull("gender")) {
                                    int x = responseJSON.getInt("gender");
                                    switch (x) {
                                        case 0:
                                            mRadioGroupGender.check(R.id.radioBtn_edit_gender_o);
                                            mIntGender = 0;
                                            break;
                                        case 1:
                                            mRadioGroupGender.check(R.id.radioBtn_edit_gender_m);
                                            mIntGender = 1;
                                            break;
                                        case 2:
                                            mRadioGroupGender.check(R.id.radioBtn_edit_gender_f);
                                            mIntGender = 2;
                                            break;
                                    }
                                }

                                if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                    //URL profilePicUrl = new URL(responseJSON.getString("profilePicture"));
                                    ImageLoader.getInstance().displayImage(prefs.getString(GlobalVariable.USER_IMG_URL_STR, "null"),mImgViewPhoto);
                                }else {
                                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.photo_round,mImgViewPhoto);
                                }
                            } else {
                                TSnackbarCall.showTSnackbar(getView().findViewById(R.id.coordinatorLayout_edit_profile),"連線失敗");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    void updateUserInfo() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();

        String gender="";
        if (mIntGender != null) gender=String.valueOf(mIntGender);
        JSONObject json=ConnectJson.updateUserInfoJson(
                prefs,
                mEditTextName.getText().toString(),
                mEditTextNickName.getText().toString(),
                mEditTextProfile.getText().toString(),
                mEditPhone.getText().toString(),
                gender);

        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        final Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_USER_INFO_UPDATE)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                TSnackbarCall.showTSnackbar(getView().findViewById(R.id.coordinatorLayout_edit_profile),"更新失敗");
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

                                TSnackbarCall.showTSnackbar(getView().findViewById(R.id.coordinatorLayout_edit_profile),getString(R.string.public_update_successful));
                                getUserInfo();
                            } else {
                                TSnackbarCall.showTSnackbar(getView().findViewById(R.id.coordinatorLayout_edit_profile),"更新失敗");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
