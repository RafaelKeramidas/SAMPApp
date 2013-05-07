/**
 * SA-MP App - Query and RCON Application
 * 
 * @author 		Rafael 'R@f' Keramidas <rafael@keramid.as>
 * @version		2.0.2
 * @date		7th May 2012
 * @licence		GPLv3
 * @thanks		StatusRed : Took example of this query class code for the v0.2.0.
 * 				Sasuke78200 : Some help with the first query class (v0.1.x).
 * 				Woothemes.com : In app icons (tabs and menu).
 * 				TheOriginalTwig : App icon.
 */

package com.rafaelk.sampapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InfosActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infos);
	}
	
	public void onResume() {
		super.onResume();
		
		InfosProcess infos = new InfosProcess(InfosActivity.this);
		infos.execute();
	}

	private class InfosProcess extends AsyncTask<String, Void, Boolean> {
		/* Variables */
		private InfosActivity activity = null;
		private String errorMsg = null;
		private String[] infos = null;
		private String[] server = null;
		private TextView hostnameTitle = (TextView) findViewById(R.id.hostnameTitle);
		private TextView hostnameData = (TextView) findViewById(R.id.hostnameData);
		private TextView ipTitle = (TextView) findViewById(R.id.ipTitle);
		private TextView ipData = (TextView) findViewById(R.id.ipData);
		private TextView passwordTitle = (TextView) findViewById(R.id.passwordTitle);
		private TextView passwordData = (TextView) findViewById(R.id.passwordData);
		private TextView playersTitle = (TextView) findViewById(R.id.playersTitle);
		private TextView playersData = (TextView) findViewById(R.id.playersData);
		private TextView gamemodeTitle = (TextView) findViewById(R.id.gamemodeTitle);
		private TextView gamemodeData = (TextView) findViewById(R.id.gamemodeData);
		private TextView mapnameTitle = (TextView) findViewById(R.id.mapnameTitle);
		private TextView mapnameData = (TextView) findViewById(R.id.mapnameData);
		private TextView errorMessage = (TextView) findViewById(R.id.errorMessage);
		private ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		public InfosProcess(InfosActivity activity) {
			this.activity = activity;
		}
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				DatabaseHandler db = new DatabaseHandler(this.activity); 
		        if(db.getServerCount() != 0) {
		        	SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
		        	int serverid = sp.getInt("serverid", 1);
		        	try {        		
			        	server = db.getServer(serverid);
			        	SampQuery query = new SampQuery(server[2], Integer.parseInt(server[3]), server[4]);
			        	if(query.isOnline()) {	   
			        		infos = query.getInfos();
			        	}
			        	else {
			        		this.errorMsg = "Couldn't reach the server.";
			        	}
			        	
			        	query.socketClose();
		        	}
		        	catch(Exception e) {
		        		this.errorMsg = e.toString();
		        	}
		        }
		        else {
		        	this.errorMsg = "No server... Add one by using the add button in the menu.";
		        }
			}
			catch(Exception e) {
				this.errorMsg = "An unexpected error has occurred !";
			}
			return true;
		}
		
		@Override
	    protected void onPreExecute () {	
			hostnameTitle.setVisibility(View.INVISIBLE);
			hostnameData.setVisibility(View.INVISIBLE);
			ipTitle.setVisibility(View.INVISIBLE);
			ipData.setVisibility(View.INVISIBLE);
			passwordTitle.setVisibility(View.INVISIBLE); 
			passwordData.setVisibility(View.INVISIBLE); 
			playersTitle.setVisibility(View.INVISIBLE);
			playersData.setVisibility(View.INVISIBLE); 
			gamemodeTitle.setVisibility(View.INVISIBLE); 
			gamemodeData.setVisibility(View.INVISIBLE); 
			mapnameTitle.setVisibility(View.INVISIBLE); 
			mapnameData.setVisibility(View.INVISIBLE); 
			errorMessage.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
	    }
	 
	    @Override
	    protected void onPostExecute (Boolean result) {
	    	progressBar.setVisibility(View.INVISIBLE);
	    	
	    	if(errorMsg == null) {
	    		double playerPourcentage = 0;
	    		if(Integer.parseInt(infos[1]) > 0)
	    			playerPourcentage = (100 * Integer.valueOf(infos[1]) / Integer.valueOf(infos[2]));
	    		
	    		hostnameData.setText(infos[3]);
	    		ipData.setText(server[2] + ":" + server[3]);
				passwordData.setText(infos[0]);
				playersData.setText(infos[1] + "/" + infos[2] + " (" + playerPourcentage + "%)");
				gamemodeData.setText(infos[4]);
				mapnameData.setText(infos[5]);
				
				hostnameTitle.setVisibility(View.VISIBLE);
				hostnameData.setVisibility(View.VISIBLE);
				ipTitle.setVisibility(View.VISIBLE);
				ipData.setVisibility(View.VISIBLE);
				passwordTitle.setVisibility(View.VISIBLE); 
				passwordData.setVisibility(View.VISIBLE); 
				playersTitle.setVisibility(View.VISIBLE);
				playersData.setVisibility(View.VISIBLE); 
				gamemodeTitle.setVisibility(View.VISIBLE); 
				gamemodeData.setVisibility(View.VISIBLE); 
				mapnameTitle.setVisibility(View.VISIBLE); 
				mapnameData.setVisibility(View.VISIBLE);
	    	}
	    	else {
	    		errorMessage.setText(errorMsg);
	    		errorMessage.setVisibility(View.VISIBLE);
	    	}
	    }
		
	}
}
