/**
 * 
 */
package org.usemon.gui.client.utils;

import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author t514257
 *
 */
public class WidgetUtils {

	
	public static TextBox getNumericTextbox() {
		TextBox numericTextBox = new TextBox();
		
		numericTextBox.addKeyboardListener(new KeyboardListenerAdapter() {
		      public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		        if ((!Character.isDigit(keyCode)) && (keyCode != (char) KEY_TAB)
		            && (keyCode != (char) KEY_BACKSPACE)
		            && (keyCode != (char) KEY_DELETE) && (keyCode != (char) KEY_ENTER) 
		            && (keyCode != (char) KEY_HOME) && (keyCode != (char) KEY_END)
		            && (keyCode != (char) KEY_LEFT) && (keyCode != (char) KEY_UP)
		            && (keyCode != (char) KEY_RIGHT) && (keyCode != (char) KEY_DOWN)) {
		          // TextBox.cancelKey() suppresses the currentWizardStepPanel keyboard event.
		          ((TextBox)sender).cancelKey();
		        }
		      }
		    });
		
		return numericTextBox;
	}
}
