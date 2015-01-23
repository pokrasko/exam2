package ru.ifmo.md.exam1;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {
    private ListView listView;
    private SimpleCursorAdapter adapter;

    private static final String[] from = new String[] {MusicContentProvider.NAME_FIELD};
    private static final int[] to = new int[] {R.id.playlist_name};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new SimpleCursorAdapter(getApplicationContext(),
                0,
                null,
                from, to, 0);

        listView = (ListView) findViewById(R.id.playlists_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        registerForContextMenu(listView);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra(MusicContentProvider.ID_FIELD, id);
        startActivity(intent);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:
                Cursor cursor = (Cursor) adapter.getItem(acmi.position);
                long id = String.valueOf(cursor.getInt(cursor.getColumnIndex(MusicContentProvider.ID_FIELD)));

                Uri uri = Uri.parse(MusicContentProvider.CONTENT_PLAYLISTS_URI + "/" + id);
                getContentResolver().delete(uri, null, null);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_insert) {
            Intent intent = new Intent(this, NewPlaylistActitity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MusicContentProvider.CONTENT_PLAYLISTS_URI, null, null, null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
