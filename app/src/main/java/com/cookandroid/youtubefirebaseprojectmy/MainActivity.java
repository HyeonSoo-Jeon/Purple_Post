package com.cookandroid.youtubefirebaseprojectmy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.cookandroid.youtubefirebaseprojectmy.adapters.PostAdapter;
import com.cookandroid.youtubefirebaseprojectmy.keyPress.BackPressCloseHandler;
import com.cookandroid.youtubefirebaseprojectmy.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BackPressCloseHandler backPressCloseHandler;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView mPostRecyclerView;

    private PostAdapter mAdapter;
    private List<Post> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mPostRecyclerView = findViewById(R.id.main_recyclerview);

        findViewById(R.id.main_post_edit).setOnClickListener(this);
        findViewById(R.id.main_logout).setOnClickListener(this);
        findViewById(R.id.client_info_btn).setOnClickListener(this);
        findViewById(R.id.client_info_text).setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mDatas = new ArrayList<>();
        mStore.collection(FirebaseID.post)
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            mDatas.clear();
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                String documentId = String.valueOf(shot.get(FirebaseID.documentId));
                                String nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                String title = String.valueOf(snap.get(FirebaseID.title));
                                String contents = String.valueOf(shot.get(FirebaseID.contents));
                                String uid = String.valueOf(shot.get(FirebaseID.userId));

                                Timestamp postTimestamp = (Timestamp) shot.get(FirebaseID.timestamp);
                                if (postTimestamp!=null){
                                    Date postDate = postTimestamp.toDate();
                                    Post data = new Post(documentId, nickname, title, contents, postDate,uid);
                                    mDatas.add(data);
                                }
                            }
                            mAdapter = new PostAdapter(mDatas);
                            mPostRecyclerView.setAdapter(mAdapter);

                            // 게시글 클릭했을 때
                            mAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Post temp = mDatas.get(position);
                                    String post_title = temp.getTitle();
                                    String post_nickname = temp.getNickname();
                                    Date post_date = temp.getDate();
                                    String post_contents = temp.getContents();
                                    String post_docId = temp.getDocumentId();
                                    String post_uid = temp.getUid();

                                    Intent intent = new Intent(MainActivity.this, WrittenPostActivity.class);

                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a yy/MM/dd ");
                                    TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
                                    sdf.setTimeZone(time);

                                    intent.putExtra("title",post_title);
                                    intent.putExtra("nickname",post_nickname);
                                    intent.putExtra("date",sdf.format(post_date));
                                    intent.putExtra("contents",post_contents);
                                    intent.putExtra("docId",post_docId);
                                    intent.putExtra("Uid",post_uid);

                                    startActivity(intent);
                                }
                            });
                        }

                    }
                });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 글쓰기 버튼
            case R.id.main_post_edit:
                startActivity(new Intent(this, PostActivity.class));
                break;
            // 로그아웃 버튼
            case R.id.main_logout:
                new AlertDialog.Builder(this)
                        .setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                signOut();
                                finish();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 취소시 처리 로직
                            }
                        })
                        .show();
                break;
            // 회원정보 버튼
            case R.id.client_info_btn:
            case R.id.client_info_text:
                startActivity(new Intent(this, ClientInfoActivity.class));
                finish();
                break;

            // 새로고침 버튼
//            case R.id.client_refresh_btn:
//            case R.id.client_refresh_text:
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//                break;
        }

    }
}