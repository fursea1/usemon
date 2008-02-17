/*
 *  Copyright (C) 2003  Jens Kanschik,
 * 	mail : jensKanschik@users.sourceforge.net
 *
 *  Part of <hypergraph>, an open source project at sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package hypergraph.visualnet;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;


public class SearchDialog extends JDialog implements ListSelectionListener, KeyListener {

    Object[] dataBase;

    JPanel inputPanel;
    JTextField textField;
    JList list;
    SearchListModel listModel;

    boolean searching = false;

    public SearchDialog(Object[] dataBase) {
	super();
	this.dataBase = dataBase;

	setModal(true);

	addKeyListener(this);

	inputPanel = new JPanel();
	inputPanel.setLayout(new BorderLayout());
	inputPanel.add(new JLabel("Text : "),"West");
	textField = new JTextField(30);
	textField.addKeyListener( this );
	textField.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) { setVisible(false); } // called when user presses ENTER; close dialog
	    } );
	inputPanel.add(textField, "Center");

	list = new JList(dataBase);
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	list.addListSelectionListener( this);

	JButton okButton = new JButton("OK");
	okButton.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) { setVisible(false); }
	    } );
	JButton cancelButton = new JButton("Cancel");
	cancelButton.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) { list.clearSelection(); setVisible(false); }
	    } );
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new FlowLayout());
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);


	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(inputPanel,"North");
	getContentPane().add(new JScrollPane(list),"Center");
	getContentPane().add(buttonPanel,"South");

	pack();
    }
    public void keyReleased(KeyEvent e) { 

	if ((e.getSource() == textField) && !e.isActionKey() ) {
	    // call searchText to get the selection in the list right. 
	    // ensure that the textfield is not set to the list item
	    searching = true;
	    searchText( textField.getText() );
	    searching=false;
	}
 	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	     list.clearSelection(); 
	     setVisible(false); // cancel
	}
    }
    public void keyPressed(KeyEvent e) { 
    }
    public void keyTyped(KeyEvent e) { 
    }
    // is called when the selected item in the list changes
    public void valueChanged(ListSelectionEvent e) {
	if (!searching) {
	    int i = list.getSelectedIndex();
	    if (i>0)
		textField.setText( (String) dataBase[ list.getSelectedIndex() ] );
	}
    }
    // returns the selected entry, should be called when the dialog is closed to get the result.
    public Object getSelectedData() {
	int index = list.getSelectedIndex();
	if ((index < 0) || (index > dataBase.length))
	    return null;
	return dataBase[list.getSelectedIndex()];
    }
    // searchEntry searches the parameter text in the list and sets the selection on this text 
    // or on the entry that would come immediately above text if text is not found
    void searchText(String text) {
	int i = list.getSelectedIndex();
	if (i < 0) 
	    i=0; // nothing selected yet.
	int comp = text.compareTo( (String) dataBase[i] );
	boolean forward = (comp > 0); // comp > 0 means that text comes after selected item, so search forward

	while ((i >= 0) && (i < dataBase.length)) {
	    comp = text.compareTo( (String) dataBase[i] );
	    if (comp == 0) 
		break; // we reached the searched text
	    if (forward && (comp > 0))
		i++; // go on searching;
	    if (forward && (comp < 0)) 
		break; // we couldn't find the text and we are at the place where it should be, so return this index
	    if (!forward && (comp > 0)) {
		i++;
		break; // we couldn't find the text and we are above the place where it should be, so return this index +1
	    }
	    if (!forward && (comp < 0))
		i--; // go on searching
	}
	if (i<0)
	    i = 0;
	if (i == dataBase.length)
	    i--;
	list.setSelectedIndex(i);
    }



}
