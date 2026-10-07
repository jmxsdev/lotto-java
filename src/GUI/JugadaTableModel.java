package GUI;

import Modelo.Jugada;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Adaptador entre el Ticket y el JTable. La tabla no guarda estado propio:
 * solo lee la lista de jugadas que se le entrega.
 */
public class JugadaTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {
        "Juego", "Horario", "Jugada", "Monto (Bs)", "Premio (Bs)"
    };

    private List<Jugada> jugadas = new ArrayList<>();

    public void setJugadas(List<Jugada> nuevas) {
        this.jugadas = (nuevas == null) ? new ArrayList<>() : new ArrayList<>(nuevas);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return jugadas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Jugada jugada = jugadas.get(fila);
        switch (columna) {
            case 0:
                return jugada.getJuego().getNombre();
            case 1:
                return jugada.getHorario();
            case 2:
                return jugada.getOpcion().getEtiquetaTablero();
            case 3:
                return Formato.numero(jugada.getMonto());
            case 4:
                return Formato.numero(jugada.getPremioPotencial());
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    public Jugada getJugadaEn(int fila) {
        return jugadas.get(fila);
    }
}
