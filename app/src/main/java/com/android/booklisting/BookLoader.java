package com.android.booklisting;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String mUrl;


    public BookLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null){
            return null;
        }

        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}
