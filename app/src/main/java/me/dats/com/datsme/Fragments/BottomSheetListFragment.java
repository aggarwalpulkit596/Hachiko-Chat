package me.dats.com.datsme.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.dats.com.datsme.R;

public class BottomSheetListFragment extends BottomSheetDialogFragment {

    public BottomSheetListFragment() {
        // Required empty public constructor
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public static BottomSheetListFragment newInstance(String user_id) {
        BottomSheetListFragment bottomSheetFragment = new BottomSheetListFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("user_id", user_id);
//        bottomSheetFragment.setArguments(bundle);

        return bottomSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.bottom_sheet_profile, container, false);
        return root;
    }
}
