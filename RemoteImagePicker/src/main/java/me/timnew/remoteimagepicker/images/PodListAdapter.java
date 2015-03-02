package me.timnew.remoteimagepicker.images;

import android.content.Context;
import android.view.ViewGroup;
import me.timnew.remoteimagepicker.events.PodsListUpdatedEvent;
import me.timnew.shared.AdvBaseAdapter;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

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
}
