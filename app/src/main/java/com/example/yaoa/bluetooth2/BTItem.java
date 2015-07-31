package com.example.yaoa.bluetooth2;

import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.io.Serializable;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


/**
 * Created by yaoa on 7/23/2015.
 */
public class BTItem {

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    private String name, address;
    private int signal;
    public ArrayList<Integer> totalsignal = new ArrayList<Integer>();
    private double average;


    public BTItem(String name, String address, int signal){
        this.name = name;
        this.address = address;
        this.signal = signal;
        this.average = 0;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
        this.totalsignal.add(signal);
        if(this.totalsignal.size()>20){
            Collections.sort(totalsignal);
            int i[] = {1,2,3,18,19,20};
            totalsignal.remove(i);
            calculateAverage();
            resetTotal();
        }
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void calculateAverage() {
        double total = 0;
        for (int signaldata : this.totalsignal) {
                total += signaldata;
            }
        setAverage( total / this.totalsignal.size() );
    }

    public void resetTotal(){
        this.totalsignal.clear();
    }

    public int getTotalSize(){
        return this.totalsignal.size();
    }


}
