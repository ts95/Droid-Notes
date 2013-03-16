package com.sucic95.droidnotes.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sucic95.droidnotes.items.NoteItem;

public class NoteDatabase extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "NoteDatabase";
	private static final String TABLE_NOTE = "NoteTable";
	
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CONTENT = "content";
	private static final String KEY_DATE_AND_TIME = "dateandtime";
	
	// Column Index 0 = id
	// Column Index 1 = title
	// Column Index 2 = note
	// Column Index 3 = time (last time edited)
	
	private SQLiteDatabase db;
	private Cursor cursor;
	private Context context;
	
	public NoteDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		final String CREATE_TABLE = String.format(
				Locale.UK,
				"CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT);",
				TABLE_NOTE,
				KEY_ID,
				KEY_TITLE,
				KEY_CONTENT,
				KEY_DATE_AND_TIME
		);
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
		onCreate(db);
	}
	
	@Override
	public synchronized void close() {
		if (db != null)
            db.close();
        if (cursor != null)
            cursor.close();
		super.close();
	}
	
	public void addNoteItem(NoteItem note) {
		db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, note.getTitle());
		values.put(KEY_CONTENT, note.getContent());
		values.put(KEY_DATE_AND_TIME, calendarToTime(note.getLastTimeEdited()));
		
		db.insert(TABLE_NOTE, null, values);
	}
	
	public NoteItem getNoteItem(int id) {
		db = this.getReadableDatabase();
		cursor = db.query(
			TABLE_NOTE,
			new String[] { KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE_AND_TIME },
			KEY_ID + " = ?",
			new String[] { String.valueOf(id) },
			null,
			null,
			null,
			null
		);
		
		if (cursor != null)
			cursor.moveToFirst();
		
		String title = cursor.getString(1);
		String note = cursor.getString(2);
		String time = cursor.getString(3);
		
		return new NoteItem(id, title, note, timeToCalendar(time), context);
	}
	
	public List<NoteItem> getNoteItems() {
		List<NoteItem> noteItems = new ArrayList<NoteItem>();
		String selectQuery = "SELECT * FROM " + TABLE_NOTE;
		
		db = this.getReadableDatabase();
		cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(0);
				String title = cursor.getString(1);
				String note = cursor.getString(2);
				String time = cursor.getString(3);
				Calendar calendar = timeToCalendar(time);
				noteItems.add(new NoteItem(id, title, note, calendar, context));
			} while (cursor.moveToNext());
		}
		return noteItems;
	}
	
	public int getNoteItemCount() {
		String countQuery = "SELECT * FROM " + TABLE_NOTE;
		db = this.getReadableDatabase();
		cursor = db.rawQuery(countQuery, null);
		return cursor.getCount();
	}
	
	public void updateNoteItem(NoteItem note) {
		db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, note.getTitle());
		values.put(KEY_CONTENT, note.getContent());
		values.put(KEY_DATE_AND_TIME, calendarToTime(note.getLastTimeEdited()));
		
		db.update(
			TABLE_NOTE,
			values,
			KEY_ID + " = ?",
			new String[] { String.valueOf(note.getId()) }
		);
	}
	
	public void deleteNoteItem(int id) {
		db = this.getWritableDatabase();
		db.delete(
			TABLE_NOTE,
			KEY_ID + " = ?",
			new String[] { String.valueOf(id) }
		);
	}
	
	private Calendar timeToCalendar(String time) {
		String timeArgs[] = time.split(",");
		int hour = Integer.valueOf(timeArgs[0]);
		int minute = Integer.valueOf(timeArgs[1]);
		int day = Integer.valueOf(timeArgs[2]);
		int month = Integer.valueOf(timeArgs[3]);
		int year = Integer.valueOf(timeArgs[4]);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		return calendar;
	}

	private String calendarToTime(Calendar calendar) {
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		String time = String.format(
				Locale.UK,
				"%d,%d,%d,%d,%d",
				hour,
				minute,
				day,
				month,
				year
		);
		return time;
	}
}
