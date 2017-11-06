package com.sctw.bonniedraw.utility;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;

/**
 * Created by Fatorin on 2017/10/20.
 */

public class ConnectJson {
    //所有與API連線的JSON格式都在這裡
    public final static MediaType MEDIA_TYPE_JSON_UTF8 = MediaType.parse("application/json; charset=utf-8");

    //登入用
    public static JSONObject loginJson(SharedPreferences prefs, int type) {
        //type 3 = third login , 1 = email login
        JSONObject json = new JSONObject();
        try {
            switch (type) {
                case 3:
                    if (prefs.getString(GlobalVariable.userPlatformStr, "").equals(GlobalVariable.THIRD_LOGIN_FACEBOOK)) {
                        json.put("uc", prefs.getString(GlobalVariable.userFbIdStr, ""));
                    } else {
                        json.put("uc", prefs.getString(GlobalVariable.userEmailStr, ""));
                    }
                    json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                    json.put("un", prefs.getString(GlobalVariable.userNameStr, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_LOGIN);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.userEmailStr, ""));
                    json.put("thirdPictureUrl", prefs.getString(GlobalVariable.userImgUrlStr, ""));
                    break;
                case 1:
                    json.put("uc", prefs.getString(GlobalVariable.userEmailStr, "null"));
                    json.put("up", prefs.getString("emailLoginPwd", "null"));
                    json.put("ut", GlobalVariable.EMAIL_LOGIN);
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_LOGIN);
                    break;
            }
            Log.d("JSON DATA", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    //搜尋個人資料用
    public static JSONObject queryUserInfoJson(SharedPreferences prefs) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    //更新個人資料用
    public static JSONObject updateUserInfoJson(SharedPreferences prefs, String userName, String nickName, String description, String phoneNo, String gender) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("userType", prefs.getString(GlobalVariable.userPlatformStr, "null"));
            json.put("userCode", prefs.getString(GlobalVariable.userEmailStr, "null"));
            json.put("userName", userName);
            json.put("nickName", nickName);
            json.put("description", description);
            json.put("phoneNo", phoneNo);
            if (!gender.isEmpty()) json.put("gender", String.valueOf(gender));
            Log.d("JSON", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject querySingleWork(SharedPreferences prefs, int wid) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("wid", wid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject queryListWork(SharedPreferences prefs, int wt, int stn, int rc) {
        //wt = 作品類別 , stn = 起始數 , rc = 筆數
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("wid", 0);
            json.put("wt", 4);
            json.put("stn", 0);
            json.put("rc", 100);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject setLike(SharedPreferences prefs,int fn,int wid){
        JSONObject json = new JSONObject();
        // fn  1=讚  2=取消讚
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("fn",fn);
            json.put("worksId", wid);
            if(fn==1){
                json.put("likeType",1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject deleteWork(SharedPreferences prefs,int wid){
        JSONObject json = new JSONObject();
        // fn  1=讚  2=取消讚
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("worksId", wid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
