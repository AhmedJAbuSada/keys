package com.keys.Hraj.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup.LayoutParams;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keys.DatabaseSQLite.DBHandler;
import com.keys.Interface.DoSearch;
import com.keys.R;
import com.keys.Hraj.Adapter.DropDownCitiesAdapter;
import com.keys.Hraj.Model.Cities;
import com.booking.rtlviewpager.RtlViewPager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DepartmentFragment extends Fragment {
    private RtlViewPager viewpager;
    TabLayout tabs;
    private AlertDialog alertDialog;
    private List<Cities> citiesList;
    private PopupWindow popupWindow;
    private int width;
    private DBHandler db;
    private Cities cities;
    private String city;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView textView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_department, container, false);

        ImageView tune_icon = (ImageView) getActivity().findViewById(R.id.tune_icon);

        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.layout);

        textView = (TextView) getActivity().findViewById(R.id.app_text);
        textView.setText(getString(R.string.app_name));
        textView.setGravity(Gravity.RIGHT);


        viewpager = (RtlViewPager) v.findViewById(R.id.viewpager);
        tabs = (TabLayout) v.findViewById(R.id.tabs);

        citiesList = new ArrayList<>();
        applyFont(getActivity(), layout);
        prepareStatusData();
        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        int height = display.getHeight();

        tune_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.citydailog, null);
                popupView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                dialog.setView(popupView);
                applyFont(getActivity(), popupView);
                final EditText editDrop = (EditText) popupView.findViewById(R.id.editdrop1);

                editDrop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard(v);
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.drop_down_list, null);
                        popupView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        RecyclerView recyclerView = (RecyclerView) popupView.findViewById(R.id.recycler_view1);
                        DropDownCitiesAdapter dropDownCitiesAdapter = new DropDownCitiesAdapter(citiesList, new DropDownCitiesAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                city = citiesList.get(position).getName();
                                getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE).getString("city", city);
                                editDrop.setText(city);
                                popupWindow.dismiss();
                            }
                        });
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(dropDownCitiesAdapter);

                        popupWindow = new PopupWindow(popupView, (int) (width * 0.7), LayoutParams.MATCH_PARENT);
                        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        popupWindow.setElevation(18f);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setFocusable(true);
                        popupWindow.showAsDropDown(v);
                    }

                });
                Button agree = (Button) popupView.findViewById(R.id.agree);
                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(editDrop.getText().toString().trim())) {
                            editDrop.setError("يجب اختيار المدينة!!");
                        } else {
                            Fragment currentFragment = viewPagerAdapter.getItem(viewpager.getCurrentItem());
                            if (currentFragment instanceof DoSearch) {
                                ((DoSearch) currentFragment).doSearch(editDrop.getText().toString().trim());
                                textView.setText(city);
                                textView.setGravity(Gravity.CENTER);
                                textView.setTypeface(null, Typeface.BOLD);        // for Bold only
                            } else {
                                textView.setText(getString(R.string.app_name));
                            }

                            alertDialog.dismiss();
                        }
                    }
                });
                Button disagree = (Button) popupView.findViewById(R.id.disagree);
                disagree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
//        List<Departments> departmentsList = db.getAllDepartments();
//        for (int i = 0; i < departmentsList.size(); i++) {
//            viewPagerAdapter.addTittle(departmentsList.get(i).getName());
//            Log.e("Tag", departmentsList.get(i).getName());
//        }

        viewPagerAdapter.addFragment(new AllFragment(), "الكل");
        viewPagerAdapter.addFragment(new CarsFragment(), "السيارات");
        viewPagerAdapter.addFragment(new BuildingFragment(), "مواد البناء");
        viewPagerAdapter.addFragment(new ClothsFragment(), "الملابس");
        viewPagerAdapter.addFragment(new PropertyFragment(), "العقارات");
        viewPagerAdapter.addFragment(new MobilesFragment(), "الجوالات");
        viewPagerAdapter.addFragment(new FurnitureFragment(), "الآثاث");

        viewPager.setAdapter(viewPagerAdapter);
        //tabs.setupWithViewPager(viewPager);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<String> stringList = new ArrayList<>();
        private final List<Fragment> fragmentList = new ArrayList<>();

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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    private void prepareStatusData() {
        db = new DBHandler(getActivity());
        citiesList = db.getAllCities();

//        for (int i = 0; i < citiesList.size(); i++) {
//            cities = citiesList.get(i);
//            Log.e("Tag", citiesList.get(i).getName());
//        }
//        citiesList.add(cities);
    }
}
