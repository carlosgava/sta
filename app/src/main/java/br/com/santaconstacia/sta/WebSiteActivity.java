package br.com.santaconstacia.sta;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import br.com.santaconstacia.sta.base.BaseActivity;

public class WebSiteActivity extends BaseActivity {

	private WebView browser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_website);
		browser = (WebView) findViewById(R.id.browser);
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		browser.getSettings().setJavaScriptEnabled(true);  
		browser.getSettings().setLoadWithOverviewMode(true);
	    browser.getSettings().setUseWideViewPort(true);		
		browser.loadUrl(getString(R.string.website));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void onBackClick(View view) {
		browser.goBack();
	}

	public void onForwardClick(View view) {
		browser.goForward();
	}

	public void onReloadClick(View view) {
		browser.loadUrl("http://www.santaconstancia.com.br");
		//browser.reload();
	}
}