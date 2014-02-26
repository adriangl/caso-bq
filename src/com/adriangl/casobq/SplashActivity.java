package com.adriangl.casobq;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.adriangl.casobq.dropbox.DbxAccountManager;

public class SplashActivity extends Activity implements OnClickListener {
	
	private DbxAccountManager mDBXMan;
	private Button mButtonLinkDbx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// Setup Views
		mButtonLinkDbx = (Button) findViewById(R.id.link_to_dropbox);
		mButtonLinkDbx.setOnClickListener(this);

		// Init Dropbox Account Manager
		mDBXMan = new DbxAccountManager(this);
		if (mDBXMan.isLoggedIn()){
			launchBrowser();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDBXMan.completeAuthentication()){
			launchBrowser();
		}
	}

	private void launchBrowser() {
		Toast.makeText(this, "LINK OK", Toast.LENGTH_LONG).show();		
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

}
