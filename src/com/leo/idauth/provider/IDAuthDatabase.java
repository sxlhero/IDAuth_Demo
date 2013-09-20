package com.leo.idauth.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class IDAuthDatabase{
    
    private static final String TAG = IDAuthDatabase.class.getSimpleName();
    
    private final DatabaseHelper mDatabaseHelper;
    
    private static HashMap<String, String> sPersonProjection;
    private static HashMap<String, String> sSubjectProjection;
    private static HashMap<String, String> sExamineeProjection;
    private static HashMap<String, String> sVerificatioProjection;
    
    static {
        //TODO: Add projectionMap if needed
        sPersonProjection = new HashMap<String, String>();
        sSubjectProjection = new HashMap<String, String>();
        sExamineeProjection = new HashMap<String, String>();
        sVerificatioProjection = new HashMap<String, String>();
    }
    

    /**
     * Constructor
     * @param context The Context within which to work, used to create the DB
     */
    public IDAuthDatabase(Context context) {
        // Creates a new helper object. Note that the database itself isn't opened until
        // something tries to access it, and it's only created if it doesn't already exist.
        mDatabaseHelper = new DatabaseHelper(context);
    }
    
    public SQLiteOpenHelper getSQLiteOpenHelper(){
        return mDatabaseHelper;
    }
    
    /**
    *
    * This class helps open, create, and upgrade the database file. Set to package visibility
    * for testing purposes.
    */
   private static class DatabaseHelper extends SQLiteOpenHelper {

       DatabaseHelper(Context context) {
           // calls the super constructor, requesting the default cursor factory.
           super(context, IDAuthMetaData.DATABASE_NAME, null, IDAuthMetaData.DATABASE_VERSION);
       }

       /**
        *
        * Creates the underlying database with table name and column names taken from the
        * NotePad class.
        */
       @Override
       public void onCreate(SQLiteDatabase db) {
           db.execSQL(IDAuthMetaData.PersonMetaData.CREATE_STATEMENT);
           db.execSQL(IDAuthMetaData.SubjectMetaData.CREATE_STATEMENT);
           db.execSQL(IDAuthMetaData.ExamineeMetaData.CREATE_STATEMENT);
           db.execSQL(IDAuthMetaData.VerificationMetaData.CREATE_STATEMENT);
       }

       /**
        *
        * Demonstrates that the provider must consider what happens when the
        * underlying datastore is changed. In this sample, the database is upgraded the database
        * by destroying the existing data.
        * A real application should upgrade the database in place.
        */
       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

           // Logs that the database is being upgraded
           Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                   + newVersion + ", which will destroy all old data");

           // Kills the table and existing data
           db.execSQL("DROP TABLE IF EXISTS " + IDAuthMetaData.PersonMetaData.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS " + IDAuthMetaData.SubjectMetaData.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS " + IDAuthMetaData.ExamineeMetaData.TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS " + IDAuthMetaData.VerificationMetaData.TABLE_NAME);

           // Recreates the database with a new version
           onCreate(db);
       }
   }
}