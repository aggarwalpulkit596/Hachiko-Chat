package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.dats.com.datsme.Models.MyItem;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class BottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

     public List<MyItem> item;

    Context mContext;

    public BottomSheetAdapter(List<MyItem> item, Context context) {
        this.item = item;
        this.mContext = context;
        Log.i("TAG","sheetConstructor");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View currentUserView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout, parent, false);
        RecyclerView.ViewHolder viewHolder = new BottomSheetViewHolder(currentUserView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyItem generalItem = item.get(position);
        BottomSheetViewHolder bottomSheetViewHolder = (BottomSheetViewHolder) holder;
        bottomSheetViewHolder.bind(generalItem, mContext);
    }

    @Override
    public int getItemCount() {

        if (item != null) {
            return item.size();
        }return 0;
    }
}
