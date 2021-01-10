package com.robertovecchio.model.db.error;

/**
 * Classe che permette di astrarre un errore quando un handler non viene trovato
 * @author robertovecchio
 * @version 1.0
 * @since 10/01/2021
 * @see Exception
 * */
public class HandlerNotFoundException extends Exception{

    private final static long serialVersionUID = 3L;

    /**
     * Metodo sovrascritto che permette di reperire un messaggio di errore quando viene lanciato
     * */
    @Override
    public String getMessage(){
        return "Handler non Trovato";
    }
}
