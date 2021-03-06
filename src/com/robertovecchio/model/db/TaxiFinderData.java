package com.robertovecchio.model.db;

import com.robertovecchio.model.booking.Booking;
import com.robertovecchio.model.db.error.CustomerNotFoundException;
import com.robertovecchio.model.db.error.HandlerNotFoundException;
import com.robertovecchio.model.db.error.TaxiDriverNotFoundException;
import com.robertovecchio.model.graph.WeightedGraph;
import com.robertovecchio.model.graph.edge.observer.Edge;
import com.robertovecchio.model.graph.node.Node;
import com.robertovecchio.model.graph.node.Parking;
import com.robertovecchio.model.graph.node.WaitingStation;
import com.robertovecchio.model.user.*;
import com.robertovecchio.model.veichle.builderTaxi.Taxi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Questa classe si occupa di simulare un database attraverso l'utilizzo di file. Inoltre vi è applicato un singleton
 * Che restituisce una singola istanza per evitare la costruzione di molteplici "db"
 * @author robertovecchio
 * @version 1.0
 * @since 09/01/2021
 * */
public class TaxiFinderData {

    //==================================================
    //                   Attributi
    //==================================================

    // Singleton - versione Eager
    /**
     * Singleton - versione Eager
     */
    private final static TaxiFinderData instance = new TaxiFinderData();
    /**
     * Variabile statica che rappresenta il percorso verso il file dove saranno memorizzati i clienti
     */
    private final static String customerFileName = "files/customer.txt";             // Percorso file dei clienti
    /**
     * Variabile statica che rappresenta il percorso verso il file dove saranno memorizzati i gestori
     */
    private final static String handlerFileName = "files/handler.txt";               // Percorso file dei gestori
    /**
     * Variabile statica che rappresenta il percorso verso il file dove saranno memorizzati i tassisti
     */
    private final static String taxiDriverFileName = "files/taxiDriver.txt";         // Percorso file dei tassisti
    /**
     * Variabile statica che rappresenta il percorso verso il file dove saranno memorizzati i parcheggi
     */
    private final static String parkingDriverFileName = "files/parking.txt";         // Percorso file dei parcheggi
    /**
     * Variabile statica che rappresenta il percorso verso il file dove saranno memorizzati le postazioni
     */
    private final static String waitingStationFileName = "files/waitingStation.txt"; // Percorso file delle postazioni
    /**
     * Variabile statica che rappresenta il percorso verso il file dove sarà memorizzatoi il grafo
     */
    private final static String graphFileName = "files/graph.txt";                   // Percorso file del grafo
    /**
     * Variabile statica che rappresenta il percorso verso il file dove saranno memorizzate le prenotazioni
     */
    private final static String bookingFileName = "files/booking.txt";               // Percorso file delle prenotazioni

    /**
     * Map utile ad associare ad ogni Stringa un enum
     * @see Map
     * @see GenderType
     */
    private final Map<String,GenderType> genders;
    /**
     * Formatter per la gestione delle date
     * @see DateTimeFormatter
     * */
    private DateTimeFormatter dateTimeFormatter;
    /**
     * Formatter per la gestione dell'orario
     * @see DateTimeFormatter
     */
    private final DateTimeFormatter timeFormatter;
    /**
     * lista osservabile di clienti
     * @see ObservableList
     * @see Customer
     * */
    private final ObservableList<Customer> customers;
    /**
     * lista osservabile di tassisti
     * @see ObservableList
     * @see Customer
     */
    private final ObservableList<TaxiDriver> taxiDrivers;
    /**
     * lista osservabile di parcheggi
     * @see ObservableList
     * @see Parking
     */
    private final ObservableList<Parking> parkings;
    /**
     * lista osservabile di postazioni di attesa
     * @see ObservableList
     * @see WaitingStation
     */
    private final ObservableList<WaitingStation> waitingStations;
    /**
     * lista osservabile di prenotazioni
     * @see ObservableList
     * @see Booking
     */
    private final ObservableList<Booking> bookings;
    /**
     * lista osservabile di gestori
     * @see List
     * @see Handler
     */
    private Set<Handler> handlers;
    /**
     * Variabile che cappresenta l'utente corrente
     * @see UserAccount
     */
    private UserAccount currentUser;
    /**
     * Variabile che rappresenta il grafo
     * @see WeightedGraph
     */
    private WeightedGraph graph;
    /**
     * Variabile che rappresenta il grafo con peso su archi dipendente dal traffico
     */
    private WeightedGraph trafficGraph;

