package Modelo;

/**
 * Representa una opción apostable dentro de un juego: puede ser un animalito
 * (con su número), un número de terminal o un signo zodiacal.
 * Es inmutable: todos sus campos se asignan una sola vez en el constructor
 * y nunca cambian después.
 */
public final class Opcion {

    private final Integer numero;
    private final String label;
    private final String value;
    private final String icono;

    public Opcion(Integer numero, String label, String value, String icono) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("El label de la opción no puede ser nulo ni vacío.");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El value de la opción no puede ser nulo ni vacío.");
        }
        this.numero = numero;
        this.label = label;
        this.value = value;
        this.icono = icono;
    }

    public Integer getNumero() {
        return numero;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getIcono() {
        return icono;
    }

    /**
     * Texto con el que se muestra la opción en el tablero.
     * Si no hay número (signo zodiacal) o el value ya coincide con el número
     * formateado (casos terminales como "00"), solo se usa el label; en caso
     * contrario se antepone el número separado por un punto medio.
     */
    public String getEtiquetaTablero() {
        if (numero == null) {
            return label;
        }
        if (value.equals(String.valueOf(numero))) {
            return label;
        }
        return numero + " · " + label;
    }

    @Override
    public String toString() {
        return label;
    }
}
