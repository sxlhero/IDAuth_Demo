package com.leo.idauth.provider;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import cn.com.aratek.idcard.IDCard;



public class DatabaseUtils {
    public static String TAG = DatabaseUtils.class.getSimpleName();
    
    public static class Person {

        public String mName;

        public String mSex;

        public String mNationality;

        public Date mBirthday;

        public String mAddress;

        public String mNumber;

        public String mAuthority;

        public Date mValidFrom;

        public Date mValidTo;

        public String mLatestAddress;
        
        public Bitmap mPhoto_idcard;

        public String mPhoto_url; // additional definition

        public Bitmap mPhoto;    // additional definition

        public boolean mFinger_idcard_support;

        public byte[] mFinger1_idcard;

        public byte[] mFinger2_idcard;

        public byte[] mFinger1_scan; // additional definition

        public byte[] mFinger2_scan; // additional definition

        public static Person getTestInstance() {
            Person person = new Person();
            person.mNumber = "12345";
            return person;

        }

        public static Person valueOf(IDCard card) {
            //information from idcard
            Person person = new Person();
            person.mName = card.getName();
            person.mSex = card.getSex().name();
            person.mNationality = card.getNationality().name();
            person.mBirthday = card.getBirthday();
            person.mAddress = card.getAddress();
            person.mNumber = card.getNumber();
            person.mAuthority = card.getAuthority();
            person.mValidFrom = card.getValidFrom();
            person.mValidTo = card.getValidTo();
            person.mLatestAddress = card.getAddress();
            person.mPhoto_idcard = card.getPhoto();
            person.mFinger_idcard_support = card.isSupportFingerprint();
            person.mFinger1_idcard = card.getFpCharacteristic1();
            person.mFinger2_idcard = card.getFpCharacteristic2();
            //additional information from pad
            person.mPhoto_url = null;
            person.mPhoto = null;
            person.mFinger1_scan = null;
            person.mFinger2_scan = null;
            
            return person;

        }

    }

    public static class Subject{
       public String mSubjectNO;
       
       public String mSubjectName;
       
       public String mSubjectDate;
    
       public Date mStartTime;
       
       public Date mEndTime;
       
       public Date mSignStartTime;
       
       public Date mSignEndTime;
    }


    public static class Examinee {
        public String mStudentNO;

        public String mID;

        public String mStudentName;

        public String mExamNO;

        public String mHallNO;

        public String mRoomNO;

        public String mSubjectNO;

        public byte[] mFinger1;

        public byte[] mFinger2;
    }

    public static class Verification {
        public String mStudentNO;

        public String mExamNO;

        public String mHallNO;

        public String mRoomNO;

        public String mSubjectNO;

        public String mVerifyFinger;

        public String mVerifyTime;

        public int mVerifyType;

        public int mVerifyState;
    }
    
    public static boolean addOrUpdatePerson(Context context, Person person){
        boolean ret = false;
        ContentResolver cr = context.getContentResolver();
        String selection = IDAuthMetaData.PersonMetaData.ID + " = " + person.mNumber;
        Cursor c = cr.query(IDAuthMetaData.PersonMetaData.CONTENT_URI, null, selection, null, null);
        
        if (null == c) {
            Log.w(TAG, "Null Cursor pointer!");
            return false;
        }

        if (c.getCount() > 0) {
            ret = updatePerson(context, person);
        } else {
            ret = addPerson(context, person);
        }
        c.close();
        return ret;
    }
    
