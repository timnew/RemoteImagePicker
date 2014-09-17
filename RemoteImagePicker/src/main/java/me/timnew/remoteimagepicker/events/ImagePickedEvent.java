package me.timnew.remoteimagepicker.events;

import me.timnew.remoteimagepicker.images.ImageInfo;

public class ImagePickedEvent {
    public final ImageInfo imageInfo;

    public ImagePickedEvent(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
}
