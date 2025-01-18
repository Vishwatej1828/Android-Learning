package com.example.recycleviewworkout;


public class Company {
    private final String name;
    private final int imageResId;
    private final int shortDescription;
    private final int fullDescription;

    public Company(String name, int imageResId, int shortDescription, int fullDescription) {
        this.name = name;
        this.imageResId = imageResId;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getShortDescription() {
        return shortDescription;
    }

    public int getFullDescription() {
        return fullDescription;
    }
}
