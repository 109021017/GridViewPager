package com.willli.gridpager.sample;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.willli.gridpager.GridViewPager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GridViewPager mGridPager = (GridViewPager) findViewById(R.id.gvp);
		mGridPager.setAdapter(new GridPagerAdapter(34));
	}
	
	public class GridPagerAdapter extends BaseAdapter {

		int mSize;
		Random mRandom;
		
		public GridPagerAdapter(int size){
			mSize = size;
			mRandom = new Random(47);
		}
		
		@Override
		public int getCount() {
			return mSize;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.item_gvp, null);
			}
			int color = mRandom.nextInt(0xFFFFFF)+0xFF000000;
			convertView.setBackgroundColor(color);
			TextView number = (TextView) convertView.findViewById(R.id.tv_position);
			number.setText(""+position);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {}
			});
			return convertView;
		}

	}
}
