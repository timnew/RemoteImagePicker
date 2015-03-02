package me.timnew.remoteimagepicker;

import android.support.v4.app.Fragment;
import me.timnew.remoteimagepicker.images.ImageGridAdapter;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_image_grid)
public class ImageGridFragment extends Fragment {
    @Bean
    protected ImageGridAdapter imageGridAdapter;
}
