package models;

import java.time.LocalDate;

public class Cuenta {
	private long numero;
	private String titular1,titular2,titular3;
	private double saldo;
	private LocalDate alta;
	
	public long getNumero() {
		return numero;
	}

	public void setNumero(long numero) {
		this.numero = numero;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public LocalDate getAlta() {
		return alta;
	}

	public void setAlta(LocalDate alta) {
		this.alta = alta;
	}

	public Cuenta(long numero, String titular1, String titular2, String titular3, double saldo, LocalDate alta) {
		super();
		this.numero = numero;
		this.titular1 = titular1;
		this.titular2 = titular2;
		this.titular3 = titular3;
		this.saldo = saldo;
		this.alta = alta;
	}

	@Override
	public String toString() {
		return "Cuenta [numero=" + numero + ", titular1=" + titular1 + ", titular2=" + titular2 + ", titular3="
				+ titular3 + ", saldo=" + saldo + ", alta=" + alta + "]";
	}
	
	
	
}
