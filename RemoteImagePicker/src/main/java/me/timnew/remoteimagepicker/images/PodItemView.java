package me.timnew.remoteimagepicker.images;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.timnew.remoteimagepicker.R;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.pod_item_view)
public class PodItemView extends RelativeLayout {

    public PodItemView(Context context) {
        super(context);
    }

    @ViewById(R.id.pod_name)
    protected TextView podName;

    public void update(PodInfo item) {
        podName.setText(item.name);
    }
}
