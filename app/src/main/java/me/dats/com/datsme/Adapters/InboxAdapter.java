package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Activities.ChatActivity;
import me.dats.com.datsme.Activities.ImageActivity;
import me.dats.com.datsme.Activities.InboxActivity;
import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.BubbleTransformation;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

    private ArrayList<Messages> messages;
    private InboxActivity mContext;
    private Bitmap image;

    public InboxAdapter(ArrayList<Messages> messages, InboxActivity mContext) {
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Messages m = messages.get(position);
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users").child(m.fuid);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final Users u = dataSnapshot.getValue(Users.class);
                Picasso.get().load(u.getThumb_image()).centerCrop().fit().placeholder(R.drawable.defaultavatar).into(holder.imageView);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Target target=new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                image=bitmap;
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }
                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        Picasso.get().load(u.getImage())
                                .into(target);

                        Intent i = new Intent(mContext, ImageActivity.class);
                        i.putExtra("image",image);
                        ActivityOptionsCompat compat=ActivityOptionsCompat.makeSceneTransitionAnimation(mContext,holder.imageView,"trans1");
                        mContext.startActivity(i,compat.toBundle());
                    }
                });
                holder.name.setText(u.getName().toUpperCase());

                String time = DateUtils.formatDateTime(mContext, m.getTime(), DateUtils.FORMAT_SHOW_TIME);
                holder.time.setText(time);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, ChatActivity.class);
                        i.putExtra("from_user_id", messages.get(position).fuid);
                        i.putExtra("userName", u.getName());
                        mContext.startActivity(i);
                    }
                });
                if(m.getType().equals("text")||m.getType().equals("image"))
                    holder.message.setText(m.getMessage());

                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        public TextView message, name, time;
        public CircleImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            this.view=view;
            message = view.findViewById(R.id.user_single_status);
            name = view.findViewById(R.id.user_single_name);
            imageView = view.findViewById(R.id.user_image);
            time = view.findViewById(R.id.user_lastmessage_time);

        }
    }
}
