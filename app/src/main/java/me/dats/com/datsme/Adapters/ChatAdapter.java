package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import me.dats.com.datsme.Models.ChatModelObject;
import me.dats.com.datsme.Models.DateObject;
import me.dats.com.datsme.Models.ListObject;
import me.dats.com.datsme.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListObject> listObjects;
    private Context mContext;
    private FirebaseAuth mAuth;
//    final int width = getScreensWidh();
//    public int getScreensWidh() {
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        return size.x;
//
//    }

    public ChatAdapter(List<ListObject> listObjects, Context mContext) {
        this.listObjects = listObjects;
        mAuth = FirebaseAuth.getInstance();
        this.mContext = mContext;
    }


    public void setDataChange(List<ListObject> asList) {
        this.listObjects = asList;
        //now, tell the adapter about the update
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListObject.TYPE_GENERAL_RIGHT:
                View currentUserView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_list_row_right, parent, false);
                viewHolder = new ChatRightViewHolder(currentUserView); // view holder for normal items
                break;
            case ListObject.TYPE_GENERAL_LEFT:
                View otherUserView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_list_row_left, parent, false);
                viewHolder = new ChatLeftViewHolder(otherUserView); // view holder for normal items
                break;
            case ListObject.TYPE_DATE:
                View v2 = inflater.inflate(R.layout.date_row, parent, false);
                viewHolder = new DateViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ListObject.TYPE_GENERAL_RIGHT:
                Log.i("TAG", "onBindViewHolder: right");
                ChatModelObject generalItem = (ChatModelObject) listObjects.get(position);
                ChatRightViewHolder chatViewHolder = (ChatRightViewHolder) viewHolder;
                chatViewHolder.bind(generalItem.getChatModel(), mContext);
                break;
            case ListObject.TYPE_GENERAL_LEFT:
                Log.i("TAG", "onBindViewHolder: left");
                ChatModelObject generalItemLeft = (ChatModelObject) listObjects.get(position);
                ChatLeftViewHolder chatLeftViewHolder = (ChatLeftViewHolder) viewHolder;
                chatLeftViewHolder.bind(generalItemLeft.getChatModel(), mContext);
                break;
            case ListObject.TYPE_DATE:
                Log.i("TAG", "onBindViewHolder: date");
                DateObject dateItem = (DateObject) listObjects.get(position);
                DateViewHolder dateViewHolder = (DateViewHolder) viewHolder;
                dateViewHolder.bind(dateItem.getDate());
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (listObjects != null) {
            return listObjects.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return listObjects.get(position).getType(mAuth.getCurrentUser().getUid());
    }

    public ListObject getItem(int position) {
        return listObjects.get(position);
    }
}
