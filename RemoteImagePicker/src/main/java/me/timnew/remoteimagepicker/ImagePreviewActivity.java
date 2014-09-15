package me.timnew.remoteimagepicker;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import com.koushikdutta.ion.future.ImageViewFuture;
import me.timnew.remoteimagepicker.images.ImageInfo;
import me.timnew.remoteimagepicker.images.PodInfo;
import me.timnew.remoteimagepicker.images.RestClient;
import me.timnew.remoteimagepicker.servers.ServerInfo;
import org.androidannotations.annotations.*;

@EActivity(R.layout.activity_image_preview)
public class ImagePreviewActivity extends FragmentActivity {

    @Extra
    protected ServerInfo serverInfo;

    @Extra
    protected PodInfo podInfo;

    @Extra
    protected ImageInfo imageInfo;

    @Bean
    protected RestClient client;

    @ViewById(R.id.image_view)
    protected ImageView imageView;

    private ImageViewFuture downloadImageRequest;

    @AfterViews
    protected void afterViews() {
        ActionBar actionBar = getActionBar();

        //noinspection ConstantConditions
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        downloadImage();
    }

    protected void downloadImage() {
        downloadImageRequest = client.previewImage(serverInfo, podInfo, imageInfo, imageView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (downloadImageRequest != null && !downloadImageRequest.isDone()) {
            downloadImageRequest.cancel(true);
        }
    }

    @OptionsItem(android.R.id.home)
    protected void onBackClicked() {
        finish();
    }
}
