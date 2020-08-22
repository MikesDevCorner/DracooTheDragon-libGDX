package com.xsheetgames.genericElements;

public class PowerupDescription implements Comparable<PowerupDescription> {
	public float ctime, x, y;
	
	@Override
	public int compareTo(PowerupDescription b) {
		if(this.ctime < b.ctime) return 1;
		else if(this.ctime > b.ctime) return -1;
		else return 0;
	}
}
