package GUI;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Grilla reutilizable de botones con resaltado de la selección.
 * No conoce el dominio: solo muestra captions y avisa quién la use.
 */
public class Botonera extends JPanel {

    private final JButton[] botones;
    private final Border bordeNormal;
    private final Border bordeSeleccion;
    private int indiceSeleccionado = -1;

    public Botonera(String[] captions, int columnas) {
        if (columnas <= 0) {
            throw new IllegalArgumentException("El número de columnas debe ser mayor que cero.");
        }
        setLayout(new GridLayout(0, columnas, 4, 4));

        botones = new JButton[captions.length];
        for (int i = 0; i < captions.length; i++) {
            botones[i] = new JButton(captions[i]);
            add(botones[i]);
        }

        bordeNormal = botones.length > 0 ? botones[0].getBorder() : null;
        bordeSeleccion = BorderFactory.createLineBorder(new Color(0x1565C0), 3);
    }

    public int getCantidad() {
        return botones.length;
    }

    public JButton getBoton(int i) {
        if (i < 0 || i >= botones.length) {
            throw new IndexOutOfBoundsException("Índice de botón fuera de rango: " + i);
        }
        return botones[i];
    }

    public void marcarSeleccion(int indice) {
        if (indiceSeleccionado >= 0 && indiceSeleccionado < botones.length) {
            botones[indiceSeleccionado].setBorder(bordeNormal);
        }
        if (indice >= 0 && indice < botones.length) {
            botones[indice].setBorder(bordeSeleccion);
            indiceSeleccionado = indice;
        }
    }

    public void limpiarSeleccion() {
        if (indiceSeleccionado >= 0 && indiceSeleccionado < botones.length) {
            botones[indiceSeleccionado].setBorder(bordeNormal);
        }
        indiceSeleccionado = -1;
    }
}
