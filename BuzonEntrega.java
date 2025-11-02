// actua de monitor - capacidad limitada
import java.util.ArrayList;
import java.util.List;

public class BuzonEntrega {

    private final List<Correo> correos = new ArrayList<>();
    private final int capacidadMaxima;
    private int contador = 0;

    // Para duplicar FIN a todos los servidores
    private int servidoresRegistrados = 0;
    private boolean finPendiente = false;
    private Correo plantillaFin = null;

    public BuzonEntrega(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public synchronized void registrarServidor() {
        servidoresRegistrados++;
    }

    // Semiactiva: true si entró, false si estaba lleno
    public synchronized boolean intentarDepositar(Correo correo) {
        if (contador >= capacidadMaxima) return false;
        correos.add(correo);
        contador++;
        notifyAll();
        return true;
    }

    // Activa: si no hay mensajes, devuelve null y el servidor sigue “polling”
    public synchronized Correo intentarTomar() {
        if (correos.isEmpty()) {
            publicarFinSiCorresponde();
            return null;
        }
        Correo c = correos.remove(0);
        contador--;
        if (correos.isEmpty()) {
            publicarFinSiCorresponde();
        }
        return c;
    }

    // El filtro la invoca cuando decide cerrar el sistema
    public synchronized void depositarMensajeFin(Correo fin) {
        plantillaFin = fin;
        finPendiente = true;
        publicarFinSiCorresponde();
    }

    private void publicarFinSiCorresponde() {
        if (finPendiente && correos.isEmpty() && plantillaFin != null && servidoresRegistrados > 0) {
            for (int i = 0; i < servidoresRegistrados; i++) {
                correos.add(plantillaFin); // una copia lógica para cada servidor
                contador++;
            }
            finPendiente = false; // FIN publicado una sola vez
            notifyAll();
        }
    }

    public synchronized boolean estaVacio() {
        return correos.isEmpty();
    }
}
