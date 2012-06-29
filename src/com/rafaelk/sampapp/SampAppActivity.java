/**
 * SA-MP App - Query and RCON Application
 * 
 * @author 		Rafael 'R@f' Keramidas <rafael@keramid.as>
 * @version		0.2.1 Beta
 * @date		30th June 2012
 * @licence		GPLv3
 * @thanks		StatusRed : Took example of this query class code for the v0.2.0.
 * 				Sasuke78200 : Some help with the first query class (v0.1.x).
 * 				Woothemes.com : In app icons (tabs and menu).
 * 				TheOriginalTwig : App icon.
 */

package com.rafaelk.sampapp;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

public class SampAppActivity extends TabActivity {
	public static final String PREFS_PRIVATE = "SampAppPref";
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();  
        TabHost.TabSpec spec; 
        Intent intent; 

        intent = new Intent().setClass(this, InfosActivity.class);
        spec = tabHost.newTabSpec("infos").setIndicator("Infos", res.getDrawable(R.drawable.tab_infos)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, RulesActivity.class);
        spec = tabHost.newTabSpec("rules").setIndicator("Rules", res.getDrawable(R.drawable.tab_rules)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PlayersActivity.class);
        spec = tabHost.newTabSpec("players").setIndicator("Players", res.getDrawable(R.drawable.tab_players)).setContent(intent);
        tabHost.addTab(spec);

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
	            this.rconDialog();
	        	return true;
	 
	        case R.id.menu_about:
	            Toast.makeText(SampAppActivity.this, "SA-MP App V 0.2.0 beta\n\nThis application has been developped by Rafael 'R@f' Keramidas.\n\nThanks: StatusRed (Inspiration of his query class), Sasuke78200 (Help with query class), woothemes.com (In-app icons) and TheOriginalTwig (App icon).", Toast.LENGTH_LONG).show();
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
    
    private void rconDialog() {
    	SharedPreferences sp = this.getSharedPreferences(SampAppActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
    	final int serverid = sp.getInt("serverid", 1);
    	try {
    		DatabaseHandler db = new DatabaseHandler(this);
        	final String[] server = db.getServer(serverid);
        	if(!server[3].equals("")) {
	        	LayoutInflater li = LayoutInflater.from(SampAppActivity.this);
	    		View rconconsoleView = li.inflate(R.layout.rconconsole, null);
	
	    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SampAppActivity.this);
	    		alertDialogBuilder.setView(rconconsoleView);
	
	    		final EditText rconcommand = (EditText) rconconsoleView.findViewById(R.id.editText1);
	
	    		alertDialogBuilder
	    			.setTitle("RCON Console")
	    			.setCancelable(false)
	    			.setPositiveButton("Send",
	    			new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog,int id) {
	    					try {
	    						SampQuery query = new SampQuery(server[2], Integer.parseInt(server[3]), server[4]);
	    						if(query.sendRconCommand(rconcommand.getText().toString())) {
	    							Toast.makeText(SampAppActivity.this, "Command sent !", Toast.LENGTH_SHORT).show();
	    						}
	    						else {
	    							Toast.makeText(SampAppActivity.this, "Couldn't send the command...", Toast.LENGTH_SHORT).show();
	    						}
	    					}
	    					catch(Exception e) {
	    						Toast.makeText(SampAppActivity.this, "Couldn't send the command...", Toast.LENGTH_SHORT).show();
	    					}
	    					
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
        	else {
        		Toast.makeText(SampAppActivity.this, "RCON Password is empty.", Toast.LENGTH_SHORT).show();	
        	}
    	}
    	catch(Exception e) {
    		Toast.makeText(SampAppActivity.this, "No server selected.", Toast.LENGTH_SHORT).show();	
    	}
    }
    
    private void serverDialogList() {
    	DatabaseHandler db = new DatabaseHandler(this);
    	if(db.getServerCount() != 0) {
			final String[][] servers = db.getAllServers();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SampAppActivity.this);
			alertDialogBuilder
				.setTitle("Servers")
		        .setItems(servers[1], new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int whichButton) {
		        		TabHost tabHost = getTabHost();
		        		SharedPreferences prefsPrivate = SampAppActivity.this.getSharedPreferences(SampAppActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
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
    		Toast.makeText(SampAppActivity.this, "No servers.", Toast.LENGTH_SHORT).show();
	    }
    }
    
    private void addServerDialog() {
    	LayoutInflater li = LayoutInflater.from(SampAppActivity.this);
		View addeditdialogView = li.inflate(R.layout.addeditdialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SampAppActivity.this);
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
					DatabaseHandler db = new DatabaseHandler(SampAppActivity.this);
			    	Toast.makeText(SampAppActivity.this, "Server " + alias.getText().toString() + " added !", Toast.LENGTH_SHORT).show();
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
		SharedPreferences sp = this.getSharedPreferences(SampAppActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
    	final int serverid = sp.getInt("serverid", 1);
    	try {
    		DatabaseHandler db = new DatabaseHandler(this);
        	String[] server = db.getServer(serverid);
			LayoutInflater li = LayoutInflater.from(SampAppActivity.this);
			View addeditdialogView = li.inflate(R.layout.addeditdialog, null);
	
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SampAppActivity.this);
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
						DatabaseHandler db = new DatabaseHandler(SampAppActivity.this);
						Toast.makeText(SampAppActivity.this, "Server " + alias.getText().toString() + " edited !", Toast.LENGTH_SHORT).show();
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
    		Toast.makeText(SampAppActivity.this, "No server to edit.", Toast.LENGTH_SHORT).show();	
    	}
	}
	
	private void deleteServerDialog() {
		final DatabaseHandler db = new DatabaseHandler(SampAppActivity.this);
		
		if(db.getServerCount() != 0) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			
			alertDialogBuilder
				.setTitle("Delete this server")
				.setMessage("Are you sure ?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						TabHost tabHost = getTabHost();
						SharedPreferences sp = SampAppActivity.this.getSharedPreferences(SampAppActivity.PREFS_PRIVATE, Context.MODE_PRIVATE);
				    	int serverid = sp.getInt("serverid", 1);
				    	db.deleteServer(serverid);
						Toast.makeText(SampAppActivity.this, "Server deleted !", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(SampAppActivity.this, "Add some server first before trying to delete them...", Toast.LENGTH_SHORT).show();
		}
	}
}