package tv.hd3g.f2utility;

import java.util.Arrays;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Table for all the files and folders
 * @author Jelmerro
 */
public class FileList extends TableView<File> {

	private static FileList fileList;

	/**
	 * Returns the only allowed instance for the list of files
	 * @return fileList FileList
	 */
	public static FileList getInstance() {
		if (fileList == null) {
			// FileList
			fileList = new FileList();
			fileList.setEditable(true);
			fileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			fileList.setTableMenuButtonVisible(true);
			// Startup dialog using placeholder
			final VBox placeholder = new VBox(50);
			placeholder.setAlignment(Pos.CENTER);
			final Text welcomeLabel = new Text("Drag and drop your files or folder here to start\nOr browse to them to get going");
			welcomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 32;");
			welcomeLabel.setStrokeType(StrokeType.INSIDE);
			welcomeLabel.setTextAlignment(TextAlignment.CENTER);
			welcomeLabel.setFill(Color.web("#bbb"));
			placeholder.getChildren().add(welcomeLabel);
			// Buttons for file and folder choosers inside the placeholder
			final HBox buttonBox = new HBox(10);
			buttonBox.setAlignment(Pos.CENTER);
			// Files button
			final Button fileChooserButton = new Button("Files");
			fileChooserButton.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
			fileChooserButton.setTextFill(Color.web("#444"));
			fileChooserButton.setOnAction(e -> {
				fileList.showFileChooser();
			});
			buttonBox.getChildren().add(fileChooserButton);
			// Folder button
			final Button dirChooserButton = new Button("Folder");
			dirChooserButton.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
			dirChooserButton.setTextFill(Color.web("#444"));
			dirChooserButton.setOnAction(e -> {
				fileList.showDirChooser();
			});
			buttonBox.getChildren().add(dirChooserButton);
			// Add placeholder to list
			placeholder.getChildren().add(buttonBox);
			fileList.setPlaceholder(placeholder);
			fileList.setStyle("-fx-background-color: white");
			// Columns
			// Current name
			final TableColumn<File, String> currentName = new TableColumn<>("Current name");
			currentName.setMinWidth(50);
			currentName.setPrefWidth(200);
			currentName.setCellValueFactory(new PropertyValueFactory<>("name"));
			// New name
			final TableColumn<File, String> newName = new TableColumn<>("New name");
			newName.setMinWidth(50);
			newName.setPrefWidth(200);
			newName.setCellValueFactory(new PropertyValueFactory<>("newName"));
			// Path
			final TableColumn<File, String> parent = new TableColumn<>("Path");
			parent.setMinWidth(50);
			parent.setPrefWidth(300);
			parent.setCellValueFactory(new PropertyValueFactory<>("parent"));
			// Extension
			final TableColumn<File, String> ext = new TableColumn<>("Extension");
			ext.setMinWidth(50);
			ext.setPrefWidth(100);
			ext.setCellValueFactory(new PropertyValueFactory<>("ext"));
			// Size
			final TableColumn<File, String> size = new TableColumn<>("Size");
			size.setMinWidth(50);
			size.setPrefWidth(100);
			size.setCellValueFactory(new PropertyValueFactory<>("size"));
			// Last modified
			final TableColumn<File, String> lastModified = new TableColumn<>("Modified");
			lastModified.setMinWidth(50);
			lastModified.setPrefWidth(300);
			lastModified.setVisible(false);
			lastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
			// Custom style (green and bold) for changed names
			newName.setCellFactory(column -> new TableCell<>() {

				@Override
				protected void updateItem(final String t, final boolean bln) {
					super.updateItem(t, bln);
					// Try to change the color if the file name was changed
					// Exceptions can happen when an item is still loading
					// These will simply be ignored and updated later
					try {
						final int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
						final File file = column.getTableView().getItems().get(currentIndex);
						if (!file.getName().equals(file.getNewName())) {
							setTextFill(Color.GREEN);
							setStyle("-fx-font-weight: bold;");
						} else {
							setTextFill(Color.BLACK);
							setStyle("");
						}
						setText(t.toString());
					} catch (final Exception e) {
						setText("");
					}
				}

			});
			// Add all columns
			fileList.getColumns().add(currentName);
			fileList.getColumns().add(newName);
			fileList.getColumns().add(parent);
			fileList.getColumns().add(ext);
			fileList.getColumns().add(size);
			fileList.getColumns().add(lastModified);

			// Drag and drop
			// Detection of drag over the frame
			fileList.setOnDragOver(e -> {
				// Start accepting copy transfers
				e.acceptTransferModes(TransferMode.COPY);
				e.consume();
			});
			// Move inside of the frame with drag
			fileList.setOnDragEntered(e -> {
				// If the list is empty, color the background light blue
				if (fileList.getItems().isEmpty()) {
					fileList.setStyle("-fx-background-color: #ddfbff");
				}
				e.consume();
			});
			// Cancel drag or move out of the frame
			fileList.setOnDragExited(e -> {
				// Reset the background color to white
				fileList.setStyle("-fx-background-color: white");
				e.consume();
			});
			// Items drag and dropped
			fileList.setOnDragDropped(e -> {
				// List the files from the dragboard
				final Dragboard db = e.getDragboard();
				List<java.io.File> files = db.getFiles();
				if (files.size() == 1 && files.get(0).isDirectory()) {
					files = Arrays.asList(files.get(0).listFiles());
				}
				fileList.addFiles(files);
				e.consume();
			});
		}
		return fileList;
	}

	/**
	 * Configures and shows the folder chooser and adds the folder contents to the list afterwards
	 */
	public void showDirChooser() {
		final DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Select folder");
		// Try to list the files from the folder and add them
		try {
			fileList.addFiles(Arrays.asList(dirChooser.showDialog(F2Utility.stage).listFiles()));
		} catch (final NullPointerException ex) {

		}
		ToolBox.getInstance().updateNewNames();
	}

	/**
	 * Configures and shows the file chooser and adds the files to the list afterwards
	 */
	public void showFileChooser() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select files");
		// Try to add all selected files
		try {
			fileList.addFiles(fileChooser.showOpenMultipleDialog(F2Utility.stage));
		} catch (final NullPointerException ex) {

		}
		ToolBox.getInstance().updateNewNames();
	}

	/**
	 * Adds the provided files to the fileList, includes duplicate and null checks
	 * @param files List<java.io.File>
	 */
	public void addFiles(final List<java.io.File> files) {
		if (files != null) {
			for (final java.io.File file : files) {
				boolean exists = false;
				for (final File listedFile : fileList.getItems()) {
					if (file.getAbsolutePath().equals(listedFile.getAbsolutePath())) {
						exists = true;
					}
				}
				if (!exists) {
					fileList.getItems().add(new File(file.getAbsolutePath()));
				}
			}
		}
		ToolBox.getInstance().updateNewNames();
	}

	/**
	 * Removes all the currently selected files from the list and clears the selection
	 */
	public void removeSelectedFiles() {
		fileList.getItems().removeAll(fileList.getSelectionModel().getSelectedItems());
		fileList.getSelectionModel().clearSelection();
		ToolBox.getInstance().updateNewNames();
	}

	private FileList() {
		super();
	}
}
