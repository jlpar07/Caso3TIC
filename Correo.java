public class Correo {

    private String idCorreo;
    private int flagSpam; // 0 no es spam, 1 es spam
    private ClienteEmisor clienteEmisor;

    public Correo(String idCorreo, int flagSpam, ClienteEmisor clienteEmisor) {
        this.idCorreo = idCorreo;
        this.flagSpam = flagSpam;
        this.clienteEmisor = clienteEmisor;
    }

    public String getIdCorreo() {
        return idCorreo;
    }

    public ClienteEmisor getClienteEmisor() {
        return clienteEmisor;
    }

    public int getFlagSpam() {
        return flagSpam;
    }

}
