package com.example.mainactivity.adapter;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.mainactivity.fragment.BirthdayFragment;
import com.example.mainactivity.fragment.CalendarPageFragment;

public class PageAdapter extends FragmentStateAdapter {
    public PageAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                Log.i("PageAdapter", "Returning calendare");
                return new CalendarPageFragment();
            case 1:
                Log.i("PageAdapter", "Returning birthdayer");
                return new BirthdayFragment();
        }
        Log.e("PageAdapter", "Returning null");
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}