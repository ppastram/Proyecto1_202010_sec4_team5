package model.logic;

import java.util.Comparator;

public class ComparadorInfraccionInverso implements Comparator<Comparendo> 
{
	@Override
	public int compare(Comparendo p1, Comparendo p2) 
	{
		int resultado = 0;
		
		if(p1.getInfraccion().compareTo(p2.getInfraccion()) > 0)
		{
			resultado = 1;
		}
		else if(p1.getInfraccion().compareTo(p2.getInfraccion()) == 0)
		{
			resultado = 0;
		}
		else if(p1.getInfraccion().compareTo(p2.getInfraccion()) < 0)
		{
			resultado = -1;
		}
		
		return resultado;
	}
}

