package com.android.booklisting;

public class Book {

    private String mTitle;
    private String mAuthor;
    private String mImageUrl;



    private String mUrl;


    public Book(String title, String author, String imageUrl, String url){

        this.mTitle = title;
        this.mAuthor = author;
        this.mImageUrl = imageUrl;
        this.mUrl = url;

    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }


    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmUrl() {
        return mUrl;
    }
}
