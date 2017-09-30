package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
public class ProfileFragment extends Fragment {
    private ImageView profilePhoto;
    private TextView profileUserName, profileUserId, profileWorks, profileUserFans, profileUserFollow;
    private ImageButton profileSettingBtn, profileGridBtn, profileListBtn;
    Button profileEditBtn;
    RecyclerView profileRecyclerView;
    ArrayList<String> myDataset;
    ListAdapter mAdapter;
    HomeAdapter homeAdapter;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager layoutManager;
    SharedPreferences prefs;
    FragmentManager fm;
    FragmentTransaction ft;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        profilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
        profileUserName = (TextView) view.findViewById(R.id.profile_userName);
        profileUserId = (TextView) view.findViewById(R.id.profile_userId);
        profileWorks = (TextView) view.findViewById(R.id.profile_userWorks);
        profileUserFollow = (TextView) view.findViewById(R.id.profile_userFans);
        profileUserFans = (TextView) view.findViewById(R.id.profile_userFollow);
        profileSettingBtn = (ImageButton) view.findViewById(R.id.profile_setting_btn);
        profileGridBtn = (ImageButton) view.findViewById(R.id.profile_grid_btn);
        profileListBtn = (ImageButton) view.findViewById(R.id.profile_list_btn);
        profileEditBtn=view.findViewById(R.id.btn_edit_profile);
        updateProfileInfo();
        fm=getFragmentManager();


        profileSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
            }
        });

        profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft=fm.beginTransaction();
                ft.replace(R.id.main_actitivy_layout,new EditProfileFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        myDataset = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            myDataset.add(Integer.toString(i));
        }
        profileRecyclerView = (RecyclerView) view.findViewById(R.id.profile_recyclerview);
        mAdapter = new ListAdapter(myDataset);
        profileRecyclerView.setLayoutManager(gridLayoutManager);
        profileRecyclerView.setAdapter(mAdapter);

        profileGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileRecyclerView.setLayoutManager(gridLayoutManager);
                profileRecyclerView.setAdapter(mAdapter);
            }
        });

        profileListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeAdapter = new HomeAdapter(myDataset);
                profileRecyclerView.setLayoutManager(layoutManager);
                profileRecyclerView.setAdapter(homeAdapter);
            }
        });
    }

    void updateProfileInfo() {
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
        final Request request = new Request.Builder()
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
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseStr);
                            if (responseJSON.getInt("res") == 1) {
                                //Successful
                                profileUserName.setText(responseJSON.getString("userName"));

                                if (responseJSON.has("nickName") && !responseJSON.isNull("nickName")) {
                                    profileUserId.setText(responseJSON.getString("nickName"));
                                } else {
                                    profileUserId.setText("");
                                }

                                if (responseJSON.has("profilePicture") && !responseJSON.isNull("profilePicture")) {
                                    try {
                                        //URL profilePicUrl = new URL(responseJSON.getString("profilePicture"));
                                        URL profilePicUrl = new URL(prefs.getString(GlobalVariable.userImgUrlStr,"null"));
                                        Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                                        profilePhoto.setImageBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    profilePhoto.setBackgroundColor(Color.BLACK);
                                }

                                Log.d("RESTFUL API : ", responseJSON.toString());
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

    //Two Adapter
    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private List<String> mData;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.card_textview);
            }
        }

        public ListAdapter(List<String> data) {
            mData = data;
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photocard_layout, parent, false);
            ListAdapter.ViewHolder vh = new ListAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position) {
            holder.mTextView.setText(mData.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getActivity(), "Item " + position + " is long clicked.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
        List<String> data;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.home_number_text);
            }
        }

        public HomeAdapter(List<String> data) {
            this.data = data;
        }

        @Override
        public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card, parent, false);
            HomeAdapter.ViewHolder vh = new HomeAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final HomeAdapter.ViewHolder holder, int position) {
            holder.mTextView.setText(data.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Item " + holder.getAdapterPosition() + " is clicked.", Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getActivity(), "Item " + holder.getAdapterPosition() + " is long clicked.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

}
