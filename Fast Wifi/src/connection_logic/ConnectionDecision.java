package connection_logic;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import database.*;

public class ConnectionDecision {

	public int checkTopNeighbourStrength(int pcid, int[] ncid, int[] calc_dbm, MySqlLiteHelper_PrimaryCell db){
		
		int scan = 0;
		int counter = 0;
		ArrayList<Integer> fno_list = new ArrayList<Integer>();
		fno_list = db.getAllFnoPrimary(pcid);
		List<NeighbourCells> ncell_list = new ArrayList<NeighbourCells>();
		double sdv_effective = 0.0;
		
		for(int i = 0; i < fno_list.size(); i++){
			
			counter = 0;
			ncell_list = db.getNeighbourInfo(fno_list.get(i));
			if(ncell_list.size() >= 2){
				//Check top power
				for(int j = 0; j < ncid.length; j++){
					if(ncell_list.get(0).getNcid() == ncid[j]){
						if(3*ncell_list.get(0).getSdv() > 5){
							sdv_effective = 5;
						}
						else{
							sdv_effective = 3*ncell_list.get(0).getSdv();
						}
						Log.d("check strength 0: ", (ncell_list.get(0).getNrssi() + sdv_effective + 3)+ "," + (ncell_list.get(0).getNrssi() - sdv_effective - 3) + "," +calc_dbm[j]);
						if((calc_dbm[j] >= (ncell_list.get(0).getNrssi() - sdv_effective - 3)) && (calc_dbm[j] <= (ncell_list.get(0).getNrssi() + sdv_effective + 3))){
							counter++;
							break;
						}
					}
				}
				//Check 2nd top power
				for(int j = 0; j < ncid.length; j++){
					if(ncell_list.get(1).getNcid() == ncid[j]){
						if(3*ncell_list.get(1).getSdv() > 5){
							sdv_effective = 5;
						}
						else{
							sdv_effective = 3*ncell_list.get(1).getSdv();
						}
						Log.d("check strength 1: ", (ncell_list.get(1).getNrssi() + sdv_effective + 3)+ "," + (ncell_list.get(1).getNrssi() - sdv_effective - 3) + "," +calc_dbm[j]);
						if((calc_dbm[j] >= (ncell_list.get(1).getNrssi() - sdv_effective - 3)) && (calc_dbm[j] <= (ncell_list.get(1).getNrssi() + sdv_effective + 3))){
							counter++;
							break;
						}
					}
				}
				if(counter == 2){
					scan = ncell_list.get(0).getFno();
					Log.d("henry", "match found");
					//TODO logic for wifi info
					break;
				}
				else{
					Log.d("counter value: ", ""+counter);
				}
			}
			else{
				Log.d("Henry", "ncell list size < 2");
			}
		}
		
		return scan;
	}
}
