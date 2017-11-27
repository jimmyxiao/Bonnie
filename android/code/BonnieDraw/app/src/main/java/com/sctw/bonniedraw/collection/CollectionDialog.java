package com.sctw.bonniedraw.collection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sctw.bonniedraw.R;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class CollectionDialog extends DialogFragment implements View.OnClickListener {
    private Button mBtnBack, mBtnAdd;
    private EditText mEtClass;
    private boolean mbCheck;
    private LinearLayout mLlInputClass;

    public void CollectionDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View rootView = inflater.inflate(R.layout.dialog_add_collection, container, false);
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
        mBtnBack = view.findViewById(R.id.btn_add_collection_cancel);
        mBtnAdd = view.findViewById(R.id.btn_add_collection_next);
        mEtClass = view.findViewById(R.id.editText_add_collection);
        mLlInputClass = view.findViewById(R.id.linearLayout_add_collection);
        mBtnAdd.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mEtClass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mEtClass.setError("請填寫該欄位");
                    mBtnAdd.setTextColor(ContextCompat.getColor(getContext(), R.color.HintColor));
                    mbCheck = false;
                } else {
                    mEtClass.setError(null);
                    mBtnAdd.setTextColor(ContextCompat.getColor(getContext(), R.color.Black));
                    mbCheck = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_collection_cancel:
                dismiss();
                break;
            case R.id.btn_add_collection_next:
                break;
        }
    }
}
