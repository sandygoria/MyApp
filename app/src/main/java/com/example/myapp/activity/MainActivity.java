package com.example.myapp.activity;

import java.util.List;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapp.R;
import com.example.myapp.adapter.FeedAdapter;
import com.example.myapp.model.Message;
import com.example.myapp.utils.RssFeedParser;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{


	//private LikeView likeView;
	private Toolbar mToolbar;
	private FragmentDrawer drawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//FacebookSdk.sdkInitialize(getApplicationContext());

		mToolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		drawerFragment = (FragmentDrawer)
				getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
		drawerFragment.setUp(R.id.fragment_navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
		drawerFragment.setDrawerListener(this);

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		displayView(0);

		AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
				.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
					@Override
					public void onAppInstallAdLoaded(NativeAppInstallAd appInstallAd) {
						// Show the app install ad.
					}
				})
				.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
					@Override
					public void onContentAdLoaded(NativeContentAd contentAd) {
						// Show the content ad.
					}
				})
				.withAdListener(new AdListener() {
					@Override
					public void onAdFailedToLoad(int errorCode) {
						// Handle the failure by logging, altering the UI, etc.
					}
				})
				.withNativeAdOptions(new NativeAdOptions.Builder()
						// Methods in the NativeAdOptions.Builder class can be
						// used here to specify individual options settings.
						.build())
				.build();
	}



	@Override
	public void onDrawerItemSelected(View view, int position) {
		displayView(position);
	}

	private void displayView(int position) {

		Fragment fragment = null;
		String title = "Fragment Name";
		switch (position) {
			case 0:
				fragment = new FeedViewFragment();
				title = "Feed Fragment";
				break;

			default:
				break;
		}

		if(fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.container_body,fragment);
			fragmentTransaction.commit();

			getSupportActionBar().setTitle(title);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
