import java.io.Serializable;

/**
 * Stores basic information about the neighbours.
 */
public class Neighbours implements Serializable {
	double lx, ly, hx, hy;
	String ip;
	double midx;
	double midy;

	public void setCoordinates(double x1, double x2, double y1, double y2,String ip) {
		lx = x1;
		ly = y1;
		hx = x2;
		hy = y2;
		midx =(lx + hx) / 2;
		midy =(ly + hy) / 2;
		this.ip=ip;
	}

	public double getLx() {
		return lx;
	}

	public void setLx(double lx) {
		this.lx = lx;
	}

	public double getLy() {
		return ly;
	}

	public void setLy(double ly) {
		this.ly = ly;
	}

	public double getHx() {
		return hx;
	}

	public void setHx(double hx) {
		this.hx = hx;
	}

	public double getHy() {
		return hy;
	}

	public void setHy(double hy) {
		this.hy = hy;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public double getMidx() {
		return midx;
	}

	public void setMidx(double midx) {
		this.midx = midx;
	}

	public double getMidy() {
		return midy;
	}

	public void setMidy(double midy) {
		this.midy = midy;
	}
	
	
}
