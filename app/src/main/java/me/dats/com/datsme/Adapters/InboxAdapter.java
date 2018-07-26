package me.dats.com.datsme.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

    private ArrayList<Messages> messages;

    public String otherUserid;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message, name;
        public CircleImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.user_single_status);
            name = (TextView) view.findViewById(R.id.user_single_name);
            imageView = (CircleImageView) view.findViewById(R.id.user_image);
        }
    }


    public InboxAdapter(ArrayList<Messages> messages) {
        this.messages = messages;
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
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users").child(m.getFrom());
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users u = dataSnapshot.getValue(Users.class);
                Picasso.get().load(u.getThumb_image()).centerCrop().fit().placeholder(R.drawable.defaultavatar).into(holder.imageView);
                holder.name.setText(u.getName().toUpperCase());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.message.setText(m.getMessage());
    }

    public void getUserId() {

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
