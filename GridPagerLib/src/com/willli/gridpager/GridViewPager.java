package com.willli.gridpager;

import java.util.ArrayList;
import java.util.List;

import com.fuwen.gridpager.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class GridViewPager extends ViewPager {
	
	private List<BGGridView> mGridViewList = null;
	private static final int DEFAULT_COLUMN_NUMBER = 2;
	private static final int DEFAULT_ROW_NUMBER = 3;
	private int mRowNumber = 0;
	private int mColumnNumber = 0;
	private float mCellMinWidth = 0;
	private float mCellMinHeight = 0;
	private float mColumnMargin = 0;
	private float mRowMargin = 0;
	private BaseAdapter mAdapter;
	private View mEmptyView = null;
	private int mPaddingLeft = 0;
	private int mPaddingRight = 0;
	private int mSelection = -1;
	
	public GridViewPager(Context context) {
		this(context, null);
	}

	public GridViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(attrs != null){
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GridViewPager);
			final int N = a.getIndexCount();
	        for (int i = 0; i < N; i++) {
	            int attr = a.getIndex(i);
	            switch (attr) {
	            case R.styleable.GridViewPager_gvpColumnNumber:
	            	mColumnNumber = a.getInt(attr, -1);
	            	break;
	            case R.styleable.GridViewPager_gvpRowNumber:
	            	mRowNumber = a.getInt(attr, -1);
	            	break;
	            case R.styleable.GridViewPager_gvpColumnMargin:
	            	mColumnMargin = a.getDimension(attr, 0);
	            	break;
	            case R.styleable.GridViewPager_gvpRowMargin:
	            	mRowMargin = a.getDimension(attr, 0);
	            	break;
	            case R.styleable.GridViewPager_gvpMinCellWidth:
	            	mCellMinWidth = a.getDimension(attr, -1);
	            	break;
	            case R.styleable.GridViewPager_gvpMinCellHeight:
	            	mCellMinHeight = a.getDimension(attr, -1);
	            	break;
	            case R.styleable.GridViewPager_android_padding:
	            	int padding = a.getDimensionPixelSize(attr, 0);
	            	setPadding(padding, padding, padding, padding);
	            	break;
	            case R.styleable.GridViewPager_android_paddingLeft:
	            	mPaddingLeft = a.getDimensionPixelSize(attr, 0);
	            	break;
	            case R.styleable.GridViewPager_android_paddingRight:
	            	mPaddingRight = a.getDimensionPixelSize(attr, 0);
	            	break;
	            	
	            }
	        }
			if(mColumnNumber <=0 && mCellMinWidth <= 0){
				mColumnNumber = DEFAULT_COLUMN_NUMBER;
			}

			if(mRowNumber <=0 && mCellMinHeight <= 0){
				mRowNumber = DEFAULT_ROW_NUMBER;
			}
			a.recycle();
		}
		init();
	}
	
	private void init(){
		mGridViewList = new ArrayList<BGGridView>();
	}
	
	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		mPaddingLeft = left;
		mPaddingRight = right;
		super.setPadding(0, top, 0, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 设置宽度和高度
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int columnNumberOld = mColumnNumber;
		int rowNumberOld = mRowNumber;
		if (mCellMinWidth > 0) {
			mColumnNumber = (int) Math
					.floor((MeasureSpec.getSize(widthMeasureSpec)
							+ mColumnMargin - mPaddingLeft - mPaddingRight)
							/ (mCellMinWidth + mColumnMargin));
		}
		if(mCellMinHeight > 0){
			mRowNumber = (int) Math
					.floor((MeasureSpec.getSize(heightMeasureSpec)
							+ mRowMargin)
							/ (mCellMinHeight + mRowMargin));
		}
		if(rowNumberOld != mRowNumber || columnNumberOld != mColumnNumber){
			resetAdapter();
		}
	}
	
	public int getPageCount(){
		return mGridViewList.size();
	}
	
	public int getPageSize(){
		return mColumnNumber*mRowNumber;
	}

	public void setSelection(int position) {
		final int pageSize = getPageSize();
		if(mAdapter==null || pageSize<=0){
			mSelection = position;
			return;
		}
		mSelection = -1;
		setCurrentItem(position/pageSize, true);
	}  
	
	public int getSelection(){
		return getCurrentItem()*getPageSize();
	}
	
	@Override
	public Parcelable onSaveInstanceState() {
		//begin boilerplate code that allows parent classes to save state
	    Parcelable superState = super.onSaveInstanceState();

	    SavedState ss = new SavedState(superState);
	    //end

	    ss.selection = getSelection();
	    return ss;
	}
	
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		//begin boilerplate code so parent classes can restore state
	    if(!(state instanceof SavedState)) {
	      super.onRestoreInstanceState(state);
	      return;
	    }

	    SavedState ss = (SavedState)state;
	    super.onRestoreInstanceState(ss.getSuperState());
	    //end

	    setSelection(ss.selection);
	    Log.e("NULL", "setSelection: "+ss.selection);
	}
	
	static class SavedState extends BaseSavedState {
		int selection;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			this.selection = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(this.selection);
		}

		// required field that makes Parcelables from a Parcel
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public void setEmptyView(TextView emptyView) {
		mEmptyView = emptyView;
	}
	
	public void setAdapter(BaseAdapter adapter){
		mAdapter = adapter;
		resetAdapter();
	}
	
	public void notifyDataSetChanged(){
		resetAdapter();
	}
	
	private void resetAdapter() {
		int pageSize = mColumnNumber*mRowNumber;
		if(pageSize <= 0)
			return;
		
		if(mAdapter.getCount() == 0){
			mGridViewList.removeAll(mGridViewList);
			if(mEmptyView != null)
				mEmptyView.setVisibility(View.VISIBLE);
		}else{
			if(mEmptyView != null)
				mEmptyView.setVisibility(View.GONE);
		}
		int pageCount = mAdapter.getCount()/pageSize;
		if(mAdapter.getCount()%pageSize == 0){
			pageCount--;
		}
		int listSize = mGridViewList.size()-1;
		BGGridView gridview;
		GridAdapter gridAdapter;
		for(int i=0;i<=Math.max(listSize, pageCount);i++){
			if(i<=listSize&&i<=pageCount){
				gridview = mGridViewList.get(i);
				gridAdapter = new GridAdapter(i,pageSize,mAdapter);
				gridview.setAdapter(gridAdapter);
				mGridViewList.set(i, gridview);
				continue;
			}
			if(i>listSize&&i<=pageCount){
				gridview = new BGGridView();
				gridAdapter = new GridAdapter(i,pageSize,mAdapter);
				gridview.setAdapter(gridAdapter);
				mGridViewList.add(gridview);
				continue;
			}
			if(i>pageCount&&i<=listSize){
				mGridViewList.remove(pageCount+1);
				continue;
			}
		}
		super.setAdapter(new GridPagerAdapter());
		if(mSelection >= 0)
			setSelection(mSelection);
	}

	private class GridPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mGridViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mGridViewList.get(position),new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			return mGridViewList.get(position);
		}
	}
	
	private class GridAdapter extends BaseAdapter{
		int mPage;
		int mSize;
		BaseAdapter mAdapter;
		public GridAdapter(int page,int size,BaseAdapter adapter){
			mPage = page;
			mSize = size;
			mAdapter = adapter;
		}
		@Override
		public int getCount() {
			if(mAdapter.getCount()%mSize==0)
				return mSize;
			else if(mPage < mAdapter.getCount()/mSize){
				return mSize;
			}else{
				return mAdapter.getCount()%mSize;
			}
		}

		@Override
		public Object getItem(int position) {
			return mAdapter.getItem(mPage*mSize+position);
		}

		@Override
		public long getItemId(int position) {
			return mAdapter.getItemId(mPage*mSize+position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return mAdapter.getView(mPage*mSize+position, convertView, parent);
		}
		
	}
	
	
	public class BGGridView extends AdapterView<ListAdapter>{  

	    private ListAdapter mAdapter;
	    
	    public BGGridView() {  
	        super(GridViewPager.this.getContext());
	    }
	    
	    /** 
	     * 继承AdapterView需要实现以下四个方法 
	     *  getAdapter() 
	     *  setAdapter(ListAdapter adapter) 
	     *  getSelectedView() 
	     *  setSelection(int position) 
	     */  
	    @Override  
	    public ListAdapter getAdapter() {
	        return mAdapter;  
	    }  
	  
	    @Override  
	    public void setAdapter(ListAdapter adapter) {  
	        this.mAdapter = adapter;  
	        //把所有的child添加到布局中  
	        int oldChildCount = getChildCount();
	        int newChildCount = mAdapter.getCount();
	        
	        for(int i=0 ; i<oldChildCount && i<newChildCount; i++){
	        	mAdapter.getView(i,getChildAt(i),this);
	        }
	        for(int i = oldChildCount; i<newChildCount ; i++){  
	            View child = mAdapter.getView(i,null,this);
	            addViewInLayout(child,i,new LayoutParams(0, 0));  
	        }
	        int d = oldChildCount - newChildCount;
	        if(d > 0){
	        	removeViewsInLayout(newChildCount, d);
	        }
	    }  
	  
	    @Override  
	    public View getSelectedView() {  
	        if(getChildCount()>0){
	        	return getChildAt(0);
	        }
	        return null;
	    }  
	  
	    @Override  
	    public void setSelection(int position) { }  


	    @Override
	    public int getPaddingLeft() {
	    	return mPaddingLeft;
	    }
	    
	    @Override
	    public int getPaddingRight() {
	    	return mPaddingRight;
	    }
	    
		/** 
	     * 设置大小 
	     */  
	    @Override  
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	        //设置宽度和高度   
	        int childWidth = (int)(MeasureSpec.getSize(widthMeasureSpec)-mColumnMargin*(mColumnNumber-1)-getPaddingLeft() - getPaddingRight())/mColumnNumber;  
	        int childHeight = (int)(MeasureSpec.getSize(heightMeasureSpec)-mRowMargin*(mRowNumber-1))/mRowNumber; 
	    	for(int i = 0;i<getChildCount();i++){
	    		View child = getChildAt(i);
	    		LayoutParams lp = (LayoutParams) child.getLayoutParams();
	    		lp.width = childWidth;
	    		lp.height = childHeight;
	    		child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
	    				, MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
	    	}
	        setMeasuredDimension(  
	                getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec),  
	                getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec)  
	        );  
	    } 
	    
	    /** 
	     * 设置布局 
	     */  
	    @Override  
	    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	    	
	        int childCount = getChildCount();  
	        int pLeft = 0;
	        int pTop = getPaddingTop(); 

	  
			for (int i = 0; i < childCount && i < mColumnNumber * mRowNumber; i++) {
				View child = getChildAt(i);
				int x = i % mColumnNumber;
				if(x == 0){
					pLeft = getPaddingLeft();
				}
	    		LayoutParams lp = child.getLayoutParams();
	    		child.layout(pLeft, pTop, pLeft + lp.width, pTop + lp.height);
				
	    		pLeft += lp.width + mColumnMargin;
				if(x == mColumnNumber-1){
					pTop+=lp.height + mRowMargin;
				}
			}
	  
	    }

	} 

}
