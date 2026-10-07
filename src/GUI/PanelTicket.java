package GUI;

import Modelo.Jugada;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * Vista del ticket en curso. Expone las acciones de quitar y cobrar,
 * y no contiene lógica de negocio.
 */
public class PanelTicket extends JPanel {

    private final JugadaTableModel modelo;
    private final JTable tabla;
    private final JLabel lblTotal;
    private final JButton btnQuitar;
    private final JButton btnCobrar;
    private Runnable alQuitar;
    private Runnable alCobrar;

    public PanelTicket() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createTitledBorder("Ticket en curso"));

        modelo = new JugadaTableModel();
        tabla = new JTable(modelo);
        tabla.setFillsViewportHeight(true);

        lblTotal = new JLabel("Total: " + Formato.bs(0));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        btnQuitar = new JButton("Quitar seleccionada");
        btnCobrar = new JButton("Cobrar ticket");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnQuitar);
        panelBotones.add(btnCobrar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(lblTotal, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.CENTER);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    public void actualizar(List<Jugada> jugadas, double total) {
        modelo.setJugadas(jugadas);
        lblTotal.setText("Total: " + Formato.bs(total));
        boolean vacio = jugadas == null || jugadas.isEmpty();
        btnCobrar.setEnabled(!vacio);
        btnQuitar.setEnabled(!vacio);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }

    public void setAlQuitar(Runnable r) {
        this.alQuitar = r;
        btnQuitar.addActionListener(e -> {
            if (alQuitar != null) {
                alQuitar.run();
            }
        });
    }

    public void setAlCobrar(Runnable r) {
        this.alCobrar = r;
        btnCobrar.addActionListener(e -> {
            if (alCobrar != null) {
                alCobrar.run();
            }
        });
    }
}
