package com.adriangl.casobq.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.adriangl.casobq.misc.Constants;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class DbxAccountManager {
	
	private static DropboxAPI<AndroidAuthSession> mDBApi;
	private Context mContext;
	
	public DbxAccountManager(Context ctx){
		mContext = ctx.getApplicationContext();
		if (mDBApi == null){
			setupSession();
		}
	}

	private void setupSession() {
		// Set-up Dropbox initialization
		AppKeyPair appKeys = new AppKeyPair(Constants.APP_KEY,
						Constants.APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys);
		String storedAccessToken = getStoredSession();
		if (storedAccessToken != null){
			session.setOAuth2AccessToken(storedAccessToken);
		}
		mDBApi= new DropboxAPI<AndroidAuthSession>(session);
	}
	
	public void startAuthentication(){
		mDBApi.getSession().startOAuth2Authentication(mContext);
	}
	
	public boolean completeAuthentication(){
		if (mDBApi.getSession().authenticationSuccessful()) {
			try {
				// Complete auth
				mDBApi.getSession().finishAuthentication();
				String accessToken = mDBApi.getSession().getOAuth2AccessToken();
				storeAccessToken(accessToken);
				return true;
	            
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	private void storeAccessToken(String accessToken) {
		// We need to store the access token somewhere, we'll use
		// SharedPreferences
		SharedPreferences prefs = mContext.getSharedPreferences(
				Constants.DROPBOX_ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(Constants.DROPBOX_ACCESS_KEY_NAME, "oauth2:");
        edit.putString(Constants.DROPBOX_ACCESS_SECRET_NAME, accessToken);
        edit.commit();
	}
	
	private String getStoredSession(){
		// We'll assume that we'll use OAuth2 authentication, so no need to
		// save a reference to identify that we're actually using OAuth2
		SharedPreferences prefs = mContext.getSharedPreferences(
				Constants.DROPBOX_ACCOUNT_PREFS_NAME, 0);
        String secret = prefs.getString(Constants.DROPBOX_ACCESS_SECRET_NAME, null);
        if (secret == null || secret.length() == 0){
        	return null;
        }
        else {
        	return secret;
        }
	}
	
	public boolean isLoggedIn(){
		return mDBApi.getSession().isLinked();
	}

	public DropboxAPI<AndroidAuthSession> getApi() {
		return mDBApi;
	}
	
}
