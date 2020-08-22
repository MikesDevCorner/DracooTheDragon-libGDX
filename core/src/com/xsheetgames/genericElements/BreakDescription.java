package com.xsheetgames.genericElements;

public class BreakDescription implements Comparable<BreakDescription> {
	public float ctime;
	public String message;
	
	
	@Override
	public int compareTo(BreakDescription b) {
		if(this.ctime < b.ctime) return 1;
		else if(this.ctime > b.ctime) return -1;
		else return 0;
	}
}
