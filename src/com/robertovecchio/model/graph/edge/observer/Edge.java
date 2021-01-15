package com.robertovecchio.model.graph.edge.observer;

import com.robertovecchio.model.graph.node.Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe utile ad astrarre il concetto di collegamento tra nodi (arco). Inoltre tale classe sarà utile all'algoritmo di
 * Dijkstra per risolvere il problema dello shortest path, pertanto l'edge, ovvero la lineaa sarà definita come
 * direzionale.
 * @author robertovecchio
 * @version 1.0
 * @since 14/01/2021
 * @see ArchObserver
 * @see Serializable
 */
public class Edge extends ArchObserver implements Serializable {

    //==================================================
    //               Attributi statici
    //==================================================

    /** Tipo numerico Long utile alla serializzazione*/
    @Serial
    private final static long serialVersionUID = 12L;

    //==================================================
    //               Variabili d'istanza
    //==================================================
    /**
     * Nodo sorgente dell'edge
     * @see Node
     */
    private final Node source;
    /**
     * Nodo di destinazione dell'edge
     * @see Node
     * */
    private final Node destination;
    /**
     * Peso del nodo
     */
    private final double weight;

    //==================================================
    //                  Costruttori
    //==================================================

    /**
     * Metodo Costruttore di un edge
     * @param source Nodo sorgente
     * @param destination Nodo Destinazione
     * @param weight peso dell'edge
     * @see Node
     */
    public Edge(Node source, Node destination, double weight){
        this.source = source;
        this.destination = destination;
        this.weight = weight;

    }

    //==================================================
    //                  Getter
    //==================================================


    /**
     * Metodo getter del nodo sorgente
     * @return Nodo Sorgente
     * @see Node
     */
    public Node getSource() {
        return source;
    }

    /**
     * Metodo getter del nodo di destinazione
     * @return Nodo di destinazione
     * @see Node
     */
    public Node getDestination() {
        return destination;
    }

    /**
     * Metodo Getter del peso di un edge
     * @return Peso dell'edge
     */
    public double getWeight() {
        return weight;
    }

    @Override
    void update() {
        // stub
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return weight == edge.weight &&
                source.equals(edge.source) &&
                destination.equals(edge.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, weight);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source +
                ", destination=" + destination +
                ", weight=" + weight +
                '}';
    }
}
