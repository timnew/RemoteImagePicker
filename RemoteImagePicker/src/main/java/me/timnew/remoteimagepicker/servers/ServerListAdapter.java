package me.timnew.remoteimagepicker.servers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import me.timnew.shared.AdvBaseAdapter;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import java.util.ArrayList;

import static me.timnew.remoteimagepicker.servers.DiscoveryService.DiscoveryStatusChangedEvent;

@EBean
public class ServerListAdapter extends AdvBaseAdapter<ServerInfo, ServerView> {

    @RootContext
    protected Context context;

    @SystemService
    protected LayoutInflater inflater;

    @Bean
    protected Bus bus;

    public ServerListAdapter() {
        super(new ArrayList<ServerInfo>());
    }

    @AfterInject
    protected void afterInject() {
        bus.register(this);
    }

    @Override
    protected ServerView createView(ViewGroup parent) {
        return ServerView_.build(context);
    }

    @Override
    protected ServerView updateView(ServerView itemView, ServerInfo item) {
        itemView.update(item);
        return itemView;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(DiscoveryStatusChangedEvent event) {
        switch (event.type) {
            case SERVER_ONLINE:
                getItems().add(event.server);
                break;
            case SERVER_OFFLINE:
                getItems().remove(event.server);
                break;
        }
        notifyDataSetChanged();
    }
}