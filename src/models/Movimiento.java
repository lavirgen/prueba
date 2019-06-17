package models;

public class Movimiento {
	private int tarjeta;
	private double importe;
	private boolean cargado;
	public Movimiento(int tarjeta, double importe, boolean cargado) {
		super();
		this.tarjeta = tarjeta;
		this.importe = importe;
		this.cargado = cargado;
	}
	public Movimiento(int tarjeta, double importe) {
		super();
		this.tarjeta = tarjeta;
		this.importe = importe;
	}
	public int getTarjeta() {
		return tarjeta;
	}
	public void setTarjeta(int tarjeta) {
		this.tarjeta = tarjeta;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public boolean isCargado() {
		return cargado;
	}
	public void setCargado(boolean cargado) {
		this.cargado = cargado;
	}
	
	
}
