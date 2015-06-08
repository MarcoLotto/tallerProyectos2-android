package com.example.marco.fiubados.commons;

import com.example.marco.fiubados.model.WallPost;

import java.util.Comparator;

public class WallPostComparator implements Comparator<WallPost>{

    @Override
    public int compare(WallPost comentary, WallPost comentary2) {
        Long date1 = Long.valueOf(comentary.getDate());
        Long date2 = Long.valueOf(comentary2.getDate());
        return date1 > date2 ? -1 : date1.equals(date2) ? 0 : 1;
    }
}
