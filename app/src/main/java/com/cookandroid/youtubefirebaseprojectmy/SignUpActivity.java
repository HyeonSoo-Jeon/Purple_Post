package com.cookandroid.youtubefirebaseprojectmy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private TextView mEmailCheckFinish, mWrittenEmail, mPhoneCheckFinish;
    private Button mDoubleCheck, mPhoneAuthCheck, mPhoneAuth;
    private EditText mEmailText, mPasswordText, mNickname, mPhoneNum, mName, mAuthNumber;

    private String SMSCode;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailCheckFinish = findViewById(R.id.email_check_finish);
        mWrittenEmail = findViewById(R.id.written_email);
        mPhoneCheckFinish = findViewById(R.id.phone_check_finish);

        mDoubleCheck = findViewById(R.id.double_check);
        mPhoneAuthCheck = findViewById(R.id.phone_auth_check);
        mPhoneAuth = findViewById(R.id.phone_auth);

        mNickname = findViewById(R.id.sign_nickname);
        mEmailText = findViewById(R.id.sign_email);
        mPasswordText = findViewById(R.id.sign_password);
        mPhoneNum = findViewById(R.id.sign_phone);
        mName = findViewById(R.id.sign_name);
        mAuthNumber = findViewById(R.id.auth_number);

        Button mSignUp = (Button) findViewById(R.id.sign_success);

        // ??????
        findViewById(R.id.sign_success).setOnClickListener(this);
        findViewById(R.id.signup_temp).setOnClickListener(this);
        findViewById(R.id.double_check).setOnClickListener(this);
        findViewById(R.id.phone_auth).setOnClickListener(this);
        findViewById(R.id.signUp_close_button).setOnClickListener(this);
        findViewById(R.id.written_email).setOnClickListener(this);
        findViewById(R.id.phone_auth_check).setOnClickListener(this);

        // ???????????? ?????? ??? ?????? ????????? ????????? ?????? ??????
        mPasswordText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int KeyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && KeyCode == KeyEvent.KEYCODE_ENTER) {
                    mSignUp.performClick();
                    return true;
                }
                return false;
            }
        });

        // ??? ??????
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                SMSCode = credential.getSmsCode();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
            }
        };
    }

    // @?????? ?????? ????????????
    public String change(String str) {
        if (str.length() != 0) {
            int index = str.indexOf('@');
            char temp = str.charAt(index + 1);
            int tempInt = (int) temp;
            if (tempInt > 65 && tempInt < 91) {
                StringBuffer sb = new StringBuffer(str);
                tempInt += 32;
                temp = (char) tempInt;
                sb.replace(index + 1, index + 2, Character.toString(temp));

                str = sb.toString();
            }
        }
        return str;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // ????????????
            case R.id.signUp_close_button:
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setMessage("?????????????????????????\n(??????????????? ???????????? ????????????.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // ????????? ?????? ??????
                            }
                        })
                        .show();
                break;
            // ????????? ?????? ??????
            case R.id.double_check:
                String email = change(mEmailText.getText().toString());

                if (email.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                } else if (!email.contains("@")) {
                    Toast.makeText(SignUpActivity.this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    mStore.collection(FirebaseID.user)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> shot = document.getData();
                                            String userEmail = String.valueOf(shot.get(FirebaseID.email));
                                            Log.v("thooapp", userEmail);

                                            if (email.equals(userEmail)) {
                                                Toast.makeText(SignUpActivity.this, "????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                                                break;
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "?????????????????????!", Toast.LENGTH_SHORT).show();

                                                mEmailText.setVisibility(View.GONE);
                                                mWrittenEmail.setText(email);
                                                mWrittenEmail.setVisibility(View.VISIBLE);

                                                mEmailCheckFinish.setVisibility(View.VISIBLE);
                                                mDoubleCheck.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                }
                            });
                }
                break;

            // ????????? ??? ??????
            case R.id.written_email:
                new AlertDialog.Builder(this)
                        .setTitle("?????????")
                        .setMessage("?????? ?????? ???????????????????")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mWrittenEmail.setText(null);
                                mWrittenEmail.setVisibility(View.GONE);
                                mEmailText.setVisibility(View.VISIBLE);

                                mEmailCheckFinish.setVisibility(View.GONE);
                                mDoubleCheck.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // ????????? ?????? ??????
                            }
                        })
                        .show();

                break;

            // ????????? ??????
            case R.id.phone_auth:
                String tempNum = mPhoneNum.getText().toString();

                if (tempNum.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "?????????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    String phoneNumber = "+82 " + tempNum.substring(1);
//                String phoneNumber = "+1 650-555-3434";
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber.replace("-", ""))       // Phone number to verify
                                    .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                    mPhoneAuthCheck.setVisibility(View.VISIBLE);
                    findViewById(R.id.phone_auth_layout).setVisibility(View.VISIBLE);
                    mPhoneAuth.setVisibility(View.GONE);
                    findViewById(R.id.phone_auth_sent).setVisibility(View.VISIBLE);
                    Toast.makeText(SignUpActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
                break;

            // ???????????? ??????
            case R.id.phone_auth_check:
                String authNum = mAuthNumber.getText().toString();

                if (authNum.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!authNum.equals(SMSCode)) {
                        Toast.makeText(SignUpActivity.this, "??????????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "?????????????????????!", Toast.LENGTH_SHORT).show();
                        mPhoneAuthCheck.setVisibility(View.GONE);
                        mPhoneCheckFinish.setVisibility(View.VISIBLE);
                    }
                }
                break;

            // ???????????? ??????
            case R.id.sign_success:
                // ???????????? ???????????? ????????? ???
                if (mEmailCheckFinish.getVisibility() == View.GONE) {
                    Toast.makeText(SignUpActivity.this, "???????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                } else if (mPhoneCheckFinish.getVisibility() == View.GONE) {
                    Toast.makeText(SignUpActivity.this, "??????????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();

                } else {
                    mAuth.createUserWithEmailAndPassword(mEmailText.getText().toString(), mPasswordText.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put(FirebaseID.userId, user.getUid());
                                            userMap.put(FirebaseID.email, change(mEmailText.getText().toString()));
                                            userMap.put(FirebaseID.password, mPasswordText.getText().toString());
                                            userMap.put(FirebaseID.nickname, mNickname.getText().toString());
                                            userMap.put(FirebaseID.phoneNum, mPhoneNum.getText().toString());
                                            userMap.put(FirebaseID.name, mName.getText().toString());
                                            // FireStore??? ??????
                                            mStore.collection(FirebaseID.user).document(user.getUid()).set(userMap, SetOptions.merge());
                                            Toast.makeText(SignUpActivity.this, "???????????? ??????!\n\n?????? ?????????????????????!", Toast.LENGTH_LONG).show();
                                            signOut();
                                            finish();
                                        }

                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Sign up error.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;

            // ?????? ??????
            case R.id.signup_temp:
                startActivity(new Intent(this, PhoneAuthActivity.class));
                break;
        }
    }
}