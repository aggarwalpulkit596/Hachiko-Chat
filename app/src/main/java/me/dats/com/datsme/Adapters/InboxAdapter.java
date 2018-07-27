package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Activities.ChatActivity;
import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

    private ArrayList<Messages> messages;
    private Context mContext;
    public InboxAdapter(ArrayList<Messages> messages, Context mContext) {
        this.messages = messages;
        this.mContext = mContext;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_layout2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Messages m = messages.get(position);
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users").child(m.fuid);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users u = dataSnapshot.getValue(Users.class);
                Picasso.get().load(u.getThumb_image()).centerCrop().fit().placeholder(R.drawable.defaultavatar).into(holder.imageView);
                holder.name.setText(u.getName().toUpperCase());
                String time = DateUtils.formatDateTime(mContext, m.getTime(), DateUtils.FORMAT_SHOW_TIME);
                holder.time.setText(time);
                holder.itemView.setOnClickListener(v -> {
                    Intent i = new Intent(mContext, ChatActivity.class);
                    i.putExtra("from_user_id", messages.get(position).fuid);
                    i.putExtra("userName", u.getName());
                    mContext.startActivity(i);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.message.setText(m.getMessage());
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message, name, time;
        public CircleImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.user_single_status);
            name = view.findViewById(R.id.user_single_name);
            imageView = view.findViewById(R.id.user_image);
            time = view.findViewById(R.id.user_lastmessage_time);

        }
    }
}
