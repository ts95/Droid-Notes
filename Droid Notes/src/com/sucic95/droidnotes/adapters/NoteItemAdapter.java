package com.sucic95.droidnotes.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sucic95.droidnotes.R;
import com.sucic95.droidnotes.items.NoteItem;

public class NoteItemAdapter extends ArrayAdapter<NoteItem> {
	
	private Context context;
	private List<NoteItem> noteItems;
	
	public NoteItemAdapter(Context context, List<NoteItem> noteItems) {
		super(context, R.layout.listview_noteitem, noteItems);
		
		this.context = context;
		this.noteItems = noteItems;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NoteItemHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.listview_noteitem, parent, false);
			
			holder = new NoteItemHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tvListTitle);
			holder.content = (TextView) convertView.findViewById(R.id.tvListConent);
			holder.clock = (TextView) convertView.findViewById(R.id.tvClock);
			holder.date = (TextView) convertView.findViewById(R.id.tvDate);
			
			convertView.setTag(holder);
		} else {
			holder = (NoteItemHolder) convertView.getTag();
		}
		
		NoteItem note = noteItems.get(position);
		
		holder.title.setText(note.getTitle());
		holder.content.setText(note.getShortContent());
		holder.clock.setText(note.getClock());
		holder.date.setText(note.getDate());
		
		return convertView;
	}
	
	private class NoteItemHolder {
		public TextView title;
		public TextView content;
		public TextView clock;
		public TextView date;
	}
}
