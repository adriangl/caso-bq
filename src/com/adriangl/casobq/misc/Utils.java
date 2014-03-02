package com.adriangl.casobq.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	
	public static Date parseDropboxFileDate (String date) throws ParseException{
		SimpleDateFormat mDateFormatter = 
				new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US);
		return mDateFormatter.parse(date);
	}
	
	public static boolean hasInternetConnection(Context ctx) {
		Context c = ctx.getApplicationContext();
	    ConnectivityManager cm =
	        (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
}
