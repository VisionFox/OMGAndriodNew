package com.followme.lusir.omgandriodnew;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

public class DetailActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tell the activity which XML layout is right
        setContentView(R.layout.activity_detail);
        // Enable the "Up" button for more navigation options
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // Access the imageview from XML
        ImageView imageView = (ImageView) findViewById(R.id.img_cover);

    }
}
