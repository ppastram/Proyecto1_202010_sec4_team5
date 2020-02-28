package view;

import model.logic.Modelo;

public class View 
{
	    /**
	     * Metodo constructor
	     */
	    public View()
	    {
	    	
	    }
	    
		public void printMenu()
		{
			System.out.println("1. Total de comparendos en el archivo");
			System.out.println("2. Mostrar la informacion del comparendo con el mayor OBJECTID encontrado");
			System.out.println("3. La zona Minimax de los comparendos definida como los límites inferior y superior de latitud y longitud en todo el archivo");
			System.out.println("4. Requerimiento 1A - Primer comparendo con la localidad dada");
			System.out.println("5. Requerimiento 2A - Primer comparendo con la infraccion dada \n");
		}

		public void printMessage(String mensaje) {

			System.out.println(mensaje);
		}		
		
		public void printModelo(Modelo modelo)
		{
			System.out.println(modelo);
		}
}
