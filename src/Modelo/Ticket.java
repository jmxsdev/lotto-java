package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * El ticket en curso: acumula las jugadas que el taquillero va registrando.
 */
public final class Ticket {

    private final List<Jugada> jugadas = new ArrayList<>();

    public void agregar(Jugada jugada) {
        if (jugada == null) {
            throw new IllegalArgumentException("La jugada no puede ser nula.");
        }
        jugadas.add(jugada);
    }

    public void quitar(int indice) {
        if (indice < 0 || indice >= jugadas.size()) {
            throw new IndexOutOfBoundsException("Índice de jugada fuera de rango: " + indice);
        }
        jugadas.remove(indice);
    }

    public void limpiar() {
        jugadas.clear();
    }

    public List<Jugada> getJugadas() {
        return Collections.unmodifiableList(jugadas);
    }

    public double getTotal() {
        double total = 0;
        for (Jugada jugada : jugadas) {
            total += jugada.getMonto();
        }
        return total;
    }

    public int getCantidad() {
        return jugadas.size();
    }
}
