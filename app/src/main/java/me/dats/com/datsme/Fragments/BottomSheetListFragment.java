package me.dats.com.datsme.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Activities.MapsActivity;
import me.dats.com.datsme.Adapters.BottomSheetAdapter;
import me.dats.com.datsme.Models.MyItem;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.SpacesItemDecoration;

public class BottomSheetListFragment extends BottomSheetDialogFragment {

    ArrayList<MyItem> list;
    BottomSheetAdapter bottomSheetAdapter;
    @BindView(R.id.ItemsList_BottomSheetListFragment)
    RecyclerView mRecyclerView;

    public BottomSheetListFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void setupDialog(Dialog dialog, int style) {
//        super.setupDialog(dialog, style);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//    }

    public static BottomSheetListFragment newInstance(ArrayList<MyItem> list) {

        BottomSheetListFragment bottomSheetFragment = new BottomSheetListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Items",list);
        bottomSheetFragment.setArguments(bundle);

        return bottomSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.bottom_sheet_list_profile, container, false);
        ButterKnife.bind(this, root);
        list = (ArrayList<MyItem>) getArguments().getSerializable("Items");

        mRecyclerView.setHasFixedSize(true);
        int spacingInPixels = 10;

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemViewCacheSize(10);
        bottomSheetAdapter = new BottomSheetAdapter(list,getActivity(),this);
        mRecyclerView.setAdapter(bottomSheetAdapter);
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();

        // Resize bottom sheet dialog so it doesn't span the entire width past a particular measurement
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels - 70;
        int height = -1; // MATCH_PARENT

        getDialog().getWindow().setLayout(width, height);
    }

}
