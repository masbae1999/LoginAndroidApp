package com.lasakpedia.carwash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterOneAct extends AppCompatActivity {

    AppCompatButton btn_reg1continue;
    LinearLayoutCompat btn_back;
    AppCompatEditText username,email_address,password;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";

    DatabaseReference reference,ref_CheckUserExist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);

        btn_reg1continue = findViewById(R.id.btn_reg1continue);
        btn_back = findViewById(R.id.btn_back);
        username = findViewById(R.id.username);
        email_address = findViewById(R.id.email_address);
        password = findViewById(R.id.password);

        btn_reg1continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // ubah state menjadi loading
            btn_reg1continue.setEnabled(false);
            btn_reg1continue.setText("Loading ...");

            final String regusername = username.getText().toString();
            final String regpassword = password.getText().toString();
            final String regemail_address = email_address.getText().toString();
            ref_CheckUserExist = FirebaseDatabase.getInstance().getReference().child("Users").child(regusername);
            ref_CheckUserExist.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){ //cek apakah username sudah ada?
                        Toast.makeText(RegisterOneAct.this, "Username "+regusername+" is already taken.", Toast.LENGTH_SHORT).show();
                        btn_reg1continue.setEnabled(true);
                        btn_reg1continue.setText("Continue");
                        username.setText("");
                        password.setText("");
                        email_address.setText("");
                        username.requestFocus();
                    } else{
                        if (regusername.isEmpty() || regusername == null) {
                            Toast.makeText(getApplicationContext(), "Please fill in your username!", Toast.LENGTH_SHORT).show();
                            btn_reg1continue.setEnabled(true);
                            btn_reg1continue.setText("Continue");
                            username.requestFocus();
                        }else {
                            if (regemail_address.isEmpty() || regemail_address == null) {
                                Toast.makeText(getApplicationContext(), "Please fill in your email address!", Toast.LENGTH_SHORT).show();
                                btn_reg1continue.setEnabled(true);
                                btn_reg1continue.setText("Continue");
                                email_address.requestFocus();
                            } else {
                                if (regpassword.isEmpty() || regpassword == null) {
                                    Toast.makeText(getApplicationContext(), "Please fill in your password!", Toast.LENGTH_SHORT).show();
                                    btn_reg1continue.setEnabled(true);
                                    btn_reg1continue.setText("Continue");
                                    password.requestFocus();
                                } else {
                                    //menyimpan data kepada local storage (handphone)
                                    SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(username_key, username.getText().toString());
                                    editor.apply();

                                    //simpan ke database
                                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username.getText().toString());
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            dataSnapshot.getRef().child("username").setValue(username.getText().toString());
                                            dataSnapshot.getRef().child("email_address").setValue(email_address.getText().toString());
                                            dataSnapshot.getRef().child("password").setValue(password.getText().toString());
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                    //berpindah activity
                                    Toast.makeText(getApplicationContext(), "Username " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                                    Intent gotonextregister = new Intent(RegisterOneAct.this, RegisterTwoAct.class);
                                    startActivity(gotonextregister);
                                }
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
