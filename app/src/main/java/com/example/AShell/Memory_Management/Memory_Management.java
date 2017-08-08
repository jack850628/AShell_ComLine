package com.example.AShell.Memory_Management;

import com.example.AShell.AShell;
import com.example.AShell.AShellException.AShellMempryValueNotFindException;
import com.example.AShell.Data_Type_And_Struct.Class_Type;
import com.example.AShell.Data_Type_And_Struct.Function;
import com.example.AShell.Data_Type_And_Struct.Native_Function;
import com.example.AShell.Data_Type_And_Struct.Type_String;
import com.example.AShell.Data_Type_And_Struct.Value_Array;
import com.example.AShell.ValueProcess.StrDW;
import java.util.LinkedList;
/**
 * AShell記憶體管理類別
 * 關於AShell垃圾回收機制(GC)說明如下
 * 1.目前能夠觸發GC的只有當函數結束時和AShlib.gc函數
 * 2.每個AShell記憶體值都有一個參考指數，當指數歸零時就會被丟進回收桶中等待回收
 * 3.當StrDW或VarStrDW的處理結果是AShell記憶體類型時，該記憶體的參考指數就會加一，如果沒有變數承接時就會被減一，如果承接變數不承接了也會減一，當承接變數所在的變數清單被銷毀時也會減一
 * ，另外當AShell記憶體類型被當作函數回傳值時也會被加一，然後該函數的變數清單被銷毀時才會把剛剛的加一減回去但是如果歸零時
 * 4.AShell現在的GC(垃圾回收)是有缺陷的，因為在使用var功能時，當有同樣名稱的變數在同一層變數清單時AShell無法判斷同名變數是否來自父類別的變數清單，所以不能把參考指數減一
 * 5.類別的this變數的參照不會被算進參照指數裡，但是super變數會
 * 6.因為目前在AShell類別中，子類別與父類別的記憶體位置是不同的，各自有各自的參考指數，如果有用父類別中的函數取得父類別位置，那這樣就有可能造成錯誤的回收，所以目前暫時創建父類別時把參照指數定為1，參照者為super變數
 */
public class Memory_Management {
    private static final LinkedList<Class_Memory_Type> Class_Memory=new LinkedList<>();
    private static final LinkedList<Class_Memory_Type> Static_Class_Memory=new LinkedList<>();
    private static final LinkedList<Function_Memory_Type> Function_Memory=new LinkedList<>();
    private static final LinkedList<Native_Function_Memory_Type> Native_Function_Memory=new LinkedList<>();
    private static final LinkedList<Array_Memory_Type> Array_Memory=new LinkedList<>();
    private static final LinkedList<AShell_Memory_Type> Recycle_Bin=new LinkedList<>();//回收桶，當AShell記憶體類型的引用指數歸零後，就嘿被放置於這裡等待回收
    //-------------------------------------------------------------------------------
    private static class AShell_Memory_Type{//AShell記憶體類型基本型態
        int Arguments=0;//參考者的數量，紀錄有多少變數參考
        int Key;//識別碼，用來查詢用的
    }
    private static class Class_Memory_Type extends AShell_Memory_Type{
        static int count=0;//數量紀錄，是一個遞增值。
        static int Scount=0;//數量紀錄，是一個遞增值。
        Class_Type Obj=null;
        public Class_Memory_Type(int Key,Class_Type Obj){
            this.Obj=Obj;
            this.Key=Key;//識別碼，用來查詢用的。
        }
        public Class_Memory_Type(int Key,Class_Type Obj,int Arguments){
            this.Obj=Obj;
            this.Key=Key;//識別碼，用來查詢用的。
            this.Arguments=Arguments;
        }
    }
    private static class Function_Memory_Type extends AShell_Memory_Type{
        static int count=0;//數量紀錄，是一個遞增值。
        Function Obj=null;
        public Function_Memory_Type(int Key,Function Obj){
            this.Obj=Obj;
            this.Key=Key;
        }
        public Function_Memory_Type(int Key,Function Obj,int Arguments){
            this.Obj=Obj;
            this.Key=Key;
            this.Arguments=Arguments;
        }
    }
    private static class Native_Function_Memory_Type extends AShell_Memory_Type{
        static int count=0;//數量紀錄，是一個遞增值。
        Native_Function Obj=null;
        public Native_Function_Memory_Type(int Key,Native_Function Obj){
            this.Obj=Obj;
            this.Key=Key;
        }
        public Native_Function_Memory_Type(int Key,Native_Function Obj,int Arguments){
            this.Obj=Obj;
            this.Key=Key;
            this.Arguments=Arguments;
        }
    }
    private static class Array_Memory_Type extends AShell_Memory_Type{
        static int count=0;//數量紀錄，是一個遞增值。
        StringBuilder Obj=null;
        StringBuilder AShell_Map_Key=null;
        int size;
        Value_Array ValueArray;
        public Array_Memory_Type(int Key,StringBuilder AShell_Map_Key,StringBuilder Obj,Value_Array ValueArray){
            this.AShell_Map_Key=AShell_Map_Key;
            this.Obj=Obj;
            this.Key=Key;
            this.ValueArray=ValueArray;
        }
        public Array_Memory_Type(int Key,StringBuilder AShell_Map_Key,StringBuilder Obj,Value_Array ValueArray,int Arguments){
            this.AShell_Map_Key=AShell_Map_Key;
            this.Obj=Obj;
            this.Key=Key;
            this.ValueArray=ValueArray;
            this.Arguments=Arguments;
        }
    }
    //-------------------------------------------------------------------------------
    public static StringBuilder Class_Builder(Class_Type obj){
        StringBuilder assage;
        synchronized(Class_Memory){
            assage=new StringBuilder("AShell_Class@"+Class_Memory_Type.count);
            Class_Memory.add(new Class_Memory_Type(Class_Memory_Type.count++, obj));
        }
        return assage;
    }
    public static StringBuilder Class_Builder(Class_Type obj,int Arguments){
        StringBuilder assage;
        synchronized(Class_Memory){
            assage=new StringBuilder("AShell_Class@"+Class_Memory_Type.count);
            Class_Memory.add(new Class_Memory_Type(Class_Memory_Type.count++, obj,Arguments));
        }
        return assage;
    }
    
    
    public static StringBuilder Static_Class_Builder(Class_Type obj){
        StringBuilder assage;
        synchronized(Static_Class_Memory){
            assage=new StringBuilder("AShell_SClass@"+Class_Memory_Type.Scount);
            Static_Class_Memory.add(new Class_Memory_Type(Class_Memory_Type.Scount++, obj));
        }
        return assage;
    }
    public static StringBuilder Static_Class_Builder(Class_Type obj,int Arguments){
        StringBuilder assage;
        synchronized(Static_Class_Memory){
            assage=new StringBuilder("AShell_SClass@"+Class_Memory_Type.Scount);
            Static_Class_Memory.add(new Class_Memory_Type(Class_Memory_Type.Scount++, obj,Arguments));
        }
        return assage;
    }
    
