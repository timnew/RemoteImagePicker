package me.timnew.remoteimagepicker.images;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import me.timnew.remoteimagepicker.R;
import me.timnew.remoteimagepicker.servers.ServerInfo;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import java.util.List;

import static me.timnew.remoteimagepicker.SlidingMenuFragment.CurrentPodInfo;
import static me.timnew.remoteimagepicker.SlidingMenuHeaderView.CurrentServerInfo;
import static me.timnew.remoteimagepicker.images.ImageGridAdapter.ImageListUpdatedEvent;
import static me.timnew.remoteimagepicker.images.PodListAdapter.PodsListUpdatedEvent;
import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class RestClient {

    public static final String REST_CLIENT = "RestClient";
    @RootContext
    protected Context context;

    @Bean
    protected Bus bus;

    private ServerInfo currentServer;
    private PodInfo currentPod;

    @AfterInject
    protected void afterInjects() {
        bus.register(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        bus.unregister(this);
    }

    public void onEvent(CurrentServerInfo currentServer) {
        this.currentServer = currentServer.currentServer;
        refreshPods();
    }

    public void refreshPods() {
        Ion.with(context)
                .load(currentServer.buildUrl("/pods"))
                .setLogging(REST_CLIENT, Log.DEBUG)
                .as(new TypeToken<List<PodInfo>>() { })
                .setCallback(new FutureCallback<List<PodInfo>>() {
                    @Override
                    public void onCompleted(Exception e, List<PodInfo> podInfos) {
                        bus.post(new PodsListUpdatedEvent(podInfos));
                    }
                });
    }

    public void onEvent(CurrentPodInfo currentPodInfo) {
        this.currentPod = currentPodInfo.pod;
        refreshImages();
    }

    private void refreshImages() {
        Ion.with(context)
                .load(currentServer.buildUrl("/pods/%d/images", currentPod.id))
                .setLogging(REST_CLIENT, Log.DEBUG)
                .as(new TypeToken<List<ImageInfo>>() { })
                .setCallback(new FutureCallback<List<ImageInfo>>() {
                    @Override
                    public void onCompleted(Exception e, List<ImageInfo> imageInfos) {
                        bus.post(new ImageListUpdatedEvent(imageInfos));
                    }
                });
    }

    public void loadImage(ImageInfo image, ImageView imageView) {
        String imageUrl = currentServer.buildUrl("/pods/%d/images/%s", currentPod.id, image.name);

        Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}