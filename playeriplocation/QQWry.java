/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package playeriplocation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 *///2014年8月22日18:21:27 By KeyMove
public class QQWry {
    public int PosList[]=new int[256];
    public static byte DataBuff[];
    public int ErrorFlag;
    public long DataBuffLen;
    public int StartPos;
    public int EndPos;
    public int len;
    public boolean Init(File DataFile) throws FileNotFoundException, IOException
    {
        if(!DataFile.exists())
        {
            return false;
        }
        DataBuffLen=DataFile.length();
        DataBuff=new byte[(int)DataBuffLen];
        FileInputStream p=new FileInputStream(DataFile);
        if(p.read(DataBuff,0, (int) DataBuffLen)==-1)
        {
            return false;
        }
        StartPos=(int)ReadInt(0);
        EndPos=(int)ReadInt(4);
        len=(EndPos-StartPos)/7;
        int save;
        int dat;
        int pos;
        save=0xff;
        pos=StartPos+3;
        for(int i=0;i<len;i++)
        {
            dat=(int)DataBuff[pos]&0xff;
            if(dat!=save)
            {
                save=dat;
                PosList[dat]=pos-3;
            }
            pos+=7;
        }
        return true;
    }
    private long ReadInt(int offset){
        return ((int)DataBuff[offset]&0xff)|((int)DataBuff[offset+1]&0xff)<<8|((int)DataBuff[offset+2]&0xff)<<16|((int)DataBuff[offset+3]&0xff)<<24;
    }
    private long ReadInt3(int offset){
        return ((int)DataBuff[offset]&0xff)|((int)DataBuff[offset+1]&0xff)<<8|((int)DataBuff[offset+2]&0xff)<<16;
    }
    private String ReadString(int offset,int lenx){
        byte buff[]=new byte[lenx];
        for(int i=0;i<lenx;i++)
        {
            buff[i]=DataBuff[offset];
            offset++;
        }
        String str;
        try {
            str = new String(buff,"GBK");
            return str;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(QQWry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    private int GetOffset(byte ip[]){
        int offset=0;
        long IP=((int)ip[3]&0xff)|((int)ip[2]&0xff)<<8|((int)ip[1]&0xff)<<16|((int)ip[0]&0xff)<<24;
        int i=PosList[(int)ip[0]&0xff];
        long p;
        long epos=0;
        do{
            p=ReadInt(i);
            p&=(long)0x00000000ffffffffL;
            if(IP==p)
            {
                epos=ReadInt3(i+4);
		break;
            }
            if(p>IP)
            {
                i-=7;
                epos=ReadInt3(i+4);
                break;
            }
            i+=7;
        }while(i<EndPos);
        p=ReadInt((int)epos);
        if(p==0)
            return 0;
        if(p>=IP)
        {
            return (int) (epos+4);
        }
        return 0;
    }
    private int GetLen(int offset){
        int i=0;
        while(DataBuff[offset]!=0)
        {
            i++;
            offset++;
        }
        return i;
    }
    private int GetStrLen(int offset){
        int i=0;
        while(DataBuff[offset]!=0)
        {
            if(DataBuff[offset]<=127){
                i++;
                offset++;
            }
            else{
                i+=2;
                offset++;
            }
        }
        return i;
    }
    private String GetOffsetString(int offset,boolean flag){
        String str="";
        int noffset=0;
        if(DataBuff[offset]<3&&DataBuff[offset]>=0)
        {
            switch(DataBuff[offset])
            {
                case 1:
                    offset=(int)ReadInt3(offset+1);
                    str=GetOffsetString(offset, true);
                    if(DataBuff[offset]>3)
                    {
                        offset+=GetLen(offset)-3;
                    }
                    str+=GetOffsetString(offset+4, true);
                    break;
                case 2:
                    noffset=(int)ReadInt3(offset+1);
                    str=GetOffsetString(noffset, true);
                    if(!flag)
                        str+=GetOffsetString(offset+4, true);
                    break;
            }
        }
        else{
            if(DataBuff[offset]==0xff)
                return "";
            str=ReadString(offset,GetStrLen(offset));
            if(!flag)
            {
                offset+=GetLen(offset)+1;
                str+=GetOffsetString(offset, true);
            }
        }
        return str;
    }
    
    public String QQWryGetLocation(byte ip[]){
        String loc;
        int offset=GetOffset(ip);
        loc=GetOffsetString(offset, false);
        return loc;
    }
}
