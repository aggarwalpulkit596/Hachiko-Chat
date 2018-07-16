package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.Models.MyItem;
import me.dats.com.datsme.R;

public class BottomSheetViewHolder extends RecyclerView.ViewHolder {

TextView name;
Button profile;

    public BottomSheetViewHolder(View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.user_name);
        profile=itemView.findViewById(R.id.user_viewprofile);
    }
    public void bind(final MyItem model, Context mContext)
    {
        name.setText(model.getTitle());
        Log.i("TAG",""+model.getTitle());
    }
}
