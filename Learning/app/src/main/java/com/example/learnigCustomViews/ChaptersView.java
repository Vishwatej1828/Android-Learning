package com.example.learnigCustomViews;

public class ChaptersView {
    private int logo;
    private String link;

    public ChaptersView(int f_image, String f_name) {
        logo = f_image;
        link = f_name;
    }


    public int getLogo() {
        return logo;
    }

    public String getLink() {
        return link;
    }

}
