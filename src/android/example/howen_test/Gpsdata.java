package example.howen_test;

public class Gpsdata {
	public int InfoType;
	public int Latitude;
	public int Longitude;
	public double High;
	public double Direct;
	public double Speed;
	public String GpsTime;
	float signalintensity;
	int statenumber;

	public Gpsdata() {

	}

	public Gpsdata(float signalintensity, int statenumber) {
		super();
		this.signalintensity = signalintensity;
		this.statenumber = statenumber;
	}

	public float getSignalintensity() {
		return signalintensity;
	}

	public void setSignalintensity(float signalintensity) {
		this.signalintensity = signalintensity;
	}

	public int getStatenumber() {
		return statenumber;
	}

	public void setStatenumber(int statenumber) {
		this.statenumber = statenumber;
	}

}
