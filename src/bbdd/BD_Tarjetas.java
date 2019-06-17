package bbdd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Vector;

import models.*;
import exceptions.*;
import models.Movimiento;
import models.Cuenta;
public class BD_Tarjetas extends BD_Conector {

	private static Statement s;
	private static ResultSet reg;
	private static int res;

	public BD_Tarjetas(String file) {
		super(file);
	}
	
	public Vector <Cuenta> BD_BuscarCuentas(String titular){
		String cadenaSQL = "Select * from cuentas where titular1='" + titular + "' OR titular2='" +titular +"' OR titular3='"+ titular+"'";
		Vector<Cuenta> cuentas = new Vector<Cuenta>();
		try {
			this.abrir();
			s = c.createStatement();
			reg = s.executeQuery(cadenaSQL);
			while (reg.next()) {
				java.sql.Date t=reg.getDate("fecha");
				LocalDate fecha = t.toLocalDate();
				cuentas.add(new Cuenta(reg.getLong("número"), reg.getString("titular1"),reg.getString("titular2"),reg.getString("titular3"),reg.getDouble("saldo"),fecha
						));
			}
			s.close();
			this.cerrar();
			return cuentas;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public int BD_InsertarTarjeta(Tarjeta t) {
		String cadenaSQL;
		if (t instanceof Credito)
			cadenaSQL = "INSERT INTO tarjetas (numero, cuenta, titular, tipo, caducidad, clave, limite) VALUES("+ t.getNumero() + "," + t.getCta() + ",'" + t.getNombre() + "','C','"+ t.getCaducidad() + "','" + t.getClave()+ "',"+((Credito)t).getLimite()+")";		
			
		else
			cadenaSQL = "INSERT INTO tarjetas (numero, cuenta, titular, tipo, caducidad, clave ) VALUES("+ t.getNumero() + "," + t.getCta() + ",'" + t.getNombre() + "','D','"+ t.getCaducidad() + "','"+ t.getClave() +"')";
		try {
			this.abrir();
			s = c.createStatement();
			res = s.executeUpdate(cadenaSQL);
			s.close();
			this.cerrar();
			return res;
		} catch (SQLException e) {
			return -1;
		}
	}
	public Tarjeta BD_BuscarTarjeta(int numero, String clave) throws TarjetaBloqueada{
		String cadenaSQL = "Select * from tarjetas where numero=" + numero + " AND clave='" +clave+"'";
		Tarjeta t=new Tarjeta(0);
		try {
			this.abrir();
			s = c.createStatement();
			reg = s.executeQuery(cadenaSQL);
			if (reg.next()) {
				if (reg.getBoolean("bloqueada")==true){
					s.close();
					this.cerrar();
					throw new TarjetaBloqueada("Tarjeta bloqueada");}
				if (reg.getString("tipo").equals("C"))
					t=new Credito(reg.getInt("numero"),reg.getInt("cuenta"),reg.getString("titular"),null,null,reg.getDouble("limite"));
				else
					t=new Tarjeta(reg.getInt("numero"),reg.getInt("cuenta"),reg.getString("titular"),null,null);
			}
			s.close();
			this.cerrar();
			return t;
		} catch (SQLException e) {
			return null;
		}
	}
	public boolean BD_ActualizarSaldo(long cta, double importe){
		boolean realizada=false;
		String cadenaSQL = "Select saldo from cuentas where número=" + cta;
		try {
			this.abrir();
			s = c.createStatement();
			reg = s.executeQuery(cadenaSQL);
			if (reg.next()) {
				
			    double saldo=reg.getDouble("saldo");
				if (saldo<importe)
					System.out.println("No hay suficiente saldo");
				else{
					saldo=saldo-importe;
				
					
					String SQL = "update cuentas set saldo="+ saldo+" where número=" + cta;				
					realizada=true;							
					s.executeUpdate(SQL);
							
				
				}
			}
			s.close();
			this.cerrar();
			return realizada;
		} catch (SQLException e) {
			System.out.println("Problemas técnicos");
			return realizada;
		}
		
	}
	
	public int BD_AltaMovimiento(Movimiento m) {
		String cadenaSQL = "INSERT INTO movimientos (tarjeta,importe,cargado) VALUES("+ m.getTarjeta() +"," + m.getImporte() +","+ m.isCargado()+")";	
				
		try {
			this.abrir();
			s = c.createStatement();
			res = s.executeUpdate(cadenaSQL);
			s.close();
			this.cerrar();
			return res;
		} catch (SQLException e) {
			return -1;
		}
	}
	

public Cuenta buscarCuenta(String nCuenta){
	String cadenaSQL = "SELECT * from cuentas WHERE número='"+nCuenta+"'";
	
	Cuenta c2 = null;
	try {
		this.abrir();
		s = c.createStatement();
		reg = s.executeQuery(cadenaSQL);
		if (reg.next()) {
			java.sql.Date f = reg.getDate("fecha");
			LocalDate fBuena = f.toLocalDate();
			c2 = new Cuenta(reg.getInt("número"), reg.getString("titular1"), reg.getString("titular2"),
					reg.getString("titular3"), reg.getDouble("saldo"), fBuena);
		}
		s.close();
		this.cerrar();
		return c2;
	} catch (SQLException e) {
		System.out.println(e);

		return null;
	}
}
public void cobrarMovimientosCredito() {
	String cadenaSQL = "SELECT sum(importe) importes,tarjeta,cuenta FROM movimientos "
			+ "m,tarjetas t where cargado = 0 and t.numero = tarjeta group by tarjeta";
	try {
		this.abrir();
		/* Iniciamos la transacción */
		c.setAutoCommit(false);
		s = c.createStatement();
		reg = s.executeQuery(cadenaSQL);
		while (reg.next()) {
			String SQL = "update cuentas set saldo = saldo - '" + reg.getInt("importes") + "' where número="
					+ reg.getInt("cuenta");
			try {
				Statement ss = c.createStatement();
				ss.executeUpdate(SQL);
				ss.close();
				
			} catch (SQLException e) {
				c.rollback();
				c.setAutoCommit(true);
				System.out.println(e);
				
			}
		}
	
	
		String SQL2 = "update movimientos set cargado = 1 where cargado = 0";
	
		try{
			s.executeUpdate(SQL2);
			s.close();
			/* Ejecutamos de una vez todas las actualizaciones */
		
			c.commit();
		}
		/* Si hay un problema al ejecutar las actualizaciones, deshacemos los cambios */
		catch(SQLException e){
			c.rollback();
			c.setAutoCommit(true);
		}
		this.cerrar();
		
	} catch (SQLException e) {
		System.out.println(e);
		
		
	}
	
}

public int actualizarCargadoMov(int numT) {
	
	String cadena = "UPDATE movimientos SET cargado = 1 WHERE tarjeta = '" + numT + "'";
	
	try{
		this.abrir();
		s=c.createStatement();
		int filas=s.executeUpdate(cadena);
		s.close();
		this.cerrar();
		return filas;
	}
	catch ( SQLException e){			
		return -1;
	}
	
}
public int cargarMovimientos(int numT, double imp) {
	
	String cadena1 = "SELECT cuenta FROM tarjetas WHERE numero = '" + numT + "'";
	int numCuenta = 0;
	double saldo = 0;
	
	try {
		this.abrir();
		
		s=c.createStatement();
		reg = s.executeQuery(cadena1);
		if(reg.next()) {
			numCuenta = reg.getInt("cuenta");
			
			ResultSet reg2;
			
			String cadena2 = "SELECT saldo FROM cuentas WHERE número = '" + numCuenta + "'";
			
			try{
				
				reg2 = s.executeQuery(cadena2);
				if(reg2.next()) {
					saldo = reg2.getDouble("saldo");
					
					ResultSet reg3;
					double newSaldo = saldo - imp;
					
					String cadena3 = "UPDATE cuentas SET saldo = '" +newSaldo+ "' WHERE número = '" + numCuenta + "'";
							
					try{
						
					int filas=s.executeUpdate(cadena3);
					s.close();
					this.cerrar();
					return filas;
					}
					catch ( SQLException e){			
						return -1;
					}		
					
					
				}
				s.close();
				this.cerrar();
				return 0;
			}
			catch ( SQLException e){			
				return -1;
			}
		}
		s.close();
		
		this.cerrar();
		return 0;
	}
	catch ( SQLException e){			
		return -1;
	}	
}
public Vector<Movimiento> listadoMovimientos(int valor) {
	
	String cadena="SELECT * FROM movimientos WHERE cargado = '" + valor + "'";
	Vector<Movimiento> listaMovimientos=new Vector<Movimiento>();
	
	try{
		this.abrir();
		s=c.createStatement();
		reg=s.executeQuery(cadena);
		while ( reg.next()){
			listaMovimientos.add(new Movimiento(reg.getInt("tarjeta"), reg.getDouble("importe")));
		}	
		s.close();
		this.cerrar();
		return listaMovimientos;
	}
	catch ( SQLException e){			
		return null;
	}
}
}