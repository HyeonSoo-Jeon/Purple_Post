package com.cookandroid.youtubefirebaseprojectmy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MyCommentPopupActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_comment_popup);

        findViewById(R.id.comment_delete).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.comment_delete:
                Intent intent = getIntent();
                String commentID = intent.getStringExtra("commentID");
                String docId = intent.getStringExtra("docID");

                Intent newIntent = new Intent(MyCommentPopupActivity.this, CommentDeletePopUpActivity.class);
                newIntent.putExtra("commentID", commentID);
                newIntent.putExtra("docID",docId);

                startActivity(newIntent);
                finish();
                break;
        }
    }
}