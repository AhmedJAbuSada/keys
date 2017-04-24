package com.keys.Hraj.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keys.R;
import com.booking.rtlviewpager.RtlViewPager;

import java.util.ArrayList;
import java.util.List;


public class FragmentTwo extends Fragment {

    private TabLayout tabs;
    private RtlViewPager viewpager;

    private MainAdsFragment mainAdsFragment;
    private DepartmentFragment departmentFragment;
    private MarketFragment marketFragment;

    private int[] tabIcons = {R.drawable.ic_home_white_24dp, R.drawable.ic_reorder, R.drawable.ic_store_mall};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_two, container, false);
        viewpager = (RtlViewPager) v.findViewById(R.id.viewpager);
        tabs = (TabLayout) v.findViewById(R.id.tabs_icon1);

        RelativeLayout fragment_main = (RelativeLayout) v.findViewById(R.id.fragment_layout);
        applyFont(getActivity(), fragment_main);
        TextView textView = (TextView) getActivity().findViewById(R.id.app_text);
        textView.setText(getString(R.string.app_name));
        textView.setGravity(Gravity.RIGHT);

        mainAdsFragment = new MainAdsFragment();
        departmentFragment = new DepartmentFragment();
        marketFragment = new MarketFragment();

        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);

        setupTabIcons();

        tabs.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);

        getActivity().findViewById(R.id.image_toolbar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.ic_group).setVisibility(View.GONE);
        getActivity().findViewById(R.id.chat_icon).setVisibility(View.GONE);
        getActivity().findViewById(R.id.refresh_icon).setVisibility(View.GONE);
        getActivity().findViewById(R.id.add_user).setVisibility(View.GONE);
        getActivity().findViewById(R.id.add_icon).setVisibility(View.VISIBLE);

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                viewpager.setCurrentItem(tab.getPosition());

                if (tab.getText() == "الرئيسية") {
                    getActivity().findViewById(R.id.add_icon).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.market_icon).setVisibility(View.GONE);

                }

                if (tab.getText() == "المتاجر") {
                    getActivity().findViewById(R.id.market_icon).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.add_icon).setVisibility(View.GONE);
                }

                if (tab.getText() == "الأقسام") {
                    getActivity().findViewById(R.id.add_icon).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.tune_icon).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.market_icon).setVisibility(View.GONE);
                } else {
                    getActivity().findViewById(R.id.tune_icon).setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        return v;
    }

    private void setupTabIcons() {

        tabs.getTabAt(0).setIcon(tabIcons[0]);
        tabs.getTabAt(1).setIcon(tabIcons[1]);
        tabs.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(mainAdsFragment, "الرئيسية");
        viewPagerAdapter.addFragment(departmentFragment, "الأقسام");
        viewPagerAdapter.addFragment(marketFragment, "المتاجر");

        viewPager.setAdapter(viewPagerAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> stringList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            stringList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return stringList.get(position);
        }
    }

    public void applyFont(final Context context, final View v) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "frutigerltarabic_roman.ttf");
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyFont(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(font);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(font);
            } else if (v instanceof RadioButton) {
                ((RadioButton) v).setTypeface(font);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }
}