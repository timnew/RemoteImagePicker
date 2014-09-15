package me.timnew.remoteimagepicker.servers;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.timnew.remoteimagepicker.R;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.server_item_view)
public class ServerItemView extends RelativeLayout {
    public ServerItemView(Context context) {
        super(context);
    }

    @ViewById(R.id.ip_address)
    protected TextView ipAddress;

    public void update(ServerInfo server) {
        ipAddress.setText(server.toString());
    }
}
