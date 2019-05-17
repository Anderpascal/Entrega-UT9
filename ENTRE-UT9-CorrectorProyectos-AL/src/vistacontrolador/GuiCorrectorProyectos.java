
package vistacontrolador;

import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.AlumnoNoExistenteExcepcion;
import modelo.CorrectorProyectos;
import modelo.Proyecto;

public class GuiCorrectorProyectos extends Application
{

	private MenuItem itemLeer;
	private MenuItem itemGuardar;
	private MenuItem itemSalir;

	private TextField txtAlumno;
	private Button btnVerProyecto;

	private RadioButton rbtAprobados;
	private RadioButton rbtOrdenados;
	private Button btnMostrar;

	private TextArea areaTexto;

	private Button btnClear;
	private Button btnSalir;

	private CorrectorProyectos corrector; // el modelo

	@Override
	public void start(Stage stage) {

		corrector = new CorrectorProyectos();

		BorderPane root = crearGui();

		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.setTitle("- Corrector de proyectos -");
		scene.getStylesheets().add(getClass()
		                .getResource("/css/application.css").toExternalForm());
		stage.show();
	}

	private BorderPane crearGui() {

		BorderPane panel = new BorderPane();
		
		panel.setTop(crearBarraMenu());

		panel.setCenter(crearPanelPrincipal());

		panel.setBottom(crearPanelBotones());

		return panel;
	}

	private MenuBar crearBarraMenu() {

		MenuBar barraMenu = new MenuBar();

		Menu menu = new Menu("Archivo");

		itemLeer = new MenuItem("_Leer de fichero");
		itemLeer.setAccelerator(KeyCombination.keyCombination("CTRL+L"));
		itemLeer.setOnAction(event -> leerDeFichero());
		itemGuardar = new MenuItem("_Guardar Fichero");
		itemGuardar.setAccelerator(KeyCombination.keyCombination("CTRL+G"));
		itemGuardar.setDisable(true);
		itemGuardar.setOnAction(event -> salvarEnFichero());
		itemSalir = new MenuItem("_Salir del Fichero");
		itemSalir.setAccelerator(KeyCombination.keyCombination("CTRL+S"));
		itemSalir.setOnAction(event -> salir());
		
		barraMenu.getMenus().add(menu);
		menu.getItems().addAll(itemGuardar, itemLeer, itemSalir);

		return barraMenu;
	}

	private VBox crearPanelPrincipal() {

		VBox panel = new VBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);
		

		Label lblEntrada = new Label("Panel de entrada");
		lblEntrada.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		lblEntrada.getStyleClass().add("titulo-panel");
		Label lblOpciones = new Label("Panel de Opciones");
		lblOpciones.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		lblOpciones.getStyleClass().add("titulo-panel");
		
		areaTexto = new TextArea();
		
		panel.getChildren().addAll(lblEntrada,  crearPanelEntrada(),lblOpciones, crearPanelOpciones(), areaTexto);

		return panel;
	}

	private HBox crearPanelEntrada() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);
		
		Label lblEntrada = new Label("Alumno");
		txtAlumno = new TextField();
		txtAlumno.setPrefColumnCount(30);
		btnVerProyecto = new Button("Ver Proyecto");
		btnVerProyecto.setPrefWidth(120);
		btnVerProyecto.setOnAction(event -> verProyecto());
		btnVerProyecto.setDefaultButton(true);

		panel.getChildren().addAll(lblEntrada, txtAlumno, btnVerProyecto);
		return panel;
	}

	private HBox crearPanelOpciones() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(50);
		panel.setAlignment(Pos.CENTER);
		
		rbtAprobados = new RadioButton("Mostrar aprobados");
		rbtAprobados.setSelected(true);
		rbtOrdenados = new RadioButton("Mostrar Ordenados");
		btnMostrar = new Button("Mostrar");
		btnMostrar.setOnAction(event -> mostrar());
		
		ToggleGroup grupo = new ToggleGroup();
		rbtAprobados.setToggleGroup(grupo);
		rbtOrdenados.setToggleGroup(grupo);

		panel.getChildren().addAll(rbtAprobados, rbtOrdenados, btnMostrar);

		return panel;
	}

	private HBox crearPanelBotones() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);
		panel.setAlignment(Pos.BOTTOM_RIGHT);
		
		btnClear = new Button("Clear");
		btnClear.setPrefWidth(90);
		btnClear.setOnAction(event -> clear());

		btnSalir = new Button("Salir");
		btnSalir.setPrefWidth(90);
		btnSalir.setOnAction(event -> salir());

		panel.getChildren().addAll(btnClear, btnSalir);
		return panel;
	}

	private void salvarEnFichero() {
		
		try {
			corrector.guardarOrdenadosPorNota();
		}
		catch(IOException e) {
			areaTexto.setText("Error de IO");
		}
	}

	private void leerDeFichero() {
		
		corrector.leerDatosProyectos();
		areaTexto.setText(corrector.toString());
		itemGuardar.setDisable(false);
		itemLeer.setDisable(true);
	}

	private void verProyecto() {
		
		if(itemGuardar.isDisable()) {
			areaTexto.setText("No se han leido los datos aún.");
		}
		else {
			if(txtAlumno.getText().isEmpty()) {
				areaTexto.setText("Teclea un alumno.");
				
			}
			else {
				try {
					Proyecto aux = corrector.proyectoDe(txtAlumno.getText());
					areaTexto.setText(aux.toString());
				}
				catch(AlumnoNoExistenteExcepcion e) {
					areaTexto.setText("Alumno no existente.");
				}
			}
		}
		cogerFoco();
	}

	private void mostrar() {
		clear();
		if(itemGuardar.isDisable()) {
			areaTexto.setText("No se han leido los datos aún.");
		}
		else {
			if(rbtAprobados.isSelected()) {
				areaTexto.setText("Número de proyectos aprobados: " + String.valueOf(corrector.aprobados()));
			}
			else {
				areaTexto.setText(corrector.ordenadosPorNota().toString());	
			}	
		}
		cogerFoco();
	}

	private void cogerFoco() {

		txtAlumno.requestFocus();
		txtAlumno.selectAll();

	}

	private void salir() {

		System.exit(0);
	}

	private void clear() {

		areaTexto.clear();
		cogerFoco();
	}

	public static void main(String[] args) {

		launch(args);
	}
}
