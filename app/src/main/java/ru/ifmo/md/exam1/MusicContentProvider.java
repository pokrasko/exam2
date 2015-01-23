package ru.ifmo.md.exam1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MusicContentProvider extends ContentProvider {
    public static String DB_NAME = "music.db";
    public static int DB_VERSION = 1;

    private static final int MUSIC = 0;
    private static final int MUSIC_ID = 1;
    private static final int PLAYLISTS = 2;
    private static final int PLAYLISTS_ID = 3;
    private static final int GENRES = 4;
    private static final int GENRES_ID = 5;
    private static final int SONGS_PLAYLISTS = 6;
    private static final int SONGS_PLAYLISTS_ID = 7;
    private static final int SONGS_GENRES = 8;
    private static final int SONGS_GENRES_ID = 9;

    public static final String ID_FIELD = "id";
    public static final String ARTIST_FIELD = "artist";
    public static final String NAME_FIELD = "name";
    public static final String URL_FIELD = "url";
    public static final String DURATION_FIELD = "duration";
    public static final String POPULARITY_FIELD = "popularity";
    public static final String YEAR_FIELD = "year";
    public static final String SONG_FIELD = "song";
    public static final String GENRE_FIELD = "genre";
    public static final String PLAYLIST_FIELD = "playlist";

    private static final String AUTHORITY = "com.pokrasko.exam2";

    private static final String MUSIC_PATH = "music";
    private static final String PLAYLISTS_PATH = "playlists";
    private static final String SONGS_PLAYLISTS_PATH = "songsplaylists";
    private static final String GENRES_PATH = "genres";
    private static final String SONGS_GENRES_PATH = "songsgenres";

    public static final Uri CONTENT_MUSIC_URI = Uri.parse("content://" +
            AUTHORITY + "/" + MUSIC_PATH);
    public static final Uri CONTENT_PLAYLISTS_URI = Uri.parse("content://" +
            AUTHORITY + "/" + PLAYLISTS_PATH);
    public static final Uri CONTENT_SONGS_PLAYLISTS_URI = Uri.parse("content://" +
            AUTHORITY + "/" + SONGS_PLAYLISTS_PATH);
    public static final Uri CONTENT_GENRES_URI = Uri.parse("content://" +
            AUTHORITY + "/" + GENRES_PATH);
    public static final Uri CONTENT_SONGS_GENRES_URI = Uri.parse("content://" +
            AUTHORITY + "/" + SONGS_GENRES_PATH);

    private static final String MUSIC_TABLE = "music";
    private static final String PLAYLISTS_TABLE = "playlists";
    private static final String SONGS_PLAYLISTS_TABLE = "songsplaylists";
    private static final String GENRES_TABLE = "genres";
    private static final String SONGS_GENRES_TABLE = "songsgenres";

    private static final String CREATE_MUSIC_TABLE = "CREATE TABLE " + MUSIC_PATH +
            " (" + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", " + ARTIST_FIELD + " TEXT NOT NULL" +
            ", " + URL_FIELD + " TEXT NOT NULL UNIQUE" +
            ", " + DURATION_FIELD + " TEXT NOT NULL" +
            ", " + POPULARITY_FIELD + " INTEGER" +
            ", " + YEAR_FIELD + " INTEGER NOT NULL);";
    private static final String CREATE_PLAYLISTS_TABLE = "CREATE TABLE " + PLAYLISTS_PATH +
            " (" + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", " + NAME_FIELD + " TEXT NOT NULL);";
    private static final String CREATE_GENRES_TABLE = "CREATE TABLE " + GENRES_PATH +
            " (" + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", " + NAME_FIELD + " TEXT NOT NULL);";
    private static final String CREATE_SONGS_PLAYLISTS_TABLE = "CREATE TABLE " + SONGS_PLAYLISTS_PATH +
            " (" + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", " + SONG_FIELD + " INTEGER REFERENCES music(id) ON DELETE CASCADE" +
            ", " + PLAYLIST_FIELD + " INTEGER REFERENCES playlists(id) ON DELETE CASCADE);";
    private static final String CREATE_SONGS_GENRES_TABLE = "CREATE TABLE " + SONGS_GENRES_PATH +
            " (" + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", " + SONG_FIELD + " INTEGER REFERENCES music(id) ON DELETE CASCADE" +
            ", " + GENRE_FIELD + " INTEGER REFERENCES genres(id) ON DELETE CASCADE);";

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(AUTHORITY, MUSIC_PATH, MUSIC);
        matcher.addURI(AUTHORITY, MUSIC_PATH + "/#", MUSIC_ID);
        matcher.addURI(AUTHORITY, PLAYLISTS_PATH, PLAYLISTS);
        matcher.addURI(AUTHORITY, PLAYLISTS_PATH + "/#", PLAYLISTS_ID);
        matcher.addURI(AUTHORITY, GENRES_PATH, GENRES);
        matcher.addURI(AUTHORITY, GENRES_PATH + "/#", GENRES_ID);
        matcher.addURI(AUTHORITY, SONGS_PLAYLISTS_PATH, SONGS_PLAYLISTS);
        matcher.addURI(AUTHORITY, SONGS_PLAYLISTS_PATH + "/#", SONGS_PLAYLISTS_ID);
        matcher.addURI(AUTHORITY, SONGS_GENRES_PATH, SONGS_GENRES);
        matcher.addURI(AUTHORITY, SONGS_GENRES_PATH + "/#", SONGS_GENRES_ID);
    }

    private ImageDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new ImageDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return Integer.toString(matcher.match(uri));
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        int uriType = matcher.match(uri);

        switch (uriType) {
            case MUSIC:
                id = db.insert(MUSIC_TABLE, null, values);
                break;
            case PLAYLISTS:
                id = db.insert(PLAYLISTS_TABLE, null, values);
                break;
            case GENRES:
                id = db.insert(GENRES_TABLE, null, values);
                break;
            case SONGS_PLAYLISTS:
                id = db.insert(SONGS_PLAYLISTS_TABLE, null, values);
                break;
            case SONGS_GENRES:
                id = db.insert(SONGS_GENRES_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, "" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int uriType = matcher.match(uri);

        int updatedRows;
        switch (uriType) {
            case MUSIC_ID:
                String songId = uri.getLastPathSegment();
                updatedRows = db.update(MUSIC_TABLE, values, ID_FIELD + "=" + songId, null);
                break;
            case PLAYLISTS_ID:
                String playlistId = uri.getLastPathSegment();
                updatedRows = db.update(MUSIC_TABLE, values, ID_FIELD + "=" + playlistId, null);
                break;
            case GENRES_ID:
                String genreId = uri.getLastPathSegment();
                updatedRows = db.update(MUSIC_TABLE, values, ID_FIELD + "=" + genreId, null);
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = matcher.match(uri);

        switch (uriType) {
            case MUSIC:
                queryBuilder.setTables(MUSIC_TABLE);
                break;
            case MUSIC_ID:
                queryBuilder.setTables(MUSIC_TABLE);
                queryBuilder.appendWhere(ID_FIELD + "=" + uri.getLastPathSegment());
                break;
            case PLAYLISTS:
                queryBuilder.setTables(PLAYLISTS_TABLE);
                break;
            case PLAYLISTS_ID:
                queryBuilder.setTables(PLAYLISTS_TABLE);
                queryBuilder.appendWhere(ID_FIELD + "=" + uri.getLastPathSegment());
                break;
            case GENRES:
                queryBuilder.setTables(GENRES_TABLE);
                break;
            case GENRES_ID:
                queryBuilder.setTables(GENRES_TABLE);
                queryBuilder.appendWhere(ID_FIELD + "=" + uri.getLastPathSegment());
                break;
            case SONGS_PLAYLISTS:
                queryBuilder.setTables(SONGS_PLAYLISTS_TABLE);
                break;
            case SONGS_PLAYLISTS_ID:
                queryBuilder.setTables(SONGS_PLAYLISTS_TABLE);
                queryBuilder.appendWhere(PLAYLIST_FIELD + "=" + uri.getLastPathSegment());
                break;
            case SONGS_GENRES:
                queryBuilder.setTables(SONGS_GENRES_TABLE);
                break;
            case SONGS_GENRES_ID:
                queryBuilder.setTables(SONGS_GENRES_TABLE);
                queryBuilder.appendWhere(GENRE_FIELD + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int uriType = matcher.match(uri);

        int deletedRows;
        switch (uriType) {
            case MUSIC_ID:
                String songId = uri.getLastPathSegment();
                deletedRows = db.delete(MUSIC_TABLE, ID_FIELD + "=" + songId, null);
                break;
            case PLAYLISTS_ID:
                String playlistId = uri.getLastPathSegment();
                deletedRows = db.delete(PLAYLISTS_TABLE, ID_FIELD + "=" + playlistId, null);
                break;
            case GENRES_ID:
                String genreId = uri.getLastPathSegment();
                deletedRows = db.delete(GENRES_TABLE, ID_FIELD + "=" + genreId, null);
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }


    public class ImageDBHelper extends SQLiteOpenHelper {
        public ImageDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL(CREATE_MUSIC_TABLE);
            db.execSQL(CREATE_PLAYLISTS_TABLE);
            db.execSQL(CREATE_GENRES_TABLE);
            db.execSQL(CREATE_SONGS_PLAYLISTS_TABLE);
            db.execSQL(CREATE_SONGS_GENRES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int o, int n) {
            db.execSQL("DROP TABLE IF EXISTS " + MUSIC_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PLAYLISTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + GENRES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SONGS_PLAYLISTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SONGS_GENRES_TABLE);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int o, int n) {
            db.execSQL("DROP TABLE IF EXISTS " + MUSIC_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PLAYLISTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + GENRES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SONGS_PLAYLISTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SONGS_GENRES_TABLE);
            onCreate(db);
        }
    }
}
