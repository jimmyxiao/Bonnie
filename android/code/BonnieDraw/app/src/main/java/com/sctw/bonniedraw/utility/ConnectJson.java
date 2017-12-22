package com.sctw.bonniedraw.utility;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Fatorin on 2017/10/20.
 */

public class ConnectJson {
    //所有與API連線的JSON格式都在這裡
    public final static MediaType MEDIA_TYPE_JSON_UTF8 = MediaType.parse("application/json; charset=utf-8");

    //登入用
    public static Request loginJson(SharedPreferences prefs, int type) {
        //type 3 = third login , 1 = email login
        JSONObject json = new JSONObject();
        try {
            switch (type) {
                case 3:
                    int platform = prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0);
                    if (platform != GlobalVariable.EMAIL_LOGIN) {
                        json.put("uc", prefs.getString(GlobalVariable.USER_THIRD_ID_STR, ""));
                    } else {
                        json.put("uc", prefs.getString(GlobalVariable.USER_EMAIL_STR, ""));
                    }
                    json.put("ut", prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0));
                    json.put("un", prefs.getString(GlobalVariable.USER_NAME_STR, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_LOGIN);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.USER_EMAIL_STR, ""));
                    json.put("thirdPictureUrl", prefs.getString(GlobalVariable.USER_IMG_URL_STR, ""));
                    json.put("token", prefs.getString(GlobalVariable.USER_FCM_TOKEN_STR, ""));
                    json.put("deviceId", prefs.getString(GlobalVariable.USER_DEVICE_ID_STR, ""));
                    break;
                case 1:
                    json.put("uc", prefs.getString(GlobalVariable.USER_EMAIL_STR, "null"));
                    json.put("up", prefs.getString(GlobalVariable.USER_PWD_STR, "null"));
                    json.put("ut", GlobalVariable.EMAIL_LOGIN);
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_LOGIN);
                    json.put("token", prefs.getString(GlobalVariable.USER_FCM_TOKEN_STR, ""));
                    json.put("deviceId", prefs.getString(GlobalVariable.USER_DEVICE_ID_STR, ""));
                    break;
            }
            Log.d("JSON DATA", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LOGIN)
                .post(body)
                .build();
        return request;
    }

    //更新作品資料
    public static Request updateWorksave(SharedPreferences prefs, int privacyType, String title, String description, int worksId) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("ac", 2);
            json.put("privacyType", privacyType);
            json.put("title", title);
            json.put("description", description);
            json.put("worksId", worksId);
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_SAVE)
                .post(body)
                .build();
        return request;
    }

    //搜尋個人資料用
    public static Request queryUserInfoJson(SharedPreferences prefs) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("type", 0);
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_USER_INFO_QUERY)
                .post(body)
                .build();
        return request;
    }

    public static Request queryOtherUserInfoJson(SharedPreferences prefs, int queryId) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("type", 1);
            json.put("queryId", queryId);
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_USER_INFO_QUERY)
                .post(body)
                .build();
        return request;
    }

    //更新個人資料用
    public static JSONObject updateUserInfoJson(SharedPreferences prefs, String userName, String description, String phoneNo, String gender) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("userType", prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0));
            json.put("userCode", prefs.getString(GlobalVariable.USER_EMAIL_STR, "null"));
            json.put("userName", userName);
            json.put("description", description);
            json.put("phoneNo", phoneNo);
            if (!gender.isEmpty()) json.put("gender", String.valueOf(gender));
            Log.d("JSON", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Request querySingleWork(SharedPreferences prefs, int wid) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("wid", wid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_LIST)
                .post(body)
                .build();
        return request;
    }

    public static Request queryListWorkAdvanced(SharedPreferences prefs, int wt, int stn, int rc, String input) {
        //wt = 作品類別 , stn = 起始數 , rc = 筆數
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("wid", 0);
            json.put("wt", wt);
            json.put("stn", stn);
            json.put("rc", rc);
            if (wt == 8) {
                json.put("tagName", input);
            } else if (wt == 9) {
                json.put("search", input);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(json.toString());
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_LIST)
                .post(body)
                .build();
        return request;
    }

    public static Request queryListWork(SharedPreferences prefs, int wt, int stn, int rc) {
        //wt = 作品類別 , stn = 起始數 , rc = 筆數
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("wid", 0);
            json.put("wt", wt);
            json.put("stn", stn);
            json.put("rc", rc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(json.toString());
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_LIST)
                .post(body)
                .build();
        return request;
    }

    public static Request queryListWorkOther(SharedPreferences prefs, int wt, int stn, int rc, int queryId) {
        //wt = 作品類別 , stn = 起始數 , rc = 筆數
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("wid", 0);
            json.put("wt", wt);
            json.put("stn", stn);
            json.put("rc", rc);
            json.put("queryId", queryId);
            System.out.println(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_LIST)
                .post(body)
                .build();
        return request;
    }

    public static Request setLike(SharedPreferences prefs, int fn, int wid) {
        JSONObject json = new JSONObject();
        // fn  1=讚  0=取消讚
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("fn", fn);
            json.put("worksId", wid);
            json.put("likeType", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_SET_LIKE)
                .post(body)
                .build();
        return request;

    }

    public static Request setCollection(SharedPreferences prefs, int fn, int wid) {
        JSONObject json = new JSONObject();
        // fn  1=讚  0=取消讚
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("fn", fn);
            json.put("worksId", wid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_SET_COLLECTION)
                .post(body)
                .build();
        return request;
    }

    public static Request setFollow(SharedPreferences prefs, int fn, int followingUserId) {
        JSONObject json = new JSONObject();
        // fn  1=讚  0=取消讚
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("fn", fn);
            json.put("followingUserId", followingUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_SET_FOLLOW)
                .post(body)
                .build();
        return request;
    }

    public static JSONObject deleteWork(SharedPreferences prefs, int wid) {
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

    public static Request leaveMsg(SharedPreferences prefs, int wid, String msg) {
        JSONObject json = new JSONObject();
        try {
            //fn 1= add , 0= delete
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("fn", 1);
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("worksId", wid);
            json.put("message", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LEAVE_MESSAGE)
                .post(body)
                .build();
        return request;
    }

    public static Request deleteLeaveMsg(SharedPreferences prefs, int wid, int msgId) {
        JSONObject json = new JSONObject();
        try {
            //fn 1= add , 0= delete
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("fn", 0);
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("worksId", wid);
            json.put("msgId", msgId);
            System.out.println("DELETE MSG JSON=" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LEAVE_MESSAGE)
                .post(body)
                .build();
        return request;
    }

    public static Request forgetPwd(String email) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_FORGET_PWD)
                .post(body)
                .build();
        return request;
    }

    public static JSONObject getNotice(SharedPreferences prefs, int notiMsgId) {
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("notiMsgId", notiMsgId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Request reportWork(SharedPreferences prefs, int workId, int turnInType, String description) {
        // turnInType 1:內容色情  2:內容暴力
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("workId", workId);
            json.put("turnInType", turnInType);
            json.put("description", description);
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_REPOT_WORK)
                .post(body)
                .build();
        return request;
    }

    public static Request queryFansOrFollow(SharedPreferences prefs, int ui, int fn) {
        //wt = 作品類別 , stn = 起始數 , rc = 筆數
        JSONObject json = new JSONObject();
        try {
            json.put("ui", ui);
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("fn", fn);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_FOLLOW_LIST_LINK)
                .post(body)
                .build();
        return request;
    }

    public static Request queryFriends(SharedPreferences prefs, JSONArray uidList) {
        //wt = 作品類別 , stn = 起始數 , rc = 筆數
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, ""));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("thirdPlatform", prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0));
            json.put("uidList", uidList);
            System.out.println(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_FRIENDS_LINK)
                .post(body)
                .build();
        return request;
    }

    public static Request getTagList(SharedPreferences prefs, int tagId) {
        //wt = 作品類別 , stn = 起始數 , rc = 筆數
        JSONObject json = new JSONObject();
        try {
            json.put("ui", prefs.getString(GlobalVariable.API_UID, ""));
            json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("tagId", tagId);
            json.put("countryCode", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_TAG_LIST)
                .post(body)
                .build();
        return request;
    }
}
