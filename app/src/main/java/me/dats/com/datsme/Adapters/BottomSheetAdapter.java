package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.dats.com.datsme.Fragments.BottomSheetListFragment;
import me.dats.com.datsme.Models.MyItem;
import me.dats.com.datsme.R;

public class BottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<? extends MyItem> item;

    private Context mContext;
    private BottomSheetListFragment bottomSheetListFragment;

    public BottomSheetAdapter(ArrayList<? extends MyItem> item, Context context, BottomSheetListFragment bottomSheetListFragment) {
        this.item = item;
        this.mContext = context;
        this.bottomSheetListFragment = bottomSheetListFragment;
        Log.i("TAG", "sheetConstructor");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View currentUserView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout, parent, false);
        return new BottomSheetViewHolder(currentUserView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyItem generalItem = item.get(position);
        BottomSheetViewHolder bottomSheetViewHolder = (BottomSheetViewHolder) holder;
        bottomSheetViewHolder.bind(generalItem, mContext, position, bottomSheetListFragment);

    }

    @Override
    public int getItemCount() {

        if (item != null) {
            return item.size();
        }
        return 0;
    }
}
