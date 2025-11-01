// threads consumidores
public class ServidorEntrega extends Thread {

    private BuzonEntrega buzonEntrega;
    public ServidorEntrega(BuzonEntrega buzonEntrega) {
        this.buzonEntrega = buzonEntrega;
    }

    @Override
    public void run(){
        
    }

    public void leerCorreo(){
        // Lógica para leer los correos del buzón de entrega
    }

}
