package com.willli.allappslauncher;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
import com.willli.gridpager.GridViewPager;

public class Launcher extends Activity implements LoaderCallbacks<List<ResolveInfo>>{

	List<ResolveInfo> mApps;
	GridViewPager mGridView;
	AppAdapter mAdapter;
	AppListChangedReceiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		mGridView = (GridViewPager) findViewById(R.id.all_apps);
		mGridView.setTransitionEffect(TransitionEffect.Tablet);
		mGridView.setFadeEnabled(true);
        mAdapter = new AppAdapter();
        mGridView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        mReceiver = new AppListChangedReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        registerReceiver(mReceiver, filter);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		mGridView.setCurrentItem(0);
	}
	
	@Override
	public Loader<List<ResolveInfo>> onCreateLoader(int id, Bundle args) {
		return new AsyncTaskLoader<List<ResolveInfo>>(Launcher.this){

			@Override
			protected void onStartLoading() {
				forceLoad();
			}
			
			@Override
			public List<ResolveInfo> loadInBackground() {
		        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
		        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		        List<ResolveInfo> result = getPackageManager().queryIntentActivities(mainIntent, 0);
		        Collections.sort(result, new ResolveInfo.DisplayNameComparator(getPackageManager()));
		        return result;
			}
			
		};
	}
	
	@Override
	public void onLoadFinished(Loader<List<ResolveInfo>> loader,
			List<ResolveInfo> data) {
		mApps = data;
		mGridView.notifyDataSetChanged();
	}
	
	@Override
	public void onLoaderReset(Loader<List<ResolveInfo>> loader) {}
	
	class AppAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(mApps == null)
				return 0;
			return mApps.size();
		}

		@Override
		public Object getItem(int position) {
			return mApps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.item_app, null);
			}
			final ResolveInfo info = (ResolveInfo) getItem(position);
			TextView lable = (TextView)convertView.findViewById(R.id.label);
			lable.setText(info.loadLabel(getPackageManager()));			
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
			icon.setImageDrawable(info.loadIcon(getPackageManager()));
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String pkg = info.activityInfo.packageName;
		            String cls = info.activityInfo.name;
		             
		            ComponentName componet = new ComponentName(pkg, cls);
		             
		            Intent i = new Intent();
		            i.setComponent(componet);
		            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            startActivity(i);
				}
			});
			return convertView;
		}
		
	}
	
	class AppListChangedReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
	        getLoaderManager().restartLoader(0, null, Launcher.this);
		}
		
	}


}
