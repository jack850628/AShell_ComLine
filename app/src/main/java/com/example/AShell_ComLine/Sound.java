package com.example.AShell_ComLine;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
/**
 *音效類別
 *@param 這是專門處理音效的類別
 *音效指令的型態為：\a或\a<>，<>中可以是c、d、e、f、g、a、b加上1、2、3，例如：\a<c3>發出來的聲音為ㄉㄡ **/
public class Sound {
	public static SoundPool SP = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	public static int c2,d2,e2,f2,g2,a2,b2,c3,d3,e3,f3,g3,a3,b3,c4,d4,e4,f4,g4,a4,b4;
	public static void setSound(Context context){
		c2=SP.load(context, R.raw.c2 , 1);
		d2=SP.load(context, R.raw.d2 , 1);
		e2=SP.load(context, R.raw.e2 , 1);
		f2=SP.load(context, R.raw.f2 , 1);
		g2=SP.load(context, R.raw.g2 , 1);
		a2=SP.load(context, R.raw.a2 , 1);
		b2=SP.load(context, R.raw.b2 , 1);
		c3=SP.load(context, R.raw.c3 , 1);
		d3=SP.load(context, R.raw.d3 , 1);
		e3=SP.load(context, R.raw.e3 , 1);
		f3=SP.load(context, R.raw.f3 , 1);
		g3=SP.load(context, R.raw.g3 , 1);
		a3=SP.load(context, R.raw.a3 , 1);
		b3=SP.load(context, R.raw.b3 , 1);
		c4=SP.load(context, R.raw.c4 , 1);
		d4=SP.load(context, R.raw.d4 , 1);
		e4=SP.load(context, R.raw.e4 , 1);
		f4=SP.load(context, R.raw.f4 , 1);
		g4=SP.load(context, R.raw.g4 , 1);
		a4=SP.load(context, R.raw.a4 , 1);
		b4=SP.load(context, R.raw.b4 , 1);
	}
}