    public static boolean addPerson(Context context, Person person) {
        ContentResolver cr = context.getContentResolver();
        
        String selection = IDAuthMetaData.PersonMetaData.ID + " = " + person.mNumber;
        Cursor c = cr.query(IDAuthMetaData.PersonMetaData.CONTENT_URI, null, selection, null, null);

        if (null == c) {
            Log.w(TAG, "Null Cursor pointer!");
            return false;
        }

        if (c.getCount() > 0) {
            Log.w(TAG, "same ID person exists already");
            c.close();
            return false;
        }
        c.close();

        ContentValues values = new ContentValues();
        
        //information from idcard
        values.put(IDAuthMetaData.PersonMetaData.NAME, person.mName);
        values.put(IDAuthMetaData.PersonMetaData.SEX, person.mSex);
        values.put(IDAuthMetaData.PersonMetaData.NATIONALITY, person.mNationality);
        values.put(IDAuthMetaData.PersonMetaData.BIRTHDAY, person.mBirthday!=null ? person.mBirthday.getTime(): null);
        values.put(IDAuthMetaData.PersonMetaData.ADDRESS, person.mAddress);
        values.put(IDAuthMetaData.PersonMetaData.ID, person.mNumber);
        values.put(IDAuthMetaData.PersonMetaData.AUTHOR, person.mAuthority);
        values.put(IDAuthMetaData.PersonMetaData.VALID_FROM, person.mValidFrom!=null ? person.mValidFrom.getTime(): null);
        values.put(IDAuthMetaData.PersonMetaData.VALID_TO, person.mValidTo!=null ? person.mValidTo.getTime(): null);
        values.put(IDAuthMetaData.PersonMetaData.LATEST_ADDRESS, person.mLatestAddress);
        values.put(IDAuthMetaData.PersonMetaData.PHOTO_IDCARD, Bitmap2Bytes(person.mPhoto_idcard));
        values.put(IDAuthMetaData.PersonMetaData.FINGER_IDCARD_SUPPORT, person.mFinger_idcard_support);
        values.put(IDAuthMetaData.PersonMetaData.FINGER1_IDCARD, person.mFinger1_idcard);
        values.put(IDAuthMetaData.PersonMetaData.FINGER2_IDCARD, person.mFinger2_idcard);
        //additional information from pad
        values.put(IDAuthMetaData.PersonMetaData.PHOTO_URL, person.mPhoto_url);
        values.put(IDAuthMetaData.PersonMetaData.PHOTO, Bitmap2Bytes(person.mPhoto));
        values.put(IDAuthMetaData.PersonMetaData.FINGER1_SCAN, person.mFinger1_scan);
        values.put(IDAuthMetaData.PersonMetaData.FINGER2_SCAN, person.mFinger2_scan);
 
        Uri uri = cr.insert(IDAuthMetaData.PersonMetaData.CONTENT_URI, values);

        return uri !=null ? true: false;
    }

    public static boolean updatePerson(Context context, Person person) {
        ContentResolver cr = context.getContentResolver();
        
        String selection = IDAuthMetaData.PersonMetaData.ID + " = " + person.mNumber;

        ContentValues values = new ContentValues();

        //information from idcard
        values.put(IDAuthMetaData.PersonMetaData.NAME, person.mName);
        values.put(IDAuthMetaData.PersonMetaData.SEX, person.mSex);
        values.put(IDAuthMetaData.PersonMetaData.NATIONALITY, person.mNationality);
        values.put(IDAuthMetaData.PersonMetaData.BIRTHDAY, person.mBirthday!=null ? person.mBirthday.getTime(): null);
        values.put(IDAuthMetaData.PersonMetaData.ADDRESS, person.mAddress);
        values.put(IDAuthMetaData.PersonMetaData.ID, person.mNumber);
        values.put(IDAuthMetaData.PersonMetaData.AUTHOR, person.mAuthority);
        values.put(IDAuthMetaData.PersonMetaData.VALID_FROM, person.mValidFrom!=null ? person.mValidFrom.getTime(): null);
        values.put(IDAuthMetaData.PersonMetaData.VALID_TO, person.mValidTo!=null ? person.mValidTo.getTime(): null);
        values.put(IDAuthMetaData.PersonMetaData.LATEST_ADDRESS, person.mLatestAddress);   
        values.put(IDAuthMetaData.PersonMetaData.PHOTO_IDCARD, Bitmap2Bytes(person.mPhoto_idcard));
        values.put(IDAuthMetaData.PersonMetaData.FINGER_IDCARD_SUPPORT, person.mFinger_idcard_support);
        values.put(IDAuthMetaData.PersonMetaData.FINGER1_IDCARD, person.mFinger1_idcard);
        values.put(IDAuthMetaData.PersonMetaData.FINGER2_IDCARD, person.mFinger2_idcard);
        //additional information from pad
        values.put(IDAuthMetaData.PersonMetaData.PHOTO_URL, person.mPhoto_url);
        values.put(IDAuthMetaData.PersonMetaData.PHOTO, Bitmap2Bytes(person.mPhoto));
        values.put(IDAuthMetaData.PersonMetaData.FINGER1_SCAN, person.mFinger1_scan);
        values.put(IDAuthMetaData.PersonMetaData.FINGER2_SCAN, person.mFinger2_scan);
 
        int count = cr.update(IDAuthMetaData.PersonMetaData.CONTENT_URI, values, selection, null);

        return count !=0 ? true: false;
        
    }
    
