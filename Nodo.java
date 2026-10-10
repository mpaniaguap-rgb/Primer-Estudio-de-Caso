// Nodo de la pila enlazada.
//Cada nodo guarda un token de la expresión (número, variable, operador * o paréntesis) y una referencia al nodo que está debajo de él en la pila.
public class Nodo {

    private String token;   // Texto del token almacenado
    private Nodo siguiente; // Nodo inferior (hacia la base de la pila)

    public Nodo(String token) {
        this.token = token;
        this.siguiente = null;
    }

    public String getToken() {
        return token;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}