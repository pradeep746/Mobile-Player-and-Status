package com.pradeep.mobileacess.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.SEND_SMS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.databinding.ActivityHomeBinding;
import com.pradeep.mobileacess.databinding.ActivitySmsSndBinding;
import com.pradeep.mobileacess.model.ContactDataParameterForm;
import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.adaptor.SearchViewAdapter;
import com.pradeep.mobileacess.adaptor.UserAllSmsAdaptor;
import com.pradeep.mobileacess.model.UserSmsDataParameterForm;
import com.pradeep.mobileacess.model.smsSendDataParameterForm;
import com.pradeep.mobileacess.adaptor.smsViewAdaptor;

public class SmsSendActivity extends AppCompatActivity {
    private static final String TAG = "SmsSendActivity";
    private Toolbar mToolbar;
    private Activity mActivity;
    private Context mContext;
    private ArrayMap<String, ContactDataParameterForm> mPhoneNumberList;
    private SearchViewAdapter mSearchViewAdapter;
    private UserAllSmsAdaptor mSingleAdaptor;
    private smsViewAdaptor smsViewAdaptor;
    private List<Object> mContactList,mAllMessage,mAllMessageSingle;
    private String mPhoneNumber;
    private int mSimSelect = 0;
    private String mSendUserAddress;
    private ActivitySmsSndBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySmsSndBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = this;
        mContext = this;
        mAllMessage = new ArrayList<>();
        mContactList = new ArrayList<>();
        mAllMessageSingle = new ArrayList<>();
        binding.appBar.backRegister.setOnClickListener(View-> {
            finish();
        });
        binding.appBar.textView.setText("SMS detail");
        mPhoneNumberList = new ArrayMap<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                getContactList();
            }
        }.start();

        displayAllSms();
        displaySingleMsg();

        binding.sendSmsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.allView.setVisibility(View.GONE);
                binding.smsSendLayout.setVisibility(View.VISIBLE);
                binding.smsSendListLayout.setVisibility(View.GONE);
                smsSendDisplay();
            }
        });
        binding.viewSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.allView.setVisibility(View.GONE);
                binding.smsSendLayout.setVisibility(View.GONE);
                binding.smsSendListLayout.setVisibility(View.VISIBLE);
                displayAllSmsAgain();
            }
        });
        binding.appSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void displayAllSmsAgain() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                getAllSms(mContext);
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (smsViewAdaptor != null) {
                            smsViewAdaptor.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if(binding.userSmsListLayout.getVisibility() == View.VISIBLE) {
            binding.allView.setVisibility(View.GONE);
            binding.userSmsListLayout.setVisibility(View.GONE);
            binding.smsSendLayout.setVisibility(View.GONE);
            binding.smsSendListLayout.setVisibility(View.VISIBLE);
            displayAllSmsAgain();
        } else if(binding.smsSendListLayout.getVisibility() == View.VISIBLE) {
            binding.allView.setVisibility(View.VISIBLE);
            binding.smsSendLayout.setVisibility(View.GONE);
            binding.smsSendListLayout.setVisibility(View.GONE);
        } else if(binding.smsSendLayout.getVisibility() == View.VISIBLE) {
            binding.allView.setVisibility(View.VISIBLE);
            binding.smsSendLayout.setVisibility(View.GONE);
            binding.smsSendListLayout.setVisibility(View.GONE);
        } else {
            Intent i = new Intent(SmsSendActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(i);
            finish();
        }
    }

    @SuppressLint("Range")
    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME+" ASC");
        Log.v(TAG, "Profile....start send "+cur.getCount());
        while (cur != null && cur.moveToNext()) {
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String thumbNel = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            Log.e(TAG, " uri:"+thumbNel);
            if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                while (pCur.moveToNext()) {
                    String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (mPhoneNumberList.containsKey(name)) {
                        Log.e(TAG, "added  "+name + " number"+phoneNo);
                        int index = mPhoneNumberList.indexOfKey(name);
                        ContactDataParameterForm data = mPhoneNumberList.valueAt(index);
                        data.setUserNumerList(phoneNo);
                    } else {
                        ContactDataParameterForm userClass = new ContactDataParameterForm(name,thumbNel);
                        mPhoneNumberList.put(name, userClass);
                        userClass.setUserNumerList(phoneNo);
                        mContactList.add(userClass);
                        mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (mSearchViewAdapter != null) {
                                    mSearchViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
                pCur.close();
            }
        }
        if (cur != null) {
            cur.close();
        }

    }

    private void displaySingleAgain(UserSmsDataParameterForm address) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                getAllSmsUser(address.getUserName());
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (mSingleAdaptor != null) {
                            mSingleAdaptor.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    private void displaySingleMsg() {
        binding.recyclerviewUserSendSms.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mContext);
        binding.recyclerviewUserSendSms.setLayoutManager(mLayoutManager1);
        binding.recyclerviewUserSendSms.setItemAnimator(new DefaultItemAnimator());
        mSingleAdaptor = new UserAllSmsAdaptor(mContext,mAllMessageSingle);
        binding.recyclerviewUserSendSms.setAdapter(mSingleAdaptor);
        binding.buttonSimSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(SmsSendActivity.this, R.style.programClickStyle);
                android.widget.PopupMenu popup = new android.widget.PopupMenu(wrapper, v);
                popup.inflate(R.menu.sim_select_option);
                popup.setGravity(Gravity.CENTER);
                popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sim_1:
                                mSimSelect = 0;
                                break;
                            case R.id.sim_2:
                                mSimSelect = 1;
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        binding.buttonSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(mSendUserAddress,binding.editSmsToUser.getText().toString(),mSimSelect);
                Calendar c = Calendar.getInstance();
                long timestamp = c.getTimeInMillis();
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                String strbody = "\t\t"+formatter.format(calendar.getTime())+"\n\n"+binding.editSmsToUser.getText().toString();
                mAllMessageSingle.add(strbody);
                mSingleAdaptor.notifyDataSetChanged();
                binding.editSmsToUser.setText("");
                binding.editSmsToUser.clearFocus();
            }
        });
    }

    private void displayAllSms() {
        binding.recyclerviewSendSms.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mContext);
        binding.recyclerviewSendSms.setLayoutManager(mLayoutManager2);
        binding.recyclerviewSendSms.setItemAnimator(new DefaultItemAnimator());
        Log.e("tags", "" + mAllMessage.size());
        smsViewAdaptor = new smsViewAdaptor(mContext, mAllMessage, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position, final List<Object> list) {
                binding.userSmsListLayout.setVisibility(View.VISIBLE);
                binding.allView.setVisibility(View.GONE);
                binding.smsSendLayout.setVisibility(View.GONE);
                binding.smsSendListLayout.setVisibility(View.GONE);
                UserSmsDataParameterForm address = (UserSmsDataParameterForm)list.get(position);
                mAllMessage.clear();
                smsViewAdaptor.notifyDataSetChanged();
                displaySingleAgain(address);
            }

            @Override
            public boolean onItemLongClick(final View v, int position, List<Object> list) {
                return true;
            }
        });
        binding.recyclerviewSendSms.setAdapter(smsViewAdaptor);
    }

    public void getAllSms(Context context) {
        ArrayMap<String, UserSmsDataParameterForm> filterList = new ArrayMap<>();
        smsSendDataParameterForm smsSendData;
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.ADDRESS+" ASC");
        int totalSMS = 0;
        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToFirst()) {
                Log.e("ddagaaa", "" + totalSMS);
                for (int j = 0; j < totalSMS; j++) {
                    String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    String address = number;
                    address.replaceAll(" ","");
                    if(address.length() == 10){
                        address = "+91"+address;
                    }
                    if (!filterList.containsKey(address)) {
                        UserSmsDataParameterForm data = new UserSmsDataParameterForm(number, body);
                        mAllMessage.add(data);
                        filterList.put(number, data);
                    }
                    c.moveToNext();
                }
            }
            c.close();
        } else {
            Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllSmsUser(String address) {
        mSendUserAddress = address;
        Log.e("ddafaa","ddd"+address);
        StringBuilder smsBuilder = new StringBuilder();
        final String SMS_URI_ALL = "content://sms/";
        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[]{ "body", "date"};
            Cursor cur = getContentResolver().query(uri, projection, "address='" + address + "'", null, "date desc");
            if (cur.moveToFirst()) {
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                do {
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(longDate);
                    smsBuilder.append(strbody + "");
                    smsBuilder.append("\n");
                    smsBuilder.append("\t\t"+formatter.format(calendar.getTime())+"\n");
                    mAllMessageSingle.add(smsBuilder.toString());
                } while (cur.moveToNext());
                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            }
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        return;
    }

    private void smsSendDisplay() {
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
                } else if (data.getUserNumerList().size() > 0) {
                    binding.recyclerSearchView.setVisibility(View.GONE);
                    mPhoneNumber = data.getUserNumerList().get(0);
                    binding.searchEditText.setText("" + data.getUserName() + "(" + data.getUserNumerList().get(0) + ")");
                } else {
                    binding.recyclerSearchView.setVisibility(View.GONE);
                    binding.searchEditText.setText("error occurred");
                }
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
                Log.v(TAG, "Profile....start send");
                String sms = binding.editSms.getText().toString();
                Log.e(TAG, "Profile....sms send" + sms + mPhoneNumber);
                sendSMS(mPhoneNumber, sms, 0);
                Intent i = new Intent(SmsSendActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(i);
                finish();
            }
        });
        binding.btnSim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Profile....start send");
                String sms = binding.editSms.getText().toString();
                Log.e(TAG, "Profile....sms send" + sms + mPhoneNumber);
                sendSMS(mPhoneNumber, sms, 1);
                Intent i = new Intent(SmsSendActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(i);
                finish();
            }
        });
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Profile....stop send");
                Intent i = new Intent(SmsSendActivity.this, MainActivity.class);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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

    public void sendSMS(String phoneNo, String msg, int sim) {
        Log.e(TAG, "hideProgressBar " + phoneNo);
        try {
            if (ActivityCompat.checkSelfPermission(this, SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                try {
                    final ArrayList<Integer> simCardList = new ArrayList<>();
                    SubscriptionManager subscriptionManager;
                    subscriptionManager = SubscriptionManager.from(mActivity);
                    final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                    for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                        int subscriptionId = subscriptionInfo.getSubscriptionId();
                        simCardList.add(subscriptionId);
                    }
                    int smsToSendFrom = simCardList.get(sim);
                    SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(phoneNo, null, msg, null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
                } catch (Exception ErrVar) {
                    Toast.makeText(getApplicationContext(), ErrVar.getMessage().toString(), Toast.LENGTH_LONG).show();
                    ErrVar.printStackTrace();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{SEND_SMS}, 10);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
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
        Log.e(TAG, "pradeep hide");
        if (binding.mainProgressBar.getVisibility() == View.VISIBLE) {
            Log.v(TAG, "pradeep gone visible");
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.mainProgressBar.setVisibility(View.GONE);
        }
    }


}