// threads consumidores
public class ServidorEntrega extends Thread {

    private final BuzonEntrega buzonEntrega;

    // Estado propio del servidor
    private volatile boolean iniciado = false;
    private volatile boolean terminado = false;

    // Identificador legible en logs
    private final int idServidor;
    private static int secuenciaId = 1;

    public ServidorEntrega(BuzonEntrega buzonEntrega) {
        this.buzonEntrega = buzonEntrega;
        synchronized (ServidorEntrega.class) {
            this.idServidor = secuenciaId++;
        }
    }

    @Override
    public void run(){
        // Muy importante: registrar el servidor para que reciba su propia copia del FIN
        buzonEntrega.registrarServidor();
        System.out.println("Servidor " + idServidor + " listo (esperando INICIO en buzón de entrega).");

        while (!terminado) {
            leerCorreo(); // una iteración de lectura/procesamiento
        }

        System.out.println("Servidor " + idServidor + " terminó.");
    }

     //Una iteración de la "espera activa": intenta tomar un mensaje.
     //- Si no hay, cede CPU (yield) y vuelve a intentar.
     //- Si hay, maneja INICIO/FIN/NORMAL según el estado 'iniciado'.
    
    public void leerCorreo(){
        Correo correo = buzonEntrega.intentarTomar(); // espera ACTIVA

        if (correo == null) {
            // No hay mensajes ahora mismo -> espera activa
            Thread.yield();
            return;
        }

        String tipo = correo.getTipo();
        switch (tipo) {
            case "INICIO":
                // El servidor arranca al ver un INICIO
                iniciado = true;
                System.out.println("Servidor " + idServidor + " recibió INICIO: " + correo.getIdCorreo());
                break;

            case "FIN":
                // Señal de terminación
                terminado = true;
                System.out.println("Servidor " + idServidor + " recibió FIN.");
                break;

            default: // "NORMAL" u otros válidos
                // Si aún no se ha visto INICIO, NO procesar: reinsertar y seguir esperando INICIO
                if (!iniciado) {
                    boolean devuelto = false;
                    while (!devuelto && !terminado) {
                        devuelto = buzonEntrega.intentarDepositar(correo); // semiactiva
                        if (!devuelto) {
                            try { Thread.sleep(50); } 
                            catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                        }
                    }
                    Thread.yield();
                    return;
                }

                // Procesar correo válido
                procesarCorreo(correo);
                break;
        }
    }

    //Simula el procesamiento del correo por un tiempo aleatorio corto.
    private void procesarCorreo(Correo correo){
        int t = 50 + (int)(Math.random() * 251); // 50–300 ms
        try { Thread.sleep(t); } 
        catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        System.out.println("Servidor " + idServidor + " procesó: " + correo.getIdCorreo() + " (" + t + " ms)");
    }
}
