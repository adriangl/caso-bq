package com.adriangl.casobq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.adriangl.casobq.adapters.DropboxEntryAdapter;
import com.adriangl.casobq.classes.EpubEntry;
import com.adriangl.casobq.comparators.FileBookNameComparator;
import com.adriangl.casobq.comparators.FileDateComparator;
import com.adriangl.casobq.dropbox.DbxAccountManager;
import com.adriangl.casobq.misc.Utils;
import com.adriangl.casobq.views.DoubleClickGridView;
import com.adriangl.casobq.views.DoubleClickGridView.OnItemDoubleClickListener;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

public class BrowserActivity extends Activity implements OnItemDoubleClickListener {
	
	private DbxAccountManager mDbxAcctMgr;
	
	private DoubleClickGridView mGridView;
	private DropboxEntryAdapter mAdapter;

	private ArrayList<EpubEntry> mListItemInfo;
	
	private boolean mDataDownloaded = false;

	private boolean mEpubDownloadTaskRunning;
	
	ProgressDialog mPd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		mGridView = (DoubleClickGridView)findViewById(R.id.gridView1);
		mGridView.setOnItemDoubleClickListener(this);
		
		if (savedInstanceState != null){
			mDataDownloaded = savedInstanceState.getBoolean("mDataDownloaded", false);
			if (mDataDownloaded){
				mListItemInfo = (ArrayList<EpubEntry>)savedInstanceState.getSerializable("mListItemInfo");
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (Utils.hasInternetConnection(this.getApplicationContext())){
			mDbxAcctMgr = new DbxAccountManager(this);
			showData();
		}
		else{
			Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
			this.finish();
		}
	}
	
	private void populateView(){
		mAdapter = new DropboxEntryAdapter(mListItemInfo, getApplicationContext());
		mGridView.setAdapter(mAdapter);
		mDataDownloaded = true;
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
			orderListByBookName();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("mDataDownloaded", mDataDownloaded);
		outState.putSerializable("mListItemInfo", mListItemInfo);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mPd != null){
			mPd.dismiss();
		}
	}
	
	@Override
	public void onItemDoubleClick(AdapterView<?> parent, View view, int position,
            long id) {
		new DownloadEpubCoverAsyncTask().execute(new EpubEntry[]{
				(EpubEntry) mGridView.getItemAtPosition(position)});	
	}

	private void orderListByBookName() {
		Collections.sort(mListItemInfo, new FileBookNameComparator());
		mGridView.invalidateViews();
	}

	private void orderListByDate() {
		Collections.sort(mListItemInfo, new FileDateComparator());
		mGridView.invalidateViews();
	}

	private void showData() {
		if (!mDataDownloaded && !mEpubDownloadTaskRunning){
			new DownloadEpubDataAsyncTask().execute();
		}
		else{
			populateView();
		}
	}
	
	
	protected class DownloadEpubDataAsyncTask extends AsyncTask<Void, Void, List<EpubEntry>>{
		
		@Override
		protected void onPreExecute() {
			if (mPd != null){
				mPd.dismiss();
			}
			mPd = ProgressDialog.show(BrowserActivity.this, getResources().getString(R.string.wait), 
					getResources().getString(R.string.downloading_epub_data));
			
			mEpubDownloadTaskRunning = true;
		}
		
		@Override
		protected List<EpubEntry> doInBackground(Void... args) {
			try{
				// Recover epub info from Dropbox
				List<Entry> listItemInfo = mDbxAcctMgr.getApi().search("/", ".epub", 0, false);
				mListItemInfo = new ArrayList<EpubEntry>();
				// Iterate and retrieve epub book name for sorting
				for (Entry e : listItemInfo){
					// Create epub entry
					EpubEntry entry = new EpubEntry(e.fileName(), e.clientMtime, e.path);
					// Get book title
					DropboxInputStream dis = mDbxAcctMgr.getApi().getFileStream(e.path, e.rev);
					// Read ePub				
					EpubReader reader = new EpubReader();
					Book book = reader.readEpub(dis);
					// Save title
					entry.setBookName(book.getTitle());
					
					// Close input stream
			        dis.close();
			        
			        mListItemInfo.add(entry);
				}
				return mListItemInfo;
			} catch (DropboxException e) {
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<EpubEntry> result) {
			if (mPd != null){
				mPd.dismiss();
			}
			if (result != null){
				populateView();
			}
			super.onPostExecute(result);
		}
		
	}
	
	protected class DownloadEpubCoverAsyncTask extends AsyncTask<EpubEntry, Void, String>{
		
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
		protected String doInBackground(EpubEntry... args) {
			try {
				EpubEntry entry = args[0];
				
				if (entry.getCover() == null){
					// Search cache in case we have the stored cover
					File dataFolder = Environment.getExternalStorageDirectory();
			        File imageFile = new File(dataFolder, entry.getFileName()+".png");
			        
			        if (!imageFile.exists()){
			        	DropboxInputStream dis = mDbxAcctMgr.getApi().getFileStream(
								entry.getRemotePath(), null);
						
						// Read ePub				
						EpubReader reader = new EpubReader();
						Book book = reader.readEpub(dis);
						
						// Read cover				
						byte[] coverBytes = book.getCoverImage().getData();
				        Bitmap bm = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
				        
				        // Close input stream
				        dis.close();
				        
				        //Save image cache
				        FileOutputStream fos = new FileOutputStream(imageFile);
				        bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
				        
				        fos.flush();
				        fos.close();
				        bm.recycle();
			        }				
			        
			        entry.setCover(imageFile.getAbsolutePath());
			        
			        return imageFile.getAbsolutePath();
				}
				else{
					return entry.getCover();
				}
		        
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
		protected void onPostExecute(String result) {
			if (pd != null){
				pd.dismiss();
			}
			if (result != null){
				Dialog coverDialog = new Dialog(BrowserActivity.this);
				coverDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				coverDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_cover, null));
				ImageView imgView = (ImageView) coverDialog.findViewById(R.id.cover_image);
				imgView.setImageURI(Uri.parse(result));
				coverDialog.setCanceledOnTouchOutside(true);
				coverDialog.show();
			}
			else{
				super.onPostExecute(result);
			}
			
			mEpubDownloadTaskRunning = false;
		}
		
	}

}
