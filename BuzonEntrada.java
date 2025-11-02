// actua de monitor

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

    // espera pasiva (bloqueante)
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

    // espera pasiva con timeout para permitir verificación de FIN
    public synchronized Correo obtenerCorreoConTimeout(long millis) {
        if (correos.isEmpty()) {
            try {
                wait(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            if (correos.isEmpty()) return null;
        }
        Correo correo = correos.remove(0);
        contador--;
        notifyAll();
        return correo;
    }

    public synchronized boolean estaVacio() {
        return correos.isEmpty();
    }
}
