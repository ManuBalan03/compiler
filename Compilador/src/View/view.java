package View;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import compilerTools.Directory;
import compilerTools.Functions;
import controller.Compilador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import compilerTools.Directory;
import controller.Compilador;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class view extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable T_lexemas;
    private Directory directory;
    private JTextPane jtpCode; // Editor de código
    private String title;
    private Timer timerKeyReleased;
    private Compilador compilador;
    private JTable table;

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
        compilador = new Compilador(this);  // El compilador ahora controla la lógica
        components(); // Inicializa los componentes
    }

    // Método que inicializa los componentes
    public void components() {

        title = "Compiler";
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 935, 555);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton BtnNuevo = new JButton("Nuevo");
        BtnNuevo.addActionListener(e -> compilador.nuevoArchivo());
        BtnNuevo.setBounds(0, 10, 85, 21);
        contentPane.add(BtnNuevo);

        JButton BtnGuardar = new JButton("Guardar");
        BtnGuardar.addActionListener(e -> compilador.guardarArchivo());
        BtnGuardar.setBounds(214, 10, 85, 21);
        contentPane.add(BtnGuardar);

        JButton BtnAbrir = new JButton("Abrir");
        BtnAbrir.addActionListener(e -> compilador.abrirArchivo());
        BtnAbrir.setBounds(107, 10, 85, 21);
        contentPane.add(BtnAbrir);

        JButton BtnGuardarComo = new JButton("Guardar como");
        BtnGuardarComo.addActionListener(e -> compilador.guardarArchivoComo());
        BtnGuardarComo.setBounds(312, 10, 129, 21);
        contentPane.add(BtnGuardarComo);

        JButton BtnCompilar = new JButton("Compilar");
        BtnCompilar.addActionListener(e -> compilador.compilarCodigo());
        BtnCompilar.setBounds(465, 10, 85, 21);
        contentPane.add(BtnCompilar);

        JButton BtnEjecutar = new JButton("Ejecutar");
        BtnEjecutar.addActionListener(e -> compilador.ejecutarCodigo());
        BtnEjecutar.setBounds(575, 10, 85, 21);
        contentPane.add(BtnEjecutar);

        // Inicializar el JTextPane
        jtpCode = new JTextPane(); // Editor de código
        JScrollPane scrollPaneCode = new JScrollPane(jtpCode);
        scrollPaneCode.setBounds(10, 50, 500, 400); // Ajustar el tamaño y la posición
        contentPane.add(scrollPaneCode); // Agregar a la ventana

        // Inicializar la tabla de léxicos
        JScrollPane scrollPaneLexemas = new JScrollPane();
        scrollPaneLexemas.setBounds(603, 50, 256, 265);
        contentPane.add(scrollPaneLexemas);

        T_lexemas = new JTable();
        T_lexemas.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Lexema", "Tipo de dato"}
        ));
        scrollPaneLexemas.setViewportView(T_lexemas);

        // Inicializar el directorio después de que jtpCode ya esté inicializado
        try {
            directory = new Directory(this, jtpCode, title, ".comp");
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

        // Establecer la numeración de líneas en el JTextPane
        Functions.setLineNumberOnJTextComponent(jtpCode);

        // Configuración del temporizador para análisis de color (al soltar teclas)
        timerKeyReleased = new Timer((int) (1000 * 0.3), (ActionEvent e) -> {
            timerKeyReleased.stop();
            //compilador.colorAnalysis();  // Delegamos la lógica de análisis de color al controlador
        });

        // Insertar un asterisco en el nombre cuando hay cambios
        Functions.insertAsteriskInName(this, jtpCode, () -> {
            timerKeyReleased.restart();
        });

        // Autocompletado en el JTextPane
        Functions.setAutocompleterJTextComponent(new String[]{}, jtpCode, () -> {
            timerKeyReleased.restart();
        });
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(520, 333, 391, 175);
        contentPane.add(scrollPane);
        
        table = new JTable();
        table.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Token", "Lexema", "Renglon", "Descripcion"
        	}
        ));
        scrollPane.setViewportView(table);
    }

    // Métodos para obtener los componentes relevantes desde la vista
    public JTextPane getJtpCode() {
        return jtpCode;
    }

    public JTable getT_lexemas() {
        return T_lexemas;
    }

    public Directory getDirectory() {
        return directory;
    }
    public String title(){
        return title;
    }
}
