package com.pradeep.mobileacess.activity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.adaptor.*;
import com.pradeep.mobileacess.databinding.ActivityAppDisplayBinding;
import com.pradeep.mobileacess.databinding.ActivityHomeBinding;

public class AppDisplayActivity extends AppCompatActivity {
    private String TAG = "AppDisplayActivity";
    private AppDataDisplayAdaptor mAdapter;
    private  Dialog myDialog;
    private Activity mActivity;
    private Context mContext;
    private List<Object> mAllInstalledAppList;
    private ActivityAppDisplayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        binding = ActivityAppDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = this;
        mContext = this;
        binding.appBar.textView.setText("App List");
        binding.appBar.backRegister.setOnClickListener(View-> {
            finish();
        });
        binding.recyclerView.setLayoutManager(new GridLayoutManager(AppDisplayActivity.this, 2));
        mAllInstalledAppList = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                GetAllInstalledApkInfo();
            }
        }.start();
        mAdapter = new AppDataDisplayAdaptor(AppDisplayActivity.this, mAllInstalledAppList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, final int position, final List<Object> list) {
                String packageName = (String)list.get(position);
                Intent intent = AppDisplayActivity.this.getPackageManager().getLaunchIntentForPackage(packageName);
                if(intent != null){
                    AppDisplayActivity.this.startActivity(intent);
                }
                else {
                    Toast.makeText(AppDisplayActivity.this,packageName + " Error, Please Try Again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public boolean onItemLongClick(View v, final int position, final List<Object> appInstallList) {
                String packageName = (String)appInstallList.get(position);
                Log.e(TAG,"packageName:"+packageName);
                Context wrapper = new ContextThemeWrapper(AppDisplayActivity.this, R.style.programClickStyle);
                PopupMenu popup = new PopupMenu(wrapper, v);
                popup.inflate(R.menu.app_menu_option);
                popup.setGravity(Gravity.CENTER);
                setForceShowIcon(popup);
                if(isSystemApp(packageName)) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                }
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.share: {
                            shareData((String) mAllInstalledAppList.get(position));
                            break;
                        }
                        case R.id.detail: {
                            startApplicationDetailsActivity((String) mAllInstalledAppList.get(position));
                            break;
                        }
                        case R.id.uninstall: {
                            showAlert(position);
                            break;
                        }
                    }
                    return false;
                });
                popup.show();
                return true;
            }
        });
        binding.recyclerView.setAdapter(mAdapter);
    }
    public boolean isSystemApp(String packageName) {
        try {
            PackageManager pm = mContext.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e("TAG","toolbar");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private void shareData(String packageName) {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            File srcFile = new File(ai.publicSourceDir);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setType("application/vnd.android.package-archive");
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(srcFile));
            startActivity(Intent.createChooser(share, "PersianCoders"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ShareApp", e.getMessage());
            Toast.makeText(mContext, "This app you can't share.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startApplicationDetailsActivity(String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        startActivity(intent);
    }

    private void showAlert(final int etc1) {
        mActivity.runOnUiThread(() -> {
            myDialog = new Dialog(mContext);
            myDialog.setContentView(R.layout.app_display_popup);
            final Button btn_yes = (Button) myDialog.findViewById(R.id.btn_yes);
            final Button btn_no = (Button) myDialog.findViewById(R.id.btn_no);
            btn_yes.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + mAllInstalledAppList.get(etc1)));
                    startActivity(intent);
                    Toast.makeText(mActivity, "Successfully uninstalled app", Toast.LENGTH_SHORT).show();
                    mAllInstalledAppList.remove(etc1);
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "failed to uninstalled app", Toast.LENGTH_SHORT).show();
                }
                myDialog.dismiss();
            });
            btn_no.setOnClickListener(v -> myDialog.dismiss());
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.setCancelable(false);
            myDialog.show();
        });
    }


    public void GetAllInstalledApkInfo() {
        Map<String,String> appList = new TreeMap<>();
        List<ApplicationInfo> packages = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        appList.putAll(getPackagesOfDialerApps(AppDisplayActivity.this));
        for (ApplicationInfo listInfo : packages) {
            if(mContext.getPackageManager().getLaunchIntentForPackage(listInfo.packageName) != null) {
                appList.put(GetAppName(listInfo.packageName),listInfo.packageName);
            }
        }
        HashMap<String, String> map = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        for (String packageInfo : appList.values()) {
            try {
                PackageManager packageManager = mContext.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageInfo, 0);
                String ApplicationLabelName = "";
                if (applicationInfo != null) {
                    ApplicationLabelName = (String) packageManager.getApplicationLabel(applicationInfo);
                }
                map.put(packageInfo, ApplicationLabelName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            list.add(entry.getValue());
        }
        Collections.sort(list, (str, str1) -> (str).compareToIgnoreCase(str1));
        for (String str : list) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue().equals(str)) {
                    mAllInstalledAppList.add(entry.getKey());
                }
            }
        }
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
    public Map<String,String> getPackagesOfDialerApps(Context context){
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        Map<String,String> appList = new HashMap<>();
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        for(ResolveInfo resolveInfo : resolveInfos){
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            appList.put(GetAppName(activityInfo.packageName),activityInfo.packageName);
        }
        return appList;
    }

    public String GetAppName(String ApkPackageName) {
        String Name = "";
        ApplicationInfo applicationInfo;
        PackageManager packageManager = AppDisplayActivity.this.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0);
            if (applicationInfo != null) {
                Name = (String) packageManager.getApplicationLabel(applicationInfo);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Name;
    }
}