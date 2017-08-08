package com.example.AShell;

import com.example.AShell.Data_Type_And_Struct.Type_String;

public class SubEndStateCode {
    public static enum State{Break,Continue,Tag,End,Exception,Return};//區塊結束狀態碼的所有狀態
    public String Code,Message;//錯誤發生的程式碼，區塊結束時的訊息(當Return時是Return的結果(以AShell類型存放)，當Exception時則是Exception的訊息(以java字串類型存放))
    public State state;//結束狀態
    boolean Main;//判斷當下是否執行在最外成的執行續
    int Index;//當遇到tag時儲存Tag中的跳轉程式碼行數
    int LineNumbers;//發生錯誤的程式碼的行數
    public SubEndStateCode(){
        this.state=State.Return;
        this.Code=null;
        this.Message=Type_String.NULL;
        this.Main=true;
    }
    public void setSubEndStateCode(State state,String Code,int LineNumbers,String Message){
        this.state=state;
        this.Code=Code;
        this.LineNumbers=LineNumbers;
        this.Message=Message;
    }
    public void setIndex(int Index){
        this.Index=Index;
    }
}
