package com.example.user.productlist;

import android.net.Uri;

public class ProductItem {
    String name;
    String explanation;
    Uri image;
    String exist;
    Boolean favorate;

    public ProductItem() {

    }

    public Boolean getFavorate() {
        return favorate;
    }

    public void setFavorate(Boolean favorate) {
        this.favorate = favorate;
    }

    public ProductItem(String name, String explanation, Uri image, String exist, Boolean favorate) {
        this.name = name;
        this.explanation = explanation;
        this.image = image;
        this.exist = exist;
        this.favorate = favorate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
        this.exist = exist;
    }


}

