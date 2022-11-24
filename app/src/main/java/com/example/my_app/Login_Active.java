package com.example.my_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Active extends AppCompatActivity {
    TextView btn;
    Button btnLogin;
    EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_active);
        btnLogin =findViewById(R.id.btnLogin);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btn = findViewById(R.id.textViewSignUp);
        mAuth =FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(Login_Active.this);

        btn.setOnClickListener(view -> startActivity(new Intent(Login_Active.this, RegisterActive.class)));
        btnLogin.setOnClickListener(view -> checkDataLogin());
    }

    private void checkDataLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || !email.contains("@")) {
            showError(inputEmail , "It is not an email!");
        }
        else if(password.isEmpty() || password.length() < 7) {
            showError(inputPassword, "Password minimum 8 character");
        }
        else
        {
            mLoadingBar.setTitle("Loading title");
            mLoadingBar.setMessage("please wait!");
            mLoadingBar.setCanceledOnTouchOutside(false); // cancel defaule behavier
            mLoadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        mLoadingBar.dismiss();
                        Intent intent = new Intent(Login_Active.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("Topic", email);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(Login_Active.this, "Error !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private void showError(EditText input, String s) {
        input.setError(s);
    }

}
