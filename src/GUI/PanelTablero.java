package GUI;

import Modelo.Juego;
import Modelo.Opcion;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Vista del tablero de opciones: muestra las opciones del juego activo
 * y notifica la opción elegida mediante un callback.
 */
public class PanelTablero extends JPanel {

    private final JLabel titulo;
    private final JPanel contenedor;
    private Botonera botonera;
    private Juego juego;
    private Opcion seleccionada;
    private Consumer<Opcion> alSeleccionar;

    public PanelTablero() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        titulo = new JLabel("Seleccione un juego", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        contenedor = new JPanel(new BorderLayout());

        add(titulo, BorderLayout.NORTH);
        add(contenedor, BorderLayout.CENTER);
    }

    public void setAlSeleccionar(Consumer<Opcion> cb) {
        this.alSeleccionar = cb;
    }

    public void mostrar(Juego juego) {
        this.juego = juego;
        this.seleccionada = null;

        titulo.setText(juego.getNombre()
                + "   (premio x" + (int) juego.getPremioMultiplo() + ")");

        contenedor.removeAll();

        List<Opcion> opciones = juego.getOpciones();
        String[] captions = new String[opciones.size()];
        for (int i = 0; i < opciones.size(); i++) {
            captions[i] = opciones.get(i).getEtiquetaTablero();
        }

        int columnas = opciones.size() > 40 ? 10 : opciones.size() > 12 ? 5 : 4;
        botonera = new Botonera(captions, columnas);
        for (int i = 0; i < captions.length; i++) {
            final int indice = i;
            botonera.getBoton(i).addActionListener(e -> seleccionar(indice));
        }

        contenedor.add(botonera, BorderLayout.CENTER);
        contenedor.revalidate();
        contenedor.repaint();
        notificarSeleccion();
    }

    private void seleccionar(int indice) {
        seleccionada = juego.getOpciones().get(indice);
        botonera.marcarSeleccion(indice);
        notificarSeleccion();
    }

    /**
     * Avisa al observador de la selección actual (o de que ya no hay ninguna).
     * El panel no decide qué hacer con ello: solo informa.
     */
    private void notificarSeleccion() {
        if (alSeleccionar != null) {
            alSeleccionar.accept(seleccionada);
        }
    }

    public Opcion getSeleccionada() {
        return seleccionada;
    }

    public void limpiarSeleccion() {
        seleccionada = null;
        if (botonera != null) {
            botonera.limpiarSeleccion();
        }
        notificarSeleccion();
    }
}
