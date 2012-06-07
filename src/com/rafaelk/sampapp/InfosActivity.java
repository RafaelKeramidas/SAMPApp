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

public class InfosActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.showInfos();       
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	this.showInfos();  
    }
    
    public void showInfos() {
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
	        		
	        		String[] infos = query.getInfos();
	        		
	        		TextView hostnameTitle = new TextView(this);
	        		hostnameTitle.setTextSize(24);
	        		hostnameTitle.setTextColor(Color.WHITE);
	        		hostnameTitle.setText("Hostname");
	        		ll.addView(hostnameTitle);
	        		
	        		TextView hostnameValue = new TextView(this);
	        		hostnameValue.setTextSize(16);
	        		hostnameValue.setText(infos[3]);
	        		ll.addView(hostnameValue);
	        		
	        		TextView ipTitle = new TextView(this);
	        		ipTitle.setTextSize(24);
	        		ipTitle.setTextColor(Color.WHITE);
	        		ipTitle.setText("IP");
	        		ll.addView(ipTitle);
	        		
	        		TextView ipValue = new TextView(this);
	        		ipValue.setTextSize(16);
	        		ipValue.setText(server[2] + ":" + server[3]);
	        		ll.addView(ipValue);
	        		
	        		TextView passwordTitle = new TextView(this);
	        		passwordTitle.setTextSize(24);
	        		passwordTitle.setTextColor(Color.WHITE);
	        		passwordTitle.setText("Password");
	        		ll.addView(passwordTitle);
	        		
	        		TextView passwordValue = new TextView(this);
	        		passwordValue.setTextSize(16);
	        		passwordValue.setText(infos[0]);
	        		ll.addView(passwordValue);
	        		
	        		TextView playersTitle = new TextView(this);
	        		playersTitle.setTextSize(24);
	        		playersTitle.setTextColor(Color.WHITE);
	        		playersTitle.setText("Players");
	        		ll.addView(playersTitle);
	        		
	        		TextView playersValue = new TextView(this);
	        		playersValue.setTextSize(16);
	        		playersValue.setText(infos[1] + "/" + infos[2] + " (" + (100 * Integer.valueOf(infos[1]) / Integer.valueOf(infos[2]))  + "%)");
	        		ll.addView(playersValue);
	        		
	        		TextView gamemodeTitle = new TextView(this);
	        		gamemodeTitle.setTextSize(24);
	        		gamemodeTitle.setTextColor(Color.WHITE);
	        		gamemodeTitle.setText("Gamemode");
	        		ll.addView(gamemodeTitle);
	        		
	        		TextView gamemodeValue = new TextView(this);
	        		gamemodeValue.setTextSize(16);
	        		gamemodeValue.setText(infos[4]);
	        		ll.addView(gamemodeValue);
	        		
	        		TextView mapnameTitle = new TextView(this);
	        		mapnameTitle.setTextSize(24);
	        		mapnameTitle.setTextColor(Color.WHITE);
	        		mapnameTitle.setText("Map name");
	        		ll.addView(mapnameTitle);
	        		
	        		TextView mapnameValue = new TextView(this);
	        		mapnameValue.setTextSize(16);
	        		mapnameValue.setText(infos[5]);
	        		ll.addView(mapnameValue);
	        		
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
        		TextView textview = new TextView(this);
        		textview.setTextSize(20);
        		setContentView(textview);
        	}
        }
        else {
        	TextView textview = new TextView(this);
        	textview.setTextSize(20);
            textview.setText("No server... Add one by using the add button in the menu.");
            setContentView(textview);
        }
    }
}