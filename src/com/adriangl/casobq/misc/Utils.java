package com.adriangl.casobq.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
	
	public static Date parseDropboxFileDate (String date) throws ParseException{
		SimpleDateFormat mDateFormatter = 
				new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US);
		return mDateFormatter.parse(date);
	}
	
}
