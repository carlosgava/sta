package br.com.santaconstacia.sta.base;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class BaseActivity extends FragmentActivity {
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case android.R.id.home:
		        NavUtils.navigateUpFromSameTask(this);
		        return true;
	    }
		
	    return super.onOptionsItemSelected(item);
	}
}
