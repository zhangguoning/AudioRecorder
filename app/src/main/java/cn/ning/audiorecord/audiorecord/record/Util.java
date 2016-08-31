package cn.ning.audiorecord.audiorecord.record;

public class Util {

public static String formatTime(int second){
		
		int min = second/60;
		int sec = second%60 ;
		return String.format("%02d", min) + ":" + String.format("%02d", sec);
		
	}
}
