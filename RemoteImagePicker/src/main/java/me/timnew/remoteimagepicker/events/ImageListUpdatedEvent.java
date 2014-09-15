package me.timnew.remoteimagepicker.events;

import me.timnew.remoteimagepicker.images.ImageInfo;

import java.util.List;

public class ImageListUpdatedEvent {
    public final List<ImageInfo> imageInfos;

    public ImageListUpdatedEvent(List<ImageInfo> imageInfos) {
        this.imageInfos = imageInfos;
    }
}
