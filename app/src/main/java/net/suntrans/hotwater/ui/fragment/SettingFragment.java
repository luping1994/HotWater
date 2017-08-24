package net.suntrans.hotwater.ui.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.suntrans.hotwater.R;
import net.suntrans.hotwater.databinding.FragmentSettingBinding;

import static android.R.attr.fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    private FragmentSettingBinding binding;

    public SettingFragment() {
        // Required empty public constructor
    }

    private Fragment[] fragments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        WendusettingFragment fragment = new WendusettingFragment();
        TimesettingFragment fragment1 = new TimesettingFragment();
        fragments = new Fragment[]{fragment,fragment1};
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        binding.viewpager.setAdapter(adapter);
        binding.tablayout.setupWithViewPager( binding.viewpager);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private final String[] title = new String[]{"温度/水位设置", "工作时间设置"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }

}
