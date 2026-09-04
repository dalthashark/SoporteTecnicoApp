package com.mantenimiento.soportetecnicoapp.db;

import android.os.AsyncTask;
import android.util.Log;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestApiClient {

    // URL simulada del servidor central de la empresa de mantenimiento
    private static final String API_URL = "https://mantenimientoempresa.com";

    public static void enviarTicketAlServidor(final String cliente, final String telefono, final String falla) {
        // Ejecutamos la petición en segundo plano (Background Thread) para no congelar la pantalla del celular
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

                    // Configuración del protocolo HTTP REST
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setRequestProperty("Accept", "application/json");
                    conexion.setDoOutput(true);

                    // Estructuramos el objeto JSON nativo de la orden de soporte
                    String jsonInputString = "{"
                            + "\"cliente\": \"" + cliente + "\","
                            + "\"telefono\": \"" + telefono + "\","
                            + "\"falla_reportada\": \"" + falla + "\""
                            + "}";

                    // Enviamos los bytes por el flujo de red
                    try (OutputStream os = conexion.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Leemos la respuesta del servidor (Ej: 201 significa Creado con Éxito)
                    int codigoRespuesta = conexion.getResponseCode();
                    Log.d("API_REST", "Código de respuesta del servidor central: " + codigoRespuesta);

                    conexion.disconnect();
                } catch (Exception e) {
                    Log.e("API_REST", "Error de conectividad de red: " + e.getMessage());
                }
            }
        });
    }
}