    //==================================================
    //                   Costruttori
    //==================================================

    /**
     * Metodo costruttore privato volto alla realizzazione di un Singleton Pattern - versione Eager
     * */
    private TaxiFinderData(){
        /* Inizializzo le collections */
        this.customers = FXCollections.observableArrayList();
        this.taxiDrivers = FXCollections.observableArrayList();
        this.parkings = FXCollections.observableArrayList();
        this.waitingStations = FXCollections.observableArrayList();
        this.bookings = FXCollections.observableArrayList();
        this.handlers = new HashSet<>();
        this.genders = new HashMap<>();

        /* Inizializzaziamo grafo */
        graph = new WeightedGraph(new ArrayList<>(), new ArrayList<>());
        graph.getVertexes().addAll(this.parkings);
        graph.getVertexes().addAll(this.waitingStations);

        /* Popoliamo l'hasmap */
        genders.put("UOMO", GenderType.MALE);
        genders.put("DONNA", GenderType.FEMALE);
        genders.put("ALTRO", GenderType.OTHER);

        /* Inizializziamo i formatter */
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        System.out.println("Istanza db creata");
    }

    //==================================================
    //                Metodi statici
    //==================================================

    /**
     * Metodo atto a ritornare la singola istanza di TaxiFinderData (Singleton - versione Eager)
     * @return Istanza di TaxiFinderData (singleton)
     * @see TaxiFinderData
     * */
    public static TaxiFinderData getInstance(){
        return instance;
    }

    //==================================================
    //                 Metodi SETTER
    //==================================================

