package com.sctw.bonniedraw.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
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
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_USER_INFO_QUERY)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showTSnackbar("連線失敗");
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
                                    try {
                                        //URL profilePicUrl = new URL(responseJSON.getString("profilePicture"));
                                        URL profilePicUrl = new URL(prefs.getString(GlobalVariable.userImgUrlStr, "null"));
                                        Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                                        mImgViewPhoto.setImageBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                showTSnackbar("連線失敗");
                            }
                            Log.d("RESTFUL API : ", responseJSON.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    void updateUserInfo() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("userType", prefs.getString(GlobalVariable.userPlatformStr, "null"));
            json.put("userCode", prefs.getString(GlobalVariable.userEmailStr, "null"));
            json.put("userName", mEditTextName.getText().toString());
            json.put("nickName", mEditTextNickName.getText().toString());
            json.put("description", mEditTextProfile.getText().toString());
            json.put("phoneNo", mEditPhone.getText().toString());
            if (mIntGender != null) json.put("gender", String.valueOf(mIntGender));
            Log.d("JSON",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, json.toString());
        final Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_USER_INFO_UPDATE)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showTSnackbar("更新失敗");
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
                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setMessage(getString(R.string.public_update_successful));
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        getUserInfo();
                                    }
                                });
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                            } else {
                                showTSnackbar("更新失敗");
                            }
                            Log.d("RESTFUL API : ", responseJSON.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    void showTSnackbar(String string){
        TSnackbar snackbar = TSnackbar.make(getView().findViewById(R.id.coordinatorLayout_snackbar_content), "", TSnackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#ff5722"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setText(string);
        snackbar.show();
    }
}
