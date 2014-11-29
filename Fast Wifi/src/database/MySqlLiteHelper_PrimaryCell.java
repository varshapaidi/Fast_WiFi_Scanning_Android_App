package database;

import java.util.ArrayList; 
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class MySqlLiteHelper_PrimaryCell extends SQLiteOpenHelper {
 
	private static final String TABLE_PRIMARY_CELL = "primarycell";
	private static final String TABLE_NEIGHBOUR_CELL = "neighbourcells";
	private static final String TABLE_WIFI = "wifi";
	
    private static final String KEY_ID = "fno";
    private static final String KEY_CID = "cid";
    private static final String KEY_LAC = "lac";
    private static final String KEY_NAME = "name";

    private static final String NFNO = "fno";
    private static final String NKEY_NCID = "ncid";
    private static final String NKEY_NLAC = "nlac";
    private static final String NKEY_NRSSI = "nrssi";
    private static final String NKEY_SDV = "sdv";
    
    private static final String APFNO = "fno";
    private static final String APNETID = "net_id";
    private static final String APSTRENGTH = "strength";
    private static final String APNAME = "ap_name";
    private static final String APSECTYPE = "sec_type";
    
    private static final String[] COLUMNS = {KEY_ID,KEY_CID,KEY_LAC, KEY_NAME};
    private static final String[] NCOLUMNS = {NFNO, NKEY_NCID, NKEY_NLAC, NKEY_NRSSI, NKEY_SDV};
    private static final String[] APCOLUMNS = {APFNO, APNETID, APSTRENGTH, APNAME, APSECTYPE};
	
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "fast_wifi_scan_test5";
 
    public MySqlLiteHelper_PrimaryCell(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); 
    }
         
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        String CREATE_BOOK_TABLE = "CREATE TABLE primarycell ( " +
                "fno INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cid INTEGER NOT NULL, "+
                "lac INTEGER NOT NULL, "+
                "name TEXT NOT NULL )";
 
        db.execSQL(CREATE_BOOK_TABLE);
        
        String CREATE_NCELLS_TABLE = "CREATE TABLE neighbourcells ( " +
                "fno INTEGER NOT NULL, " +
                "ncid INTEGER NOT NULL, "+
                "nlac INTEGER NOT NULL, "+
                "nrssi REAL NOT NULL, "+
                "sdv REAL NOT NULL )";
 
        db.execSQL(CREATE_NCELLS_TABLE);
        
        String CREATE_WIFI_TABLE = "CREATE TABLE wifi ( " +
                "fno INTEGER NOT NULL, " +        		
                "ap_name TEXT NOT NULL, "+
                "net_id INTEGER NOT NULL, "+
                "strength INTEGER NOT NULL,"+
                "sec_type TEXT NOT NULL) ";
 
        db.execSQL(CREATE_WIFI_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS primarycell");
        db.execSQL("DROP TABLE IF EXISTS neighbourcells");
        db.execSQL("DROP TABLE IF EXISTS wifi");
        this.onCreate(db);
    }
    
    public void addPrimaryCell(PrimaryCell obj){
        //for logging
    	Log.d("addPrimaryCell", obj.toString());

    	// 1. get reference to writable DB
    	SQLiteDatabase db = this.getWritableDatabase();

    	// 2. create ContentValues to add key "column"/value
    	ContentValues values = new ContentValues();
    	values.put(KEY_CID, obj.getCid()); // get title
    	values.put(KEY_LAC, obj.getLac()); // get author
    	values.put(KEY_NAME, obj.getName());
    	
    	// 3. insert
    	db.insert(TABLE_PRIMARY_CELL, // table
        null, //nullColumnHack
        values); // key/value -> keys = column names/ values = column values

    	// 4. close
    	db.close();
    }

    public void addNeighbourCell(NeighbourCells obj){
        //for logging
    	Log.d("addNeighbourCells", obj.toString());

    	// 1. get reference to writable DB
    	SQLiteDatabase db = this.getWritableDatabase();

    	// 2. create ContentValues to add key "column"/value
    	ContentValues values = new ContentValues();
    	values.put(NFNO, obj.getFno()); // get title 
    	values.put(NKEY_NCID, obj.getNcid());
    	values.put(NKEY_NLAC, obj.getNlac());
    	values.put(NKEY_NRSSI, obj.getNrssi());
    	values.put(NKEY_SDV, obj.getSdv());
    	// 3. insert
    	db.insert(TABLE_NEIGHBOUR_CELL, // table
        null, //nullColumnHack
        values); // key/value -> keys = column names/ values = column values

    	// 4. close
    	db.close();
    }

    public void addAP(APInfo obj){
        //for logging
    	Log.d("addAP", obj.toString());

    	// 1. get reference to writable DB
    	SQLiteDatabase db = this.getWritableDatabase();

    	// 2. create ContentValues to add key "column"/value
    	ContentValues values = new ContentValues();
    	values.put(APFNO, obj.getFno()); // get title 
    	values.put(APNAME, obj.getAp_name());
    	values.put(APNETID, obj.getNet_id());
    	values.put(APSTRENGTH, obj.getStrength());
    	values.put(APSECTYPE, obj.getSec_type());
    	
    	// 3. insert
    	db.insert(TABLE_WIFI, // table
        null, //nullColumnHack
        values); // key/value -> keys = column names/ values = column values

    	// 4. close
    	db.close();
    }
    
    public PrimaryCell getPrimaryCell(int id){
    	 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
     
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_PRIMARY_CELL, // a. table
                COLUMNS, // b. column names
                "fno = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
     
        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
     
        // 4. build book object
        PrimaryCell pcell = new PrimaryCell();
        pcell.setFno(Integer.parseInt(cursor.getString(0)));
        pcell.setCid(Integer.parseInt(cursor.getString(1)));
        pcell.setLac(Integer.parseInt(cursor.getString(2)));
        pcell.setName(cursor.getString(3));
        
        //log
    Log.d("arsenal pcell:("+id+")", pcell.toString());
     
        // 5. return book
        return pcell;
    }
    
    public List<APInfo> getAP(int id){
   	 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        List<APInfo> ap_list = new ArrayList<APInfo>(); 
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_WIFI, // a. table
                APCOLUMNS, // b. column names
                "fno = ?", // c. selections
                new String[] { String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
     
        // 3. if we got results get the first one
        APInfo ap = null;
        if (cursor.moveToFirst()) {
            do {
            	ap = new APInfo();
            	 ap.setFno(Integer.parseInt(cursor.getString(0)));
                 ap.setNet_id(Integer.parseInt(cursor.getString(1)));
                 ap.setStrength(Integer.parseInt(cursor.getString(2)));
                 ap.setAp_name(cursor.getString(3));
                 ap.setSec_type(cursor.getString(4));
  
                // Add book to books
            	ap_list.add(ap);
            } while (cursor.moveToNext());
        }
        
        // 4. build book object
        
        //log
        Log.d("AP:("+id+")", ap.toString());
     
        // 5. return book
        return ap_list;
    }
    
    public List<NeighbourCells> getNeighbourCell(int id){
   	 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        List<NeighbourCells> ncell_list = new ArrayList<NeighbourCells>();
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_NEIGHBOUR_CELL, // a. table
                NCOLUMNS, // b. column names
                "fno = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
     
        // 3. if we got results get the first one
        NeighbourCells ncell = null;
        if (cursor.moveToFirst()) {
            do {
            	ncell = new NeighbourCells();
            	ncell.setFno(Integer.parseInt(cursor.getString(0)));
            	ncell.setNcid(Integer.parseInt(cursor.getString(1)));
            	ncell.setNlac(Integer.parseInt(cursor.getString(2)));
            	ncell.setNrssi(Double.parseDouble(cursor.getString(3)));
            	ncell.setSdv(Double.parseDouble(cursor.getString(4)));
  
                // Add book to books
            	ncell_list.add(ncell);
            } while (cursor.moveToNext());
        }
     
        //log
    Log.d("arsenal ncell:("+id+")", ncell_list.toString());
     
        // 5. return book
        return ncell_list;
    }
    
    public List<PrimaryCell> getAllPrimaryCells() {
        List<PrimaryCell> pcell_list = new ArrayList<PrimaryCell>();
  
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PRIMARY_CELL;
  
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        // 3. go over each row, build book and add it to list
        PrimaryCell pcell = null;
        if (cursor.moveToFirst()) {
            do {
            	pcell = new PrimaryCell();
            	pcell.setFno(Integer.parseInt(cursor.getString(0)));
            	pcell.setCid(Integer.parseInt(cursor.getString(1)));
            	pcell.setLac(Integer.parseInt(cursor.getString(2)));
            	pcell.setName(cursor.getString(3));
  
                // Add book to books
            	pcell_list.add(pcell);
            } while (cursor.moveToNext());
        }
  
        Log.d("getPrimaryCells()", pcell_list.toString());
  
        // return books
        return pcell_list;
    }
  
    public List<NeighbourCells> getAllNeighbourCells() {
        List<NeighbourCells> ncell_list = new ArrayList<NeighbourCells>();
  
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_NEIGHBOUR_CELL;
  
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        // 3. go over each row, build book and add it to list
        NeighbourCells ncell = null;
        if (cursor.moveToFirst()) {
            do {
            	ncell = new NeighbourCells();
            	ncell.setFno(Integer.parseInt(cursor.getString(0)));
            	ncell.setNcid(Integer.parseInt(cursor.getString(1)));
            	ncell.setNlac(Integer.parseInt(cursor.getString(2)));
            	ncell.setNrssi(Double.parseDouble(cursor.getString(3)));
            	ncell.setSdv(Double.parseDouble(cursor.getString(4)));
  
                // Add book to books
            	ncell_list.add(ncell);
            } while (cursor.moveToNext());
        }
  
        Log.d("getAllNeighbourCells()", ncell_list.toString());
  
        // return books
        return ncell_list;
    }
  
    
    public void deletePrimaryCell(PrimaryCell pcell) {
    	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_PRIMARY_CELL, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(pcell.getFno()) }); //selections args
 
        // 3. close
        db.close();
 
        //log
    Log.d("deleteprimarycells", pcell.toString());
 
    }
    
    public void deletePrimaryCellAll() {
   	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_PRIMARY_CELL, null, null);
 
        // 3. close
        db.close();
 
        //log
    }
    
    public void deleteNeighbourCellAll() {
      	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_NEIGHBOUR_CELL, null, null);
 
        // 3. close
        db.close();
 
        //log
    }
    
    public int getRowCount() {
  
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PRIMARY_CELL;
  
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        // 3. go over each row, build book and add it to list
        
        return cursor.getCount();
    }
    
    public int getTopRowCount() {
    	  
        // 1. build the query
        String query = "SELECT  fno FROM " + TABLE_PRIMARY_CELL+" ORDER BY fno DESC LIMIT 1";
        int fno = 0;
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
            	fno = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
  
        // 3. go over each row, build book and add it to list
        
        return fno;
    }
    
    //TODO
    public int getRowCounTestt() {
    	  
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PRIMARY_CELL;
  
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        // 3. go over each row, build book and add it to list
        
        return cursor.getCount();
    }
    //TODO
    
    // Main logic functions 
    public ArrayList<Integer> getAllFnoPrimary(int cid){
    	//Get all fno for given pcid    
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> fno_list = new ArrayList<Integer>();
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_PRIMARY_CELL, // a. table
                COLUMNS, // b. column names
                "cid = ?", // c. selections
                new String[] { String.valueOf(cid) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
     
        
        if (cursor.moveToFirst()) {
            do {
            	fno_list.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
     
        //log
     
        // 5. return book
        return fno_list;
    }
  
    public List<NeighbourCells> getNeighbourInfo(int fno){
      	 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        List<NeighbourCells> ncell_list = new ArrayList<NeighbourCells>();
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_NEIGHBOUR_CELL, // a. table
                NCOLUMNS, // b. column names
                "fno = ?", // c. selections
                new String[] { String.valueOf(fno) }, // d. selections args
                null, // e. group by
                null, // f. having
                "nrssi", // g. order by
                null); // h. limit
     
        // 3. if we got results get the first one
        NeighbourCells ncell = null;
        if (cursor.moveToFirst()) {
            do {
            	ncell = new NeighbourCells();
            	ncell.setFno(Integer.parseInt(cursor.getString(0)));
            	ncell.setNcid(Integer.parseInt(cursor.getString(1)));
            	ncell.setNlac(Integer.parseInt(cursor.getString(2)));
            	ncell.setNrssi(Double.parseDouble(cursor.getString(3)));
            	ncell.setSdv(Double.parseDouble(cursor.getString(4)));
  
                // Add book to books
            	ncell_list.add(ncell);
            } while (cursor.moveToNext());
        }
     
        //log
    Log.d("arsenal ncell:("+fno+")", ncell_list.toString());
        // 5. return book
        return ncell_list;
    }
}