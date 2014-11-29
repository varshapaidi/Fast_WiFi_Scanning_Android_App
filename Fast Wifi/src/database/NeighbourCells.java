package database;

public class NeighbourCells {

	private int fno;
	private int ncid;
	private int nlac;
	private double nrssi;
	private double sdv;
	
    public NeighbourCells(){}

	public int getFno() {
		return fno;
	}

	public void setFno(int fno) {
		this.fno = fno;
	}


	public int getNcid() {
		return ncid;
	}



	public void setNcid(int ncid) {
		this.ncid = ncid;
	}



	public int getNlac() {
		return nlac;
	}



	public void setNlac(int nlac) {
		this.nlac = nlac;
	}

	public double getNrssi() {
		return nrssi;
	}

	public void setNrssi(double nrssi) {
		this.nrssi = nrssi;
	}

	public double getSdv() {
		return sdv;
	}

	public void setSdv(double sdv) {
		this.sdv = sdv;
	}

	public NeighbourCells(int fno , int ncid, int nlac, double nrssi, double sdv) {
        super();
        this.fno = fno;
        this.ncid = ncid;
        this.nlac = nlac;
        this.nrssi = nrssi;
        this.sdv = sdv;
    }
 
    //getters & setters
 
    @Override
    public String toString() {
        return "NeighbourCell [fno=" + fno +", ncid=" + ncid + ",nlac=" + nlac
                + ", nrssi = "+nrssi+", sdv="+sdv+"]";
    }
}
