package com.example.myapp.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapp.R;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

public class MainActivity extends AppCompatActivity {

	private String urlToParse = "http://rss.jagran.com/naidunia/madhya-pradesh/ujjain.xml";
	private ListView listViewPosts;
	private List<Message> messages;
	private  ProgressDialog pdDialog;

	private SwipeRefreshLayout swipeRefreshLayout;
	//private LikeView likeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//FacebookSdk.sdkInitialize(getApplicationContext());


		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		pdDialog =  new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		pdDialog.setMessage("Please wait..");
		pdDialog.setTitle("Fetching latest news");
		
		listViewPosts = (ListView)findViewById(R.id.listViewPost);
		swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefLayoutLayout);

		//likeView = (LikeView) findViewById(R.id.like_view);


		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				fetchFeeds();
			}
		});

		fetchFeeds();
		
		listViewPosts.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(MainActivity.this, messages.get(position).toString(), Toast.LENGTH_SHORT).show();

				Intent viewMessage = new Intent(Intent.ACTION_VIEW, Uri.parse(messages.get(position).getLink()));
				startActivity(viewMessage);

				Intent inAppBroswer = new Intent(MainActivity.this, WebActivity.class);
				inAppBroswer.putExtra("URL", messages.get(position).getLink());
				inAppBroswer.putExtra("TITLE", messages.get(position).getTitle());
				startActivity(inAppBroswer);
//
//
//				ShareLinkContent content = new ShareLinkContent.Builder()
//						.setContentUrl(Uri.parse(messages.get(position).getLink()))
//						.setContentDescription("shared from facevook")
//						.build();

//				likeView.setObjectIdAndType(
//						messages.get(position).getLink(),
//						LikeView.ObjectType.PAGE);
			}
		});


		listViewPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(MainActivity.this,"Long press "+messages.get(position).getDescription(), Toast.LENGTH_SHORT).show();

				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, messages.get(position).getDescription());
				startActivity(Intent.createChooser(shareIntent, "Send To.."));
				startActivity(shareIntent);

				return true;
			}
		});

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

	private void fetchFeeds() {

		if(isNetworkAvailable()) {
			FeedParseTask feedParseTask = new FeedParseTask(this);
			feedParseTask.execute(urlToParse);
		} else {
			Toast.makeText(this, "Please check your network connection and try again later.", Toast.LENGTH_SHORT).show();
		}
	}

	private class FeedParseTask extends AsyncTask<String, Integer, List<Message>> {
		
		private Context context;
		public FeedParseTask(MainActivity mainActivity) {
			this.context = mainActivity;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(pdDialog !=null && !pdDialog.isShowing()) {
				pdDialog.show();
			}
		}
		
		@Override
		protected List<Message> doInBackground(String... params) {
			String urlStr = params[0];
							
			RssFeedParser feedParser = new RssFeedParser(urlStr);
			messages = feedParser.parse();
			
			//Log.d("debug", " -- parsed message are :" + messages.toString());
			return messages;
		} 
		
		
		@Override
		protected void onPostExecute(List<Message> result) {
			super.onPostExecute(result);

			if(result != null && result.size() > 0 ) {
				Log.e("Feed count", " list size is ::" +result.size());
				
				FeedAdapter feedAdapter = new FeedAdapter(context, result);
				listViewPosts.setAdapter(feedAdapter);
				
			} else {
				Log.e("Error ::", "-- in Error Getting list size");
			}

			swipeRefreshLayout.setRefreshing(false);

			if(pdDialog !=null && pdDialog.isShowing()) {
				pdDialog.dismiss();
			}
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
	
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


}
