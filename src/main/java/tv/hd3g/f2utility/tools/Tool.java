package tv.hd3g.f2utility.tools;

/**
 * Interface Tool stores the style for the border and defines all the needed methods
 * @author Jelmerro
 */
public interface Tool {

	String ACTIVATED = "-fx-border-color: #ADA;-fx-border-width: 2px;-fx-border-radius: 3px;";
	String DEACTIVATED = "-fx-border-color: #bbb;-fx-border-width: 2px;-fx-border-radius: 3px;";

	/**
	 * Renames the provided string using the tool it's capabilities
	 * @param name String
	 * @return newName String
	 */
	String processName(String name);

	/**
	 * Similar to the process of renaming, but changes the border accordingly
	 */
	void checkActive();

	/**
	 * Activates the border/Colors the border green-ish
	 */
	void Activate();

	/**
	 * Deactivates the border/Colors the border gray-ish
	 */
	void Deactivate();

	/**
	 * Resets all the fields for the Tool and calls checkActive to style the border appropriately
	 */
	void Reset();
}
