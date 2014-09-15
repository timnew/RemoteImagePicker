package me.timnew.remoteimagepicker;

import android.support.v4.app.Fragment;
import android.widget.ListView;
import me.timnew.remoteimagepicker.images.PodInfo;
import me.timnew.remoteimagepicker.images.PodListAdapter;
import me.timnew.remoteimagepicker.images.RestClient;
import me.timnew.remoteimagepicker.servers.DiscoveryService;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import static me.timnew.remoteimagepicker.ImagePickerActivity.SlidingMenuCommands.CLOSE_SLIDING_MENU;

@EFragment(R.layout.sliding_menu)
public class SlidingMenuFragment extends Fragment {
    @Bean
    protected DiscoveryService discoveryService;

    @Bean
    protected PodListAdapter podListAdapter;

    @Bean
    protected RestClient client;

    @Bean
    protected Bus bus;

    @ViewById(R.id.pod_list)
    protected ListView podList;

    @AfterViews
    protected void afterViews() {
        podList.setAdapter(podListAdapter);
    }

    @ItemClick(R.id.pod_list)
    protected void podSelected(PodInfo pod) {
        bus.post(new CurrentPodInfo(pod));
        bus.post(CLOSE_SLIDING_MENU);
    }

    public static class CurrentPodInfo {
        public final PodInfo pod;

        public CurrentPodInfo(PodInfo pod) {
            this.pod = pod;
        }
    }
}
