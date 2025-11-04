public class FiltroSpam extends Thread {
    private BuzonEntrada buzonEntrada;
    private BuzonEntrega buzonEntrega;
    private BuzonCuarentena buzonCuarentena;
    private boolean terminado = false;
    private static int clientesRegistrados = 0;
    private static int clientesTerminados = 0;
    private static boolean finEnviado = false; 

    public FiltroSpam(BuzonEntrada buzonEntrada, BuzonEntrega buzonEntrega, BuzonCuarentena buzonCuarentena) {
        this.buzonEntrada = buzonEntrada;
        this.buzonEntrega = buzonEntrega;
        this.buzonCuarentena = buzonCuarentena;
    }

    @Override
    public void run() {
        while (!terminado) {
            Correo correo = buzonEntrada.obtenerCorreo();
            
            if (correo != null) {
                procesarCorreo(correo);
            }
            verificarTerminacion();
        }
        System.out.println("FiltroSpam terminado");
    }

    private void procesarCorreo(Correo correo) {
        String tipo = correo.getTipo();
        
        switch (tipo) {
            case "INICIO":
                manejarMensajeInicio(correo);
                break;
            case "FIN":
                manejarMensajeFin(correo);
                break;
            case "NORMAL":
                manejarMensajeNormal(correo);
                break;
        }
    }

    private void manejarMensajeInicio(Correo correo) {
        synchronized (FiltroSpam.class) {
            clientesRegistrados++;
            System.out.println("Cliente registrado. Total: " + clientesRegistrados);
        }
        enviarAEntrega(correo);
    }

    private void manejarMensajeFin(Correo correo) {
        synchronized (FiltroSpam.class) {
            clientesTerminados++;
            System.out.println("Cliente terminado. Total: " + clientesTerminados);
        }
    }

    private void manejarMensajeNormal(Correo correo) {
        if (correo.isFlagSpam()) {
            enviarACuarentena(correo);
        } else {
            enviarAEntrega(correo);
        }
    }

    private void enviarACuarentena(Correo correo) {
        int tiempoCuarentena = 10000 + (int)(Math.random() * 10001);
        
        boolean depositado = false;
        while (!depositado && !terminado) {
            if (buzonCuarentena.intentarDepositar(correo, tiempoCuarentena)) {
                depositado = true;
                System.out.println("SPAM enviado a cuarentena: " + correo.getIdCorreo() + " tiempo: " + tiempoCuarentena);
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void enviarAEntrega(Correo correo) {
        boolean depositado = false;
        while (!depositado && !terminado) {
            if (buzonEntrega.intentarDepositar(correo)) {
                depositado = true;
                System.out.println("Correo válido enviado a entrega: " + correo.getIdCorreo());
            } else {
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
        synchronized (FiltroSpam.class) {
            
            // SOLO UN FILTRO envía el FIN del sistema
            if (!finEnviado && 
                clientesTerminados == clientesRegistrados && 
                clientesRegistrados > 0 && 
                buzonEntrada.estaVacio()) {
                
                finEnviado = true; // MARCAR que ya se envió FIN
                System.out.println("FILTRO COORDINADOR - Enviando FIN del sistema");
                enviarMensajesFin();
                terminado = true;
            }

            // Los demás filtros terminan cuando ya se envió FIN
            if (finEnviado) {
                terminado = true;
            }
        }
    }

    private void enviarMensajesFin() {
        // Solo un filtro ejecuta esto (gracias a finEnviado)
        synchronized (FiltroSpam.class) {
            System.out.println("ENVIANDO MENSAJES FIN DEL SISTEMA");
            
            // Enviar FIN al buzón de entrega
            Correo finEntrega = new Correo("FIN", "SISTEMA-FIN-ENTREGA", false, null);
            buzonEntrega.depositarMensajeFin(finEntrega);
            
            // Enviar FIN al buzón de cuarentena  
            Correo finCuarentena = new Correo("FIN", "SISTEMA-FIN-CUARENTENA", false, null);
            buzonCuarentena.depositarMensajeFin(finCuarentena);
            
            System.out.println("Mensajes FIN enviados a entrega y cuarentena");
        }
    }

    public static int getClientesRegistrados() {
        synchronized (FiltroSpam.class) {
            return clientesRegistrados;
        }
    }

    public static int getClientesTerminados() {
        synchronized (FiltroSpam.class) {
            return clientesTerminados;
        }
    }
}