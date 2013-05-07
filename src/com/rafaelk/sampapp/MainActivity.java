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
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	public static final String PREFS_PRIVATE = "SampAppPref";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Resources ressources = getResources(); 
		TabHost tabHost = getTabHost(); 
 
		// Android tab
		Intent intentInfos = new Intent().setClass(this, InfosActivity.class);
		TabSpec tabSpecInfos = tabHost
			.newTabSpec("Infos")
			.setIndicator("Infos", ressources.getDrawable(R.drawable.tab_infos))
			.setContent(intentInfos);
 
		Intent intentRules = new Intent().setClass(this, RulesActivity.class);
		TabSpec tabSpecRules = tabHost
			.newTabSpec("Rules")
			.setIndicator("Rules", ressources.getDrawable(R.drawable.tab_rules))
			.setContent(intentRules);
 
		Intent intentPlayers = new Intent().setClass(this, PlayersActivity.class);
		TabSpec tabSpecPlayers = tabHost
			.newTabSpec("Players")
			.setIndicator("Players", ressources.getDrawable(R.drawable.tab_players))
			.setContent(intentPlayers);
		
		tabHost.addTab(tabSpecInfos);
		tabHost.addTab(tabSpecRules);
		tabHost.addTab(tabSpecPlayers);
 
		tabHost.setCurrentTab(0);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        	case R.id.menu_servers:
        		this.serverDialogList();
        		return true;
 
	        case R.id.menu_rcon:
	        	SharedPreferences sp = this.getSharedPreferences(MainActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
	        	final int serverid = sp.getInt("serverid", 1);
	        	try {
	        		DatabaseHandler db = new DatabaseHandler(this);
	            	String[] server = db.getServer(serverid);
	            	OpenRconConsole rconConsole = new OpenRconConsole(MainActivity.this, server[2], server[3], server[4]);
	            	rconConsole.execute();
	        	}
	        	catch (Exception e) {
	        		Log.d("SAMPAPP", e.toString());
	        		Toast.makeText(MainActivity.this, "No server selected.", Toast.LENGTH_SHORT).show();	
	        	}
	        	return true;
	 
	        case R.id.menu_about:
	        	Intent intent = new Intent(MainActivity.this, AboutActivity.class);
	        	MainActivity.this.startActivity(intent);
	        	return true;
	 
	        case R.id.menu_add:
	            this.addServerDialog();
	            return true;
	 
	        case R.id.menu_edit:
	            this.editServerDialog();
	        	return true;
	 
	        case R.id.menu_delete:
	        	this.deleteServerDialog();
	            return true;
	 
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    private class OpenRconConsole extends AsyncTask<String, Void, Boolean> {
    	private MainActivity activity = null;
    	private String serverIP = null;
    	private String serverPort = null;
    	private String rconPassword = null;
    	private String errorMsg = null;
    	
    	public OpenRconConsole(MainActivity activity, String serverIP, String serverPort, String rconPassword) {
    		this.activity = activity;
    		this.serverIP = serverIP;
    		this.serverPort = serverPort;
    		this.rconPassword = rconPassword;
    	}

		@Override
		protected Boolean doInBackground(String... params) {
			SampQuery query = new SampQuery(this.serverIP, Integer.parseInt(this.serverPort), this.rconPassword);
			if(!query.isValidRconPassword()) {
				this.errorMsg = "Invalid password.";
			}
			query.socketClose();
			return true;
		}
		
		@Override
		protected void onPostExecute (Boolean result) {
			if(errorMsg == null) {
				rconDialog();
			}
			else {
				Toast.makeText(this.activity, this.errorMsg, Toast.LENGTH_SHORT).show();	
			}
		}
    	
    }
    
    private class SendRconCommand extends AsyncTask<String, Void, Boolean> {
    	private MainActivity activity = null;
    	private String serverIP = null;
    	private String serverPort = null;
    	private String rconPassword = null;
    	private String rconCommand = null;
    	private DialogInterface dialog = null;
    	private String toastMessage = null;
    	
    	public SendRconCommand(MainActivity activity, String serverIP, String serverPort, String rconPassword, String rconCommand, DialogInterface dialog) {
    		this.activity = activity;
    		this.serverIP = serverIP;
    		this.serverPort = serverPort;
    		this.rconPassword = rconPassword;
    		this.rconCommand = rconCommand;
    		this.dialog = dialog;
    	}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				SampQuery query = new SampQuery(this.serverIP, Integer.parseInt(this.serverPort), this.rconPassword);
				if(query.sendRconCommand(this.rconCommand)) {
					toastMessage = "Command sent !";
				}
				else {
					toastMessage = "Couldn't send the command...";
				}
				query.socketClose();
			}
			catch(Exception e) {
				toastMessage = "Couldn't send the command...";
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute (Boolean result) {
			Toast.makeText(this.activity, this.toastMessage, Toast.LENGTH_SHORT).show();
			dialog.cancel();
		}
    	
    }
    
    private void rconDialog() {
    	SharedPreferences sp = this.getSharedPreferences(MainActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
    	final int serverid = sp.getInt("serverid", 1);
    	try {
    		DatabaseHandler db = new DatabaseHandler(this);
        	final String[] server = db.getServer(serverid);
        	LayoutInflater li = LayoutInflater.from(MainActivity.this);
    		View rconconsoleView = li.inflate(R.layout.rconconsole, null);

    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
    		alertDialogBuilder.setView(rconconsoleView);

    		final EditText rconcommand = (EditText) rconconsoleView.findViewById(R.id.editText1);

    		alertDialogBuilder
    			.setTitle("RCON Console")
    			.setCancelable(false)
    			.setPositiveButton("Send",
    			new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog,int id) {
    					SendRconCommand rconCmd = new SendRconCommand(MainActivity.this, server[2], server[3], server[4], rconcommand.getText().toString(), dialog);
    					rconCmd.execute();
    				}
    		  	})
    			.setNegativeButton("Cancel",
    			new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog,int id) {
    			    	dialog.cancel();
    			    }
    			});

    		AlertDialog alertDialog = alertDialogBuilder.create();
    		alertDialog.show();
    	}
    	catch(Exception e) {
    		Toast.makeText(MainActivity.this, "No server selected.", Toast.LENGTH_SHORT).show();	
    	}
    }
    
    private void serverDialogList() {
    	DatabaseHandler db = new DatabaseHandler(this);
    	if(db.getServerCount() != 0) {
			final String[][] servers = db.getAllServers();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
			alertDialogBuilder
				.setTitle("Servers")
		        .setItems(servers[1], new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int whichButton) {
		        		TabHost tabHost = getTabHost();
		        		SharedPreferences prefsPrivate = MainActivity.this.getSharedPreferences(MainActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
						Editor prefsPrivateEditor = prefsPrivate.edit();
						prefsPrivateEditor.putInt("serverid", Integer.parseInt(servers[0][whichButton]));
						prefsPrivateEditor.commit();
						tabHost.setCurrentTab(1);
						tabHost.setCurrentTab(0);
						dialog.cancel();
		        	}
		        });
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
    	}
    	else {
    		Toast.makeText(MainActivity.this, "No servers.", Toast.LENGTH_SHORT).show();
	    }
    }
    
    private void addServerDialog() {
    	LayoutInflater li = LayoutInflater.from(MainActivity.this);
		View addeditdialogView = li.inflate(R.layout.addeditdialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
		alertDialogBuilder.setView(addeditdialogView);

		final EditText alias = (EditText) addeditdialogView.findViewById(R.id.editText1);
		final EditText ip = (EditText) addeditdialogView.findViewById(R.id.editText01);
		final EditText port = (EditText) addeditdialogView.findViewById(R.id.editText02);
		final EditText rcon = (EditText) addeditdialogView.findViewById(R.id.editText03);

		alertDialogBuilder
			.setTitle("Add server")
			.setCancelable(false)
			.setPositiveButton("OK",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					TabHost tabHost = getTabHost();
					DatabaseHandler db = new DatabaseHandler(MainActivity.this);
			    	Toast.makeText(MainActivity.this, "Server " + alias.getText().toString() + " added !", Toast.LENGTH_SHORT).show();
			    	db.addServer(alias.getText().toString(), ip.getText().toString(), port.getText().toString(), rcon.getText().toString());
			    	tabHost.setCurrentTab(1);
					tabHost.setCurrentTab(0);
					dialog.cancel();
				}
		  	})
			.setNegativeButton("Cancel",
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	dialog.cancel();
			    }
			});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
    
	private void editServerDialog() {
		SharedPreferences sp = this.getSharedPreferences(MainActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
    	final int serverid = sp.getInt("serverid", 1);
    	try {
    		DatabaseHandler db = new DatabaseHandler(this);
        	String[] server = db.getServer(serverid);
			LayoutInflater li = LayoutInflater.from(MainActivity.this);
			View addeditdialogView = li.inflate(R.layout.addeditdialog, null);
	
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
			alertDialogBuilder.setView(addeditdialogView);
	
			final EditText alias = (EditText) addeditdialogView.findViewById(R.id.editText1);
			final EditText ip = (EditText) addeditdialogView.findViewById(R.id.editText01);
			final EditText port = (EditText) addeditdialogView.findViewById(R.id.editText02);
			final EditText rcon = (EditText) addeditdialogView.findViewById(R.id.editText03);
			
			alias.setText(server[1]);
			ip.setText(server[2]);
			port.setText(server[3]);
			rcon.setText(server[4]);
	
			alertDialogBuilder
				.setTitle("Edit server")
				.setCancelable(false)
				.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						TabHost tabHost = getTabHost();
						DatabaseHandler db = new DatabaseHandler(MainActivity.this);
						Toast.makeText(MainActivity.this, "Server " + alias.getText().toString() + " edited !", Toast.LENGTH_SHORT).show();
				    	db.updateServer(serverid, alias.getText().toString(), ip.getText().toString(), port.getText().toString(), rcon.getText().toString());
				    	tabHost.setCurrentTab(1);
						tabHost.setCurrentTab(0);
					}
			  	})
				.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
				    	dialog.cancel();
				    }
				});
	
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
    	}
    	catch(Exception e) {
    		Toast.makeText(MainActivity.this, "No server to edit.", Toast.LENGTH_SHORT).show();	
    	}
	}
	
	private void deleteServerDialog() {
		final DatabaseHandler db = new DatabaseHandler(MainActivity.this);
		
		if(db.getServerCount() != 0) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			
			alertDialogBuilder
				.setTitle("Delete this server")
				.setMessage("Are you sure ?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						TabHost tabHost = getTabHost();
						SharedPreferences sp = MainActivity.this.getSharedPreferences(MainActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
				    	int serverid = sp.getInt("serverid", 1);
				    	db.deleteServer(serverid);
						Toast.makeText(MainActivity.this, "Server deleted !", Toast.LENGTH_SHORT).show();
				    	tabHost.setCurrentTab(1);
						tabHost.setCurrentTab(0);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
	 
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
		}
		else {
			Toast.makeText(MainActivity.this, "Add some server first before trying to delete them...", Toast.LENGTH_SHORT).show();
		}
	}

}
