package com.adriangl.casobq.views;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.GridView;

public class DoubleClickGridView extends GridView {
	
	public interface OnItemDoubleClickListener {
		public void onItemDoubleClick(AdapterView<?> parent, View view, int position,
	            long id);
	}
	
	private OnItemDoubleClickListener mOnItemDoubleClickListener;
	private boolean mItemClicked = false;
	private int mItemClickedPosition = -1;
	
	private TimerTask mClickTask;
	private Timer mTimer = new Timer();
	
	private android.widget.AdapterView.OnItemClickListener mOnItemClickListener;
	
	private static final int DOUBLE_CLICK_DELAY = ViewConfiguration.getDoubleTapTimeout();
		
	public DoubleClickGridView(Context context) {
		super(context);
	}
	
	public DoubleClickGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DoubleClickGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
    public boolean performItemClick(View view, int position, long id) {
		return super.performItemClick(view, position, id);
    }
	
	public void setOnItemDoubleClickListener(OnItemDoubleClickListener l){
		mOnItemDoubleClickListener = l;
		
		super.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final AdapterView<?> mParent = parent;
				final View mView = view;
				final int mPosition = position;
				final long mId = id;
				
				// Check if item has been already clicked
				if (!mItemClicked){
					// If it isn't, set as clicked & setup delayed task
					mItemClicked = true;
					mItemClickedPosition = mPosition;
					
					mClickTask = new TimerTask(){
						@Override
						public void run() {
							mItemClicked = false;
							mItemClickedPosition = -1;
							
							if (mOnItemClickListener != null){
								mOnItemClickListener.onItemClick(mParent, mView, mPosition, mId);
							}
						}						
					};
					
					mTimer.schedule(mClickTask, DOUBLE_CLICK_DELAY);
				}
				else{
					// If it has been clicked once, and the clicked item is the same
					// as the previous, launch double click and cancel task
					if (position == mItemClickedPosition){
						mClickTask.cancel();						
						mTimer.purge();
						
						if (mOnItemDoubleClickListener != null){
							mOnItemDoubleClickListener.onItemDoubleClick(mParent, mView, mPosition, mId);
						}
					}					
					mItemClicked = false;
					mItemClickedPosition = -1;
				}
				
			}			
		});
		
	}
	
	@Override
	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
		super.setOnItemClickListener(l);
	}	

}
