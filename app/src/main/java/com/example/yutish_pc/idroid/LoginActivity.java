package com.example.yutish_pc.idroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText LoginEmail;
    private EditText LoginPassword;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference usersreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_butt);
        LoginEmail = (EditText) findViewById(R.id.login_email);
        LoginPassword = (EditText) findViewById(R.id.login_password);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        usersreference = FirebaseDatabase.getInstance().getReference().child("Users");

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                                                  //checking the parameters to be filled
                String email = LoginEmail.getText().toString();
                String password = LoginPassword.getText().toString();

                LoginUserAccount(email, password);
            }
        });

    }

    private void LoginUserAccount(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Please write your Email", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please write your Password", Toast.LENGTH_LONG).show();
        } else {
            loadingBar.setIcon(R.drawable.common_google_signin_btn_icon_light_normal);
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are verifying your credentials");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String online_user_id = mAuth.getCurrentUser().getUid();
                        String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                        usersreference.child(online_user_id).child("device_token").setValue(DeviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();                                                           // logging in the user....
                            }
                        });

                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong Email or Password, please write your valid Email or Password", Toast.LENGTH_LONG).show();
                    }

                    loadingBar.dismiss();
                }
            });
        }
    }
}
