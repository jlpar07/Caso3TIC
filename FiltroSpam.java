//thread analista
public class FiltroSpam extends Thread{

    private BuzonEntrada buzonEntrada;
    private BuzonEntrega buzonEntrega;
    private BuzonCuarentena buzonCuarentena;

    public FiltroSpam(BuzonEntrada buzonEntrada, BuzonEntrega buzonEntrega, BuzonCuarentena buzonCuarentena) {
        this.buzonEntrada = buzonEntrada;
        this.buzonEntrega = buzonEntrega;
        this.buzonCuarentena = buzonCuarentena;
    }

    @Override
    public void run(){

    }

    public void revisarCorreo(){
        // Lógica para revisar los correos del buzón de entrada

    }

    public void depositarCorreo(){
        // Lógica para depositar el correo en el buzón de cuarentena ó en el buzon de entrega
    }

}
