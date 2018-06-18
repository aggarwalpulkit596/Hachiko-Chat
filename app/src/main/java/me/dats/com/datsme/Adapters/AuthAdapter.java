package me.dats.com.datsme.Adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import me.dats.com.datsme.Fragments.AuthFragment;
import me.dats.com.datsme.Fragments.LogInFragment;
import me.dats.com.datsme.Fragments.SignUpFragment;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Widgets.AnimatedViewPager;

public class AuthAdapter extends FragmentStatePagerAdapter
        implements AuthFragment.Callback{

    private final AnimatedViewPager pager;
    private final SparseArray<AuthFragment> authArray;
    private float factor;

    public AuthAdapter(FragmentManager manager,
                       AnimatedViewPager pager){
        super(manager);
        this.pager=pager;
        this.authArray=new SparseArray<>(getCount());
        pager.setDuration(350);
        final float textSize=pager.getResources().getDimension(R.dimen.folded_size);
        final float textPadding=pager.getResources().getDimension(R.dimen.folded_label_padding);
        factor=1-(textSize+textPadding)/(pager.getWidth());
    }

    @Override
    public AuthFragment getItem(int position) {
        AuthFragment fragment=authArray.get(position);
        if(fragment==null){
            fragment=position!=1?new LogInFragment():new SignUpFragment();
            authArray.put(position,fragment);
            fragment.setCallback(this);
        }
        return fragment;
    }

    @Override
    public void show(AuthFragment fragment) {
        final int index=authArray.keyAt(authArray.indexOfValue(fragment));
        pager.setCurrentItem(index,true);
//        shiftSharedElements(getPageOffsetX(fragment), index==1);
        for(int jIndex=0;jIndex<authArray.size();jIndex++){
            if(jIndex!=index){
                authArray.get(jIndex).fold();
            }
        }
    }

    private float getPageOffsetX(AuthFragment fragment){
        int pageWidth=fragment.getView().getWidth();
        return pageWidth-pageWidth*factor;
    }


    @Override
    public void scale(boolean hasFocus) {


    }

    @Override
    public float getPageWidth(int position) {
        return factor;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
