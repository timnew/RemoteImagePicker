package me.timnew.remoteimagepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import me.timnew.remoteimagepicker.servers.ServerInfo;
import me.timnew.remoteimagepicker.servers.ServerListAdapter;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;

import static me.timnew.remoteimagepicker.servers.DiscoveryService.DiscoveryServiceCommands.START_DISCOVERY;
import static me.timnew.remoteimagepicker.servers.DiscoveryService.DiscoveryServiceCommands.STOP_DISCOVERY;

@EViewGroup(R.layout.sliding_menu_header)
public class SlidingMenuHeaderView extends LinearLayout {

    public SlidingMenuHeaderView(Context context) {
        super(context);
    }

    public SlidingMenuHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingMenuHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @ViewById(R.id.server_ip)
    protected TextView serverIp;

    @ViewById(R.id.server_list)
    protected ListView serverList;

    @Bean
    protected ServerListAdapter serverListadapter;

    @Bean
    protected Bus bus;

    @AfterViews
    protected void afterViews() {
        serverList.setAdapter(serverListadapter);
    }

    @Click(R.id.search_server)
    protected void onSearchServerClicked() {
        serverList.setVisibility(VISIBLE);
        bus.post(START_DISCOVERY);
    }

    @ItemClick(R.id.server_list)
    protected void serverItemClicked(ServerInfo server) {
        bus.post(STOP_DISCOVERY);
        serverIp.setText(server.toString());
        serverList.setVisibility(GONE);

        bus.post(new CurrentServerInfo(server));
    }

    public static class CurrentServerInfo {
        public final ServerInfo currentServer;

        public CurrentServerInfo(ServerInfo currentServer) {
            this.currentServer = currentServer;
        }
    }
}
