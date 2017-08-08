package com.example.AShell_ComLine;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AShell.AShell;
import com.example.AShell.CommandResolve.Command;
import com.example.AShell.CommandResolve.CommandArray;
import com.example.AShell.CommandResolve.StringScan;
import com.example.AShell.Data_Type_And_Struct.Value_Array;
import com.example.AShell.InterFact.*;

public class AShell_ComLine extends AppCompatActivity {
	LinearLayout ll;
	View EV;
	Eview et;
	TextView tv;
	ScrollView sv;
	Button eb;
	boolean External_Boot=false;
	/**
	 * 讀取類型
	 *  Read 讀取變數
	 *  DeleteFolder 刪除資料夾
	 *  XDeleteFolder 超級刪除資料夾
	 *  IFRead IF迴圈
	 *  WHRead WHILE迴圈
	 *  DWHRead DOWHILE迴圈
	 */
	enum ReadType{Read,IFRead,WHRead,DWHRead,FORRead,FUNRead,CLARead,TRY,BEGIN};
	int tin=0;//退出程式用
	long time1,time2;//退出程式用
	int Color=android.graphics.Color.WHITE,Size=20;
	int i=0;//view數量
	Value_Array ValueArray=new Value_Array(null);
	Handler mHandler = new Handler();
	final int ViewCount=170;//畫面View的最大顯示數量
	InputMethodManager imm;
	OnClickListener OCL=new OnClickListener(){
		@Override
		public void onClick(View v) {
			et.requestFocus();
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	};
	Typeface Fonts;
	AShell AS;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acmd);
		Fonts=Typeface.createFromAsset(getAssets(),"fonts/mingliu.ttc");//字型(細明體)
		//Sound.setSound(this);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		sv=(ScrollView)findViewById(R.id.scrollView1);
		ll=(LinearLayout)findViewById(R.id.sll);
		EV=getLayoutInflater().inflate(R.layout.texte,null);
		LinearLayout llv=(LinearLayout)EV.findViewById(R.id.ll);
		Intent intent = getIntent();
		External_Boot=intent.getBooleanExtra("External_Boot", false);
		AS=new AShell(this,
				new Print(){
					public void Print(final String Str, Thread r) {
						Log.i("Print指令", Str);
						mHandler.post(new Runnable(){
							@Override
							public void run() {
								AShell_ComLine.this.Print(Str);
							}
						});
					}
				},
				new Clear(){
					@Override
					public void Clear() {
						mHandler.post(new Runnable(){
							@Override
							public void run() {
								ll.removeAllViews();
								i=0;
							}
						});
					}
				},
				new Read(){
					@Override
					public String Rand(final int mode) {
						// TODO Auto-generated method stub
						et.setRun(this);
						mHandler.post(new Runnable(){
							@Override
							public void run() {
								if(mode==0){
									et.RT=ReadType.Read;
									et.read=true;
								}else
									et.pause=1;
								tv.setText("");
								et.Recovery();
							}
						});
						Log.i("Read:", "開始"+mode);
						//Log.i("this",this.toString());
						synchronized(this){
							try {
								this.wait();
							} catch (InterruptedException e) {
								Log.e("暫停錯誤", ""+e.getMessage());
							}
						}
						Log.i("Read:", "結束"+mode);
						/*mHandler.post(new Runnable(){
							@Override
							public void run() {
								ll.removeViewAt(i);
							}
						});*/
						return et.ReadTemp;
					}
				},
				new Stop(){
					@Override
					public void Stop() {
						System.exit(0);
					}

				},
				intent.getStringArrayExtra("Args"));
		if(!External_Boot){
			Print("    ___   _____ __         ____\n"
					+"   /   | / ___// /_  ___  / / /\n"
					+"  / /| | \\__ \\/ __ \\/ _ \\/ / /\n"
					+" / ___ |___/ / / / /  __/ / /\n"
					+"/_/  |_/____/_/ /_/\\___/_/_/ ");
			Print("歡迎使用"+getString(R.string.ASName)+"[版本"+getString(R.string.ver)+" Powrered by AShell "+AS.getVar()+"]");
			Print("如果不知道如何使用，可以輸入'HELP'指令來查看用法:)");
			Print("");
		}
		et=new Eview(this);
		et.setTextColor(android.graphics.Color.WHITE);
		eb=new Button(this);
		eb.setText("Enter");
		eb.setTextColor(android.graphics.Color.WHITE);
		eb.setVisibility(View.GONE);
		eb.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				et.onKeyDown(KeyEvent.KEYCODE_ENTER,null);
			}
		});
		llv.addView(et);
		llv.addView(eb);
		tv=(TextView)EV.findViewById(R.id.textView1);
		tv.setTypeface(Fonts);
		tv.setText(">>>");
		tv.setTextColor(android.graphics.Color.WHITE);
		tv.setOnClickListener(OCL);
		ll.setOnClickListener(OCL);
		//((LinearLayout)findViewById(R.id.hsll)).setOnClickListener(OCL);
		//((LinearLayout)findViewById(R.id.mainll)).setOnClickListener(OCL);
		et.requestFocus();
		if(External_Boot){
			Uri data = intent.getData();
			File FireName=new File(data.getPath());
			setTitle(getString(R.string.app_name)+"("+FireName.getName()+")");
			AS.Start_Run(FireName);
		}else{
			ValueArray=AS.interactive_Mode_Start(new Stop() {
				@Override
				public void Stop() {
					mHandler.post(new Runnable(){
						@Override
						public void run() {
							et.Recovery();
						}
					});
				}
			}, Environment.getExternalStorageDirectory().toString());
			CommandArray al=new CommandArray();
			al.add(new Command(new StringBuilder("using Stdio.ash"),0));
			AS.ComLineRun(ValueArray,al);
			//ll.addView(EV);
		}

	}
	public void Print(String write){
		//顯示輸出資訊
		if(i>=ViewCount){
			ll.removeViewAt(0);
			i--;
		}
		TextView etv=new TextView(AShell_ComLine.this);
		etv.setTypeface(Fonts);
		etv.setText(write);
		etv.setTextSize(Size);
		etv.setTextColor(Color);
		etv.setOnClickListener(OCL);
		etv.setTextIsSelectable(true);
		ll.addView(etv);
		etv.requestFocus();
		if(i<=ViewCount)
			i++;//會++的原因是為了讓指標指向下一個輸入View
	}
	public void PrintValue(View write){
		//顯示輸出資訊
		if(i>=ViewCount){
			ll.removeViewAt(0);
			i--;
		}
		ll.addView(write);
		if(i<=ViewCount)
			i++;
	}
	class Eview extends android.support.v7.widget.AppCompatEditText{
		StringBuilder ValueNameOrPath;//用來作為Read變數名稱暫存和刪除檔案之路徑暫存
		CommandArray command =new CommandArray();//用來存放讀出的批次指令
		int TagCount=1;//用來紀錄多行輸入時的行數，起始值為1是因為迴圈開頭會再多行輸入開始前先存放到command陣列裡
		int LoopCount=0;//迴圈數量紀錄，0：迴圈開頭IF或WHILE，1：迴圈結尾ENDIF或ENDWH
		int comLine=0;
		/**@param 儲存讀取類型**/
		ReadType RT;
		boolean read=false;//讀取模式判別
		boolean Run_Continue=false;
		int pause=0;//暫停模式判別，1=getch 2=pause
		Object r;
		String ReadTemp;
		StringScan SS=new StringScan();
		public Eview(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		/**將執行續傳入Eview的方法
		 * @param r：現在執行的執行序**/
		public void setRun(Object r){
			this.r=r;
		}
		public void Recovery(){//將指令輸入條還原
			setText("");
			ll.addView(EV);
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					sv.fullScroll(ScrollView.FOCUS_DOWN);
					et.requestFocus();
				}
			});
			//Continue();
		}
		/**
		 * 繼續執行
		 ***/
		public void Continue(){
			ReadTemp=getText().toString();//用於Rean、getch須讓使用者輸入之函數，取得使用者所輸入之文字的暫存變數
			if(r!=null)
				//synchronized：執行續類別鎖，同步執行續用。
				synchronized(r){
					r.notify();
					Run_Continue=true;
				}
		}
		/**
		 * 復原/回到指令讀取狀態
		 ***/
		public void Re(){
			tv.setText(">>>");
			read=false;
		}
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event)
		{
			if(pause==1){
				try{
					super.onKeyDown(keyCode, event);//這個方法會將按下的文字儲存在EditText中
				}catch(Exception e){}
				ll.removeViewAt(i);
				pause=0;
				Re();
				//Recovery();
				Continue();
				return true;
			}else if(pause==2){
				ll.removeViewAt(i);
				Print(tv.getText().toString());
				pause=0;
				Re();
				//Recovery();
				Continue();
				return true;
			}else if (keyCode==KeyEvent.KEYCODE_ENTER){
				try{
					if(read){
						ll.removeViewAt(i);
						Print(tv.getText().toString()+getText());
						String[] cr;
						StringBuilder SB;
						switch(RT){
							case Read:
								Re();
								//Recovery();
								Continue();
								break;
							case BEGIN:
								setText(((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"");
								cr=getText().toString().split(" ");
								comLine++;
								if(cr[0].equals("begin")){//如果讀取到另外的IF指令時的動作
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount++;//這是為了避免使用者在輸入另外IF的ENDIF指令時誤判成第一個IF的ENDIF指令
									TagCount++;
								}else if(cr[0].equals("endbe")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount--;
									TagCount++;
								}else if(!cr[0].equals("")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									TagCount++;
								}
								if(LoopCount<=0){
									Re();
									AS.ComLineRun(ValueArray,command);
									command =new CommandArray();
									LoopCount=0;
									TagCount=1;
									comLine=0;
								}else
									Recovery();
								break;
							case IFRead:
								setText(((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"");
								cr=getText().toString().split(" ");
								comLine++;
								if(cr[0].equals("if")){//如果讀取到另外的IF指令時的動作
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount++;//這是為了避免使用者在輸入另外IF的ENDIF指令時誤判成第一個IF的ENDIF指令
									TagCount++;
								}else if(cr[0].equals("endif")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount--;
									TagCount++;
								}else if(!cr[0].equals("")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									TagCount++;
								}
								if(LoopCount<=0){
									Re();
									AS.ComLineRun(ValueArray,command);
									command =new CommandArray();
									LoopCount=0;
									TagCount=1;
									comLine=0;
								}else
									Recovery();
								break;
							case WHRead:
								setText(((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"");
								cr=getText().toString().split(" ");
								comLine++;
								if(cr[0].equals("while")){//如果讀取到另外的IF指令時的動作
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount++;//這是為了避免使用者在輸入另外IF的ENDIF指令時誤判成第一個IF的ENDIF指令
									TagCount++;
								}else if(cr[0].equals("endwh")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount--;
									TagCount++;
								}else if(!cr[0].equals("")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									TagCount++;
								}
								if(LoopCount<=0){
									Re();
									AS.ComLineRun(ValueArray,command);
									command =new CommandArray();
									LoopCount=0;
									TagCount=1;
									comLine=0;
								}else
									Recovery();
								break;
							case FORRead:
								setText(((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"");
								cr=getText().toString().split(" ");
								comLine++;
								if(cr[0].equals("for")){//如果讀取到另外的IF指令時的動作
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount++;//這是為了避免使用者在輸入另外IF的ENDIF指令時誤判成第一個IF的ENDIF指令
									TagCount++;
								}else if(cr[0].equals("endfo")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount--;
									TagCount++;
								}else if(!cr[0].equals("")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									TagCount++;
								}
								if(LoopCount<=0){
									Re();
									AS.ComLineRun(ValueArray,command);
									command =new CommandArray();
									LoopCount=0;
									TagCount=1;
									comLine=0;
								}else
									Recovery();
								break;
							case DWHRead:
								setText(((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"");
								cr=getText().toString().split(" ");
								comLine++;
								if(cr[0].equals("do")){//如果讀取到另外的IF指令時的動作
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount++;//這是為了避免使用者在輸入另外IF的ENDIF指令時誤判成第一個IF的ENDIF指令
									TagCount++;
								}else if(cr[0].equals("dwhile")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount--;
									TagCount++;
								}else if(!cr[0].equals("")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									TagCount++;
								}
								if(LoopCount<=0){
									Re();
									AS.ComLineRun(ValueArray,command);
									command =new CommandArray();
									LoopCount=0;
									TagCount=1;
									comLine=0;
								}else
									Recovery();
								break;
							case FUNRead:
								setText(((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"");
								cr=getText().toString().split(" ");
								comLine++;
								if(cr[0].equals("function")){//如果讀取到另外的IF指令時的動作
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount++;//這是為了避免使用者在輸入另外IF的ENDIF指令時誤判成第一個IF的ENDIF指令
									TagCount++;
								}else if(cr[0].equals("endfu")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount--;
									TagCount++;
								}else if(!cr[0].equals("")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									TagCount++;
								}
								if(LoopCount<=0){
									Re();
									AS.ComLineRun(ValueArray,command);
									command =new CommandArray();
									LoopCount=0;
									TagCount=1;
									comLine=0;
								}else
									Recovery();
								break;
							case CLARead:
								setText(((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"");
								cr=getText().toString().split(" ");
								if(cr[0].equals("class")){//如果讀取到另外的IF指令時的動作
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount++;//這是為了避免使用者在輸入另外IF的ENDIF指令時誤判成第一個IF的ENDIF指令
									TagCount++;
								}else if(cr[0].equals("endcl")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount--;
									TagCount++;
								}else if(!cr[0].equals("")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									TagCount++;
								}
								if(LoopCount<=0){
									Re();
									AS.ComLineRun(ValueArray,command);
									command =new CommandArray();
									LoopCount=0;
									TagCount=1;
									comLine=0;
								}else
									Recovery();
								break;
							case TRY:
								setText(((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"");
								cr=getText().toString().split(" ");
								comLine++;
								if(cr[0].equals("try")){//如果讀取到另外的IF指令時的動作
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount++;//這是為了避免使用者在輸入另外IF的ENDIF指令時誤判成第一個IF的ENDIF指令
									TagCount++;
								}else if(cr[0].equals("endtr")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									LoopCount--;
									TagCount++;
								}else if(!cr[0].equals("")){
									command.add(new Command(new StringBuilder(getText().toString()),comLine));
									TagCount++;
								}
								if(LoopCount<=0){
									Re();
									AS.ComLineRun(ValueArray,command);
									command =new CommandArray();
									LoopCount=0;
									TagCount=1;
									comLine=0;
								}else
									Recovery();
								break;
						}
					}else{
						ll.removeViewAt(i);
						Print(tv.getText().toString()+getText());
						//ll.removeViewAt(--i);//會--i的原因是因為原本指標指向了下一個輸入View，但是因為不要留下輸入紀錄，所以需要刪除紀錄View，所以先把指標移向紀錄View再刪除，等等因刪除紀錄View的空格就會被輸入View取代。
						StringBuilder SB;
						String Com=((SB=SS.StrBlankDeal_with(getText().toString()))!=null)?SB.toString():"";
						char ch=' ';
						try{
							ch=Com.charAt(0);
						}catch(Exception e){}
						// Just ignore the [Enter] key
						if(ch=='b'){
							if(StringScan.startsWith(Com,"break")){
								Print("break無法單獨使用，須和while一同使用。");
							}else if(StringScan.startsWith(Com,"begin")){
								read=true;
								RT=ReadType.BEGIN;
								tv.setText("...");
								command.add(new Command(new StringBuilder(Com),++comLine));
								LoopCount++;
							}else
								ch=' ';
						}else if(ch=='c'){
							if(StringScan.startsWith(Com,"continue")){
								Print("continue無法單獨使用，須和while一同使用。");
							}else if(Com.startsWith("class ")){
								read=true;
								RT=ReadType.CLARead;
								tv.setText("...");
								command.add(new Command(new StringBuilder(Com),++comLine));
								LoopCount++;
							}else
								ch=' ';
						}else if(ch=='d'){
							if(StringScan.startsWith(Com,"do")){
								read=true;
								RT=ReadType.DWHRead;
								tv.setText("...");
								command.add(new Command(new StringBuilder(Com),++comLine));
								LoopCount++;
							}else if(Com.startsWith("dwhile ")){
								Print("dwhile無法單獨使用，須和do一同使用。");
							}else
								ch=' ';
						}else if(ch=='e'){
							if(StringScan.startsWith(Com,"else")){
								Print("else無法單獨使用，須和if一同使用。");
							}else if(Com.startsWith("elseif ")){
								Print("elseif無法單獨使用，須和if一同使用。");
							}else if(Com.startsWith("endif ")){
								Print("endif無法單獨使用，須和if一同使用。");
							}else if(StringScan.startsWith(Com,"endwh")){
								Print("endwh無法單獨使用，須和while一同使用。");
							}else if(StringScan.startsWith(Com,"endcl")){
								Print("endcl無法單獨使用，須和class一同使用。");
							}else if(StringScan.startsWith(Com,"exit")){
								System.exit(0);
							}else
								ch=' ';
						}else if(ch=='f'){
							if(Com.startsWith("for ")){
								read=true;
								RT=ReadType.FORRead;
								tv.setText("...");
								command.add(new Command(new StringBuilder(Com),++comLine));
								LoopCount++;
							}else if(Com.startsWith("function ")){
								read=true;
								RT=ReadType.FUNRead;
								tv.setText("...");
								command.add(new Command(new StringBuilder(Com),++comLine));
								LoopCount++;
							}else
								ch=' ';
						}else if(ch=='h'){
							if(StringScan.startsWith(Com,"help")){
								Print(getString(R.string.Help));

							}else
								ch=' ';
						}else if(ch=='i'){
							if(Com.startsWith("if ")){
								read=true;
								RT=ReadType.IFRead;
								tv.setText("...");
								command.add(new Command(new StringBuilder(Com),++comLine));
								LoopCount++;
							}else
								ch=' ';
						}else if(ch=='r'){
							if(Com.startsWith("reset ")){
								Print("RESET只能在多行輸入模式中使用。");
							}else if(StringScan.startsWith(Com,"restart")){
								ReStart();
							}else
								ch=' ';
						}else if(ch=='t'){
							if(StringScan.startsWith(Com,"try")){
								read=true;
								RT=ReadType.TRY;
								tv.setText("...");
								command.add(new Command(new StringBuilder(Com),++comLine));
								LoopCount++;
							}else
								ch=' ';
						}else if(ch=='v'){
							if(StringScan.startsWith(Com,"ver")){
								Print(getString(R.string.ASName)+" [版本 "+getString(R.string.ver)+" Powrered by AShell "+AS.getVar()+"]");

							}else
								ch=' ';
						}else if(ch=='w'){
							if(Com.startsWith("while ")){
								read=true;
								RT=ReadType.WHRead;
								tv.setText("...");
								command.add(new Command(new StringBuilder(Com),++comLine));
								LoopCount++;
							}else
								ch=' ';
						}else if(Com.startsWith(":")){
							Print("標籤在一般模式下無法使用，只能在批次檔或多行輸入模式中使用。");
						}else
							ch=' ';
						if(ch==' '){
							Log.i("指令", Com);
							CommandArray ComArr=new CommandArray();
							ComArr.add(new Command(new StringBuilder(Com),1));
							AS.ComLineRun(ValueArray,ComArr);
						}else
							Recovery();

					}
				}catch(Exception e){
					Log.e("Error", Log.getStackTraceString(e));
					Print("錯誤！"+e.getMessage());
					Recovery();
					read=false;
				}
				return true;
			}
			// Handle all other keys in the default way
			return super.onKeyDown(keyCode, event);
		}
	}
	class directoryfilefilter implements FilenameFilter{
		int Number_of_Files=0,Number_of_Directory=0;
		@Override
		public boolean accept(File dir, String filename) {
			// TODO Auto-generated method stub
			if(new File(dir.toString()+"/"+filename).isDirectory())
				Number_of_Directory++;
			else
				Number_of_Files++;
			return true;
		}
	}
	public void ReStart(){
		AS.Stop();
		Intent intent = new Intent();
		intent.setClass(AShell_ComLine.this, AShell_ComLine.class);
		finish();
		startActivity(intent);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(tin<=1){
				tin++;
			}
			if(tin==1){
				time1 = System.currentTimeMillis();
				Toast.makeText(getApplicationContext(), "再按一次返回鍵，即可退出程式", Toast.LENGTH_LONG).show();
			}else{
				time2 = System.currentTimeMillis();
				if(time2-time1<=2000){
					System.exit(0);
				}
				time1 = System.currentTimeMillis();
				Toast.makeText(getApplicationContext(), "再按一次返回鍵，即可退出程式", Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Menu.FIRST, 0, "Entre按鈕");
		if(!External_Boot)
			menu.add(0, Menu.FIRST+1, 0, "重新啟動AShell");
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch (item_id){
			case Menu.FIRST:
				if(eb.isLayoutRequested())
					eb.setVisibility(View.VISIBLE);
				else
					eb.setVisibility(View.GONE);
				break;
			case Menu.FIRST+1:
				ReStart();
				break;
			default: return false;
		}
		return true;
	}
}
