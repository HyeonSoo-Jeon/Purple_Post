package com.cookandroid.youtubefirebaseprojectmy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private EditText mTitle, mContents;
    private String nickname;

    // 종료할 때 에러 메시지
    public void closeAlert() {
        new AlertDialog.Builder(this)
                .setTitle("작성 취소")
                .setMessage("작성 중이던 글은 저장되지 않습니다.")
                .setIcon(R.drawable.ic_close)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 취소시 처리 로직
                    }
                })
                .show();
    }

    // 제목이 없을 때 에러 메시지
    public void titleNullAlert() {
        new AlertDialog.Builder(this)
                .setTitle("업로드 오류")
                .setMessage("제목을 입력하세요.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 실행내용
                    }
                })
                .show();
    }

    // 내용이 없을 때 에러 메시지
    public void contentNullAlert() {
        new AlertDialog.Builder(this)
                .setTitle("업로드 오류")
                .setMessage("내용을 입력하세요.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 실행내용
                    }
                })
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mTitle = findViewById(R.id.post_title_edit);
        mContents = findViewById(R.id.post_contents_edit);

        findViewById(R.id.post_save_button).setOnClickListener(this);
        findViewById(R.id.post_close_button).setOnClickListener(this);

        // 닉네임 가져오기
        if (mAuth.getCurrentUser() != null) {
            mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {
                                nickname = (String) task.getResult().getData().get(FirebaseID.nickname);
                            }
                        }
                    });
        }

        // 패스워드 입력 후 엔터 입력시 로그인 버튼 클릭
        mTitle.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int KeyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && KeyCode == KeyEvent.KEYCODE_ENTER) {
                    // 수행 버튼
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        closeAlert();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 저장 버튼 누를 때
            case R.id.post_save_button:
                String title = mTitle.getText().toString();
                String content = mContents.getText().toString();
                // 제목이 없을 때
                if (title.length() == 0) {
                    titleNullAlert();
                }
                // 내용이 없을 때
                else if (content.length() == 0) {
                    contentNullAlert();
                }
                // 다 있을 때
                else {
                    if (mAuth.getCurrentUser() != null) {
                        String postId = mStore.collection(FirebaseID.post).document().getId();
                        Map<String, Object> data = new HashMap<>();
                        data.put(FirebaseID.userId, mAuth.getCurrentUser().getUid());
                        data.put(FirebaseID.nickname, nickname);
                        data.put(FirebaseID.title, mTitle.getText().toString());
                        data.put(FirebaseID.contents, mContents.getText().toString());
                        data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());
                        data.put(FirebaseID.documentId, postId);
                        mStore.collection(FirebaseID.post).document(postId).set(data, SetOptions.merge());
                        finish();
                    }
                }
                break;
            // 닫기 버튼 누를 떄
            case R.id.post_close_button:
                closeAlert();
                break;
        }
    }
}