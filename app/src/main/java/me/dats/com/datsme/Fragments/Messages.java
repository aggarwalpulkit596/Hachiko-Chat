package me.dats.com.datsme.Fragments;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import me.dats.com.datsme.Models.Users;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull final Friends model) {

                final String uid = getRef(position).getKey();

                assert uid != null;
                mUsersDatabase.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot documentSnapshot) {

                        if (documentSnapshot.exists())
                        {
                            final Users u = documentSnapshot.getValue(Users.class);
                            if (!u.getThumb_image().equals("default")) {
                                Picasso.get()
                                        .load(u.getThumb_image())
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.default_avatar)
                                        .into(holder.userImageView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }
                                            @Override
                                            public void onError(Exception e) {
                                                if (!u.getThumb_image().equals("default"))
                                                    Picasso.get()
                                                            .load(u.getThumb_image())
                                                            .placeholder(R.drawable.default_avatar)
                                                            .into(holder.userImageView);
                                            }
                                        });
                            }
                            holder.userNameTextView.setText(u.getName());
                            holder.userstatusTextView.setText(model.getDate());
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatintent = new Intent(getActivity(), ChatActivity.class);
                                    chatintent.putExtra("from_user_id", uid);
                                    chatintent.putExtra("userName", u.getName());
                                    chatintent.putExtra("image", u.getThumb_image());
                                    startActivity(chatintent);
                                }
                            });

                            holder.userImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Target target = new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                            Intent i = new Intent(getActivity(), ImageActivity.class);
                                            i.putExtra("image", bitmap);
                                            ActivityOptionsCompat compat =
                                                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),holder.userImageView, "trans1");
                                            startActivity(i, compat.toBundle());
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    };
                                    Picasso.get().load(u.getImage()).into(target);

                                }
                            });


                        }
//                            name[0] = Objects.requireNonNull(documentSnapshot.child("name").getValue()).toString();
//                            image[0] = Objects.requireNonNull(documentSnapshot.child("thumb_image").getValue()).toString();
//                            image_main[0]=Objects.requireNonNull(documentSnapshot.child("image").getValue()).toString();
//
//                            holder.bind(name[0], image[0],image_main[0]);                       }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
        inflater.inflate(R.menu.friendsmenu, menu);
        this.menu = menu;
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
                q = new Intent(getActivity(), UserAnswerActivity.class);
                startActivity(q);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView userstatusTextView;
        TextView userNameTextView;
        CircleImageView userImageView;

        FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            userstatusTextView = itemView.findViewById(R.id.user_single_status);
            userNameTextView = itemView.findViewById(R.id.user_single_name);
            userImageView = itemView.findViewById(R.id.user_image);
        }

    }
}