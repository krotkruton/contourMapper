package com.kruton.geocoder.utils;

public class Debug {

	public static enum LEVEL {
		NONE (0),
		IMPORTANT (1),
		LOW (2),
		MEDIUM (3),
		HIGH (4),
		ALL (5);
		
		private final int level;
		LEVEL(int level) {
			this.level = level;
		}
		
		public int getLevel() {
			return level;
		}
		
	}
	
}
