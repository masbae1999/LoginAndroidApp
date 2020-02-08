package com.lasakpedia.carwash;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class RegisterTwoAct extends AppCompatActivity {

    AppCompatButton btn_reg2continue, btn_add_photo;
    AppCompatEditText full_name, phone_number;
    AppCompatSpinner genderCmBx;
    AppCompatImageView pic_photo_register_user;

    Uri photo_location;
    Integer photo_max = 1;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    DatabaseReference reference;
    StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);

        getUsernameLocal();

        btn_reg2continue = findViewById(R.id.btn_reg2continue);
        btn_add_photo = findViewById(R.id.btn_add_photo);
        full_name = findViewById(R.id.full_name);
        phone_number = findViewById(R.id.phone_number);
        genderCmBx = findViewById(R.id.genderCmBx);
        pic_photo_register_user = findViewById(R.id.pic_photo_register_user);


        String[] pilihan_gender = getResources().getStringArray(R.array.gender_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spin_gender, R.id.txSpinGender, pilihan_gender);
        genderCmBx.setAdapter(adapter);

        //menyimpan ke firebase
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        storage = FirebaseStorage.getInstance().getReference().child("Photousers").child(username_key_new);

        btn_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhoto();
                //validasi untuk file photo
            }
        });

        btn_reg2continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ubah state menjadi loading
                btn_reg2continue.setEnabled(false);
                btn_reg2continue.setText("Loading...");

                final String regfullname = full_name.getText().toString();
                final String regphone = phone_number.getText().toString();

                if (photo_location != null) {
                    final StorageReference storageReference1 = storage.child(System.currentTimeMillis() + "." + getFileExtension(photo_location));
                    storageReference1.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //menyimpan data url_photo dari storage ke database realtime
                            storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String uri_photo = uri.toString();
                                    reference.getRef().child("url_photo_profile").setValue(uri_photo);
                                }
                            });
                        }
                    });
                }

                if (regfullname.isEmpty() || regfullname == null) {
                    Toast.makeText(getApplicationContext(), "Please fill in your Full Name!", Toast.LENGTH_SHORT).show();
                    btn_reg2continue.setEnabled(true);
                    btn_reg2continue.setText("Continue");
                    full_name.requestFocus();
                } else {
                    if (regphone.isEmpty() || regphone == null) {
                        Toast.makeText(getApplicationContext(), "Please fill in your Phone Number!", Toast.LENGTH_SHORT).show();
                        btn_reg2continue.setEnabled(true);
                        btn_reg2continue.setText("Continue");
                        phone_number.requestFocus();
                    } else {
                        reference.getRef().child("full_name").setValue(full_name.getText().toString());
                        reference.getRef().child("phone_number").setValue(phone_number.getText().toString());
                        reference.getRef().child("gender").setValue(genderCmBx.getSelectedItem());

                        //berpindah activity
                        Intent gotosuccess = new Intent(RegisterTwoAct.this, RegisterSuccessAct.class);
                        startActivity(gotosuccess);
                    }
                }
            }
        });
    }

    String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void findPhoto() {
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photo_max);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            Picasso.with(this).load(photo_location).centerCrop().fit().into(pic_photo_register_user);
        }
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }
}
