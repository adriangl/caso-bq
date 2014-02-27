package com.adriangl.casobq.comparators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.dropbox.client2.DropboxAPI;

public class FileDateComparator implements Comparator<DropboxAPI.Entry> {
	
	private SimpleDateFormat mDateFormatter = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US);
	
	@Override
	public int compare(DropboxAPI.Entry lhs, DropboxAPI.Entry rhs) {
		try {
			Date lhsDate = mDateFormatter.parse(lhs.clientMtime);
			Date rhsDate = mDateFormatter.parse(rhs.clientMtime);
			
			return lhsDate.compareTo(rhsDate);
		} catch (ParseException e) {
			return 0;
		}
	}

}
