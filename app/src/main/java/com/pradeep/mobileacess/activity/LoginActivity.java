package com.pradeep.mobileacess.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pradeep.mobileacess.SessionManager;
import com.pradeep.mobileacess.databinding.ActivityLoginBinding;
import com.pradeep.mobileacess.dbaccess.Entities.User;
import com.pradeep.mobileacess.dbaccess.LoginAccess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private SessionManager mSession;
    private Context mContext;
    private LoginAccess mLoginAccess;

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        mSession = new SessionManager(this);
        mLoginAccess = LoginAccess.getInstance(mContext);
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.status.setVisibility(View.GONE);
                showProgressBar();
                dismissKeyboard();
                String user = binding.username.getText().toString();
                String password = binding.password.getText().toString();
                if (validate(user, password)) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            performLogin(binding.username.getText().toString(),  binding.password.getText().toString());
                        }
                    }.start();
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enter User Id and Password", Toast.LENGTH_SHORT).show();
                }
                hideProgressBar();
            }
        });
        if (mSession.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        binding.register.setOnClickListener(View-> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void performLogin(String user, String passwd) {
        User result = mLoginAccess.login(user,passwd);
        Log.e("TAG",user.toString());
        if(result == null) {
            ((LoginActivity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, "Failed to login.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ((LoginActivity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    mSession.setLogin(true, (int) result.getId());
                    Toast.makeText(LoginActivity.this, "Login is successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
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

    private boolean validate(String user, String passwd) {
        boolean valid = true;

        if (user.isEmpty() || user.length() < 0) {
            binding.username.setError("Enter at least 3 characters");
            valid = false;
        } else {
            binding.username.setError(null);
        }

        if (passwd.isEmpty() || !validatePassword(passwd)) {
            binding.password.setError("Password must between 8 and 15 alphanumeric characters and Atleast 1 letter, 1 number, 1 special character and SHOULD NOT start with a special character ");
            valid = false;
        } else {
            binding.password.setError(null);
        }

        return valid;
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
