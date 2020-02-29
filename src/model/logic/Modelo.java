package model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
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
			br = new BufferedReader(new FileReader("./data/comparendos_dei_2018.geojson"));
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
			if(elemento.getCordenadas()[0] >= pLongitudIn && elemento.getCordenadas()[1] <= pLatitudSu && elemento.getCordenadas()[0] <= pLongitudSu && elemento.getCordenadas()[1] >= pLongitudIn)
			{
				nueva.addNodeFirst(elemento);
			}
		}
		return nueva;
	}

	/**
	 * Crea un arreglo comparable de los comparendos para poder ser utilizado en los sorts
	 * @return Arreglo Comparable<Comparendos>
	 */
	public Comparable<Comparendo>[] copiarComparendosArreglo()
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				nuevo[i] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
			}
		}

		return nuevo;
	}	

	/**
	 * Recibe una localidad dada por parametro y retorna el primer comparendo con esa localidad, si no encuntra manda un mensaje de error
	 * @param pLocalidad Localidad donde se hizo el comparendo
	 * @return Mensaje del comparendo con la informcaion basica si lo encontro, si no un mensaje diciendo que no lo encontro
	 */
	public String darPrimerComparendoLocalidad(String pLocalidad) 
	{
		String mensaje = "";
		boolean encontrado = false;

		Comparable<Comparendo> copia_Comparendos [ ] = copiarComparendosArreglo();
		Comparendo nuevo = null;

		for(int i = 0; i < copia_Comparendos.length && !encontrado; i++)
		{
			nuevo = (Comparendo) copia_Comparendos[i];
			if(nuevo.getLocalidad().equals(pLocalidad))
			{
				mensaje = nuevo.getObjective() + ", " + nuevo.getFecha_hora() + ", " + nuevo.getInfraccion() + ", " + nuevo.getClase_vehi() + ", " + nuevo.getTipo_servi() + ", " + nuevo.getLocalidad();
				encontrado = true;
			}
		}

		if(!encontrado)
		{
			mensaje = "No se encontro en el archivo un comparendo con esta localidad.";
		}
		return mensaje;
	}

	/**
	 * Recibe una localidad dada por parametro y retorna el primer comparendo con esa infraccion, si no encuentra manda un mensaje de error
	 * @param pInfracion Infraccion del comparendo
	 * @return Mensaje del comparendo con la informcaion basica si lo encontro, si no un mensaje diciendo que no lo encontro
	 */
	public String darPrimerComparendoInfraccion(String pInfracion) 
	{
		String mensaje = "";
		boolean encontrado = false;

		Comparable<Comparendo> copia_Comparendos [ ] = copiarComparendosArreglo();
		Comparendo nuevo = null;

		for(int i = 0; i < copia_Comparendos.length && !encontrado; i++)
		{
			nuevo = (Comparendo) copia_Comparendos[i];
			if(nuevo.getInfraccion().equals(pInfracion))
			{
				mensaje = nuevo.getObjective() + ", " + nuevo.getFecha_hora() + ", " + nuevo.getInfraccion() + ", " + nuevo.getClase_vehi() + ", " + nuevo.getTipo_servi() + ", " + nuevo.getLocalidad();
				encontrado = true;
			}
		}

		if(!encontrado)
		{
			mensaje = "No se encontro en el archivo un comparendo con esa infraccion.";
		}
		return mensaje;
	}

	public Comparable<Comparendo>[] darComparendosRegistradosFecha(String pFecha)
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		int j = 0;
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				if(elementoActual.getFecha_hora().equals(pFecha)) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		return nuevo;
	}

	public static <E> void sort(E[] a, Comparator<? super E> comp) {
		mergeSort(a, 0, a.length - 1, comp);
	}


	private static <E> void mergeSort(E[] a, int from, int to, Comparator<? super E> comp) {
		if (from == to)
			return;
		int mid = (from + to) / 2;
		// Sort the first and the second half
		mergeSort(a, from, mid, comp);
		mergeSort(a, mid + 1, to, comp);
		merge(a, from, mid, to, comp);
	}

	@SuppressWarnings("unchecked")
	private static <E> void merge(E[] a, int from, int mid, int to, Comparator<? super E> comp) {
		int n = to - from + 1;
		Object[] values = new Object[n];

		int fromValue = from;

		int middleValue = mid + 1;

		int index = 0;

		while (fromValue <= mid && middleValue <= to) {
			if (comp.compare(a[fromValue], a[middleValue]) < 0) {
				values[index] = a[fromValue];
				fromValue++;
			} else {
				values[index] = a[middleValue];
				middleValue++;
			}
			index++;
		}

		while (fromValue <= mid) {
			values[index] = a[fromValue];
			fromValue++;
			index++;
		}
		while (middleValue <= to) {
			values[index] = a[middleValue];
			middleValue++;
			index++;
		}

		for (index = 0; index < n; index++)
			a[from + index] = (E) values[index];
	}
}
