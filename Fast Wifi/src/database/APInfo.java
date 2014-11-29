package database;

public class APInfo {
	private int fno;
	private int net_id;
	private int strength;
	private String ap_name;
	private String sec_type;
	
	public String getSec_type() {
		return sec_type;
	}

	public void setSec_type(String sec_type) {
		this.sec_type = sec_type;
	}

	public int getFno() {
		return fno;
	}
	public void setFno(int fno) {
		this.fno = fno;
	}
	public int getNet_id() {
		return net_id;
	}
	public void setNet_id(int net_id) {
		this.net_id = net_id;
	}
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
	public String getAp_name() {
		return ap_name;
	}
	public void setAp_name(String ap_name) {
		this.ap_name = ap_name;
	}

	public APInfo(int fno, int net_id, int strength, String ap_name,
			String sec_type) {
		super();
		this.fno = fno;
		this.net_id = net_id;
		this.strength = strength;
		this.ap_name = ap_name;
		this.sec_type = sec_type;
	}

	public APInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "APInfo [fno=" + fno + ", net_id=" + net_id + ", strength="
				+ strength + ", ap_name=" + ap_name + ", sec_type=" + sec_type
				+ "]";
	}

}
