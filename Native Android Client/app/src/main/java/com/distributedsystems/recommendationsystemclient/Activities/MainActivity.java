package com.distributedsystems.recommendationsystemclient.Activities;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.distributedsystems.recommendationsystemclient.Fragments.ResultsTabFragment;
import com.distributedsystems.recommendationsystemclient.Fragments.SearchUserFragment;
import com.distributedsystems.recommendationsystemclient.R;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    public NavigationView mNavView;

    @BindView(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);

        if(getSupportActionBar() == null) return;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        mNavView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;
            switch (item.getItemId()){
                case R.id.recommendations: selectedFragment = new SearchUserFragment(); break;
                default: selectedFragment = new ResultsTabFragment(); break;
            }

            try{
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

                item.setChecked(true);

                mDrawerLayout.closeDrawers();
            }catch(Exception e){
                e.printStackTrace();
            }

            return true;
        });

        mNavView.setCheckedItem(R.id.recommendations);

        try{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SearchUserFragment())
                    .commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() != android.R.id.home) return super.onOptionsItemSelected(item);

        mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
    }
}
