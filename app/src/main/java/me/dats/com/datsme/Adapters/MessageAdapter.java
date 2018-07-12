package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private Context mContext;
    private FirebaseAuth mAuth;
    String messagetime;

    public MessageAdapter(List<Messages> mMessageList, Context applicationContext) {
        this.mMessageList = mMessageList;
        this.mContext = applicationContext;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout2, parent, false));
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        Messages message = mMessageList.get(position);
        String meesage_sender_id = mAuth.getCurrentUser().getUid();
        String from_user = message.getFrom();
        String message_type = message.getType();
        long time = message.getTime();
//        java.sql.Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
//        java.sql.Date date = new java.sql.Date(timeStamp.getTime());
//        Log.i("TAG:",""+date);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (message_type.equals("text")) {

            holder.messageImage.setVisibility(View.GONE);
            if (from_user.equals(meesage_sender_id)) {

                holder.messageText.setBackgroundResource(R.drawable.message_text_background2);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messagebackground.setGravity(Gravity.RIGHT);
                holder.time1.setGravity(Gravity.RIGHT);
                holder.msg.setBackgroundResource(R.drawable.message_text_background2);

            } else {

                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messagebackground.setGravity(Gravity.LEFT);
                holder.time1.setGravity(Gravity.LEFT);
                holder.messageText.setTextColor(Color.BLACK);
                holder.msg.setBackgroundResource(R.drawable.message_text_background);

            }

            holder.messageText.setText(message.getMessage());

        } else {

            holder.messageText.setVisibility(View.INVISIBLE);
            holder.messageText.setPadding(0,0,0,0);
            if (from_user.equals(meesage_sender_id)) {
                holder.messageImage.setPadding(5,0,0,0);
                Picasso.get().load(message.getMessage())
                        .placeholder(R.drawable.default_avatar).into(holder.messageImage);
            }

        }
        messagetime = DateUtils.formatDateTime(mContext, time, DateUtils.FORMAT_SHOW_TIME);
        holder.time1.setText(messagetime);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        //        public CircleImageView profileImage;
        public ImageView messageImage;
        public RelativeLayout messagebackground;
        public TextView time1;
        public LinearLayout msg;


        public MessageViewHolder(View itemView) {
            super(itemView);

//            profileImage = itemView.findViewById(R.id.message_image);
            messageText = itemView.findViewById(R.id.message_text);
            messagebackground = itemView.findViewById(R.id.message_root_layout);
            messageImage = itemView.findViewById(R.id.message_image_layout);
            time1 = itemView.findViewById(R.id.text_message_time);
            msg=itemView.findViewById(R.id.message_layout);
        }
    }
}
