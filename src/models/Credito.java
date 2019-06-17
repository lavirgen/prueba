package models;

import java.time.LocalDate;

import bbdd.BD_Tarjetas;

public class Credito extends Tarjeta{
	private double limite;

	public Credito(int numero, long cta, String nombre, String clave, double limite) {
		super(numero, cta, nombre,clave);
		// TODO Auto-generated constructor stub
		this.limite=limite;
	}
	

	public Credito(int numero, long cta, String nombre, LocalDate caducidad, String clave, double limite) {
		super(numero, cta, nombre, caducidad, clave);
		// TODO Auto-generated constructor stub
		this.limite=limite;
	}


	public double getLimite() {
		return limite;
	}

	@Override
	public String toString() {
		return "Credito ["+super.toString()+" limite=" + limite + "]";
	}

	public boolean sacar(BD_Tarjetas bd, double importe){
		if (importe>limite){
			System.out.println("Límite excedido");
			return false;
		}
			
		if (bd.BD_AltaMovimiento(new Movimiento(numero,importe,false))!=1)
			return false;
		
		return true;
		
	}
	
	

}
