package GUI;

import Modelo.Catalogo;
import Modelo.Juego;
import Modelo.Jugada;
import Modelo.Opcion;
import Modelo.Ticket;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * Ventana principal y controlador de la taquilla. Coordina el modelo con las
 * vistas y contiene el flujo completo de venta.
 */
public class VentanaTaquilla extends JFrame {

    private Catalogo catalogo;
    private Ticket ticket;
    private JList<Juego> listaJuegos;
    private JComboBox<String> comboHorarios;
    private PanelTablero panelTablero;
    private PanelTicket panelTicket;
    private JSpinner spinnerMonto;
    private JButton btnAnadir;
    private JButton btnLimpiar;

    public VentanaTaquilla() {
        super("Taquilla de Loterías");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));

        construirInterfaz();
        conectarEventos();
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        catalogo = new Catalogo();
        ticket = new Ticket();

        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Encabezado
        JLabel lblTitulo = new JLabel("Taquilla de Loterías");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 22f));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        btnLimpiar = new JButton("Limpiar ticket");

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(lblTitulo, BorderLayout.WEST);
        panelNorte.add(btnLimpiar, BorderLayout.EAST);

        // Columna izquierda: juegos y horarios
        DefaultListModel<Juego> modeloLista = new DefaultListModel<>();
        for (Juego juego : catalogo.getJuegos()) {
            modeloLista.addElement(juego);
        }
        listaJuegos = new JList<>(modeloLista);
        listaJuegos.setSelectedIndex(0);

        comboHorarios = new JComboBox<>();

        JLabel lblJuegos = new JLabel("Juegos");
        JPanel panelHorario = new JPanel(new BorderLayout(4, 0));
        panelHorario.add(new JLabel("Horario:"), BorderLayout.WEST);
        panelHorario.add(comboHorarios, BorderLayout.CENTER);

        JPanel panelOeste = new JPanel(new BorderLayout(0, 8));
        panelOeste.setPreferredSize(new Dimension(220, 0));
        panelOeste.add(lblJuegos, BorderLayout.NORTH);
        panelOeste.add(new JScrollPane(listaJuegos), BorderLayout.CENTER);
        panelOeste.add(panelHorario, BorderLayout.SOUTH);

        // Centro: tablero
        panelTablero = new PanelTablero();

        // Derecha: monto y ticket
        spinnerMonto = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 100000.0, 1.0));
        btnAnadir = new JButton("Añadir jugada");
        btnAnadir.setEnabled(false); // se habilita cuando hay una opción elegida

        JPanel panelMonto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMonto.add(new JLabel("Monto (Bs):"));
        panelMonto.add(spinnerMonto);
        panelMonto.add(btnAnadir);

        panelTicket = new PanelTicket();

        JPanel panelEste = new JPanel(new BorderLayout(0, 8));
        panelEste.setPreferredSize(new Dimension(430, 0));
        panelEste.add(panelMonto, BorderLayout.NORTH);
        panelEste.add(panelTicket, BorderLayout.CENTER);

        add(panelNorte, BorderLayout.NORTH);
        add(panelOeste, BorderLayout.WEST);
        add(new JScrollPane(panelTablero), BorderLayout.CENTER);
        add(panelEste, BorderLayout.EAST);
    }

    private void conectarEventos() {
        listaJuegos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                alCambiarJuego();
            }
        });
        btnAnadir.addActionListener(e -> anadirJugada());
        btnLimpiar.addActionListener(e -> limpiarTicket());
        panelTicket.setAlQuitar(this::quitarJugada);
        panelTicket.setAlCobrar(this::cobrarTicket);

        // El tablero nos avisa cuándo hay una opción elegida: el botón de añadir
        // solo se habilita en ese momento.
        panelTablero.setAlSeleccionar(opcion -> btnAnadir.setEnabled(opcion != null));

        // Muestra el juego preseleccionado al abrir la ventana.
        alCambiarJuego();
        refrescarTicket();
    }

    private void alCambiarJuego() {
        Juego juego = listaJuegos.getSelectedValue();
        if (juego == null) {
            return;
        }
        comboHorarios.removeAllItems();
        for (String horario : juego.getHorarios()) {
            comboHorarios.addItem(horario);
        }
        panelTablero.mostrar(juego);
    }

    private void anadirJugada() {
        Juego juego = listaJuegos.getSelectedValue();
        if (juego == null) {
            mostrarAviso("Seleccione un juego.");
            return;
        }
        Opcion opcion = panelTablero.getSeleccionada();
        if (opcion == null) {
            mostrarAviso("Seleccione una opción en el tablero.");
            return;
        }
        String horario = (String) comboHorarios.getSelectedItem();
        if (horario == null) {
            mostrarAviso("Seleccione un horario.");
            return;
        }
        double monto = ((Number) spinnerMonto.getValue()).doubleValue();
        if (monto <= 0) {
            mostrarAviso("El monto debe ser mayor que cero.");
            return;
        }

        ticket.agregar(new Jugada(juego, horario, opcion, monto));
        refrescarTicket();
        panelTablero.limpiarSeleccion();
    }

    private void quitarJugada() {
        int fila = panelTicket.getFilaSeleccionada();
        if (fila < 0) {
            mostrarAviso("Seleccione una jugada de la tabla.");
            return;
        }
        ticket.quitar(fila);
        refrescarTicket();
    }

    private void cobrarTicket() {
        if (ticket.getCantidad() == 0) {
            mostrarAviso("No hay jugadas en el ticket.");
            return;
        }

        JTextArea area = new JTextArea(construirTextoTicket());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(480, 320));

        JOptionPane.showMessageDialog(this, scroll, "Ticket", JOptionPane.INFORMATION_MESSAGE);

        ticket.limpiar();
        refrescarTicket();
    }

    private String construirTextoTicket() {
        final String separador = "========================================";
        final String linea = "----------------------------------------";

        StringBuilder sb = new StringBuilder();
        sb.append(separador).append('\n');
        sb.append("          TAQUILLA DE LOTERÍAS\n");
        sb.append(separador).append('\n');

        for (Jugada jugada : ticket.getJugadas()) {
            sb.append("Juego    : ").append(jugada.getJuego().getNombre()).append('\n');
            sb.append("Horario  : ").append(jugada.getHorario()).append('\n');
            sb.append("Jugada   : ").append(jugada.getOpcion().getEtiquetaTablero()).append('\n');
            sb.append("Monto    : ").append(Formato.bs(jugada.getMonto())).append('\n');
            sb.append("Premio   : ").append(Formato.bs(jugada.getPremioPotencial()))
                    .append("  (x").append((int) jugada.getJuego().getPremioMultiplo()).append(")\n");
            sb.append(linea).append('\n');
        }

        sb.append("TOTAL    : ").append(Formato.bs(ticket.getTotal())).append('\n');
        sb.append("JUGADAS  : ").append(ticket.getCantidad()).append('\n');
        sb.append(separador).append('\n');

        return sb.toString();
    }

    private void limpiarTicket() {
        ticket.limpiar();
        refrescarTicket();
        panelTablero.limpiarSeleccion();
    }

    private void refrescarTicket() {
        panelTicket.actualizar(ticket.getJugadas(), ticket.getTotal());
        setTitle("Taquilla de Loterías — " + ticket.getCantidad() + " jugada(s)");
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaTaquilla().setVisible(true));
    }
}
