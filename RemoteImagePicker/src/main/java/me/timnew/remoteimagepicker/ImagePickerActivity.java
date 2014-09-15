package me.timnew.remoteimagepicker;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import static android.view.Gravity.LEFT;

@EActivity(R.layout.activity_image_picker)
public class ImagePickerActivity extends FragmentActivity {

    @ViewById(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    private ActionBar actionBar;

    protected ActionBarDrawerToggle drawerToggle;

    @Bean
    protected Bus bus;

    @AfterViews
    protected void afterViews() {
        actionBar = getActionBar();

        //noinspection ConstantConditions
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerToggle = new SlidingMenuToggle();
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @OptionsItem(android.R.id.home)
    public void toggleSlidingMenu() {
        if (isSlidingMenuShown()) {
            closeSlidingMenu();
        } else {
            openSlidingMenu();
        }
    }

    public boolean isSlidingMenuShown() {
        return drawerLayout.isDrawerOpen(LEFT);
    }

    public void openSlidingMenu() {
        drawerLayout.openDrawer(LEFT);
    }

    public void closeSlidingMenu() {
        drawerLayout.closeDrawer(LEFT);
    }

    public void onEvent(SlidingMenuCommands command) {
        command.applyTo(this);
    }

    public static enum SlidingMenuCommands {
        OPEN_SLIDING_MENU {
            @Override
            void applyTo(ImagePickerActivity activity) {
                activity.openSlidingMenu();
            }
        },
        CLOSE_SLIDING_MENU {
            @Override
            void applyTo(ImagePickerActivity activity) {
                activity.closeSlidingMenu();
            }
        };

        abstract void applyTo(ImagePickerActivity activity);
    }

    private class SlidingMenuToggle extends ActionBarDrawerToggle {
        public SlidingMenuToggle() {
            super(ImagePickerActivity.this,
                    drawerLayout,
                    R.drawable.ic_navigation_drawer,
                    R.string.drawer_open, R.string.drawer_close);
        }

        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            actionBar.setTitle(ImagePickerActivity.this.getTitle());
            invalidateOptionsMenu();
        }

        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            actionBar.setTitle(R.string.sliding_menu_title);
            invalidateOptionsMenu();
        }
    }
}
