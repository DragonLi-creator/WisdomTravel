package com.example.holidaytest4.adapter;
import com.example.holidaytest4.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.holidaytest4.fragment.FoundFragment;
import com.example.holidaytest4.fragment.HomeFragment;
import com.example.holidaytest4.fragment.MineFragment;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"首页","目的地","我的"};
    private int imageDefaultId[] = new int[]{R.drawable.home,
            R.drawable.found,
            R.drawable.mine};
    private int imageActiveId[] = new int[]{R.drawable.home_pressed,
            R.drawable.found_pressed,
            R.drawable.mine_pressed};
    private Context context;
    private int mChildCount;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tv = v.findViewById(R.id.textView);
        tv.setText(tabTitles[position]);
        ImageView img = v.findViewById(R.id.imageView);
        img.setImageResource(imageDefaultId[position]);
        return v;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return FoundFragment.newInstance();
            case 2:
                return  MineFragment.newInstance();
            default:
                Log.d("FragmentPagerAdapter", "Can't find corresponding fragment at position " + position);
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        // 重写getItemPosition,保证每次获取时都强制重绘UI
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
