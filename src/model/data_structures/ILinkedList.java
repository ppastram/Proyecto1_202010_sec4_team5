package model.data_structures;

/**
 * Interfaz de la lista encadenada
 * @author Julian Padilla y Pablo Pastrana
 * @param <E> Tipo Generico
 */
public interface ILinkedList <E> extends Iterable<E> 
{
	/**
	 * Retornar el numero de elementos presentes en el arreglo
	 * @return El tamanio de la lista
	 */
	int getSize();
	
	/**
	 * Agregar un objeto a la lista enlazada sencilla al principio
	 * @param pObjeto Nuevo Objeto
	 */
	public void addNodeFirst(E pItem);
	
	/**
	 * Agregar un objeto al final de la lista enlazada sencilla
	 * @param pObjeto Nuevo Objeto
	 */
	public void addNodeLast(E pItem);
	
	/**
	 * Elimina el nodo cabezera de la lista enlazada sencilla
	 */
	public void eliminateNodeFirst();
	
	/**
	 * Elimina el objeto de la lista enlazada sencilla
	 * @param pObjeto Objeto a eliminar
	 */
	public void eliminateNode(E pItem);
	
	/**
	 * Devuelve el objeto en la posicion indicada de la lista enlazada sencilla
	 * @param pPosicion Posicion en la lista
	 * @return Objeto en la posicion especificada
	 */
	public E seeObjetc(int pPosition);
	
	/**
	 * Devuelve el objeto actual del nodo que entra como parametro
	 * @param pNodo Nodo actual
	 * @return El objeto actual del nodo
	 */
	public E seeObjectActual(Node<E> pNodo);
}
