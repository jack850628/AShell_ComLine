package com.example.AShell.AShellException;

import com.example.AShell.SubEndStateCode;
//AShell例外類型，用於傳送從函數拋出的錯誤
public class AShellException extends Exception{
    public SubEndStateCode SESC;
    public AShellException(SubEndStateCode SESC){
        super(SESC.Message);
        this.SESC=SESC;
    }
}
