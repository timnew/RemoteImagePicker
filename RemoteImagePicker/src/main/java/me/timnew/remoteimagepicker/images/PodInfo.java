package me.timnew.remoteimagepicker.images;

import java.io.Serializable;

public class PodInfo implements Serializable {
    public final int id;
    public final String name;

    public PodInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

