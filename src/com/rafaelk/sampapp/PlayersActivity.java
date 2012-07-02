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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
	        		boolean isAdmin = false;
	        		if(query.isValidRconPassword()) 
	        			isAdmin = true;
	        		
	        		final boolean isRconAdmin = isAdmin;
	        		
	        		getListView().setAdapter(null);
	        		
	        		if(playercount == 0) {
	        			Toast.makeText(this, "No players.", Toast.LENGTH_SHORT).show();
	        		}
	        		else if(playercount <= 100) {
	        			try {	    	        		
		        			final String[][] players = query.getPlayers();
		        			String[] idplayername = new String[playercount];
		        			
		        			for(int i = 0; i < playercount; i++) {
		        				idplayername[i] = players[1][i] + " (" + players[0][i] + ")";
		        			}
		        			
		        			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idplayername));
		        			
		        			ListView listView = getListView();
		        			listView.setTextFilterEnabled(true);
		        	 
		        			listView.setOnItemClickListener(new OnItemClickListener() {
		        				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        					showPlayerDialog(players[0][position], players[1][position], players[2][position], players[3][position], isRconAdmin);
		        				}
		        			});
	        			}
	        			catch(Exception e) {
	        				Toast.makeText(this, "Couldn't get the player list.", Toast.LENGTH_SHORT).show();
	        				Log.d("Players list bug:", ""+e);
	        			}
	        		}
	        		else {
	        			Toast.makeText(this, "Too many players to get the list.", Toast.LENGTH_SHORT).show();
	        		}
	        		
	        	}
	        	else {
	        		Toast.makeText(this, "Server offline.", Toast.LENGTH_SHORT).show();
	        	}
	        	
	        	query.socketClose();
        	}
        	catch(Exception e) {
        		Toast.makeText(this, "Server not found. Please choose another from the list.", Toast.LENGTH_SHORT).show();
        	}
        }
        else {
        	Toast.makeText(this, "No server... Add one by using the add button in the menu.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showPlayerDialog(final String playerid, String playername, String playerscore, String playerping, final boolean isRconAdmin) {
    	SharedPreferences sp = this.getSharedPreferences(SampAppActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
    	int serverid = sp.getInt("serverid", 1);
    	try {
    		DatabaseHandler db = new DatabaseHandler(this);
        	final String[] server = db.getServer(serverid);
        	LayoutInflater li = LayoutInflater.from(PlayersActivity.this);
    		View playerdetailView = li.inflate(R.layout.playerdetail, null);

    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayersActivity.this);
    		alertDialogBuilder.setView(playerdetailView);

    		TextView playernameText = (TextView) playerdetailView.findViewById(R.id.textView1);
    		TextView playeridText = (TextView) playerdetailView.findViewById(R.id.textView2);
    		TextView playerscoreText = (TextView) playerdetailView.findViewById(R.id.textView3);
    		TextView playerpingText = (TextView) playerdetailView.findViewById(R.id.textView4);
    		
    		playernameText.setText(playername);
    		playeridText.setText("ID: " + playerid);
    		playerscoreText.setText("Score: " + playerscore);
    		playerpingText.setText("Ping: " + playerping);

    		if(isRconAdmin == true) {
	    		alertDialogBuilder
	    			.setTitle("Details of " + playername)
	    			.setCancelable(false)
	    			.setPositiveButton("Kick", 
	    			new DialogInterface.OnClickListener() {
	    			    public void onClick(DialogInterface dialog,int id) {
	    			    	try {
	    	                	SampQuery query = new SampQuery(server[2], Integer.parseInt(server[3]), server[4]);
	    						if(isRconAdmin == true) {
	    							if(query.sendRconCommand("kick " + playerid)) {
	        							Toast.makeText(PlayersActivity.this, "Player kicked !", Toast.LENGTH_SHORT).show();
	        							dialog.cancel();
	        							showPlayers();
	        						}
	        						else {
	        							Toast.makeText(PlayersActivity.this, "Couldn't kick the player...", Toast.LENGTH_SHORT).show();
	        						}
	    						}
	    						else {
	    							Toast.makeText(PlayersActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();	
	    						}
	    						query.socketClose();	
	                    	}
	                    	catch (Exception e) {
	                    		Toast.makeText(PlayersActivity.this, "Couldn't send the command...", Toast.LENGTH_SHORT).show();
	                    	}
	    			    }
	    			})
	    			.setNeutralButton("Ban", 
	    			new DialogInterface.OnClickListener() {
	    			    public void onClick(DialogInterface dialog,int id) {
	    			    	try {
	    	                	SampQuery query = new SampQuery(server[2], Integer.parseInt(server[3]), server[4]);
	    						if(query.isValidRconPassword()) {
	    							if(query.sendRconCommand("ban " + playerid)) {
	        							Toast.makeText(PlayersActivity.this, "Player banned !", Toast.LENGTH_SHORT).show();
	        	    			    	dialog.cancel();
	        	    			    	showPlayers();
	        						}
	        						else {
	        							Toast.makeText(PlayersActivity.this, "Couldn't ban the player...", Toast.LENGTH_SHORT).show();
	        						}
	    						}
	    						else {
	    							Toast.makeText(PlayersActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();	
	    						}
	    						query.socketClose();	
	                    	}
	                    	catch (Exception e) {
	                    		Toast.makeText(PlayersActivity.this, "Couldn't send the command...", Toast.LENGTH_SHORT).show();
	                    	}
	    			    }
	    			})
	    			.setNegativeButton("Close",
	    			new DialogInterface.OnClickListener() {
	    			    public void onClick(DialogInterface dialog,int id) {
	    			    	dialog.cancel();
	    			    }
	    			});
    		}
    		else {
    			alertDialogBuilder
    			.setTitle("Details of " + playername)
    			.setCancelable(false)
    			.setNegativeButton("Close",
    			new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog,int id) {
    			    	dialog.cancel();
    			    }
    			});
    		}

    		AlertDialog alertDialog = alertDialogBuilder.create();
    		alertDialog.show();
    	}
    	catch(Exception e) {
    		Toast.makeText(PlayersActivity.this, "No server selected.", Toast.LENGTH_SHORT).show();	
    	}
    }
}