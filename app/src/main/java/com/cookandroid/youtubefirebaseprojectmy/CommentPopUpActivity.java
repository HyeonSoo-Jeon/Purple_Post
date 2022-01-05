package com.cookandroid.youtubefirebaseprojectmy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class CommentPopUpActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment_pop_up);

        findViewById(R.id.comment_report).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.comment_report:
                startActivity(new Intent(CommentPopUpActivity.this, CommentReportPopUpActivity1.class));
                finish();
                break;
        }
    }
}