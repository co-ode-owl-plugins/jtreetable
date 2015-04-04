package uk.ac.manchester.cs.bhig.jtreetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
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
 * Date: Sep 5, 2008<br><br>
 */
public class JTreeTable<R> extends JComponent {

    private final JTree tree;

    private JTable table;

    private String treeTitle = "tree";

    private JScrollPane treeScrollPane;
    private JScrollPane tableScrollPane;

    private TreeTableModel<R> model;

    private CellEditorFactory editorFactory = null;

    public JTreeTable(JTree tree, TreeTableModel<R> model) {
        this.tree = tree;

        this.model = model;

        this.table = new VariableHeightCellsTable(model);

        table.setAutoCreateColumnsFromModel(false); // otherwise widths/order etc are lost
        table.setFont(tree.getFont());

        // share a selection model
        ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper(tree);
        tree.setSelectionModel(selectionWrapper);
        table.setSelectionModel(selectionWrapper.getListSelectionModel());
        selectionWrapper.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent event) {
                JTreeTable.this.tree.scrollPathToVisible(event.getPath());
                syncScrollers(treeScrollPane, tableScrollPane);
            }
        });

        setLayout(new BorderLayout(6, 6));

        JComponent treeComponent = createTreeComponent();

        tableScrollPane = new JScrollPane(table);
        tableScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // sync the 2 scrollpanes in the vertical axis
        tableScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            public void adjustmentValueChanged(AdjustmentEvent event) {
                syncScrollers(tableScrollPane, treeScrollPane);
            }
        });
        treeScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            public void adjustmentValueChanged(AdjustmentEvent event) {
                syncScrollers(treeScrollPane, tableScrollPane);
            }
        });

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                             treeComponent,
                                             tableScrollPane);
        splitter.setResizeWeight(0.3);
        splitter.setBackground(Color.WHITE);
        splitter.setOneTouchExpandable(true);
        add(splitter, BorderLayout.CENTER);

        setVisible(true);
    }


    private void syncScrollers(JScrollPane source, JScrollPane target) {
        int y = source.getViewport().getViewPosition().y;
        target.getViewport().setViewPosition(new Point(target.getViewport().getViewPosition().x, y));
    }


    public void setFont(Font font) {
        super.setFont(font);
        tree.setFont(font);
        table.setFont(font);
    }


    public void setSelectionMode(int selMode){
        tree.getSelectionModel().setSelectionMode(selMode);
        // as they share a model, this should work for both
    }


    public void setCellEditorFactory(CellEditorFactory fac){
        this.editorFactory = fac;
    }


    public JTree getTree(){
        return tree;
    }


    public TreeTableModel<R> getModel() {
        return model;
    }


    public JTable getTable() {
        return table;
    }


    private JComponent createTreeComponent() {

        final DefaultTableColumnModel dummyColumnModel = new DefaultTableColumnModel();
        final TableColumn tc = new TableColumn();
        tc.setHeaderValue(getTreeTitle());
        dummyColumnModel.addColumn(tc);

        // hack creates table that is never used to ensure the header UI is consistent
        final JTableHeader treeHeader = new JTable().getTableHeader();
        treeHeader.setColumnModel(dummyColumnModel);

        // keep width of column 100%
        treeHeader.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent event) {
                tc.setWidth(event.getComponent().getSize().width);
            }
        });

        

        tree.setScrollsOnExpand(true);
        treeScrollPane = new JScrollPane(tree);
        Border scrollPaneBorder = treeScrollPane.getBorder(); // we'll use this outside
        treeScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
//        treeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        treeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JComponent treeHolder = new JPanel(new BorderLayout());
        treeHolder.setBorder(scrollPaneBorder); // reused the border
        treeHolder.add(treeHeader, BorderLayout.NORTH);
        treeHolder.add(treeScrollPane, BorderLayout.CENTER);
        return treeHolder;
    }


    protected String getTreeTitle() {
        return treeTitle;
    }


    // table that ensures row heights are always the same as the tree row heights
    // could be optimised if we know the cell heights are always the same
    class VariableHeightCellsTable extends JTable {

        VariableHeightCellsTable(TreeTableModel tableModel) {
            super(tableModel, tableModel.getColumnModel());
        }

        public int getRowHeight(int row) {
            final int treeRowHeight = tree.getRowHeight();
            if (treeRowHeight < 0){ // then tree gets its height from the renderer
                final Object node = tree.getPathForRow(row).getLastPathComponent();
                Component c;
                c = tree.getCellRenderer().getTreeCellRendererComponent(tree,
                                                                        node,
                                                                        Arrays.asList(tree.getSelectionRows()).contains(row),
                                                                        tree.isExpanded(row),
                                                                        tree.getModel().isLeaf(node),
                                                                        row,
                                                                        false);

                if (c != null){
                    return c.getPreferredSize().height;
                }
            }
            return super.getRowHeight(row);
        }

        public int getRowHeight() {
            final int treeRowHeight = tree.getRowHeight();
            if (treeRowHeight < 0){ // then tree gets its height from the renderer
                return super.getRowHeight();
            }
            return treeRowHeight;
        }


        // rowAtPoint doesn't appear to calculate correctly with custom heights, so do it by hand
        public int rowAtPoint(Point point) {
            int rowYPos = 0; int row = -1;
            while (row < getRowCount() && rowYPos < point.y){
                row++;
                rowYPos += getRowHeight(row);
            }
            if (row < getRowCount()){
                return row;
            }
            return -1;
        }


        public TableCellEditor getCellEditor(int row, int col) {
            int modelIndex = table.convertColumnIndexToModel(col);
            if (editorFactory != null){
                TableCellEditor editor = editorFactory.getEditor(row, modelIndex);
                if (editor != null){
                    return editor;
                }
            }
            TableCellEditor editor = super.getCellEditor(row, col);
            return editor;
        }


    }
}
