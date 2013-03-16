package com.sucic95.droidnotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sucic95.droidnotes.adapters.NoteItemAdapter;
import com.sucic95.droidnotes.database.NoteDatabase;
import com.sucic95.droidnotes.items.NoteItem;

public class NotelistActivity extends Activity {
	
	private ListView lvNotes;
	private TextView tvEmpty;
	private NoteItemAdapter adapter;
	private NoteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes_list);
		db = new NoteDatabase(NotelistActivity.this);
		adapter = new NoteItemAdapter(NotelistActivity.this, db.getNoteItems());
		initViews();
	}

	@Override
	protected void onResume() {
		refreshListView(); 
		super.onResume();
	}

	@Override
	protected void onStop() {
		db.close();
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_notes_list, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Intent iNewNoteActivity = new Intent(NotelistActivity.this,
					NewNoteActivity.class);
			startActivity(iNewNoteActivity);
			break;
		case R.id.menu_settings:
			Intent iSettingsActivity = new Intent(NotelistActivity.this,
					SettingsActivity.class);
			startActivity(iSettingsActivity);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void refreshListView() {
		adapter = new NoteItemAdapter(NotelistActivity.this, db.getNoteItems());
		lvNotes.setAdapter(adapter);
	}
	
	private void initViews() {
		tvEmpty = (TextView) findViewById(R.id.tvEmpty);
		
		lvNotes = (ListView) findViewById(R.id.lvNotes);
		lvNotes.setEmptyView(tvEmpty);
		lvNotes.setAdapter(adapter);
		lvNotes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				NoteItem note = (NoteItem) arg0.getAdapter().getItem(index);
				Intent iNoteActivity = new Intent(NotelistActivity.this,
						NoteActivity.class);
				iNoteActivity.putExtra("id", note.getId());
				iNoteActivity.putExtra("title", note.getTitle());
				iNoteActivity.putExtra("content", note.getContent());
				startActivity(iNoteActivity);
			}
		});
		lvNotes.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final NoteItem note = adapter.getItem(arg2);
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						NotelistActivity.this);
				dialog.setTitle(note.getTitle());
				dialog.setIcon(R.drawable.ic_launcher);
				String noteName = "\"" + note.getTitle() + "\"";
				String[] items = new String[] {
					"Delete " + noteName
				};				
				dialog.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							AlertDialog.Builder d = new AlertDialog.Builder(NotelistActivity.this);
							d.setTitle(String.format("Deleting \"%s\"", note.getTitle()));
							d.setMessage("Are you sure you want to delete this note?");
							d.setIcon(R.drawable.ic_launcher);
							d.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									db.deleteNoteItem(note.getId());
									Toast.makeText(NotelistActivity.this, "Note deleted  ( ͡° ͜ʖ ͡°)",
											Toast.LENGTH_LONG).show();
									refreshListView();
								}
							});
							d.setNegativeButton("No", null);
							d.create();
							d.show();
							break;
						}
					}
				});
				dialog.create();
				dialog.show();
				return false;
			}
		});
	}
}
