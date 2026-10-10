import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//Análisis de expresiones aritméticas con pilas.
//Soporta números, variables, los operadores + - * / ^ y paréntesis.
//Valida la expresión, la convierte a notación postfija y la evalúa.
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static String expresion = ""; // Expresión ingresada por el usuario

    public static void main(String[] args) {
        menu();
    }

    // ===================== MENÚ =====================

    //Muestra el menú y ejecuta la opción elegida hasta que el usuario salga.
    private static void menu() {
        int opcion = 0;
        do {
            System.out.println("\n===== ANÁLISIS DE EXPRESIONES ARITMÉTICAS =====");
            System.out.println("Expresión actual: " + (expresion.isEmpty() ? "(ninguna)" : expresion));
            System.out.println("1. Ingresar expresión");
            System.out.println("2. Mostrar tokens");
            System.out.println("3. Validar expresión");
            System.out.println("4. Convertir a notación postfija");
            System.out.println("5. Evaluar expresión");
            System.out.println("6. Ver paso a paso la pila (conversión)");
            System.out.println("7. Operar la pila manualmente (push, pop, peek...)");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opción: ");
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = 0; // Entrada no numérica: se tratará como opción inválida
            }

            switch (opcion) {
                case 1: ingresarExpresion(); break;
                case 2: mostrarTokens(); break;
                case 3: validarOpcion(); break;
                case 4: convertirOpcion(false); break;
                case 5: evaluarOpcion(); break;
                case 6: convertirOpcion(true); break;
                case 7: operarPila(); break;
                case 8: System.out.println("Programa finalizado."); break;
                default: System.out.println("Opción inválida. Digite un número del 1 al 8.");
            }
        } while (opcion != 8);
    }

    // ===================== OPCIONES DEL MENÚ =====================

    private static void ingresarExpresion() {
        System.out.print("Digite la expresión (ej.: (5 + 3) * x): ");
        expresion = sc.nextLine().trim();
        System.out.println("Expresión guardada.");
    }

    private static void mostrarTokens() {
        List<String> tokens = obtenerTokens();
        if (tokens != null) {
            System.out.println("Tokens: " + tokens);
        }
    }

    private static void validarOpcion() {
        if (obtenerTokensValidos() != null) {
            System.out.println("La expresión es válida.");
        }
    }

    private static void convertirOpcion(boolean mostrarPasos) {
        List<String> tokens = obtenerTokensValidos();
        if (tokens != null) {
            List<String> postfija = aPostfija(tokens, mostrarPasos);
            System.out.println("Notación postfija: " + String.join(" ", postfija));
        }
    }

    private static void evaluarOpcion() {
        List<String> tokens = obtenerTokensValidos();
        if (tokens == null) {
            return;
        }
        try {
            double resultado = evaluar(aPostfija(tokens, false));
            if (resultado == Math.rint(resultado) && Math.abs(resultado) < 1e15) {
                System.out.println("Resultado: " + (long) resultado); // Sin decimales si es entero
            } else {
                System.out.println("Resultado: " + resultado);
            }
        } catch (ArithmeticException | IllegalStateException e) {
            System.out.println("Error al evaluar: " + e.getMessage());
        }
    }

    //Submenú para probar directamente las operaciones fundamentales de la pila.
    private static void operarPila() {
        Pila pila = new Pila();
        int opcion = 0;
        do {
            System.out.println("\n--- OPERACIONES DE LA PILA ---");
            System.out.println("Pila actual (base -> cima): " + pila.mostrar());
            System.out.println("1. push (apilar un token)");
            System.out.println("2. pop (retirar la cima)");
            System.out.println("3. peek (consultar la cima)");
            System.out.println("4. isEmpty (¿está vacía?)");
            System.out.println("5. size (cantidad de elementos)");
            System.out.println("6. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = 0;
            }
            try {
                switch (opcion) {
                    case 1:
                        System.out.print("Token a apilar: ");
                        String token = sc.nextLine().trim();
                        if (token.isEmpty()) {
                            System.out.println("No se puede apilar un token vacío.");
                        } else {
                            pila.push(token);
                        }
                        break;
                    case 2: System.out.println("Retirado: " + pila.pop()); break;
                    case 3: System.out.println("Cima: " + pila.peek()); break;
                    case 4: System.out.println("¿Vacía? " + (pila.isEmpty() ? "Sí" : "No")); break;
                    case 5: System.out.println("Elementos: " + pila.size()); break;
                    case 6: break;
                    default: System.out.println("Opción inválida. Digite un número del 1 al 6.");
                }
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage()); // Pila vacía
            }
        } while (opcion != 6);
    }

    // ===================== ANÁLISIS =====================

    //Devuelve los tokens de la expresión actual, o null si hay un error.
    private static List<String> obtenerTokens() {
        if (expresion.isEmpty()) {
            System.out.println("Primero ingrese una expresión (opción 1).");
            return null;
        }
        try {
            return tokenizar(expresion);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    //Devuelve los tokens solo si la expresión es válida; si no, informa el error y devuelve null.
    private static List<String> obtenerTokensValidos() {
        List<String> tokens = obtenerTokens();
        if (tokens == null) {
            return null;
        }
        String error = validar(tokens);
        if (error != null) {
            System.out.println("Expresión inválida: " + error);
            return null;
        }
        return tokens;
    }

    //Separa la expresión en tokens: números, variables, operadores y paréntesis.
    private static List<String> tokenizar(String texto) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < texto.length()) {
            char c = texto.charAt(i);
            if (c == ' ') {
                i++;
            } else if (Character.isDigit(c) || c == '.') {          //Número literal
                int inicio = i;
                while (i < texto.length() && (Character.isDigit(texto.charAt(i)) || texto.charAt(i) == '.')) {
                    i++;
                }
                tokens.add(texto.substring(inicio, i));
            } else if (Character.isLetter(c)) {                      //Variable
                int inicio = i;
                while (i < texto.length() && (Character.isLetterOrDigit(texto.charAt(i)) || texto.charAt(i) == '_')) {
                    i++;
                }
                tokens.add(texto.substring(inicio, i));
            } else if ("+-*/^()".indexOf(c) >= 0) {                  //Operador o paréntesis
                tokens.add(String.valueOf(c));
                i++;
            } else {
                throw new IllegalArgumentException("carácter no válido '" + c + "'.");
            }
        }
        return tokens;
    }

    //Valida la expresión usando una pila para los paréntesis.
    //Devuelve un mensaje de error, o null si la expresión es correcta.
    private static String validar(List<String> tokens) {
        Pila parentesis = new Pila();
        boolean esperaOperando = true; //true: se espera número, variable o "("
        for (String t : tokens) {
            if (t.equals("(")) {
                if (!esperaOperando) return "falta un operador antes de '('.";
                parentesis.push(t);
            } else if (t.equals(")")) {
                if (esperaOperando) return "falta un operando antes de ')'.";
                if (parentesis.isEmpty()) return "paréntesis de cierre sin apertura.";
                parentesis.pop();
            } else if (esOperador(t)) {
                if (esperaOperando) return "operador '" + t + "' en posición incorrecta.";
                esperaOperando = true;
            } else { // Operando
                if (!esperaOperando) return "falta un operador antes de '" + t + "'.";
                if (!Character.isLetter(t.charAt(0))) {
                    try {
                        Double.parseDouble(t);
                    } catch (NumberFormatException e) {
                        return "número mal escrito '" + t + "'.";
                    }
                }
                esperaOperando = false;
            }
        }
        if (esperaOperando) return "la expresión está incompleta.";
        if (!parentesis.isEmpty()) return "hay paréntesis de apertura sin cerrar.";
        return null;
    }

    // ===================== CONVERSIÓN Y EVALUACIÓN =====================

    private static boolean esOperador(String t) {
        return t.length() == 1 && "+-*/^".indexOf(t.charAt(0)) >= 0;
    }

    //Prioridad de cada operador: ^ (3), * y / (2), + y - (1).
    private static int prioridad(String op) {
        switch (op) {
            case "^": return 3;
            case "*":
            case "/": return 2;
            default:  return 1;
        }
    }

    //Convierte la expresión infija a postfija con una pila de operadores.
    //Si mostrarPasos es true, imprime el estado de la pila y de la salida en cada token.
    private static List<String> aPostfija(List<String> tokens, boolean mostrarPasos) {
        Pila operadores = new Pila();
        List<String> salida = new ArrayList<>();
        if (mostrarPasos) {
            System.out.printf("%-8s %-20s %s%n", "Token", "Pila", "Salida");
        }
        for (String t : tokens) {
            if (t.equals("(")) {
                operadores.push(t);
            } else if (t.equals(")")) {
                while (!operadores.peek().equals("(")) {   //Se vacía hasta la apertura
                    salida.add(operadores.pop());
                }
                operadores.pop();                           //Se descarta el "("
            } else if (esOperador(t)) {
                // Se retiran los operadores de la cima con mayor prioridad
                // (o igual, salvo "^" que es asociativo por la derecha).
                while (!operadores.isEmpty() && !operadores.peek().equals("(")
                        && (prioridad(operadores.peek()) > prioridad(t)
                        || (prioridad(operadores.peek()) == prioridad(t) && !t.equals("^")))) {
                    salida.add(operadores.pop());
                }
                operadores.push(t);
            } else {
                salida.add(t);                              //Operandos van directo a la salida
            }
            if (mostrarPasos) {
                System.out.printf("%-8s %-20s %s%n", t, operadores.mostrar(), String.join(" ", salida));
            }
        }
        while (!operadores.isEmpty()) {                     //Se vacía la pila al terminar
            salida.add(operadores.pop());
        }
        if (mostrarPasos) {
            System.out.printf("%-8s %-20s %s%n", "(fin)", operadores.mostrar(), String.join(" ", salida));
        }
        return salida;
    }

    //Evalúa una expresión postfija con una pila de valores. Pide el valor de las variables.
    private static double evaluar(List<String> postfija) {
        Pila valores = new Pila();
        Map<String, Double> variables = new HashMap<>();
        for (String t : postfija) {
            if (esOperador(t)) {
                double b = Double.parseDouble(valores.pop()); // Segundo operando (sale primero)
                double a = Double.parseDouble(valores.pop());
                valores.push(String.valueOf(operar(a, b, t)));
            } else if (Character.isLetter(t.charAt(0))) {
                if (!variables.containsKey(t)) {
                    variables.put(t, pedirValor(t));
                }
                valores.push(String.valueOf(variables.get(t)));
            } else {
                valores.push(t);
            }
        }
        return Double.parseDouble(valores.pop());
    }

    private static double operar(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("división entre cero.");
                }
                return a / b;
            default:  return Math.pow(a, b); // "^"
        }
    }

    //Solicita al usuario el valor numérico de una variable hasta que sea válido.
    private static double pedirValor(String nombre) {
        while (true) {
            System.out.print("Valor de " + nombre + ": ");
            try {
                return Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Valor no válido, intente de nuevo.");
            }
        }
    }
}