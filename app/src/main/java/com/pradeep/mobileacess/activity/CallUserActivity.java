package com.pradeep.mobileacess.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.databinding.ActivityCallUserBinding;
import com.pradeep.mobileacess.databinding.ActivityHomeBinding;
import com.pradeep.mobileacess.model.ContactDataParameterForm;
import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.adaptor.SearchViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CallUserActivity extends AppCompatActivity {
    private static final String TAG = "CallUserActivity";
    private Activity mActivity;
    private Context mContext;
    private ArrayMap<String, ContactDataParameterForm> mPhoneNumberList;
    private SearchViewAdapter mSearchViewAdapter;
    private List<Object> mContactList;
    private String mPhoneNumber;
    private List<PhoneAccountHandle> phoneAccountHandleList;
    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim", "com.android.phone.extra.slot", "slot", "simslot", "sim_slot",
            "subscription", "Subscription", "phone", "com.android.phone.DialingMode", "simSlot", "slot_id",
            "simId", "simnum", "phone_type", "slotId", "slotIdx"
    };
    private ActivityCallUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = this;
        mContext = this;
        mPhoneNumberList = new ArrayMap<>();
        binding.appBar.backRegister.setOnClickListener(View-> {
            finish();
        });
        binding.appBar.textView.setText("Call User");
        mContactList = new ArrayList<>();
        showProgressBar();
         new Thread() {
            @Override
            public void run() {
                super.run();
                getContactList();
            }
        }.start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("Range")
    private void getContactList() {
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME+" ASC");
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                     String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String thumbNel = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (mPhoneNumberList.containsKey(name)) {
                            int index = mPhoneNumberList.indexOfKey(name);
                            ContactDataParameterForm data = mPhoneNumberList.valueAt(index);
                            data.setUserNumerList(phoneNo);
                        } else {
                            ContactDataParameterForm userClass = new ContactDataParameterForm(name,thumbNel);
                            mPhoneNumberList.put(name, userClass);
                            userClass.setUserNumerList(phoneNo);
                            mContactList.add(userClass);
                        }
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        hideProgressBar();

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if(mContactList.size() > 6) {
                    ViewGroup.LayoutParams params = binding.secondLayout.getLayoutParams();
                    params.height = pxToDp(400);
                    binding.secondLayout.setLayoutParams(params);
                } else {
                    ViewGroup.LayoutParams params = binding.secondLayout.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;;
                    binding.secondLayout.setLayoutParams(params);
                }
                callUserDisplay();
            }
        });

    }


    public int pxToDp(int px) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round(px * density);
    }

    private void callUserDisplay() {
        binding.recyclerSearchView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        binding.recyclerSearchView.setLayoutManager(mLayoutManager);
        binding.recyclerSearchView.setItemAnimator(new DefaultItemAnimator());
        mSearchViewAdapter = new SearchViewAdapter(mContext, mContactList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position, final List<Object> list) {
                final ContactDataParameterForm data = (ContactDataParameterForm) list.get(position);
                if (data.getUserNumerList().size() > 1) {
                    Context wrapper = new ContextThemeWrapper(mActivity, R.style.programClickStyle);
                    PopupMenu popup = new PopupMenu(wrapper, v);
                    for (int i = 0; i < data.getUserNumerList().size(); i++) {
                        popup.getMenu().add(data.getNumber(i));
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            binding.searchEditText.setText("" + data.getUserName() + "(" + item.toString() + ")");
                            mPhoneNumber = item.toString();
                            binding.recyclerSearchView.setVisibility(View.GONE);
                            return false;
                        }
                    });
                    popup.show();
                    return;
                } else if (data.getUserNumerList().size() > 0) {
                    mPhoneNumber = data.getUserNumerList().get(0);
                    binding.searchEditText.setText("" + data.getUserName() + "(" + data.getUserNumerList().get(0) + ")");
                } else {
                    binding.searchEditText.setText("error occurred");
                }
                binding.recyclerSearchView.setVisibility(View.GONE);
            }

            @Override
            public boolean onItemLongClick(View v, int position, List<Object> list) {
                return false;
            }
        });
        binding.recyclerSearchView.setAdapter(mSearchViewAdapter);
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable sshowProgressBar) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchViewAdapter.getFilter().filter(s);
            }
        });
        binding.btnSim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelecomManager telecomManager = (TelecomManager) mActivity.getSystemService(Context.TELECOM_SERVICE);
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
                Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + mPhoneNumber));
                intent.putExtra("com.android.phone.force.slot", true);
                intent.putExtra("Cdma_Supp", true);
                for (String s : simSlotName) {
                    intent.putExtra(s, 0);
                }
                if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0) {
                    intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                }
                startActivity(intent);
            }
        });
        binding.btnSim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelecomManager telecomManager = (TelecomManager) mActivity.getSystemService(Context.TELECOM_SERVICE);
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
                Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + mPhoneNumber));
                intent.putExtra("com.android.phone.force.slot", true);
                intent.putExtra("Cdma_Supp", true);
                for (String s : simSlotName) {
                    intent.putExtra(s, 1); //0 or 1 according to sim.......
                }
                if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0) {
                    intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                }
                startActivity(intent);
            }
        });
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Profile....stop send");
                Intent i = new Intent(CallUserActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(i);
                finish();
            }
        });


        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "onTextChanged: " + s);
                if (binding.recyclerSearchView.getVisibility() != View.VISIBLE) {
                    binding.recyclerSearchView.setVisibility(View.VISIBLE);
                }
            }
        });
        Log.v(TAG, "search completed");
    }


    private void showProgressBar() {
        Log.e(TAG, "pradeep show");
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.e(TAG, "pradeep show");
                if (binding.mainProgressBar.getVisibility() != View.VISIBLE) {
                    mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    binding.mainProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.e(TAG, "pradeep hide");
                if (binding.mainProgressBar.getVisibility() == View.VISIBLE) {
                    Log.v(TAG, "pradeep gone visible");
                    mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    binding.mainProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}