    public static StringBuilder Function_Builder(Function obj){
        StringBuilder assage;
        synchronized(Function_Memory){
            assage=new StringBuilder("AShell_Function@"+Function_Memory_Type.count);
            Function_Memory.add(new Function_Memory_Type(Function_Memory_Type.count++, obj));
        }
        return assage;
    }
    public static StringBuilder Function_Builder(Function obj,int Arguments){
        StringBuilder assage;
        synchronized(Function_Memory){
            assage=new StringBuilder("AShell_Function@"+Function_Memory_Type.count);
            Function_Memory.add(new Function_Memory_Type(Function_Memory_Type.count++, obj,Arguments));
        }
        return assage;
    }
    
    public static StringBuilder Native_Function_Builder(Native_Function obj){
        StringBuilder assage;
        synchronized(Native_Function_Memory){
            assage=new StringBuilder("AShell_NFunction@"+Native_Function_Memory_Type.count);
            Native_Function_Memory.add(new Native_Function_Memory_Type(Native_Function_Memory_Type.count++, obj));
        }
        return assage;
    }
    public static StringBuilder Native_Function_Builder(Native_Function obj,int Arguments){
        StringBuilder assage;
        synchronized(Native_Function_Memory){
            assage=new StringBuilder("AShell_NFunction@"+Native_Function_Memory_Type.count);
            Native_Function_Memory.add(new Native_Function_Memory_Type(Native_Function_Memory_Type.count++, obj,Arguments));
        }
        return assage;
    }
    
