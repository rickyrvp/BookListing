package com.android.booklisting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.android.booklisting.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String GOOGLE_BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";

    private static final int BOOK_LOADER_ID = 1;
    private static final String LOG_TAG = BookActivity.class.getSimpleName();

    private SearchView mSearchViewField;

    private BookAdapter mAdapter;
    private String searchValue = "";

    LoaderManager loaderManager;

    private TextView mEmptyStateTextView;

    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);

        loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);


        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);


        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setAdapter(mAdapter);

        mSearchViewField = findViewById(R.id.search_view_field);
        mSearchViewField.onActionViewExpanded();
        mSearchViewField.setIconified(true);
        mSearchViewField.setQueryHint("Enter a book title");

        Button mSearchButton = (Button) findViewById(R.id.search_button);

        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        mSearchViewField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchValue = query;
                loadingIndicator.setVisibility(View.VISIBLE);
                getSupportLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchValue = mSearchViewField.getQuery().toString();
                loadingIndicator.setVisibility(View.VISIBLE);
                getSupportLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
            }
        });

        //Set an item click listener on the ListView, which sends an intent to a web browser
        //to open a website with more information
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Find the current book that was clicked
                Book currentBook = mAdapter.getItem(position);

                //Convert the URL into URI object
                Uri buyBookUri = Uri.parse(currentBook.getmUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, buyBookUri);
                startActivity(websiteIntent);
            }
        });


    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {



        Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchValue);
        uriBuilder.appendQueryParameter("maxResults", "20");

        mEmptyStateTextView.setVisibility(View.GONE);

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {

        //hide loading indicator because the data has been loaded
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText("No books found");


        // Clear the adapter of previous book data
        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {

        mAdapter.clear();

    }
}
