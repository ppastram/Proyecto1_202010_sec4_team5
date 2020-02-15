package model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import com.google.gson.Gson;

import Infracciones.Example;
import model.data_structures.*;

/**
 * Definicion del modelo del mundo
 *
 */
public class Modelo 
{
	/**
	 * Lista-pila de tipo Comparendos
	 */
	private LinkedList<Comparendo> datos1;

	/**
	 * Lista-cola de tipo Comparendos
	 */
	private LinkedQueue<Comparendo> datos2;

	/**
	 * Constructor del modelo del mundo
	 */
	public Modelo()
	{	
		Gson gson = new Gson();
		BufferedReader br = null;
		datos1 = new LinkedList<>();
		datos2 = new LinkedQueue<>();

		try
		{
			br = new BufferedReader(new FileReader("./data/comparendos_dei_2018_small.geojson"));
			Example result = gson.fromJson(br, Example.class);

			for(int  i = 0; i < result.getFeatures().size(); i ++)
			{
				int objective = result.getFeatures().get(i).getProperties().getOBJECTID();
				String fecha_hora = result.getFeatures().get(i).getProperties().getFECHAHORA();
				String medio_dete = result.getFeatures().get(i).getProperties().getMEDIODETE();
				String clase_vehi = result.getFeatures().get(i).getProperties().getCLASEVEHI();
				String tipo_servi = result.getFeatures().get(i).getProperties().getTIPOSERVI();
				String infraccion = result.getFeatures().get(i).getProperties().getINFRACCION();
				String des_infrac = result.getFeatures().get(i).getProperties().getDESINFRAC();
				String localidad = result.getFeatures().get(i).getProperties().getLOCALIDAD();
				double cordenada1 = result.getFeatures().get(i).getGeometry().getCoordinates().get(0);
				double cordenada2 = result.getFeatures().get(i).getGeometry().getCoordinates().get(1);

				Comparendo actual = new Comparendo(objective, fecha_hora, medio_dete, clase_vehi, tipo_servi, infraccion, des_infrac, localidad, cordenada1, cordenada2);
				datos1.addNodeFirst(actual);
				datos2.enqueue(actual);
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			if(br != null)
			{
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Servicio de consulta de numero de elementos presentes en el modelo de la pila
	 * @return numero de elementos presentes en el modelo
	 */
	public int darTamanoComparendos()
	{
		return datos1.getSize();
	}
	
	/**
	 * Muestra la informacion con el mayor OBJECTID encontrado en la lista
	 * @return El comparendo con maoyr objectid encontrado
	 */
	public String darObjectidMayor()
	{
		String mensaje = " ";
		Comparendo actual = datos1.seeObjetc(0);
		
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			Comparendo elemento = it.next();
			if(elemento.getObjective() > actual.getObjective())
			{
				actual = elemento;
			}
		}
		
		mensaje = actual.getObjective() + ", " + actual.getFecha_hora() + ", " + actual.getInfraccion() + ", " + 
                  actual.getClase_vehi() + ", " + actual.getTipo_servi() + ", " +  actual.getLocalidad();
		
		return mensaje;
	}
	
	/**
	 * Determina la sonaMiniMax para despues ser utilizado
	 * @return Los limites teniendo en cuenta el rectangulo (la menor latitud, la menor longitud) y (la mayor latitud, la mayor longitud).
	 */
	public double[] darZonaMiniMax()
	{
		double[] rango = new double[4];
		
		double mayLa = 0;
		double mayLo = datos1.seeObjetc(0).getCordenadas()[0];
		double menLa = datos1.seeObjetc(0).getCordenadas()[1];
		double menLo = 0;
		
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			Comparendo elemento = it.next();
			if(elemento.getCordenadas()[0] > mayLo)
			{
				mayLo = elemento.getCordenadas()[0];
			}
			else if(elemento.getCordenadas()[0] < menLo)
			{
				menLo = elemento.getCordenadas()[0];
			}
			
			if(elemento.getCordenadas()[1] > mayLa)
			{
				mayLa = elemento.getCordenadas()[1];
			}
			else if(elemento.getCordenadas()[1] < menLa)
			{
				menLa = elemento.getCordenadas()[1];
			}
		}
		
		rango[0] = menLo;
		rango[1] = mayLo;
		rango[2] = menLa;
		rango[3] = mayLa;
		return rango;
	}

	/**
	 * Retona una lista con los comparendos que se encuntran dentro de la ZonaMiniMax
	 * @param pLongitudIn Longitud Inferior
	 * @param pLatitudSu Latitud Superior
	 * @param pLongitudSu Longitud Superior
	 * @param pLatitudIn Latitud Inferior
	 * @return Lista con los comparendos encontrados
	 */
	public LinkedList<Comparendo> darComparendosZonaMinimax(double pLongitudIn, double pLatitudSu, double pLongitudSu, double pLatitudIn)
	{
		LinkedList<Comparendo> nueva = new LinkedList<Comparendo>();
		
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			Comparendo elemento = it.next();
			if(elemento.getCordenadas()[0] >= pLongitudIn && elemento.getCordenadas()[1] <= pLatitudSu && elemento.getCordenadas()[0] <= pLongitudSu)
			{
				nueva.addNodeFirst(elemento);
			}
		}
		return nueva;
	}
}
