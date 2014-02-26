package com.adriangl.casobq;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.adriangl.casobq.adapters.DropboxEntryAdapter;
import com.adriangl.casobq.dropbox.DbxAccountManager;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

public class BrowserActivity extends Activity {
	
	private DbxAccountManager mDbxAcctMgr;
	
	private ListView mListView;

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
				List<DropboxAPI.Entry> rootInfo = mDbxAcctMgr.getApi().search("/", ".epub", 0, false);
				return rootInfo;
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
				DropboxEntryAdapter adapter = new DropboxEntryAdapter(result, getApplicationContext());
				mListView.setAdapter(adapter);
			}
			super.onPostExecute(result);
		}
		
	}
}
