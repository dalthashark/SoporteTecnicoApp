package com.mantenimiento.soportetecnicoapp.modelos;

public class TicketEnvio {
    private String cliente;
    private String telefono;
    private String falla;

    public TicketEnvio(String cliente, String telefono, String falla) {
        this.cliente = cliente;
        this.telefono = telefono;
        this.falla = falla;
    }
}