    public static StringBuilder Array_Builder(StringBuilder AShell_Map_Key,StringBuilder obj,Value_Array ValueArray){
        StringBuilder assage;
        synchronized(Array_Memory){
            assage=new StringBuilder("AShell_Array@"+Array_Memory_Type.count);
            Array_Memory.add(new Array_Memory_Type(Array_Memory_Type.count++, AShell_Map_Key, obj, ValueArray));
        }
        return assage;
    }
    public static StringBuilder Array_Builder(StringBuilder AShell_Map_Key,StringBuilder obj,Value_Array ValueArray,int Arguments){
        StringBuilder assage;
        synchronized(Array_Memory){
            assage=new StringBuilder("AShell_Array@"+Array_Memory_Type.count);
            Array_Memory.add(new Array_Memory_Type(Array_Memory_Type.count++, AShell_Map_Key, obj, ValueArray,Arguments));
        }
        return assage;
    }
    private static class Arguments_Temp{//參考指數更動查詢暫存
        static AShell_Memory_Type Arguments=null;
        static String Address=null;
    }
    private static final byte[] _Arguments_Temp=new byte[0];//Arguments_Temp的同步鎖
    /**
     * 將AShell記憶體類型的參考指數增加，用於當記憶體位置被變數參考時
     * @param Address AShell記憶體位置
     * @throws Exception Address當不是AShell記憶體位置
     */
    public static void Add_To_Arguments(String Address) throws Exception{
        synchronized(_Arguments_Temp){
            if(Arguments_Temp.Address!=null&&Address.equals(Arguments_Temp.Address)){//判斷這次的參考指數更動查詢是否跟上次的一樣
                Arguments_Temp.Arguments.Arguments++;
                return;
            }
        }
        AShell_Memory_Type Arguments;
        try{
            if(Address.matches(Type_String.CLASS_M))
                (Arguments=M_get_Class(Integer.valueOf(Address.substring(13)))).Arguments++;
            else if(Address.matches(Type_String.STATIC_CLASS_M))
                (Arguments=M_get_Static_Class(Integer.valueOf(Address.substring(14)))).Arguments++;
            else if(Address.matches( Type_String.FUNCTION_M))
                (Arguments=M_get_Function(Integer.valueOf(Address.substring(16)))).Arguments++;
            else if(Address.matches(Type_String.NATIVE_FUNCTION_M))
                (Arguments=M_get_Native_Function(Integer.valueOf(Address.substring(17)))).Arguments++;
            else if(Address.matches(Type_String.ARRAY_M))
                synchronized(Array_Memory){
                    (Arguments=Array_Memory.get(M_get_Array_inxde(Integer.valueOf(Address.substring(13))))).Arguments++;
                }
            else throw new Exception("來自Add_To_Arguments函數的錯誤，只能更動AShell記憶體類型的參考值");
            synchronized(_Arguments_Temp){
                Arguments_Temp.Arguments=Arguments;
                Arguments_Temp.Address=Address;
            }
        }catch(AShellMempryValueNotFindException e){}//過濾掉查無記憶體所引發的錯誤，因為當有記憶體被手動回收後，其他變數不再引用時也會修試圖修改Arguments
    }
     /**
     * 將AShell記憶體類型的參考指數減少，用於當記憶體位置被變數取消參考時
     * @param Address AShell記憶體位置
     * @throws Exception Address當不是AShell記憶體位置
     */
    public static void Cut_To_Arguments(String Address) throws Exception{
        synchronized(_Arguments_Temp){
            if(Arguments_Temp.Address!=null&&Address.equals(Arguments_Temp.Address)){//判斷這次的參考指數更動查詢是否跟上次的一樣
                if(--Arguments_Temp.Arguments.Arguments<=0){//當參考指數為零時
                    Recycle_Bin.add(Arguments_Temp.Arguments);//將該記憶體位置放置垃圾桶
                    //清除參考指數更動查詢暫存
                    Arguments_Temp.Arguments=null;
                    Arguments_Temp.Address=null;
                }
                return;
            }
        }
        try{
            if(Address.matches(Type_String.CLASS_M)){ 
                Class_Memory_Type ASClass=M_get_Class(Integer.valueOf(Address.substring(13)));
                if(--ASClass.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASClass);//將該記憶體位置放置垃圾桶
                else{
                    synchronized(_Arguments_Temp){
                        Arguments_Temp.Arguments=ASClass;
                        Arguments_Temp.Address=Address;
                    }
                }
            }else if(Address.matches(Type_String.STATIC_CLASS_M)){ 
                Class_Memory_Type ASSClass=M_get_Static_Class(Integer.valueOf(Address.substring(14)));
                if(--ASSClass.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASSClass);//將該記憶體位置放置垃圾桶
                else{
                    synchronized(_Arguments_Temp){
                        Arguments_Temp.Arguments=ASSClass;
                        Arguments_Temp.Address=Address;
                    }
                }
            }else if(Address.matches( Type_String.FUNCTION_M)){ 
                Function_Memory_Type ASFunction=M_get_Function(Integer.valueOf(Address.substring(16)));
                if(--ASFunction.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASFunction);//將該記憶體位置放置垃圾桶
                else{
                    synchronized(_Arguments_Temp){
                        Arguments_Temp.Arguments=ASFunction;
                        Arguments_Temp.Address=Address;
                    }
                }
            }else if(Address.matches(Type_String.NATIVE_FUNCTION_M)){ 
                Native_Function_Memory_Type ASNFunction=M_get_Native_Function(Integer.valueOf(Address.substring(17)));
                if(--ASNFunction.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASNFunction);//將該記憶體位置放置垃圾桶
                else{
                    synchronized(_Arguments_Temp){
                        Arguments_Temp.Arguments=ASNFunction;
                        Arguments_Temp.Address=Address;
                    }
                }
            }else if(Address.matches(Type_String.ARRAY_M)){ 
                Array_Memory_Type ASArray;
                synchronized(Array_Memory){
                     ASArray=Array_Memory.get(M_get_Array_inxde(Integer.valueOf(Address.substring(13))));
                }
                if(--ASArray.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASArray);//將該記憶體位置放置垃圾桶
                else{
                    synchronized(_Arguments_Temp){
                        Arguments_Temp.Arguments=ASArray;
                        Arguments_Temp.Address=Address;
                    }
                }
            }else throw new Exception("來自Cut_To_Arguments函數的錯誤，只能更動AShell記憶體類型的參考值");
        }catch(AShellMempryValueNotFindException e){}//過濾掉查無記憶體所引發的錯誤，因為當有記憶體被手動回收後，其他變數不再引用時也會修試圖修改Arguments
    }
    /**
     * 將AShell記憶體類型的參考指數減少但不回收，用於當記憶體位置被函數回傳時
     * @param Address AShell記憶體位置
     * @throws Exception Address當不是AShell記憶體位置
     */
    public static void Cut_To_Arguments_Not_Recycle(String Address) throws Exception{
        synchronized(_Arguments_Temp){
            if(Arguments_Temp.Address!=null&&Address.equals(Arguments_Temp.Address)){//判斷這次的參考指數更動查詢是否跟上次的一樣
                Arguments_Temp.Arguments.Arguments--;
                return;
            }
        }
        AShell_Memory_Type Arguments;
        try{
            if(Address.matches(Type_String.CLASS_M))
                (Arguments=M_get_Class(Integer.valueOf(Address.substring(13)))).Arguments--;
            else if(Address.matches(Type_String.STATIC_CLASS_M))
                (Arguments=M_get_Static_Class(Integer.valueOf(Address.substring(14)))).Arguments--;
            else if(Address.matches( Type_String.FUNCTION_M))
                (Arguments=M_get_Function(Integer.valueOf(Address.substring(16)))).Arguments--;
            else if(Address.matches(Type_String.NATIVE_FUNCTION_M))
                (Arguments=M_get_Native_Function(Integer.valueOf(Address.substring(17)))).Arguments--;
            else if(Address.matches(Type_String.ARRAY_M))
                synchronized(Array_Memory){
                     (Arguments=Array_Memory.get(M_get_Array_inxde(Integer.valueOf(Address.substring(13))))).Arguments--;
                }
            else throw new Exception("來自Cut_To_Arguments_Not_Recycle函數的錯誤，只能更動AShell記憶體類型的參考值");
            synchronized(_Arguments_Temp){
                Arguments_Temp.Arguments=Arguments;
                Arguments_Temp.Address=Address;
            }
        }catch(AShellMempryValueNotFindException e){}//過濾掉查無記憶體所引發的錯誤，因為當有記憶體被手動回收後，其他變數不再引用時也會修試圖修改Arguments
    }
    /**
     * 檢查AShell記憶體類型的參考指數是否為零，需要被回收，用於運算是結束時
     * @param Address AShell記憶體位置
     * @throws Exception Address當不是AShell記憶體位置
     */
    public static void Check_Arguments(String Address) throws Exception{
        try{
            if(Address.matches(Type_String.CLASS_M)){ 
                Class_Memory_Type ASClass=M_get_Class(Integer.valueOf(Address.substring(13)));
                if(ASClass.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASClass);//將該記憶體位置放置垃圾桶
            }else if(Address.matches(Type_String.STATIC_CLASS_M)){ 
                Class_Memory_Type ASSClass=M_get_Static_Class(Integer.valueOf(Address.substring(14)));
                if(ASSClass.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASSClass);//將該記憶體位置放置垃圾桶
            }else if(Address.matches( Type_String.FUNCTION_M)){ 
                Function_Memory_Type ASFunction=M_get_Function(Integer.valueOf(Address.substring(16)));
                if(ASFunction.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASFunction);//將該記憶體位置放置垃圾桶
            }else if(Address.matches(Type_String.NATIVE_FUNCTION_M)){ 
                Native_Function_Memory_Type ASNFunction=M_get_Native_Function(Integer.valueOf(Address.substring(17)));
                if(ASNFunction.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASNFunction);//將該記憶體位置放置垃圾桶
            }else if(Address.matches(Type_String.ARRAY_M)){ 
                Array_Memory_Type ASArray;
                synchronized(Array_Memory){
                     ASArray=Array_Memory.get(M_get_Array_inxde(Integer.valueOf(Address.substring(13))));
                }
                if(ASArray.Arguments<=0)//當參考指數為零時
                    Recycle_Bin.add(ASArray);//將該記憶體位置放置垃圾桶
            }else throw new Exception("來自Check_Arguments函數的錯誤，只能更動AShell記憶體類型的參考值");
        }catch(AShellMempryValueNotFindException e){}//過濾掉查無記憶體所引發的錯誤，因為當有記憶體被手動回收後，其他變數不再引用時也會修試圖修改Arguments
    }
    /**取得類別記憶體位置下的值
    *@param assage  記憶體位址
    * @return  該記憶體位置的值
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public static Class_Type get_Class(String assage) throws Exception{
        if(!assage.matches(Type_String.CLASS_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的類別記憶體");
        return M_get_Class(Integer.valueOf(assage.substring(13))).Obj;
    }
    /**取得類別記憶體位置所在的Memory類別
    *@param assage  記憶體位址
    * @return  該記憶體位置的Key和值
    */
    private static Class_Memory_Type M_get_Class(int assage) throws Exception{
        synchronized(Class_Memory){
            if(Class_Memory.isEmpty())
                throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_Class@"+assage+"'");
            int top=Class_Memory.size(),botton=0,middle=(top-botton)/2;
            Class_Memory_Type CMT;
            while(true){
                CMT=Class_Memory.get(middle);
                if(CMT.Key==assage)
                    return Class_Memory.get(middle);
                else if(CMT.Key>assage){
                    if(top==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    top=middle;
                    middle=((top-botton)/2)+botton;
                }else if(CMT.Key<assage){
                    if(botton==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    botton=middle;
                    middle=((top-botton)/2)+botton;
                }
            }
        }
        throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_Class@"+assage+"'");
    }
    /**取得靜態類別記憶體位置下的值
    *@param assage  記憶體位址
    * @return  該記憶體位置的值
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public static Class_Type get_Static_Class(String assage) throws Exception{
        if(!assage.matches(Type_String.STATIC_CLASS_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的靜態類別記憶體");
        return M_get_Static_Class(Integer.valueOf(assage.substring(14))).Obj;
    }
    /**取得靜態類別記憶體位置所在的Memory類別
    *@param assage  記憶體位址
    * @return  該記憶體位置的Key和值
    */
    private static Class_Memory_Type M_get_Static_Class(int assage) throws Exception{
        synchronized(Static_Class_Memory){
            if(Static_Class_Memory.isEmpty())
                throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_SClass@"+assage+"'");
            int top=Static_Class_Memory.size(),botton=0,middle=(top-botton)/2;
            Class_Memory_Type SCMT;
            while(true){
                SCMT=Static_Class_Memory.get(middle);
                if(SCMT.Key==assage)
                    return Static_Class_Memory.get(middle);
                else if(SCMT.Key>assage){
                    if(top==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    top=middle;
                    middle=((top-botton)/2)+botton;
                }else if(SCMT.Key<assage){
                    if(botton==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    botton=middle;
                    middle=((top-botton)/2)+botton;
                }
            }
        }
        throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_SClass@"+assage+"'");
    }
    /**取得函數記憶體位置下的值
    *@param assage  記憶體位址
    * @return  該記憶體位置的值
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public static Function get_Function(String assage) throws Exception{
        if(!assage.matches(Type_String.FUNCTION_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的函數記憶體");
        Function_Memory_Type FMT=M_get_Function(Integer.valueOf(assage.substring(16)));
        return FMT.Obj;
    }
    /**取得函數記憶體位置所在的Memory類別
    *@param assage  記憶體位址
    * @return  該記憶體位置的Key和值
    */
    private static Function_Memory_Type M_get_Function(int assage) throws Exception{
        synchronized(Function_Memory){
            if(Function_Memory.isEmpty())
                throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_Function@"+assage+"'");
            int top=Function_Memory.size(),botton=0,middle=(top-botton)/2;
            Function_Memory_Type FMT;
            while(true){
                FMT=Function_Memory.get(middle);
                if(FMT.Key==assage)
                    return FMT;
                else if(FMT.Key>assage){
                    if(top==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    top=middle;
                    middle=((top-botton)/2)+botton;
                }else if(FMT.Key<assage){
                    if(botton==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    botton=middle;
                    middle=((top-botton)/2)+botton;
                }
            }
        }
        throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_Function@"+assage+"'");
    }
    /**取得Native函數記憶體位置下的值
    *@param assage  記憶體位址
    * @return  該記憶體位置的值
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public static Native_Function get_Native_Function(String assage) throws Exception{
        if(!assage.matches(Type_String.NATIVE_FUNCTION_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的Native函數記憶體");
        return M_get_Native_Function(Integer.valueOf(assage.substring(17))).Obj;
    }
    /**取得Native函數記憶體位置所在的Memory類別
    *@param assage  記憶體位址
    * @return  該記憶體位置的Key和值
    */
    private static Native_Function_Memory_Type M_get_Native_Function(int assage) throws Exception{
        synchronized(Native_Function_Memory){
            if(Native_Function_Memory.isEmpty())
                throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_NFunction@"+assage+"'");
            int top=Native_Function_Memory.size(),botton=0,middle=(top-botton)/2;
            Native_Function_Memory_Type MFMT;
            while(true){
                MFMT=Native_Function_Memory.get(middle);
                if(MFMT.Key==assage)
                    return Native_Function_Memory.get(middle);
                else if(MFMT.Key>assage){
                    if(top==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    top=middle;
                    middle=((top-botton)/2)+botton;
                }else if(MFMT.Key<assage){
                    if(botton==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    botton=middle;
                    middle=((top-botton)/2)+botton;
                }
            }
        }
        throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_NFunction@"+assage+"'");
    }
    /**設定陣列記憶體所記錄的陣列大小，用於陣列的第一個記憶體位置
    *@param assage  記憶體位址
    * @param size  陣列大小
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public static void set_Array_Sise(String assage,int size) throws Exception{
        if(!assage.matches(Type_String.ARRAY_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的陣列記憶體");
        synchronized(Array_Memory){
            Array_Memory.get(M_get_Array_inxde(Integer.valueOf(assage.substring(13)))).size=size;
        }
    }
    /**取得陣列記憶體所記錄的陣列大小，用於陣列的第一個記憶體位置
    *@param assage  記憶體位址
     * @return  陣列大小
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public static int get_Array_Sise(String assage) throws Exception{
        if(!assage.matches(Type_String.ARRAY_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的陣列記憶體");
        synchronized(Array_Memory){
            return Array_Memory.get(M_get_Array_inxde(Integer.valueOf(assage.substring(13)))).size;
        }
    }
    /**取得陣列記憶體位置下的值
    *@param assage  記憶體位址
    * @return  該記憶體位置的值
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public static StringBuilder get_Array_Item(String assage) throws Exception{
        if(!assage.matches(Type_String.ARRAY_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的陣列記憶體");
        synchronized(Array_Memory){
            return Array_Memory.get(M_get_Array_inxde(Integer.valueOf(assage.substring(13)))).Obj;
        }
    }
    /**取得陣列記憶體位置下的變數清單
    *@param assage  記憶體位址
    * @return  該記憶體位置的變數清單
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public static Value_Array get_Array_ValueArray(String assage) throws Exception{
        if(!assage.matches(Type_String.ARRAY_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的陣列記憶體");
        synchronized(Array_Memory){
            return Array_Memory.get(M_get_Array_inxde(Integer.valueOf(assage.substring(13)))).ValueArray;
        }
    }
    /**取得該assage在Array_Memory所在的位置
    *@param assage  記憶體位址
    * @return  該assage在Array_Memory所在的位置
    */
    private static int M_get_Array_inxde(int assage) throws Exception{
        synchronized(Array_Memory){
            if(Array_Memory.isEmpty())
                throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_Array@"+assage+"'");
            int top=Array_Memory.size(),botton=0,middle=(top-botton)/2;
            Array_Memory_Type AMT;
            while(true){
                AMT=Array_Memory.get(middle);
                if(AMT.Key==assage)
                    return middle;
                else if(AMT.Key>assage){
                    if(top==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    top=middle;
                    middle=((top-botton)/2)+botton;
                }else if(AMT.Key<assage){
                    if(botton==middle)//如果搜尋範圍沒有改變，就代表要找的記憶體位置不存在
                        break;
                    botton=middle;
                    middle=((top-botton)/2)+botton;
                }
            }
        }
        throw new AShellMempryValueNotFindException("查無記憶體位置'AShell_Array@"+assage+"'");
    }
    /**依照所指定的值，切移動鄰近的記憶體位置
    *@param assage  記憶體位址
    * @param distance 移動的距離
    * @return  移動過後的記憶體位置
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public synchronized static String move(String assage,int distance) throws Exception{
        if(!assage.matches(Type_String.ARRAY_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的陣列記憶體");
        if(distance==0)
            return assage;
        distance+=M_get_Array_inxde(Integer.valueOf(assage.substring(13)));
        if(0<=distance&&distance<Array_Memory.size())
            return "AShell_Array@"+Array_Memory.get(distance).Key;
        else
            throw new AShellMempryValueNotFindException("陣列引數超出範圍");
        //return "AShell_Array@"+(distance+Integer.valueOf(assage.substring(13)));
    }
    /**找出指定Key的記憶體位置
    *@param assage  記憶體位址
    * @param Map_Key 要取得的記憶體位置的AShell的Map的Key
    * @return  移動過後的記憶體位置
     * @throws java.lang.Exception 拋出變數未宣告之錯誤給執行續接收
    */
    public synchronized static String move(String assage,String Map_Key) throws Exception{
        if(!assage.matches(Type_String.ARRAY_M))
            throw new AShellMempryValueNotFindException("'"+assage+"'不是有效的陣列記憶體");
        int index=M_get_Array_inxde(Integer.valueOf(assage.substring(13)));
        for(int i=index;i<index+Array_Memory.get(index).size;i++){
            Array_Memory_Type AMT=Array_Memory.get(i);
            if(AMT.AShell_Map_Key!=null&&Map_Key.equals(AMT.AShell_Map_Key.toString()))
                return "AShell_Array@"+AMT.Key;
        }
        throw new Exception("Key值"+Map_Key+"不存在");
    }
    /**
     * 陣列記憶體回收函數
     * @param assage 陣列記憶體Key值
     * @param isGC 判斷是否回自動垃圾回收所呼叫
     * @throws Exception 拋出變數未宣告之錯誤給執行續接收
     */
    private static void ArrayRemove(int assage,boolean isGC) throws Exception{
        Array_Memory_Type AArray;
        synchronized(Array_Memory){
            AArray=Array_Memory.get(M_get_Array_inxde(assage));
        }
        if(!isGC||isGC&&AArray.Arguments<=0)
            ArrayRemove(AArray,assage,isGC);
    }
    /**
     * 陣列記憶體回收函數
     * @param  AArray 要回收的AShell陣列的頭元素
     * @param assage 陣列記憶體Key值
     *  @param isGC 判斷是否回自動垃圾回收所呼叫
     * @throws Exception 拋出變數未宣告之錯誤給執行續接收
     */
    private static void ArrayRemove(Array_Memory_Type AArray,int assage,boolean isGC) throws Exception{
        synchronized(Array_Memory){
            AArray.ValueArray.clear();
            for(int i=0,size=AArray.size-1;true;i++){
                if(AArray.Obj.toString().startsWith("AShell_A"))
                    ArrayRemove(Integer.valueOf(AArray.Obj.substring(13)),isGC);
                //System.err.println("LOG:AShell_Array@"+AArray.Key);
                Array_Memory.remove(AArray);
                if(i==size)
                    break;
                AArray=Array_Memory.get(M_get_Array_inxde(++assage));
            }
        }
    }
    /**
     * 將類別回收之函數
     * @param  AS AShell解析器的實體
     * @param RP 執行續指標，用來記錄一個AShell執行續在執行續清單裡的位置
     * @param assage 要回收的類別記憶體位置
     * @param isGC 判斷是否為GC回收
     * @throws Exception 拋出變數未宣告之錯誤給執行續接收
     */
    private static void ClassRemove(AShell AS,AShell.Run_Point RP,String assage,boolean isGC) throws Exception{
        Class_Memory_Type AClass=M_get_Class(Integer.valueOf(assage.substring(13)));
        if(!isGC||isGC&&AClass.Arguments<=0)
            ClassRemove(AS,RP,AClass,isGC);
    }
    /**
     * 將類別回收之函數
     * @param  AS AShell解析器的實體
     * @param RP 執行續指標，用來記錄一個AShell執行續在執行續清單裡的位置
     * @param AClass 要回收的AShell Class
     * @param isGC 判斷是否為GC回收
     * @throws Exception 拋出變數未宣告之錯誤給執行續接收
     */
    private static void ClassRemove(AShell AS,AShell.Run_Point RP,Class_Memory_Type AClass,boolean isGC) throws Exception{
        new StrDW(AS,RP,Type_String.DESTRUCTOR_FUNCTION_NAME+"()",AClass.Obj.ValueArray);//呼叫解構式
        CensorValueReturn CVR=CensorValue.CensorValue(Type_String.SUPER,AClass.Obj.ValueArray,true);//尋找這個類別有沒有父類別
        if(CVR.Result){//如果有父類別
            String SuperAddress=AClass.Obj.ValueArray.get(CVR.index).Tent.toString();
            AClass.Obj.ValueArray.clear();
            ClassRemove(AS,RP,SuperAddress,isGC);//將父類別回收
        }else
            AClass.Obj.ValueArray.clear();
        synchronized(Class_Memory){
            Class_Memory.remove(AClass);
        }
    }
    /*//參考者遞增
    public static void Reference_person_PP(String assage){
        if(assage.matches("(AShell@)(\\d+)")){
            Memory.get(assage).Reference_person++;
            System.err.println("LOGPP:"+Memory.get(assage).Reference_person+","+((Class_Type)Memory.get(assage).Obj).Name);
        }
    }
    //參考者遞減
    public static void Reference_person_NN(String assage){
        if(assage.matches("(AShell@)(\\d+)")){
            Memory object=Memory.get(assage);
            System.err.println("LOGNN:"+(object.Reference_person-1)+","+((Class_Type)Memory.get(assage).Obj).Name);
            if(--object.Reference_person==0){
                Memory.remove(assage);
            }
        }
    }*/
    /*public static void set(String assage,Object obj){
        Memory.put(assage,obj);
    }*/
    
    /**
     * 自動AShell垃圾回收
     * 不過AShell現在的GC(垃圾回收)是有缺陷的，因為在使用var功能時，當有同樣名稱的變數在同一層變數清單時AShell無法判斷同名變數是否來自父類別的變數清單，所以不能把參考指數減一
     * @param AS AShell語言解析器的主類別
     *@param RP 執行續指標，用來記錄一個AShell執行續在執行續清單裡的位置
     */
    private static volatile boolean GCStart=false;//垃圾回收狀態，用來判斷垃圾回收是否啟動
    public static void Garbage_Collection_Start(final AShell AS,final AShell.Run_Point RP){
        if(!GCStart){
            GCStart=true;
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     AShell_Memory_Type AShell_Object;
                     for(int i=0;i<Recycle_Bin.size();){
                         if((AShell_Object=Recycle_Bin.remove(i)).Arguments>0)
                             continue;
                         if(AShell_Object instanceof Class_Memory_Type){
                             Class_Memory_Type ASClass=(Class_Memory_Type)AShell_Object;
                             if(ASClass.Obj.CodeArray==null)//判斷是靜態類別還是動態類別，當Class_Type.ValueArray為null時就表示為動態類別
                                 try {
                                     ClassRemove(AS,RP,ASClass,true);
                                 } catch (Exception ex) {}
                             else{
                                 ASClass.Obj.CodeArray.clear();
                                 ASClass.Obj.ValueArray.clear();
                                 synchronized(Static_Class_Memory){
                                     Static_Class_Memory.remove(ASClass);
                                 }
                             }
                         }else if(AShell_Object instanceof Function_Memory_Type){
                             Function_Memory_Type AFunction=(Function_Memory_Type)AShell_Object;
                             AFunction.Obj.ValueArray.clear();
                             AFunction.Obj.CodeArray.clear();
                             synchronized(Function_Memory){
                                 Function_Memory.remove(AFunction);
                             }
                         }else if(AShell_Object instanceof Native_Function_Memory_Type){
                             synchronized(Native_Function_Memory){
                                 Native_Function_Memory.remove((Native_Function_Memory_Type)AShell_Object);
                             }
                         }else /*if(AShell_Object instanceof Array_Memory_Type)*/{
                             Array_Memory_Type AArray=(Array_Memory_Type)AShell_Object;
                             try {
                                 ArrayRemove(AArray,AArray.Key,true);
                             } catch (Exception ex) {}
                         }
                     }
                     GCStart=false;
                 }
             }).start();
        }
    }
    /**
     * 手動AShell垃圾回收
     * @param AS AShell語言解析器的主類別
     *@param RP 執行續指標，用來記錄一個AShell執行續在執行續清單裡的位置
     * @param assage 要回收的AShell記憶體位置
     */
    public static void remove(AShell AS,AShell.Run_Point RP,String assage) throws Exception{
        if(assage.startsWith("AShell_C")){
            ClassRemove(AS,RP,assage,false);
        }else if(assage.startsWith("AShell_S")){
            Class_Memory_Type ASClass=M_get_Static_Class(Integer.valueOf(assage.substring(14)));
            ASClass.Obj.CodeArray.clear();
            ASClass.Obj.ValueArray.clear();
            synchronized(Static_Class_Memory){
                Static_Class_Memory.remove(ASClass);
            }
        }else if(assage.startsWith("AShell_F")){
            Function_Memory_Type AFunction=M_get_Function(Integer.valueOf(assage.substring(16)));
            AFunction.Obj.ValueArray.clear();
            AFunction.Obj.CodeArray.clear();
            synchronized(Function_Memory){
                Function_Memory.remove(AFunction);
            }
        }else if(assage.startsWith("AShell_N")){
            Native_Function_Memory_Type ANFunction=M_get_Native_Function(Integer.valueOf(assage.substring(17)));
            synchronized(Native_Function_Memory){
                Native_Function_Memory.remove(ANFunction);
            }
        }else if(assage.startsWith("AShell_A")){
            ArrayRemove(Integer.valueOf(assage.substring(13)),false);
        }else
            throw new AShellMempryValueNotFindException("類型必須是AShell記憶體位置");
    }
}