    public static boolean deletePersonByID(Context context, String ID){
        ContentResolver cr = context.getContentResolver();
        
        String selection = IDAuthMetaData.PersonMetaData.ID + " = " + ID;
        int count = cr.delete(IDAuthMetaData.PersonMetaData.CONTENT_URI, selection, null);
        return count !=0 ? true: false;
    }
    
    public static Person getPersonByID(Context context, String ID){
        ContentResolver cr = context.getContentResolver();
        Person person = new Person();
        
        String selection = IDAuthMetaData.PersonMetaData.ID + " = " + ID;
        Cursor c = cr.query(IDAuthMetaData.PersonMetaData.CONTENT_URI, null, selection, null, null);
        
        if (c != null){
            if (c.getCount() > 0) {
                c.moveToFirst();
                //information from idcard
                person.mName = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.NAME));
                person.mSex = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.SEX));
                person.mNationality = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.NATIONALITY));
                person.mBirthday = new Date(c.getLong(c.getColumnIndex(IDAuthMetaData.PersonMetaData.BIRTHDAY)));
                person.mAddress = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.ADDRESS));
                person.mNumber = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.ID));
                person.mAuthority = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.AUTHOR));
                person.mValidFrom = new Date(c.getLong(c.getColumnIndex(IDAuthMetaData.PersonMetaData.VALID_FROM)));
                person.mValidTo = new Date(c.getLong(c.getColumnIndex(IDAuthMetaData.PersonMetaData.VALID_TO)));
                person.mLatestAddress = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.LATEST_ADDRESS));
                person.mPhoto_idcard = Bytes2Bimap(c.getBlob(c.getColumnIndex(IDAuthMetaData.PersonMetaData.PHOTO_IDCARD)));
                person.mFinger_idcard_support = c.getInt(c.getColumnIndex(IDAuthMetaData.PersonMetaData.FINGER_IDCARD_SUPPORT))!=0 ? true:false;
                person.mFinger1_idcard = c.getBlob(c.getColumnIndex(IDAuthMetaData.PersonMetaData.FINGER1_IDCARD));
                person.mFinger2_idcard = c.getBlob(c.getColumnIndex(IDAuthMetaData.PersonMetaData.FINGER2_IDCARD));
                
                //additional information from pad
                person.mPhoto_url = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.PHOTO_URL));
                person.mPhoto = Bytes2Bimap(c.getBlob(c.getColumnIndex(IDAuthMetaData.PersonMetaData.PHOTO)));
                person.mFinger1_scan = c.getBlob(c.getColumnIndex(IDAuthMetaData.PersonMetaData.FINGER1_SCAN));
                person.mFinger2_scan = c.getBlob(c.getColumnIndex(IDAuthMetaData.PersonMetaData.FINGER2_SCAN));
                
            }
            c.close();
        }
        return person;
    }
    
    public static Person getFirstPerson(Context context){
        ContentResolver cr = context.getContentResolver();
        Person person = new Person();
        
        Cursor c = cr.query(IDAuthMetaData.PersonMetaData.CONTENT_URI, null, null, null, null);
        
        if (c != null){
            if (c.getCount() > 0) {
                c.moveToFirst();
                String ID = c.getString(c.getColumnIndex(IDAuthMetaData.PersonMetaData.ID));
                person = getPersonByID(context, ID);
            }
            c.close();
        }
        return person;
    }
    
    public static boolean isEmptyPerson(Context context){
        ContentResolver cr = context.getContentResolver();
        
        Cursor c = cr.query(IDAuthMetaData.PersonMetaData.CONTENT_URI, null, null, null, null);
        if (null == c) {
            Log.w(TAG, "Null Cursor pointer!");
            return true;
        }

        if (c.getCount() > 0) {
            c.close();
            return false;
        } else {
            c.close();
            return true;
        }
    }
    
    public static void ClearDB(Context context){
        //TODO
    }
    
    public static byte[] Bitmap2Bytes(Bitmap bm) { 
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
        return baos.toByteArray();  
    } 
    public static Bitmap Bytes2Bimap(byte[] b) { 
        if (b == null) {
            return null;
        }
        if (b.length != 0) {  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        } else {  
            return null;  
        }  
    }  
}