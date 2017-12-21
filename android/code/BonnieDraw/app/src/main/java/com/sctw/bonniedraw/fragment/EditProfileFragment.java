package com.sctw.bonniedraw.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends DialogFragment implements RequestListener<Drawable> {
    Button mBtnDone, mBtnCancel;
    TextView mTextViewChangePhoto;
    CircleImageView mImgViewPhoto;
    EditText mEditTextName, mEditTextProfile, mEditTextEmail, mEditPhone, mEditGender;
    SharedPreferences prefs;
    Integer mIntGender;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
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
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mBtnCancel = view.findViewById(R.id.btn_edit_form_cancel);
        mBtnDone = view.findViewById(R.id.btn_edit_form_done);
        mTextViewChangePhoto = view.findViewById(R.id.textView_edit_user_photo);
        mImgViewPhoto = view.findViewById(R.id.imgView_edit_user_photo);
        mImgViewPhoto.setDrawingCacheEnabled(true);
        mEditTextName = view.findViewById(R.id.editText_edit_name);
        mEditTextProfile = view.findViewById(R.id.editText_edit_profile);
        mEditTextEmail = view.findViewById(R.id.editText_edit_email);
        mEditPhone = view.findViewById(R.id.editText_edit_phone);
        mEditGender = view.findViewById(R.id.editText_edit_gender);
        setOnClick();
        getUserInfo();
    }

    void setOnClick() {
        mEditGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FullScreenDialog dialog = new FullScreenDialog(getContext(), R.layout.dialog_gender_select);
                RelativeLayout layout = dialog.findViewById(R.id.relativeLayout_gender_select);
                Button btnNone = dialog.findViewById(R.id.btn_gender_none);
                Button btnMale = dialog.findViewById(R.id.btn_gender_male);
                Button btnFemale = dialog.findViewById(R.id.btn_gender_famale);
                Button btnCancel = dialog.findViewById(R.id.btn_gender_cancel);
                btnNone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEditGender.setText(getString(R.string.not_select_gender));
                        mIntGender=0;
                        dialog.dismiss();
                    }
                });
                btnMale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEditGender.setText(getString(R.string.male));
                        mIntGender=1;
                        dialog.dismiss();
                    }
                });
                btnFemale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEditGender.setText(getString(R.string.female));
                        mIntGender=2;
                        dialog.dismiss();
                    }
                });
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        mTextViewChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)), 1);
            }
        });

        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment.this.dismiss();
            }
        });
    }

    void getUserInfo() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.queryUserInfoJson(prefs);
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.createToastWindow(getContext(), getString(R.string.connect_fail), PxDpConvert.getSystemHight(getContext()) / 3);
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
                                prefs.edit().putString(GlobalVariable.USER_NAME_STR, responseJSON.getString("userName")).apply();
                                mEditTextName.setText(responseJSON.getString("userName"));

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
                                    int x = responseJSON.getInt("gender");   //0 not , 1=male, 2=female
                                    switch (x) {
                                        case 0:
                                            mEditGender.setText(R.string.not_select_gender);
                                            mIntGender = 0;
                                            break;
                                        case 1:
                                            mIntGender = 1;
                                            mEditGender.setText(R.string.male);
                                            break;
                                        case 2:
                                            mEditGender.setText(R.string.female);
                                            mIntGender = 2;
                                            break;
                                    }
                                } else {
                                    mEditGender.setText(R.string.not_select_gender);
                                    mIntGender = 0;
                                }

                                String profileUrl = "";
                                if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                    profileUrl = GlobalVariable.API_LINK_GET_FILE + responseJSON.getString("profilePicture");
                                    prefs.edit().putString(GlobalVariable.USER_IMG_URL_STR, responseJSON.getString("profilePicture")).apply();
                                }
                                Glide.with(getContext())
                                        .load(profileUrl)
                                        .apply(GlideAppModule.getUserOptions())
                                        .into(mImgViewPhoto);
                            } else {
                                ToastUtil.createToastWindow(getContext(), getString(R.string.connect_fail), PxDpConvert.getSystemHight(getContext()) / 3);
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

        String gender = "";
        if (mIntGender != null) gender = String.valueOf(mIntGender);
        JSONObject json = ConnectJson.updateUserInfoJson(
                prefs,
                mEditTextName.getText().toString(),
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
                ToastUtil.createToastWindow(getContext(), getString(R.string.connect_fail), PxDpConvert.getSystemHight(getContext()) / 3);
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
                                ToastUtil.createToastWindow(getContext(), getString(R.string.update_successful), PxDpConvert.getSystemHight(getContext()) / 3);
                                EditProfileFragment.this.dismiss();
                            } else {
                                ToastUtil.createToastWindow(getContext(), getString(R.string.update_fail), PxDpConvert.getSystemHight(getContext()) / 3);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            // action cancelled
        }
        if (resultCode == RESULT_OK) {
            Uri selectedimg = data.getData();
            Glide.with(getContext()).load(selectedimg).listener(this).into(mImgViewPhoto);
        }
    }

    public void uploadFile() {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM)
                .addFormDataPart("ui", prefs.getString(GlobalVariable.API_UID, "null"))
                .addFormDataPart("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"))
                .addFormDataPart("dt", GlobalVariable.LOGIN_PLATFORM)
                .addFormDataPart("fn", "2")
                .addFormDataPart("ftype", "1");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mImgViewPhoto.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, bos); //bm is the bitmap object
        bodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.png\""), RequestBody.create(MediaType.parse("image/png"), bos.toByteArray()));

        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_UPLOAD)
                .post(bodyBuilder.build())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Upload File", "Fail");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = null;
                            responseJSON = new JSONObject(response.body().string());
                            if (responseJSON.getInt("res") == 1) {
                                Log.d("上傳圖片", "成功");
                                ToastUtil.createToastIsCheck(getContext(), getString(R.string.update_successful), true, PxDpConvert.getSystemHight(getActivity()) / 3);
                            } else {
                                ToastUtil.createToastIsCheck(getContext(), getString(R.string.update_fail), false, PxDpConvert.getSystemHight(getActivity()) / 3);
                            }
                            System.out.println(responseJSON.toString());
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        mImgViewPhoto.setImageDrawable(resource);
        uploadFile();
        return false;
    }
}
