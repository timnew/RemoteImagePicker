package me.timnew.remoteimagepicker;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.future.ImageViewFuture;
import me.timnew.remoteimagepicker.events.ImagePickedEvent;
import me.timnew.remoteimagepicker.events.PreviewImageEvent;
import me.timnew.remoteimagepicker.images.RestClient;
import me.timnew.remoteimagepicker.images.RestClient_;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import java.io.File;
import java.io.IOException;

import static android.view.Gravity.LEFT;

@EActivity(R.layout.activity_image_picker)
public class ImagePickerActivity extends FragmentActivity {

    public static final String TAG_IMAGE_LIST = "image_list";
    public static final String TAG_IMAGE_PREVIEW = "image_preview";
    @ViewById(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    private ActionBar actionBar;

    protected ActionBarDrawerToggle drawerToggle;

    @Bean
    protected Bus bus;

    @Bean
    protected RestClient client;

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

    public void onEvent(final PreviewImageEvent event) {
        DialogFragment previewFragment = new DialogFragment() {

            private ImageViewFuture downloadImageRequest;

            private void downloadImage(ImageView imageView) {
                cancelRequest();
                downloadImageRequest = client.previewImage(event.imageInfo, imageView);
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Dialog dialog = new Dialog(ImagePickerActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

                dialog.setContentView(R.layout.preview_image_dialog);
                final ImageView imageView = (ImageView) dialog.findViewById(R.id.image_view);
                RelativeLayout layout = (RelativeLayout) dialog.findViewById(R.id.container);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePickedEvent pickedEvent = new ImagePickedEvent(event.imageInfo);
                        bus.post(pickedEvent);
                        dialog.dismiss();
                    }
                });

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        downloadImage(imageView);
                    }
                });
                return dialog;
            }

            @Override
            public void onDetach() {
                super.onDetach();
                cancelRequest();
            }

            private void cancelRequest() {
                if (downloadImageRequest != null && !downloadImageRequest.isDone()) {
                    downloadImageRequest.cancel(true);
                }
            }
        };

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(TAG_IMAGE_PREVIEW);

        previewFragment.show(fragmentTransaction, TAG_IMAGE_PREVIEW);
    }

    public void onEvent(ImagePickedEvent event) {
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals(MediaStore.ACTION_IMAGE_CAPTURE)) {
            Uri outputPath = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            if (outputPath == null) {
                client.downloadBitmap(event.imageInfo).setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap bitmap) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(MediaStore.EXTRA_OUTPUT, bitmap);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
            } else {
                client.download(event.imageInfo, new File(outputPath.getPath())).setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                });
            }
        } else if (action.equals(Intent.ACTION_GET_CONTENT)) {
            try {
                File outputDir = getCacheDir();
                File tempFile = File.createTempFile("remote-image", ".jpg", outputDir);
                // FIXME: this apporoach doesn't work since the path is not guaranteed to be accessible from other apps.

                client.download(event.imageInfo, tempFile).setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        Uri uri = Uri.fromFile(file);
                        try {
                            if (e != null)
                                throw e;
                            Intent resultIntent = Intent.parseUri(uri.toString(), 0);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                setResult(RESULT_CANCELED);
                finish();
            }
        }
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
