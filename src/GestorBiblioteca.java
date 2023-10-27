import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import com.toedter.calendar.JDateChooser;
import java.awt.Color;

public class GestorBiblioteca extends JFrame {

	private JPanel contentPane;
	private JTable tableLibros;
	private DefaultTableModel modeloLibros;
	private JButton btnAnadir;
	private JButton btnEliminar;
	private JButton btnEditar;
	private JTextField txtTitulo;
	private JTextField txtAutor;
	private JTextField txtISBN;
	private JCheckBox checkAutoguardado;
	private JButton btnCargar;
	private JButton btnGuardar;
	private JTextField txtBuscar;
	private JButton btnBuscar;
	private JTable tablePrestamos;
	private DefaultTableModel modeloPrestamos;
	private JTextField txtUsuario;
	private JLabel lblPrestamo;
	private JButton btnPrestamo;
	private JLabel lblFechaDeInicio;
	private JLabel lblFechaDeFinalizacion;
	private static final int DURACION_ESTANDAR_PRESTAMO = 15;
	private JButton btnDevuelto;
	private JScrollPane scrollPanePrestamos;

	public static void main(String[] args) {
		GestorBiblioteca frame = new GestorBiblioteca();
		frame.setVisible(true);
	}

	public GestorBiblioteca() {
		ImageIcon icon = new ImageIcon("icono.png");
		Image appIcon = icon.getImage();
		setIconImage(appIcon);
		setTitle("Biblioteca");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1638, 612);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(234, 234, 234));
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tableLibros.getSelectionModel().clearSelection();
				tablePrestamos.getSelectionModel().clearSelection();
				updateEliminar();
				updateEditar();
				updateMarcarDevuelto();
			}
		});
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPaneLibros = new JScrollPane();
		scrollPaneLibros.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tableLibros.getSelectionModel().clearSelection();
				tablePrestamos.getSelectionModel().clearSelection();
				updateEliminar();
				updateEditar();
				updateMarcarDevuelto();
			}
		});
		scrollPaneLibros.setBounds(10, 85, 535, 445);
		contentPane.add(scrollPaneLibros);

		// TABLA LIBROS
		tableLibros = new JTable();
		// Rellenar los campos de texto al seleccionar una fila
		tableLibros.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selectedRowInView = tableLibros.getSelectedRow();
				int fila = tableLibros.convertRowIndexToModel(selectedRowInView);
				txtTitulo.setText((String) modeloLibros.getValueAt(fila, 0));
				txtAutor.setText((String) modeloLibros.getValueAt(fila, 1));
				txtISBN.setText((String) modeloLibros.getValueAt(fila, 2));
				updateEliminar();
				updatePrestamo();
				updateEditar();
				tablePrestamos.clearSelection();
				updateMarcarDevuelto();
			}
		});

		modeloLibros = new DefaultTableModel(new Object[][] {}, new String[] { "Titulo", "Autor", "ISBN" }) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Make all cells non-editable
			}
		};

		tableLibros.setModel(modeloLibros);
		tableLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scrollPaneLibros.setViewportView(tableLibros);

		TableColumn colISBN = tableLibros.getColumnModel().getColumn(2);
		colISBN.setPreferredWidth(0); // para que la columna ISBN sea mas estrecha
		TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(modeloLibros);
		tableLibros.setRowSorter(rowSorter);

		// BOTON AÑADIR
		btnAnadir = new JButton("Añadir");
		btnAnadir.setToolTipText("Añadir un libro");
		btnAnadir.setEnabled(false);
		btnAnadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // añade una fila a la tabla con los datos de los campos
				modeloLibros.addRow(new String[] { txtTitulo.getText(), txtAutor.getText(), txtISBN.getText() });
				limpiarCampos();
				if (checkAutoguardado.isEnabled()) // guarda despues de cada cambio si autoguardado esta habilitado
					guardarLibros();
			}
		});
		btnAnadir.setBounds(555, 88, 109, 23);
		contentPane.add(btnAnadir);

		// BOTON ELIMINAR
		btnEliminar = new JButton("Eliminar");
		btnEliminar.setToolTipText("Eliminar el libro seleccionado");
		btnEliminar.setEnabled(false);
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // elimina la fila seleccionada
				int selectedRowInView = tableLibros.getSelectedRow();
				int fila = tableLibros.convertRowIndexToModel(selectedRowInView);
				modeloLibros.removeRow(fila);
				limpiarCampos();
				txtBuscar.setText("");
				btnBuscar.doClick();
				if (checkAutoguardado.isEnabled()) // guarda despues de cada cambio si autoguardado esta habilitado
					guardarLibros();
			}
		});
		btnEliminar.setBounds(555, 126, 109, 23);
		contentPane.add(btnEliminar);

		// BOTON EDITAR
		btnEditar = new JButton("Modificar");
		btnEditar.setToolTipText("Modificar el libro seleccionado");
		btnEditar.setEnabled(false);
		btnEditar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // sustituye una fila por los valores de los campos de texto
				int selectedRowInView = tableLibros.getSelectedRow();
				int fila = tableLibros.convertRowIndexToModel(selectedRowInView);
				modeloLibros.setValueAt(txtTitulo.getText(), fila, 0);
				modeloLibros.setValueAt(txtAutor.getText(), fila, 1);
				modeloLibros.setValueAt(txtISBN.getText(), fila, 2);
				limpiarCampos();
				txtBuscar.setText("");
				btnBuscar.doClick();
				if (checkAutoguardado.isEnabled()) // guarda despues de cada cambio si autoguardado esta habilitado
					guardarLibros();
				tableLibros.clearSelection();
			}
		});
		btnEditar.setBounds(555, 160, 109, 23);
		contentPane.add(btnEditar);

		// CAMPOS DE ENTRADA
		// actualizan los botones añadir y modificar, para deshabilitarlos si los campos
		// estan vacios
		txtTitulo = new JTextField();
		txtTitulo.setToolTipText("Titulo");
		txtTitulo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAnadir();
				updateEditar();
			}
		});
		txtTitulo.setBounds(10, 541, 171, 20);
		contentPane.add(txtTitulo);
		txtTitulo.setColumns(10);

		txtAutor = new JTextField();
		txtAutor.setToolTipText("Autor");
		txtAutor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAnadir();
				updateEditar();
			}
		});
		txtAutor.setBounds(191, 541, 171, 20);
		contentPane.add(txtAutor);
		txtAutor.setColumns(10);

		txtISBN = new JTextField();
		txtISBN.setToolTipText("ISBN");
		txtISBN.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAnadir();
				updateEditar();
			}
		});
		txtISBN.setBounds(374, 541, 171, 20);
		contentPane.add(txtISBN);
		txtISBN.setColumns(10);

		// BOTON GUARDAR
		btnGuardar = new JButton("Guardar");
		btnGuardar.setToolTipText("Guardar las tablas en archivos de texto");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guardarLibros();
				guardarPrestamos();
			}
		});
		btnGuardar.setBounds(10, 11, 80, 23);
		contentPane.add(btnGuardar);

		// BOTON CARGAR
		btnCargar = new JButton("Cargar");
		btnCargar.setToolTipText("Cargar datos de los archivos de texto en las tablas");
		btnCargar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cargar();
			}
		});
		btnCargar.setBounds(106, 11, 80, 23);
		contentPane.add(btnCargar);

		// CHECK AUTOGUARDADO
		// si esta habilitado, se deshabilitan los botones de guardar y cargar, y se
		// guarda automaticamente cada vez que se produce un cambio (se pulsan los
		// botones añadir, eliminar o modificar)
		checkAutoguardado = new JCheckBox("Autoguardado");
		checkAutoguardado.setToolTipText("Cambiar entre los modos de guardado manual y automatico");
		checkAutoguardado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // actualizar los botones
				if (checkAutoguardado.isSelected()) {
					btnGuardar.setEnabled(false);
					btnCargar.setEnabled(false);
					guardarLibros();
				} else {
					btnGuardar.setEnabled(true);
					btnCargar.setEnabled(true);
				}
			}
		});
		checkAutoguardado.setBounds(196, 11, 116, 23);
		contentPane.add(checkAutoguardado);

		// CAMPO BUSCAR
		txtBuscar = new JTextField();
		txtBuscar.addKeyListener(new KeyAdapter() { // al pulsar enter se simula una pulsacion al botón de buscar
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					btnBuscar.doClick();
				}
			}
		});
		txtBuscar.setToolTipText("Titulo, autor o ISBN del libro a buscar");
		txtBuscar.setBounds(10, 54, 354, 20);
		contentPane.add(txtBuscar);
		txtBuscar.setColumns(10);

		// BOTON BUSCAR
		// oculta las filas de la tabla que no contengan el texto buscado
		btnBuscar = new JButton("Buscar");
		btnBuscar.setToolTipText("Buscar en la tabla de libros");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String textoBuscado = txtBuscar.getText().toLowerCase();
				if (textoBuscado.length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					// Comprobar si el texto buscado aparece en alguna celda
					RowFilter<DefaultTableModel, Integer> filter = new RowFilter<DefaultTableModel, Integer>() {
						@Override
						public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
							DefaultTableModel model = entry.getModel();
							int rowCount = model.getRowCount();
							int columnCount = model.getColumnCount();

							for (int row = entry.getIdentifier(), col = 0; col < columnCount; col++) {
								Object cellValue = model.getValueAt(row, col);
								if (cellValue != null && cellValue.toString().toLowerCase().contains(textoBuscado)) {
									return true;
								}
							}
							return false;
						}
					};
					rowSorter.setRowFilter(filter);
				}
			}
		});
		btnBuscar.setBounds(374, 53, 89, 23);
		contentPane.add(btnBuscar);

		// SCROLLPANEL PRESTAMOS
		scrollPanePrestamos = new JScrollPane();
		scrollPanePrestamos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tableLibros.getSelectionModel().clearSelection();
				tablePrestamos.getSelectionModel().clearSelection();
				updateEliminar();
				updateEditar();
				updateMarcarDevuelto();
			}
		});
		scrollPanePrestamos.setBounds(674, 85, 938, 445);
		scrollPanePrestamos.setBackground(Color.WHITE);
		contentPane.add(scrollPanePrestamos);

		// TABLA PRESTAMOS
		tablePrestamos = new JTable();
		tablePrestamos.setForeground(Color.BLACK);
		tablePrestamos.setBackground(Color.WHITE);
		tablePrestamos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateMarcarDevuelto();
				tableLibros.clearSelection();
				updateEditar();
				updateEliminar();
			}
		});
		modeloPrestamos = new DefaultTableModel(new Object[][] {},
				new String[] { "Titulo", "Autor", "ISBN", "Usuario", "Fecha inicio", "Fecha fin", "Estado" }) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Make all cells non-editable
			}
		};
		tablePrestamos.setModel(modeloPrestamos);
		scrollPanePrestamos.setViewportView(tablePrestamos);
		tablePrestamos.getColumnModel().getColumn(2).setPreferredWidth(20);
		tablePrestamos.getColumnModel().getColumn(3).setPreferredWidth(0);
		tablePrestamos.getColumnModel().getColumn(4).setPreferredWidth(0);
		tablePrestamos.getColumnModel().getColumn(5).setPreferredWidth(0);
		tablePrestamos.getColumnModel().getColumn(6).setPreferredWidth(0);
		tablePrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// CAMPO USUARIO
		txtUsuario = new JTextField();
		txtUsuario.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updatePrestamo();
			}
		});
		txtUsuario.setBounds(555, 289, 109, 20);
		contentPane.add(txtUsuario);
		txtUsuario.setColumns(10);

		lblPrestamo = new JLabel("Usuario");
		lblPrestamo.setBounds(555, 264, 109, 14);
		contentPane.add(lblPrestamo);

		// DATE CHOOSER INICIO
		JDateChooser dateChooserInicio = new JDateChooser();
		dateChooserInicio.setToolTipText("Inicio del plazo de prestamo");
		dateChooserInicio.setBounds(555, 345, 109, 20);
		contentPane.add(dateChooserInicio);
		dateChooserInicio.setDate(new Date()); // pone la fecha actual por defecto

		// DATE CHOOSER FIN
		JDateChooser dateChooserFin = new JDateChooser();
		dateChooserFin.setToolTipText("Fin del plazo de prestamo");
		dateChooserFin.setBounds(555, 401, 109, 20);
		contentPane.add(dateChooserFin);

		// BOTON PRESTAMO
		btnPrestamo = new JButton("Prestamo");
		btnPrestamo.setToolTipText(
				"Registrar un préstamo del libro seleccionado al usuario introducido a continuacion, durante el plazo seleccionado");
		btnPrestamo.setEnabled(false);
		btnPrestamo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// obtiene strings de las fechas
				Date fechaInicioDate = dateChooserInicio.getDate();
				Date fechaFinDate = dateChooserFin.getDate();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String fechaInicio = dateFormat.format(fechaInicioDate);
				String fechaFin = dateFormat.format(fechaFinDate);

				String estado;
				if (isOverdue(fechaFinDate)) { // calcula el estado
					estado = "RETRASADO";
				} else {
					estado = "Por devolver";
				}

				int selectedRowInView = tableLibros.getSelectedRow();
				int row = tableLibros.convertRowIndexToModel(selectedRowInView);

				// añade prestamo a la tabla
				modeloPrestamos.addRow(new String[] { (String) modeloLibros.getValueAt(row, 0),
						(String) modeloLibros.getValueAt(row, 1), (String) modeloLibros.getValueAt(row, 2),
						txtUsuario.getText(), fechaInicio, fechaFin, estado });
				txtUsuario.setText("");
				txtBuscar.setText("");
				btnBuscar.doClick(); // resetea busqueda
				updatePrestamo();
				if (checkAutoguardado.isEnabled())
					guardarPrestamos();
				tableLibros.clearSelection();
			}
		});
		btnPrestamo.setBounds(555, 230, 109, 23);
		contentPane.add(btnPrestamo);

		lblFechaDeInicio = new JLabel("Fecha de inicio");
		lblFechaDeInicio.setBounds(555, 320, 109, 14);
		contentPane.add(lblFechaDeInicio);

		lblFechaDeFinalizacion = new JLabel("Fecha de fin");
		lblFechaDeFinalizacion.setBounds(555, 376, 132, 14);
		contentPane.add(lblFechaDeFinalizacion);

		// BOTON MARCAR COMO DEVUELTO
		btnDevuelto = new JButton("Marcar como devuelto");
		btnDevuelto.setToolTipText("Marcar el prestamo seleccionado como devuelto");
		btnDevuelto.setEnabled(false);
		btnDevuelto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tablePrestamos.getSelectedRow();
				modeloPrestamos.setValueAt("Devuelto", row, 6);
				if (checkAutoguardado.isEnabled())
					guardarPrestamos();
				tablePrestamos.clearSelection();
			}
		});
		btnDevuelto.setBounds(1448, 540, 164, 23);
		contentPane.add(btnDevuelto);

		cargar(); // carga los datos

		// establece la fecha del segundo datechooser a despues del periodo de prestamo
		// estandar tras un pequeño delay.
		// hacer esto inmediatamente no funcionaba, mostraba la fecha actual
		SwingUtilities.invokeLater(() -> {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, DURACION_ESTANDAR_PRESTAMO);
			Date futureDate = calendar.getTime();
			dateChooserFin.setDate(futureDate);
		});
	}

	// GUARDAR PRESTAMOS
	private void guardarPrestamos() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("prestamos.txt", false))) {
			for (int row = 0; row < modeloPrestamos.getRowCount(); row++) {
				for (int col = 0; col < modeloPrestamos.getColumnCount(); col++) {
					writer.write(modeloPrestamos.getValueAt(row, col).toString());
					writer.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// GUARDAR LIBROS
	private void guardarLibros() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("libros.txt", false))) {
			for (int row = 0; row < modeloLibros.getRowCount(); row++) {
				for (int col = 0; col < modeloLibros.getColumnCount(); col++) {
					writer.write(modeloLibros.getValueAt(row, col).toString());
					writer.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// CARGAR
	// borra las tabla y la rellena leyendo los archivos linea por linea
	private void cargar() {
		modeloLibros.setRowCount(0);
		try (BufferedReader reader = new BufferedReader(new FileReader("libros.txt"))) {
			String titulo, autor, isbn;
			while ((titulo = reader.readLine()) != null && (autor = reader.readLine()) != null
					&& (isbn = reader.readLine()) != null) {
				modeloLibros.addRow(new String[] { titulo, autor, isbn });
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		modeloPrestamos.setRowCount(0);
		try (BufferedReader reader = new BufferedReader(new FileReader("prestamos.txt"))) {
			String titulo, autor, isbn, usuario, inicio, fin, estado;
			while ((titulo = reader.readLine()) != null && (autor = reader.readLine()) != null
					&& (isbn = reader.readLine()) != null && (usuario = reader.readLine()) != null
					&& (inicio = reader.readLine()) != null && (fin = reader.readLine()) != null
					&& (estado = reader.readLine()) != null) {
				modeloPrestamos.addRow(new String[] { titulo, autor, isbn, usuario, inicio, fin, estado });
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateEstados();
	}

	// tras cargar, recalcula si algun prestamo se ha pasado de plazo
	private void updateEstados() {
		for (int row = 0; row < modeloPrestamos.getRowCount(); row++) {
			if (!modeloPrestamos.getValueAt(row, 6).equals("Devuelto")) {
				try {
					if (isOverdue(toDate((String) modeloPrestamos.getValueAt(row, 5)))) {
						tablePrestamos.setValueAt("RETRASADO", row, 6);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Vacía campos de texto de libros
	private void limpiarCampos() {
		txtTitulo.setText("");
		txtAutor.setText("");
		txtISBN.setText("");
		updateEditar();
		updateAnadir();
		updateEliminar();
	}

	// Metodos para que los botones esten deshabilitados cuando proceda
	private void updateAnadir() { // se habilita cuando los 3 campos estan llenos
		if (txtTitulo.getText().length() == 0 || txtAutor.getText().length() == 0 || txtISBN.getText().length() == 0) {
			btnAnadir.setEnabled(false);
		} else {
			btnAnadir.setEnabled(true);
		}
	}

	private void updateEliminar() { // se habilita cuando hay una fila seleccinada
		if (tableLibros.getSelectedRow() == -1) {
			btnEliminar.setEnabled(false);
		} else {
			btnEliminar.setEnabled(true);
		}
	}

	private void updatePrestamo() { // se habilita cuando hay una fila seleccinada y un usuario en el campo
		if (tableLibros.getSelectedRow() == -1 || txtUsuario.getText().length() == 0) {
			btnPrestamo.setEnabled(false);
		} else {
			btnPrestamo.setEnabled(true);
		}
	}

	private void updateEditar() { // se habilita cuando los campos estan llenos y hay una fila seleccionada
		if (txtTitulo.getText().length() == 0 || txtAutor.getText().length() == 0 || txtISBN.getText().length() == 0
				|| tableLibros.getSelectedRow() == -1) {
			btnEditar.setEnabled(false);
		} else {
			btnEditar.setEnabled(true);
		}
	}

	protected void updateMarcarDevuelto() { // se habilita cuando hay una fila seleccinada
		if (tablePrestamos.getSelectedRow() == -1) {
			btnDevuelto.setEnabled(false);
		} else {
			btnDevuelto.setEnabled(true);
		}
	}

	// Metodos para poder trabajar con fechas con mas facilidad
	private static boolean isOverdue(Date dateToCheck) {
		// Create a Calendar instance for today's date
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);

		// Create a Calendar instance for the date to check
		Calendar dueDate = Calendar.getInstance();
		dueDate.setTime(dateToCheck);
		dueDate.set(Calendar.HOUR_OF_DAY, 0);
		dueDate.set(Calendar.MINUTE, 0);
		dueDate.set(Calendar.SECOND, 0);
		dueDate.set(Calendar.MILLISECOND, 0);

		// Compare the two dates
		return dueDate.before(today);
	}

	private static Date toDate(String dateString) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.parse(dateString);
	}

}
