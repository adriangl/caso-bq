package com.adriangl.casobq.comparators;

import java.util.Comparator;

import com.dropbox.client2.DropboxAPI;

public class FileNameComparator implements Comparator<DropboxAPI.Entry> {

	@Override
	public int compare(DropboxAPI.Entry lhs, DropboxAPI.Entry rhs) {
		return lhs.fileName().compareToIgnoreCase(rhs.fileName());
	}

}
