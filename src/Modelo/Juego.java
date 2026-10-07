package Modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Un juego vendible en la taquilla: define su catálogo de opciones, los
 * horarios en los que se juega y el multiplicador con el que paga.
 */
public final class Juego {

    private final int id;
    private final String nombre;
    private final String tipo;
    private final double premioMultiplo;
    private final List<Opcion> opciones;
    private final List<String> horarios;

    public Juego(int id, String nombre, String tipo, double premioMultiplo,
                 List<Opcion> opciones, List<String> horarios) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del juego no puede ser nulo ni vacío.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo del juego no puede ser nulo ni vacío.");
        }
        if (premioMultiplo <= 0) {
            throw new IllegalArgumentException("El multiplicador de premio debe ser mayor que cero.");
        }
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.premioMultiplo = premioMultiplo;
        this.opciones = (opciones == null) ? new ArrayList<>() : new ArrayList<>(opciones);
        this.horarios = (horarios == null) ? new ArrayList<>() : new ArrayList<>(horarios);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPremioMultiplo() {
        return premioMultiplo;
    }

    public List<Opcion> getOpciones() {
        return java.util.Collections.unmodifiableList(opciones);
    }

    public List<String> getHorarios() {
        return java.util.Collections.unmodifiableList(horarios);
    }

    public double premioPotencial(double monto) {
        return monto * premioMultiplo;
    }

    public int getCantidadOpciones() {
        return opciones.size();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
