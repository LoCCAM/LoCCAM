package br.ufc.great.loccam;

import java.util.List;

import br.ufc.great.loccam.service.SysSUService;
import br.ufc.loccam.adaptation.reasorner.AdaptationReasoner;
import br.ufc.loccam.cacmanager.CACManager;
import android.os.Bundle;
import android.os.Looper;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	// Ui Elements
	private TextView state, cacState;
	private ListView cacList;
	private ToggleButton tButton;
	private Intent startServiceIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		state = (TextView) findViewById(R.id.textView1);
		cacState = (TextView) findViewById(R.id.textView2);
		tButton = (ToggleButton) findViewById(R.id.toggleButton1);
		cacList = (ListView) findViewById(R.id.listView1);
		startServiceIntent = new Intent(MainActivity.this, SysSUService.class);
		updateUI();
		tButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!checkServiceState()) 					
			        startService(startServiceIntent);  
				else 
					stopService(startServiceIntent);
				updateUI();	
			}		
		});
		
		//PackageManager p = getPackageManager();
		//p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}

	private void updateUI() {		
		if(checkServiceState()) {
			state.setText(R.string.isRunning);
			state.setTextColor(Color.GREEN);
			tButton.setChecked(true);
			cacState.setText(R.string.cacState);
			cacState.setBackgroundResource(R.drawable.header_bg);
			fillCACList();
		}		
		else {
			state.setText(R.string.isNotRunning);
			state.setTextColor(Color.RED);
			tButton.setChecked(false);
			cacState.setText("");
			cacState.setBackgroundColor(Color.TRANSPARENT);
			removeCACList();
		}		
	}

	private void removeCACList() {
		
	}

	private void fillCACList() {
		String s = readState();		
	}

	private String readState() {
		CACManager cacManager = CACManager.getInstance(this);
		AdaptationReasoner adptReasoner = new AdaptationReasoner(CACManager.getInstance(this).getListOfAvailableCACs());
		String r;
		r = ((CACManager)cacManager).printBundlesState();
		r += ((AdaptationReasoner)adptReasoner).printDesiredOZ();
		
		return r;
	}

	private boolean checkServiceState() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    String name = SysSUService.class.getName();
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (name.equals(service.service.getClassName())) 
	        	return true;
	    }
	    return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
