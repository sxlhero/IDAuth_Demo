package com.leo.idauth.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class IDAuthProvider extends ContentProvider{
    public static final String TAG = IDAuthProvider.class.getSimpleName();
    
    /**
     * A UriMatcher instance
     */
    private static final UriMatcher sUriMatcher;
    
    // Handle to a new DatabaseHelper.
    private SQLiteOpenHelper mSQLiteOpenHelper;
    
    private static final int PERSON_BASE = 0;
    private static final int PERSON = PERSON_BASE;
    private static final int PERSON_ID = PERSON_BASE + 1;

    private static final int SUBJECT_BASE = 0x1000;
    private static final int SUBJECT = SUBJECT_BASE;
    private static final int SUBJECT_ID = SUBJECT_BASE + 1;

    private static final int EXAMINEE_BASE = 0x2000;
    private static final int EXAMINEE = EXAMINEE_BASE;
    private static final int EXAMINEE_ID = EXAMINEE_BASE + 1;

    private static final int VERIFICATION_BASE = 0x3000;
    private static final int VERIFICATION = VERIFICATION_BASE;
    private static final int VERIFICATION_ID = VERIFICATION_BASE + 1;
    
    private static final int BASE_SHIFT = 12;  // 12 bits to the base type: 0, 0x1000, 0x2000, etc.
    
    private static final String[] TABLE_NAMES = {
        IDAuthMetaData.PersonMetaData.TABLE_NAME,
        IDAuthMetaData.SubjectMetaData.TABLE_NAME,
        IDAuthMetaData.ExamineeMetaData.TABLE_NAME,
        IDAuthMetaData.VerificationMetaData.TABLE_NAME,
    };
    
    private static final String[] TABLE_IDS = {
        IDAuthMetaData.PersonMetaData._ID,
        IDAuthMetaData.SubjectMetaData._ID,
        IDAuthMetaData.ExamineeMetaData._ID,
        IDAuthMetaData.VerificationMetaData._ID,
    };
    
    private static final String[] TABLE_DEFAULT_ORDERS = {
        IDAuthMetaData.PersonMetaData.DEFAULT_SORT_ORDER,
        IDAuthMetaData.SubjectMetaData.DEFAULT_SORT_ORDER,
        IDAuthMetaData.ExamineeMetaData.DEFAULT_SORT_ORDER,
        IDAuthMetaData.VerificationMetaData.DEFAULT_SORT_ORDER,
    };
    
    private static final String[] TABLE_NULL_COLUMNS = {
        IDAuthMetaData.PersonMetaData.ID,
        IDAuthMetaData.SubjectMetaData.SUBJECT_NO,
        IDAuthMetaData.ExamineeMetaData.EXAMINEE_NO,
        IDAuthMetaData.VerificationMetaData.EXAMINEE_NO,
    };
    
    private static final Uri[] TABLE_URLS = {
        IDAuthMetaData.PersonMetaData.CONTENT_URI,
        IDAuthMetaData.SubjectMetaData.CONTENT_URI,
        IDAuthMetaData.ExamineeMetaData.CONTENT_URI,
        IDAuthMetaData.VerificationMetaData.CONTENT_URI,
    };
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        sUriMatcher.addURI(IDAuthMetaData.AUTHORITY, IDAuthMetaData.PersonMetaData.TABLE_NAME, PERSON);
        sUriMatcher.addURI(IDAuthMetaData.AUTHORITY, IDAuthMetaData.PersonMetaData.TABLE_NAME + "/#", PERSON_ID);
        
        sUriMatcher.addURI(IDAuthMetaData.AUTHORITY, IDAuthMetaData.SubjectMetaData.TABLE_NAME, SUBJECT);
        sUriMatcher.addURI(IDAuthMetaData.AUTHORITY, IDAuthMetaData.SubjectMetaData.TABLE_NAME + "/#", SUBJECT_ID);
        
        sUriMatcher.addURI(IDAuthMetaData.AUTHORITY, IDAuthMetaData.ExamineeMetaData.TABLE_NAME, EXAMINEE);
        sUriMatcher.addURI(IDAuthMetaData.AUTHORITY, IDAuthMetaData.ExamineeMetaData.TABLE_NAME + "/#", EXAMINEE_ID);
        
        sUriMatcher.addURI(IDAuthMetaData.AUTHORITY, IDAuthMetaData.VerificationMetaData.TABLE_NAME, VERIFICATION);
        sUriMatcher.addURI(IDAuthMetaData.AUTHORITY, IDAuthMetaData.VerificationMetaData.TABLE_NAME + "/#", VERIFICATION_ID);
    }

    @Override
    public boolean onCreate() {
        mSQLiteOpenHelper =  new IDAuthDatabase(getContext()).getSQLiteOpenHelper();
        return (mSQLiteOpenHelper.getWritableDatabase() == null) ? false : true;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        int match = sUriMatcher.match(uri);
        int table = match >> BASE_SHIFT;
        
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (match) {
            case PERSON:
            case SUBJECT:
            case EXAMINEE:
            case VERIFICATION:
                qb.setTables(TABLE_NAMES[table]);
                break;
            case PERSON_ID:
            case SUBJECT_ID:
            case EXAMINEE_ID:
            case VERIFICATION_ID:
                qb.setTables(TABLE_NAMES[table]);
                qb.appendWhere(
                        TABLE_IDS[table] +    // the name of the ID column
                        "=" +
                        uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        
        String orderBy;
        // If no sort order is specified, uses the default
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = TABLE_DEFAULT_ORDERS[table];
        } else {
            // otherwise, uses the incoming sort order
            orderBy = sortOrder;
        }
                
        // Opens the database object in "read" mode, since no writes need to be done.
        SQLiteDatabase db = mSQLiteOpenHelper.getReadableDatabase();
        Log.d(TAG, "query: uri=" + uri + " ,selection="+ selection + ", match is " + match);
        /*
         * Performs the query. If no problems occur trying to read the database, then a Cursor
         * object is returned; otherwise, the cursor variable contains null. If no records were
         * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
         */
        Cursor c = qb.query(
            db,            // The database to query
            projection,    // The columns to return from the query
            selection,     // The columns for the where clause
            selectionArgs, // The values for the where clause
            null,          // don't group the rows
            null,          // don't filter by row groups
            orderBy        // The sort order
        );

        // Tells the Cursor what URI to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        int table = match >> BASE_SHIFT;
        
        switch (match) {
            case PERSON:
            case SUBJECT:
            case EXAMINEE:
            case VERIFICATION:
            case PERSON_ID:
            case SUBJECT_ID:
            case EXAMINEE_ID:
            case VERIFICATION_ID:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
                
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(
            TABLE_NAMES[table],        // The table to insert into.
            TABLE_NULL_COLUMNS[table],  // A hack, SQLite sets this column value to null
                                             // if values is empty.
            values                           // A map of column names, and the values to insert
                                             // into the columns.
        );

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(TABLE_URLS[table], rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(noteUri, null);
            Log.d(TAG, "insert: uri=" + uri + " ,rowId="+ rowId + ", match is " + match);
            return noteUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
    }
    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int table = match >> BASE_SHIFT;
        
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        String finalSelection;
        int count;

        switch (match) {
            // If the incoming pattern matches the general pattern for notes, does a delete
            // based on the incoming "where" columns and arguments.
            case PERSON:
            case SUBJECT:
            case EXAMINEE:
            case VERIFICATION:
                finalSelection = selection;
                break;
                
            // If the incoming URI matches a single note ID, does the delete based on the
            // incoming data, but modifies the where clause to restrict it to the
            // particular note ID.
            case PERSON_ID:
            case SUBJECT_ID:
            case EXAMINEE_ID:
            case VERIFICATION_ID:
                /*
                 * Starts a final WHERE clause by restricting it to the
                 * desired note ID.
                 */
                finalSelection =
                TABLE_IDS[table] +                              // The ID column name
                " = " +                                         
                uri.getPathSegments().get(1);                  
                
                // If there were additional selection criteria, append them to the final
                // WHERE clause
                if (!TextUtils.isEmpty(selection)) {
                    finalSelection = finalSelection + " AND (" + selection + ')';
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Performs the delete.
        count = db.delete(
            TABLE_NAMES[table],  // The database table name.
            finalSelection,                // The final WHERE clause
            selectionArgs                  // The incoming where clause values.
        );
        Log.d(TAG, "delete: uri=" + uri + " ,selection=" + selection + " ,count="+ count + ", match is " + match);
        
        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows deleted.
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int table = match >> BASE_SHIFT;
        
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        int count;
        String finalSelection;       
        
        switch (match) {
            // If the incoming pattern matches the general pattern for notes, does a delete
            // based on the incoming "where" columns and arguments.
            case PERSON:
            case SUBJECT:
            case EXAMINEE:
            case VERIFICATION:
                finalSelection = selection;
                break;
                
            // If the incoming URI matches a single note ID, does the delete based on the
            // incoming data, but modifies the where clause to restrict it to the
            // particular note ID.
            case PERSON_ID:
            case SUBJECT_ID:
            case EXAMINEE_ID:
            case VERIFICATION_ID:
                /*
                 * Starts a final WHERE clause by restricting it to the
                 * desired note ID.
                 */
                finalSelection =
                TABLE_IDS[table] +                              // The ID column name
                " = " +                                         
                uri.getPathSegments().get(1);                  
                
                // If there were additional selection criteria, append them to the final
                // WHERE clause
                if (!TextUtils.isEmpty(selection)) {
                    finalSelection = finalSelection + " AND (" + selection + ')';
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        // Does the update and returns the number of rows updated.
        count = db.update(
            TABLE_NAMES[table],           // The database table name.
            values,                       // A map of column names and new values to use.
            finalSelection,               // The final WHERE clause to use
                                          // placeholders for whereArgs
            selectionArgs                 // The where clause column values to select on, or
                                          // null if the values are in the where argument.
        );
        Log.d(TAG, "update: uri=" + uri + " ,selection=" + selection + " ,count="+ count + ", match is " + match);

        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows updated.
        return count;
    }
    
    @Override
    public String getType(Uri uri) {
        /**
         * Chooses the MIME type based on the incoming URI pattern
         */
        switch (sUriMatcher.match(uri)) {

            case PERSON:
                return IDAuthMetaData.PersonMetaData.CONTENT_TYPE;
            case PERSON_ID:
                return IDAuthMetaData.PersonMetaData.CONTENT_ITEM_TYPE;
            case SUBJECT:
                return IDAuthMetaData.SubjectMetaData.CONTENT_TYPE;
            case SUBJECT_ID:
                return IDAuthMetaData.SubjectMetaData.CONTENT_ITEM_TYPE;
            case EXAMINEE:
                return IDAuthMetaData.ExamineeMetaData.CONTENT_TYPE;
            case EXAMINEE_ID:
                return IDAuthMetaData.ExamineeMetaData.CONTENT_ITEM_TYPE;
            case VERIFICATION:
                return IDAuthMetaData.VerificationMetaData.CONTENT_TYPE;
            case VERIFICATION_ID:
                return IDAuthMetaData.VerificationMetaData.CONTENT_ITEM_TYPE;
                
            // If the URI pattern doesn't match any permitted patterns, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

}