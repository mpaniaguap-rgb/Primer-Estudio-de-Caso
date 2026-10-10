//Pila (LIFO) implementada con una lista enlazada de nodos.
//Mantiene una referencia a la cima, por lo que push, pop, peek e isEmpty se ejecutan en tiempo constante O(1) y no hay capacidad máxima fija.
public class Pila {

    private Nodo cima;   // Nodo superior de la pila (null si está vacía)
    private int tamano;  // Cantidad de elementos almacenados

    public Pila() {
        cima = null;
        tamano = 0;
    }

    //Inserta un token en la cima de la pila.
    public void push(String token) {
        Nodo nuevo = new Nodo(token);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamano++;
    }

    //Retira y devuelve el token de la cima. Lanza excepción si la pila está vacía.
    public String pop() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía: no se puede retirar (pop).");
        }
        String token = cima.getToken();
        cima = cima.getSiguiente();
        tamano--;
        return token;
    }

    //Devuelve el token de la cima sin retirarlo. Lanza excepción si la pila está vacía.
    public String peek() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía: no hay cima que consultar (peek).");
        }
        return cima.getToken();
    }

    //Indica si la pila no contiene elementos.
    public boolean isEmpty() {
        return cima == null;
    }

    //Devuelve la cantidad de elementos de la pila. 
    public int size() {
        return tamano;
    }

    //Devuelve el contenido de la pila escrito de la base a la cima, ej.: [ ( + ]. 
    public String mostrar() {
        if (isEmpty()) {
            return "[ ]";
        }
        String texto = "";
        for (Nodo actual = cima; actual != null; actual = actual.getSiguiente()) {
            texto = actual.getToken() + " " + texto; // Se antepone para dejar la base primero
        }
        return "[ " + texto + "]";
    }
}