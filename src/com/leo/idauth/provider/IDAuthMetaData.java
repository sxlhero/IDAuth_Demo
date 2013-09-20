package com.leo.idauth.provider;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;

public class IDAuthMetaData{
    
    public static final String AUTHORITY = "com.leo.idauth.provider.IDAuthProvider";
    public static final String DATABASE_NAME = "idauth.db";
    public static final int DATABASE_VERSION = 2;
    
    
    public static final class PersonMetaData implements BaseColumns{
        public static final String TABLE_NAME = "person";
        
        //uri and MIME type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.idauth." + TABLE_NAME;
        
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.idauth." + TABLE_NAME;
        
        // Column names
        public static final String ID = "id";
        
        public static final String NAME = "name";
        
        public static final String SEX = "sex";
        
        public static final String NATIONALITY = "nationality";
        
        public static final String BIRTHDAY = "birthday";
        
        public static final String ADDRESS = "address";
        
        public static final String AUTHOR = "author";
        
        public static final String VALID_FROM = "valid_from";
        
        public static final String VALID_TO = "valid_to";
        
        public static final String LATEST_ADDRESS = "latest_address";
        
        public static final String PHOTO_IDCARD = "photo_idcard";
        
        public static final String PHOTO_URL = "photo_url";
        
        public static final String PHOTO = "photo";
        
        public static final String FINGER_IDCARD_SUPPORT = "finger_support";
        
        public static final String FINGER1_IDCARD = "finger1_idcard";
        
        public static final String FINGER2_IDCARD = "finger2_idcard";
        
        public static final String FINGER1_SCAN = "finger1_scan";
        
        public static final String FINGER2_SCAN = "finger2_scan";
        
        // Internal data
        public static final String DEFAULT_SORT_ORDER = ID + " DESC";
        
        //create table sql 
        public static final String CREATE_STATEMENT = "create table if not exists " 
                + TABLE_NAME
                + "(" 
                + BaseColumns._ID + " INTEGER PRIMARY KEY, " 
                + ID + " TEXT, " 
                + NAME + " TEXT, " 
                + SEX + " TEXT, " 
                + NATIONALITY + " TEXT, " 
                + BIRTHDAY + " LONG, " 
                + ADDRESS + " TEXT, " 
                + AUTHOR + " TEXT, " 
                + VALID_FROM + " LONG, " 
                + VALID_TO + " LONG, " 
                + LATEST_ADDRESS + " TEXT, " 
                + PHOTO_IDCARD + " BLOB, " 
                + PHOTO_URL + " TEXT, "
                + PHOTO + " BLOB, " 
                + FINGER_IDCARD_SUPPORT + " BOOLEAN, " 
                + FINGER1_IDCARD + " BLOB, " 
                + FINGER2_IDCARD + " BLOB, "
                + FINGER1_SCAN + " BLOB, " 
                + FINGER2_SCAN + " BLOB"
                + " );";
        
    }
    public static final class SubjectMetaData implements BaseColumns{
        public static final String TABLE_NAME = "subject";
        
        //uri and MIME type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.idauth."+TABLE_NAME;
        
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.idauth."+TABLE_NAME;
        
        // Column names
        public static final String SUBJECT_NO = "subject_no";
        
        public static final String SUBJECT_NAME = "subject_name";
        
        public static final String SUBJECT_DATE = "subject_date";
        
        public static final String START_TIME = "start_time";
        
        public static final String END_TIME = "end_time";
        
        public static final String SIGN_START_TIME = "sign_start";
        
        public static final String SIGN_END_TIME = "sign_end";
        
        // Internal data
        public static final String DEFAULT_SORT_ORDER = SUBJECT_NO + " DESC";
        
        //create table sql 
        public static final String CREATE_STATEMENT = "create table if not exists " 
                + TABLE_NAME
                + "(" 
                + BaseColumns._ID + " INTEGER PRIMARY KEY, " 
                + SUBJECT_NO + " TEXT, " 
                + SUBJECT_NAME + " TEXT, " 
                + SUBJECT_DATE + " LONG, " 
                + START_TIME + " LONG, "
                + END_TIME + " LONG, " 
                + SIGN_START_TIME + " LONG, " 
                + SIGN_END_TIME + " LONG"
                + " );";
        
    }
    public static final class ExamineeMetaData implements BaseColumns{
        public static final String TABLE_NAME = "examinee";
        
        //uri and MIME type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.idauth."+TABLE_NAME;
        
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.idauth."+TABLE_NAME;
        
        // Column names
        public static final String EXAMINEE_NO = "examinee_no";
        
        public static final String ID = PersonMetaData.ID;
        
        public static final String EXAMINATION_NO = "exam_no";
        
        public static final String HALL_NO = "hall_no";
        
        public static final String ROOM_NO = "room_no";
        
        public static final String SUBJECT_NO = SubjectMetaData.SUBJECT_NO;
        
        // Internal data
        public static final String DEFAULT_SORT_ORDER = EXAMINEE_NO + " DESC";
        
        //create table sql 
        public static final String CREATE_STATEMENT = "create table if not exists " 
                + TABLE_NAME
                + "(" 
                + BaseColumns._ID + " INTEGER PRIMARY KEY, " 
                + EXAMINEE_NO + " TEXT, " 
                + ID + " TEXT, " 
                + EXAMINATION_NO + " TEXT, " 
                + HALL_NO + " TEXT, "
                + ROOM_NO + " TEXT, " 
                + SUBJECT_NO + " TEXT "
                + " );";
        
    }
    public static final class VerificationMetaData implements BaseColumns{
        public static final String TABLE_NAME = "verification";
        
        //uri and MIME type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/verification");
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.idauth.verification";
        
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.idauth.verification";
        
        // Column names
        public static final String EXAMINEE_NO = ExamineeMetaData.EXAMINEE_NO;
        
        public static final String EXAMINATION_NO = ExamineeMetaData.EXAMINATION_NO;
        
        public static final String HALL_NO = ExamineeMetaData.HALL_NO;
        
        public static final String ROOM_NO = ExamineeMetaData.ROOM_NO;
        
        public static final String SUBJECT_NO = SubjectMetaData.SUBJECT_NO;
        
        public static final String VERIFY_FINGER = "verify_finger";
        
        public static final String VERIFY_TIME = "verify_time";
        
        public static final String VERIFY_TYPE = "verify_type";
        
        public static final String VERIFY_STATE = "verify_state";
        
        // Internal data
        public static final String DEFAULT_SORT_ORDER = EXAMINEE_NO + " DESC";
        
        //create table sql 
        public static final String CREATE_STATEMENT = "create table if not exists " 
                + TABLE_NAME
                + "(" 
                + BaseColumns._ID + " INTEGER PRIMARY KEY, " 
                + EXAMINEE_NO + " TEXT, " 
                + EXAMINATION_NO + " TEXT, " 
                + HALL_NO + " TEXT, " 
                + ROOM_NO + " TEXT, "
                + SUBJECT_NO + " TEXT, " 
                + VERIFY_FINGER + " BLOB, "
                + VERIFY_TIME + " LONG, "
                + VERIFY_TYPE + " INTEGER, "
                + VERIFY_STATE + " INTEGER"
                + " );";
    }
}
