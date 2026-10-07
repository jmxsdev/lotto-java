package Modelo;

/**
 * Una línea del ticket: una apuesta ya registrada sobre un juego, un horario
 * y una opción concreta, por un monto determinado.
 */
public final class Jugada {

    private final Juego juego;
    private final String horario;
    private final Opcion opcion;
    private final double monto;

    public Jugada(Juego juego, String horario, Opcion opcion, double monto) {
        if (juego == null) {
            throw new IllegalArgumentException("El juego de la jugada no puede ser nulo.");
        }
        if (opcion == null) {
            throw new IllegalArgumentException("La opción de la jugada no puede ser nula.");
        }
        if (horario == null || horario.isBlank()) {
            throw new IllegalArgumentException("El horario de la jugada no puede ser nulo ni vacío.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto de la jugada debe ser mayor que cero.");
        }
        this.juego = juego;
        this.horario = horario;
        this.opcion = opcion;
        this.monto = monto;
    }

    public Juego getJuego() {
        return juego;
    }

    public String getHorario() {
        return horario;
    }

    public Opcion getOpcion() {
        return opcion;
    }

    public double getMonto() {
        return monto;
    }

    public double getPremioPotencial() {
        return juego.premioPotencial(monto);
    }
}
