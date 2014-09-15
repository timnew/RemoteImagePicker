package me.timnew.remoteimagepicker.events;

import me.timnew.remoteimagepicker.images.PodInfo;

public class CurrentPodInfo {
    public final PodInfo pod;

    public CurrentPodInfo(PodInfo pod) {
        this.pod = pod;
    }
}
