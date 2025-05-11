package eft_s9_alonso_basualdo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class EFT_S9_Alonso_Basualdo {

    static String[] VIP = new String[10];   // V1-V10 ASIENTOS DISPONIBLES PARA VIP
    static String[] PALCO = new String[5]; // P1-P5 Asientos Disponibles para PALCO
    static String[] PLATEABAJA = new String[20]; // PB1-PB20 Asientos disponibles para Platea Baja
    static String[] PLATEALTA = new String[20]; // PA1 - PA20 Asientos disponibles para Platea Alta
    static String[] GALERIA = new String[25]; // G1-G25 Asientos disponibles para Galeria
    
    static Cliente[] clientes = new Cliente[100]; // arreglo para almacenar hasta 100 clientes
    static Venta[] ventas = new Venta[100]; // arreglo para almacenar hasta 100 ventas
    
    static ArrayList<Descuento> descuentos = new ArrayList<>();
    static ArrayList<Reserva> reservas = new ArrayList<>();

    static int contadorClientes = 0; // variables estaticas esta se utiliza para llevar un conteo global de clientes que se incrementa cada nueva vetna (cliente)
    static int contadorVentas = 0; // variable estatica para manejar el contador de ventas
    static int contadorReservas = 0; // variable estatica para manejar las reservas
    static double totalIngresos = 0; // variable statica en formato double para gestionar el total de ingresos

    static Scanner scanner = new Scanner(System.in);

    static void inicializadorAsientos() { // metodo para los asientos
        for (int i = 0; i < VIP.length; i++) {
            VIP[i] = "V" + (i + 1);
        }
        for (int i = 0; i < PALCO.length; i++) {
            PALCO[i] = "P" + (i + 1);
        }
        for (int i = 0; i < PLATEABAJA.length; i++) {
            PLATEABAJA[i] = "PB" + (i + 1);
        }
        for ( int i = 0 ; i < PLATEALTA.length; i++){
            PLATEALTA[i] = "PA" + (i + 1);
        }
        for (int i = 0; i < GALERIA.length; i++){
            GALERIA[i] = "G" + (i +  1);
        }
       
    }

    static void inicializarDescuentos() { // metodo para descuentos
        descuentos.add(new Descuento("E", 0.15)); // descuento para estudiantes
        descuentos.add(new Descuento("T", 0.25)); // descuento para Tercera edad
        descuentos.add(new Descuento("N", 0.10)); // descuento para Niños
        descuentos.add(new Descuento("M", 0.20)); // descuento para Mujeres
    }

    static void mostrarMenuPrincipal() { // menu principal que se llama desde el main
        int op;
        do {
            System.out.println("\n--- TEATRO MORO ---");
            System.out.println("1. Vender entrada");
            System.out.println("2. Reservar entrada");
            System.out.println("3. Modificar reserva");
            System.out.println("4. Imprimir boleta");
            System.out.println("5. Eliminar una venta");
            System.out.println("6. Eliminar una reserva");
            System.out.println("7. Ver resumen");
            System.out.println("0. Salir");
            System.out.print("Seleccione opcion: ");
            while (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Ingrese un numero valido: ");
            }
            op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1 ->venderEntrada();
                case 2 ->reservarEntrada();
                case 3 ->modificarReserva();
                case 4 ->imprimirBoleta();
                case 5 ->eliminarVenta();
                case 6 ->eliminarReserva();
                case 7 -> verResumen();
                case 0 ->
                    System.out.println("Gracias por visitar la pagina de Teatro Moro, hasta pronto!");
                default ->
                    System.out.println("Opcion invalida");
            }
        }while (op != 0);
    }
    static void venderEntrada(){ // metodo para vender entradas
        System.out.println("Procediendo a registrar la venta");
        Cliente clie = validarClienteyObtenerDesc();
        clie.id=contadorClientes;
        clientes[contadorClientes] = clie;
        contadorClientes++;
        System.out.println("Cliente creado: " + clie.nombre + " con ID: " + clie.id);
        
        int sec = seleccionSeccion();
        if (sec < 1 || sec > 5 ){
            System.out.println("Seccion invalida, favor ingrese un numero valido");
            return;
        }
        mostrarAsientosDisponibles(sec); // llama al metodo de asientos
        
        System.out.println("Seleccione el asiento que desea comprar: ");
        String asiento = scanner.nextLine().trim().toUpperCase();
        
        if (!validarAsiento(asiento,sec)){  // observa si el asiento ingresado es valido
            System.out.println("Asiento no disponible.");
            return;
        }
        Reserva r = buscarReserva(asiento); //llama al metodo, busca si existe una reserva asociada y el resultado se guarda en la variable r
        if ( r != null ){ 
            if (!r.esVigente()){
                reservas.remove(r);
                System.out.println("La reserva expiro.");
                return;                               
            }
            reservas.remove(r);
        }
        double precio = precioPorSeccion(asiento); // llama al metodo, entregando un parametro y guarda el precio calculado en la variable precio "determina cuanto cuesta la entrada segun ubicacion
        double descuento = obtenerDescuento(clie.tipo);   // Aqui para verificar que los descuentos se aplican
        double precioFinal = precio * (1 - descuento);
        
        marcarOcupado(asiento);
        Venta v = new Venta(contadorVentas, asiento, precio,descuento,precioFinal,clie.id);  // verifica que la venta se registre correctamente agreagdno los datos, descuentos etc
        ventas[contadorVentas] = v; // se guarda en la posicion 0 o cual corresponda
        contadorVentas++; // se incrementa para la proxima venta
        System.out.println("Venta registrada: "+ v.asiento+ " para cliente: "+ v.idCliente);
        totalIngresos += precioFinal; // suma la venta realizada al total general 
        
        System.out.println("Venta exitosa "+ clie.nombre + ("| Asiento: "+ asiento + ("| Total : $"+precioFinal)));
    }
    
    static void reservarEntrada(){ // metodo para reservar entradas
        System.out.println("Registrando reserva");
        
        Cliente clie = validarClienteyObtenerDesc();
        clie.id= contadorClientes;
        clientes[contadorClientes]= clie; // guarda al cliente en el arreglo e incrementa para la proxima reserva
        contadorClientes++;
         System.out.println("Cliente creado: " + clie.nombre + " con ID: " + clie.id);
        
        int sec = seleccionSeccion();
        if (sec < 1 || sec > 5){  // validacion para cliente al ingresar un numero inferior a 1 o mayor a 5 
            return;
        }
        mostrarAsientosDisponibles(sec);
        
        System.out.println("Eliga la butaca que desea reservar: ");
        String asiento = scanner.nextLine().trim().toUpperCase();
        
        if (!validarAsiento(asiento, sec)){
            System.out.println("Asiento no disponible");
            return;
        }
        
       
        Reserva r = new Reserva(contadorReservas++, clie.id, asiento); //Analiza como se crea una reserva
        reservas.add(r);
        System.out.println("Reserva creada: " + r.asiento + " con ID de reserva: " + r.idReserva);
        System.out.println("Reserva exitosa (valida por 2 min)");
        System.out.println("Id de la reserva: "+ r.idReserva);  
        
        // Hilo para gestionar la expiracion de la reserva
        new Thread(() -> {
            try {
                Thread.sleep(120000); // 2 min
                if (!r.esVigente()){
                    liberarReserva(r);
                }
            }catch(InterruptedException e){
                e.printStackTrace(); // para depuracion
                Thread.currentThread().interrupt(); // restaura el estado de interrupcion
            }
        }).start();
    }
    
    static void modificarReserva(){ // metodo para la modificacion de reservas (pasarlas a ventas)
        System.out.println("Ingrese el ID de la reserva que desea modificar");
        if (!scanner.hasNextInt()){
            System.out.println("ID invalido.");
            return;
        }
        int id = scanner.nextInt();
        scanner.nextLine();
        Reserva r = buscarReserva(id);
        if (r == null){
            System.out.println("Reserva no existe");
            return;
        }
        if (!r.esVigente()){
            System.out.println("Reserva caducada");
            reservas.remove(r);
            return;
        }
        String respuesta;
        OUTER: // etiqueta utilizada para identificar un bloque o un bucle especifico, util cuando se necesita salir de multiples niveles de bucles o bloques de control al mismo tiempo 
        do {
            System.out.println("Confirmar la compra?");
            respuesta = scanner.nextLine().trim().toUpperCase();
            switch (respuesta) {
                case "S" -> {
                    Cliente clie = clientes[r.idCliente];
                    double precio = precioPorSeccion(r.asiento);
                    double descuento = obtenerDescuento(clie.tipo);
                    double precioFinal= precio *(1 - descuento);
                    Venta v = new Venta(contadorVentas, r.asiento, precio, descuento, precioFinal, clie.id);
                    ventas[contadorVentas++] = v;
                    totalIngresos+= precioFinal;
                    reservas.remove(r);
                    System.out.println("Reserva confirmada para venta");
                    break OUTER;
                }
                case "N" -> {
                    System.out.println("Compra cancelada");
                    break OUTER;
                }
                default -> System.out.println("Opcion no valida, favor intente nuevamente Ingrese 'S' para confirmar o 'N' para cancelar)");
            }
        } while (true);
        
    }
    static void imprimirBoleta(){ // metodo para imprimir boleta con parametros definidos
        if (contadorVentas == 0){
            System.out.println("No hay ventas registradas");
            return;
        }
        System.out.println("\n Ventas registradas"); // llama a las variables correspondientes para visualizar las ventas registradas
        for (int i=0; i < contadorVentas; i++){
            Venta v = ventas[i];
            if (v != null){
                Cliente clie = clientes[v.idCliente];
                System.out.println("ID"+ i +"| Asiento: "+ v.asiento + "| Cliente: "+ clie.nombre);
                System.out.println("Tipo de client: "+ tipoCompleto(clie.tipo));
            }
        }
        System.out.println("\n Ingrese ID de venta para imprimir boleta");
        if (!scanner.hasNextInt()){
            System.out.println("ID Invalido");
            scanner.next();
            return;
        }
        int id = scanner.nextInt();
        scanner.nextLine();
        if (id < 0 || id >= contadorVentas || ventas[id] == null){
            System.out.println("Venta no encontrada");
            return;
        }
        
        Venta v = ventas[id]; // Visualizar los datos que se imprimen
        Cliente clie = clientes[v.idCliente]; // recupera los datos del cliente que realizo la venta
        System.out.println("Imprimiendo boleta para venta ID: " + id);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // formato de fechas y horas
        //Boleta con formato
        System.out.println("\n================== BOLETA DE COMPRA ==================");
        System.out.printf("| %-46s |\n", "TEATRO MORO - Gracias por su compra");
        System.out.println("------------------------------------------------------");
        System.out.printf("| %-20s: %-22s |\n", "Cliente", clie.nombre);
        System.out.printf("| %-20s: %-22s |\n", "Tipo de cliente", tipoCompleto(clie.tipo));
        System.out.printf("| %-20s: %-22s |\n", "Asiento", v.asiento);
        System.out.printf("| %-20s: $%-21.2f |\n", "Precio base", v.precio);
        System.out.printf("| %-20s: %-21s |\n", "Descuento aplicado", (int) (v.descuento * 100) + "%");
        System.out.printf("| %-20s: $%-21.2f |\n", "Total pagado", v.precioFinal);
        System.out.printf("| %-20s: %-22s |\n", "Fecha", LocalDateTime.now().format(fmt));
        System.out.println("======================================================");
    }
    
    static void eliminarVenta (){ // metodo para la eliminicacion de ventas registradas
        if (contadorVentas == 0){ // verificacion si existen ventas
            System.out.println("No hay ventas registradas");
            return;
        }
        System.out.println("\n Ventas registradas");
        for (int i=0; i < contadorVentas; i++){
            Venta v = ventas[i];
            if (v != null){
                Cliente clie = clientes[v.idCliente];
                System.out.println("ID"+ i +"| Asiento: "+ v.asiento + "| Cliente: "+ clie.nombre);
            }
        }
        System.out.println("Ingrese ID de venta para eliminar: ");
        if (!scanner.hasNextInt()){
            System.out.println("ID invalido");
            scanner.next();
            return;
        }
        int buscado = scanner.nextInt();
        scanner.nextLine();
        
        Venta v = null;
        int pos = -1;
        for (int i = 0; i < contadorVentas; i++){
            if (ventas[i] != null && ventas[i].idVenta == buscado){
                v = ventas[i];
                pos = i;
                break;
            }
        }
        if ( v == null){
            System.out.println("Venta no encontrada");
            return;
        }
        
        marcarDisponible(v.asiento);
        totalIngresos -= v.precioFinal;
        
        ventas[pos]=null; // Verificar si se borra correctamente la venta
        System.out.println("Venta eliminada");
        
    }
    
    static void eliminarReserva(){
        System.out.println("\nReservas vigentes: ");
        boolean hayReservas = false;
        for (Reserva r : reservas){
            Cliente clie = clientes[r.idCliente];
            System.out.println("ID: "+ r.idReserva+" | Asiento: "+ r.asiento+ "1| Cliente: "+ clie.nombre);
            hayReservas = true;
        }
        if (!hayReservas){
            System.out.println("No hay reservas vigentes.");
            return;
        }
                        
        System.out.println("Ingrese ID de reserva que desea eliminar");
        if (!scanner.hasNextInt()){
            System.out.println("ID invalido");
            scanner.next();
            return;
        }
        int id =scanner.nextInt();
        scanner.nextLine();
        Reserva r = buscarReserva(id);
        if (r == null){
            System.out.println("Reserva no encontrada");
            return;
        }
        marcarDisponible(r.asiento);
        reservas.remove(r); // verificar si se borra correctamente la reserva
        System.out.println("Reserva eliminada con exito");
    }
    
    static void verResumen(){
        System.out.println("Total ventas: "+ contadorVentas);
        System.out.println("Reservas vigentes: "+ reservas.stream().filter(r -> r.esVigente()).count());
        System.out.println("Ingresos totales: "+ totalIngresos);
    }
    
    static void liberarReserva(Reserva r){
        if (reservas.contains(r)){
            reservas.remove(r);
            marcarDisponible(r.asiento);
            System.out.println("La reserva para el asiento "+ r.asiento + " ha expirado y el asiento fue liberado");
        }
    }
    
    static boolean validarTexto (String txt){ 
        return txt != null && !txt.isEmpty() && txt.matches("[\\p{L} ]+"); // verifica si se ingreso algo, que el texto no este vacio, y que el texto contenga letras y espacios
    } // este metodo devuelte true, cuando No es nulo, no esta vacio y solo contiene letras y espacios
    
    static boolean validarAsiento (String asiento, int seccion){
        String[] zona;
        
        switch(seccion){
            case 1 -> zona = VIP;
            case 2 -> zona = PALCO;
            case 3 -> zona = PLATEABAJA;
            case 4 -> zona = PLATEALTA;
            case 5 -> zona = GALERIA;
            default -> {
                return false;
            }
        }
        for (String a : zona) {
            if(asiento.equals(a)){
                return true;
            }
        }
        return false;
    }
    
    static int seleccionSeccion(){// metodo que nos indica las secciones a gestionar
        System.out.println("Seccion (1 = VIP || 2 = PALCO || 3 = PLATEA BAJA || 4 PLATEA ALTA || 5 GALERIA) : ");
        if (!scanner.hasNextInt()){
            System.out.println("Seccion selecionada invalida");
            scanner.next();
            return -1;           
        }
        int f = scanner.nextInt();
        scanner.nextLine();
        if ( f < 1 || f > 5){
            System.out.println("Seccion invalida");
        }
        return f;
    }
    
    static void mostrarAsientosDisponibles(int f) { // realiza un mapeo de los asientos disponibles, por seccion
        String[] zona;
        String nombreZona;
        
        switch(f){
            case 1 -> {
                zona = VIP;
                nombreZona="VIP";
            }
            case 2 -> {
                zona = PALCO;
                nombreZona= "PALCO";
            }
            case 3 -> {
                zona = PLATEABAJA;
                nombreZona="PLATEA BAJA";
            }
            case 4 -> {
                zona = PLATEALTA;
                nombreZona ="PLATEA ALTA";
            }
            case 5 -> {
                zona = GALERIA;
                nombreZona= "GALERIA";
            }
            default -> {
                System.out.println("Zona invalida");
                return;
            }
            
        }
        System.out.println("Butacas disponibles en la zona " + nombreZona + ":");
        
        int columnaPorfila = 5;
        int dispo = 0;
        
        for (int i =0; i < zona.length; i++){
            if (zona[i] != null && !zona[i].equals("X")) {
                System.out.printf("%-6s", zona[i]); // alineacion uniforme
                dispo++;
            }else {
                System.out.printf("%-6s", ""); // espacio en blanco para butacas ocupadas
            }
            if ((i + 1) % columnaPorfila == 0){
                System.out.println(); // salto de linea 
            }
        }
        System.out.println("\nTotal de butacas disponibles: " + dispo);
    }
        
    
    static void marcarOcupado (String asiento){ // metodo para cuando los asientos se compran
        for (String[] arreglo : new String [][]{VIP, PALCO, PLATEABAJA, PLATEALTA, GALERIA}){
            for (int i =0; i< arreglo.length; i++){
                if (asiento.equals(arreglo[i])){
                    arreglo[i]=null;
                    return;
                }
            }
        }
    }
    static void marcarDisponible (String asiento){ // metodo para marcar como disponible, reservas expiradas o ventas eliminadas
        String prefi = asiento.replaceAll("\\d", "");
        int num = Integer.parseInt(asiento.replaceAll("\\D",""));
        switch (prefi){
            case "V" -> VIP[num-1] = asiento;
            case "P" -> PALCO[num-1] = asiento;
            case "PB" -> PLATEABAJA[num-1] = asiento;
            case "PA" -> PLATEALTA[num-1] = asiento;
            case "G" -> GALERIA[num-1] = asiento;
        }
    }
    static double precioPorSeccion (String asiento){ // especifica los precios por seccion corresponidentes al asiento seleccionado y a la nomenclatura correspond.
        if (asiento.startsWith("V"))return 25000;        
        if (asiento.startsWith("P"))return 35000;
        if (asiento.startsWith("PB")) return 20000;
        if (asiento.startsWith("PA")) return 18500;
        if (asiento.startsWith("G")) return 15000;
        return 0;
        
    }
    static double obtenerDescuento (String tipo){ // realiza la gestion de los descuentos
        for (Descuento des : descuentos){
            if (des.tipoCliente.equals(tipo)){
                System.out.println("Descuento aplicado para tipo : " + tipo + " es: " + (int)(des.porcentaje * 100)+ "%");
                return des.porcentaje;
            }
        }
        return 0;
    }
    
    static Cliente validarClienteyObtenerDesc(){ // realiza la validacion de que tipo de cliente y que tipo de descuento le corresponde
        String nombre;
        while(true){
            System.out.println("Ingrese su nombre: ");
            nombre = scanner.nextLine().trim();
            
            if (validarTexto(nombre)){
                break;
            }else{
                System.out.println("El nombre solo puede contener letras y espacios");
            }
        }
        char esMujer;
        while (true)
        {
            System.out.println("Eres mujer? (S/N)");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.length() == 1 && (input.charAt(0) == 's' || input.charAt(0) == 'n')){
                esMujer = input.charAt(0);
                break;
            }else{
                System.out.println("Ingrese 'S' o 'N'");
            }
        }
        int edad;
        while (true) {
            try {
                System.out.println("Ingrese su edad: ");
            edad = Integer.parseInt(scanner.nextLine());
            if (edad >= 0 && edad <= 100){
                break;
            }else {
                System.out.println("Ingrese una edad valida entre 1 y 99");
            }
                
            }catch(NumberFormatException e){
                System.out.println("Ingrese un numero valido para la edad ");
            }
            
        }
        String tipo; // general por defecto
        
        if (esMujer == 's'){
            tipo = "M"; //descuento para mujeres
        }else if( edad <= 15){
            tipo ="N"; // desc niños
        }else if ( edad <= 30){
            tipo = "E"; // desc estudiantes
        }else if ( edad >= 60 && edad <=99){
            tipo = "T"; // desc tercera edad
        }else {
            tipo = "G"; // Adulto general, sin descuento
        }
        for (Descuento des : descuentos){
            if (des.tipoCliente.equalsIgnoreCase(tipo)){
                break;
            }
        }
        System.out.println("tipo asignado al cliente: " + tipo);
        return new Cliente(0,nombre,tipo);
    }    
    static String tipoCompleto (String tipo){
        return switch (tipo){
            case "E" -> "Estudiante";
            case "T" -> "Tercera edad";
            case "N" -> "Niño";
            case "M" -> "Mujer";
            default -> "General";
                
        };
    }
    static Reserva buscarReserva ( String asiento ){ // este metodo debe recorrer el arraylist de reserva y comparar el asiento de cada reserva con el valor recibido
        for(Reserva r: reservas){
            if (r != null && r.asiento.equals(asiento)){
                return r;
            }
        }return null; // No se encuentra ninguna reserva con ese asiento
        
    }
    static Reserva buscarReserva ( int id ){ // metodo que busca las reservas
        for (Reserva re : reservas){
            if ( re != null && re.idReserva == id){
                return re;
            }
        }return null; // no se encontro ninguna reserva con ese ID
    }
        
    
    
    static class Cliente{ // clase para los clientes con atributos
        
        int id;
        String nombre;
        String tipo;
       
        Cliente(int id, String n, String t ){
            this.id = id;
            this.nombre = n;
            this.tipo = t;
            
        }
        
    }
    static class Venta { // clase para las ventas con atributos correspondientes
        int idVenta;
        String asiento;
        double precio;
        double descuento;
        double precioFinal;
        int idCliente;
        
        Venta(int id, String a, double p, double d, double pf, int cid){
            this.idVenta = id;
            this.asiento = a;
            this.precio = p;
            this.descuento =d;
            this.precioFinal = pf;
            this.idCliente = cid;
        }
    }
    
    static class Descuento { // clase para descuentos
        String tipoCliente;
        double porcentaje;
        
        Descuento (String t, double p){
            this.tipoCliente = t;
            this.porcentaje = p;
        }
    }
    
    static class Reserva { // clase para reservas
        int idReserva, idCliente;
        String asiento;
        LocalDateTime tiempo;
        
        Reserva ( int idRes, int idC, String a){
            this.idReserva = idRes;
            this.idCliente = idC;
            this.asiento = a;
            this.tiempo = LocalDateTime.now();
        }
        boolean esVigente(){
            return Duration.between(tiempo,LocalDateTime.now()).toMinutes()<3;
        }
    }
    
    public static void main(String[] args) {
        inicializadorAsientos();
        inicializarDescuentos();
        mostrarMenuPrincipal();  // Breakpoint para iniciar la depuracion del flujo principal
    }
}