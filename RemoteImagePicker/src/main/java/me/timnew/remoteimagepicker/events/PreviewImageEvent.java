package me.timnew.remoteimagepicker.events;

import me.timnew.remoteimagepicker.images.ImageInfo;

public class PreviewImageEvent {
    public final ImageInfo imageInfo;

    public PreviewImageEvent(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
}
