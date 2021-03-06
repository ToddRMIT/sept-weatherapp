
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author Daniel Bugeja & Todd Ryan
 *
 */
public class GuiListWindow {

	
	private static final String SITES_FILE = "sites.txt";
	public static final String FAVOURITES_FILE = "favourites.txt";
	private static final String GUI_LIST_PREFS_FILE = "guilistprefs.txt";
	
	static TableView<Station> table;
	static Stage subStage; 
	
	
	static void GuiWindow(Stage primaryStage, ObservableList sites ) throws IOException{

		subStage = new Stage();
		subStage.setTitle( "Site List" );
		subStage.initModality(Modality.WINDOW_MODAL);
		
		// Load prefs
		String prefs[] = loadPrefs();
		if( prefs != null ){
			subStage.setX(Double.parseDouble(prefs[0]));
			subStage.setY(Double.parseDouble(prefs[1]));
		}else{
			// Sane defaults
			subStage.setX(100);
			subStage.setY(100);
		}
		
		//Name column
		TableColumn<Station, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setCellFactory(new Callback<TableColumn<Station,String>,TableCell<Station, String>>() {
			
			public TableCell<Station,String> call(TableColumn<Station,String> t){
				  TableCell cell = new TableCell<Station, String>() {
					  
					     @Override
		                    public void updateItem(String item, boolean empty) {
		                        super.updateItem(item, empty);
		                        setText(empty ? null : getString());
		                        setGraphic(null);
		                    }

		                    private String getString() {
		                        return getItem() == null ? "" : getItem().toString();
		                    }
		                
				  };
				  
				  cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	                    @Override
	                    public void handle(MouseEvent event) {
	                        if (event.getClickCount() > 1) {
	                            TableCell c = (TableCell) event.getSource();
	                            for(int i = 0; i < sites.size(); i++){
	                            	if(c.getText().equals(((Station) sites.get(i)).getName())){
	                            		try {
											GuiDataWindow.dataWindow( primaryStage, (Station) sites.get(i) );
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
	                            	}
	                            }
	                            // System.out.println("Cell text: " + c.getText());
	                        }
	                    }
	                });
	                return cell;
			}
			
		});
			
		/*
		//URL column
		TableColumn<Station, String> urlColumn = new TableColumn<>("URL");
		urlColumn.setCellValueFactory(new PropertyValueFactory<>("URL"));
		*/
		
		//Favorite column
		TableColumn<Station, Boolean> favoriteColumn = new TableColumn<>("Favourite");
		favoriteColumn.setCellValueFactory(new PropertyValueFactory<>("fav"));
		favoriteColumn.setCellFactory(new Callback<TableColumn<Station, Boolean>, TableCell<Station, Boolean>>() {
			public TableCell<Station, Boolean> call(TableColumn<Station, Boolean> p) {
		    return new CheckBoxTableCell<Station, Boolean>();
		}});
		
		table = new TableView<>();
		//table.setMinSize(640, 480);

		
		FilteredList<Station> filteredSites = new FilteredList<>(sites,p-> true);
		TextField searchText = new TextField("search");
		searchText.setMaxSize(140, 20);
		searchText.textProperty().addListener((observable, oldValue,newValue) -> {
			filteredSites.setPredicate(site -> {
				if (newValue == null || newValue.isEmpty()){
					return true;
				}
				String lowerCase = newValue.toLowerCase();
				if(site.getName().toLowerCase().contains(lowerCase)){
					return true;
				}
				return false;
			});		
		});

		SortedList<Station> sortedSites = new SortedList<>(filteredSites);
		sortedSites.comparatorProperty().bind(table.comparatorProperty());
		
		
		//Append columns to table and fill in data
		table.setItems(sortedSites);
		table.getColumns().addAll(nameColumn, favoriteColumn);
				
		//Setting layout
		VBox layout = new VBox(10);
		layout.getChildren().addAll(searchText, table);
		layout.setPadding(new Insets (15,0,0,0));
		layout.setStyle("-fx-background-color: #336699;");        
		
	    Scene scene = new Scene(layout);
	    subStage.setScene(scene);
	    

	    subStage.setOnCloseRequest(e -> closeWindow());
	    subStage.show();
	}

	
	
	private static  void closeWindow () {
		GuiHandler.listClosed();
		savePrefs( subStage.getX(), subStage.getY() );
		subStage.close();
	}
	
	
	
	public static String[] loadPrefs(){
		String tokens[] = null;
		try{
			FileReader file = new FileReader( GUI_LIST_PREFS_FILE );
			BufferedReader reader = new BufferedReader( file );
			tokens = reader.readLine().split(",");
			reader.close();
		}
		catch (IOException e){
			System.err.println( "Error loading prefs: " + e );
		}
		return tokens;
	}

	
	
	public static boolean savePrefs( Double x, Double y ){
		try{
			FileWriter file = new FileWriter( GUI_LIST_PREFS_FILE );
			PrintWriter out = new PrintWriter( file );
			out.println( Double.toString(x) + "," + Double.toString(y) );
			if( out != null ) out.close();
		}
		catch( IOException e ){
			System.err.println( "Error saving prefs: " + e );
			return false;
		}
		return true;
	}
	
	

	
	public static class CheckBoxTableCell<S, T> extends TableCell<S, T> {
		private CheckBox checkBox;
		private ObservableValue<T> ov;
		public CheckBoxTableCell() {
			this.checkBox = new CheckBox();
			this.checkBox.setAlignment(Pos.CENTER);
			setAlignment(Pos.CENTER);
			setGraphic(checkBox);
		}
		
		@Override 
		public void updateItem(T item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				setGraphic(checkBox);
				if (ov instanceof BooleanProperty) {
					checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);
				}
				ov = getTableColumn().getCellObservableValue(getIndex());
				if (ov instanceof BooleanProperty) {
					checkBox.selectedProperty().bindBidirectional((BooleanProperty) ov);
				}
			}
		}
	}
}

