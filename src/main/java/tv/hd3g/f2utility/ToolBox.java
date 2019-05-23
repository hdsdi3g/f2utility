package tv.hd3g.f2utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tv.hd3g.f2utility.tools.Add;
import tv.hd3g.f2utility.tools.Misc;
import tv.hd3g.f2utility.tools.Numbering;
import tv.hd3g.f2utility.tools.Regex;
import tv.hd3g.f2utility.tools.RemoveRange;
import tv.hd3g.f2utility.tools.RemoveStartEnd;
import tv.hd3g.f2utility.tools.Tool;

/**
 * Horizontal Box containing all the tools for renaming
 * @author Jelmerro
 */
public class ToolBox extends HBox {

	private static ToolBox toolBox;
	private static ArrayList<Node> tools;
	private static Numbering numberingTool;

	/**
	 * Returns the only allowed instance of the ToolBox
	 * @return toolBox ToolBox
	 */
	public static ToolBox getInstance() {
		if (toolBox == null) {
			// ToolBox
			toolBox = new ToolBox(5);
			toolBox.setMinHeight(100);
			toolBox.setMaxHeight(100);
			toolBox.setBackground(new Background(new BackgroundFill(Color.web("#EEE"), CornerRadii.EMPTY, Insets.EMPTY)));
			toolBox.setPadding(new Insets(5));
			// Tools
			tools = new ArrayList<>();
			tools.add(new Regex());
			tools.add(new RemoveRange());
			tools.add(new RemoveStartEnd());
			tools.add(new Add());
			numberingTool = new Numbering();
			tools.add(numberingTool);
			tools.add(new Misc());
			// ButtonBox
			final VBox buttonBox = new VBox(6);
			// Rename button
			final Button renameButton = new Button("Rename");
			renameButton.setMinSize(90, 42);
			renameButton.setOnAction(e -> {
				// Results of the rename process are checked here
				final HashMap<File, Boolean> results = getInstance().rename();
				// And a list is made for all the items that failed
				String failedItems = "";
				for (final Entry<File, Boolean> entry : results.entrySet()) {
					if (!entry.getValue()) {
						final File file = entry.getKey();
						final String ext = file.getExt();
						if (ext.equals("-")) {
							failedItems += file.getParent() + java.io.File.separator + file.getNewName() + "\n";
						} else {
							failedItems += file.getParent() + java.io.File.separator + file.getNewName() + "." + ext + "\n";
						}
					}
				}
				// No failed items means the renaming was a success
				// Otherwise a the list of failed items is shown
				// Only show a message if there were files in the list
				if (results.size() > 0) {
					if (failedItems.isEmpty()) {
						final Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setTitle("Rename success");
						alert.setHeaderText("Succesfully renamed all files");
						alert.setContentText(results.size() + " files and folders were renamed with success");
						alert.showAndWait();
					} else {
						final Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Rename error");
						alert.setHeaderText("Some files were not renamed correctly");
						final TextArea list = new TextArea(failedItems);
						alert.getDialogPane().setContent(list);
						alert.showAndWait();
					}
				}
			});
			buttonBox.getChildren().add(renameButton);
			// Reset button
			final Button resetButton = new Button("Reset");
			resetButton.setMinSize(90, 42);
			resetButton.setOnAction(e -> {
				for (final Node n : tools) {
					try {
						final Tool tool = (Tool) n;
						tool.Reset();
					} catch (final ClassCastException ex) {
					}
				}
			});
			buttonBox.getChildren().add(resetButton);
			tools.add(buttonBox);
			// Add all the tools
			for (final Node tool : tools) {
				toolBox.getChildren().add(tool);
			}
		}
		return toolBox;
	}

	/**
	 * Renames all the items and returns the list with results, skips unchanged ones
	 * @return results HashMap<File, Boolean>
	 */
	public HashMap<File, Boolean> rename() {
		final HashMap<File, Boolean> map = new HashMap<>();
		for (final File file : FileList.getInstance().getItems()) {
			if (!file.getName().equals(file.getNewName())) {
				final boolean success = file.rename();
				if (success) {
					final String ext = file.getExt();
					if (ext.equals("-")) {
						FileList.getInstance().getItems().set(FileList.getInstance().getItems().indexOf(file), new File(file.getParent() + java.io.File.separator + file.getNewName()));
					} else {
						FileList.getInstance().getItems().set(FileList.getInstance().getItems().indexOf(file), new File(file.getParent() + java.io.File.separator + file.getNewName() + "." + ext));
					}
				}
				map.put(file, success);
			}
		}
		return map;
	}

	/**
	 * Loops over the tools and updates the list with the new name
	 */
	public void updateNewNames() {
		numberingTool.resetCount();
		for (final File file : FileList.getInstance().getItems()) {
			String name = file.getName();
			for (final Node n : tools) {
				try {
					final Tool tool = (Tool) n;
					name = tool.processName(name);
				} catch (final ClassCastException ex) {

				}
			}
			file.setNewName(name);
		}
		FileList.getInstance().getColumns().get(0).setVisible(false);
		FileList.getInstance().getColumns().get(0).setVisible(true);
	}

	private ToolBox(final double d) {
		super(d);
	}
}
