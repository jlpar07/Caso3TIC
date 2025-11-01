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
            }
        }
        correos.add(correo);
        contador++;
        //notifyAll(); Notificar a los hilos consumidores que hay un nuevo correo
    }

    //falta que cuando un cliente logra enviar un correo se despierte uno de los filtros


}
