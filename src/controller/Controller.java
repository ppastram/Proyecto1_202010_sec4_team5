package controller;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;

import model.logic.ComparadorInfraccion;
import model.logic.ComparadorInfraccionInverso;
import model.logic.Comparendo;
import model.logic.Modelo;
import view.View;

public class Controller {

	/* Instancia del Modelo*/
	private Modelo modelo;

	/* Instancia de la Vista*/
	private View view;

	/**
	 * Crear la vista y el modelo del proyecto
	 * @param capacidad tamaNo inicial del arreglo
	 */
	public Controller ()
	{
		view = new View();
		modelo = new Modelo();
	}

	/**
	 * Metodo run que corre el sistema
	 */
	public void run() 
	{
		Scanner lector = new Scanner(System.in);
		boolean fin = false;

		while( !fin )
		{
			view.printMenu();

			int option = lector.nextInt();
			switch(option){
			case 1:
				int tamanio = modelo.darTamanoComparendos();
				view.printMessage("El total de comparendos es de: " + tamanio);
				break;

			case 2:
				view.printMessage("El comparendo con el mayor OBJECTID es: " + modelo.darObjectidMayor() + " \n");
				break;

			case 3:
				view.printMessage("La zona MiniMax de los comparendos dentro del siguiente rango " + modelo.darZonaMiniMax()[0] + " a " + modelo.darZonaMiniMax()[1] + " y " + modelo.darZonaMiniMax()[2] + " a " + modelo.darZonaMiniMax()[3] + " es: \n");
				Iterator<Comparendo> resultado1 = modelo.darComparendosZonaMinimax(modelo.darZonaMiniMax()[0], modelo.darZonaMiniMax()[2],modelo.darZonaMiniMax()[1], modelo.darZonaMiniMax()[3]).iterator();
				while(resultado1.hasNext())
				{
					Comparendo elemento = resultado1.next();
					view.printMessage(elemento.getInfraccion() + ", " + elemento.getObjective() + ", " + elemento.getFecha_hora() + ", " + elemento.getClase_vehi() + ", " + elemento.getTipo_servi() + ", " + elemento.getLocalidad());
				}
				break;

			case 4:
				view.printMessage("Por favor ingresar la localidad para ser buscada en el archivo (En caso de ser Barrios Unidos ingresar BARRIOS)");
				String entrada1 = lector.next();
				if(entrada1.startsWith("BARRIOS"))
				{
					entrada1 = "BARRIOS UNIDOS";
				}
				view.printMessage("El primer comparendo en el archivo con la localidad de " + entrada1 + ":");
				view.printMessage(modelo.darPrimerComparendoLocalidad(entrada1) + "\n");
				break;

			case 5:
				view.printMessage("Por favor ingresar la infraccion para ser buscada en el archivo");
				String entrada2 = lector.next();
				view.printMessage("El primer comparendo en el archivo con la localidad de " + entrada2 + ":");
				view.printMessage(modelo.darPrimerComparendoInfraccion(entrada2) + "\n");
				break;

			case 6:
				view.printMessage("Por favor ingresar la fecha a realizar la consulta en la base de datos");
				String entrada3 = lector.next();
				Comparable<Comparendo> copia_Comparendos [ ] = modelo.darComparendosRegistradosFecha(entrada3);

				int j =0;
				Comparendo nuevo1 = null;
				for(int i = 0; i < copia_Comparendos.length ; i++)
				{
					nuevo1 = (Comparendo) copia_Comparendos[i];
					if(nuevo1 != null)
					{
						j++;
					}
				}

				Comparendo[] nuevo = new Comparendo[j];

				Comparendo nuevo2 = null;
				for(int i = 0; i < copia_Comparendos.length ; i++)
				{
					nuevo2 = (Comparendo) copia_Comparendos[i];
					if(nuevo2 != null)
					{
						nuevo[i] = nuevo2;
					}
				}

				Comparator<Comparendo> comp = new ComparadorInfraccion();
				Modelo.sort(nuevo, comp);

				view.printMessage("Estos son los comparendos con la fecha ingresada, estan oredenados de mayor a menor por infraccion \n");

				for(int i = 0; i < nuevo.length; i++)
				{
					Comparendo nuevo3 = nuevo[i];
					System.out.println(nuevo3.getObjective() + ", " + nuevo3.getFecha_hora() + ", " + nuevo3.getInfraccion() + ", " + nuevo3.getClase_vehi() + ", " + nuevo3.getTipo_servi() + ", " + nuevo3.getLocalidad());
				}

				view.printMessage("El total de comparendos es de: " + j);
				break;

			case 7:
				view.printMessage("Por favor ingresar la infraccion a realizar la consulta en la base de datos");
				String entrada4 = lector.next();
				Comparable<Comparendo> copia_Comparendos1 [ ] = modelo.darComparendosRegistradosInfraccion(entrada4);

				int k =0;
				Comparendo nuevo4 = null;
				for(int i = 0; i < copia_Comparendos1.length ; i++)
				{
					nuevo4 = (Comparendo) copia_Comparendos1[i];
					if(nuevo4 != null)
					{
						k++;
					}
				}

				Comparendo[] nuevoI = new Comparendo[k];

				Comparendo nuevo5 = null;
				for(int i = 0; i < copia_Comparendos1.length ; i++)
				{
					nuevo5 = (Comparendo) copia_Comparendos1[i];
					if(nuevo5 != null)
					{
						nuevoI[i] = nuevo5;
					}
				}

				Comparator<Comparendo> compI = new ComparadorInfraccion();
				Modelo.sort(nuevoI, compI);

				// For para invertir la lista para que los objetos queden de menor a mayor

				Comparendo temporal; 
				int longitudDeArreglo = nuevoI.length;

				for (int x = 0; x < longitudDeArreglo / 2; x++) 
				{
					temporal = nuevoI[x];
					int indiceContrario = longitudDeArreglo - x - 1;
					nuevoI[x] = nuevoI[indiceContrario];
					nuevoI[indiceContrario] = temporal;
				}

				// Imprime los comparendos organizados por fecha de menor a mayor dada la infraccion que recibio

				view.printMessage("Estos son los comparendos con la infraccion ingresada, estan oredenados de menor a mayor por fecha \n");

				for(int i = 0; i < nuevoI.length; i++)
				{
					Comparendo nuevo6 = nuevoI[i];
					System.out.println(nuevo6.getObjective() + ", " + nuevo6.getFecha_hora() + ", " + nuevo6.getInfraccion() + ", " + nuevo6.getClase_vehi() + ", " + nuevo6.getTipo_servi() + ", " + nuevo6.getLocalidad());
				}

				view.printMessage("El total de comparendos es de: " + k);
				break;

			case 8:
				view.printMessage("Por favor ingresar las fehas para mostrar la informacion");
				view.printMessage("Fecha 1:");
				String entrada5 = lector.next();
				view.printMessage("Fecha 2:");
				String entrada6 = lector.next();
				String[] fecha1 = modelo.darComparendosFecha1(entrada5);
				String[] fecha2 = modelo.darComparendosFecha2(entrada6);

				view.printMessage("Infraccion   | " + entrada5 + " | " + entrada6);
				for(int i = 0; i < fecha1.length; i++)
				{
					view.printMessage(fecha1[i]+fecha2[i]);
				}
				break;

			case 9:
				String[] particular = modelo.darNumerosComparendosTipoServicioParticular();
				String[] publico = modelo.darNumerosComparendosTipoServicioPublico();

				view.printMessage("Infraccion   | Particular | Publico");
				for(int i = 0; i < particular.length; i++)
				{
					view.printMessage(particular[i]+publico[i]);
				}
				break;

			case 10:
				view.printMessage("Por favor ingresar una localidad, fecha de inicio y fecha final para mostrar los comparendos");
				view.printMessage("Localidad: ");
				String entrada7 = lector.next();
				view.printMessage("Fecha de inicio: ");
				String entrada8 = lector.next();
				view.printMessage("Fecha de fin: ");
				String entrada9 = lector.next();

				view.printMessage("Comparación de comparendos en " + entrada7 + " del "+ entrada8 + " al " + entrada9);
				Comparable<Comparendo> comparadorLF[] = modelo.darComparendos1C(entrada7, entrada8, entrada9);

				int v =0;
				Comparendo ok1 = null;
				for(int i = 0; i < comparadorLF.length ; i++)
				{
					ok1 = (Comparendo) comparadorLF[i];
					if(ok1 != null)
					{
						v++;
					}
				}

				Comparendo[] nuevoLF = new Comparendo[v];

				Comparendo actual1 = null;
				for(int i = 0; i < comparadorLF.length ; i++)
				{
					actual1 = (Comparendo) comparadorLF[i];
					if(actual1 != null)
					{
						nuevoLF[i] = actual1;
					}
				}

				Comparator<Comparendo> compOK = new ComparadorInfraccionInverso();
				Modelo.sort(nuevoLF, compOK);
				
				for(int i = 0; i < nuevoLF.length ; i++)
				{
					System.out.println(nuevoLF[i].getInfraccion()+nuevoLF[i].getLocalidad());
				}
				
				break;

			case 11:
				break;

			case 12:
				break;

			case 13:
				view.printMessage("Hasta pronto"); 
				lector.close();
				fin = true;
				break;

			default: 
				view.printMessage("Opcion Invalida !!");
				break;
			}	
		}
	}	
}
