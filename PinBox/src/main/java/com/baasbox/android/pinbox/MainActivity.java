package com.baasbox.android.pinbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.baasbox.android.BAASBox;
import com.baasbox.android.BaasAccount;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;
import com.baasbox.android.pinbox.common.BaseActivity;
import com.baasbox.android.pinbox.gallery.GalleryFragment;
import com.baasbox.android.pinbox.gallery.UploadFragment;
import com.baasbox.android.pinbox.login.LoginActivity;
import com.baasbox.android.pinbox.profile.ProfileFragment;
import com.baasbox.android.pinbox.utils.Utils;

import java.io.File;
import java.util.Locale;

public class MainActivity extends BaseActivity implements ActionBar.TabListener {
    private final static String TOKEN = "TOKEN";
    private final static String UPLOAD = "UPLOAD_TOKEN";

    private final static int TABS_COUNT = 2;
    private final static int GALLERY_TAB = 0;
    private final static int PROFILE_TAB = 1;

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
    private RequestToken uploadToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo check login
        if (!BaasAccount.isUserLoggedIn(PinBox.getBaasBox())) {
            startLoginScreen();
            return;
        }
        setContentView(R.layout.activity_main);
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
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
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof GalleryFragment){
            ((GalleryFragment) fragment).setOnImageChoosenListener(new GalleryFragment.OnImageChoosen() {
                @Override
                public void onImageChoosen(Uri imageUri) {
                    UploadFragment.show(getSupportFragmentManager(),imageUri);
                }
            });
        } else if(fragment instanceof UploadFragment){
            ((UploadFragment) fragment).setOnUploadConfirmedListener(new UploadFragment.OnUploadConfirmedListener() {
                @Override
                public void onUploadConfirmed(Uri imageUri) {
                    File f = Utils.getMediaFile(MainActivity.this, imageUri);
                    if (f!=null){
                    uploadToken=BaasFile.save(PinBox.getBaasBox(),null,f,MainActivity.this,0,uploadHander);
                    }
                };
            });
        }
    } 
    private static final BAASBox.BAASHandler<BaasFile,MainActivity>
            uploadHander = new BAASBox.BAASHandler<BaasFile, MainActivity>() {
        @Override
        public void handle(BaasResult<BaasFile> baasFileBaasResult, MainActivity mainActivity) {

        }
    };
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
                break;
            case R.id.action_logout:
                //todo
                logout = BaasAccount.logout(PinBox.getBaasBox(), this, logoutHandler);
                break;
            default:
                handled = false;

        }
        return handled || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PinBox.getBaasBox().suspend(TOKEN, logout);
        PinBox.getBaasBox().suspend(UPLOAD,uploadToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logout = PinBox.getBaasBox().resume(TOKEN, this, logoutHandler);
        uploadToken=PinBox.getBaasBox().resume(UPLOAD,this,uploadHander);
    }

    private final static BAASBox.BAASHandler<Void, MainActivity> logoutHandler = new BAASBox.BAASHandler<Void, MainActivity>() {
        @Override
        public void handle(BaasResult<Void> result, MainActivity mainActivity) {
            if (result.isSuccess()) {
                mainActivity.startLoginScreen();
            }
            mainActivity.logout = null;
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
            }
            return null;
        }
    }

}
