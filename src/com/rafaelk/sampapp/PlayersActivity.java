/**
 * SA-MP App - Query and RCON Application
 * 
 * @author 		Rafael 'R@f' Keramidas <rafael@keramid.as>
 * @version		2.0.5
 * @date		8th May 2012
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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayersActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players);
	}

	public void onResume() {
		super.onResume();
		
		PlayersProcess players = new PlayersProcess(PlayersActivity.this);
		players.execute();
	}

	private class PlayersProcess extends AsyncTask<String, Void, Boolean> {
		/* Variables */
		private PlayersActivity activity = null;
		private String errorMsg = null;
		private String[][] players = null;
		private int playerCount = 0;
		private String[] idPlayerName = null;
		private boolean isRconAdmin;
		private ListView playerList = (ListView) findViewById(R.id.listView1);
		private TextView errorMessage = (TextView) findViewById(R.id.errorMessage);
		private ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		public PlayersProcess(PlayersActivity activity) {
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
			        		String[] infos = query.getInfos();
			        		this.playerCount = Integer.valueOf(infos[1]);
			        		boolean isAdmin = false;
			        		if(query.isValidRconPassword()) 
			        			isAdmin = true;
			        		
			        		this.isRconAdmin = isAdmin;
			        		
			        		if(this.playerCount == 0) {
			        			this.errorMsg = "No players.";
			        		}
			        		else if(this.playerCount <= 100) {
			        			try {	    	        		
				        			this.players = query.getPlayers();
				        			this.idPlayerName = new String[this.playerCount];
			        			}
			        			catch(Exception e) {
			        				this.errorMsg = "Couldn't get the player list.";
			        			}
			        		}
			        		else {
			        			this.errorMsg = "Too many players to get the list.";
			        		}
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
			playerList.setAdapter(null);
			errorMessage.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
	    }
	 
	    @Override
	    protected void onPostExecute (Boolean result) {
	    	progressBar.setVisibility(View.INVISIBLE);
	    	
	    	if(errorMsg == null) {
	    		try {
		    		ArrayAdapter<String> playersInList = new ArrayAdapter<String>(this.activity, android.R.layout.simple_list_item_1);
		    		playerList.setAdapter(playersInList);
		    		
		    		for(int i = 0; i < playerCount; i++) {
	    				idPlayerName[i] = players[1][i] + " (" + players[0][i] + ")";
	    				playersInList.add(idPlayerName[i]);		
	    			}
		    		
		    		playerList.setOnItemClickListener(new OnItemClickListener() {
	    				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    					showPlayerDialog(players[0][position], players[1][position], players[2][position], players[3][position], isRconAdmin);
	    				}
	    			});
	    		}
	    		catch(Exception e) {
	    			errorMessage.setText("An unexpected error has occurred !");
		    		errorMessage.setVisibility(View.VISIBLE);
	    		}
	    	}
	    	else {
	    		errorMessage.setText(errorMsg);
	    		errorMessage.setVisibility(View.VISIBLE);
	    	}
	    }
	    
	    private void showPlayerDialog(final String playerid, String playername, String playerscore, String playerping, final boolean isRconAdmin) {
	    	SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
	    	int serverid = sp.getInt("serverid", 1);
	    	try {
	    		DatabaseHandler db = new DatabaseHandler(this.activity);
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
		    			    		KickPlayerProcess kick = new KickPlayerProcess(PlayersActivity.this, server[2], server[3], server[4], playerid, dialog);
		    			    		kick.execute();
		    			    	}
		    			    	catch(Exception e) {
		    			    		Log.d("SAMPAPP", e.toString());
		    			    	}
		    			    }
		    			})
		    			.setNeutralButton("Ban", 
		    			new DialogInterface.OnClickListener() {
		    			    public void onClick(DialogInterface dialog,int id) {
		    			    	BanPlayerProcess ban = new BanPlayerProcess(PlayersActivity.this, server[2], server[3], server[4], playerid, dialog);
		    			    	ban.execute();
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
	
	private class KickPlayerProcess extends AsyncTask<String, Void, Boolean> {
		private PlayersActivity activity = null;
		private String serverIP = null;
		private String serverPort = null;
		private String rconPassword = null;
		private String playerID = null;
		private DialogInterface dialog = null;
		private String toastMessage = null;
		
		public KickPlayerProcess(PlayersActivity activity, String serverIP, String serverPort, String rconPassword, String playerID, DialogInterface dialog) {
			this.activity = activity;
			this.serverIP = serverIP;
			this.serverPort = serverPort;
			this.rconPassword = rconPassword;
			this.playerID = playerID;
			this.dialog = dialog;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
            	SampQuery query = new SampQuery(serverIP, Integer.parseInt(serverPort), rconPassword);
				if(query.isValidRconPassword()) {
					if(query.sendRconCommand("kick " + playerID)) {
						this.toastMessage = "Player kicked !";
					}
					else {
						this.toastMessage = "Couldn't kick the player...";
					}
				}
				else {
					this.toastMessage = "Invalid password.";	
				}
				query.socketClose();	
        	}
        	catch (Exception e) {
        		this.toastMessage = "Couldn't send the command...";
        	}
			return true;
		}
	 
	    @Override
	    protected void onPostExecute (Boolean result) {
	    	Toast.makeText(this.activity, toastMessage, Toast.LENGTH_SHORT).show();
		    dialog.cancel();
			PlayersProcess players = new PlayersProcess(this.activity);
			players.execute();
	    }
	}
	
	private class BanPlayerProcess extends AsyncTask<String, Void, Boolean> {
		private PlayersActivity activity = null;
		private String serverIP = null;
		private String serverPort = null;
		private String rconPassword = null;
		private String playerID = null;
		private DialogInterface dialog = null;
		private String toastMessage = null;
		
		public BanPlayerProcess(PlayersActivity activity, String serverIP, String serverPort, String rconPassword, String playerID, DialogInterface dialog) {
			this.activity = activity;
			this.serverIP = serverIP;
			this.serverPort = serverPort;
			this.rconPassword = rconPassword;
			this.playerID = playerID;
			this.dialog = dialog;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
            	SampQuery query = new SampQuery(serverIP, Integer.parseInt(serverPort), rconPassword);
				if(query.isValidRconPassword()) {
					if(query.sendRconCommand("ban " + playerID)) {
						this.toastMessage = "Player banned !";
					}
					else {
						this.toastMessage = "Couldn't ban the player...";
					}
				}
				else {
					this.toastMessage = "Invalid password.";	
				}
				query.socketClose();	
        	}
        	catch (Exception e) {
        		this.toastMessage = "Couldn't send the command...";
        	}
			return true;
		}
	 
	    @Override
	    protected void onPostExecute (Boolean result) {
	    	Toast.makeText(this.activity, toastMessage, Toast.LENGTH_SHORT).show();
		    dialog.cancel();
			PlayersProcess players = new PlayersProcess(this.activity);
			players.execute();
	    }
	}
}
