package me.timnew.remoteimagepicker.events;

import me.timnew.remoteimagepicker.images.PodInfo;

import java.util.List;

public class PodsListUpdatedEvent {
    public final List<PodInfo> pods;

    public PodsListUpdatedEvent(List<PodInfo> pods) {
        this.pods = pods;
    }
}
