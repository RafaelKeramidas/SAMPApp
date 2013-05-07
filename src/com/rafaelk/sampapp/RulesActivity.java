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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RulesActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules);
	}
	
	public void onResume() {
		super.onResume();
		
		RulesProcess rules = new RulesProcess(RulesActivity.this);
		rules.execute();
	}

	private class RulesProcess extends AsyncTask<String, Void, Boolean> {
		/* Variables */
		private RulesActivity activity = null;
		private String errorMsg = null;
		private String[][] rules = null;
		private LinearLayout rulesView = (LinearLayout) findViewById(R.id.rulesView);
		private TextView errorMessage = (TextView) findViewById(R.id.errorMessage);
		private ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		public RulesProcess(RulesActivity activity) {
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
		        		String[] server = db.getServer(serverid);
			        	SampQuery query = new SampQuery(server[2], Integer.parseInt(server[3]), server[4]);
			        	if(query.isOnline()) {		        		
			        		rules = query.getRules();
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
			rulesView.removeAllViews();
			errorMessage.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
	    }
	 
	    @Override
	    protected void onPostExecute (Boolean result) {
	    	progressBar.setVisibility(View.INVISIBLE);
	    	
	    	if(errorMsg == null) {
	    		for(int i = 0; i < rules[0].length; i++) {
	    			TextView ruleTitle = new TextView(this.activity);
        			ruleTitle.setTextSize(24);
        			ruleTitle.setText(capitalize(rules[0][i]));
        			rulesView.addView(ruleTitle);
	        		
	        		TextView ruleValue = new TextView(this.activity);
	        		ruleValue.setTextSize(16);
	        		ruleValue.setText(rules[1][i]);
	        		rulesView.addView(ruleValue);
	    		}
	    	}
	    	else {
	    		errorMessage.setText(errorMsg);
	    		errorMessage.setVisibility(View.VISIBLE);
	    	}
	    }
	    
		private String capitalize(String s) {
	        if (s.length() == 0) return s;
	        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	    }
	}
}
