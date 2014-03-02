package com.adriangl.casobq.comparators;

import java.util.Comparator;

import com.adriangl.casobq.classes.EpubEntry;

public class FileBookNameComparator implements Comparator<EpubEntry> {
	
	@Override
	public int compare(EpubEntry lhs, EpubEntry rhs) {
		return lhs.getBookName().compareToIgnoreCase(rhs.getBookName());
	}

}