    /**
     * Metodo setter del dateTimeFormatter
     * @param dateTimeFormatter formattazione data e ora
     * @see DateTimeFormatter
     */
    public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter){
        this.dateTimeFormatter = dateTimeFormatter;
    }

    /**
     * Metodo setter dei customers
     * @param customers clienti da impostare
     * @see ObservableList
     * @see Customer
     * */
    public void setCustomers(ObservableList<Customer> customers){
        this.customers.setAll(customers);
    }

    /**
     * Metodo setter dei tassisti
     * @param taxiDrivers Tassisti da impostare
     * @see ObservableList
     * @see TaxiDriver
     * */
    public void setTaxiDrivers(ObservableList<TaxiDriver> taxiDrivers){
        this.taxiDrivers.setAll(taxiDrivers);
    }

    /**
     * Metodo setter dei parcheggi
     * @param parkings Parcheggi da importare
     * @see ObservableList
     * @see Parking
     */
    public void setParkings(ObservableList<Parking> parkings){
        this.parkings.setAll(parkings);
    }

    /**
     * Metodo setter delle postazioni di attesa
     * @param waitingStations postazioni di attesa da importare
     * @see ObservableList
     * @see WaitingStation
     */
    public void setWaitingStation(ObservableList<WaitingStation> waitingStations){
        this.waitingStations.setAll(waitingStations);
    }

    /**
     * Metodo setter delle prenotazioni
     * @param bookings Lista delle prenotazioni
     * @see ObservableList
     * @see Booking
     */
    public void setBookings(ObservableList<Booking> bookings){
        this.bookings.setAll(bookings);
    }

    /**
     * Metodo setter degli handlers
     * @param handlers Gestori da impostare
     * @see Set
     * @see Handler
     */
    public void setHandlers(Set<Handler> handlers){
        this.handlers = handlers;
    }

    /**
     * Metodo setter dell'account corrente
     * @param userAccount Account corrente
     * @see UserAccount
     * */
    public void setCurrentUser(UserAccount userAccount){
        this.currentUser = userAccount;
    }

    /**
     * Metodo setter del grafo corrente
     * @param graph Grafo da impostare
     * @see WeightedGraph
     * @see Node
     */
    public void setGraph(WeightedGraph graph){
        this.graph = graph;
    }

    //==================================================
    //                 Metodi GETTER
    //==================================================

    /**
     * Metodo Getter del dateTimeFormatte
     * @return Un DateTimeFormatter per la data
     */
    public DateTimeFormatter getDateTimeFormatter(){
        return this.dateTimeFormatter;
    }

    /**
     * Metodo Getter del timeFormatter
     * @return un DateTimeFormatter per l'orario
     */
    public DateTimeFormatter getTimeFormatter(){
        return this.timeFormatter;
    }

    /**
     * Metodo Getter dei Customers
     * @return la lista di customers
     * @see ObservableList
     * @see Customer
     */
    public ObservableList<Customer> getCustomers(){
        return this.customers;
    }

    /**
     * Metodo Getter dei tassisti
     * @return la lista dei tassisti
     * @see ObservableList
     * @see TaxiDriver
     */
    public ObservableList<TaxiDriver> getTaxiDrivers(){
        return this.taxiDrivers;
    }

    /**
     * Metodo getter dei parcheggi
     * @return La lista dei parcheggi
     * @see ObservableList
     * @see Parking
     */
    public ObservableList<Parking> getParkings(){
        return this.parkings;
    }

    /**
     * Metodo getter delle postazioni di attesa
     * @return La lista delle postazioni
     * @see ObservableList
     * @see WaitingStation
     */
    public ObservableList<WaitingStation> getWaitingStations(){
        return this.waitingStations;
    }

    /**
     * Metodo getter delle prenotazioni
     * @return La lista delle prenotazioni
     * @see ObservableList
     * @see Booking
     */
    public ObservableList<Booking> getBookings(){
        return this.bookings;
    }

    /**
     * Metodo Getter degli handler
     * @return la lista dei gestori
     * @see Set
     * @see Handler
     */
    public Set<Handler> getHandlers(){
        return this.handlers;
    }

    /**
     * Metodo getter dell'utente corrente
     * @return Utente corrente
     * @see UserAccount
     * */
    public UserAccount getCurrentUser(){
        return this.currentUser;
    }

    /**
     * Metodo getter del grafo
     * @return Il grafo corrente
     * @see WeightedGraph
     */
    public WeightedGraph getGraph(){
        return this.graph;
    }

    /**
     * Metodo getter del grafo in cui vi è traffico
     * @return Grafo con traffico
     * @see WeightedGraph
     */
    public WeightedGraph getTrafficGraph(){
        return this.trafficGraph;
    }

    //==================================================
    //                 Metodi ADDER
    //==================================================

    /**
     * Metodo che aggiunge un cliente all'ObservableList di clienti
     * @param customer Cliente da aggiungere
     * @see Customer
     */
    public void addCustomer(Customer customer){
        this.customers.add(customer);
    }

    /**
     * Metodo che aggiunge un tassista all'ObservableList di tassisti
     * @param taxiDriver Tassista da aggiungere
     * @see TaxiDriver
     */
    public void addTaxiDriver(TaxiDriver taxiDriver){
        this.taxiDrivers.add(taxiDriver);
    }

    /**
     * Metodo che aggiunge un parcheggio all'ObservableList di parcheggi
     * @param parking Parcheggio da aggiungere
     * @see Parking
     */
    public void addParking(Parking parking){
        this.parkings.add(parking);
    }

    /**
     * Metodo che aggiunge una postazione all'ObservableList delle postazioni
     * @param waitingStation Postazione da aggiungere
     * @see WaitingStation
     */
    public void addWaitingStation(WaitingStation waitingStation){
        this.waitingStations.add(waitingStation);
    }

    /**
     * Metodo che aggiunge una prenotazione all'ObservableMap delle prenotazioni
     * @param booking Prenotazione
     * @see ObservableList
     * @see Booking
     */
    public void addBooking(Booking booking){
        this.bookings.add(booking);
    }

    /**
     * Metodo che aggiunge un handler alla lista diegli handler
     * @param handler Gestore da aggiungere
     * @see Handler
     */
    public void addHandler(Handler handler){
        this.handlers.add(handler);
    }

    //==================================================
    //                 Metodi REMOVER
    //==================================================

    /**
     * Metodo che rimuove un cliente dall'observablelist di clienti
     * @param customer Cliente da rimuovere
     * @see Customer
     */
    public void removeCustomer(Customer customer){
        this.customers.remove(customer);
    }

    /**
     * Metodo che rimuove un tassista dall'Observablelist di tassisti
     * @param taxiDriver Tassista da rimuovere
     * @see TaxiDriver
     */
    public void removeTaxiDriver(TaxiDriver taxiDriver){
        this.taxiDrivers.remove(taxiDriver);
    }

    /**
     * Metodo che rimuove un parcheggio dall'ObservableList di Parcheggi
     * @param parking Parcheggio da rimuovere
     * @see Parking
     */
    public void removeParking(Parking parking){
        this.parkings.remove(parking);
    }

    /**
     * Metodo che rimuove una postazione dall'ObservableList delle postazioni
     * @param waitingStation Postazione di attesa da rimuovere
     * @see WaitingStation
     */
    public void removeWaitingStation(WaitingStation waitingStation){
        this.waitingStations.remove(waitingStation);
    }

    /**
     * Metodo che rimuove un handler dalla lista di handlers
     * @param handler Gestore da rimuovere
     * @see Handler
     */
    public void removeHandler(Handler handler){
        this.handlers.remove(handler);
    }

    //==================================================
    //                 Metodi STORE
    //==================================================

    /**
     * Metodo atto alla memorizzazione dei dati inirenti al cliente (Customer)
     * @exception IOException Questo metodo può lanciare una exception nel caso in cui vi sia un errore di input/output
     * */
    public void storeCustomers() throws IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // Creiamo uno stream serializzato di risorse
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(customerFileName))) {
            oos.writeObject(new ArrayList<>(this.customers));
        }
    }

    /**
     * Metodo atto alla memorizzazione tassisti
     * @exception IOException Questo metodo può lanciare una exception nel caso in cui vi sia un errore di input/output
     * */
    public void storeTaxiDrivers() throws IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // Creiamo uno stream serializzato di risorse
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(taxiDriverFileName))) {
            oos.writeObject(new ArrayList<>(this.taxiDrivers));
        }
    }

    /**
     * Metodo atto alla memorizzazione dei parcheggi
     * @throws IOException Questo metodo può lanciare una exception nel caso in cui vi sia un errore di input/output
     */
    public void storeParkings() throws IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // Creiamo uno stream serializzato di risorse
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(parkingDriverFileName))){
            oos.writeObject(new ArrayList<>(this.parkings));
        }
    }

    /**
     * Metodo atto alla memorizzazione delle postazioni
     * @throws IOException Questo metodo può lanciare una exception nel caso in cui vi sia un errore di input/output
     */
    public void storeWaitingStations() throws IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // Creiamo uno stream serializzato di risorse
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(waitingStationFileName))){
            oos.writeObject(new ArrayList<>(this.waitingStations));
        }
    }

    /**
     * Metodo atto alla memorizzazione delle prenptazioni
     * @throws IOException Questo metodo può lanciare una exception nel caso in cui vi sia un errore di input/output
     */
    public void storeBookings() throws IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // Creiamo uno stream serializzato di risorse
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(bookingFileName))){
            oos.writeObject(new ArrayList<>(this.bookings));
        }
    }

    /**
     * Metodo atto alla memorizzazione delle prenotazioni
     * @throws IOException Questo metodo può lanciare una exception nel caso in cui vi sia un errore di input/output
     */
    public void storeGraph() throws IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // Creiamo uno stream serializzato di risorse
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(graphFileName))){
            oos.writeObject(this.graph);
        }
    }

    //==================================================
    //                 Metodi LOADER
    //==================================================

    /**
     * Metodo atto a popolare la lista dei clienti
     * @exception ClassNotFoundException questo metodo potrebbe non trovare la classe richiesta
     * @exception IOException Questo metodo potrebbe generare errori di input/output
     * */
    @SuppressWarnings("unchecked")
    public void loadCustomers() throws ClassNotFoundException, IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // sfruttiamo uno stream per deserializzare risorse
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(customerFileName))){
            this.customers.setAll((ArrayList<Customer>) ois.readObject());
        } catch (EOFException e){
            System.out.println("Customers non presenti");
        }
    }

    /**
     * Metodo atto a popolare la lista dei tassisti
     * @exception ClassNotFoundException questo metodo potrebbe non trovare la classe richiesta
     * @exception IOException Questo metodo potrebbe generare errori di input/output
     * */
    @SuppressWarnings("unchecked")
    public void loadTaxiDrivers() throws ClassNotFoundException, IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // sfruttiamo uno stream per deserializzare risorse
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(taxiDriverFileName))){
            this.taxiDrivers.setAll((ArrayList<TaxiDriver>) ois.readObject());
        } catch (EOFException e){
            System.out.println("Taxi Drivers Non presenti");
        }
    }

    /**
     * Metodo atto a popolare la lista dei parcheggi
     * @exception  ClassNotFoundException questo metodo potrebbe non trovare la classe richiesta
     * @exception IOException Questo metodo potrebbe generare errori di input/output
     */
    @SuppressWarnings("unchecked")
    public void loadParkings() throws ClassNotFoundException, IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // sfruttiamo uno stream per deserializzare risorse
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(parkingDriverFileName))){
            this.parkings.setAll((ArrayList<Parking>) ois.readObject());
        } catch (EOFException e){
            System.out.println("Parcheggi non presenti");
        }
    }

    /**
     * Metodo atto a popolare la lista delle postazioni
     * @exception  ClassNotFoundException questo metodo potrebbe non trovare la classe richiesta
     * @exception IOException Questo metodo potrebbe generare errori di input/output
     */
    @SuppressWarnings("unchecked")
    public void loadWaitingStations() throws ClassNotFoundException, IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // sfruttiamo uno stream per deserializzare risorse
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(waitingStationFileName))){
            this.waitingStations.setAll((ArrayList<WaitingStation>) ois.readObject());
        } catch (EOFException e){
            System.out.println("Postazioni non trovate non presenti");
        }
    }

    /**
     * Metodo atto a popolare la lista delle prenotazioni
     * @exception  ClassNotFoundException questo metodo potrebbe non trovare la classe richiesta
     * @exception IOException Questo metodo potrebbe generare errori di input/output
     */
    @SuppressWarnings("unchecked")
    public void loadBookings() throws ClassNotFoundException, IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // sfruttiamo uno stream per deserializzare risorse
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(bookingFileName))){
            this.bookings.setAll((ArrayList<Booking>) ois.readObject());
        } catch (EOFException e){
            System.out.println("Prenotazioni non trovate non presenti");
        }
    }

    /**
     * Metodo atto a popolare il grafo
     * @throws ClassNotFoundException questo metodo potrebbe non trovare la classe richiesta
     * @throws IOException Questo metodo potrebbe generare errori di input/output
     */
    public void loadGraph() throws ClassNotFoundException, IOException{
        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // sfruttiamo uno stream per deserializzare risorse
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(graphFileName))){
            this.setGraph((WeightedGraph) ois.readObject());
            this.trafficGraph = this.graph.clone();
            System.out.println("======>" + trafficGraph.getEdges());
        } catch (EOFException | CloneNotSupportedException e){
            System.out.println("Grafo non presente");
        }
    }

    /**
     * Metodo atto a popolare la lista di gestori
     * @exception IOException Questo metodo potrebbe generare errori di input/output
     * */
    public void loadHandlers() throws IOException{
        // Costruiamo un path sfruttando la stringa che compone il percorso verso il file degli handler
        Path path = Paths.get(handlerFileName);

        // Inizializziamo una stringa vuota
        String input;

        // try with resources viene sfruttato per chiamare automaticamente il metodo close
        // Inizializziamo un buffered reader per la lettura del file, sfruttando path per il suo costruttore
        try (BufferedReader br = Files.newBufferedReader(path)){
            // Inizializzo input con la stringa letta lungo tutta la linea, questa però viene
            // reinizializzata ad ogni ciclo con il valore alla riga successiva, permettendo l'iterazione
            // su tutto il file
            while ((input = br.readLine()) != null){
                // Inizializzo un array di stringhe sfruttando il REGEX PATTERN del metodo split
                String[] handlerPieces = input.split(" - ");

                // Inizializzo i valori utili al costruttore di handler
                String fiscalCode = handlerPieces[0];
                String name = handlerPieces[1];
                String surname = handlerPieces[2];
                LocalDate dateOfBirth = LocalDate.parse(handlerPieces[3],dateTimeFormatter);
                GenderType genderType = this.genders.get(handlerPieces[4]);
                String email = handlerPieces[5];
                String username = handlerPieces[6];
                String password = handlerPieces[7];

                Handler newHandler = new Handler(fiscalCode, name, surname, dateOfBirth,
                                              genderType, email, username, password);

                this.addHandler(newHandler);
            }
        }
    }

    //==================================================
    //                 Metodi Login
    //==================================================

    /**
     * Metodo dedito all'accesso di un gestore (figura rappresentante un admin)
     * @param handler handler con cui si vuole effettuare l'accesso
     * @return Un Handler nel caso in cui vi ci sia, altrimenti lancia un errore
     * @see Handler
     * @exception HandlerNotFoundException Nel caso in cui non venga trovato un handler permette di utilizzare
     * getMessage() per intrpretare l'errore con maggiore dettaglio
     * @see HandlerNotFoundException
     * */
    public Handler loginHandler(Handler handler) throws HandlerNotFoundException {
        if (this.handlers.contains(handler)){
            for (Handler tempHandler : this.handlers) {
                if (tempHandler.equals(handler)) {
                    return tempHandler;
                }
            }
        }
        throw new HandlerNotFoundException();
    }

    /**
     * Metodo dedito all'accesso di un Tassista
     * @param taxiDriver Tassista con cui si vuole effettuare l'accesso
     * @return Un Tassista nel caso in cui vi ci sia, altrimenti lancia un errore
     * @see TaxiDriver
     * @exception TaxiDriverNotFoundException Nel caso in cui non venga trovato un Tassista permette di utilizzare
     * getMessage() per intrpretare l'errore con maggiore dettaglio
     * @see TaxiDriverNotFoundException
     * */
    public TaxiDriver loginTaxiDriver(TaxiDriver taxiDriver) throws TaxiDriverNotFoundException {
        if (this.taxiDrivers.contains(taxiDriver)){
            for (TaxiDriver driver : this.taxiDrivers){
                if(driver.equals(taxiDriver)){
                    return driver;
                }
            }
        }
        throw new TaxiDriverNotFoundException();
    }

    /**
     * Metodo dedito all'accesso di un Cliente
     * @param customer Cliente con cui si vuole effettuare l'accesso
     * @return Un liente nel caso in cui vi ci sia, altrimenti lancia un errore
     * @see Customer
     * @exception CustomerNotFoundException Nel caso in cui non venga trovato un Cliente permette di utilizzare
     * getMessage() per intrpretare l'errore con maggiore dettaglio
     * @see CustomerNotFoundException
     * */
    public Customer loginCustomer(Customer customer) throws CustomerNotFoundException{
        if (this.customers.contains(customer)){
            for (Customer tempCustomer : this.customers){
                if (tempCustomer.equals(customer)){
                    return tempCustomer;
                }
            }
        }
        throw new CustomerNotFoundException();
    }

    /**
     * Metodo utile ad inserire un tassista in un determitato parcheggio
     * @param taxi Taxi da inserire nel parcheggio
     * @param parking Parcheggio in cui si vuole inserire il tassista
     * @see Taxi
     * @see Parking
     * */
    public void insertTaxiDriverInParking(Taxi taxi, Parking parking){
        parking.getTaxis().add(taxi);
    }

    /**
     * Metodo utile a reperire una lista di tassisti presenti in un determinato parcheggio
     * @param parking Parcheggio da cui si vuole reperire la lista di tassisti
     * @return Lista di tassisti presenti in un determinato parcheggio
     * @see List
     * @see TaxiDriver
     * @see Parking
     */
    public List<TaxiDriver> getTaxiDriversFromParking(Parking parking){
        /* Memorizziamo il parcheggio recuperandolo dalla lista nodi del grafo all'indice del parcheggio fornito come
         * parametro di input */
        Parking parking1 = (Parking) this.getGraph().getVertexes().get(this.getGraph().getVertexes().indexOf(parking));

        /* Inizializziamo una lista di tassisti vuota */
        List<TaxiDriver> taxiDrivers = new ArrayList<>();

        /* Per ogni Taxi nella coda Taxi presente nel parcheggio */
        for (Taxi taxi : parking1.getTaxis()){

            /* Par ogni tassista nella lista tassisti presente nel db */
            for (TaxiDriver taxiDriver : this.getTaxiDrivers()){

                /* Se il veicolo del tassista coincide con quello nel parcheggio */
                if (taxiDriver.getTaxi().equals(taxi)){

                    /* Aggiungilo alla lista da ritornare */
                    taxiDrivers.add(taxiDriver);
                }
            }
        }

        /* Ritorna la lista di autisti nel parcheggio */
        return taxiDrivers;
    }

    /**
     * Metodo utile a ritornare il Parcheggio in cui è locato un Tassista
     * @param taxiDriver Tassista di cui si vuole cercare un parcheggio
     * @return Il parcheggio in cui è òlocato un tassista
     * @throws Exception Questo metodo potrebbe lanciare un'eccezione nel caso in cui il Tassista non sia locato in
     * alcun parcheggio
     * @see Parking
     * @see TaxiDriver
     * @see Exception
     */
    public Parking getParkingFromTaxiDriver(TaxiDriver taxiDriver) throws Exception {
        for (Node node : this.getGraph().getVertexes()){
            if (node instanceof Parking){
                Parking parking = (Parking) node;
                if (parking.getTaxis().contains(taxiDriver.getTaxi())){
                    return parking;
                }
            }
        }
        throw new Exception("Errore");
    }

    /**
     * Metodo utile a ritornare la lista dei parcheggi presenti nel grafo
     * @return Lista di parcheggi presenti nel grafo
     * @see List
     * @see Parking
     */
    public List<Parking> getParkingsFromGraph(){
        List<Parking> parkings = new ArrayList<>();
        for (Node node : TaxiFinderData.getInstance().getGraph().getVertexes()){
            if (node instanceof Parking){
                parkings.add((Parking)node);
            }
        }
        return parkings;
    }

    /**
     * Metodo utile a ritornare un tassista dato un Taxi
     * @param taxi Taxi di cui si vuole conoscere il tassista
     * @return Il Proprietario del Taxi
     * @see Taxi
     * @see TaxiDriver
     * @throws TaxiDriverNotFoundException Questo metodo potrebbe non trovare un tassista associato
     */
    public TaxiDriver takeTaxiDriverFrom(Taxi taxi) throws TaxiDriverNotFoundException {
        for (TaxiDriver taxiDriver : this.getTaxiDrivers()){
            if (taxiDriver.getTaxi().equals(taxi)){
                return taxiDriver;
            }
        }

        throw new TaxiDriverNotFoundException();
    }

    /**
     * Metodo utile a ritornare la lista dei collegamenti di un nodo presente nel grafo
     * @param node Nodo di cui di vogliono conoscere i colelgamenti
     * @return Lista collegamenti
     * @see List
     * @see Edge
     * @see WaitingStation
     */
    public List<Edge> getEdgesFromNode(WaitingStation node){
        List<Edge> edges = new ArrayList<>();

        for (Edge edge : this.getGraph().getEdges()){
            if (edge.getSource().equals(node) || edge.getDestination().equals(node)){
                edges.add(edge);
            }
        }

        return edges;
    }
}