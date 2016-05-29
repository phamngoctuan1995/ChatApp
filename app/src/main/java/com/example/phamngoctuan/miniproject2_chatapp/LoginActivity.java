package com.example.phamngoctuan.miniproject2_chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.utilities.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity implements LoginCallback {
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    ImageView _logo;
    CheckBox _savePassword;
    ProgressDialog _progressDialog = null;

    private void InitView()
    {
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        _logo = (ImageView) findViewById(R.id.imv_logo);
        _savePassword = (CheckBox) findViewById(R.id.cb_savepassword);

        _savePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(MyConstant.SETTING_REF, MODE_PRIVATE).edit();
                    editor.putBoolean("isSaveAccount", isChecked);
                    editor.commit();
                }
            }
        });

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        _logo.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_animation));

        SharedPreferences ref = getSharedPreferences(MyConstant.SETTING_REF, MODE_PRIVATE);
        Boolean isSave = ref.getBoolean("isSaveAccount", false);

        if (isSave)
        {
            SharedPreferences acc = getSharedPreferences(MyConstant.ACCOUNT_REF, MODE_PRIVATE);
            String email = acc.getString("email", "");
            String password = acc.getString("password", "");
            _emailText.setText(email);
            _passwordText.setText(password);
            _savePassword.setChecked(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitView();
        Firebase.setAndroidContext(getApplicationContext());
        Intent i = new Intent(this, RegistrationService.class);
        startService(i);
    }

    public void login() {
        Log.d("debug", "Login");

        if (!validate()) {
            return;
        }

        if (!MyConstant.checkInternetAvailable(getApplicationContext()))
        {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        _loginButton.setEnabled(false);

        _progressDialog = new ProgressDialog(LoginActivity.this);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage("Authenticating...");
        _progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        new LoginAsynctask(this, email, password).execute();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    public void onLoginSuccess() {
        Log.d("debug", "Login success");
        Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
        _loginButton.setEnabled(true);

        if (_savePassword.isChecked()) {
            SharedPreferences.Editor setting = getSharedPreferences(MyConstant.SETTING_REF, MODE_PRIVATE).edit();
            setting.putBoolean("isSaveAccount", true);
            setting.commit();
        }
        SharedPreferences.Editor acc = getSharedPreferences(MyConstant.ACCOUNT_REF, MODE_PRIVATE).edit();
        acc.putString("email", _emailText.getText().toString());
        acc.putString("password", _passwordText.getText().toString());
        acc.commit();

        if(_progressDialog != null)
            _progressDialog.dismiss();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginFailed() {
        Log.d("debug", "Login failed");
        _emailText.setError("Authentication failed!");
//        Toast.makeText(getBaseContext(), "Authentication failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);

        if (_progressDialog != null)
            _progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        if (email.isEmpty()) {
            _emailText.setError("Enter a valid  address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 30) {
            _passwordText.setError("Between 4 and 30 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

interface LoginCallback
{
    public void onLoginSuccess();
    public void onLoginFailed();
}

class LoginAsynctask extends AsyncTask<Void, Void, Boolean>
{
    WeakReference<LoginCallback> _callback;
    String _username, _password;
    Boolean _isSuccess;

    LoginAsynctask(LoginCallback cb, String usr, String pw)
    {
        _callback = new WeakReference<LoginCallback>(cb);
        _username = usr;
        _password = pw;
        _isSuccess = true;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Document document = Jsoup.connect("http://www.spoj.com/login").data("login_user", _username)
                                                                          .data("password", _password).post();

            if (document != null)
            {
                Element element= document.getElementsByClass("avatar").first();
                if (element == null)
                    _isSuccess = false;

//                File root = Environment.getExternalStorageDirectory();
//                File file = new File(root, "webhtml");
//                FileOutputStream fout = new FileOutputStream(file);
//                OutputStreamWriter out = new OutputStreamWriter(fout);
//                out.write(document.outerHtml());
            }
            else
                _isSuccess = false;
        } catch (Exception e) {
            _isSuccess = false;
            e.printStackTrace();
        }
        return _isSuccess;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        LoginCallback cb = _callback.get();
        if (cb != null) {
            if (aBoolean)
                cb.onLoginSuccess();
            else
                cb.onLoginFailed();
        }
    }
}