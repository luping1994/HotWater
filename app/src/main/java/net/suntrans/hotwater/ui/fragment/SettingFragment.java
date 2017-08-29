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
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends LazyLoadFragment {


    private FragmentSettingBinding binding;
    private WendusettingFragment fragment;
    private TimesettingFragment fragment1;

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
        super.onViewCreated(view,savedInstanceState);
        fragment = new WendusettingFragment();
        fragment1 = new TimesettingFragment();
        fragments = new Fragment[]{fragment, fragment1};
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setOffscreenPageLimit(1);
        binding.tablayout.setupWithViewPager( binding.viewpager);
    }

    @Override
    protected void onFragmentFirstVisible() {
        fragment.getData();
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
