package me.timnew.remoteimagepicker.images;

import android.content.Context;
import android.view.ViewGroup;
import me.timnew.shared.AdvBaseAdapter;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import java.util.List;

@EBean
public class PodListAdapter extends AdvBaseAdapter<PodInfo, PodItemView> {

    @RootContext
    protected Context context;

    @Bean
    protected Bus bus;

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
    protected PodItemView updateView(PodItemView itemView, PodInfo item) {
        itemView.update(item);

        return itemView;
    }

    @Override
    protected PodItemView createView(ViewGroup parent) {
        return PodItemView_.build(context);
    }

    public void onEventMainThread(PodsListUpdatedEvent event) {
        setItems(event.pods);
    }

    public static class PodsListUpdatedEvent {
        public final List<PodInfo> pods;

        public PodsListUpdatedEvent(List<PodInfo> pods) {
            this.pods = pods;
        }
    }
}
