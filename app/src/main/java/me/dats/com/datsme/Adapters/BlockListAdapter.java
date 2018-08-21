package me.dats.com.datsme.Adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Models.BlockUsers;
import me.dats.com.datsme.R;

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<BlockUsers> Blockeduserslist;

    public BlockListAdapter(ArrayList<BlockUsers> messages, Context mContext) {
        this.Blockeduserslist = messages;
        this.mContext = mContext;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blocklistitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(Blockeduserslist.get(position).getName());
        Picasso.get()
                .load(Blockeduserslist.get(position).getImage())
                .placeholder(R.drawable.default_avatar)
                .centerCrop()
                .fit()
                .into(holder.imageView);
        holder.date.setText(Blockeduserslist.get(position).getDate());
        holder.unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogebuilder = new AlertDialog.Builder(mContext);
                dialogebuilder.setMessage("Do you want to unblock " + Blockeduserslist.get(position).getName());
                dialogebuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        FirebaseDatabase.getInstance().getReference().child("Blocklist").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(Blockeduserslist.get(position).getUserId()).child("blocked").setValue(null)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference().child("Blocklist").child(Blockeduserslist.get(position).getUserId())
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("blockedby").setValue(null)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task1) {
                                                            if (task1.isSuccessful()) {
                                                                Toast.makeText(mContext, "" + Blockeduserslist.get(position).getName() + " is Unblocked", Toast.LENGTH_SHORT).show();
                                                                Blockeduserslist.remove(position);
                                                                notifyItemRemoved(position);
                                                                notifyDataSetChanged();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create().show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return Blockeduserslist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, unblock, date;
        CircleImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            unblock = itemView.findViewById(R.id.unblock_button);
            imageView = itemView.findViewById(R.id.image);

        }
    }
}

