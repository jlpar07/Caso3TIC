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
        Correo mensajeInicio = generarCorreo(id, 0);
        depositarCorreo(mensajeInicio);  
        for (int i = 0; i < correos; i++) {
            Correo correo =generarCorreo(id, i+1);
            try {
                Thread.sleep(100); //simula tiempo de produccion
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            depositarCorreo(correo);  //revisar
        }
        Correo mensajeFin = generarCorreo(id, correos + 1);
        depositarCorreo(mensajeFin);
    }

    public Correo generarCorreo(int idCliente, int idCorreo){
        String idUnico = generarIdUnico(idCliente, idCorreo);
        boolean esSpam = Math.random() < 0.4; //40% de probabilidad de ser spam
        
        String tipo;
        if (idCorreo == 0) {
            tipo = "INICIO";
        } else if (idCorreo == correos + 1) {
            tipo = "FIN";
        } else {
            tipo = "NORMAL";
        }
        
        return new Correo(tipo, idUnico, esSpam, this);
    }

    public void depositarCorreo(Correo correo){  

        buzonEntrada.agregarCorreo(correo);

    }

    public String generarIdUnico(int idCliente, int idCorreo){
        return "C-"+idCliente+"-M-"+idCorreo;
    }

}