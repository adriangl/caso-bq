package com.adriangl.casobq.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adriangl.casobq.R;
import com.dropbox.client2.DropboxAPI;

public class DropboxEntryAdapter extends BaseAdapter {
	
	private List<DropboxAPI.Entry> mItems;
	private Context mContext;
	
	// We apply here the ViewHolder pattern. It consists on keeping a reference
	// to the item's layout elements in order to avoid constant use of 
	// findViewById each time the view is loaded.
	
	static class ViewHolder{
		ImageView mFileIconView;
		TextView mFileNameView;
		TextView mFileDateView;
	}
	
	public DropboxEntryAdapter(List<DropboxAPI.Entry> items, Context ctx) {
		mItems = items;
		mContext = ctx.getApplicationContext();
	}

	@Override
	public int getCount() {
		if (mItems == null) {
			return -1;
		}
		else{
			return mItems.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.row_browser, null);
			ImageView fileIconView = (ImageView)convertView.findViewById(R.id.file_icon);
			TextView fileNameView = (TextView)convertView.findViewById(R.id.file_name);
			TextView fileDateView = (TextView)convertView.findViewById(R.id.file_date);
			
			ViewHolder vh = new ViewHolder();
			vh.mFileIconView = fileIconView;
			vh.mFileNameView = fileNameView;
			vh.mFileDateView = fileDateView;
			
			convertView.setTag(vh);
		}
		
		ViewHolder vh = (ViewHolder) convertView.getTag();
		DropboxAPI.Entry fileEntry = mItems.get(position);
		
		if (fileEntry != null){
			vh.mFileNameView.setText(fileEntry.fileName());
			vh.mFileDateView.setText(fileEntry.modified);
			vh.mFileIconView.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.ic_launcher));
		}
		
		return convertView;
	}

}
