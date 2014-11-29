package database;

public class PrimaryCell {

	private int fno;
	private int cid;
	private int lac;
	private String name;
	
    public PrimaryCell(){}
 
    
    public int getFno() {
		return fno;
	}

	public void setFno(int fno) {
		this.fno = fno;
	}



	public int getCid() {
		return cid;
	}



	public void setCid(int cid) {
		this.cid = cid;
	}



	public int getLac() {
		return lac;
	}



	public void setLac(int lac) {
		this.lac = lac;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public PrimaryCell(int fno ,int cid, int lac, String name) {
        super();
        this.fno = fno;
        this.cid = cid;
        this.lac = lac;
        this.name = name;
    }
 
    //getters & setters
 
    @Override
    public String toString() {
        return "PrimaryCell [fno=" + fno + ", cid=" + cid + ", lac=" + lac +", name="+name
                + "]";
    }
}
