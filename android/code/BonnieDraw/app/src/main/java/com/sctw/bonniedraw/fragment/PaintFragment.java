package com.sctw.bonniedraw.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paint.PaintActivity;
import com.sctw.bonniedraw.utility.FileUtils;
import com.sctw.bonniedraw.utility.GlobalVariable;

import java.net.URISyntaxException;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaintFragment extends Fragment {
    private static final int READ_REQUEST_CODE = 6060;
    Button createPaint;
    Button openRecord;

    public PaintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paint, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createPaint = (Button) view.findViewById(R.id.create_new_paint);
        openRecord = (Button) view.findViewById(R.id.open_record);

        createPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(getActivity(), PaintActivity.class);
                startActivity(it);
            }
        });

        openRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileBrowserIntent();
            }
        });
    }

    private void fileBrowserIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "選擇檔案"), READ_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // 若使用者沒有安裝檔案瀏覽器的 App 則顯示提示訊息
            Toast.makeText(getActivity(), "沒有檔案瀏覽器 是沒辦法選擇檔案的", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 檔案選擇代碼
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 取得檔案路徑 Uri
            Uri uri = data.getData();
            String path;
            try {
                path = FileUtils.getPath(getContext(), uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "沒有讀取到檔案", Toast.LENGTH_SHORT).show();
                return;
            }

            // 檢查檔案類型
            String filename = FileUtils.typefaceChecker(path);
            if (filename.isEmpty()) {
                Toast.makeText(getActivity(), "請選擇.bdw格式的檔案", Toast.LENGTH_SHORT).show();
            }

            Intent intent=new Intent();
            intent.setClass(getActivity(),PaintActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString(GlobalVariable.fileURIStr,uri.toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
