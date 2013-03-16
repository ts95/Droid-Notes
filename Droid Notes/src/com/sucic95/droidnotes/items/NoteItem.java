package com.sucic95.droidnotes.items;

import java.util.Calendar;
import java.util.Locale;

import android.content.Context;

public class NoteItem {

	private int id;
	private String title;
	private String content;
	private Calendar lastTimeEdited;
	private Context context;

	public NoteItem(int id, String title, String content,
			Calendar lastTimeEdited, Context con) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.lastTimeEdited = lastTimeEdited;
		this.context = con;
	}

	public NoteItem(String title, String content, Calendar lastTimeEdited,
			Context con) {
		this(-1, title, content, lastTimeEdited, con);
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getShortContent() {
		if (content.length() > 100) {
			return content.substring(0, 100);
		} else {
			return content;
		}
	}

	public Calendar getLastTimeEdited() {
		return lastTimeEdited;
	}

	public String getClock() {
		if (android.text.format.DateFormat.is24HourFormat(context)){
			int hourOfDay = lastTimeEdited.get(Calendar.HOUR_OF_DAY);
			int minute = lastTimeEdited.get(Calendar.MINUTE);
			return String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
		} else {
			int hour = lastTimeEdited.get(Calendar.HOUR);
			int minute = lastTimeEdited.get(Calendar.MINUTE);
			String amOrPm = lastTimeEdited.get(Calendar.AM_PM) == Calendar.PM ? "PM" : "AM";
			return String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amOrPm);
		}
	}

	public String getDate() {
		java.text.DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(context);
		return dateFormat.format(lastTimeEdited.getTime());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NoteItem) {
			NoteItem otherNoteItem = (NoteItem) o;
			if (title.equals(otherNoteItem.getTitle())
					&& content.equals(otherNoteItem.getContent())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
