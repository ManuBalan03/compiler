package View;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextArea;

public class view extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton BtnAbrir;
	private JTable T_lexemas;

	public void run() {
		try {
			view frame = new view();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public view() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 924, 540);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton BtnNuevo = new JButton("Nuevo");
		BtnNuevo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		BtnNuevo.setBounds(0, 10, 85, 21);
		contentPane.add(BtnNuevo);
		
		JButton BtnGuardar = new JButton("Guardar");
		BtnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		BtnGuardar.setBounds(214, 10, 85, 21);
		contentPane.add(BtnGuardar);
		
		BtnAbrir = new JButton("Abrir");
		BtnAbrir.setBounds(107, 10, 85, 21);
		contentPane.add(BtnAbrir);
		
		JButton BtnGuardarComo = new JButton("Guardar como");
		BtnGuardarComo.setBounds(312, 10, 129, 21);
		contentPane.add(BtnGuardarComo);
		
		JButton BtnCompliar = new JButton("Compilar");
		BtnCompliar.setBounds(465, 10, 85, 21);
		contentPane.add(BtnCompliar);
		
		JButton BtnEjecutar = new JButton("Ejecutar");
		BtnEjecutar.setBounds(575, 10, 85, 21);
		contentPane.add(BtnEjecutar);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(547, 56, 322, 408);
		contentPane.add(scrollPane);
		
		T_lexemas = new JTable();
		T_lexemas.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Lexema", "Tipo de dato"
			}
		));
		scrollPane.setViewportView(T_lexemas);
		
		JButton BtnErrores = new JButton("Errores");
		BtnErrores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		BtnErrores.setBounds(684, 10, 85, 21);
		contentPane.add(BtnErrores);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 84, 438, 319);
		contentPane.add(textArea);
	}
}
