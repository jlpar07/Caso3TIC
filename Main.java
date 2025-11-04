import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Leer configuración
        Configuracion config = new Configuracion("parametros.txt");
        BuzonEntrada buzonEntrada = new BuzonEntrada(config.getCapacidadEntrada());
        BuzonEntrega buzonEntrega = new BuzonEntrega(config.getCapacidadEntrega());
        BuzonCuarentena buzonCuarentena = new BuzonCuarentena();
        List<ClienteEmisor> clientes = new ArrayList<>();
        List<FiltroSpam> filtros = new ArrayList<>();
        List<ServidorEntrega> servidores = new ArrayList<>();
        for (int i = 0; i < config.getNumClientes(); i++) {
            ClienteEmisor cliente = new ClienteEmisor(
                i + 1, 
                config.getMensajesPorCliente(), 
                buzonEntrada
            );
            clientes.add(cliente);
        }
        for (int i = 0; i < config.getNumFiltros(); i++) {
            FiltroSpam filtro = new FiltroSpam(
                buzonEntrada, 
                buzonEntrega, 
                buzonCuarentena
            );
            filtros.add(filtro);
        }
        for (int i = 0; i < config.getNumServidores(); i++) {
            ServidorEntrega servidor = new ServidorEntrega(
                buzonEntrega
            );
            servidores.add(servidor);
        }
        ManejadorCuarentena manejador = new ManejadorCuarentena(
            buzonCuarentena, 
            buzonEntrega
        );
        
        // Iniciar todos los threads
        System.out.println("Iniciando sistema...");
        for (ServidorEntrega servidor : servidores) {
            servidor.start();
        }
        for (FiltroSpam filtro : filtros) {
            filtro.start();
        }
        manejador.start();
        for (ClienteEmisor cliente : clientes) {
            cliente.start();
        }
        try {
            for (ClienteEmisor cliente : clientes) {
                cliente.join();
            }
            for (FiltroSpam filtro : filtros) {
                filtro.join();
            }
            manejador.join();
            for (ServidorEntrega servidor : servidores) {
                servidor.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Sistema terminado exitosamente");
    }
}
