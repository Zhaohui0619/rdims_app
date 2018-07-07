package com.ctkj.xj_app.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.TextView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.adapter.ExceptionImagesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExceptionImagesActivity extends AppCompatActivity {

/*    @BindView(R.id.tv_image_path)
    TextView imagePath_tv;*/
    @BindView(R.id.gv_exception)
    GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_images);
        ButterKnife.bind(this);

        String imagePath = getIntent().getStringExtra("imagePathList");
//        imagePath_tv.setText(imagePath);

        ExceptionImagesAdapter mAdapter = new ExceptionImagesAdapter(getImagePaths(imagePath), this);
        mGridView.setAdapter(mAdapter);
    }

    private List<String> getImagePaths(String path) {
        List<String> pathList = new ArrayList<>(0);
        String[] p = path.split(",");
        for (String s : p) {
            if (s.contains(".jpg") || s.contains(".png")) {
                pathList.add(s);
            }
        }
        return pathList;
    }
}
