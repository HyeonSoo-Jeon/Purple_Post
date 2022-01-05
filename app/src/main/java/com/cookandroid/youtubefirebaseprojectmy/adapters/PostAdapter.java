package com.cookandroid.youtubefirebaseprojectmy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookandroid.youtubefirebaseprojectmy.R;
import com.cookandroid.youtubefirebaseprojectmy.model.Post;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> datas;

    public PostAdapter(List<Post> datas) {
        this.datas = datas;
    }

    // 클릭 시 리스너
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    // 여기 까지

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  yy/MM/dd ");
        TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
        sdf.setTimeZone(time);

        Post data = datas.get(position);
        holder.nicname.setText("작성자 : " + data.getNickname());
        holder.title.setText(data.getTitle());
        holder.contents.setText(data.getContents());
        holder.postDate.setText(sdf.format(data.getDate()));
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;

        private TextView nicname;
        private TextView title;
        private TextView contents;
        private TextView postDate;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            nicname = itemView.findViewById(R.id.item_post_nicname);
            title = itemView.findViewById(R.id.item_post_title);
            contents = itemView.findViewById(R.id.item_post_contents);
            postDate = itemView.findViewById(R.id.item_post_date);

            // 클릭 리스너
            itemView.setOnClickListener(new View.OnClickListener() {
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
        }
    }
}

