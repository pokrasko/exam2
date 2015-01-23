package ru.ifmo.md.exam1;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlaylistActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private long playlistId;

    private String[] from = new String[] {}

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        playlistId = bundle.getLong("playlistId");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_playlist);

        listView = (ListView) findViewById(R.id.songs_listview);
        adapter = new SimpleCursorAdapter(this, R.layout.song_item, null, from, to, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        registerForContextMenu(listView);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private int[] getSongsOfPlaylist() {
        Cursor cursor = getContentResolver().query(MusicContentProvider.CONTENT_SONGS_PLAYLISTS_URI,
                null, MusicContentProvider.PLAYLIST_FIELD + "=" + playlistId, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            cursor.moveToNext();
        }
    }
}
