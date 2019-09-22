package com.asher.wally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private int page = 1;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<Photo> photos;
    PhotosAdapter adapter;
    PhotosAdapter.OnPhotoClickedListener photoClickListener;

    UnsplashInterface dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(MainActivity.this, config);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        dataService = UnsplashClient.getUnsplashClient().create(UnsplashInterface.class);


        photoClickListener = new PhotosAdapter.OnPhotoClickedListener() {
            @Override
            public void photoClicked(List<Photo> photo, ImageView imageView, int position) {

                List<String> list = new ArrayList<>();
                for (int i = 0; i < photo.size(); i++) {

                    list.add(photo.get(i).getUrls().getRegular());

                }

                new ImageViewer.Builder(MainActivity.this, list)
                        .setStartPosition(position)
                        .show();


            }
        };

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotosAdapter(new ArrayList<Photo>(), this, photoClickListener);
        recyclerView.setAdapter(adapter);
        adapter.notifyItemRangeChanged(0,adapter.getItemCount());

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadPhotos();
            }
        });

        loadPhotos();

    }


    private void loadPhotos() {
        progressBar.setVisibility(View.VISIBLE);

        dataService.getPhotos(page, null, "latest")
                .enqueue(new Callback<List<Photo>>() {
                    @Override
                    public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {

                        if (photos != null) {
                            photos.clear();

                        }

                        photos = response.body();
                        //
                        Log.d("Photos", "Photos Fetched " + photos.size());
                        //add to adapter
                        page++;
                        adapter.addPhotos(photos);
                   //     recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                 //       adapter.notifyItemRangeChanged(0,adapter.getItemCount());

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<List<Photo>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);

                    }
                });

    }



    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
