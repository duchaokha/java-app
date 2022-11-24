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

public class RegisterActive extends AppCompatActivity {
    TextView btn;
    Button btnRegister;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    private EditText inputuserName,inputEmail,inputPassword,inputConformPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_active);
        btn = findViewById(R.id.textViewLogin);
        inputuserName = findViewById(R.id.inputEmail);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConformPassword = findViewById(R.id.inputConformPassword);
        btnRegister =findViewById(R.id.btnRegister);
        mAuth =FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(RegisterActive.this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataRegister();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActive.this, Login_Active.class));
            }
        });
    }
    private void  checkDataRegister()
    {
        String userName = inputuserName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConformPassword.getText().toString();
        if(userName.isEmpty() || userName.length() <= 7)
        {
            showError(inputuserName , "Your user name is minimum 8 characters!");
        } else if (email.isEmpty() || !email.contains("@")) {
            showError(inputEmail , "It isn't email!");
        }
        else if(password.isEmpty() || password.length() <= 7)
        {
            showError(inputConformPassword , "Your password is minimum 8 characters!");
        }
        else if(confirmPassword.isEmpty() || !confirmPassword.equals(password))
        {
            showError(inputConformPassword , "Incorrect password!");
        }

        else
        {
            mLoadingBar.setTitle("Loading Register");
            mLoadingBar.setMessage("please wait");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActive.this, "Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(RegisterActive.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void showError(EditText input, String s) {
        input.setError(s);
    }
}