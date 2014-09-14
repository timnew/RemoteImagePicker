package me.timnew.remoteimagepicker;

import android.support.v4.app.Fragment;
import me.timnew.remoteimagepicker.servers.DiscoveryService;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.sliding_menu)
public class SlidingMenuFragment extends Fragment {
    @Bean
    protected DiscoveryService discoveryService;
}
