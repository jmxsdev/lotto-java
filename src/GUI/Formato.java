package GUI;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utilidades de formato de moneda para mostrar montos en bolívares.
 */
public final class Formato {

    private static final DecimalFormat BS =
            new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.of("es", "VE")));

    private Formato() {
    }

    public static String numero(double v) {
        synchronized (BS) {
            return BS.format(v);
        }
    }

    public static String bs(double v) {
        return "Bs. " + numero(v);
    }
}
