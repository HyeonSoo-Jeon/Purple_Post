package com.cookandroid.youtubefirebaseprojectmy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.youtubefirebaseprojectmy.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ClientInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_info);

        // 버튼 클릭 리스너
        findViewById(R.id.info_close_button).setOnClickListener(this);
        findViewById(R.id.info_write).setOnClickListener(this);
        findViewById(R.id.info_written_post).setOnClickListener(this);
        findViewById(R.id.info_suggest).setOnClickListener(this);
        findViewById(R.id.info_logout).setOnClickListener(this);
        findViewById(R.id.client_out).setOnClickListener(this);
        findViewById(R.id.temp_btn).setOnClickListener(this);


        final TextView client_name = (TextView) findViewById(R.id.info_client_name);


//         사용자 이름 가져오기
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

                                String content = nickname + " 님 안녕하세요!";

                                String word = " 님 안녕하세요!";
                                int start = content.indexOf(word);
                                int end = start + word.length();

                                SpannableStringBuilder builder = new SpannableStringBuilder(content);
                                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                builder.setSpan(new StyleSpan(Typeface.NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                builder.setSpan(new RelativeSizeSpan(0.5f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                client_name.setText(builder);
                            }
                        }
                    }
                });

    }

    // Auth에서 사용자 삭제
    private void deleteUserAuth() {
        mAuth.getCurrentUser().delete();
    }

    // 로그아웃
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    // Firestore에서 사용자 삭제
    private void deleteUserFirestore() {
        mStore.collection("user")
                .document(mAuth.getUid())
                .delete();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // x 버튼
            case R.id.info_close_button:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

            // 글쓰기 버튼
            case R.id.info_write:
                startActivity(new Intent(this, PostActivity.class));
                finish();
                break;

            // 내가 쓴 글 버튼
            case R.id.info_written_post:
                // 만들어야함
                startActivity(new Intent(this, MyPostActivity.class));
                break;

            // 제안하기 버튼
            case R.id.info_suggest:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"handeul3304@gmail.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                startActivity(email);
                break;

            // 로그아웃 버튼
            case R.id.info_logout:
                new AlertDialog.Builder(this)
                        .setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                signOut();
                                finish();
                                startActivity(new Intent(ClientInfoActivity.this, LoginActivity.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 취소시 처리 로직
                            }
                        })
                        .show();
                break;

            // 회원 탈퇴 버튼
            case R.id.client_out:
                new AlertDialog.Builder(this)
                        .setTitle("회원 탈퇴")
                        .setMessage("정말로 탈퇴하시겠습니까?")
//                        .setIcon(android.R.drawable.ic_secure)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteUserFirestore();
                                deleteUserAuth();
                                signOut();
                                Toast.makeText(ClientInfoActivity.this, "탈퇴 되셨습니다!\n감사합니다!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ClientInfoActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 취소시 처리 로직
                                Toast.makeText(ClientInfoActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;

            case R.id.temp_btn:
                Toast.makeText(this, "임시 버튼", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}