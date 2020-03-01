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

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen la localidad igual a recibida por parametro
	 * @param pFecha Fecha de cuando se hizo el comparendo
	 * @return Arrgelo Comparable de comparendos que cumplen la condicion
	 */
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

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen la infraccion igual a recibida por parametro
	 * @param pInfraccion Infraccion del comparendo
	 * @return Arrgelo Comparable de comparendos que cumplen la condicion
	 */
	public Comparable<Comparendo>[] darComparendosRegistradosInfraccion(String pInfraccion)
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
				if(elementoActual.getInfraccion().equals(pInfraccion)) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		return nuevo;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen tipo de servicio particular
	 * @return Arrgelo Comparable de comparendos que son de tipo servicio particular
	 */
	public Comparable<Comparendo>[] darComparendosTipoParticular()
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
				if(elementoActual.getTipo_servi().equals("Particular")) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		return nuevo;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen tipo de servicio publico
	 * @return Arrgelo Comparable de comparendos que son de tipo servicio publico
	 */
	public Comparable<Comparendo>[] darComparendosTipoPublico()
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
				if(elementoActual.getTipo_servi().equals("Público")) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		return nuevo;
	}

	public int darNumeroComparendosInfracion(String pInfraccion)
	{
		int i = 0;
		if(pInfraccion.startsWith("C02"))
		{
			i++;
		}
		return i;
	}

	public String[] darNumerosComparendosTipoServicioParticular()
	{
		Comparable<Comparendo>[] aParticular = darComparendosTipoParticular();
		String infraciones[] = new String[116];

		int A01 = 0;int A02 = 0;int A03 = 0;int A04 = 0;int A05 = 0;int A06 = 0;int A07 = 0;int A08 = 0;int A09 = 0;int A10 = 0;int A11 = 0;int A12 = 0;
		int B01 = 0;int B02 = 0;int B03 = 0;int B04 = 0;int B05 = 0;int B06 = 0;int B07 = 0;int B09 = 0;int B10 = 0;int B11 = 0;int B12 = 0;int B13 = 0;int B14 = 0;int B15 = 0;int B16 = 0;int B17 = 0;int B18 = 0;int B19 = 0;int B20 = 0;int B21 = 0;int B22 = 0;int B23 = 0;
		int C01 = 0;int C02 = 0;int C03 = 0;int C04 = 0;int C05 = 0;int C06 = 0;int C07 = 0;int C08 = 0;int C09 = 0;int C10 = 0;int C11 = 0;int C12 = 0;int C13 = 0;int C14 = 0;int C15 = 0;int C16 = 0;int C17 = 0;int C18 = 0;int C19 = 0;int C20 = 0;int C21 = 0;int C22 = 0;int C23 = 0;int C24 = 0;int C25 = 0;int C26 = 0;int C27 = 0;int C28 = 0;int C29 = 0;int C30 = 0;int C31 = 0;int C32 = 0;int C33 = 0;int C34 = 0;int C35 = 0;int C36 = 0;int C37 = 0;int C38 = 0;int C39 = 0;int C40 = 0;
		int D01 = 0;int D02 = 0;int D03 = 0;int D04 = 0;int D05 = 0;int D06 = 0;int D07 = 0;int D08 = 0;int D09 = 0;int D11 = 0;int D12 = 0;int D13 = 0;int D14 = 0;int D15 = 0;int D16 = 0;int D17 = 0;
		int E02 = 0;int E04 = 0;
		int F01 = 0;int F02 = 0;int F03 = 0;int F04 = 0;int F05 = 0;int F06 = 0;int F07 = 0;int F08 = 0;int F09 = 0;int F11 = 0;int F12 = 0;
		int G01 = 0;
		int H01 = 0;int H02 = 0;int H03 = 0;int H04 = 0;int H05 = 0;int H06 = 0;int H07 = 0;int H08 = 0;int H10 = 0;int H11 = 0;int H13 = 0;
		int I01 = 0;int I02 = 0;

		for(int i = 0; i < aParticular.length ; i++)
		{
			Comparendo actual = (Comparendo) aParticular[i];
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01"))
					A01 ++;
				if(actual.getInfraccion().startsWith("A02"))
					A02 ++;
				if(actual.getInfraccion().startsWith("A03"))
					A03 ++;
				if(actual.getInfraccion().startsWith("A04"))
					A04 ++;
				if(actual.getInfraccion().startsWith("A05"))
					A05 ++;
				if(actual.getInfraccion().startsWith("A06"))
					A06 ++;
				if(actual.getInfraccion().startsWith("A07"))
					A07 ++;
				if(actual.getInfraccion().startsWith("A08"))
					A08 ++;
				if(actual.getInfraccion().startsWith("A09"))
					A09 ++;
				if(actual.getInfraccion().startsWith("A10"))
					A10 ++;
				if(actual.getInfraccion().startsWith("A11"))
					A11 ++;
				if(actual.getInfraccion().startsWith("A12"))
					A12 ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01"))
					B01 ++;
				if(actual.getInfraccion().startsWith("B02"))
					B02 ++;
				if(actual.getInfraccion().startsWith("B03"))
					B03 ++;
				if(actual.getInfraccion().startsWith("B04"))
					B04 ++;
				if(actual.getInfraccion().startsWith("B05"))
					B05 ++;
				if(actual.getInfraccion().startsWith("B06"))
					B06 ++;
				if(actual.getInfraccion().startsWith("B07"))
					B07 ++;
				if(actual.getInfraccion().startsWith("B09"))
					B09 ++;
				if(actual.getInfraccion().startsWith("B10"))
					B10 ++;
				if(actual.getInfraccion().startsWith("B11"))
					B11 ++;
				if(actual.getInfraccion().startsWith("B12"))
					B12 ++;
				if(actual.getInfraccion().startsWith("B13"))
					B13 ++;
				if(actual.getInfraccion().startsWith("B14"))
					B14 ++;
				if(actual.getInfraccion().startsWith("B15"))
					B15 ++;
				if(actual.getInfraccion().startsWith("B16"))
					B16 ++;
				if(actual.getInfraccion().startsWith("B17"))
					B17 ++;
				if(actual.getInfraccion().startsWith("B18"))
					B18 ++;
				if(actual.getInfraccion().startsWith("B19"))
					B19 ++;
				if(actual.getInfraccion().startsWith("B20"))
					B20 ++;
				if(actual.getInfraccion().startsWith("B21"))
					B21 ++;
				if(actual.getInfraccion().startsWith("B22"))
					B22 ++;
				if(actual.getInfraccion().startsWith("B23"))
					B23 ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01"))
					C01 ++;
				if(actual.getInfraccion().startsWith("C02"))
					C02 ++;
				if(actual.getInfraccion().startsWith("C03"))
					C03 ++;
				if(actual.getInfraccion().startsWith("C04"))
					C04 ++;
				if(actual.getInfraccion().startsWith("C05"))
					C05 ++;
				if(actual.getInfraccion().startsWith("C06"))
					C06 ++;
				if(actual.getInfraccion().startsWith("C07"))
					C07 ++;
				if(actual.getInfraccion().startsWith("C08"))
					C08 ++;
				if(actual.getInfraccion().startsWith("C09"))
					C09 ++;
				if(actual.getInfraccion().startsWith("C10"))
					C10 ++;
				if(actual.getInfraccion().startsWith("C11"))
					C11 ++;
				if(actual.getInfraccion().startsWith("C12"))
					C12 ++;
				if(actual.getInfraccion().startsWith("C13"))
					C13 ++;
				if(actual.getInfraccion().startsWith("C14"))
					C14 ++;
				if(actual.getInfraccion().startsWith("C15"))
					C15 ++;
				if(actual.getInfraccion().startsWith("C16"))
					C16 ++;
				if(actual.getInfraccion().startsWith("C17"))
					C17 ++;
				if(actual.getInfraccion().startsWith("C18"))
					C18 ++;
				if(actual.getInfraccion().startsWith("C19"))
					C19 ++;
				if(actual.getInfraccion().startsWith("C20"))
					C20 ++;
				if(actual.getInfraccion().startsWith("C21"))
					C21 ++;
				if(actual.getInfraccion().startsWith("C22"))
					C22 ++;
				if(actual.getInfraccion().startsWith("C23"))
					C23 ++;
				if(actual.getInfraccion().startsWith("C24"))
					C24 ++;
				if(actual.getInfraccion().startsWith("C25"))
					C25 ++;
				if(actual.getInfraccion().startsWith("C26"))
					C26 ++;
				if(actual.getInfraccion().startsWith("C27"))
					C27 ++;
				if(actual.getInfraccion().startsWith("C28"))
					C28 ++;
				if(actual.getInfraccion().startsWith("C29"))
					C29 ++;
				if(actual.getInfraccion().startsWith("C30"))
					C30 ++;
				if(actual.getInfraccion().startsWith("C31"))
					C31 ++;
				if(actual.getInfraccion().startsWith("C32"))
					C32 ++;
				if(actual.getInfraccion().startsWith("C33"))
					C33 ++;
				if(actual.getInfraccion().startsWith("C34"))
					C34 ++;
				if(actual.getInfraccion().startsWith("C35"))
					C35 ++;
				if(actual.getInfraccion().startsWith("C36"))
					C36 ++;
				if(actual.getInfraccion().startsWith("C37"))
					C37 ++;
				if(actual.getInfraccion().startsWith("C38"))
					C38 ++;
				if(actual.getInfraccion().startsWith("C39"))
					C39 ++;
				if(actual.getInfraccion().startsWith("C40"))
					C40 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01"))
					D01 ++;
				if(actual.getInfraccion().startsWith("D02"))
					D02 ++;
				if(actual.getInfraccion().startsWith("D03"))
					D03 ++;
				if(actual.getInfraccion().startsWith("D04"))
					D04 ++;
				if(actual.getInfraccion().startsWith("D05"))
					D05 ++;
				if(actual.getInfraccion().startsWith("D06"))
					D06 ++;
				if(actual.getInfraccion().startsWith("D07"))
					D07 ++;
				if(actual.getInfraccion().startsWith("D08"))
					D08 ++;
				if(actual.getInfraccion().startsWith("D09"))
					D09 ++;
				if(actual.getInfraccion().startsWith("D11"))
					D11 ++;
				if(actual.getInfraccion().startsWith("D12"))
					D12 ++;
				if(actual.getInfraccion().startsWith("D13"))
					D13 ++;
				if(actual.getInfraccion().startsWith("D14"))
					D14 ++;
				if(actual.getInfraccion().startsWith("D15"))
					D15 ++;
				if(actual.getInfraccion().startsWith("D16"))
					D16 ++;
				if(actual.getInfraccion().startsWith("D17"))
					D17 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E02"))
					E02 ++;
				if(actual.getInfraccion().startsWith("E04"))
					E04 ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01"))
					F01 ++;
				if(actual.getInfraccion().startsWith("F02"))
					F02 ++;
				if(actual.getInfraccion().startsWith("F03"))
					F03 ++;
				if(actual.getInfraccion().startsWith("F04"))
					F04 ++;
				if(actual.getInfraccion().startsWith("F05"))
					F05 ++;
				if(actual.getInfraccion().startsWith("F06"))
					F06 ++;
				if(actual.getInfraccion().startsWith("F07"))
					F07 ++;
				if(actual.getInfraccion().startsWith("F08"))
					F08 ++;
				if(actual.getInfraccion().startsWith("F09"))
					F09 ++;
				if(actual.getInfraccion().startsWith("F11"))
					F11 ++;
				if(actual.getInfraccion().startsWith("F12"))
					F12 ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01"))
					G01 ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01"))
					H01 ++;
				if(actual.getInfraccion().startsWith("H02"))
					H02 ++;
				if(actual.getInfraccion().startsWith("H03"))
					H03 ++;
				if(actual.getInfraccion().startsWith("H04"))
					H04 ++;
				if(actual.getInfraccion().startsWith("H05"))
					H05 ++;
				if(actual.getInfraccion().startsWith("H06"))
					H06 ++;
				if(actual.getInfraccion().startsWith("H07"))
					H07 ++;
				if(actual.getInfraccion().startsWith("H08"))
					H08 ++;
				if(actual.getInfraccion().startsWith("H10"))
					H10 ++;
				if(actual.getInfraccion().startsWith("H11"))
					H11 ++;
				if(actual.getInfraccion().startsWith("H13"))
					H13 ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01"))
					I01 ++;
				if(actual.getInfraccion().startsWith("I02"))
					I02 ++;
			}
		}

		infraciones[0] = "A01          | " + A01;infraciones[1] = "A02          | " + A02;infraciones[2] = "A03          | " + A03;infraciones[3] = "A04          | " + A04;infraciones[4] = "A05          | " + A05;
		infraciones[5] = "A06          | " + A06;infraciones[6] = "A07          | " + A07;infraciones[7] = "A08          | " + A08;infraciones[8] = "A09          | " + A09;infraciones[9] = "A10          | " + A10;
		infraciones[10] = "A11          | " + A11;infraciones[11] = "A12          | " + A12;infraciones[12] = "B01          | " + B01;infraciones[12] = "B01          | " + B01;infraciones[13] = "B02          | " + B02;
		infraciones[14] = "B03          | " + B03;infraciones[15] = "B04          | " + B04;infraciones[16] = "B05          | " + B05;infraciones[17] = "B06          | " + B06;infraciones[18] = "B07          | " + B07;
		infraciones[19] = "B09          | " + B09;infraciones[20] = "B10          | " + B10;infraciones[21] = "B11          | " + B11;infraciones[22] = "B12          | " + B12;
		infraciones[23] = "B13          | " + B13;infraciones[24] = "B14          | " + B14;infraciones[25] = "B15          | " + B15;infraciones[26] = "B16          | " + B16;infraciones[27] = "B17          | " + B17;
		infraciones[28] = "B18          | " + B18;infraciones[29] = "B19          | " + B19;infraciones[30] = "B20          | " + B20;infraciones[31] = "B21          | " + B21;infraciones[32] = "B22          | " + B22;
		infraciones[33] = "B23          | " + B23;infraciones[34] = "C01          | " + C01;infraciones[35] = "C02          | " + C02;infraciones[36] = "C03          | " + C03;infraciones[37] = "C04          | " + C04;
		infraciones[37] = "C05          | " + C05;infraciones[38] = "C06          | " + C06;infraciones[39] = "C07          | " + C07;infraciones[40] = "C08          | " + C08;infraciones[41] = "C09          | " + C09;
		infraciones[42] = "C10          | " + C10;infraciones[43] = "C11          | " + C11;infraciones[44] = "C12          | " + C12;infraciones[45] = "C13          | " + C13;infraciones[46] = "C14          | " + C14;
		infraciones[47] = "C15          | " + C15;infraciones[48] = "C16          | " + C16;infraciones[49] = "C17          | " + C17;infraciones[50] = "C18          | " + C18;infraciones[51] = "C19          | " + C19;
		infraciones[52] = "C20          | " + C20;infraciones[53] = "C21          | " + C21;infraciones[54] = "C22          | " + C22;infraciones[55] = "C23          | " + C23;infraciones[56] = "C24          | " + C24;
		infraciones[57] = "C25          | " + C25;infraciones[58] = "C26          | " + C26;infraciones[59] = "C27          | " + C27;infraciones[60] = "C28          | " + C28;infraciones[61] = "C29          | " + C29;
		infraciones[62] = "C30          | " + C30;infraciones[63] = "C31          | " + C31;infraciones[64] = "C32          | " + C32;infraciones[65] = "C33          | " + C33;infraciones[66] = "C34          | " + C34;
		infraciones[67] = "C35          | " + C35;infraciones[68] = "C36          | " + C36;infraciones[69] = "C37          | " + C37;infraciones[70] = "C38          | " + C38;infraciones[71] = "C39          | " + C39;
		infraciones[72] = "C40          | " + C40;infraciones[73] = "D01          | " + D01;infraciones[74] = "D02          | " + D02;infraciones[75] = "D03          | " + D03;infraciones[76] = "D04          | " + D04;
		infraciones[77] = "D05          | " + D05;infraciones[78] = "D06          | " + D06;infraciones[79] = "D07          | " + D07;infraciones[80] = "D08          | " + D08;infraciones[81] = "D09          | " + D09;
		infraciones[82] = "D11          | " + D11;infraciones[83] = "D12          | " + D12;infraciones[84] = "D13          | " + D13;infraciones[85] = "D14          | " + D14;
		infraciones[86] = "D15          | " + D15;infraciones[87] = "D16          | " + D16;infraciones[88] = "D17          | " + D17;infraciones[89] = "E02          | " + E02;infraciones[90] = "E04          | " + E04;
		infraciones[91] = "F01          | " + F01;infraciones[92] = "F02          | " + F02;infraciones[93] = "F03          | " + F03;infraciones[94] = "F04          | " + F04;infraciones[95] = "F05          | " + F05;
		infraciones[96] = "F06          | " + F06;infraciones[97] = "F07          | " + F07;infraciones[98] = "F08          | " + F08;infraciones[99] = "F09          | " + F09;
		infraciones[100] = "F11          | " + F11;infraciones[101] = "F12          | " + F12;infraciones[102] = "G01          | " + G01;infraciones[103] = "H01          | " + H01;
		infraciones[104] = "H02          | " + H02;infraciones[105] = "H03          | " + H03;infraciones[106] = "H04          | " + H04;infraciones[107] = "H05          | " + H05;infraciones[108] = "H06          | " + H06;
		infraciones[109] = "H07          | " + H07;infraciones[110] = "H08          | " + H08;infraciones[111] = "H10          | " + H10;infraciones[112] = "H11          | " + H11;
		infraciones[113] = "H13          | " + H13;infraciones[114] = "I01          | " + I01;infraciones[115] = "I02          | " + I02;

		return infraciones;
	}

	public String[] darNumerosComparendosTipoServicioPublico()
	{
		Comparable<Comparendo>[] aParticular = darComparendosTipoPublico();
		String infraciones[] = new String[116];

		int A01 = 0;int A02 = 0;int A03 = 0;int A04 = 0;int A05 = 0;int A06 = 0;int A07 = 0;int A08 = 0;int A09 = 0;int A10 = 0;int A11 = 0;int A12 = 0;
		int B01 = 0;int B02 = 0;int B03 = 0;int B04 = 0;int B05 = 0;int B06 = 0;int B07 = 0;int B09 = 0;int B10 = 0;int B11 = 0;int B12 = 0;int B13 = 0;int B14 = 0;int B15 = 0;int B16 = 0;int B17 = 0;int B18 = 0;int B19 = 0;int B20 = 0;int B21 = 0;int B22 = 0;int B23 = 0;
		int C01 = 0;int C02 = 0;int C03 = 0;int C04 = 0;int C05 = 0;int C06 = 0;int C07 = 0;int C08 = 0;int C09 = 0;int C10 = 0;int C11 = 0;int C12 = 0;int C13 = 0;int C14 = 0;int C15 = 0;int C16 = 0;int C17 = 0;int C18 = 0;int C19 = 0;int C20 = 0;int C21 = 0;int C22 = 0;int C23 = 0;int C24 = 0;int C25 = 0;int C26 = 0;int C27 = 0;int C28 = 0;int C29 = 0;int C30 = 0;int C31 = 0;int C32 = 0;int C33 = 0;int C34 = 0;int C35 = 0;int C36 = 0;int C37 = 0;int C38 = 0;int C39 = 0;int C40 = 0;
		int D01 = 0;int D02 = 0;int D03 = 0;int D04 = 0;int D05 = 0;int D06 = 0;int D07 = 0;int D08 = 0;int D09 = 0;int D11 = 0;int D12 = 0;int D13 = 0;int D14 = 0;int D15 = 0;int D16 = 0;int D17 = 0;
		int E02 = 0;int E04 = 0;
		int F01 = 0;int F02 = 0;int F03 = 0;int F04 = 0;int F05 = 0;int F06 = 0;int F07 = 0;int F08 = 0;int F09 = 0;int F11 = 0;int F12 = 0;
		int G01 = 0;
		int H01 = 0;int H02 = 0;int H03 = 0;int H04 = 0;int H05 = 0;int H06 = 0;int H07 = 0;int H08 = 0;int H10 = 0;int H11 = 0;int H13 = 0;
		int I01 = 0;int I02 = 0;

		for(int i = 0; i < aParticular.length ; i++)
		{
			Comparendo actual = (Comparendo) aParticular[i];
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01"))
					A01 ++;
				if(actual.getInfraccion().startsWith("A02"))
					A02 ++;
				if(actual.getInfraccion().startsWith("A03"))
					A03 ++;
				if(actual.getInfraccion().startsWith("A04"))
					A04 ++;
				if(actual.getInfraccion().startsWith("A05"))
					A05 ++;
				if(actual.getInfraccion().startsWith("A06"))
					A06 ++;
				if(actual.getInfraccion().startsWith("A07"))
					A07 ++;
				if(actual.getInfraccion().startsWith("A08"))
					A08 ++;
				if(actual.getInfraccion().startsWith("A09"))
					A09 ++;
				if(actual.getInfraccion().startsWith("A10"))
					A10 ++;
				if(actual.getInfraccion().startsWith("A11"))
					A11 ++;
				if(actual.getInfraccion().startsWith("A12"))
					A12 ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01"))
					B01 ++;
				if(actual.getInfraccion().startsWith("B02"))
					B02 ++;
				if(actual.getInfraccion().startsWith("B03"))
					B03 ++;
				if(actual.getInfraccion().startsWith("B04"))
					B04 ++;
				if(actual.getInfraccion().startsWith("B05"))
					B05 ++;
				if(actual.getInfraccion().startsWith("B06"))
					B06 ++;
				if(actual.getInfraccion().startsWith("B07"))
					B07 ++;
				if(actual.getInfraccion().startsWith("B09"))
					B09 ++;
				if(actual.getInfraccion().startsWith("B10"))
					B10 ++;
				if(actual.getInfraccion().startsWith("B11"))
					B11 ++;
				if(actual.getInfraccion().startsWith("B12"))
					B12 ++;
				if(actual.getInfraccion().startsWith("B13"))
					B13 ++;
				if(actual.getInfraccion().startsWith("B14"))
					B14 ++;
				if(actual.getInfraccion().startsWith("B15"))
					B15 ++;
				if(actual.getInfraccion().startsWith("B16"))
					B16 ++;
				if(actual.getInfraccion().startsWith("B17"))
					B17 ++;
				if(actual.getInfraccion().startsWith("B18"))
					B18 ++;
				if(actual.getInfraccion().startsWith("B19"))
					B19 ++;
				if(actual.getInfraccion().startsWith("B20"))
					B20 ++;
				if(actual.getInfraccion().startsWith("B21"))
					B21 ++;
				if(actual.getInfraccion().startsWith("B22"))
					B22 ++;
				if(actual.getInfraccion().startsWith("B23"))
					B23 ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01"))
					C01 ++;
				if(actual.getInfraccion().startsWith("C02"))
					C02 ++;
				if(actual.getInfraccion().startsWith("C03"))
					C03 ++;
				if(actual.getInfraccion().startsWith("C04"))
					C04 ++;
				if(actual.getInfraccion().startsWith("C05"))
					C05 ++;
				if(actual.getInfraccion().startsWith("C06"))
					C06 ++;
				if(actual.getInfraccion().startsWith("C07"))
					C07 ++;
				if(actual.getInfraccion().startsWith("C08"))
					C08 ++;
				if(actual.getInfraccion().startsWith("C09"))
					C09 ++;
				if(actual.getInfraccion().startsWith("C10"))
					C10 ++;
				if(actual.getInfraccion().startsWith("C11"))
					C11 ++;
				if(actual.getInfraccion().startsWith("C12"))
					C12 ++;
				if(actual.getInfraccion().startsWith("C13"))
					C13 ++;
				if(actual.getInfraccion().startsWith("C14"))
					C14 ++;
				if(actual.getInfraccion().startsWith("C15"))
					C15 ++;
				if(actual.getInfraccion().startsWith("C16"))
					C16 ++;
				if(actual.getInfraccion().startsWith("C17"))
					C17 ++;
				if(actual.getInfraccion().startsWith("C18"))
					C18 ++;
				if(actual.getInfraccion().startsWith("C19"))
					C19 ++;
				if(actual.getInfraccion().startsWith("C20"))
					C20 ++;
				if(actual.getInfraccion().startsWith("C21"))
					C21 ++;
				if(actual.getInfraccion().startsWith("C22"))
					C22 ++;
				if(actual.getInfraccion().startsWith("C23"))
					C23 ++;
				if(actual.getInfraccion().startsWith("C24"))
					C24 ++;
				if(actual.getInfraccion().startsWith("C25"))
					C25 ++;
				if(actual.getInfraccion().startsWith("C26"))
					C26 ++;
				if(actual.getInfraccion().startsWith("C27"))
					C27 ++;
				if(actual.getInfraccion().startsWith("C28"))
					C28 ++;
				if(actual.getInfraccion().startsWith("C29"))
					C29 ++;
				if(actual.getInfraccion().startsWith("C30"))
					C30 ++;
				if(actual.getInfraccion().startsWith("C31"))
					C31 ++;
				if(actual.getInfraccion().startsWith("C32"))
					C32 ++;
				if(actual.getInfraccion().startsWith("C33"))
					C33 ++;
				if(actual.getInfraccion().startsWith("C34"))
					C34 ++;
				if(actual.getInfraccion().startsWith("C35"))
					C35 ++;
				if(actual.getInfraccion().startsWith("C36"))
					C36 ++;
				if(actual.getInfraccion().startsWith("C37"))
					C37 ++;
				if(actual.getInfraccion().startsWith("C38"))
					C38 ++;
				if(actual.getInfraccion().startsWith("C39"))
					C39 ++;
				if(actual.getInfraccion().startsWith("C40"))
					C40 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01"))
					D01 ++;
				if(actual.getInfraccion().startsWith("D02"))
					D02 ++;
				if(actual.getInfraccion().startsWith("D03"))
					D03 ++;
				if(actual.getInfraccion().startsWith("D04"))
					D04 ++;
				if(actual.getInfraccion().startsWith("D05"))
					D05 ++;
				if(actual.getInfraccion().startsWith("D06"))
					D06 ++;
				if(actual.getInfraccion().startsWith("D07"))
					D07 ++;
				if(actual.getInfraccion().startsWith("D08"))
					D08 ++;
				if(actual.getInfraccion().startsWith("D09"))
					D09 ++;
				if(actual.getInfraccion().startsWith("D11"))
					D11 ++;
				if(actual.getInfraccion().startsWith("D12"))
					D12 ++;
				if(actual.getInfraccion().startsWith("D13"))
					D13 ++;
				if(actual.getInfraccion().startsWith("D14"))
					D14 ++;
				if(actual.getInfraccion().startsWith("D15"))
					D15 ++;
				if(actual.getInfraccion().startsWith("D16"))
					D16 ++;
				if(actual.getInfraccion().startsWith("D17"))
					D17 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E02"))
					E02 ++;
				if(actual.getInfraccion().startsWith("E04"))
					E04 ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01"))
					F01 ++;
				if(actual.getInfraccion().startsWith("F02"))
					F02 ++;
				if(actual.getInfraccion().startsWith("F03"))
					F03 ++;
				if(actual.getInfraccion().startsWith("F04"))
					F04 ++;
				if(actual.getInfraccion().startsWith("F05"))
					F05 ++;
				if(actual.getInfraccion().startsWith("F06"))
					F06 ++;
				if(actual.getInfraccion().startsWith("F07"))
					F07 ++;
				if(actual.getInfraccion().startsWith("F08"))
					F08 ++;
				if(actual.getInfraccion().startsWith("F09"))
					F09 ++;
				if(actual.getInfraccion().startsWith("F11"))
					F11 ++;
				if(actual.getInfraccion().startsWith("F12"))
					F12 ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01"))
					G01 ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01"))
					H01 ++;
				if(actual.getInfraccion().startsWith("H02"))
					H02 ++;
				if(actual.getInfraccion().startsWith("H03"))
					H03 ++;
				if(actual.getInfraccion().startsWith("H04"))
					H04 ++;
				if(actual.getInfraccion().startsWith("H05"))
					H05 ++;
				if(actual.getInfraccion().startsWith("H06"))
					H06 ++;
				if(actual.getInfraccion().startsWith("H07"))
					H07 ++;
				if(actual.getInfraccion().startsWith("H08"))
					H08 ++;
				if(actual.getInfraccion().startsWith("H10"))
					H10 ++;
				if(actual.getInfraccion().startsWith("H11"))
					H11 ++;
				if(actual.getInfraccion().startsWith("H13"))
					H13 ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01"))
					I01 ++;
				if(actual.getInfraccion().startsWith("I02"))
					I02 ++;
			}
		}

		infraciones[0] = "        | " + A01;infraciones[1] = "         | " + A02;infraciones[2] = "         | " + A03;infraciones[3] = "        | " + A04;infraciones[4] = "       | " + A05;
		infraciones[5] = "       | " + A06;infraciones[6] = "         | " + A07;infraciones[7] = "       | " + A08;infraciones[8] = "         | " + A09;infraciones[9] = "          | " + A10;
		infraciones[10] = "         | " + A11;infraciones[11] = "         | " + A12;infraciones[12] = "         | " + B01;infraciones[12] = "       | " + B01;infraciones[13] = "       | " + B02;
		infraciones[14] = "        | " + B03;infraciones[15] = "        | " + B04;infraciones[16] = "         | " + B05;infraciones[17] = "          | " + B06;infraciones[18] = "        | " + B07;
		infraciones[19] = "          | " + B09;infraciones[20] = "        | " + B10;infraciones[21] = "         | " + B11;infraciones[22] = "          | " + B12;
		infraciones[23] = "          | " + B13;infraciones[24] = "         | " + B14;infraciones[25] = "         | " + B15;infraciones[26] = "          | " + B16;infraciones[27] = "          | " + B17;
		infraciones[28] = "          | " + B18;infraciones[29] = "          | " + B19;infraciones[30] = "         | " + B20;infraciones[31] = "         | " + B21;infraciones[32] = "        | " + B22;
		infraciones[33] = "          | " + B23;infraciones[34] = "         | " + C01;infraciones[35] = "     | " + C02;infraciones[36] = "       | " + C03;infraciones[37] = "          | " + C04;
		infraciones[37] = "          | " + C05;infraciones[38] = "       | " + C06;infraciones[39] = "         | " + C07;infraciones[40] = "        | " + C08;infraciones[41] = "          | " + C09;
		infraciones[42] = "         | " + C10;infraciones[43] = "       | " + C11;infraciones[44] = "          | " + C12;infraciones[45] = "         | " + C13;infraciones[46] = "      | " + C14;
		infraciones[47] = "        | " + C15;infraciones[48] = "          | " + C16;infraciones[49] = "          | " + C17;infraciones[50] = "         | " + C18;infraciones[51] = "        | " + C19;
		infraciones[52] = "          | " + C20;infraciones[53] = "         | " + C21;infraciones[54] = "         | " + C22;infraciones[55] = "         | " + C23;infraciones[56] = "      | " + C24;
		infraciones[57] = "         | " + C25;infraciones[58] = "         | " + C26;infraciones[59] = "         | " + C27;infraciones[60] = "        | " + C28;infraciones[61] = "       | " + C29;
		infraciones[62] = "         | " + C30;infraciones[63] = "      | " + C31;infraciones[64] = "       | " + C32;infraciones[65] = "        | " + C33;infraciones[66] = "         | " + C34;
		infraciones[67] = "      | " + C35;infraciones[68] = "          | " + C36;infraciones[69] = "         | " + C37;infraciones[70] = "       | " + C38;infraciones[71] = "          | " + C39;
		infraciones[72] = "          | " + C40;infraciones[73] = "       | " + D01;infraciones[74] = "       | " + D02;infraciones[75] = "       | " + D03;infraciones[76] = "       | " + D04;
		infraciones[77] = "       | " + D05;infraciones[78] = "        | " + D06;infraciones[79] = "       | " + D07;infraciones[80] = "        | " + D08;infraciones[81] = "          | " + D09;
		infraciones[82] = "          | " + D11;infraciones[83] = "       | " + D12;infraciones[84] = "          | " + D13;infraciones[85] = "          | " + D14;
		infraciones[86] = "          | " + D15;infraciones[87] = "          | " + D16;infraciones[88] = "          | " + D17;infraciones[89] = "          | " + E02;infraciones[90] = "          | " + E04;
		infraciones[91] = "         | " + F01;infraciones[92] = "         | " + F02;infraciones[93] = "        | " + F03;infraciones[94] = "         | " + F04;infraciones[95] = "         | " + F05;
		infraciones[96] = "       | " + F06;infraciones[97] = "       | " + F07;infraciones[98] = "          | " + F08;infraciones[99] = "          | " + F09;
		infraciones[100] = "          | " + F11;infraciones[101] = "         | " + F12;infraciones[102] = "        | " + G01;infraciones[103] = "         | " + H01;
		infraciones[104] = "       | " + H02;infraciones[105] = "      | " + H03;infraciones[106] = "        | " + H04;infraciones[107] = "         | " + H05;infraciones[108] = "         | " + H06;
		infraciones[109] = "         | " + H07;infraciones[110] = "         | " + H08;infraciones[111] = "       | " + H10;infraciones[112] = "          | " + H11;
		infraciones[113]= "       | " + H13;infraciones[114] = "        | " + I01;infraciones[115] = "          | " + I02;

		return infraciones;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen la Fecha 1
	 * @return Arrgelo Comparable de comparendos con las Fecha1
	 */
	public String[] darComparendosFecha1(String pFecha1)
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
				if(elementoActual.getFecha_hora().equals(pFecha1)) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}
		
		Comparable<Comparendo>[] aFecha1 = null;
		aFecha1 = nuevo;
		String infraciones[] = new String[126];

		int A01 = 0;int A02 = 0;int A03 = 0;int A04 = 0;int A05 = 0;int A06 = 0;int A07 = 0;int A08 = 0;int A09 = 0;int A10 = 0;int A11 = 0;int A12 = 0;
		int B01 = 0;int B02 = 0;int B03 = 0;int B04 = 0;int B05 = 0;int B06 = 0;int B07 = 0;int B08 = 0;int B09 = 0;int B10 = 0;int B11 = 0;int B12 = 0;int B13 = 0;int B14 = 0;int B15 = 0;int B16 = 0;int B17 = 0;int B18 = 0;int B19 = 0;int B20 = 0;int B21 = 0;int B22 = 0;int B23 = 0;
		int C01 = 0;int C02 = 0;int C03 = 0;int C04 = 0;int C05 = 0;int C06 = 0;int C07 = 0;int C08 = 0;int C09 = 0;int C10 = 0;int C11 = 0;int C12 = 0;int C13 = 0;int C14 = 0;int C15 = 0;int C16 = 0;int C17 = 0;int C18 = 0;int C19 = 0;int C20 = 0;int C21 = 0;int C22 = 0;int C23 = 0;int C24 = 0;int C25 = 0;int C26 = 0;int C27 = 0;int C28 = 0;int C29 = 0;int C30 = 0;int C31 = 0;int C32 = 0;int C33 = 0;int C34 = 0;int C35 = 0;int C36 = 0;int C37 = 0;int C38 = 0;int C39 = 0;int C40 = 0;
		int D01 = 0;int D02 = 0;int D03 = 0;int D04 = 0;int D05 = 0;int D06 = 0;int D07 = 0;int D08 = 0;int D09 = 0;int D10 = 0;int D11 = 0;int D12 = 0;int D13 = 0;int D14 = 0;int D15 = 0;int D16 = 0;int D17 = 0;
		int E01 = 0;int E02 = 0;int E03 = 0;int E04 = 0;
		int F01 = 0;int F02 = 0;int F03 = 0;int F04 = 0;int F05 = 0;int F06 = 0;int F07 = 0;int F08 = 0;int F09 = 0;int F10 = 0;int F11 = 0;int F12 = 0;
		int G01 = 0;int G02 = 0;
		int H01 = 0;int H02 = 0;int H03 = 0;int H04 = 0;int H05 = 0;int H06 = 0;int H07 = 0;int H08 = 0;int H09 = 0;int H10 = 0;int H11 = 0;int H12 = 0;int H13 = 0;
		int I01 = 0;int I02 = 0;
		int J01 = 0;int J02 = 0; int J03 = 0;int J04 = 0;

		for(int i = 0; i < aFecha1.length ; i++)
		{
			Comparendo actual = (Comparendo) aFecha1[i];
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01"))
					A01 ++;
				if(actual.getInfraccion().startsWith("A02"))
					A02 ++;
				if(actual.getInfraccion().startsWith("A03"))
					A03 ++;
				if(actual.getInfraccion().startsWith("A04"))
					A04 ++;
				if(actual.getInfraccion().startsWith("A05"))
					A05 ++;
				if(actual.getInfraccion().startsWith("A06"))
					A06 ++;
				if(actual.getInfraccion().startsWith("A07"))
					A07 ++;
				if(actual.getInfraccion().startsWith("A08"))
					A08 ++;
				if(actual.getInfraccion().startsWith("A09"))
					A09 ++;
				if(actual.getInfraccion().startsWith("A10"))
					A10 ++;
				if(actual.getInfraccion().startsWith("A11"))
					A11 ++;
				if(actual.getInfraccion().startsWith("A12"))
					A12 ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01"))
					B01 ++;
				if(actual.getInfraccion().startsWith("B02"))
					B02 ++;
				if(actual.getInfraccion().startsWith("B03"))
					B03 ++;
				if(actual.getInfraccion().startsWith("B04"))
					B04 ++;
				if(actual.getInfraccion().startsWith("B05"))
					B05 ++;
				if(actual.getInfraccion().startsWith("B06"))
					B06 ++;
				if(actual.getInfraccion().startsWith("B07"))
					B07 ++;
				if(actual.getInfraccion().startsWith("B08"))
					B08 ++;
				if(actual.getInfraccion().startsWith("B09"))
					B09 ++;
				if(actual.getInfraccion().startsWith("B10"))
					B10 ++;
				if(actual.getInfraccion().startsWith("B11"))
					B11 ++;
				if(actual.getInfraccion().startsWith("B12"))
					B12 ++;
				if(actual.getInfraccion().startsWith("B13"))
					B13 ++;
				if(actual.getInfraccion().startsWith("B14"))
					B14 ++;
				if(actual.getInfraccion().startsWith("B15"))
					B15 ++;
				if(actual.getInfraccion().startsWith("B16"))
					B16 ++;
				if(actual.getInfraccion().startsWith("B17"))
					B17 ++;
				if(actual.getInfraccion().startsWith("B18"))
					B18 ++;
				if(actual.getInfraccion().startsWith("B19"))
					B19 ++;
				if(actual.getInfraccion().startsWith("B20"))
					B20 ++;
				if(actual.getInfraccion().startsWith("B21"))
					B21 ++;
				if(actual.getInfraccion().startsWith("B22"))
					B22 ++;
				if(actual.getInfraccion().startsWith("B23"))
					B23 ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01"))
					C01 ++;
				if(actual.getInfraccion().startsWith("C02"))
					C02 ++;
				if(actual.getInfraccion().startsWith("C03"))
					C03 ++;
				if(actual.getInfraccion().startsWith("C04"))
					C04 ++;
				if(actual.getInfraccion().startsWith("C05"))
					C05 ++;
				if(actual.getInfraccion().startsWith("C06"))
					C06 ++;
				if(actual.getInfraccion().startsWith("C07"))
					C07 ++;
				if(actual.getInfraccion().startsWith("C08"))
					C08 ++;
				if(actual.getInfraccion().startsWith("C09"))
					C09 ++;
				if(actual.getInfraccion().startsWith("C10"))
					C10 ++;
				if(actual.getInfraccion().startsWith("C11"))
					C11 ++;
				if(actual.getInfraccion().startsWith("C12"))
					C12 ++;
				if(actual.getInfraccion().startsWith("C13"))
					C13 ++;
				if(actual.getInfraccion().startsWith("C14"))
					C14 ++;
				if(actual.getInfraccion().startsWith("C15"))
					C15 ++;
				if(actual.getInfraccion().startsWith("C16"))
					C16 ++;
				if(actual.getInfraccion().startsWith("C17"))
					C17 ++;
				if(actual.getInfraccion().startsWith("C18"))
					C18 ++;
				if(actual.getInfraccion().startsWith("C19"))
					C19 ++;
				if(actual.getInfraccion().startsWith("C20"))
					C20 ++;
				if(actual.getInfraccion().startsWith("C21"))
					C21 ++;
				if(actual.getInfraccion().startsWith("C22"))
					C22 ++;
				if(actual.getInfraccion().startsWith("C23"))
					C23 ++;
				if(actual.getInfraccion().startsWith("C24"))
					C24 ++;
				if(actual.getInfraccion().startsWith("C25"))
					C25 ++;
				if(actual.getInfraccion().startsWith("C26"))
					C26 ++;
				if(actual.getInfraccion().startsWith("C27"))
					C27 ++;
				if(actual.getInfraccion().startsWith("C28"))
					C28 ++;
				if(actual.getInfraccion().startsWith("C29"))
					C29 ++;
				if(actual.getInfraccion().startsWith("C30"))
					C30 ++;
				if(actual.getInfraccion().startsWith("C31"))
					C31 ++;
				if(actual.getInfraccion().startsWith("C32"))
					C32 ++;
				if(actual.getInfraccion().startsWith("C33"))
					C33 ++;
				if(actual.getInfraccion().startsWith("C34"))
					C34 ++;
				if(actual.getInfraccion().startsWith("C35"))
					C35 ++;
				if(actual.getInfraccion().startsWith("C36"))
					C36 ++;
				if(actual.getInfraccion().startsWith("C37"))
					C37 ++;
				if(actual.getInfraccion().startsWith("C38"))
					C38 ++;
				if(actual.getInfraccion().startsWith("C39"))
					C39 ++;
				if(actual.getInfraccion().startsWith("C40"))
					C40 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01"))
					D01 ++;
				if(actual.getInfraccion().startsWith("D02"))
					D02 ++;
				if(actual.getInfraccion().startsWith("D03"))
					D03 ++;
				if(actual.getInfraccion().startsWith("D04"))
					D04 ++;
				if(actual.getInfraccion().startsWith("D05"))
					D05 ++;
				if(actual.getInfraccion().startsWith("D06"))
					D06 ++;
				if(actual.getInfraccion().startsWith("D07"))
					D07 ++;
				if(actual.getInfraccion().startsWith("D08"))
					D08 ++;
				if(actual.getInfraccion().startsWith("D09"))
					D09 ++;
				if(actual.getInfraccion().startsWith("D10"))
					D10 ++;
				if(actual.getInfraccion().startsWith("D11"))
					D11 ++;
				if(actual.getInfraccion().startsWith("D12"))
					D12 ++;
				if(actual.getInfraccion().startsWith("D13"))
					D13 ++;
				if(actual.getInfraccion().startsWith("D14"))
					D14 ++;
				if(actual.getInfraccion().startsWith("D15"))
					D15 ++;
				if(actual.getInfraccion().startsWith("D16"))
					D16 ++;
				if(actual.getInfraccion().startsWith("D17"))
					D17 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E01"))
					E01 ++;
				if(actual.getInfraccion().startsWith("E02"))
					E02 ++;
				if(actual.getInfraccion().startsWith("E03"))
					E03 ++;
				if(actual.getInfraccion().startsWith("E04"))
					E04 ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01"))
					F01 ++;
				if(actual.getInfraccion().startsWith("F02"))
					F02 ++;
				if(actual.getInfraccion().startsWith("F03"))
					F03 ++;
				if(actual.getInfraccion().startsWith("F04"))
					F04 ++;
				if(actual.getInfraccion().startsWith("F05"))
					F05 ++;
				if(actual.getInfraccion().startsWith("F06"))
					F06 ++;
				if(actual.getInfraccion().startsWith("F07"))
					F07 ++;
				if(actual.getInfraccion().startsWith("F08"))
					F08 ++;
				if(actual.getInfraccion().startsWith("F09"))
					F09 ++;
				if(actual.getInfraccion().startsWith("F10"))
					F10 ++;
				if(actual.getInfraccion().startsWith("F11"))
					F11 ++;
				if(actual.getInfraccion().startsWith("F12"))
					F12 ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01"))
					G01 ++;
				if(actual.getInfraccion().startsWith("G02"))
					G02 ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01"))
					H01 ++;
				if(actual.getInfraccion().startsWith("H02"))
					H02 ++;
				if(actual.getInfraccion().startsWith("H03"))
					H03 ++;
				if(actual.getInfraccion().startsWith("H04"))
					H04 ++;
				if(actual.getInfraccion().startsWith("H05"))
					H05 ++;
				if(actual.getInfraccion().startsWith("H06"))
					H06 ++;
				if(actual.getInfraccion().startsWith("H07"))
					H07 ++;
				if(actual.getInfraccion().startsWith("H08"))
					H08 ++;
				if(actual.getInfraccion().startsWith("H09"))
					H09 ++;
				if(actual.getInfraccion().startsWith("H10"))
					H10 ++;
				if(actual.getInfraccion().startsWith("H11"))
					H11 ++;
				if(actual.getInfraccion().startsWith("H12"))
					H12 ++;
				if(actual.getInfraccion().startsWith("H13"))
					H13 ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01"))
					I01 ++;
				if(actual.getInfraccion().startsWith("I02"))
					I02 ++;
				
				// Infracciones J

				if(actual.getInfraccion().startsWith("J01"))
					J01 ++;
				if(actual.getInfraccion().startsWith("J02"))
					J02 ++;
				if(actual.getInfraccion().startsWith("I01"))
					J03 ++;
				if(actual.getInfraccion().startsWith("I02"))
					J04 ++;
			}
		}

		infraciones[0] = "A01          | " + A01;infraciones[1] = "A02          | " + A02;infraciones[2] = "A03          | " + A03;infraciones[3] = "A04          | " + A04;infraciones[4] = "A05          | " + A05;
		infraciones[5] = "A06          | " + A06;infraciones[6] = "A07          | " + A07;infraciones[7] = "A08          | " + A08;infraciones[8] = "A09          | " + A09;infraciones[9] = "A10          | " + A10;
		infraciones[10] = "A11          | " + A11;infraciones[11] = "A12          | " + A12;infraciones[12] = "B01          | " + B01;infraciones[12] = "B02          | " + B02;infraciones[13] = "B03          | " + B03;
		infraciones[14] = "B04          | " + B04;infraciones[15] = "B05          | " + B05;infraciones[16] = "B06          | " + B06;infraciones[17] = "B07          | " + B07;infraciones[18] = "B08          | " + B08;
		infraciones[19] = "B09          | " + B09;infraciones[20] = "B10          | " + B10;infraciones[21] = "B11          | " + B11;infraciones[22] = "B12          | " + B12;infraciones[23] = "B13          | " + B13;
		infraciones[24] = "B14          | " + B14;infraciones[25] = "B15          | " + B15;infraciones[26] = "B16          | " + B16;infraciones[27] = "B17          | " + B17;infraciones[28] = "B18          | " + B18;
		infraciones[29] = "B19          | " + B19;infraciones[30] = "B20          | " + B20;infraciones[31] = "B21          | " + B21;infraciones[32] = "B22          | " + B22;infraciones[33] = "B23          | " + B23;
		infraciones[34] = "C01          | " + C01;infraciones[35] = "C02          | " + C02;infraciones[36] = "C03          | " + C03;infraciones[37] = "C04          | " + C04;infraciones[37] = "C05          | " + C05;
		infraciones[38] = "C06          | " + C06;infraciones[39] = "C07          | " + C07;infraciones[40] = "C08          | " + C08;infraciones[41] = "C09          | " + C09;infraciones[42] = "C10          | " + C10;
		infraciones[43] = "C11          | " + C11;infraciones[44] = "C12          | " + C12;infraciones[45] = "C13          | " + C13;infraciones[46] = "C14          | " + C14;infraciones[47] = "C15          | " + C15;
		infraciones[48] = "C16          | " + C16;infraciones[49] = "C17          | " + C17;infraciones[50] = "C18          | " + C18;infraciones[51] = "C19          | " + C19;infraciones[52] = "C20          | " + C20;
		infraciones[53] = "C21          | " + C21;infraciones[54] = "C22          | " + C22;infraciones[55] = "C23          | " + C23;infraciones[56] = "C24          | " + C24;infraciones[57] = "C25          | " + C25;
		infraciones[58] = "C26          | " + C26;infraciones[59] = "C27          | " + C27;infraciones[60] = "C28          | " + C28;infraciones[61] = "C29          | " + C29;infraciones[62] = "C30          | " + C30;
		infraciones[63] = "C31          | " + C31;infraciones[64] = "C32          | " + C32;infraciones[65] = "C33          | " + C33;infraciones[66] = "C34          | " + C34;infraciones[67] = "C35          | " + C35;
		infraciones[68] = "C36          | " + C36;infraciones[69] = "C37          | " + C37;infraciones[70] = "C38          | " + C38;infraciones[71] = "C39          | " + C39;infraciones[72] = "C40          | " + C40;
		infraciones[73] = "D01          | " + D01;infraciones[74] = "D02          | " + D02;infraciones[75] = "D03          | " + D03;infraciones[76] = "D04          | " + D04;infraciones[77] = "D05          | " + D05;
		infraciones[78] = "D06          | " + D06;infraciones[79] = "D07          | " + D07;infraciones[80] = "D08          | " + D08;infraciones[81] = "D09          | " + D09;infraciones[81] = "D10          | " + D10;
		infraciones[82] = "D11          | " + D11;infraciones[83] = "D12          | " + D12;infraciones[84] = "D13          | " + D13;infraciones[85] = "D14          | " + D14;infraciones[86] = "D15          | " + D15;
		infraciones[87] = "D16          | " + D16;infraciones[88] = "D17          | " + D17;infraciones[89] = "E01          | " + E01;infraciones[90] = "E02          | " + E02;infraciones[91] = "E03          | " + E03;
		infraciones[92] = "E04          | " + E04;infraciones[93] = "F01          | " + F01;infraciones[94] = "F02          | " + F02;infraciones[95] = "F03          | " + F03;infraciones[96] = "F04          | " + F04;
		infraciones[97] = "F05          | " + F05;infraciones[98] = "F06          | " + F06;infraciones[99] = "F07          | " + F07;infraciones[100] = "F08          | " + F08;infraciones[101] = "F09          | " + F09;
		infraciones[102] = "F10          | " + F10;infraciones[103] = "F11          | " + F11;infraciones[104] = "F12          | " + F12;infraciones[105] = "G01          | " + G01;infraciones[106] = "G02          | " + G02;
		infraciones[107] = "H01          | " + H01;infraciones[108] = "H02          | " + H02;infraciones[109] = "H03          | " + H03;infraciones[110] = "H04          | " + H04;infraciones[111] = "H05          | " + H05;
		infraciones[112] = "H06          | " + H06;infraciones[113] = "H07          | " + H07;infraciones[114] = "H08          | " + H08;infraciones[115] = "H09          | " + H09;infraciones[116] = "H10          | " + H10;
		infraciones[117] = "H11          | " + H11;infraciones[118] = "H12          | " + H12;infraciones[119] = "H13          | " + H13;infraciones[120] = "I01          | " + I01;infraciones[121] = "I02          | " + I02;
		infraciones[122] = "J01          | " + J01;infraciones[123] = "J02          | " + J02;infraciones[124] = "J03          | " + J03;infraciones[125] = "J04          | " + J04;
	
		return infraciones;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen la Fecha 2
	 * @return Arrgelo Comparable de comparendos con las Fecha2
	 */
	public String[] darComparendosFecha2(String pFecha2)
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
				if(elementoActual.getTipo_servi().equals(pFecha2)) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		Comparable<Comparendo>[] aFecha2 = null;
		aFecha2 = nuevo;
		String infraciones[] = new String[126];

		int A01 = 0;int A02 = 0;int A03 = 0;int A04 = 0;int A05 = 0;int A06 = 0;int A07 = 0;int A08 = 0;int A09 = 0;int A10 = 0;int A11 = 0;int A12 = 0;
		int B01 = 0;int B02 = 0;int B03 = 0;int B04 = 0;int B05 = 0;int B06 = 0;int B07 = 0;int B08 = 0;int B09 = 0;int B10 = 0;int B11 = 0;int B12 = 0;int B13 = 0;int B14 = 0;int B15 = 0;int B16 = 0;int B17 = 0;int B18 = 0;int B19 = 0;int B20 = 0;int B21 = 0;int B22 = 0;int B23 = 0;
		int C01 = 0;int C02 = 0;int C03 = 0;int C04 = 0;int C05 = 0;int C06 = 0;int C07 = 0;int C08 = 0;int C09 = 0;int C10 = 0;int C11 = 0;int C12 = 0;int C13 = 0;int C14 = 0;int C15 = 0;int C16 = 0;int C17 = 0;int C18 = 0;int C19 = 0;int C20 = 0;int C21 = 0;int C22 = 0;int C23 = 0;int C24 = 0;int C25 = 0;int C26 = 0;int C27 = 0;int C28 = 0;int C29 = 0;int C30 = 0;int C31 = 0;int C32 = 0;int C33 = 0;int C34 = 0;int C35 = 0;int C36 = 0;int C37 = 0;int C38 = 0;int C39 = 0;int C40 = 0;
		int D01 = 0;int D02 = 0;int D03 = 0;int D04 = 0;int D05 = 0;int D06 = 0;int D07 = 0;int D08 = 0;int D09 = 0;int D10 = 0;int D11 = 0;int D12 = 0;int D13 = 0;int D14 = 0;int D15 = 0;int D16 = 0;int D17 = 0;
		int E01 = 0;int E02 = 0;int E03 = 0;int E04 = 0;
		int F01 = 0;int F02 = 0;int F03 = 0;int F04 = 0;int F05 = 0;int F06 = 0;int F07 = 0;int F08 = 0;int F09 = 0;int F10 = 0;int F11 = 0;int F12 = 0;
		int G01 = 0;int G02 = 0;
		int H01 = 0;int H02 = 0;int H03 = 0;int H04 = 0;int H05 = 0;int H06 = 0;int H07 = 0;int H08 = 0;int H09 = 0;int H10 = 0;int H11 = 0;int H12 = 0;int H13 = 0;
		int I01 = 0;int I02 = 0;
		int J01 = 0;int J02 = 0; int J03 = 0;int J04 = 0;

		for(int i = 0; i < aFecha2.length ; i++)
		{
			Comparendo actual = (Comparendo) aFecha2[i];
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01"))
					A01 ++;
				if(actual.getInfraccion().startsWith("A02"))
					A02 ++;
				if(actual.getInfraccion().startsWith("A03"))
					A03 ++;
				if(actual.getInfraccion().startsWith("A04"))
					A04 ++;
				if(actual.getInfraccion().startsWith("A05"))
					A05 ++;
				if(actual.getInfraccion().startsWith("A06"))
					A06 ++;
				if(actual.getInfraccion().startsWith("A07"))
					A07 ++;
				if(actual.getInfraccion().startsWith("A08"))
					A08 ++;
				if(actual.getInfraccion().startsWith("A09"))
					A09 ++;
				if(actual.getInfraccion().startsWith("A10"))
					A10 ++;
				if(actual.getInfraccion().startsWith("A11"))
					A11 ++;
				if(actual.getInfraccion().startsWith("A12"))
					A12 ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01"))
					B01 ++;
				if(actual.getInfraccion().startsWith("B02"))
					B02 ++;
				if(actual.getInfraccion().startsWith("B03"))
					B03 ++;
				if(actual.getInfraccion().startsWith("B04"))
					B04 ++;
				if(actual.getInfraccion().startsWith("B05"))
					B05 ++;
				if(actual.getInfraccion().startsWith("B06"))
					B06 ++;
				if(actual.getInfraccion().startsWith("B07"))
					B07 ++;
				if(actual.getInfraccion().startsWith("B08"))
					B08 ++;
				if(actual.getInfraccion().startsWith("B09"))
					B09 ++;
				if(actual.getInfraccion().startsWith("B10"))
					B10 ++;
				if(actual.getInfraccion().startsWith("B11"))
					B11 ++;
				if(actual.getInfraccion().startsWith("B12"))
					B12 ++;
				if(actual.getInfraccion().startsWith("B13"))
					B13 ++;
				if(actual.getInfraccion().startsWith("B14"))
					B14 ++;
				if(actual.getInfraccion().startsWith("B15"))
					B15 ++;
				if(actual.getInfraccion().startsWith("B16"))
					B16 ++;
				if(actual.getInfraccion().startsWith("B17"))
					B17 ++;
				if(actual.getInfraccion().startsWith("B18"))
					B18 ++;
				if(actual.getInfraccion().startsWith("B19"))
					B19 ++;
				if(actual.getInfraccion().startsWith("B20"))
					B20 ++;
				if(actual.getInfraccion().startsWith("B21"))
					B21 ++;
				if(actual.getInfraccion().startsWith("B22"))
					B22 ++;
				if(actual.getInfraccion().startsWith("B23"))
					B23 ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01"))
					C01 ++;
				if(actual.getInfraccion().startsWith("C02"))
					C02 ++;
				if(actual.getInfraccion().startsWith("C03"))
					C03 ++;
				if(actual.getInfraccion().startsWith("C04"))
					C04 ++;
				if(actual.getInfraccion().startsWith("C05"))
					C05 ++;
				if(actual.getInfraccion().startsWith("C06"))
					C06 ++;
				if(actual.getInfraccion().startsWith("C07"))
					C07 ++;
				if(actual.getInfraccion().startsWith("C08"))
					C08 ++;
				if(actual.getInfraccion().startsWith("C09"))
					C09 ++;
				if(actual.getInfraccion().startsWith("C10"))
					C10 ++;
				if(actual.getInfraccion().startsWith("C11"))
					C11 ++;
				if(actual.getInfraccion().startsWith("C12"))
					C12 ++;
				if(actual.getInfraccion().startsWith("C13"))
					C13 ++;
				if(actual.getInfraccion().startsWith("C14"))
					C14 ++;
				if(actual.getInfraccion().startsWith("C15"))
					C15 ++;
				if(actual.getInfraccion().startsWith("C16"))
					C16 ++;
				if(actual.getInfraccion().startsWith("C17"))
					C17 ++;
				if(actual.getInfraccion().startsWith("C18"))
					C18 ++;
				if(actual.getInfraccion().startsWith("C19"))
					C19 ++;
				if(actual.getInfraccion().startsWith("C20"))
					C20 ++;
				if(actual.getInfraccion().startsWith("C21"))
					C21 ++;
				if(actual.getInfraccion().startsWith("C22"))
					C22 ++;
				if(actual.getInfraccion().startsWith("C23"))
					C23 ++;
				if(actual.getInfraccion().startsWith("C24"))
					C24 ++;
				if(actual.getInfraccion().startsWith("C25"))
					C25 ++;
				if(actual.getInfraccion().startsWith("C26"))
					C26 ++;
				if(actual.getInfraccion().startsWith("C27"))
					C27 ++;
				if(actual.getInfraccion().startsWith("C28"))
					C28 ++;
				if(actual.getInfraccion().startsWith("C29"))
					C29 ++;
				if(actual.getInfraccion().startsWith("C30"))
					C30 ++;
				if(actual.getInfraccion().startsWith("C31"))
					C31 ++;
				if(actual.getInfraccion().startsWith("C32"))
					C32 ++;
				if(actual.getInfraccion().startsWith("C33"))
					C33 ++;
				if(actual.getInfraccion().startsWith("C34"))
					C34 ++;
				if(actual.getInfraccion().startsWith("C35"))
					C35 ++;
				if(actual.getInfraccion().startsWith("C36"))
					C36 ++;
				if(actual.getInfraccion().startsWith("C37"))
					C37 ++;
				if(actual.getInfraccion().startsWith("C38"))
					C38 ++;
				if(actual.getInfraccion().startsWith("C39"))
					C39 ++;
				if(actual.getInfraccion().startsWith("C40"))
					C40 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01"))
					D01 ++;
				if(actual.getInfraccion().startsWith("D02"))
					D02 ++;
				if(actual.getInfraccion().startsWith("D03"))
					D03 ++;
				if(actual.getInfraccion().startsWith("D04"))
					D04 ++;
				if(actual.getInfraccion().startsWith("D05"))
					D05 ++;
				if(actual.getInfraccion().startsWith("D06"))
					D06 ++;
				if(actual.getInfraccion().startsWith("D07"))
					D07 ++;
				if(actual.getInfraccion().startsWith("D08"))
					D08 ++;
				if(actual.getInfraccion().startsWith("D09"))
					D09 ++;
				if(actual.getInfraccion().startsWith("D10"))
					D10 ++;
				if(actual.getInfraccion().startsWith("D11"))
					D11 ++;
				if(actual.getInfraccion().startsWith("D12"))
					D12 ++;
				if(actual.getInfraccion().startsWith("D13"))
					D13 ++;
				if(actual.getInfraccion().startsWith("D14"))
					D14 ++;
				if(actual.getInfraccion().startsWith("D15"))
					D15 ++;
				if(actual.getInfraccion().startsWith("D16"))
					D16 ++;
				if(actual.getInfraccion().startsWith("D17"))
					D17 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E01"))
					E01 ++;
				if(actual.getInfraccion().startsWith("E02"))
					E02 ++;
				if(actual.getInfraccion().startsWith("E03"))
					E03 ++;
				if(actual.getInfraccion().startsWith("E04"))
					E04 ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01"))
					F01 ++;
				if(actual.getInfraccion().startsWith("F02"))
					F02 ++;
				if(actual.getInfraccion().startsWith("F03"))
					F03 ++;
				if(actual.getInfraccion().startsWith("F04"))
					F04 ++;
				if(actual.getInfraccion().startsWith("F05"))
					F05 ++;
				if(actual.getInfraccion().startsWith("F06"))
					F06 ++;
				if(actual.getInfraccion().startsWith("F07"))
					F07 ++;
				if(actual.getInfraccion().startsWith("F08"))
					F08 ++;
				if(actual.getInfraccion().startsWith("F09"))
					F09 ++;
				if(actual.getInfraccion().startsWith("F10"))
					F10 ++;
				if(actual.getInfraccion().startsWith("F11"))
					F11 ++;
				if(actual.getInfraccion().startsWith("F12"))
					F12 ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01"))
					G01 ++;
				if(actual.getInfraccion().startsWith("G02"))
					G02 ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01"))
					H01 ++;
				if(actual.getInfraccion().startsWith("H02"))
					H02 ++;
				if(actual.getInfraccion().startsWith("H03"))
					H03 ++;
				if(actual.getInfraccion().startsWith("H04"))
					H04 ++;
				if(actual.getInfraccion().startsWith("H05"))
					H05 ++;
				if(actual.getInfraccion().startsWith("H06"))
					H06 ++;
				if(actual.getInfraccion().startsWith("H07"))
					H07 ++;
				if(actual.getInfraccion().startsWith("H08"))
					H08 ++;
				if(actual.getInfraccion().startsWith("H09"))
					H09 ++;
				if(actual.getInfraccion().startsWith("H10"))
					H10 ++;
				if(actual.getInfraccion().startsWith("H11"))
					H11 ++;
				if(actual.getInfraccion().startsWith("H12"))
					H12 ++;
				if(actual.getInfraccion().startsWith("H13"))
					H13 ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01"))
					I01 ++;
				if(actual.getInfraccion().startsWith("I02"))
					I02 ++;
				
				// Infracciones J

				if(actual.getInfraccion().startsWith("J01"))
					J01 ++;
				if(actual.getInfraccion().startsWith("J02"))
					J02 ++;
				if(actual.getInfraccion().startsWith("I01"))
					J03 ++;
				if(actual.getInfraccion().startsWith("I02"))
					J04 ++;
			}
		}

		infraciones[0] = "          | " + A01;infraciones[1] = "          | " + A02;infraciones[2] = "          | " + A03;infraciones[3] = "          | " + A04;infraciones[4] = "          | " + A05;
		infraciones[5] = "          | " + A06;infraciones[6] = "          | " + A07;infraciones[7] = "          | " + A08;infraciones[8] = "           | " + A09;infraciones[9] = "          | " + A10;
		infraciones[10] = "          | " + A11;infraciones[11] = "          | " + A12;infraciones[12] = "          | " + B01;infraciones[12] = "          | " + B02;infraciones[13] = "          | " + B03;
		infraciones[14] = "          | " + B04;infraciones[15] = "          | " + B05;infraciones[16] = "          | " + B06;infraciones[17] = "          | " + B07;infraciones[18] = "          | " + B08;
		infraciones[19] = "          | " + B09;infraciones[20] = "          | " + B10;infraciones[21] = "          | " + B11;infraciones[22] = "          | " + B12;infraciones[23] = "          | " + B13;
		infraciones[24] = "          | " + B14;infraciones[25] = "          | " + B15;infraciones[26] = "          | " + B16;infraciones[27] = "          | " + B17;infraciones[28] = "          | " + B18;
		infraciones[29] = "          | " + B19;infraciones[30] = "          | " + B20;infraciones[31] = "          | " + B21;infraciones[32] = "          | " + B22;infraciones[33] = "          | " + B23;
		infraciones[34] = "          | " + C01;infraciones[35] = "          | " + C02;infraciones[36] = "          | " + C03;infraciones[37] = "          | " + C04;infraciones[37] = "          | " + C05;
		infraciones[38] = "          | " + C06;infraciones[39] = "          | " + C07;infraciones[40] = "          | " + C08;infraciones[41] = "          | " + C09;infraciones[42] = "          | " + C10;
		infraciones[43] = "          | " + C11;infraciones[44] = "          | " + C12;infraciones[45] = "          | " + C13;infraciones[46] = "          | " + C14;infraciones[47] = "          | " + C15;
		infraciones[48] = "          | " + C16;infraciones[49] = "          | " + C17;infraciones[50] = "          | " + C18;infraciones[51] = "          | " + C19;infraciones[52] = "          | " + C20;
		infraciones[53] = "          | " + C21;infraciones[54] = "          | " + C22;infraciones[55] = "          | " + C23;infraciones[56] = "          | " + C24;infraciones[57] = "          | " + C25;
		infraciones[58] = "          | " + C26;infraciones[59] = "          | " + C27;infraciones[60] = "          | " + C28;infraciones[61] = "          | " + C29;infraciones[62] = "          | " + C30;
		infraciones[63] = "          | " + C31;infraciones[64] = "          | " + C32;infraciones[65] = "          | " + C33;infraciones[66] = "          | " + C34;infraciones[67] = "          | " + C35;
		infraciones[68] = "          | " + C36;infraciones[69] = "          | " + C37;infraciones[70] = "          | " + C38;infraciones[71] = "          | " + C39;infraciones[72] = "          | " + C40;
		infraciones[73] = "          | " + D01;infraciones[74] = "          | " + D02;infraciones[75] = "          | " + D03;infraciones[76] = "          | " + D04;infraciones[77] = "          | " + D05;
		infraciones[78] = "          | " + D06;infraciones[79] = "          | " + D07;infraciones[80] = "          | " + D08;infraciones[81] = "          | " + D09;infraciones[81] = "          | " + D10;
		infraciones[82] = "          | " + D11;infraciones[83] = "          | " + D12;infraciones[84] = "          | " + D13;infraciones[85] = "          | " + D14;infraciones[86] = "          | " + D15;
		infraciones[87] = "          | " + D16;infraciones[88] = "          | " + D17;infraciones[89] = "          | " + E01;infraciones[90] = "          | " + E02;infraciones[91] = "          | " + E03;
		infraciones[92] = "          | " + E04;infraciones[93] = "          | " + F01;infraciones[94] = "          | " + F02;infraciones[95] = "          | " + F03;infraciones[96] = "          | " + F04;
		infraciones[97] = "          | " + F05;infraciones[98] = "          | " + F06;infraciones[99] = "          | " + F07;infraciones[100] = "          | " + F08;infraciones[101] = "          | " + F09;
		infraciones[102] = "          | " + F10;infraciones[103] = "          | " + F11;infraciones[104] = "          | " + F12;infraciones[105] = "          | " + G01;infraciones[106] = "          | " + G02;
		infraciones[107] = "          | " + H01;infraciones[108] = "          | " + H02;infraciones[109] = "          | " + H03;infraciones[110] = "          | " + H04;infraciones[111] = "          | " + H05;
		infraciones[112] = "          | " + H06;infraciones[113] = "          | " + H07;infraciones[114] = "          | " + H08;infraciones[115] = "          | " + H09;infraciones[116] = "          | " + H10;
		infraciones[117] = "          | " + H11;infraciones[118] = "          | " + H12;infraciones[119] = "          | " + H13;infraciones[120] = "          | " + I01;infraciones[121] = "          | " + I02;
		infraciones[122] = "          | " + J01;infraciones[123] = "          | " + J02;infraciones[124] = "          | " + J03;infraciones[125] = "          | " + J04;
	
		return infraciones;
	}
	
	public Comparable<Comparendo>[] darComparendos1C(String pLocalidad, String pFecha1, String pFecha2)
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
				if(elementoActual.getLocalidad().equals(pLocalidad) ) 
				{
					if(elementoActual.getFecha_hora().compareTo(pFecha1) < 0 && elementoActual.getFecha_hora().compareTo(pFecha2) < 0)
					{
						nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
						j++;
					}
				}
			}
		}
        return nuevo;
	}

	// Metodos de Ordenamientos - MergeSort

	/**
	 * Ordena los elmenentos de acuerdo con el criterio de comparacion
	 * @param a Arreglo de comparendos
	 * @param comp Comparador por el cual se va a organizar
	 */
	public static <E> void sort(E[] a, Comparator<? super E> comp) 
	{
		mergeSort(a, 0, a.length - 1, comp);
	}

	/**
	 * Hace las fusiones de los sub arreglos ordenados para crear un arreglo totalmente ordenado
	 * @param a Arreglo de comparendos
	 * @param from Posicion inical del arreglo
	 * @param to Posicion final del arreglo
	 * @param comp Comparador por el cual se va a organizar
	 */
	private static <E> void mergeSort(E[] a, int from, int to, Comparator<? super E> comp) 
	{
		if (from == to)
			return;
		int mid = (from + to) / 2;
		// Sort the first and the second half
		mergeSort(a, from, mid, comp);
		mergeSort(a, mid + 1, to, comp);
		merge(a, from, mid, to, comp);
	}

	/**
	 * Fusion de los arreglos para ser utilizados por mergeSort
	 * @param a Arreglo de comparendos
	 * @param from Posicion inical del arreglo
	 * @param mid Posicion de la mitad del arreglo
	 * @param to Posicion final del arreglo
	 * @param comp Comparador por el cual se va a organizar
	 */
	@SuppressWarnings("unchecked")
	private static <E> void merge(E[] a, int from, int mid, int to, Comparator<? super E> comp) 
	{
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
	
	/**
	 * Este metodo se encarga de ordenar bajo el criterio de shellSort
	 * Funcion principal: Shell sort es un algoritmo que primero clasifica los elementos muy separados entre sí y sucesivamente reduce el intervalo entre los elementos a clasificar.
	 * @param a Comparendo de tipo compareble bajo el criterio de comparcion por fecha_hora, objectid depende del caso
	 */
	public static void shellSort(Comparable<Comparendo>[] a) 
	{
		int n = a.length;

		int h = 1;
		while (h < n/3) h = 3*h + 1; 

		while (h >= 1) 
		{
			for (int i = h; i < n; i++) 
			{
				for (int j = i; j >= h && less(a[j], a[j-h]); j -= h) 
				{
					exch(a, j, j-h);
				}
			}
			h /= 3;
		}
	}

	/**
	 * Se encarga de determinar si el comparendo es menor
	 * @param v Comparendo1 de tipo coparable bajo el criterio de comparcion por fecha_hora, objectid depende del caso
	 * @param w Comparendo2 de tipo coparable bajo el criterio de comparcion por fecha_hora, objectid depende del caso
	 * @return True si es menor, false en el caso contrario
	 */
	private static boolean less(Comparable<Comparendo> v, Comparable<Comparendo> w) 
	{
		return v.compareTo((Comparendo) w) < 0;
	}

	/**
	 * Cambia el objeto de una posicion a otra
	 * @param a Objeto a intercambiar en el arreglo
	 * @param i Posicion en i
	 * @param j Posicion en j
	 */
	private static void exch(Object[] a, int i, int j) 
	{
		Object swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}
}
