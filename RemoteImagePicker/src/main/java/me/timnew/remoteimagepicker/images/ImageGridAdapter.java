package me.timnew.remoteimagepicker.images;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import me.timnew.shared.AdvBaseAdapter;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

@EBean
public class ImageGridAdapter extends AdvBaseAdapter<ImageInfo, ImageItemView> {

    @RootContext
    protected Context context;

    @Bean
    protected Bus bus;

    @Bean
    protected RestClient client;

    @AfterInject
    protected void afterInject() {
        bus.register(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        bus.unregister(this);
    }

    @Override
    protected ImageItemView updateView(ImageItemView itemView, ImageInfo item) {
        ImageView imageView = itemView.getImageView();

        client.loadImage(item, imageView);

        return itemView;
    }

    @Override
    protected ImageItemView createView(ViewGroup parent) {
        return ImageItemView_.build(context);
    }

    public void onEvent(ImageListUpdatedEvent event) {
        this.setItems(event.imageInfos);
    }

    public static class ImageListUpdatedEvent {
        public final List<ImageInfo> imageInfos;

        public ImageListUpdatedEvent(List<ImageInfo> imageInfos) {
            this.imageInfos = imageInfos;
        }
    }
}
