package com.lyue.aw_an.ndk;

/**
 * Created by Administrator on 2016/2/20.
 */
public class CircleBuf {

    int NMAX=20;

    int iput = 0;

    int iget = 0;

    int index = 0;

    int buffer[]=new int[NMAX];


    public int addring (int i){
        return (i+1) == NMAX ? 0 : i+1;
    }


    public int get() {
        int pos;
        if (index>0){
            pos = iget;
            iget = addring(iget);
            index--;
            System.out.println("get-->"+buffer[pos]);
            return buffer[pos];

        }else {
            System.out.println("Buffer is Empty");
            return 2048;//2048是波形基线
        }
    }

    public void put(int z){
        if (index<NMAX){
            buffer[iput]=z;
            System.out.println("put<--"+buffer[iput]);
            iput = addring(iput);
            index++;
        }else  System.out.println("Buffer is full");
    }
}
