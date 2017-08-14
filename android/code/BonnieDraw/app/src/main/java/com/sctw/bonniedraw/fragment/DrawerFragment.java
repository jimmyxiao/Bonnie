package com.sctw.bonniedraw.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

/**
 * Created by professor on 7/15/16.
 */
public class DrawerFragment extends Fragment {
    private Delegate delegate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        delegate = (Delegate) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            ((TextView) view.findViewById(R.id.versionName)).setText(String.format("version: %s", packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        view.findViewById(R.id.updateApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.drawerFragmentUpdateApp(view);
            }
        });
    }

    public interface Delegate {
        void drawerFragmentUpdateApp(View sender);
    }
}
