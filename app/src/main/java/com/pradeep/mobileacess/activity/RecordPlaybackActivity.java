package com.pradeep.mobileacess.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.LinearLayout;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.AudioManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.adaptor.RecordedAdapter;
import com.pradeep.mobileacess.databinding.ActivityHomeBinding;
import com.pradeep.mobileacess.databinding.ActivityRecordPlaybackBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecordPlaybackActivity extends AppCompatActivity {
    private Context mContext;
    private static final String TAG = "RecordPlaybackActivity";
    private ArrayList<Object> mRecordedEvent;
    private RecordedAdapter mRecordListAdapter;
    private ActivityRecordPlaybackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordPlaybackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        mRecordedEvent = new ArrayList<>();
        binding.appBar.backRegister.setOnClickListener(View-> {
            if(binding.videoPlayLayout.getVisibility() == View.VISIBLE) {
                binding.videoView.stopPlayback();
                binding.videoView.setVisibility(View.GONE);
                binding.videoView.setVisibility(View.VISIBLE);
                binding.videoPlayLayout.setVisibility(View.GONE);
                binding.imageViewDisplay.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                finish();
            }
        });
        binding.appBar.textView.setText("Video list");
        new Thread() {
            @Override
            public void run() {
                super.run();
                getAllRecordedVideo();
            }
        }.start();
        displayRecordedList();
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        binding.videoView.stopPlayback();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        if(binding.videoPlayLayout.getVisibility() == View.VISIBLE) {
            binding.videoView.stopPlayback();
            binding.videoView.setVisibility(View.GONE);
            binding.videoView.setVisibility(View.VISIBLE);
            binding.videoPlayLayout.setVisibility(View.GONE);
            binding.imageViewDisplay.setVisibility(View.VISIBLE);
            binding.appBar.mainLayout.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }

    private void getAllRecordedVideo() {
        HashSet<String> videoItemHashSet = new HashSet<>();
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do {
                videoItemHashSet.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            } while (cursor.moveToNext());
            cursor.close();
            Cursor cursort = mContext.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, projection, null, null, null);
            cursort.moveToFirst();
            do {
                videoItemHashSet.add((cursort.getString(cursort.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            } while (cursort.moveToNext());
            cursort.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);
        mRecordedEvent.addAll(downloadedList);
        ((RecordPlaybackActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mRecordListAdapter != null) {
                    mRecordListAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    private void nextPlayer(String mPath) {
        Log.e(TAG, "Record next file path : " + mPath);
        binding.appBar.mainLayout.setVisibility(View.GONE);
        MediaController mediaController = new MediaController(mContext);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediaController);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.videoView.setVideoPath(mPath);
        binding.videoView.requestFocus();
        binding.videoView.start();
    }

    private void displayRecordedList() {
        mRecordListAdapter = new RecordedAdapter(mContext, mRecordedEvent, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, final int position, final List<Object> recordedEvent) {
                try {
                    binding.videoPlayLayout.setVisibility(View.VISIBLE);
                    binding.imageViewDisplay.setVisibility(View.GONE);
                    nextPlayer(""+recordedEvent.get(position));
                } catch (IndexOutOfBoundsException ex) {
                    Log.w(TAG, "No such Item exists");
                    return;
                }
            }

            @Override
            public boolean onItemLongClick(View v, final int position, final List<Object> recordedEvent) {
                try {
                    Log.v(TAG, "record content onLongClicked");
                    //deleteFile(mNextPath);
                    mRecordListAdapter.notifyDataSetChanged();
                } catch (IndexOutOfBoundsException ex) {
                    Log.w(TAG, "No such Item exists");
                }
                return true;
            }
        },0);
        GridLayoutManager manager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        binding.myRecyclerView.setLayoutManager(manager);
        binding.myRecyclerView.setAdapter(mRecordListAdapter);
    }

}

