package com.sucic95.droidnotes;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sucic95.droidnotes.database.NoteDatabase;
import com.sucic95.droidnotes.items.NoteItem;

public class NoteActivity extends FragmentActivity {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private NoteDatabase db;
	private int id;
	private String title, content;
	private EditText etTitle, etContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		initViews();

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		id = this.getIntent().getExtras().getInt("id");
		title = this.getIntent().getExtras().getString("title");
		content = this.getIntent().getExtras().getString("content");

		db = new NoteDatabase(NoteActivity.this);

		getActionBar().setSubtitle(title);
	}

	private void initViews() {
		etTitle = (EditText) findViewById(R.id.etTitle);
		etContent = (EditText) findViewById(R.id.etContent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_note, menu);
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
		case R.id.menu_save:
			initViews();
			String newTitle = etTitle.getText().toString();
			String newContent = etContent.getText().toString();
			if (newTitle.isEmpty() || newContent.isEmpty())
				break;
			NoteItem newNote = new NoteItem(id, newTitle, newContent,
					Calendar.getInstance(), getApplicationContext());
			NoteItem oldNote = db.getNoteItem(id);
			if (!newNote.equals(oldNote)) {
				if (title.isEmpty() || content.isEmpty())
					break;
				db.updateNoteItem(newNote);
			}
			this.finish();
			break;
		case R.id.menu_remove:
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					NoteActivity.this);
			dialog.setTitle(String.format("Deleting \"%s\"", title));
			dialog.setMessage("Are you sure you want to delete this note?");
			dialog.setIcon(R.drawable.ic_launcher);
			dialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							db.deleteNoteItem(id);
							Toast.makeText(NoteActivity.this,
									"Note deleted  ( ͡° ͜ʖ ͡°)",
									Toast.LENGTH_LONG).show();
							NoteActivity.this.finish();
						}
					});
			dialog.setNegativeButton("No", null);
			dialog.create();
			dialog.show();
			break;
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				Fragment fViewNote = new ViewNoteFragment();
				Bundle viewNoteArgs = new Bundle();
				viewNoteArgs.putString(ViewNoteFragment.ARG_TITLE, title);
				viewNoteArgs.putString(ViewNoteFragment.ARG_CONTENT, content);
				fViewNote.setArguments(viewNoteArgs);
				return fViewNote;
			case 1:
				Fragment fEditNote = new EditNoteFragment();
				Bundle editNoteArgs = new Bundle();
				editNoteArgs.putString(EditNoteFragment.ARG_TITLE, title);
				editNoteArgs.putString(EditNoteFragment.ARG_CONTENT, content);
				fEditNote.setArguments(editNoteArgs);
				return fEditNote;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "View";
			case 1:
				return "Edit";
			}
			return null;
		}
	}

	public static class ViewNoteFragment extends Fragment {
		public static final String ARG_TITLE = "title";
		public static final String ARG_CONTENT = "content";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			String title = getArguments().getString(ARG_TITLE);
			String content = getArguments().getString(ARG_CONTENT);

			View vViewNote = inflater.inflate(R.layout.note_view, container,
					false);
			((TextView) vViewNote.findViewById(R.id.tvTitle)).setText(title);
			((TextView) vViewNote.findViewById(R.id.tvContent))
					.setText(content);
			return vViewNote;
		}
	}

	public static class EditNoteFragment extends Fragment {
		public static final String ARG_TITLE = "title";
		public static final String ARG_CONTENT = "content";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			String title = getArguments().getString(ARG_TITLE);
			String content = getArguments().getString(ARG_CONTENT);

			View vEditNote = inflater.inflate(R.layout.note_edit, container,
					false);
			((EditText) vEditNote.findViewById(R.id.etTitle)).setText(title);
			((EditText) vEditNote.findViewById(R.id.etContent))
					.setText(content);
			return vEditNote;
		}
	}
}
