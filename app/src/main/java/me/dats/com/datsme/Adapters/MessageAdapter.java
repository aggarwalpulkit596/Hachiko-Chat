package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private Context mContext;

    public MessageAdapter(List<Messages> mMessageList, Context applicationContext) {
        this.mMessageList = mMessageList;
        this.mContext = applicationContext;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout2, parent, false));
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        Messages message = mMessageList.get(position);

        String from_user = message.getFrom();
        String message_type = message.getType();
        long time = message.getTime();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.displayName.setText(name);

                Picasso.get().load(image)
                        .placeholder(R.drawable.default_avatar).into(holder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (message_type.equals("text")) {

            holder.messageText.setText(message.getMessage());
            holder.messageImage.setVisibility(View.GONE);


        } else {

            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(message.getMessage())
                    .placeholder(R.drawable.default_avatar).into(holder.messageImage);

        }
        String msgtime = DateUtils.formatDateTime(mContext, time, DateUtils.FORMAT_SHOW_TIME);


        holder.timeText.setText(msgtime);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, displayName, timeText;
        public CircleImageView profileImage;
        public ImageView messageImage;


        public MessageViewHolder(View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.message_image);
            messageText = itemView.findViewById(R.id.message_text);
            messageImage = itemView.findViewById(R.id.message_image_layout);
            displayName = itemView.findViewById(R.id.name_text_layout);
            timeText = itemView.findViewById(R.id.time_text_layout);
        }
    }
}

