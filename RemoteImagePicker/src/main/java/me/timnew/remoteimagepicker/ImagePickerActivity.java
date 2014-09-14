package me.timnew.remoteimagepicker;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import static android.view.Gravity.LEFT;

@EActivity(R.layout.activity_image_picker)
public class ImagePickerActivity extends FragmentActivity {

    @ViewById(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    private ActionBar actionBar;

    protected ActionBarDrawerToggle drawerToggle;

    @AfterViews
    protected void afterViews() {
        actionBar = getActionBar();

        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, R.drawable.ic_navigation_drawer,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                actionBar.setTitle(ImagePickerActivity.this.getTitle());
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setTitle("Sidebar");
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @OptionsItem(android.R.id.home)
    public void toggleSlidingMenu() {
        if (drawerLayout.isDrawerOpen(LEFT)) {
            drawerLayout.closeDrawer(LEFT);
        } else {
            drawerLayout.openDrawer(LEFT);
        }
    }
}
