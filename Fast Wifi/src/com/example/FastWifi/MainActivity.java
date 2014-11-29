package com.example.FastWifi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.test1.R;

import connection_logic.ConnectionDecision;
import database.APInfo;
import database.MySqlLiteHelper_PrimaryCell;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

	TextView name;
	TextView Neighboring;
	TextView textGsmCellLocation;
    TextView textMCC;
    TextView textMNC;
    TextView textCID;
    TextView status;
    
    //Wifi instance variables
    WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;
	ListView list;
	String wifis[];
	AlertDialog.Builder alert;
	EditText input;
	MySqlLiteHelper_PrimaryCell db = null;
	
	//Sensor instance variables
	private SensorManager sMgr;
	Sensor default_sensor;
	boolean has_moved = false;
	private long lastUpdate = 0;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 1500;
	public static int prevNetId = -1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Tab code start
        TabHost tabhost = (TabHost)findViewById(android.R.id.tabhost); 
        tabhost.setup();
        
        TabHost.TabSpec tabspec1 = tabhost.newTabSpec("User");
        tabspec1.setContent(R.id.User);
        tabspec1.setIndicator("User");
        tabhost.addTab(tabspec1); 
        
        TabHost.TabSpec tabspec2 = tabhost.newTabSpec("Debug");
        tabspec2.setContent(R.id.Debug);
        tabspec2.setIndicator("Debug");
        tabhost.addTab(tabspec2);
        
        TabHost.TabSpec tabspec3 = tabhost.newTabSpec("Optional");
        tabspec3.setContent(R.id.Optional);
        tabspec3.setIndicator("Optional");
        tabhost.addTab(tabspec3);
        //Tab code end
        
        //Assign id's to text boxes
        Neighboring = (TextView)findViewById(R.id.neighboring);
    	textGsmCellLocation = (TextView)findViewById(R.id.gsmcelllocation);
        textMCC = (TextView)findViewById(R.id.mcc);
        textMNC = (TextView)findViewById(R.id.mnc);
        textCID = (TextView)findViewById(R.id.cid);
        name = (TextView)findViewById(R.id.name);
        status = (TextView)findViewById(R.id.status); 
        
        //Wifi oncreate variables start 
		list = (ListView) findViewById(R.id.listView1);
		mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!mainWifiObj.isWifiEnabled())
			mainWifiObj.setWifiEnabled(true);
		alert = new AlertDialog.Builder(this);
		input = new EditText(this);
		db = new MySqlLiteHelper_PrimaryCell(this);
		wifiReciever = new WifiScanReceiver();
		list.setVisibility(View.GONE);
        //Wifi oncreate variables end
        
		//Sensor code start
		sMgr = (SensorManager)this.getSystemService(SENSOR_SERVICE);
		List<Sensor> list = sMgr.getSensorList(Sensor.TYPE_ALL);
		default_sensor = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sMgr.registerListener(this, default_sensor,SensorManager.SENSOR_DELAY_NORMAL);	
		//Sensor code end

        //Debug Tab code start
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				try {
					//start_footprint(10, db);
					//test();
					tester();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
        
        
        final Button clear = (Button) findViewById(R.id.button2);
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
		
            	Neighboring.setText("");
            	textGsmCellLocation.setText("");
                textMCC.setText("");
                textMNC.setText("");
                textCID.setText("");
                name.setText("Enter Footprint Name");
            }
        });
        
        final Button connect = (Button) findViewById(R.id.button3);
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
		
            	try {
            		if(has_moved || (prevNetId == -1)){
            			connectWifi();
            		}
            		else{
            			status.setText("Not Moved Connecting to prevNetId");
                		mainWifiObj.disconnect();
            			mainWifiObj.enableNetwork(prevNetId, true);
            			mainWifiObj.reconnect();
            		}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        });
        
        final Button disconnect = (Button) findViewById(R.id.button4);
        disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
		
            	mainWifiObj.disconnect();
            	status.setText("");
            }
        });
        //Debug tab code end
        
    }   

    void connectWifi() throws InterruptedException{
    	
    	ConnectionDecision decision = new ConnectionDecision();
    	MySqlLiteHelper_PrimaryCell db = new MySqlLiteHelper_PrimaryCell(this);
        //retrieve a reference to an instance of TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
        
        int pcid = cellLocation.getCid();
        int plac = cellLocation.getLac();
        List<NeighboringCellInfo> NeighboringList = null;
        
        boolean flag = false;
        int z = 0;
        int size = 0;
        do{
        	size = 0;
        	Thread.sleep(100);
        	telephonyManager = null;
        	telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        	NeighboringList = null;
        	NeighboringList = telephonyManager.getNeighboringCellInfo();	
        	
        	if(NeighboringList.size() > 1){
        		size = NeighboringList.size();
        		flag = true;
        	}
        	if(!flag){
        		z++;
        	}
        }while((size < 2) && (z != 5));
        
        if((z == 5) && (size < 2)){
        	Log.e("Cannot initialize neighbour Connect Wifi", "Exiting");
        	return;
        }
        
        int[] calc_dbm = new int[NeighboringList.size()];
        int[] ncid = new int[NeighboringList.size()];
        int[] nlac = new int[NeighboringList.size()];
        
        String stringNeighboring = "Neighboring List- Lac : Cid : RSSI\n";
        for(int i=0; i < NeighboringList.size(); i++){
          
         String dBm;
         int rssi = NeighboringList.get(i).getRssi();
         if(rssi == NeighboringCellInfo.UNKNOWN_RSSI){
          dBm = "Unknown RSSI";
         }else{
        	 dBm = String.valueOf(-113 + 2 * rssi) + " dBm";
        	 calc_dbm[i] = -113 + 2 * rssi;
         }
         ncid[i] = NeighboringList.get(i).getCid();
         nlac[i] = NeighboringList.get(i).getLac();
        }
         
        int scan = decision.checkTopNeighbourStrength(pcid, ncid, calc_dbm, db);
       
        if(scan == 0){
        	list.setVisibility(View.VISIBLE);
        	status.setText("Scan Required");
        	
        	Thread.sleep(1000);
        	start_footprint(10, db);
        			
        	mainWifiObj.startScan();
        	wifiReciever.scanRequiredSaveAP();
        }
        else{
        	list.setVisibility(View.GONE);
        	status.setText("Scan not required");
        	List<APInfo> apinfo = db.getAP(scan);
        	if(apinfo.get(0).getSec_type().equals("Open"))
        		wifiReciever.connectToAP(apinfo.get(0).getAp_name(), apinfo.get(0).getSec_type(), "");
        		
        	else{
        		mainWifiObj.disconnect();
    			mainWifiObj.enableNetwork(apinfo.get(0).getNet_id(), true);
    			mainWifiObj.reconnect();
    			prevNetId = apinfo.get(0).getNet_id();
        		has_moved = false;
        	}
        		
        }
    }
    
    void test(){
    	
    	MySqlLiteHelper_PrimaryCell db = new MySqlLiteHelper_PrimaryCell(this);
    	TextView textMCC = (TextView)findViewById(R.id.mcc);
        
        //Delete all cells
        db.deletePrimaryCellAll();
        db.deleteNeighbourCellAll();
        
        textMCC.setText(""+db.getTopRowCount()+"\n\n"+db.getAllPrimaryCells());
    }
    
    public boolean start_footprint(int num_points, MySqlLiteHelper_PrimaryCell db) throws InterruptedException{
    	
    	Footprint foot = new Footprint();
    	final int SLEEP_TIME = 100;
    	final int LOOP_THROUGH = num_points;
    	String mcc = "";
        String mnc = "";
        int cid = 0, lac = 0;
        String stringNeighboring = "Neighboring List- Lac : Cid : RSSI\n";
        
    	//retrieve a reference to an instance of TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
        String networkOperator = telephonyManager.getNetworkOperator();
        List<NeighboringCellInfo> NeighboringList = null;
        int contains = 0, ttl = 0; 
        //Check initial Neighbor list for empty sets and invalid values
        int z = 0;
        int size = 0;
        int cid_1 = 0, cid_2 = 0, power_1 = 0, power_2 = 0;
        do{
        	
        	size = 0;
        	Thread.sleep(SLEEP_TIME);
        	telephonyManager = null;
        	telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        	NeighboringList = null;
        	NeighboringList = telephonyManager.getNeighboringCellInfo();	
        	
        	if(NeighboringList.size() > 1){
        		size = NeighboringList.size();
        		for(int i=0; i < NeighboringList.size(); i++){
        			if(NeighboringList.get(i).getRssi() > power_2){
        				power_1 =power_2;
        				cid_2 = cid_1;
        				power_2 = NeighboringList.get(i).getRssi();
        				cid_2 = NeighboringList.get(i).getCid();	
        			}	
        		}
        		
    			if(cid_1 < 0 || cid_2 < 0){
    				size = 0;
    	        	Thread.sleep(SLEEP_TIME);
    				break;
    			}
        	}
        	z++;
        }while((size < 2) && (z != 5));
        
        if((z == 5) && (size < 2)){
        	Log.e("Cannot initialize neighbour Footprint", "Exiting");
        	return false;
        }
    
        int[] temp_ncid = new int[NeighboringList.size()]; 
        for(int i = 0; i < NeighboringList.size(); i++){	
        	temp_ncid[i] = NeighboringList.get(i).getCid();
        }
        
        int neighbourcellListSize = NeighboringList.size();
        double calc_dbm[] = new double[NeighboringList.size()];
        String []cid_neighbour = new String[NeighboringList.size()];
        String []lac_neighbour = new String[NeighboringList.size()];
    	int[][] all_dbm = new int[NeighboringList.size()][LOOP_THROUGH];
        mcc = networkOperator.substring(0, 3);
        mnc = networkOperator.substring(3);
        int j = 0;
        List<NeighboringCellInfo> nlist = null;
        boolean flag = false;
        cid = cellLocation.getCid();
        lac = cellLocation.getLac();
    	
        for(int i=0; i < NeighboringList.size(); i++){
        	cid_neighbour[i] = String.valueOf(NeighboringList.get(i).getCid());
        	lac_neighbour[i] = String.valueOf(NeighboringList.get(i).getLac());
         }
         
        while(j != LOOP_THROUGH){
        ttl++;
        if(ttl == 2000){
        	Log.d("Aboring", "Cannot find ncell id's in list TTL expired");
        	return false;
        }
    	Thread.sleep(SLEEP_TIME);
    	flag = false;
    	contains = 0;
    	telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	nlist = null;
    	nlist = telephonyManager.getNeighboringCellInfo();
    	
        if(nlist.size() >= neighbourcellListSize){
        
        	for(int p = 0; p < neighbourcellListSize; p++){
        		for(int q = 0; q < nlist.size(); q++){
        			if(temp_ncid[p] == nlist.get(q).getCid()){
        				contains++;
        			}
        		}
        	}
        	if(contains == neighbourcellListSize){
        		//continue
        	}
        	else{
        		continue;
        	}
        	
        	//check if nlist contains our values
        	for(int p = 0; p < neighbourcellListSize; p++){
        		for(int q = 0; q < nlist.size(); q++){
        			if(temp_ncid[p] == nlist.get(q).getCid()){
        				String dBm;
                		int rssi = nlist.get(q).getRssi();
                		if(rssi == NeighboringCellInfo.UNKNOWN_RSSI){
                			dBm = "Unknown RSSI";
                		}else{
                			dBm = String.valueOf(-113 + 2 * rssi) + " dBm";
                			calc_dbm[p] += -113 + 2 * rssi;
                			all_dbm[p][j] = -113 + 2 * rssi;
                		}
        			}
        		}
        	}
        		j++;
    	}
        else{
        		Log.d("Neighbourcell length not equal !!!", "");
        		continue;
        	}
        }
    	
    	for(int i = 0; i < calc_dbm.length; i++){
        	calc_dbm[i] /= (LOOP_THROUGH*1.0);
        	stringNeighboring = stringNeighboring
       	         + String.valueOf(NeighboringList.get(i).getLac()) +" : "
       	         + String.valueOf(NeighboringList.get(i).getCid()) +" : "
       	         + calc_dbm[i] +"\n";
        }
    	//Set text values	
    	textMCC.setText("mcc: " + mcc);
        textMNC.setText("mnc: " + mnc);
        textGsmCellLocation.setText(cellLocation.toString());
        textCID.setText("gsm cell id: " + String.valueOf(cid));
         
        foot.addFootprint(cid, cellLocation.getLac(), cid_neighbour, lac_neighbour, calc_dbm, all_dbm,db, name.getText().toString(), LOOP_THROUGH);
        
        Neighboring.setText(stringNeighboring);
        display_optional();
        
        return true;
	}
    
    void display_optional(){
    	
    	MySqlLiteHelper_PrimaryCell dbp = new MySqlLiteHelper_PrimaryCell(this);
    	TextView disp = (TextView)findViewById(R.id.optional1);
   
    	disp.setText(""+dbp.getAllNeighbourCells());
    }
    
    void tester(){
    	
    	TextView textGsmCellLocation = (TextView)findViewById(R.id.gsmcelllocation);
        TextView textMCC = (TextView)findViewById(R.id.mcc);
        TextView textMNC = (TextView)findViewById(R.id.mnc);
        TextView textCID = (TextView)findViewById(R.id.cid);
  
        
        //retrieve a reference to an instance of TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
        
        String networkOperator = telephonyManager.getNetworkOperator();
        String mcc = networkOperator.substring(0, 3);
        String mnc = networkOperator.substring(3);
        textMCC.setText("mcc: " + mcc);
        textMNC.setText("mnc: " + mnc);
        
        int cid = cellLocation.getCid();
        int lac = cellLocation.getLac();
        textGsmCellLocation.setText(cellLocation.toString());
        textCID.setText("gsm cell id: " + String.valueOf(cid));
        
        TextView Neighboring = (TextView)findViewById(R.id.neighboring);
        List<NeighboringCellInfo> NeighboringList = telephonyManager.getNeighboringCellInfo();
        
        String stringNeighboring = "Neighboring List- Lac : Cid : RSSI\n";
        for(int i=0; i < NeighboringList.size(); i++){
          
         String dBm;
         int rssi = NeighboringList.get(i).getRssi();
         if(rssi == NeighboringCellInfo.UNKNOWN_RSSI){
          dBm = "Unknown RSSI";
         }else{
        
        	 dBm = String.valueOf(-113 + 2 * rssi) + " dBm";
         }
  
         stringNeighboring = stringNeighboring
          + String.valueOf(NeighboringList.get(i).getLac()) +" : "
          + String.valueOf(NeighboringList.get(i).getCid()) +" : "
          + dBm +"\n";
        }
        
        Neighboring.setText(stringNeighboring);
    }
    
    class WifiScanReceiver extends BroadcastReceiver {
		public final String WPA2 = "WPA2";
		public final String WPA = "WPA";
		public final String WEP = "WEP";
		public final String OPEN = "Open";
		public final String WPA_EAP = "WPA-EAP";
		public final String IEEE8021X = "IEEE8021X";
		public int fno = 0;
		public int count_entries = 0;
		
		@SuppressLint("UseValueOf")
		public void onReceive(Context c, Intent intent) {

		}

		public void scanRequiredSaveAP(){
			List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
			wifiScanList = removeDuplicates(wifiScanList);
			wifis = new String[wifiScanList.size()];
			for (int i = 0; i < wifiScanList.size(); i++)
				wifis[i] = ((wifiScanList.get(i)).SSID);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.simple_list_item_1, wifis);
			list.setAdapter(adapter);

			alert.setTitle("Password Prompt");
			alert.setMessage("Please enter the key:");

			// Set an EditText view to get user input
			
			alert.setView(input);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Editable value = input.getText();
							List<APInfo> apinfo = db.getAP(fno);
							connectToAP(apinfo.get(0).getAp_name(), apinfo.get(0).getSec_type(),value.toString());
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});

			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String SSID = (String) parent.getAdapter()
							.getItem(position);
					List<ScanResult> wifiScanList = mainWifiObj
							.getScanResults();
					wifiScanList = removeDuplicates(wifiScanList);
					ScanResult scan_item = null;
					for (ScanResult item : wifiScanList) {
						if (item.SSID.equals(SSID)) {
							scan_item = item;
							break;
						}
					}
					
					String securityType = getScanResultSecurity(scan_item);
					APInfo apinfo = new APInfo();
					apinfo.setSec_type(securityType);
					apinfo.setAp_name(SSID);
					apinfo.setStrength(scan_item.level);
					fno = db.getTopRowCount();
					apinfo.setFno((fno));
					db.addAP(apinfo);
					if (securityType.equals(WPA2) || securityType.equals(WPA)) {
						alert.show();						
					}
					else if(securityType.equals(OPEN))
						connectToAP(SSID, OPEN,"");

				}
			});

		}
		
		public List<ScanResult> removeDuplicates(List<ScanResult> wifiScanList) {
			HashSet<String> lookup = new HashSet<String>();
			List<ScanResult> list = new ArrayList<ScanResult>();
			for (ScanResult item : wifiScanList) {
				if (!lookup.contains(item.SSID)) {
					String securityType = getScanResultSecurity(item);
					if (securityType.equals(OPEN) || securityType.equals(WPA2)
							|| securityType.equals(WPA)) {
						lookup.add(item.SSID);
						list.add(item);
					}
				}
			}
			return list;
		}

		public String getScanResultSecurity(ScanResult scanResult) {
			final String[] SECURITY_MODES = { WEP, WPA, WPA2, WPA_EAP,
					IEEE8021X };

			final String cap = scanResult.capabilities;
			for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
				if (cap.contains(SECURITY_MODES[i])) {
					return SECURITY_MODES[i];
				}
			}

			return OPEN;
		}

		public void connectToAP(String SSID, String secType, String password) {
			WifiConfiguration wifiConfig = new WifiConfiguration();
			wifiConfig.SSID = String.format("\""+SSID+"\"");
			
			if(secType.equals(OPEN)){
				wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				wifiConfig.allowedAuthAlgorithms.clear();
			}
			else if(secType.equals(WPA) || (secType.equals(WPA2))){
				wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				wifiConfig.preSharedKey = "\"".concat(password).concat("\"");
			}
			
			wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			
			wifiConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			wifiConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			wifiConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP40);
			wifiConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			wifiConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			wifiConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);

			// remember id
			int netId = mainWifiObj.addNetwork(wifiConfig);
			
			mainWifiObj.disconnect();
			mainWifiObj.enableNetwork(netId, true);
			mainWifiObj.reconnect();
			mainWifiObj.saveConfiguration();
			list.setVisibility(View.GONE);
    		status.setText("Connected to "+SSID);
    		prevNetId = netId;
    		has_moved = false;
		}
	}

    public void onSensorChanged(SensorEvent event) {
		Sensor mySensor = event.sensor;
		
		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			
			float val_x = event.values[0];
			float val_y = event.values[1];
			float val_z = event.values[2];

			long curTime = System.currentTimeMillis();

			
			if ((curTime - lastUpdate) > 100) {
				
				long diff = (curTime - lastUpdate);
				lastUpdate = curTime;
				
				float speed = Math.abs(val_x + val_y + val_z - last_x - last_y - last_z)/ diff * 10000;

				if (speed > SHAKE_THRESHOLD) {
					has_moved = true;
					status.setText("Has Moved");
				}
			}	
			
		}
	}

    public void onAccuracyChanged(Sensor sensor1, int accuracy) {

	}

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
        
    protected void onResume() {
		super.onResume();
		sMgr.registerListener(this, default_sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}