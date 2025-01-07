package com.pradeep.mobileacess.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pradeep.mobileacess.SessionManager;
import com.pradeep.mobileacess.databinding.ActivityRegisterBinding;
import com.pradeep.mobileacess.dbaccess.Entities.User;
import com.pradeep.mobileacess.dbaccess.LoginAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    private SessionManager mSession;
    private Context mContext;
    private int SELECT_PICTURE = 200;
    private String mProfileImageUrl;
    private LoginAccess mLoginAccess;

    private ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        mProfileImageUrl = "data/data/com.pradeep.mobileacess/profile.jpg";
        mLoginAccess = LoginAccess.getInstance(mContext);
        binding.uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                dismissKeyboard();
                if (validateRegister()) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            performRegister();
                        }
                    }.start();
                } else {
                    Toast.makeText(RegisterActivity.this, "Please Enter valid details", Toast.LENGTH_SHORT).show();
                }
                hideProgressBar();
            }
        });

        binding.loginPageData.setOnClickListener(View-> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        mSession = new SessionManager(mContext);
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    binding.profileImg.setImageURI(selectedImageUri);
                    BitmapDrawable draw = (BitmapDrawable) binding.profileImg.getDrawable();
                    Bitmap bitmap = draw.getBitmap();
                    try {
                        FileOutputStream outStream = null;
                        File outFile = new File(mProfileImageUrl);
                        outFile.delete();
                        outFile.createNewFile();
                        outStream = new FileOutputStream(outFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void performRegister() {
        String name = binding.registerName.getText().toString();
        String phone = binding.registerNumber.getText().toString();
        String email = binding.registerEmail.getText().toString();
        String password = binding.registerPassword.getText().toString();
        User temp = new User();
        temp.setEmail(email);
        temp.setPassword(password);
        temp.setName(name);
        temp.setNumber(phone);
        temp.setImageLocation(mProfileImageUrl);
        int result = mLoginAccess.registerUser(temp);
        if(result < 0) {
            Toast.makeText(RegisterActivity.this, "Failed to register.", Toast.LENGTH_SHORT).show();
        } else {
            ((RegisterActivity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    mSession.setRegister(true,mProfileImageUrl,name);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(RegisterActivity.this, "Registration completed please login.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateRegister() {
        boolean valid = true;
        String name = binding.registerName.getText().toString();
        String phone = binding.registerNumber.getText().toString();
        String email = binding.registerEmail.getText().toString();
        String password = binding.registerPassword.getText().toString();
        String reEnterPassword = binding.registerConfirmPassword.getText().toString();
        if (name.isEmpty() || name.length() < 3) {
            binding.registerName.setError("Enter at least 3 characters");
            valid = false;
        } else {
            binding.registerName.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 10) {
            binding.registerNumber.setError("Enter valid mobile number");
            valid = false;
        } else {
            binding.registerNumber.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.registerEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            binding.registerEmail.setError(null);
        }
        if (password.isEmpty() || !validatePassword(password)) {
            binding.registerPassword.setError("Password must between 8 and 15 characters and At least 1 letter, 1 number, 1 special character and SHOULD NOT start with a special character ");
            valid = false;
        } else {
            binding.registerPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            binding.registerConfirmPassword.setError("Password Do not match");
            valid = false;
        } else {
            binding.registerConfirmPassword.setError(null);
        }
        return valid;
    }

    private void showProgressBar() {
        if (binding.opProgressBar.getVisibility() != View.VISIBLE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.opProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (binding.opProgressBar.getVisibility() == View.VISIBLE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.opProgressBar.setVisibility(View.GONE);
        }
    }

    private boolean validatePassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$&_])[A-Za-z\\d][A-Za-z\\d@#$&_]{7,19}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void dismissKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
