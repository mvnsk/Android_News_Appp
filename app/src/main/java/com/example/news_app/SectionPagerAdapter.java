package com.example.news_app;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    public SectionPagerAdapter(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int position){
//       return fragmentList.get(position);
        if(position == 0){
            return WorldFragment.newInstance(0,"Page1");
        }
        else if(position == 1){
            return BusinessFragment.newInstance(0,"Page1");
        }
        else if(position == 2){
            return PoliticsFragment.newInstance(0,"Page1");
        }
        else if(position == 3){
            return SportsFragment.newInstance(0,"Page1");
        }
        else if(position == 4){
            return TechnologyFragment.newInstance(0,"Page1");
        }
        else if(position == 5){
            return ScienceFragment.newInstance(0,"Page1");
        }

        return null;
    }

    @Override
    public  int getCount(){
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        titleList.add(title);
    }
}
