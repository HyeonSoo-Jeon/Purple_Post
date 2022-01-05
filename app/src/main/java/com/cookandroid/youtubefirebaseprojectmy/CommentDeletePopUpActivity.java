package com.cookandroid.youtubefirebaseprojectmy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.firebase.firestore.FirebaseFirestore;

public class CommentDeletePopUpActivity extends Activity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment_delete_popup);

        findViewById(R.id.popup_delete).setOnClickListener(this);
        findViewById(R.id.popup_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            // 삭제 버튼
            case R.id.popup_delete:
                Intent intent = getIntent();
                String commentID = intent.getStringExtra("commentID");
                String docId = intent.getStringExtra("docID");


                mStore.collection("post")
                        .document(docId)
                        .collection("comments")
                        .document(commentID)
                        .delete();
                finish();
                break;
            // 취소 버튼
            case R.id.popup_cancel:
                finish();
                break;
        }
    }
}