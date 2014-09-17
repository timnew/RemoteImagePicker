package me.timnew.remoteimagepicker.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ImageViewFuture;
import com.koushikdutta.ion.future.ResponseFuture;
import me.timnew.remoteimagepicker.R;
import me.timnew.remoteimagepicker.servers.ServerInfo;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import java.io.File;
import java.util.List;

import me.timnew.remoteimagepicker.events.CurrentPodInfo;
import me.timnew.remoteimagepicker.events.CurrentServerInfo;

import me.timnew.remoteimagepicker.events.ImageListUpdatedEvent;

import me.timnew.remoteimagepicker.events.PodsListUpdatedEvent;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class RestClient {

    public static final String REST_CLIENT = "RestClient";
    public static final String URL_POD_LIST = "/pods";
    public static final String URL_IMAGE_LIST = "/pods/%d/images";
    public static final String URL_IMAGE = "/pods/%d/images/%s";

    @RootContext
    protected Context context;

    @Bean
    protected Bus bus;

    private ServerInfo currentServer;

    public ServerInfo getCurrentServer() {
        return currentServer;
    }

    private PodInfo currentPod;

    public PodInfo getCurrentPod() {
        return currentPod;
    }


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
                .load(currentServer.buildUrl(URL_POD_LIST))
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
                .load(currentServer.buildUrl(URL_IMAGE_LIST, currentPod.id))
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
        String imageUrl = currentServer.buildUrl(URL_IMAGE, currentPod.id, image.name);

        Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .animate(android.R.anim.fade_in)
                .into(imageView);
    }

    public ImageViewFuture previewImage(ImageInfo image, ImageView imageView) {
        return Ion.with(context)
                .load(getDownloadUrl(image))
                .withBitmap()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken)
                .deepZoom()
                .intoImageView(imageView);
    }

    public String getDownloadUrl(ImageInfo image) {
        return currentServer.buildUrl(URL_IMAGE, currentPod.id, image.name);
    }

    public Future<Bitmap> downloadBitmap(ImageInfo imageInfo) {
        return Ion.with(context)
                .load(getDownloadUrl(imageInfo))
                .asBitmap();
    }

    public ResponseFuture<File> download(ImageInfo imageInfo, File file) {
        return Ion.with(context)
                .load(getDownloadUrl(imageInfo))
                .write(file);
    }
}