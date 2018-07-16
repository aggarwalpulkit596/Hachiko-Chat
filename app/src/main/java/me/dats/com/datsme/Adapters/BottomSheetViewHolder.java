package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.Models.MyItem;
import me.dats.com.datsme.R;

public class BottomSheetViewHolder extends RecyclerView.ViewHolder {

TextView name;
CircleImageView circleImageView;
CardView cardView;

    public BottomSheetViewHolder(View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.user_name);
        circleImageView=itemView.findViewById(R.id.listImage_view);
        cardView=itemView.findViewById(R.id.listItemsCardView);
    }
    public void bind(final MyItem model, Context mContext)
    {
        name.setText(model.getTitle());
        circleImageView.setImageBitmap(model.getBitmap());
        Log.i("TAG",""+model.getTitle());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
