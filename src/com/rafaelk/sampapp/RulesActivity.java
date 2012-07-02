/**
 * SA-MP App - Query and RCON Application
 * 
 * @author 		Rafael 'R@f' Keramidas <rafael@keramid.as>
 * @version		1.0.0
 * @date		2th July 2012
 * @licence		GPLv3
 * @thanks		StatusRed : Took example of this query class code for the v0.2.0.
 * 				Sasuke78200 : Some help with the first query class (v0.1.x).
 * 				Woothemes.com : In app icons (tabs and menu).
 * 				TheOriginalTwig : App icon.
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
	        	
	        	query.socketClose();
        	}
        	catch(Exception e) {
        		//TextView textview = new TextView(this);
        		//textview.setTextSize(20);
        		//textview.setText(e.toString());
        		//setContentView(textview);
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