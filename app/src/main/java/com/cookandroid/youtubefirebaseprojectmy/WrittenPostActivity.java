package com.cookandroid.youtubefirebaseprojectmy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.youtubefirebaseprojectmy.adapters.CommentsAdapter;
import com.cookandroid.youtubefirebaseprojectmy.adapters.PostAdapter;
import com.cookandroid.youtubefirebaseprojectmy.model.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrittenPostActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText mComment;
    private RecyclerView mCommentRecyclerView;

    private CommentsAdapter mAdapter;
    private List<Comments> mDatas;

    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_written_post);


        mComment = findViewById(R.id.written_comment);
        mCommentRecyclerView = findViewById(R.id.written_recyclerview);

        findViewById(R.id.written_post_close_button).setOnClickListener(this);
        findViewById(R.id.written_delete_btn).setOnClickListener(this);
        findViewById(R.id.written_comment_btn).setOnClickListener(this);


        // Main에서 게시글 정보 받아오기
        TextView view_post_title = (TextView) findViewById(R.id.written_post_title);
        TextView view_nickname = (TextView) findViewById(R.id.written_post_nicname);
        TextView view_date = (TextView) findViewById(R.id.written_post_date);
        TextView view_contents = (TextView) findViewById(R.id.written_post_contents);

        view_contents.setMovementMethod(new ScrollingMovementMethod());

        intent = getIntent();

        String title = intent.getStringExtra("title");
        String nickname = intent.getStringExtra("nickname");
        String date = intent.getStringExtra("date");
        String contents = intent.getStringExtra("contents");
        String Uid = intent.getStringExtra("Uid");

        view_post_title.setText(title);
        view_nickname.setText("작성자 : " + nickname);
        view_date.setText(date);
        view_contents.setText(contents);

        // 작성자만 delete 버튼이 보인다
        if (!Uid.equals(mAuth.getUid())) {
            Button delete_btn = (Button) findViewById(R.id.written_delete_btn);
            delete_btn.setVisibility(delete_btn.INVISIBLE);
        }

    }

    @Override
    protected void onStart() {
        Intent intent = getIntent();
        String DocId = intent.getStringExtra("docId");

        super.onStart();
        mDatas = new ArrayList<>();
        mStore.collection(FirebaseID.post)
                .document(DocId)
                .collection("comments")
                .orderBy(FirebaseID.timestamp, Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            mDatas.clear();
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                String nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                String contents = String.valueOf(shot.get(FirebaseID.comments));
                                Timestamp postTimestamp = (Timestamp) shot.get(FirebaseID.timestamp);
                                String UID = String.valueOf(shot.get(FirebaseID.userId));
                                String commentID=String.valueOf(shot.get(FirebaseID.commentID));
                                if (postTimestamp != null) {
                                    Date postDate = postTimestamp.toDate();
                                    Comments data = new Comments(nickname, contents, postDate,UID,commentID,DocId);
                                    mDatas.add(data);
                                }
                            }

                            mAdapter = new CommentsAdapter(mDatas);
                            mCommentRecyclerView.setAdapter(mAdapter);

                            // 댓글 클릭했을 때
                            mAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Comments temp = mDatas.get(position);
                                    String commentID = temp.getCommentId();
                                    String com_UID = temp.getUid();
                                    String docId = temp.getDocId();

//                                    Log.v("IDID", com_UID + " / " + mAuth.getUid());
//                                    Log.v("IDID", docId + " / " + commentID);

                                    // 현재 유저와 댓글 유저가 같을 때
                                    if(com_UID.equals(mAuth.getUid())){
                                        Intent intent = new Intent(WrittenPostActivity.this, MyCommentPopupActivity.class);
                                        intent.putExtra("commentID", commentID);
                                        intent.putExtra("docID",docId);

                                        startActivity(intent);
                                    }
                                    else{
                                        Intent intent = new Intent(WrittenPostActivity.this, CommentPopUpActivity.class);
                                        startActivity(intent);

                                    }
                                }
                            });

                        }

                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 닫기 버튼
            case R.id.written_post_close_button:
                finish();
                break;

            // 게시글 게시
            case R.id.written_delete_btn:
                new AlertDialog.Builder(this)
                        .setTitle("게시물 삭제")
                        .setMessage("정말로 삭제하시겠습니까?")
//                        .setIcon(android.R.drawable.ic_secure)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = getIntent();
                                String documentId = intent.getStringExtra("docId");
                                mStore.collection("post")
                                        .document(documentId)
                                        .delete();
                                Toast.makeText(WrittenPostActivity.this, "게시글이 삭제 되었습니다!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(WrittenPostActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 취소시 처리 로직
                            }
                        })
                        .show();
                break;

            // 댓글 게시 버튼
            case R.id.written_comment_btn:
                String comment = mComment.getText().toString();

                // 공백 없애기
                String temp = comment.replaceAll("\n\n\n", "\n\n");
                while (!comment.equals(temp)) {
                    comment = temp;
                    temp = comment.replaceAll("\n\n\n", "\n\n");
                    Log.v("what", comment + " / " + temp);
                }

                if (comment.length() == 0 || comment == "\n" || comment == " ") {
                    Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();

                } else {

                    String docId = intent.getStringExtra("docId");

                    String finalComment = comment;
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
                                            String UID = mAuth.getUid();

                                            Map<String, Object> data = new HashMap<>();
                                            String commentId = mStore.collection(FirebaseID.post).document(docId).collection("comments").document().getId();
                                            data.put(FirebaseID.comments, finalComment);
                                            data.put(FirebaseID.nickname, nickname);
                                            data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());
                                            data.put(FirebaseID.userId, UID);
                                            data.put(FirebaseID.commentID, commentId);
                                            data.put(FirebaseID.documentId,docId);
                                            mStore.collection(FirebaseID.post).document(docId).collection("comments").document(commentId).set(data, SetOptions.merge());

                                            mComment.setText(null);
                                            // 깜빡임 없애기
                                            mComment.setInputType(EditorInfo.TYPE_NULL);
                                            // 키보드 없애기
                                            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                                        }
                                    }
                                }
                            });
                }
                break;

        }
    }
}