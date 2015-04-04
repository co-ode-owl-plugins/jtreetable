package uk.ac.manchester.cs.bhig.jtreetable.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
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

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.junit.Ignore;

import uk.ac.manchester.cs.bhig.jtreetable.AbstractTreeTableModel;
import uk.ac.manchester.cs.bhig.jtreetable.JTreeTable;
import uk.ac.manchester.cs.bhig.jtreetable.TreeTableModel;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 5, 2008<br><br>
 */
@Ignore
public class TestJTreeTable {

    private int column_index = 0;


    public static void main(String[] args) {
        new TestJTreeTable().testJTreeTable();
    }

    private TreeModel createTestTreeModel() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("A");
        DefaultMutableTreeNode current = root;
        for (int i=0; i<26; i++){
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("A" + i);
            current.add(node);
            current = node;
        }
        return new DefaultTreeModel(root);
    }

    public void testJTreeTable(){
        JTree tree = new JTree(createTestTreeModel());
        tree.setRowHeight(-1);
        final TreeTableModel model = new AbstractTreeTableModel(tree){

            public String[] values;

            private void setupValues() {
                if (values == null){
                    values = new String[100]; // dirty hack
                }
            }


            public void setValueAt(Object value, Object node, int column) {
                //To change body of implemented methods use File | Settings | File Templates.
            }


            public Object getValueAt(Object node, int column) {
                final Object o = getColumnObjectAtModelIndex(column);
                return node + ": " + o + " pos " + getModelIndexOfColumn(o);
            }
        };


        final JTreeTable treeTable = new JTreeTable(tree, model);

        treeTable.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        treeTable.setFont(tree.getFont().deriveFont(24.0f));
        
        JComponent panel = new JPanel(new BorderLayout(6, 6));
        JToolBar toolbar = new JToolBar();
        toolbar.add(new AbstractAction("+"){
            public void actionPerformed(ActionEvent event) {
                treeTable.getModel().addColumn("col" + column_index++);

            }
        });
        toolbar.add(new AbstractAction("-"){
            public void actionPerformed(ActionEvent event) {
                int col = treeTable.getTable().getSelectedColumn();
                if (col >= 0){
                    int modelIndex = treeTable.getTable().convertColumnIndexToModel(col);
                    Object colObj = treeTable.getModel().getColumnObjectAtModelIndex(modelIndex);
                    treeTable.getModel().removeColumn(colObj);
                }
            }
        });
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(treeTable, BorderLayout.CENTER);


        showConfirmDialog(null, "Test JTreeTable", panel, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        System.exit(0);
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
