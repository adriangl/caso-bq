package com.adriangl.casobq.classes;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.adriangl.casobq.misc.Utils;

public class EpubEntry implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3451031244335351497L;
	
	private String mFileName;
	private String mRemotePath;
	private String mBookName;
	private Date mDate;
	private String mCover;
	
	public EpubEntry(String fileName, String bookName, Date date) {
		this.mFileName = fileName;
		this.mBookName = bookName;
		this.mDate = date;
	}
	
	public EpubEntry(String fileName, String date, String remotePath) {
		try {
			this.mFileName = fileName;
			this.mDate = Utils.parseDropboxFileDate(date);
			this.mRemotePath = remotePath;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getFileName() {
		return mFileName;
	}
	public void setFileName(String fileName) {
		this.mFileName = fileName;
	}
	public String getRemotePath() {
		return mRemotePath;
	}

	public void setRemotePath(String remotePath) {
		this.mRemotePath = remotePath;
	}

	public String getBookName() {
		return mBookName;
	}
	public void setBookName(String bookName) {
		this.mBookName = bookName;
	}
	
	public String getCover() {
		return mCover;
	}
	public void setCover(String cover) {
		this.mCover = cover;
	}
	public Date getDate() {
		return mDate;
	}
	public void setDate(Date date) {
		this.mDate = date;
	}
	
}
