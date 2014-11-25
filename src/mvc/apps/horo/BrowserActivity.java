package mvc.apps.horo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class BrowserActivity extends Activity {
	
	private String domain = "";
	private String filename = "";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.output);
        WebView mWeb = (WebView) findViewById(R.id.web);
        Intent intent = getIntent();
		domain = intent.getStringExtra("DOMAIN");
		filename = intent.getStringExtra("FILENAME");
        Log.e("BROWSER", "output: "+ domain + "/horo-out/" + filename + ".html");
        mWeb.loadUrl(domain + "/horo-out/" + filename + ".html" );
	}
}
