package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Catálogo de juegos en memoria. En un entorno de producción esta información
 * vendría de un archivo JSON o de una API externa; aquí se carga directamente
 * en el constructor para simplificar el proyecto educativo.
 */
public class Catalogo {

    private final List<Juego> juegos = new ArrayList<>();

    public Catalogo() {
        cargar();
    }

    public List<Juego> getJuegos() {
        return Collections.unmodifiableList(juegos);
    }

    private void cargar() {
        List<String> horariosAnimalitos = List.of(
                "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00");

        List<Opcion> animalitos = List.of(
                new Opcion(0, "Ballena", "ballena", null),
                new Opcion(0, "Delfín", "delfin", null),
                new Opcion(1, "Carnero", "carnero", null),
                new Opcion(2, "Toro", "toro", null),
                new Opcion(3, "Ciempiés", "ciempies", null),
                new Opcion(4, "Alacrán", "alacran", null),
                new Opcion(5, "León", "leon", null),
                new Opcion(6, "Rana", "rana", null),
                new Opcion(7, "Perico", "perico", null),
                new Opcion(8, "Ratón", "raton", null),
                new Opcion(9, "Águila", "aguila", null),
                new Opcion(10, "Tigre", "tigre", null),
                new Opcion(11, "Gato", "gato", null),
                new Opcion(12, "Caballo", "caballo", null),
                new Opcion(13, "Mono", "mono", null),
                new Opcion(14, "Paloma", "paloma", null),
                new Opcion(15, "Zorro", "zorro", null),
                new Opcion(16, "Oso", "oso", null),
                new Opcion(17, "Pavo", "pavo", null),
                new Opcion(18, "Burro", "burro", null),
                new Opcion(19, "Chivo", "chivo", null),
                new Opcion(20, "Cochino", "cochino", null),
                new Opcion(21, "Gallo", "gallo", null),
                new Opcion(22, "Camello", "camello", null),
                new Opcion(23, "Cebra", "cebra", null),
                new Opcion(24, "Iguana", "iguana", null),
                new Opcion(25, "Gallina", "gallina", null),
                new Opcion(26, "Vaca", "vaca", null),
                new Opcion(27, "Perro", "perro", null),
                new Opcion(28, "Zamuro", "zamuro", null),
                new Opcion(29, "Elefante", "elefante", null),
                new Opcion(30, "Caimán", "caiman", null),
                new Opcion(31, "Lapa", "lapa", null),
                new Opcion(32, "Ardilla", "ardilla", null),
                new Opcion(33, "Pescado", "pescado", null),
                new Opcion(34, "Venado", "venado", null),
                new Opcion(35, "Jirafa", "jirafa", null),
                new Opcion(36, "Culebra", "culebra", null));

        juegos.add(new Juego(1, "Lotto Activo", "animalitos", 30, animalitos, horariosAnimalitos));

        List<Opcion> terminales = new ArrayList<>();
        for (int n = 0; n <= 99; n++) {
            terminales.add(new Opcion(n, String.format("%02d", n), String.valueOf(n), null));
        }
        juegos.add(new Juego(3, "Terminal Activo", "terminales", 60, terminales, horariosAnimalitos));

        List<String> horariosTripletas = List.of("12:45", "16:45", "19:05");
        List<Opcion> signos = List.of(
                new Opcion(null, "Aries", "ARI", null),
                new Opcion(null, "Tauro", "TAU", null),
                new Opcion(null, "Géminis", "GEM", null),
                new Opcion(null, "Cáncer", "CAN", null),
                new Opcion(null, "Leo", "LEO", null),
                new Opcion(null, "Virgo", "VIR", null),
                new Opcion(null, "Libra", "LIB", null),
                new Opcion(null, "Escorpio", "ESC", null),
                new Opcion(null, "Sagitario", "SAG", null),
                new Opcion(null, "Capricornio", "CAP", null),
                new Opcion(null, "Acuario", "ACU", null),
                new Opcion(null, "Piscis", "PIS", null));
        juegos.add(new Juego(2, "Triple Zulia", "tripletas", 600, signos, horariosTripletas));
    }
}
