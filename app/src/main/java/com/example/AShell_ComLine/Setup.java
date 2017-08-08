package com.example.AShell_ComLine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

/**
 * Created by jack8 on 2017/5/30.
 */

public class Setup extends AppCompatActivity {
    String using_file=Environment.getExternalStorageDirectory().toString() + "/AShell/using_file";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)//當沒有權限
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},0);//更使用者要權限
        else{
            if(!new File(using_file).isDirectory())
                copy_using_file.copyFilesFassets(this, "using_file", using_file);
            finish();
            startActivity(new Intent(this,AShell_ComLine.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==0){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED)//當使用者不給權限時
                finish();
            else{
                if(!new File(using_file).isDirectory())
                    copy_using_file.copyFilesFassets(this, "using_file", using_file);
                finish();
                startActivity(new Intent(this,AShell_ComLine.class));
            }
        }
    }
}
