package com.example.phamngoctuan.miniproject2_chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class LoginActivity extends AppCompatActivity {
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    ImageView _logo;
    CheckBox _savePassword;

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
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

        if (_savePassword.isChecked()) {
            SharedPreferences.Editor setting = getSharedPreferences(MyConstant.SETTING_REF, MODE_PRIVATE).edit();
            setting.putBoolean("isSaveAccount", true);
            setting.commit();

            SharedPreferences.Editor acc = getSharedPreferences(MyConstant.ACCOUNT_REF, MODE_PRIVATE).edit();
            acc.putString("email", _emailText.getText().toString());
            acc.putString("password", _passwordText.getText().toString());
            acc.commit();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
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

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
