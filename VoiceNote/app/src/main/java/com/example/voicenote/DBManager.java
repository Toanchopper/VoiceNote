package com.example.voicenote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DBManager extends SQLiteOpenHelper {
    static String DATABASE="DATA";
    static String CONTENT="CONTENT";
    static String TITLE="TITLE";
    static String ID="ID";
    public DBManager(@Nullable Context context) {
        super(context, DATABASE, null, 1);
    }
    String sql="CREATE TABLE "+DATABASE+" ("
            +ID+" INTEGER, "
            +TITLE+" TEXT, "
            +CONTENT+" TEXT)";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void add(Note note)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues content=new ContentValues();
        content.put(ID,note.getId());
        content.put(TITLE,note.getTitle());
        content.put(CONTENT,note.getContent());

        db.insert(DATABASE,null,content);
        db.close();
    }

    public void update(Note note)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues content=new ContentValues();
        content.put(TITLE,note.getTitle());
        content.put(CONTENT,note.getContent());

        db.update(DATABASE,content,ID+"="+note.getId(),null);
        db.close();
    }

    public void delete(Note note)
    {
        SQLiteDatabase db=getWritableDatabase();
        String del="delete from "+DATABASE+" where ID="+note.getId();
        db.execSQL(del);
        db.close();
    }

    public List<Note> getnote()
    {
        SQLiteDatabase db=getWritableDatabase();
        String get="select * from "+DATABASE;
        Cursor cursor=db.rawQuery(get,null);
        List<Note> list=new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do{
                Note note=new Note(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
                list.add(note);
            }while(cursor.moveToNext());

        }
        db.close();
        return list;
    }
}
