/**
 * 
 */
package models;

import java.time.LocalDate;

import bbdd.BD_Tarjetas;

/**
 * @author Bego
 *
 */
public class Tarjeta {
	protected int numero;
	protected long cta;
	private String nombre;
	private LocalDate caducidad;
	private String clave;
	private boolean bloqueada;
	
	
	public Tarjeta(int numero) {
		super();
		this.numero = numero;
	}
	public Tarjeta(int numero, long cta, String nombre,LocalDate caducidad, String clave) {
		this.numero = numero;
		this.cta = cta;
		this.nombre = nombre;
		this.caducidad=caducidad;
		this.clave=clave;
	
	}
	public Tarjeta(int numero, long cta, String nombre, String clave) {

		this.numero = numero;
		this.cta = cta;
		this.nombre = nombre;
		this.caducidad=LocalDate.now().plusYears(1);
		this.clave=clave;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	

	public int getNumero() {
		return numero;
	}

	public long getCta() {
		return cta;
	}

	public String getNombre() {
		return nombre;
	}

	public LocalDate getCaducidad() {
		return caducidad;
	}
	
	public boolean sacar(BD_Tarjetas bd, double importe){
		if (bd.BD_ActualizarSaldo(cta, importe)){
			if (bd.BD_AltaMovimiento(new Movimiento(numero,importe,true))!=1)
				return false;
		}
		return true;
		
	}

}
