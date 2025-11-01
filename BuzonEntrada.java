//actua de monitor

import java.util.ArrayList;
import java.util.List;

public class BuzonEntrada{

    private List<Correo> correos;
    private int capacidadMaxima;
    private int contador;

    public BuzonEntrada(int capacidadMaxima) {
        this.correos = new ArrayList<>();
        this.capacidadMaxima = capacidadMaxima;
        this.contador = 0;
    }

    public synchronized void agregarCorreo(Correo correo) {
        while (contador >= capacidadMaxima) {
            try {
                wait(); // Esperar si el buzón está lleno
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        correos.add(correo);
        contador++;
        notifyAll(); // despertar a los filtros para análisis
    }

    //falta que cuando un cliente logra enviar un correo se despierte uno de los filtros

    public synchronized Correo obtenerCorreo(){
        while (correos.isEmpty()) {
            try {
                wait(); // Espera pasiva si no hay correos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        Correo correo = correos.remove(0);
        contador--;
        notifyAll(); // Despierta a los CLIENTES que esperaban espacio
        return correo;
    }

    public synchronized boolean estaVacio() {
        return correos.isEmpty();
    }


       


}
