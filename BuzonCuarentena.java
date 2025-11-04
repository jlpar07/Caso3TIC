// actua de monitor 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuzonCuarentena {

    private static class Item {
        Correo correo;
        int tiempoRestanteMs;
        Item(Correo c, int ms) {
            this.correo = c;
            this.tiempoRestanteMs = ms;
        }
    }

    private final List<Item> items = new ArrayList<>();
    private boolean finRecibido = false;

    // Semiactiva en el actor llamador: acá nunca bloqueamos, solo aceptamos o no.
    public synchronized boolean intentarDepositar(Correo correo, int tiempoCuarentenaMs) {
        if (finRecibido) return false; // si ya llegó FIN, no aceptamos más
        items.add(new Item(correo, tiempoCuarentenaMs));
        return true;
    }

    // Lo llama el Manejador cada “tick”: reduce tiempos y devuelve los que quedaron listos
    public synchronized List<Correo> tickYExtraerListos(int deltaMs) {
        List<Correo> listos = new ArrayList<>();
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            item.tiempoRestanteMs -= deltaMs;
            if (item.tiempoRestanteMs <= 0) {
                listos.add(item.correo);
                it.remove();
            }
        }
        return listos;
    }

    public synchronized boolean estaVacio() {
        boolean vacio = items.isEmpty();
        System.out.println("DEBUG BuzonCuarentena - Elementos: " + items.size() + ", Vacío: " + vacio);
        return vacio;
    }

    // El FIN para el manejador se maneja como bandera
    public synchronized void depositarMensajeFin(Correo fin) {
        finRecibido = true;
    }

    public synchronized boolean hayFin() {
        return finRecibido;
    }
}
