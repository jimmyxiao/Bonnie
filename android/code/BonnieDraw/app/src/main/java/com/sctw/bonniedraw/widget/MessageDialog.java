package com.sctw.bonniedraw.widget;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.MsgAdapter;
import com.sctw.bonniedraw.utility.Msg;

import java.util.ArrayList;

/**
 * Created by Fatorin on 2017/11/7.
 */

public class MessageDialog extends DialogFragment implements View.OnClickListener {
    ImageButton mBtnBack;
    RecyclerView mRv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
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
        mBtnBack = view.findViewById(R.id.imgBtn_message_back);
        mBtnBack.setOnClickListener(this);
        mRv = view.findViewById(R.id.recyclerview_message);
        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        ArrayList<Msg> msgArrayList=new ArrayList<>();
        for(int x=0;x<=5;x++){
            msgArrayList.add(new Msg());
        }

        MsgAdapter adapter = new MsgAdapter(msgArrayList);
        mRv.setLayoutManager(lm);
        mRv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtn_message_back:
                this.dismiss();
                break;
        }
    }
}
