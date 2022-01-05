package com.cookandroid.youtubefirebaseprojectmy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cookandroid.youtubefirebaseprojectmy.adapters.PostAdapter;
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

public class MyPostActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView mPostRecyclerView;

    private PostAdapter mAdapter;
    private List<Post> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        mPostRecyclerView = findViewById(R.id.my_post_recyclerview);


        findViewById(R.id.my_post_close_button).setOnClickListener(this);
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
                                if (mAuth.getUid().equals(String.valueOf((shot.get(FirebaseID.userId))))) {
                                    String documentId = String.valueOf(shot.get(FirebaseID.documentId));
                                    String nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                    String title = String.valueOf(snap.get(FirebaseID.title));
                                    String contents = String.valueOf(shot.get(FirebaseID.contents));
                                    String uid = String.valueOf(shot.get(FirebaseID.userId));

                                    Timestamp postTimestamp = (Timestamp) shot.get(FirebaseID.timestamp);
                                    if (postTimestamp != null) {
                                        Date postDate = postTimestamp.toDate();
                                        Post data = new Post(documentId, nickname, title, contents, postDate, uid);
                                        mDatas.add(data);
                                    }
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

                                    Intent intent = new Intent(MyPostActivity.this, WrittenPostActivity.class);

                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a yy/MM/dd ");
                                    TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
                                    sdf.setTimeZone(time);

                                    intent.putExtra("title", post_title);
                                    intent.putExtra("nickname", post_nickname);
                                    intent.putExtra("date", sdf.format(post_date));
                                    intent.putExtra("contents", post_contents);
                                    intent.putExtra("docId", post_docId);
                                    intent.putExtra("Uid", post_uid);

                                    startActivity(intent);
                                }
                            });
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_post_close_button:
                finish();
        }
    }
}