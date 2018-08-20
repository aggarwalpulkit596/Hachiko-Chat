package me.dats.com.datsme.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class blockList_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar_blocklist)
    Toolbar toolbar;
    @BindView(R.id.blocklist_list)
    RecyclerView recyclerView;
    @BindView(R.id.EmptyBlockList)
    TextView textView;
    private BlockListAdapter adapter;
    List<Users> users=new ArrayList<>();
    private String TAG="BlockListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list_);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Blocklist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);




    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

 class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.BlockListViewHolder>{

     public BlockListAdapter(List<String> Users) {
     }

     @NonNull
     @Override
     public BlockListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View itemView = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.blocklistitem, parent, false);

         return new BlockListAdapter.BlockListViewHolder(itemView);
     }

     @Override
     public void onBindViewHolder(@NonNull BlockListViewHolder holder, int position) {

     }

     @Override
     public int getItemCount() {
         return 0;
     }
     class BlockListViewHolder extends RecyclerView.ViewHolder  {
         TextView name;
         public BlockListViewHolder(View itemView) {
             super(itemView);
             name=itemView.findViewById(R.id.name);
         }
     }
 }


