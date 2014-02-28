package com.adriangl.casobq;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.adriangl.casobq.adapters.DropboxEntryAdapter;
import com.adriangl.casobq.comparators.FileDateComparator;
import com.adriangl.casobq.comparators.FileNameComparator;
import com.adriangl.casobq.dropbox.DbxAccountManager;
import com.adriangl.casobq.views.DoubleClickListView;
import com.adriangl.casobq.views.DoubleClickListView.OnItemDoubleClickListener;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

public class BrowserActivity extends Activity implements OnItemDoubleClickListener {
	
	private DbxAccountManager mDbxAcctMgr;
	
	private DoubleClickListView mListView;
	private DropboxEntryAdapter mAdapter;

	private List<Entry> mListItemInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		mListView = (DoubleClickListView)findViewById(R.id.listView1);
		mListView.setOnItemDoubleClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mDbxAcctMgr = new DbxAccountManager(this);
		if (!mDbxAcctMgr.isLoggedIn()){
			this.finish();
		}
		else{
			showData();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browser, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.action_order_by_date:
			orderListByDate();
			return true;
		case R.id.action_order_by_file_name:
			orderListByFileName();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onItemDoubleClick(AdapterView<?> parent, View view, int position,
            long id) {
		new DownloadEpubCoverAsyncTask().execute(new DropboxAPI.Entry[]{
				(DropboxAPI.Entry) mListView.getItemAtPosition(position)});	
	}

	private void orderListByFileName() {
		Collections.sort(mListItemInfo, new FileNameComparator());
		mListView.invalidateViews();
	}

	private void orderListByDate() {
		Collections.sort(mListItemInfo, new FileDateComparator());
		mListView.invalidateViews();
	}

	private void showData() {
		new DownloadEpubDataAsyncTask().execute();
	}
	
	
	protected class DownloadEpubDataAsyncTask extends AsyncTask<Void, Void, List<DropboxAPI.Entry>>{
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			if (pd != null){
				pd.dismiss();
			}
			pd = ProgressDialog.show(BrowserActivity.this, getResources().getString(R.string.wait), 
					getResources().getString(R.string.downloading_epub_data));
		}
		
		@Override
		protected List<Entry> doInBackground(Void... args) {
			try{
				mListItemInfo = mDbxAcctMgr.getApi().search("/", ".epub", 0, false);
				return mListItemInfo;
			} catch (DropboxException e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<Entry> result) {
			if (pd != null){
				pd.dismiss();
			}
			if (result != null){
				mAdapter = new DropboxEntryAdapter(result, getApplicationContext());
				mListView.setAdapter(mAdapter);
			}
			super.onPostExecute(result);
		}
		
	}
	
	protected class DownloadEpubCoverAsyncTask extends AsyncTask<DropboxAPI.Entry, Void, Bitmap>{
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			if (pd != null){
				pd.dismiss();
			}
			pd = ProgressDialog.show(BrowserActivity.this, getResources().getString(R.string.wait), 
					getResources().getString(R.string.downloading_epub_data));
		}
		
		@Override
		protected Bitmap doInBackground(DropboxAPI.Entry... args) {
			try {
				DropboxAPI.Entry entry = args[0];
				DropboxInputStream dis = mDbxAcctMgr.getApi().getFileStream(entry.path, entry.rev);
				
				// Read ePub				
				EpubReader reader = new EpubReader();
				Book book = reader.readEpub(dis);
				
				// Read cover				
				byte[] coverBytes = book.getCoverImage().getData();
		        Bitmap bm = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
		        
		        //TODO save image cache
		        return bm;
		        
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if (pd != null){
				pd.dismiss();
			}
			if (result != null){
				Dialog coverDialog = new Dialog(BrowserActivity.this);
				coverDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				coverDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_cover, null));
				ImageView imgView = (ImageView) coverDialog.findViewById(R.id.cover_image);
				imgView.setImageBitmap(result);
				coverDialog.setCanceledOnTouchOutside(true);
				coverDialog.show();
			}
			else{
				super.onPostExecute(result);
			}
		}
		
	}

}
