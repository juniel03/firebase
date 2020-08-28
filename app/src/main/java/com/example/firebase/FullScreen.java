package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class FullScreen extends AppCompatActivity {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    TextView textView;
    boolean fullscreen = false;
    ImageView fullscreenButton;
    private String url;
    private boolean playwhenready = false;
    private  int currentWindow = 0;
    private  long playbackposition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        playerView = findViewById(R.id.exoplayer_item_fullscreen);
        textView = findViewById(R.id.tv_fullscreen);

        Intent intent = getIntent();
        url = intent.getExtras().getString("ur");
        String title = intent.getExtras().getString("nam");
        Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
        textView.setText(title);
    }
    private MediaSource buildMediaSource(Uri uri){
        DataSource.Factory datasourcefactory =
                new DefaultHttpDataSourceFactory("video");
        return  new ProgressiveMediaSource.Factory(datasourcefactory)
                .createMediaSource(uri);
    }

    private void initializeplayer(){
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setPlayWhenReady(playwhenready);
        player.seekTo(currentWindow,playbackposition);
        player.prepare(mediaSource,false,false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Util.SDK_INT >= 26 ){
            initializeplayer();
            //   textView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Util.SDK_INT >= 26 || player == null ){
            //  initializeplayer();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Util.SDK_INT > 26 ){
            releasePlayer();


        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Util.SDK_INT >= 26 ){
            releasePlayer();
        }
    }
    private void releasePlayer(){
        if (player != null){
            playwhenready = player.getPlayWhenReady();
            playbackposition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player = null;

        }
    }
}