/**
 * SA-MP App - Query and RCON Application
 * 
 * @author 		Rafael 'R@f' Keramidas <rafael@keramid.as>
 * @version		0.2.0 Beta
 * @date		29th June 2012
 * @licence		GPLv3
 * @thanks		StatusRed : Took example of this query class code for the v0.2.0.
 * 				Sasuke78200 : Some help with the first query class (v0.1.x).
 * 				Woothemes.com : In app icons (tabs and menu).
 * 				TheOriginalTwig : App icon.
 */

package com.rafaelk.sampapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

public class DatabaseHandler extends SQLiteOpenHelper {
	
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "serversManager";
    private static final String TABLE_SERVERS = "servers";
    private static final String KEY_ID = "id";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_IP = "ip";
    private static final String KEY_PORT = "port";
    private static final String KEY_RCON = "rcon";
    
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SERVERS_TABLE = "CREATE TABLE " + TABLE_SERVERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ALIAS + " TEXT," + KEY_IP + " TEXT,"
                + KEY_PORT + " TEXT," + KEY_RCON + " TEXT" + ")";
        db.execSQL(CREATE_SERVERS_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
        onCreate(db);
    }
    
    void addServer(String alias, String ip, String port, String rcon) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_ALIAS, alias);
        values.put(KEY_IP, ip);
        values.put(KEY_PORT, port);
        values.put(KEY_RCON, rcon);
 
        db.insert(TABLE_SERVERS, null, values);
        db.close(); 
    }
 
    String[] getServer(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] servers = new String[5];
 
        Cursor cursor = db.query(TABLE_SERVERS, new String[] { KEY_ID,
                KEY_ALIAS, KEY_IP, KEY_PORT, KEY_RCON }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        
        for(int i = 0; i < 5; i++) 
        	servers[i] = cursor.getString(i);
        
        return servers;
    }

    public String[][] getAllServers() {
    	int x = 0;
    	String[][] servers = new String[5][this.getServerCount()];
        String selectQuery = "SELECT  * FROM " + TABLE_SERVERS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
            	for(int i = 0; i < 5; i++)
            		servers[i][x] = cursor.getString(i);
            	x++;
            } while (cursor.moveToNext());
        }
 
        return servers;
    }
 
    public int updateServer(int serverid, String alias, String ip, String port, String rcon) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_ALIAS, alias);
        values.put(KEY_IP, ip);
        values.put(KEY_PORT, port);
        values.put(KEY_RCON, rcon);
 
        return db.update(TABLE_SERVERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(serverid) });
    }
 
    public void deleteServer(int serverid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SERVERS, KEY_ID + " = ?",
                new String[] { String.valueOf(serverid) });
        db.close();
    }
 
    public int getServerCount() {
    	String selectQuery = "SELECT  * FROM " + TABLE_SERVERS;
    	 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
            	i++;
            } while (cursor.moveToNext());
        }
 
        return i;
    }
}