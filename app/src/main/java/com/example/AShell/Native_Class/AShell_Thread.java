package com.example.AShell.Native_Class;

import com.example.AShell.Data_Type_And_Struct.AShell_this;
import com.example.AShell.ValueProcess.AShellType;

public class AShell_Thread {
    public static AShellType sleep(AShell_this AShThis,AShellType[] Args) throws Exception{
        if(Args.length==0)
            throw new Exception("參數數量不可以為0");
        try{
            Thread.sleep(Args[0].to_java_long());
        }catch(Exception e){}
        return new AShellType();
    }
    public static AShellType pause(AShell_this AShThis,AShellType[] Args){
        AShThis.AS.Print("請按任意鍵繼續...",null);
        AShThis.AS.read.Rand(1);
        AShThis.AS.Print("\n",null);
        return new AShellType();
    }
}
