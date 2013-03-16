package com.sucic95.droidnotes;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.sucic95.droidnotes.database.NoteDatabase;
import com.sucic95.droidnotes.items.NoteItem;

public class NewNoteActivity extends Activity {

	private EditText etTitle;
	private EditText etContent;
	private NoteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_note);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setSubtitle("New note");
		initViews();

		db = new NoteDatabase(NewNoteActivity.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_new_note, menu);
		return true;
	}

	@Override
	protected void onStop() {
		db.close();
		super.onStop();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new_note_save:
			String title = etTitle.getText().toString();
			String content = etContent.getText().toString();
			if (title.isEmpty() || content.isEmpty())
				break;
			db.addNoteItem(new NoteItem(title, content, Calendar.getInstance(),
					getApplicationContext()));
			this.finish();
			break;
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void initViews() {
		etTitle = (EditText) findViewById(R.id.etNewNoteTitle);
		etContent = (EditText) findViewById(R.id.etNewNoteContent);
	}
}
