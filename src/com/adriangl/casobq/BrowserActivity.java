package com.adriangl.casobq;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.adriangl.casobq.adapters.DropboxEntryAdapter;
import com.adriangl.casobq.comparators.FileDateComparator;
import com.adriangl.casobq.comparators.FileNameComparator;
import com.adriangl.casobq.dropbox.DbxAccountManager;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

public class BrowserActivity extends Activity {
	
	private DbxAccountManager mDbxAcctMgr;
	
	private ListView mListView;
	private DropboxEntryAdapter mAdapter;

	private List<Entry> mListItemInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		mListView = (ListView)findViewById(R.id.listView1);
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
}
