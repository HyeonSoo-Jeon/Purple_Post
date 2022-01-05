package com.cookandroid.youtubefirebaseprojectmy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class CommentReportPopUpActivity1 extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment_report_popup1);

        findViewById(R.id.popup_report).setOnClickListener(this);
        findViewById(R.id.popup_report_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.popup_report:
                startActivity(new Intent(CommentReportPopUpActivity1.this, CommentReportPopUpActivity2.class));
                finish();
                break;

            case R.id.popup_report_cancel:
                finish();
                break;
        }
    }
}