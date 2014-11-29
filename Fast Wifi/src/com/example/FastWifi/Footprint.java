package com.example.FastWifi;

import java.util.List;

import database.*;
import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.widget.TextView;
public class Footprint {
	
	public void addFootprint(int cid, int lac, String[] cid_neighbour, String[] lac_neighbour, double[] calc_dbm, int[][] all_dbm,MySqlLiteHelper_PrimaryCell db, String name, int N){
		
		int nfno = 0;
		double sdv = 0.0;
		Footprint obj = new Footprint();
		
		db.addPrimaryCell(new PrimaryCell(db.getRowCount()+1, cid, lac, name));
		nfno = db.getTopRowCount();
		for(int i = 0; i < calc_dbm.length; i++){
			calc_dbm[i] = obj.round(calc_dbm[i], 2);
			sdv = obj.calc_sdv(calc_dbm[i], all_dbm[i], N);
			sdv = obj.round(sdv, 2);
			db.addNeighbourCell(new NeighbourCells(nfno, Integer.parseInt(cid_neighbour[i]), Integer.parseInt(lac_neighbour[i]), calc_dbm[i], sdv));
		}
		
	}
	public double calc_sdv(double avg, int[] vals, int N){
		
		double sdv_sum = 0.0;
		
		for(int i = 0; i < vals.length; i++){
			sdv_sum += (avg-vals[i])*(avg-vals[i])*1.0;
		}
		
		return Math.sqrt(sdv_sum/(N*1.0));
	}
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}