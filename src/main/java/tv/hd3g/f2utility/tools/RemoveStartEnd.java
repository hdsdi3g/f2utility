package tv.hd3g.f2utility.tools;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import tv.hd3g.f2utility.ToolBox;

/**
 * Cut the first and/or last bunch of characters
 * @author Jelmerro
 */
public class RemoveStartEnd extends VBox implements Tool {

	private final Label label;
	private final TextField start;
	private final TextField end;

	/**
	 * Constructor for the RemoveStartEnd Tool
	 */
	public RemoveStartEnd() {
		super(5);
		Deactivate();
		// Label
		label = new Label("Remove start/end");
		setMargin(label, new Insets(5, 5, 0, 5));
		getChildren().add(label);
		// Start field
		start = new TextField();
		setMargin(start, new Insets(0, 5, 0, 5));
		start.setPromptText("Start");
		start.setOnKeyReleased(t -> {
			ToolBox.getInstance().updateNewNames();
			checkActive();
		});
		start.setMinWidth(100);
		start.setMaxWidth(100);
		getChildren().add(start);
		// End field
		end = new TextField();
		setMargin(end, new Insets(0, 5, 5, 5));
		end.setPromptText("End");
		end.setOnKeyReleased(t -> {
			ToolBox.getInstance().updateNewNames();
			checkActive();
		});
		end.setMinWidth(100);
		end.setMaxWidth(100);
		getChildren().add(end);
	}

	@Override
	public String processName(String name) {
		// Removes the first number of characters if start is a valid integer
		try {
			final int startNum = Integer.parseInt(start.getText());
			if (startNum > 0) {
				name = name.substring(Integer.parseInt(start.getText()));
			}
		} catch (final Exception ex) {
		}
		// Removes the last number of characters if end is a valid integer
		try {
			final int startNum = Integer.parseInt(end.getText());
			if (startNum > 0) {
				name = name.substring(0, name.length() - Integer.parseInt(end.getText()));
			}
		} catch (final Exception ex2) {
		}
		return name;
	}

	@Override
	public void checkActive() {
		// If either of them is a valid integer above 0, activate
		// Else deactivate
		try {
			final int startNum = Integer.parseInt(start.getText());
			if (startNum > 0) {
				Activate();
			} else {
				Deactivate();
			}
		} catch (final Exception ex) {
			try {
				final int endNum = Integer.parseInt(end.getText());
				if (endNum > 0) {
					Activate();
				} else {
					Deactivate();
				}
			} catch (final Exception ex2) {
				Deactivate();
			}
		}
	}

	@Override
	public void Activate() {
		setStyle(ACTIVATED);
	}

	@Override
	public void Deactivate() {
		setStyle(DEACTIVATED);
	}

	@Override
	public void Reset() {
		start.setText("");
		end.setText("");
		checkActive();
	}
}
