package uk.ac.manchester.cs.bhig.jtreetable.test;

import uk.ac.manchester.cs.bhig.jtreetable.JTreeTable;
import uk.ac.manchester.cs.bhig.jtreetable.ListToTreeSelectionModelWrapper;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 18, 2008<br><br>
 */
public class TestJTreeTableSelectionModel {

    public static void main(String[] args) {
        JTree tree = new JTree(createTestTreeModel());
        tree.setSelectionModel(new ListToTreeSelectionModelWrapper(tree));

        showConfirmDialog(null, "Test JTreeTableSelectionModel", new JScrollPane(tree), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        System.exit(0);
    }


    private static TreeModel createTestTreeModel() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("A");
        DefaultMutableTreeNode current = root;
        for (int i=0; i<26; i++){
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("A" + i);
            current.add(node);
            current = node;
        }
        return new DefaultTreeModel(root);
    }


    public static void showConfirmDialog(JComponent parent, String title, JComponent content, int messageType,
                                         int optionType) {

        JOptionPane optionPane = new JOptionPane(content, messageType, optionType);
        JDialog dlg = optionPane.createDialog(parent, title);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setVisible(true);
    }
}
