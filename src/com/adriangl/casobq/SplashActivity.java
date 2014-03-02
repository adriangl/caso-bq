package com.adriangl.casobq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.adriangl.casobq.dropbox.DbxAccountManager;
import com.adriangl.casobq.misc.Utils;

public class SplashActivity extends Activity implements OnClickListener {
	
	private DbxAccountManager mDBXMan;
	private Button mButtonLinkDbx;
	public ProgressDialog mPd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// Setup Views
		mButtonLinkDbx = (Button) findViewById(R.id.link_to_dropbox);
		mButtonLinkDbx.setOnClickListener(this);

		// Init Dropbox Account Manager
		mDBXMan = new DbxAccountManager(this);
		new CheckLoginAsyncTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Utils.hasInternetConnection(this.getApplicationContext())){
			if (mDBXMan.completeAuthentication()){
				launchBrowser();
			}
		}
		else{
			Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
			this.finish();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mPd != null){
			mPd.dismiss();
			mPd = null;
		}
	}

	private void launchBrowser() {
		Intent i = new Intent(this, BrowserActivity.class);
		startActivity(i);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.link_to_dropbox:
			goToDropbox();
			break;
		default:
			break;
		}
	}

	private void goToDropbox() {
		mDBXMan.startAuthentication();
	}
	
	private class CheckLoginAsyncTask extends AsyncTask<Void, Void, Boolean>{
		
		@Override
		protected void onPreExecute() {
			if (mPd != null){
				mPd.dismiss();
			}
			Resources res = getResources();
			mPd = ProgressDialog.show(SplashActivity.this, res.getString(R.string.wait),
					res.getString(R.string.check_credentials));
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			return mDBXMan.isLoggedIn();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (mPd != null){
				mPd.dismiss();
				mPd = null;
			}
			if (result){
				launchBrowser();
			}
		}
		
	}

}
