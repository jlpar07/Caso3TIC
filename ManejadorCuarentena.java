public class ManejadorCuarentena extends Thread {

    private BuzonCuarentena buzonCuarentena;

    public ManejadorCuarentena(BuzonCuarentena buzonCuarentena) {
        this.buzonCuarentena = buzonCuarentena;
    }

    @Override
    public void run() {
        // Lógica para manejar los correos en cuarentena
    }

    public void inspeccionarCorreos(){
        // Lógica para inspeccionar los correos en cuarentena
        // Los que no pasan la revisión se descartan
        // Los que pasan la revisión se envían al buzón de entrega
    }
    
}
