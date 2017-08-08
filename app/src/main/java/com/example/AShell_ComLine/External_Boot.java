package com.example.AShell_ComLine;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.app.AlertDialog;
/**外部啟動類別**/
public class External_Boot  extends AppCompatActivity {
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//以下為外部啟動
		intent = getIntent();
		ListView listview=new ListView(this);
		listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new String[]{"直接執行","帶參數執行"}));
		AlertDialog AD = new AlertDialog.Builder(this).setTitle("執行方式").setView(listview).setNegativeButton("取消", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				External_Boot.this.finish();
			}
		}).show();
		AD.setCancelable(false);
		listview.setOnItemClickListener(new ItemClickListener(AD));
	}
	/**
	 * 啟動AShell人機交互介面
	 */
	void To_AShell(){
		intent.putExtra("External_Boot", true);
		intent.setClass(this, AShell_ComLine.class);
		finish();
		startActivity(intent);
	}
	/**
	 *分解參數用的函數
	 * @param args 參數字串
	 * @return 分解過後的參數陣列
	 */
	String[] Args_Split(String args){
		ArrayList<String> args_list=new ArrayList<>();
		StringBuilder Buffer=new StringBuilder();
		char c;
		for(int i=0;i<args.length();i++){
			c=args.charAt(i);
			switch (c) {
				case '\"':
					while(true){
						i++;
						c=args.charAt(i);
						if(c=='\"')
							break;
						else if(c=='\\')
							Buffer.append(args.charAt(++i));
						else
							Buffer.append(c);
					}
					continue;
				case ' ':
					if(!Buffer.toString().equals("")){
						args_list.add(Buffer.toString());
						Buffer.delete(0, Buffer.length());
					}
					continue;
			}
			Buffer.append(c);
		}
		if(args.length()!=0){
			if(!Buffer.toString().equals(""))
				args_list.add(Buffer.toString());
			return args_list.toArray(new String[0]);
		}else
			return null;
	}
	class ItemClickListener implements OnItemClickListener{
		AlertDialog AD;
		public ItemClickListener(AlertDialog AD){
			this.AD=AD;
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AD.dismiss();
			if(position==0)
				To_AShell();
			else{
				final EditText  ET=new EditText (External_Boot.this);
				ET.setHint("參數");
				AlertDialog Args_AD = new AlertDialog.Builder(External_Boot.this).setTitle("輸入參數").setView(ET).setPositiveButton("確定", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						intent.putExtra("Args", Args_Split(ET.getText().toString()));
						To_AShell();
					}
				}).show();
				Args_AD.setCancelable(false);
			}
		}
	}
}
