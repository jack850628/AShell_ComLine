package com.example.AShell.Native_Class;

import com.example.AShell.AShell;
import com.example.AShell.Data_Type_And_Struct.AShell_this;
import com.example.AShell.ValueProcess.AShellType;

public class AShell_Threading {
    AShell.Run_Point RP;
    public AShellType _start(AShell_this AShThis,AShellType[] Args){
        RP=AShThis.AS.Thread_Run(AShThis.ValueArray);
        return new AShellType();
    }
    public AShellType _wait(AShell_this AShThis,AShellType[] Args) throws Exception{
        AShThis.AS.Thread_Wait(RP,Args[0].to_java_long());
        return new AShellType();
    }
    public AShellType notifyall(AShell_this AShThis,AShellType[] Args) throws Exception{
        AShThis.AS.Thread_Notifyall();
        return new AShellType();
    }
    public AShellType notify(AShell_this AShThis,AShellType[] Args) throws Exception{
        AShThis.AS.Thread_Notify(RP);
        return new AShellType();
    }
}
