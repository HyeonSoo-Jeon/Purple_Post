package com.cookandroid.youtubefirebaseprojectmy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cookandroid.youtubefirebaseprojectmy.keyPress.BackPressCloseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private BackPressCloseHandler backPressCloseHandler;

    private EditText mEmail, mPassword;

    private ImageView mLoginTitle1,mLoginTitle2, mLoginBack;
    private LinearLayout mLoginContent;

    private void eraseText() {
        mEmail.setText(null);
        mPassword.setText(null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        Button mLoginBtn = (Button) findViewById(R.id.login_success);

        mLoginTitle1 = findViewById(R.id.login_title1);
        mLoginTitle2 = findViewById(R.id.login_title2);
        mLoginBack = findViewById(R.id.logIn_Back);
        mLoginContent = findViewById(R.id.logIn_content);

        findViewById(R.id.login_signup).setOnClickListener(this);
        findViewById(R.id.login_success).setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);

        // ?????? ???????????????
        Animation animBack = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.disapearback);
        mLoginBack.startAnimation(animBack);

        Animation animTitle = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.apeartitle);
        mLoginTitle1.startAnimation(animTitle);

        Animation animTitle2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.disapeartitle);
        mLoginTitle2.startAnimation(animTitle2);

        Animation animContent = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.apearcontent);
        mLoginContent.startAnimation(animContent);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },2500);





        // ???????????? ?????? ??? ?????? ????????? ????????? ?????? ??????
        mPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int KeyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && KeyCode == KeyEvent.KEYCODE_ENTER) {
                    mLoginBtn.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        // ?????? ?????????
        if (user != null) {
            Toast.makeText(this, "?????? ?????????!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ???????????? ?????? ??????
            case R.id.login_signup:
                eraseText();
                startActivity(new Intent(this, SignUpActivity.class));
                break;

            // ????????? ?????? ??????
            case R.id.login_success:
                try {
                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            // mStore?????? ??? ????????????
                                            FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                                            mStore.collection(FirebaseID.user)
                                                    .document(mAuth.getUid())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                if (task.getResult() != null) {
                                                                    DocumentSnapshot snap = task.getResult();
                                                                    Map<String, Object> shot = snap.getData();
                                                                    String nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                                                    Toast.makeText(LoginActivity.this, "????????? ??????!\n???????????????. " + nickname+"???", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    });
                                            // ?????? ??????
                                            eraseText();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "?????????/??????????????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    break;
                    // ????????? ??????
                } catch (IllegalArgumentException e) {
                    Toast.makeText(LoginActivity.this, "?????????/??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }


        }
    }
}