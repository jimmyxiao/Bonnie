package com.sctw.bonniedraw.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

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
    Button editFormDone, editFormCancel;
    TextView editFormChangePhoto;
    ImageView editFormUserPhoto;
    EditText editFormName, editFormUserNickName, editFormProfile, editFormEmail, editFormPhone;
    RadioGroup editFormGender;
    SharedPreferences prefs;
    Integer edit_gender;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        editFormCancel = view.findViewById(R.id.edit_form_cancel);
        editFormDone = view.findViewById(R.id.edit_form_done);
        editFormChangePhoto = view.findViewById(R.id.edit_form_change_photo);
        editFormUserPhoto = view.findViewById(R.id.edit_form_user_photo);
        editFormName = view.findViewById(R.id.edit_form_name);
        editFormUserNickName = view.findViewById(R.id.edit_form_user_nickname);
        editFormProfile = view.findViewById(R.id.edit_form_profile);
        editFormEmail = view.findViewById(R.id.edit_form_email);
        editFormPhone = view.findViewById(R.id.edit_form_phone);
        editFormGender = view.findViewById(R.id.edit_form_gender_group);
        //連線抓資料 取代資料完成後覆蓋資料
        editFormGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.edit_form_gender_m:
                        edit_gender = 1;
                        break;
                    case R.id.edit_form_gender_f:
                        edit_gender = 2;
                        break;
                    case R.id.edit_form_gender_o:
                        edit_gender = 0;
                        break;
                }
            }
        });

        getUserInfo();
        editFormDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        editFormCancel.setOnClickListener(new View.OnClickListener() {
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
                .url("https://www.bonniedraw.com/bonniedraw_service/BDService/userInfoQuery")
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "連線錯誤", Toast.LENGTH_SHORT).show();
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
                                editFormName.setText(responseJSON.getString("userName"));

                                if (responseJSON.has("nickName") && !responseJSON.isNull("nickName")) {
                                    editFormUserNickName.setText(responseJSON.getString("nickName"));
                                } else {
                                    editFormUserNickName.setHint("尚未設置");
                                }

                                if (responseJSON.has("description") && !responseJSON.isNull("description")) {
                                    editFormProfile.setText(responseJSON.getString("description"));
                                } else {
                                    editFormProfile.setHint("尚未設置");
                                }

                                editFormEmail.setText(responseJSON.getString("email"));

                                if (responseJSON.has("phoneNo") && !responseJSON.isNull("phoneNo")) {
                                    editFormPhone.setText(responseJSON.getString("phoneNo"));
                                } else {
                                    editFormPhone.setHint("尚未設置");
                                }

                                if (responseJSON.has("gender") && !responseJSON.isNull("gender")) {
                                    int x = responseJSON.getInt("gender");
                                    switch (x) {
                                        case 0:
                                            editFormGender.check(R.id.edit_form_gender_o);
                                            edit_gender = 0;
                                            break;
                                        case 1:
                                            editFormGender.check(R.id.edit_form_gender_m);
                                            edit_gender = 1;
                                            break;
                                        case 2:
                                            editFormGender.check(R.id.edit_form_gender_f);
                                            edit_gender = 2;
                                            break;
                                    }

                                }

                                if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                    try {
                                        //URL profilePicUrl = new URL(responseJSON.getString("profilePicture"));
                                        URL profilePicUrl = new URL(prefs.getString(GlobalVariable.userImgUrlStr, "null"));
                                        Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                                        editFormUserPhoto.setImageBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "連線失敗", Toast.LENGTH_SHORT).show();
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
            json.put("userName", editFormName.getText().toString());
            json.put("nickName", editFormUserNickName.getText().toString());
            json.put("description", editFormProfile.getText().toString());
            json.put("phoneNo", editFormPhone.getText().toString());
            if (edit_gender != null) json.put("gender", String.valueOf(edit_gender));
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, json.toString());
        final Request request = new Request.Builder()
                .url("https://www.bonniedraw.com/bonniedraw_service/BDService/userInfoUpdate")
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "更新失敗", Toast.LENGTH_SHORT).show();
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
                                alertDialog.setMessage("Update Successful");
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
                                Toast.makeText(getActivity(), "更新失敗", Toast.LENGTH_SHORT).show();
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
}
