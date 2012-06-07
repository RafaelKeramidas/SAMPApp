/**
 * SA-MP App - Query and RCON Application
 * 
 * @author 		Rafael 'R@f' Keramidas <rafael@keramid.as>
 * @version		0.1.1 Beta
 * @date		1st June 2012
 * @licence		GPLv3
 * @thanks		Icons : woothemes.com - App icon : TheOriginalTwig
 */

package com.rafaelk.sampapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RulesActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.showRules();        	
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	this.showRules();  
    }
    
    public void showRules() {
    	DatabaseHandler db = new DatabaseHandler(this); 
        if(db.getServerCount() != 0) {
        	SharedPreferences sp = this.getSharedPreferences(SampAppActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
        	int serverid = sp.getInt("serverid", 1);
        	try {
        		String[] server = db.getServer(serverid);
	        	SampQuery query = new SampQuery(server[2], Integer.parseInt(server[3]), server[4]);
	        	if(query.isOnline()) {
	        		ScrollView sv = new ScrollView(this);
	        		LinearLayout ll = new LinearLayout(this);
	        		ll.setOrientation(LinearLayout.VERTICAL);
	        		sv.addView(ll);
	        		
	        		String[][] rules = query.getRules();
	        		for(int i = 0; i < rules[0].length; i++) {
	        			TextView ruleTitle = new TextView(this);
	        			ruleTitle.setTextSize(24);
	        			ruleTitle.setTextColor(Color.WHITE);
	        			ruleTitle.setText(capitalize(rules[0][i]));
		        		ll.addView(ruleTitle);
		        		
		        		TextView ruleValue = new TextView(this);
		        		ruleValue.setTextSize(16);
		        		ruleValue.setText(rules[1][i]);
		        		ll.addView(ruleValue);
	        		}
	        		
	        		setContentView(sv);
	        	}
	        	else {
	        		TextView textview = new TextView(this);
	        		textview.setTextSize(20);
	        		textview.setText("Server offline.");
	        		setContentView(textview);
	        	}
        	}
        	catch(Exception e) {
        	}
        }
        else {
        	TextView textview = new TextView(this);
        	textview.setTextSize(20);
            textview.setText("No server... Add one by using the add button in the menu.");
            setContentView(textview);
        }
    }
    
    public static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}