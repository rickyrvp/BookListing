package com.android.booklisting;

import com.android.booklisting.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    private static  String LOG_TAG;

    public BookAdapter(@NonNull Context context, List<Book> book) {
        super(context, 0, book);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null){

            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.book_list_item, parent, false );
        }

        Book currentBook = getItem(position);

        TextView titleView =listItemView.findViewById(R.id.book_title);
        titleView.setText(currentBook.getmTitle());

        TextView authorView =listItemView.findViewById(R.id.author);
        authorView.setText(currentBook.getmAuthor());

        ImageView imageView = listItemView.findViewById(R.id.cover_image);
        Glide.with(getContext()).load(currentBook.getmImageUrl()).into(imageView);


        return listItemView;
    }
}
