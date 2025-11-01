//thread productor
public class ClienteEmisor extends Thread {

    private int id;
    private int correos;  //numero fijo de correos a producir
    private BuzonEntrada buzonEntrada;

    public ClienteEmisor(int id, int correos, BuzonEntrada buzonEntrada) {
        this.id = id;
        this.correos = correos;
        this.buzonEntrada = buzonEntrada;
    }

    @Override
    public void run(){
        System.out.println("Comienza produccion de correos");  //revisar, ¿Será que el mensaje es correo o un print?
        for (int i = 0; i < correos; i++) {
            Correo correo =generarCorreo(id, i);
            try {
                Thread.sleep(100); //simula tiempo de produccion
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            depositarCorreo(correo);  //revisar
        }
        System.out.println("Finaliza produccion de correos");  //revisar, ¿Será que el mensaje es correo o un print?
    }

    public Correo generarCorreo(int idCliente, int idCorreo){
        Correo correo = new Correo(generarIdUnico(idCliente, idCorreo), (int)(Math.random()*2), this);
        return correo;
    }

    public void depositarCorreo(Correo correo){  

        buzonEntrada.agregarCorreo(correo);

    }

    public String generarIdUnico(int idCliente, int idCorreo){
        return "C-"+idCliente+"-M-"+idCorreo;
    }

}