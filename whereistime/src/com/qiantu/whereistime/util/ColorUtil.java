package com.qiantu.whereistime.util;

import java.util.Random;

import android.graphics.Color;

public class ColorUtil {
	public static int getRandomColor() {
		
		Random ran = new Random();
		int r = ran.nextInt(256);//[0,255]
		int g = ran.nextInt(256);
		int b = ran.nextInt(256);
		
		return Color.rgb(r, g, b);
	}
}













