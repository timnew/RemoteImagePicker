package me.timnew.remoteimagepicker.images;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import me.timnew.remoteimagepicker.R;
import me.timnew.remoteimagepicker.events.ImageListUpdatedEvent;
import me.timnew.remoteimagepicker.events.PreviewImageEvent;
import me.timnew.shared.AdvBaseAdapter;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

@EBean
public class ImageGridAdapter extends AdvBaseAdapter<ImageInfo, ImageItemView> {

    @RootContext
    protected Context context;

    @Bean
    protected Bus bus;

    @Bean
    protected RestClient client;

    @ViewById(R.id.image_grid)
    protected GridView imageGird;

    @AfterViews
    protected void afterViews() {

        bus.register(this);
        imageGird.setAdapter(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        bus.unregister(this);
    }

    @ItemClick(R.id.image_grid)
    protected void imageSelected(ImageInfo image) {
        bus.post(new PreviewImageEvent(image));
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

}
