import java.util.List;

public class ManejadorCuarentena extends Thread {
    private BuzonCuarentena buzonCuarentena;
    private BuzonEntrega buzonEntrega;
    private boolean terminado = false;
    private static final int INTERVALO_MS = 1000; 
    public ManejadorCuarentena(BuzonCuarentena buzonCuarentena, BuzonEntrega buzonEntrega) {
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
    }

    @Override
    public void run() {
        System.out.println("ManejadorCuarentena iniciado");
        
        while (!terminado) {
            try {
                procesarCuarentena();
                Thread.sleep(INTERVALO_MS);
                verificarTerminacion();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("ManejadorCuarentena terminado");
    }

    private void procesarCuarentena() {
        List<Correo> mensajesListos = buzonCuarentena.tickYExtraerListos(1000);
        
        for (Correo mensaje : mensajesListos) {
            if (!esMalicioso()) {
                enviarAEntrega(mensaje);
            } else {
                // Es malicioso -> descartar
                System.out.println("Mensaje descartado por malicioso: " + mensaje.getIdCorreo());
            }
        }
    }

    private boolean esMalicioso() {
        // número aleatorio entre 1 y 21
        int numero = 1 + (int)(Math.random() * 21); 
        
        return numero % 7 == 0;
    }

    private void enviarAEntrega(Correo mensaje) {
        boolean depositado = false;
        while (!depositado && !terminado) {
            if (buzonEntrega.intentarDepositar(mensaje)) {
                depositado = true;
                System.out.println("Mensaje de cuarentena enviado a entrega: " + mensaje.getIdCorreo());
            } else {
                // Espera semiactiva - reintentar después de breve pausa
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void verificarTerminacion() {
        if (buzonCuarentena.hayFin() && buzonCuarentena.estaVacio()) {
            terminado = true;
        }
    }
}