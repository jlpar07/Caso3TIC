//thread analista

public class FiltroSpam extends Thread {
    private BuzonEntrada buzonEntrada;
    private BuzonEntrega buzonEntrega;
    private BuzonCuarentena buzonCuarentena;
    private boolean terminado = false;
    private static int clientesRegistrados = 0;
    private static int clientesTerminados = 0;

    public FiltroSpam(BuzonEntrada buzonEntrada, BuzonEntrega buzonEntrega, BuzonCuarentena buzonCuarentena) {
        this.buzonEntrada = buzonEntrada;
        this.buzonEntrega = buzonEntrega;
        this.buzonCuarentena = buzonCuarentena;
    }

    @Override
    public void run() {
        while (!terminado) {
            // Espera pasiva por mensajes del buzón de entrada
            Correo correo = buzonEntrada.obtenerCorreo();
            
            if (correo != null) {
                procesarCorreo(correo);
            }
            
            // Verificar condiciones de terminación
            verificarTerminacion();
        }
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
    }

    private void manejarMensajeFin(Correo correo) {
        synchronized (FiltroSpam.class) {
            clientesTerminados++;
            System.out.println("Cliente terminado. Total: " + clientesTerminados);
        }
    }

    private void manejarMensajeNormal(Correo correo) {
        if (correo.isFlagSpam()) {
            // Es SPAM -> enviar a cuarentena
            enviarACuarentena(correo);
        } else {
            // Es VÁLIDO -> enviar a entrega
            enviarAEntrega(correo);
        }
    }

    private void enviarACuarentena(Correo correo) {
        // Asignar tiempo aleatorio de cuarentena (10000-20000)
        int tiempoCuarentena = 10000 + (int)(Math.random() * 10001);
        
        // Espera semiactiva hasta que pueda depositar en cuarentena
        boolean depositado = false;
        while (!depositado && !terminado) {
            if (buzonCuarentena.intentarDepositar(correo, tiempoCuarentena)) {   //falta implementar este metodo pero así seria la lógica
                depositado = true;
                System.out.println("SPAM enviado a cuarentena: " + correo.getIdCorreo() + " tiempo: " + tiempoCuarentena);
            } else {
                // Espera semiactiva - intentar nuevamente después de breve pausa
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
        // Espera semiactiva hasta que pueda depositar en entrega
        boolean depositado = false;
        while (!depositado && !terminado) {
            if (buzonEntrega.intentarDepositar(correo)) {   //falta implementar este metodo pero así seria la lógica
                depositado = true;
                System.out.println("Correo válido enviado a entrega: " + correo.getIdCorreo());
            } else {
                // Espera semiactiva - intentar nuevamente después de breve pausa
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
            // Verificar si todos los clientes terminaron y no hay más mensajes por procesar
            if (clientesTerminados == clientesRegistrados && 
                clientesRegistrados > 0 &&
                buzonEntrada.estaVacio()) {
                
                // Verificar si el buzón de cuarentena está vacío
                if (buzonCuarentena.estaVacio()) {   //falta implementar este metodo pero así seria la lógica
                    enviarMensajesFin();
                    terminado = true;
                }
            }
        }
    }

    private void enviarMensajesFin() {
        // Solo un filtro debe enviar los mensajes FIN
        synchronized (FiltroSpam.class) {
            // Enviar FIN al buzón de entrega
            Correo finEntrega = new Correo("FIN", "SISTEMA-FIN-ENTREGA", false, null);
            buzonEntrega.depositarMensajeFin(finEntrega);   //falta implementar este metodo pero así seria la lógica
            
            // Enviar FIN al buzón de cuarentena  
            Correo finCuarentena = new Correo("FIN", "SISTEMA-FIN-CUARENTENA", false, null);
            buzonCuarentena.depositarMensajeFin(finCuarentena);   //falta implementar este metodo pero así seria la lógica
            
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