package models;

public class Datos {
	
	private int tarjeta;
	private int cuenta;
	private double importe;
	
	public Datos(int tarjeta, int cuenta, double importe) {
		super();
		this.tarjeta = tarjeta;
		this.cuenta = cuenta;
		this.importe = importe;
	}

	public int getTarjeta() {
		return tarjeta;
	}

	public int getCuenta() {
		return cuenta;
	}

	public double getImporte() {
		return importe;
	}

	@Override
	public String toString() {
		return "Datos [tarjeta=" + tarjeta + ", cuenta=" + cuenta + ", importe=" + importe + "]";
	}
	
	
	
}
