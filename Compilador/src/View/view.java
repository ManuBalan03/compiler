package View;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import compilerTools.Directory;
import compilerTools.Functions;
import controller.Compilador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;
import View.TextLineNumber;

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
    private JTable table_1;

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
        compilador = new Compilador(this); // El compilador ahora controla la lógica
        components(); // Inicializa los componentes
    }

    // Método que inicializa los componentes
    public void components() {

        title = "Compiler";
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1139, 758);
        contentPane = new JPanel();
        contentPane.setBackground(Color.decode("#F2E3D5"));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton BtnNuevo = new JButton("Nuevo");
        BtnNuevo.addActionListener(e -> compilador.nuevoArchivo());
        BtnNuevo.setBackground(Color.decode("#5D8A66"));
        BtnNuevo.setBounds(5, 10, 85, 21);
        contentPane.add(BtnNuevo);

        JButton BtnGuardar = new JButton("Guardar");
        BtnGuardar.addActionListener(e -> compilador.guardarArchivo());
        BtnGuardar.setBounds(219, 10, 85, 21);
        BtnGuardar.setBackground(Color.decode("#45214A"));
        BtnGuardar.setForeground(Color.WHITE);
        contentPane.add(BtnGuardar);

        JButton BtnAbrir = new JButton("Abrir");
        BtnAbrir.addActionListener(e -> compilador.abrirArchivo());
        BtnAbrir.setBounds(112, 10, 85, 21);
        BtnAbrir.setBackground(Color.decode("#323050"));
        BtnAbrir.setForeground(Color.WHITE);
        contentPane.add(BtnAbrir);

        JButton BtnGuardarComo = new JButton("Guardar como");
        BtnGuardarComo.addActionListener(e -> compilador.guardarArchivoComo());
        BtnGuardarComo.setBounds(317, 10, 129, 21);
        BtnGuardarComo.setBackground(Color.decode("#1A6566"));
        BtnGuardarComo.setForeground(Color.WHITE);
        contentPane.add(BtnGuardarComo);

        JButton BtnCompilar = new JButton("Compilar");
        BtnCompilar.addActionListener(e -> compilador.compilarCodigo());
        BtnCompilar.setBounds(470, 10, 90, 21);
        BtnCompilar.setBackground(Color.decode("#21445B"));
        BtnCompilar.setForeground(Color.WHITE);
        contentPane.add(BtnCompilar);

        // inicializar el editor de código
        jtpCode = new JTextPane();

        // agregar la numeración de líneas
        TextLineNumber textLineNumber = new TextLineNumber(jtpCode);

        // configurar el scrollpane con el editor y la numeración
        JScrollPane scrollPaneCode = new JScrollPane(jtpCode);
        scrollPaneCode.setRowHeaderView(textLineNumber); // agrega la numeración al margen izquierdo

        // definir posición y agregar a la ventana
        scrollPaneCode.setBounds(10, 50, 426, 446);
        contentPane.add(scrollPaneCode);

        // Inicializar la tabla de léxicos
        JScrollPane scrollPaneLexemas = new JScrollPane();
        scrollPaneLexemas.setBounds(453, 50, 241, 446);
        contentPane.add(scrollPaneLexemas);

        T_lexemas = new JTable();
        T_lexemas.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "Lexema", "Tipo de dato" }));
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

        // Configuración del temporizador para análisis de color (al soltar teclas)
        timerKeyReleased = new Timer((int) (1000 * 0.3), (ActionEvent e) -> {
            timerKeyReleased.stop();
            // compilador.colorAnalysis(); // Delegamos la lógica de análisis de color al
            // controlador
        });

        // Insertar un asterisco en el nombre cuando hay cambios
        Functions.insertAsteriskInName(this, jtpCode, () -> {
            timerKeyReleased.restart();
        });

        // Autocompletado en el JTextPane
        Functions.setAutocompleterJTextComponent(new String[] {}, jtpCode, () -> {
            timerKeyReleased.restart();
        });

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(72, 517, 912, 175);
        contentPane.add(scrollPane);

        table = new JTable();
        table.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Token", "Lexema", "Renglon", "Descripcion"
                }));
        scrollPane.setViewportView(table);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(714, 56, 370, 440);
        contentPane.add(scrollPane_1);

        table_1 = new JTable();
        table_1.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "l", "Dato objeto", "Dato fuente", "Operador"
                }));
        scrollPane_1.setViewportView(table_1);

        // Para cambiar el color del encabezado de la tabla T_lexemas
        T_lexemas.getTableHeader().setBackground(Color.decode("#89D99D")); // Establecer color de fondo en el encabezado
        T_lexemas.getTableHeader().setForeground(Color.BLACK); // Establecer el color del texto en negro

        // Para cambiar el color del encabezado de la tabla table
        table.getTableHeader().setBackground(Color.decode("#A68F97")); // Establecer color de fondo en el encabezado
        table.getTableHeader().setForeground(Color.BLACK); // Establecer el color del texto en negro

        // Para cambiar el color del encabezado de la tabla table_1
        table_1.getTableHeader().setBackground(Color.decode("#c6e1fc")); // Establecer color de fondo en el encabezado
        table_1.getTableHeader().setForeground(Color.BLACK); // Establecer el color del texto en negro

        // Para el JScrollPane que contiene la tabla T_lexemas
        scrollPaneLexemas.setBackground(Color.decode("#e8eafa")); // Cambiar el fondo del JScrollPane

        // También puedes cambiar el color de fondo de la tabla dentro del JScrollPane
        T_lexemas.setBackground(Color.decode("#e8eafa")); // Cambiar el fondo de la tabla

        // Para el JScrollPane que contiene el texto en jtpCode
        scrollPaneCode.setBackground(Color.decode("#e8eafa")); // Cambiar el fondo del JScrollPane

    }

    // Métodos para obtener los componentes relevantes desde la vista
    public JTextPane getJtpCode() {
        return jtpCode;
    }

    public JTable getT_lexemas() {
        return T_lexemas;
    }

    public JTable getT_errors() {
        return table;
    }

    public Directory getDirectory() {
        return directory;
    }

    public String title() {
        return title;
    }

    public JTable getTable_1() {
        return table_1;
    }

}
