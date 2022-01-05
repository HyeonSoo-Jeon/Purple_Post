package com.cookandroid.youtubefirebaseprojectmy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cookandroid.youtubefirebaseprojectmy.R;
import com.cookandroid.youtubefirebaseprojectmy.model.Comments;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> implements View.OnClickListener {

    private List<Comments> datas;

    public CommentsAdapter(List<Comments> datas) {
        this.datas = datas;
    }

    // 클릭 시 리스너
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private PostAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(PostAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }
    // 여기 까지

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  yy/MM/dd ");
        TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
        sdf.setTimeZone(time);

        Comments data = datas.get(position);
        holder.nickname.setText(data.getNickname());
        holder.contents.setText(data.getContents());
        holder.commentDate.setText(sdf.format(data.getDate()));


    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onClick(View view) {

    }

    class CommentsViewHolder extends RecyclerView.ViewHolder {

        private TextView nickname;
        private TextView contents;
        private TextView commentDate;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            nickname = itemView.findViewById(R.id.item_my_comment_nickname);
            contents = itemView.findViewById(R.id.item_my_comment_contents);
            commentDate = itemView.findViewById(R.id.item_my_comment_date);
            itemView.findViewById(R.id.comment_setting_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }

                    }
                }
            });

            // 클릭 리스너
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }

                    }
                    return true;
                }
            });

        }
    }
}

