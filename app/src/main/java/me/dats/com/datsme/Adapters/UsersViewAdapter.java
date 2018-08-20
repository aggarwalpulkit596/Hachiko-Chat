package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Activities.MapsActivity;
import me.dats.com.datsme.Fragments.BottomSheetProfileFragment;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class UsersViewAdapter extends RecyclerView.Adapter<UsersViewAdapter.UsersViewHolder> {

    Context mContext;
    private List<Users> mUsersList;
    private List<String> mUsersUid;

    public UsersViewAdapter(List<Users> mUsers, List<String> mUsersUid, FragmentActivity activity) {
        this.mUsersList = mUsers;
        this.mUsersUid = mUsersUid;
        this.mContext = activity;
    }


    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UsersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        final Users mUser = mUsersList.get(position);
        final String userId = mUsersUid.get(position);
        holder.bind(mUser);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
                BottomSheetProfileFragment.newInstance(userId).show(((MapsActivity) mContext).getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        UsersViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.name);
            userNameView.setText(name);
        }

        void bind(Users model) {
            String name1 = model.getName();
            String[] arr = name1.split(" ");
            String fname = arr[0];
            setName(fname);
            setThumbImage(model.getThumb_image());
        }

        void setThumbImage(String thumbImage) {
            CircleImageView userImageView = mView.findViewById(R.id.image);
            if (!thumbImage.equals("default"))
                Picasso.get()
                        .load(thumbImage)
                        .placeholder(R.drawable.default_avatar)
                        .into(userImageView);

        }

    }
}
