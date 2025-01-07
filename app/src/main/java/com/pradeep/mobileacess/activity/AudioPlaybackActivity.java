package com.pradeep.mobileacess.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.adaptor.RecordedAdapter;
import com.pradeep.mobileacess.databinding.ActivityAudioPlaybackBinding;
import com.pradeep.mobileacess.databinding.ActivityRecordPlaybackBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioPlaybackActivity extends AppCompatActivity {
    private Context mContext;
    private static final String TAG = "AudioPlaybackActivity";
    private ArrayList<Object> mRecordedEvent;
    private RecordedAdapter mRecordListAdapter;
    private Activity mActivity;
    private ActivityAudioPlaybackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudioPlaybackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = this;
        mContext = this;
        mRecordedEvent = new ArrayList<>();
        binding.appBar.textView.setText("Audio list");
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
        new Thread() {
            @Override
            public void run() {
                super.run();
                getAllAudioFromDevice();
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
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        if (binding.videoPlayLayout.getVisibility() == View.VISIBLE) {
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

    private void getAllAudioFromDevice() {
        ContentResolver cr = mActivity.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if (cur != null) {
            count = cur.getCount();

            if (count > 0) {
                while (cur.moveToNext()) {
                    @SuppressLint("Range") String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    Log.e("Path :" + data, " Artist :");
                    mRecordedEvent.add(data);
                }

            }
        }
        cur.close();
        ((AudioPlaybackActivity)mContext).runOnUiThread(new Runnable() {
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
                    Log.e(TAG,""+recordedEvent.get(position));
                    nextPlayer("" + recordedEvent.get(position));
                } catch (IndexOutOfBoundsException ex) {
                    Log.w(TAG, "No such Item exists");
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
        },1);
        GridLayoutManager manager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        binding.myRecyclerView.setLayoutManager(manager);
        binding.myRecyclerView.setAdapter(mRecordListAdapter);
    }

}

