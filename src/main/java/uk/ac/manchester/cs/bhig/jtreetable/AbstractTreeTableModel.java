package uk.ac.manchester.cs.bhig.jtreetable;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.util.*;
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
public abstract class AbstractTreeTableModel<T> implements TreeTableModel<T> {

    private JTree tree;

    private java.util.List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    private TableColumnModel columnModel = new DefaultTableColumnModel();

    private Map<Object, Integer> colIndex = new HashMap<Object, Integer>();

    // easier to find errors if the indices never match up with the layout
    private int nextColumnModelIndex = 100;

    private int rowCount = 0;


    public AbstractTreeTableModel(JTree tree) {
        this.tree = tree;
        tree.addTreeExpansionListener(new TreeExpansionListener(){
            public void treeExpanded(TreeExpansionEvent event) {
                notifyTreeExpanded(event);
            }
            public void treeCollapsed(TreeExpansionEvent event) {
                notifyTreeCollapsed(event);
            }
        });
    }


    public boolean addColumn(Object o) {
        final int modelIndex = nextColumnModelIndex++;
        if (!colIndex.containsKey(o)){
            colIndex.put(o, modelIndex);
            final TableColumn tc = new TableColumn();
            tc.setHeaderValue(o);
            tc.setModelIndex(modelIndex);
            columnModel.addColumn(tc);
            return true;
        }
        return false;
    }


    public void removeColumn(Object o) {
        int modelIndex = colIndex.get(o);
        colIndex.remove(o);

        for (Enumeration it = columnModel.getColumns(); it.hasMoreElements();){
            TableColumn tc = (TableColumn)it.nextElement();
            if (tc.getModelIndex() == modelIndex){
                columnModel.removeColumn(tc);
            }
        }
    }


    public Set getColumns() {
        return colIndex.keySet();
    }


    public Object getColumnObjectAtModelIndex(int index) {
        for (Object o : colIndex.keySet()){
            if (colIndex.get(o) == index){
                return o;
            }
        }
        return null;
    }


    public Object getColumnObjectFromPhysicalIndex(int index) {
        index = columnModel.getColumn(index).getModelIndex();
        return getColumnObjectAtModelIndex(index);
    }


    public int getModelIndexOfColumn(Object o) {
        Integer index = colIndex.get(o);
        if (index == null){
            index = -1;
        }
        return index;
    }


    public int getColumnCount() {
        return columnModel.getColumnCount();
    }


    public String getColumnName(int i) {
        return getColumnObjectAtModelIndex(i).toString();
    }


    public Class<?> getColumnClass(int i) {
        return getColumnObjectAtModelIndex(i).getClass();
    }


    public final boolean isCellEditable(int row, int col) {
        return isCellEditable(getNodeForRow(row), col);
    }


    public final int getRowCount() {
        return tree.getRowCount();
    }


    public final Object getValueAt(int row, int col) {
        return getValueAt(getNodeForRow(row), col);
    }


    public final void setValueAt(Object value, int row, int col) {
        setValueAt(value, getNodeForRow(row), col);
    }


    public T getNodeForRow(int row) {
        return (T)tree.getPathForRow(row).getLastPathComponent();
    }


    public TableColumnModel getColumnModel() {
        return columnModel;
    }


    public boolean isCellEditable(T node, int col){
        return true;
    }


    public abstract void setValueAt(Object value, T node, int column);


    public abstract Object getValueAt(T node, int column);


    public final void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }


    public final void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }


    private void notifyTreeExpanded(TreeExpansionEvent event) {
        int startrow = tree.getRowForPath(event.getPath())+1;
        int endRow = startrow + getDiff();//getLastExpandedRow(event.getPath());
        TableModelEvent tableDataEvent = new TableModelEvent(AbstractTreeTableModel.this,
                                                             startrow, endRow,
                                                             TableModelEvent.ALL_COLUMNS,
                                                             TableModelEvent.INSERT);
        for (TableModelListener l : listeners){
            l.tableChanged(tableDataEvent);
        }
    }


    private void notifyTreeCollapsed(TreeExpansionEvent event) {
        int startrow = tree.getRowForPath(event.getPath())+1;
        int endRow = startrow + getDiff();

        TableModelEvent tableDataEvent = new TableModelEvent(AbstractTreeTableModel.this,
                                                             startrow, endRow,
                                                             TableModelEvent.ALL_COLUMNS,
                                                             TableModelEvent.DELETE);
        for (TableModelListener l : listeners){
            l.tableChanged(tableDataEvent);
        }
    }

    // easy way to determine how many rows have been added/removed from the model
    public int getDiff() {
        int newRowCount = tree.getRowCount();
        if (rowCount == 0){
            rowCount = newRowCount;
        }

        int newRows = newRowCount - rowCount;
        rowCount = newRowCount;
        return newRows;
    }
}
