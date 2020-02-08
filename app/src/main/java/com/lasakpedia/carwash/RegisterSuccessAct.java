package com.lasakpedia.carwash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

public class RegisterSuccessAct extends AppCompatActivity {

    Animation app_splash, btt, ttb;
    AppCompatImageView icon_success;
    AppCompatTextView textView2, textView3;
    AppCompatButton btn_washnow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        btn_washnow = findViewById(R.id.btn_washnow);
        icon_success = findViewById(R.id.icon_success);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        // load animation
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        btt = AnimationUtils.loadAnimation(this, R.anim.btt);
        ttb = AnimationUtils.loadAnimation(this, R.anim.ttb);

        // run animation
        btn_washnow.startAnimation(btt);
        icon_success.startAnimation(app_splash);
        textView2.startAnimation(ttb);
        textView3.startAnimation(ttb);


        btn_washnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoHome = new Intent(RegisterSuccessAct.this, HomeAct.class);
                startActivity(gotoHome);
            }
        });
    }
}
