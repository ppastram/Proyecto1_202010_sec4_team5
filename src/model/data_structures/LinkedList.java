package model.data_structures;

import java.util.Iterator;



/**
 * Clase de la lista enlazada sencilla que implementa la interfaz IListaEnlazada
 * @author Julian Padilla y Pablo Pastrana
 * @param <E> Tipo Generico
 */
public class LinkedList <E> implements ILinkedList <E>
{
	// Atributos

	/**
	 * Nodo que hacer referncia al primero
	 */
	private Node<E> first;

	/**
	 * Tamanio de la lista
	 */
	private int size;

	// Metodo Constructor

	/**
	 * El nodo primero se inicializa en null
	 * El tamanio comienza desde 0
	 */
	public LinkedList()
	{
		first = null;
		size = 0;
	}

	// Metodos

	/**
	 * Retorna el nodo cabezera de la lista
	 * @return Nodo cabeza lista
	 */
	public Node<E> getFirst()
	{
		return first;
	}

	/**
	 * Dar el tamanio de lista enlazada sencilla
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Agregar un objeto a la lista encadenada al principio
	 * @param pObjeto Objeto a agregar al principio
	 */
	public void addNodeFirst(E pItem)
	{
		Node<E> newNode = new Node<E>(pItem);
		newNode.setNext(first);
		first = newNode;
		size++;
	}

	/**
	 * Agregar un objeto a la lista encadenada al final de esta
	 * @param pObjeto Objeto a agregar al final
	 */
	public void addNodeLast(E pItem)
	{
		Node<E> newNode = new Node<E>(pItem);

		if(first == null)
		{
			first = newNode;
		}
		else
		{
			Node<E> actual = first;

			while(actual.getNext() != null)
			{
				actual = actual.getNext();
			}

			actual.setNext(newNode);
		}
		size++;
	}

	/**
	 * Eliminar un objeto de la lista encadenada al principio
	 * @param pObjeto Objeto a eliminar al pricipio
	 */
	public void eliminateNodeFirst()
	{
		first = first.getNext();
		size--;
	}

	/**
	 * Eliminar un objeto de la lista encadenada donde se encuntre el objeto que ingresa por parametro
	 * @param pObjeto Objeto a eliminar
	 */
	public void eliminateNode(E pItem)
	{
		Node<E> temporal = first;

		while(temporal != null && !temporal.getItem().equals(pItem))
		{
			temporal = temporal.getNext();
		}

		if(temporal != null && temporal.getItem().equals(pItem))
		{
			temporal.setNext(temporal.getNext().getNext());
		}

		temporal = temporal.getNext();
		size--;
	}

	/**
	 * Devuele el objeto en la posicion que ingreso por parametro
	 * @param Posicion en la lista
	 */
	public E seeObjetc(int pPosition)
	{
		Node<E> temporal = first;

		for(int i = 0; i < pPosition; i++)
		{
			temporal = temporal.getNext();
		}

		return temporal.getItem();
	}

	/**
	 * Devuele el objeto en el nodo que ingresa por parametro
	 * @param pNodo Nodo actual
	 */
	public E seeObjectActual(Node<E> pNode)
	{
		E answer =null;
		answer = pNode.getItem();
		return answer;
	}

	/**
	 * Permite cambiar la referencia al nodo siguiente
	 * @param pNodo Nodo actual
	 */
	public void addNodeList(Node<E> pNode)
	{	
		if(pNode.getNext() == null)
		{
			pNode = null;
		}

		pNode.getNext();
	}

	/**
	 * Metodo to String
	 */
	public String toString()
	{
		String answer = " ";

		for(Node<E> temp = first; temp != null; temp = temp.getNext())
		{
			answer += temp.getItem().toString() + "\n";
		}

		return answer;
	}

	/**
	 * Crea el iterador para recorrer la lista
	 */
	@Override
	public Iterator<E> iterator() 
	{
		return new ListIterator1<E>(this);
	}
}
