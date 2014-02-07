package com.baasbox.android.pinbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.baasbox.android.pinbox.common.BaseActivity;
import com.baasbox.android.pinbox.gallery.GalleryFragment;
import com.baasbox.android.pinbox.gallery.UploadFragment;
import com.baasbox.android.pinbox.login.LoginActivity;
import com.baasbox.android.pinbox.profile.ProfileFragment;
import com.baasbox.android.pinbox.service.RefreshService;
import com.baasbox.android.pinbox.service.UploadImageService;
import com.baasbox.android.pinbox.users.UserListFragment;

import java.util.Locale;

public class MainActivity extends BaseActivity implements ActionBar.TabListener {
    private final static String TOKEN = "TOKEN";
    private final static String UPLOAD = "UPLOAD_TOKEN";

    private final static int TABS_COUNT = 3;
    private final static int GALLERY_TAB = 0;
    private final static int PROFILE_TAB = 1;
    private final static int USERS_TAB = 2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private RequestToken logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo check login
        if (BaasUser.current() == null || !BaasUser.current().isAuthentcated()) {
            startLoginScreen();
            return;
        }
        setContentView(R.layout.activity_main);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        if (savedInstanceState == null) {
            RefreshService.doRefresh(this);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof GalleryFragment) {
            ((GalleryFragment) fragment).setOnImageChoosenListener(new GalleryFragment.OnImageChoosen() {
                @Override
                public void onImageChoosen(Uri imageUri) {
                    UploadFragment.show(getSupportFragmentManager(), imageUri, false);
                }
            });
        } else if (fragment instanceof UploadFragment) {
            ((UploadFragment) fragment).setOnUploadConfirmedListener(new UploadFragment.OnUploadConfirmedListener() {
                @Override
                public void onUploadConfirmed(Uri imageUri, String title) {
                    UploadImageService.saveAnduploadImage(MainActivity.this, imageUri, title);
                }
            });
        }
    }

    void onLogout() {
        PinBox.getSyncTimeManager().resetSyncTime();
        getContentResolver().delete(Contract.Image.CONTENT_URI, null, null);
        startLoginScreen();
    }

    private void startLoginScreen() {
        // start login screen as new task
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean handled = true;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
//                startActivity(new Intent(this, TestActivity.class));
                break;
            case R.id.action_logout:
                //todo
                logout = BaasUser.current().logout(logoutHandler);
                break;
            default:
                handled = false;

        }
        return handled || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (logout != null)
            logout.suspend();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (logout != null) logout.resume(logoutHandler);
    }

    private final BaasHandler<Void> logoutHandler = new BaasHandler<Void>() {
        @Override
        public void handle(BaasResult<Void> result) {
            if (result.isSuccess()) {
                onLogout();
            } else {
                Log.d("LOG_TAG", "failed logout " + result.error().getMessage());
            }
            logout = null;
        }
    };


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case GALLERY_TAB:
                    return GalleryFragment.newInstance();
                case PROFILE_TAB:
                    return ProfileFragment.newInstance();
                case USERS_TAB:
                    return new UserListFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return TABS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case GALLERY_TAB:
                    return getString(R.string.title_gallery).toUpperCase(l);
                case PROFILE_TAB:
                    return getString(R.string.title_profile).toUpperCase(l);
                case USERS_TAB:
                    return getString(R.string.title_users).toUpperCase(l);
            }
            return null;
        }
    }

}
