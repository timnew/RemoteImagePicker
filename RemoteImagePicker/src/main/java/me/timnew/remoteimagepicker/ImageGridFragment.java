package me.timnew.remoteimagepicker;

import android.support.v4.app.Fragment;
import android.widget.GridView;
import me.timnew.remoteimagepicker.images.ImageGridAdapter;
import me.timnew.remoteimagepicker.images.ImageInfo;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import static me.timnew.remoteimagepicker.ImagePreviewFragment.ImagePreviewEvent;

@EFragment(R.layout.fragment_image_grid)
public class ImageGridFragment extends Fragment {

    @ViewById(R.id.image_grid)
    protected GridView imageGird;

    @Bean
    protected Bus bus;

    @Bean
    protected ImageGridAdapter imageGridAdapter;

    @AfterViews
    protected void afterViews() {
        imageGird.setAdapter(imageGridAdapter);
    }

    @ItemClick(R.id.image_grid)
    protected void imageSelected(ImageInfo image) {
        bus.post(new ImagePreviewEvent(image));
    }
}
