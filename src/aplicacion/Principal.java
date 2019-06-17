package aplicacion;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Vector;

import bbdd.BD_Tarjetas;
import models.*;
import exceptions.*;
import models.Movimiento;
import models.Cuenta;

public class Principal {

	

	public static void main(String[] args) {
		BD_Tarjetas bd = new BD_Tarjetas("tarjetas");
		Scanner sc = new Scanner(System.in);
		Charset charset = Charset.forName("UTF-8");
		DateTimeFormatter fechaFormateada = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		int opc = 0;
		do {
			System.out.print("1 - Alta tarjeta\n2 - Sacar dinero\n3 - Cargar movimientos\n4 - Descubiertos + \n5 - Salir\n\tIntroduce opción: ");
			opc = sc.nextInt();
		
		switch (opc) {
		case 1:
			sc.nextLine();
			System.out.println("Introduce el titular: ");
			String titular = sc.nextLine();
			//Mostramos las cuentas de dicho titular
			Vector <Cuenta> cuentas=bd.BD_BuscarCuentas(titular);
			if (cuentas==null)
				System.out.println("Avise a sistemas");
			else
				if (cuentas.size()==0)
					System.out.println("No tiene ninguna cuenta en este banco");
				else{
					for(int i=0;i<cuentas.size();i++)
						System.out.println((i+1)+ " " + cuentas.get(i));
					int nc;
					do{
						System.out.println("Anota la cuenta de la que desees hacer la tarjeta");
						nc=sc.nextInt();
					}while(nc<=0 || nc>cuentas.size());
					sc.nextLine();
					System.out.println("Anota nombre del titular:");
					titular=sc.nextLine();
					System.out.println("Anota número:");
					int num=sc.nextInt();
					sc.nextLine();
					String tipo;
					do{
						System.out.println("Anota el tipo de tarjeta:");
						tipo=sc.nextLine();
						tipo=tipo.toUpperCase();
					}while(!tipo.equals("C") && !tipo.equals("D"));
					double limite;
					int error;
					String clave;
					boolean correcto;
					do{			
						correcto=true;
						System.out.println("Anota la clave:");
						clave=sc.nextLine();
						correcto=validar(clave);
						if (correcto){
							System.out.println("Repite la clave");
							String clave2=sc.nextLine();
							if (!clave.equals(clave2)){
									correcto=false;
									System.out.println("No coinciden las claves");
							}
						}
					}while(!correcto);
						
					
					if (tipo.equals("C")){
						System.out.println("Anota el límite:");
						limite=sc.nextDouble();
						error=bd.BD_InsertarTarjeta(new Credito(num,cuentas.get(nc-1).getNumero(),titular, clave,limite));
					}
					else
					{
						error=bd.BD_InsertarTarjeta(new Tarjeta(num,cuentas.get(nc-1).getNumero(), titular, clave));
					}
					
					if (error==0)
						System.out.println("Ya existe");
					else
						if (error==-1)
							System.out.println("Errores téctnicos");
						else
							System.out.println("Tarjeta dada de alta");
						
					
				}
			break;
					
		case 2:
			System.out.println("Anota tarjeta:");
			int numero=sc.nextInt();
			sc.nextLine();
			System.out.println("Anota clave:");
			String clave=sc.nextLine();
			try{
				Tarjeta t=bd.BD_BuscarTarjeta(numero,clave);
				if (t==null)
					System.out.println("Problemas técnicos");
				else
					if (t.getNumero()==0)
						System.out.println("Datos incorrectos");
					else{
						System.out.println("Anota importe:");
						double importe=sc.nextDouble();
						// Polimorfismo, va a entrar por el método sacar del objeto correspondiente
						if (!t.sacar(bd,importe))
							System.out.println("No se ha podido realizar la operación");
					}
			}
			catch(TarjetaBloqueada e){
				System.out.println("Tarjeta bloqueada, póngase en contacto con su banco");
				
			}
			break;
		case 4:
			Path file = Paths.get("incidencias.txt");

			try {
				BufferedReader reader = Files.newBufferedReader(file, charset);
				String line="";
				Vector <String> lineas = new Vector();
				while ((line = reader.readLine()) != null) {
					String[]datos = line.split(" ");
					String nCuenta = datos[0];
					Cuenta negativo = bd.buscarCuenta(nCuenta);
					if(negativo.getSaldo()<0) {
						try {
							LocalDate fechaNegativo = LocalDate.parse(datos[1], fechaFormateada);
							int dias =(int)(LocalDate.now().toEpochDay()-fechaNegativo.toEpochDay());
							System.out.println(negativo.toString());
							System.out.println("Debe "+(dias*2)+"euros");
						} catch (Exception e) {
							System.out.println("Fecha mal introducida");
							sc.nextLine();
						}
						lineas.add(line);
					}
				}
				reader.close();
				BufferedWriter writer = Files.newBufferedWriter(file, charset);
				for(int i=0;i<lineas.size();i++) {
					
					System.out.println(lineas.get(i));
					writer.write(lineas.get(i));
					writer.newLine();
				}
				writer.close();
				lineas.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
			
		case 3:
			System.out.println("Cargando los movimientos de las tarjetas de crédito");
			bd.cobrarMovimientosCredito(); 
			//Otra opción
			/*Vector<Movimiento> movimientos=bd.listadoMovimientos(0);
			
			for(int i = 0; i < movimientos.size(); i++) {
				
				int filas = bd.cargarMovimientos(movimientos.get(i).getTarjeta(), movimientos.get(i).getImporte());
				switch (filas){
				case 0:
					System.out.println("No se ha podido añadir un movimiento");
					break;
				case 1: 
					System.out.println("Movimiento añadido correctamente");	
					
					bd.actualizarCargadoMov(movimientos.get(i).getTarjeta());
					break;
				default:
					System.out.println("En este momento no podemos añadir movimientos. Inténtalo más tarde");
				}
			}*/
	}
	}while (opc!=5);
	}

	public static boolean validar(String clave){
		if (clave.length()!=4)
			return false;
		try{
		Integer.parseInt(clave);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
}
