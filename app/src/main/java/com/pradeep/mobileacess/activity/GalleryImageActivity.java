package com.pradeep.mobileacess.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import com.bumptech.glide.Glide;
import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.databinding.ActivityGalaryImageBinding;
import com.pradeep.mobileacess.model.ImageParameterForm;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.adaptor.ImageAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class GalleryImageActivity extends AppCompatActivity {
    private final String TAG = "GalleryImageActivity";
    private File fileUri;
    private Context mContext;
    float[] lastEvent = null;
    private boolean isOutSide;
    private static final int NONE = 0, DRAG = 1, ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF(), mid = new PointF();
    private float oldDist = 1f, oldRot = 0f, oldScaleX = 0f, oldScaleY = 0f, newRot = 0f, d = 0f, xCoOrdinate, yCoOrdinate;
    private ArrayList<Object> listImage;
    private ImageAdapter mImageAdaptor;
    private ActivityGalaryImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalaryImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        binding.appBar.backRegister.setOnClickListener(View-> {
            finish();
        });
        binding.appBar.textView.setText("Gallery detail");
        listImage = new ArrayList<>();
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.allView.setVisibility(View.GONE);
                binding.imageViewDisplay.setVisibility(View.VISIBLE);
                display();
                Log.v(TAG, "Profile....image_view");
            }
        });
        binding.imageEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"coming soon..",Toast.LENGTH_SHORT).show();
                //binding.allView.setVisibility(View.GONE);
                Log.v(TAG, "Profile....image_editor");
            }
        });
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.allView.setVisibility(View.GONE);
                Log.v(TAG, "Profile....camera");
                openCamara();

            }
        });
        DisplayImage();
        display();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e(TAG, "toolbar");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void display() {
        new Thread() {
            @Override
            public void run() {
                listImage.clear();
                ((GalleryImageActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageAdaptor.notifyDataSetChanged();
                    }
                });
                listImage.addAll(getFilePaths());
                ((GalleryImageActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageAdaptor.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }

    private void openCamara() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.i(TAG, "IOException");
        }
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(cameraIntent, 1);
        }
    }

    private File createImageFile() throws IOException {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath() + "/" + imageFileName + ".jpg");
        fileUri = image;
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode != RESULT_CANCELED) {
                    if (resultCode == RESULT_OK) {
                        saveImageToExternalStorage(fileUri);
                    } else {
                        fileUri.delete();
                    }
                } else {
                    fileUri.delete();
                }
        }
    }

    public void saveImageToExternalStorage(File finalBitmap) {
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri picUri = Uri.fromFile(finalBitmap);
            galleryIntent.setData(picUri);
            GalleryImageActivity.this.sendBroadcast(galleryIntent);
            binding.allView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    private void DisplayImage() {
        Log.e(TAG, "DisplayImage" + listImage.size());
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        binding.imageRecyclerView.setLayoutManager(manager);
        mImageAdaptor = new ImageAdapter(GalleryImageActivity.this, listImage, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, final int position, final List<Object> recordedEvent) {
                try {
                    binding.imageFullView.setVisibility(View.VISIBLE);
                    binding.deleteView.setVisibility(View.VISIBLE);
                    final ImageParameterForm imageObject = (ImageParameterForm) listImage.get(position);
                    String filePath = imageObject.getmPath();
                    File imgFile = new File(filePath);
                    if (imgFile.exists()) {
                        Glide.with(mContext).load(filePath).placeholder(R.drawable.maxresdefault).error(R.drawable.no_thumbnail).into(binding.imageFullViewDisplay);
                    }
                    oldRot = binding.imageFullViewDisplay.getRotation();
                    oldScaleX = binding.imageFullViewDisplay.getScaleX();
                    oldScaleY = binding.imageFullViewDisplay.getScaleY();
                    binding.imageFullViewDisplay.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            ImageView view = (ImageView) v;
                            view.bringToFront();
                            viewTransformation(view, event);
                            return true;
                        }
                    });
                    binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.w(TAG, "btnDelete");
                            deleteImage(imageObject, position);
                        }
                    });
                    binding.btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareData();
                        }
                    });
                } catch (IndexOutOfBoundsException ex) {
                    Log.w(TAG, "No such Item exists");
                    return;
                }
            }

            @Override
            public boolean onItemLongClick(View v, final int position, final List<Object> recordedEvent) {
                final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                return true;
            }
        });
        binding.imageRecyclerView.setAdapter(mImageAdaptor);
        binding.imageRecyclerView.hasFixedSize();
        Log.e(TAG, "end" + listImage.size());
    }

    private void shareData() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "sending some file");
        intent.setType("image/jpeg");
        ArrayList<Uri> files = new ArrayList<Uri>();
        for (int i = 0; i < listImage.size(); i++) {
            ImageParameterForm data = (ImageParameterForm) listImage.get(i);
            if (data.isSelect()) {
                File image = new File(data.getmPath());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 720, 576, true);
                File imageFile = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file" + i + ".jpg");
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    files.add(Uri.fromFile(new File(String.valueOf(imageFile))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

    private void deleteImage(ImageParameterForm data,int position) {
        String path = data.getmPath();
        File filedelete = new File(path);
        if (filedelete.exists()) {
            if (filedelete.delete()) {
                System.out.println("file Deleted :" + path);
            } else {
                System.out.println("file not Deleted :" + path);
            }
        }
        display();
        binding.imageViewDisplay.setVisibility(View.VISIBLE);
        binding.imageFullView.setVisibility(View.GONE);
        binding.deleteView.setVisibility(View.GONE);
    }

    public ArrayList<Object> getFilePaths() {
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri t = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<String> resultIAV = new ArrayList<String>();
        String[] directories = null;
        if (u != null) {
            c = getContentResolver().query(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();
                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                    ) {
                        resultIAV.add(imagePath.getAbsolutePath());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (t != null) {
            c = getContentResolver().query(t, projection, null, null, null);
        }
        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();
                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                    ) {
                        resultIAV.add(imagePath.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Log.e(TAG, "Profile....list  size " + resultIAV.size());
        Collections.sort(resultIAV, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                File f1 = new File(lhs);
                File f2 = new File(rhs);

                if (f1.lastModified() > (f2.lastModified())) {
                    return -1;
                } else if (f1.lastModified() < (f2.lastModified())) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });
        Set<String> set = new LinkedHashSet<String>(resultIAV);
        resultIAV.clear();
        resultIAV.addAll(set);
        ArrayList<Object> result = new ArrayList<>();
        for (int i = 0; i < resultIAV.size(); i++) {
            ImageParameterForm temp = new ImageParameterForm(resultIAV.get(i), false);
            result.add(temp);
        }
        return result;
    }

    public ArrayList<HashMap<String, String>> getAudioList() {
        ArrayList<HashMap<String, String>> mSongsList = new ArrayList<HashMap<String, String>>();
        Cursor mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA
        }, null, null, null);
        int count = mCursor.getCount();
        System.out.println("total no of songs are=" + count);
        HashMap<String, String> songMap;
        while (mCursor.moveToNext()) {
            songMap = new HashMap<String, String>();
            songMap.put("songTitle", mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
            songMap.put("songPath", mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            mSongsList.add(songMap);
        }
        mCursor.close();
        return mSongsList;
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "on back pressed");
        LinearLayout delete = (LinearLayout) findViewById(R.id.image_delete_layout);
        if (binding.imageFullView.getVisibility() == View.VISIBLE) {
            lastEvent = null;
            d = 0f;
            newRot = 0f;
            isOutSide = false;
            mode = NONE;
            start = null;
            start = new PointF();
            mid = null;
            mid = new PointF();
            oldDist = 1f;
            xCoOrdinate = 0.0f;
            yCoOrdinate = 0.0f;
            if ((binding.imageFullViewDisplay.getRotation() != oldRot) && (binding.imageFullViewDisplay.getScaleX() != oldScaleX) && (binding.imageFullViewDisplay.getScaleY() != oldScaleY)) {
                binding.imageFullViewDisplay.setScaleX(oldScaleX);
                binding.imageFullViewDisplay.setScaleY(oldScaleY);
                binding.imageFullViewDisplay.setRotation(oldRot);
                return;
            }
            oldScaleX = 0f;
            oldScaleY = 0f;
            oldRot = 0f;
            binding.imageFullView.setVisibility(View.GONE);
            binding.deleteView.setVisibility(View.GONE);
            return;
        } else if (binding.imageViewDisplay.getVisibility() == View.VISIBLE) {
            listImage.clear();
            mImageAdaptor.notifyDataSetChanged();
            binding.imageViewDisplay.setVisibility(View.GONE);
            binding.allView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void viewTransformation(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xCoOrdinate = view.getX() - event.getRawX();
                yCoOrdinate = view.getY() - event.getRawY();
                start.set(event.getX(), event.getY());
                isOutSide = false;
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
                if (mode == DRAG) {
                    float x = event.getX();
                    float y = event.getY();
                }
            case MotionEvent.ACTION_OUTSIDE:
                isOutSide = true;
                mode = NONE;
                lastEvent = null;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE: {
                if (!isOutSide) {
                    if (mode == DRAG) {
                        view.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                    }
                    if (mode == ZOOM && event.getPointerCount() == 2) {
                        float newDist1 = spacing(event);
                        if (newDist1 > 10f) {
                            float scale = newDist1 / oldDist * view.getScaleX();
                            view.setScaleX(scale);
                            view.setScaleY(scale);
                        }
                        if (lastEvent != null) {
                            newRot = rotation(event);
                            view.setRotation((float) (view.getRotation() + (newRot - d)));
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                lastEvent = null;
                d = 0f;
                newRot = 0f;
                isOutSide = false;
                mode = NONE;
                start = null;
                start = new PointF();
                mid = null;
                mid = new PointF();
                oldDist = 1f;
                xCoOrdinate = 0.0f;
                yCoOrdinate = 0.0f;
            }
        }
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (int) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


}