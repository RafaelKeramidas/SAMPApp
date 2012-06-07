/**
 * SA-MP App - Query and RCON Application
 * 
 * @author 		Rafael 'R@f' Keramidas <rafael@keramid.as>
 * @version		0.1.1 Beta
 * @date		1st June 2012
 * @licence		GPLv3
 * @thanks		Help with Query Class : Sasuke78200
 * 				Icons : woothemes.com - App icon : TheOriginalTwig
 */

package com.rafaelk.sampapp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class SampQuery {
	private InetAddress serverip = null;
	private int serverport = 0;
	private String serverrcon = null;
	private boolean serverstatus = false;
	private DatagramPacket packet = null;
	private DatagramSocket socket = null;
	
	public SampQuery(String srvip, int srvport, String srvrcon) {
		try {
			this.serverip = InetAddress.getByName(srvip);
			this.serverport = srvport;
			this.serverrcon = srvrcon;
			
			byte[] pkt = this.craftPacket('p');
			
			this.socket = new DatagramSocket();
			this.packet = new DatagramPacket(pkt, 15);
			this.packet.setAddress(this.serverip);
			this.packet.setPort(this.serverport);
			
			this.socket.send(this.packet);
			this.socket.setSoTimeout(500);
			
			this.socket.receive(this.packet);
			
			socket.close();
			
			this.serverstatus = true;
		}
		catch(Exception e) {
			this.serverstatus = false;
		}
	}
	
	public boolean isOnline() {
		return this.serverstatus;
	}
	
	public String[] getInfos() {
		String[] infos = new String[6];
		
		try {
			int hostnamelength = 0;
			int gamemodelength = 0;
			int mapnamelength = 0;
			byte[] pkt = this.craftPacket('i');
			
			this.socket = new DatagramSocket();
			this.packet = new DatagramPacket(pkt, 128);
			this.packet.setAddress(this.serverip);
			this.packet.setPort(this.serverport);
			
			this.socket.send(this.packet);
			this.socket.setSoTimeout(1000);
			
			this.socket.receive(this.packet);
			
			if(Integer.valueOf(pkt[11]) == 0)
				infos[0] = "No";
			else
				infos[0] = "Yes";
			
			infos[1] = String.valueOf((this.unsignedByteToInt(pkt[13]) << 8) + this.unsignedByteToInt(pkt[12]));
			infos[2] = String.valueOf((this.unsignedByteToInt(pkt[15]) << 8) + this.unsignedByteToInt(pkt[14]));
			hostnamelength = (int) pkt[16] + (int) pkt[17] + (int) pkt[18] + (int) pkt[19];
			gamemodelength = (int) pkt[20 + hostnamelength] + (int) pkt[21 + hostnamelength] + (int) pkt[22 + hostnamelength] + (int) pkt[23 + hostnamelength];
			mapnamelength = (int) pkt[24 + hostnamelength + gamemodelength] + (int) pkt[25 + hostnamelength + gamemodelength] + (int) pkt[26 + hostnamelength + gamemodelength] + (int) pkt[27 + hostnamelength + gamemodelength];
			
			infos[3] = "";
			for(int i = 0; i < hostnamelength; i++)
				infos[3] += String.valueOf((char) pkt[20+i]);
			
			infos[4] = "";
			for(int i = 0; i < gamemodelength; i++)
				infos[4] += String.valueOf((char) pkt[24+hostnamelength+i]);
			
			infos[5] = "";
			for(int i = 0; i < mapnamelength; i++)
				infos[5] += String.valueOf((char) pkt[28+hostnamelength+gamemodelength+i]);
			
			socket.close();
		}
		catch(Exception e) {
			this.serverstatus = false;
			infos = null;
		}
			
		return infos;
	}
	
	public String[][] getRules() {
		try {
			int rulescount = 0;
			int currentrulenamelength = 0;
			byte[] pkt = this.craftPacket('r');
			
			this.socket = new DatagramSocket();
			this.packet = new DatagramPacket(pkt, 256);
			this.packet.setAddress(this.serverip);
			this.packet.setPort(this.serverport);
			
			this.socket.send(this.packet);
			this.socket.setSoTimeout(1000);
			
			this.socket.receive(this.packet);
			
			rulescount = (int) pkt[11] + (int) pkt[12];			
			String[][] rules = new String[2][rulescount];
			for(int i = 0; i < rulescount; i++) {
				int rulenamelength = (int) pkt[13 + currentrulenamelength];
				int rulevaluelength = pkt[14 + currentrulenamelength + rulenamelength];
				
				rules[0][i] = "";
				for(int x = 0; x < rulenamelength; x++)
					rules[0][i] += String.valueOf((char) pkt[14 + currentrulenamelength + x]);
				
				rules[1][i] = "";
				for(int x = 0; x < rulevaluelength; x++)
					rules[1][i] += String.valueOf((char) pkt[15 + currentrulenamelength + rulenamelength + x]);
				
				currentrulenamelength = currentrulenamelength + rulenamelength + rulevaluelength + 2;
			}
			
			return rules;
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public String[][] getPlayers() {
		try {
			int playercount = 0;
			int currentplayerlength = 0;
			byte[] pkt = this.craftPacket('d');
			
			this.socket = new DatagramSocket();
			this.packet = new DatagramPacket(pkt, 3500);
			this.packet.setAddress(this.serverip);
			this.packet.setPort(this.serverport);
			
			this.socket.send(this.packet);
			this.socket.setSoTimeout(1500);
			
			this.socket.receive(this.packet);
			
			playercount = (int) pkt[11] + (int) pkt[12];
			String[][]players = new String[4][playercount];
			for(int i = 0; i < playercount; i++) {
				players[0][i] = String.valueOf((int) pkt[13 + currentplayerlength]);
				int playernamelength = (int) pkt[14 + currentplayerlength];
				players[1][i] = "";
				for(int x = 0; x < playernamelength; x++) {
					players[1][i] += String.valueOf((char) pkt[15 + currentplayerlength + x]);
				}
				
				/* NOT WORKING, CURRENTLY WORKING ON */
				players[2][i] = String.valueOf(this.unsignedByteToInt(pkt[16 + currentplayerlength]) + this.unsignedByteToInt(pkt[17 + currentplayerlength]) + this.unsignedByteToInt(pkt[18 + currentplayerlength]) + this.unsignedByteToInt(pkt[19 + currentplayerlength]));
				players[3][i] = String.valueOf((int) pkt[20 + currentplayerlength] + (int) pkt[21 + currentplayerlength] + (int) pkt[22 + currentplayerlength] + (int) pkt[23 + currentplayerlength]);
				
				currentplayerlength += playernamelength + 10;
			}
			
			return players;
		}
		catch(Exception e) {
			return null;
		}
	}
	
	private byte[] craftPacket(char type) {
		byte[] pkt = new byte[3500];
		byte[] IP = this.serverip.getAddress();
		
		pkt[0] = 'S';
		pkt[1] = 'A';
		pkt[2] = 'M';
		pkt[3] = 'P';
		
		pkt[4] = IP[0];
		pkt[5] = IP[1];
		pkt[6] = IP[2];
		pkt[7] = IP[3];
		
		pkt[8] = (byte) (this.serverport & 0xFF);
		pkt[9] = (byte) ((this.serverport >> 8) & 0xFF);
		
		/* PING */
		if(type == 'p') {
			pkt[10] = 'p';
			
			pkt[11] = '1';
			pkt[12] = '2';
			pkt[14] = '3';
			pkt[15] = '4';
		}
		/* INFO */
		else if(type == 'i') {
			pkt[10] = 'i';	
		}
		/* RULES */
		else if(type == 'r') {
			pkt[10] = 'r';
		}
		/* PLAYERS */
		else if(type == 'd') {
			pkt[10] = 'd';
		}
		/* RCON NOT FINISHED */
		else if(type == 'x') {
			pkt[10] = 'x';
			
			pkt[11] = (byte) (this.serverrcon.length() & 0xFF);
			pkt[12] = (byte) ((this.serverrcon.length() >> 8) & 0xFF);
		}
		else {
			pkt[10] = 'p';
		}
		
		return pkt;
	}
	
	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
}
