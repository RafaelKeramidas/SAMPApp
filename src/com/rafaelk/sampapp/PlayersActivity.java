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

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlayersActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.showPlayers();        	
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	this.showPlayers();  
    }
    
    public void showPlayers() {
    	DatabaseHandler db = new DatabaseHandler(this); 
        if(db.getServerCount() != 0) {
        	SharedPreferences sp = this.getSharedPreferences(SampAppActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
        	int serverid = sp.getInt("serverid", 1);
        	try {
        		String[] server = db.getServer(serverid);
	        	TextView textview = new TextView(this);
	        	textview.setTextSize(20);
	        	SampQuery query = new SampQuery(server[2], Integer.parseInt(server[3]), server[4]);
	        	if(query.isOnline()) {
	        		String[] infos = query.getInfos();
	        		int playercount = Integer.valueOf(infos[1]);
	        		
	        		if(playercount == 0) {
	        			Toast.makeText(this, "No players.", Toast.LENGTH_SHORT).show();
	        		}
	        		else if(playercount <= 100) {
	        			try {	    	        		
		        			String[][] players = query.getPlayers();
		        			String[] idplayername = new String[playercount];
		        			
		        			for(int i = 0; i < playercount; i++) {
		        				idplayername[i] = players[1][i] + " (" + players[0][i] + ")";
		        			}
		        			
		        			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idplayername));
		                	getListView().setTextFilterEnabled(true);
	        			}
	        			catch(Exception e) {
	        				Toast.makeText(this, "Couldn't get the player list.", Toast.LENGTH_SHORT).show();
	        			}
	        		}
	        		else {
	        			Toast.makeText(this, "Too many players to get the list.", Toast.LENGTH_SHORT).show();
	        		}
	        	}
	        	else {
	        		Toast.makeText(this, "Server offline.", Toast.LENGTH_SHORT).show();
	        	}
        	}
        	catch(Exception e) {
        	}
        }
        else {
        	Toast.makeText(this, "No server... Add one by using the add button in the menu.", Toast.LENGTH_SHORT).show();
        }
    }
}