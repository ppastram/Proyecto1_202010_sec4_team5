package test.data_structures;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import model.data_structures.LinkedList;;

/**
 * Clase de test para verificar que la LinkedList si esta funcionando correctamente
 * @author Julian Padilla - Pablo Pastrana
 */
public class LinkedListTest 
{
	/**
	 * Lista sencilla de tipo intger
	 */
	private LinkedList<Integer> listaPila1;
	
	/**
	 * Lista sencilla de tipo String
	 */
	private LinkedList<String> listaPila2;

	/**
	 * Verifica que la lista sencilla si se este creando
	 */
	@Test
	public void testLinkedStack() 
	{
		listaPila1 = new LinkedList<Integer>();
		listaPila2 = new LinkedList<String>();
	}
	
	/**
	 * Escenario 1: Crea una lista sencilla de tipo integer
	 */
	@Before
	public void setupEscenario1( )
	{
		listaPila1 = new LinkedList<Integer>();
		listaPila1.addNodeLast(1);
		listaPila1.addNodeLast(2);
		listaPila1.addNodeLast(3);
		listaPila1.addNodeLast(4);
	}
	
	/**
	 * Escenario 1: Crea una lista sencilla de tipo String
	 */
	@Before
	public void setupEscenario2( )
	{
		listaPila2 = new LinkedList<String>();
		listaPila2.addNodeLast("Hola");
		listaPila2.addNodeLast("como");
		listaPila2.addNodeLast("estas");
		listaPila2.addNodeLast("hoy");
		listaPila2.addNodeLast("??");
	}
	
	/**
	 * Prueba 1: Verifica que el primer elemento dentro de la lista sencilla si corresponda con el del escenario 1 y 2
	 */
	@Test
	public void testGetFirst() 
	{	
		setupEscenario1();
		assertEquals( 1, listaPila1.getFirst().getItem().intValue());
		
		setupEscenario2();
		assertEquals("Hola", listaPila2.getFirst().getItem());
	}

	/**
	 * Prueba 2: Verifica que el tamanio de la lista sencilla si corresponda adecdamente con el de los escenarios 1 y 2
	 */
	@Test
	public void testGetSize() 
	{
		setupEscenario1();
		assertEquals(4, listaPila1.getSize());

		setupEscenario2();
		assertEquals(5, listaPila2.getSize());
	}
	
	/**
	 * Prueba 3: Verifica que si estan agregando objetos a la lista sencilla al inicio
	 */
	@Test
	public void testAddNodeFirst()
	{
		setupEscenario1();
		listaPila1.addNodeFirst(5);
		assertEquals(5, listaPila1.getSize());
		
		setupEscenario2();
		listaPila2.addNodeFirst("?");
		assertEquals(6, listaPila2.getSize());
	}

	/**
	 * Prueba 3: Verifica que si estan agregando objetos a la lista sencilla al final
	 */
	@Test
	public void testAddNodeLast()
	{
		setupEscenario1();
		listaPila1.addNodeLast(5);
		assertEquals(5, listaPila1.getSize());
		
		setupEscenario2();
		listaPila2.addNodeLast("?");
		assertEquals(6, listaPila2.getSize());
	}

	/**
	 * Prueba 5: Verifica que si estan eliminando objetos a la lista sencilla al principio
	 */
	@Test
	public void testEliminateFirst() 
	{
		setupEscenario1();
		listaPila1.eliminateNodeFirst();
		assertEquals(3, listaPila1.getSize());
		
		setupEscenario2();
		listaPila2.eliminateNodeFirst();
		assertEquals(4, listaPila2.getSize());
	}
	
	/**
	 * Prueba 5: Verifica que si estan eliminando objetos a la lista sencilla
	 */
	@Test
	public void testEliminate() 
	{
		setupEscenario1();
		listaPila1.eliminateNode(3);
		assertEquals(3, listaPila1.getSize());
		
		setupEscenario2();
		listaPila2.eliminateNode("Hola");
		assertEquals(4, listaPila2.getSize());
	}
}
