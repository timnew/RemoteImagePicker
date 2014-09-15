package me.timnew.remoteimagepicker.images;

import java.io.Serializable;

public class ImageInfo implements Serializable {
    public final String name;
    public final int size;

    public ImageInfo(String name, int size) {
        this.name = name;
        this.size = size;
    }
}
