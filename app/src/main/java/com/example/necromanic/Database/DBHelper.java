package com.example.necromanic.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.necromanic.Interfaces.OnDatabaseChangedListener;
import com.example.necromanic.Models.RecordingItem;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "saved_recording.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "savedrecording";
    public static final String  COLUMN_NAME = "NAME";
    public static final String COLUMN_PATH = "PATH";
    public static final String  COLUMN_LENGTH= "LENGTH";
    public static final String  COLUMN_TIME_ADDED = "TIMEADDED";
    public static final String COLUMN_ID = "ID";
    public static final String COMA_SEP = ",";
    private  static OnDatabaseChangedListener onDatabaseChangedListener;
    public static final String SQLITE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +"(ID INTEGER primary key AUTOINCREMENT"  + COMA_SEP +
            COLUMN_NAME + " TEXT"+ COMA_SEP +
            COLUMN_PATH + " TEXT" + COMA_SEP +
            COLUMN_LENGTH + " INTEGER"+ COMA_SEP +
            COLUMN_TIME_ADDED + " INTEGER)";
  //  public static final String SQLITE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +"(ID INTEGER primary key AUTOINCREMENT,PATH TEXT, LENGTH INTEGER,TIMEADDED INTEGER)" ;




    public DBHelper (Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLITE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean addRecording (RecordingItem recordingItem){
        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME,recordingItem.getName());
            contentValues.put(COLUMN_PATH,recordingItem.getPath());
            contentValues.put(COLUMN_LENGTH,recordingItem.getLength());
            contentValues.put(COLUMN_TIME_ADDED,recordingItem.getTime_added());

            Log.d("keeping in database :     ", "addRecording: is working !!!!!!!!!!!!");
              db.insert(TABLE_NAME,null, contentValues);
             if (onDatabaseChangedListener != null){
                 onDatabaseChangedListener.onDatabaseEntryAdded(recordingItem);

             }
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void deleteEntry(int row) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(row)});
        sqLiteDatabase.delete(TABLE_NAME,   "ID=" + row, null);
        sqLiteDatabase.close();
    }

    public ArrayList<RecordingItem> getAllAudios(){

        ArrayList<RecordingItem> arrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        if(cursor != null){
            while(cursor.moveToNext()){
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                int length = (int)cursor.getLong(3);
                long timeAdded = cursor.getLong(4);

                RecordingItem recordingItem = new RecordingItem(name, path, length, timeAdded);
                recordingItem.setID(id);
                arrayList.add(recordingItem);

            }
            cursor.close();
            return arrayList;
        }
        else{
            return null;
        }

    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        DBHelper.onDatabaseChangedListener = listener;
    }
}
