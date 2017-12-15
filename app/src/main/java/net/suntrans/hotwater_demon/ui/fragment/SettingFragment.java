package net.suntrans.hotwater_demon.ui.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import net.suntrans.hotwater_demon.R;
import net.suntrans.hotwater_demon.databinding.FragmentSettingBinding;

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
//        binding.viewpager.setOffscreenPageLimit(2);
//        binding.tablayout.setupWithViewPager( binding.viewpager);
        binding.segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radio0){
                    binding.viewpager.setCurrentItem(0,false);
                }else {
                    binding.viewpager.setCurrentItem(1,false);

                }
            }
        });
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0)
                    binding.radio0.setChecked(true);
                else
                    binding.radio1.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
