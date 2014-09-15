package me.timnew.remoteimagepicker.images;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import me.timnew.remoteimagepicker.R;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.image_item_view)
public class ImageItemView extends FrameLayout {
    public ImageItemView(Context context) {
        super(context);
    }

    @ViewById(R.id.image_view)
    protected ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }
}
