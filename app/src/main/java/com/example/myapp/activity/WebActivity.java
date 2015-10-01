package com.example.myapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

	String launchURL;
	String launchTitle;
	WebView webview;
	boolean progressDisplay;

	final Activity activity = this;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);

		webview = new WebView(this);
		setContentView(webview);

		
		launchURL = getIntent().getExtras().getString("URL");
		launchTitle = getIntent().getExtras().getString("TITLE");
		

		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setRenderPriority(RenderPriority.HIGH);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webview.getSettings().setSaveFormData(false);

		webview.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
					webview.goBack();
					return true;
				}
				return false;
			}
		});

		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setProgress(progress * 100);
			}
		});
		
		webview.setWebViewClient(new InnerWebViewClient());

		if (launchURL != null)
			webview.loadUrl(launchURL);

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.book_flight, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			// Intent intent = new Intent(this, HomeActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
			super.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* To ensure links open within the application */
	private class InnerWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("tel:")) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
				startActivity(intent);
			} else {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			startProgress();
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			Log.d("Base URL : " ,launchURL);
			Log.d("Current URL : " , url);
			stopProgress();
			super.onPageFinished(view, url);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub


			super.onLoadResource(view, url);
		}
	}

	private void startProgress() {
		if (!progressDisplay) {
			progressDisplay = true;
		}
	}

	private void stopProgress() {
		progressDisplay = false;
	}

}
