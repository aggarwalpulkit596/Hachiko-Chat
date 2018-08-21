package me.dats.com.datsme.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Activities.ChatActivity;
import me.dats.com.datsme.Activities.ImageActivity;
import me.dats.com.datsme.Activities.InboxActivity;
import me.dats.com.datsme.Activities.MapsActivity;
import me.dats.com.datsme.Activities.NotificationsActivity;
import me.dats.com.datsme.Activities.UserAnswerActivity;
import me.dats.com.datsme.Models.Friends;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.SpacesItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class Messages extends Fragment implements View.OnClickListener {

    @BindView(R.id.friends_list)
    RecyclerView mFriendlist;

//    @BindView(R.id.toolbar_messages_1)
//    Toolbar toolbar;

    @BindView(R.id.no_friend_view)
    LinearLayout no_friend_view;

    @BindView(R.id.no_friends_button)
    Button no_friend_button;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;

    private String current_uid;
    private Menu menu;

    public Messages() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);


        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        current_uid = mAuth.getCurrentUser().getUid();

        mFriendlist.setLayoutManager(new LinearLayoutManager(getContext()));
        int spacingInPixels = 10;
        mFriendlist.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        no_friend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapsActivity) getActivity()).getDiscoverFragment();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(current_uid);
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {

                    no_friend_view.setVisibility(View.VISIBLE);
                    mFriendlist.setVisibility(View.GONE);
                } else {
                    no_friend_view.setVisibility(View.GONE);
                    mFriendlist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.i("TAG", "onCreateViewHolder: " + firebaseRecyclerAdapter.getItemCount());
                return new FriendsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout2, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
                holder.setDate(model.getDate());
                final String uid = getRef(position).getKey();
                final String[] name = new String[1];
                final String[] image = new String[1];
                final String[] image_main=new String[1];

                assert uid != null;
                mUsersDatabase.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot documentSnapshot) {

                        if (documentSnapshot.exists())//added
                        {
                            name[0] = Objects.requireNonNull(documentSnapshot.child("name").getValue()).toString();
                            image[0] = Objects.requireNonNull(documentSnapshot.child("thumb_image").getValue()).toString();
                            image_main[0]=Objects.requireNonNull(documentSnapshot.child("image").getValue()).toString();
//                        if (documentSnapshot.hasChild("online")) {
//                            String userOnline = documentSnapshot.child("online").getValue().toString();
//                            holder.setUserOnline(userOnline);
//                        }
                            holder.bind(name[0], image[0],image_main[0],getActivity());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent chatintent = new Intent(getActivity(), ChatActivity.class);
                        chatintent.putExtra("from_user_id", uid);
                        chatintent.putExtra("userName", name[0]);
                        chatintent.putExtra("image", image[0]);
                        startActivity(chatintent);
                    }
                });


            }
        };
        mFriendlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.no_friends_button:

                break;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.friendsmenu,menu);
        this.menu=menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent q;
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.notification:
                Intent i = new Intent(getActivity(), NotificationsActivity.class);
                startActivity(i);
                break;
            case R.id.inbox:
                q = new Intent(getActivity(), InboxActivity.class);
                startActivity(q);
                break;
            case R.id.show:
                q=new Intent(getActivity(),UserAnswerActivity.class);
                startActivity(q);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Bitmap mybigimage;
        private Activity mContext;


        FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date) {

            TextView userstatusTextView = mView.findViewById(R.id.user_single_status);
            userstatusTextView.setText(date);

        }

        public void bind(String name, final String image, String image_main, FragmentActivity activity) {
            TextView userNameTextView = mView.findViewById(R.id.user_single_name);

            mContext=activity;
            final CircleImageView userImageView = mView.findViewById(R.id.user_image);

            Target target=new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mybigimage=bitmap;
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.get().load(image_main)
                    .into(target);

            userImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, ImageActivity.class);
                    i.putExtra("image",image);
                    ActivityOptionsCompat compat=ActivityOptionsCompat.makeSceneTransitionAnimation(mContext,userImageView,"trans1");
                    mContext.startActivity(i,compat.toBundle());
                }
            });
            userNameTextView.setText(name);
            if (!image.equals("default"))
                Picasso.get()
                        .load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar)
                        .into(userImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                if (!image.equals("default"))
                                    Picasso.get()
                                            .load(image)
                                            .placeholder(R.drawable.default_avatar)
                                            .into(userImageView);
                            }
                        });

        }

//        public void setUserOnline(String userOnline) {
//
//            CircleImageView userOnlineView = mView.findViewById(R.id.user_single_online);
//            if (userOnline.equals("true")) {
//                userOnlineView.setVisibility(View.VISIBLE);
//            } else {
//                userOnlineView.setVisibility(View.INVISIBLE);
//            }
//        }
    }

}