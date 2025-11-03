import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Configuracion {
    private int numClientes;
    private int mensajesPorCliente;
    private int numFiltros;
    private int numServidores;
    private int capacidadEntrada;
    private int capacidadEntrega;

    public Configuracion(String archivoConfig) {
        cargarConfiguracion(archivoConfig);
    }

    private void cargarConfiguracion(String archivoConfig) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoConfig))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("=");
                if (partes.length == 2) {
                    String clave = partes[0].trim();
                    String valor = partes[1].trim();
                    
                    switch (clave) {
                        case "clientes":
                            numClientes = Integer.parseInt(valor);
                            break;
                        case "mensajes_por_cliente":
                            mensajesPorCliente = Integer.parseInt(valor);
                            break;
                        case "filtros":
                            numFiltros = Integer.parseInt(valor);
                            break;
                        case "servidores":
                            numServidores = Integer.parseInt(valor);
                            break;
                        case "capacidad_entrada":
                            capacidadEntrada = Integer.parseInt(valor);
                            break;
                        case "capacidad_entrega":
                            capacidadEntrega = Integer.parseInt(valor);
                            break;
                        default:
                            System.out.println("Parámetro desconocido: " + clave);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo de configuración: " + e.getMessage());
            // Valores por defecto en caso de error
            establecerValoresPorDefecto();
        } catch (NumberFormatException e) {
            System.err.println("Error en formato numérico: " + e.getMessage());
            establecerValoresPorDefecto();
        }
    }

    private void establecerValoresPorDefecto() {
        numClientes = 3;
        mensajesPorCliente = 5;
        numFiltros = 2;
        numServidores = 2;
        capacidadEntrada = 10;
        capacidadEntrega = 8;
    }
    public int getNumClientes() { return numClientes; }
    public int getMensajesPorCliente() { return mensajesPorCliente; }
    public int getNumFiltros() { return numFiltros; }
    public int getNumServidores() { return numServidores; }
    public int getCapacidadEntrada() { return capacidadEntrada; }
    public int getCapacidadEntrega() { return capacidadEntrega; }
}