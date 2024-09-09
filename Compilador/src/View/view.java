package View;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import compilerTools.Directory;
import compilerTools.Functions;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class view extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable T_lexemas;
    public Directory directory;
    private JTextPane jtpCode; // Editor de código
    private String title;
     private Timer timerKeyReleased;

    // Método para correr la vista
    public void run() {
        try {
            view frame = new view();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Constructor
    public view() {
        components(); // Inicializa los componentes
    }

    // Método que inicializa los componentes
    public void components() {

        title = "Compiler";
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 924, 540);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Inicializar el JTextPane
        jtpCode = new JTextPane(); // Editor de código
        JScrollPane scrollPaneCode = new JScrollPane(jtpCode);
        scrollPaneCode.setBounds(10, 50, 500, 400); // Ajustar el tamaño y la posición
        contentPane.add(scrollPaneCode); // Agregar a la ventana

        // Inicializar la tabla de léxicos
        JScrollPane scrollPaneLexemas = new JScrollPane();
        scrollPaneLexemas.setBounds(547, 56, 322, 408);
        contentPane.add(scrollPaneLexemas);

        T_lexemas = new JTable();
        T_lexemas.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Lexema", "Tipo de dato"}
        ));
        scrollPaneLexemas.setViewportView(T_lexemas);

        // Inicializar el directorio después de que jtpCode ya esté inicializado
				try {
					// Inicializar el directorio después de que jtpCode ya esté inicializado
					directory = new Directory(this, jtpCode, title, ".AB");
			} catch (Exception ex) {
					ex.printStackTrace(); 
			}
        // Llamar a la función de cerrar ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                directory.Exit();
                System.exit(0);
            }
        });

        // Mostrar el número de línea en el JTextPane
        Functions.setLineNumberOnJTextComponent(jtpCode);
        timerKeyReleased = new Timer((int) (1000 * 0.3), (ActionEvent e) -> {
            timerKeyReleased.stop();
            colorAnalysis();
        });
        Functions.insertAsteriskInName(this, jtpCode, () -> {
            timerKeyReleased.restart();// para ediciones
        });

        Functions.setAutocompleterJTextComponent(new String[]{"AB","AB1","Hola","Mundo","colores"}, jtpCode, () -> {
            timerKeyReleased.restart();
        });
    }

    private void colorAnalysis() {

    }
}
