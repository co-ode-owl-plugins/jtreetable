package uk.ac.manchester.cs.bhig.jtreetable;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.*;
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
 *
 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
 * to listen for changes in the ListSelectionModel it maintains. Once
 * a change in the ListSelectionModel happens, the paths are updated
 * in the DefaultTreeSelectionModel.
 */
public class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {

    /**
     * Set to true when we are updating the ListSelectionModel.
     */
    private boolean updatingListSelectionModel;

    private JTree tree;


    public ListToTreeSelectionModelWrapper(JTree tree) {
        super();
        this.tree = tree;
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    updateSelectedPathsFromSelectedRows();
                }
            }
        });
    }


    /**
     * Returns the list selection model. ListToTreeSelectionModelWrapper
     * listens for changes to this model and updates the selected paths
     * accordingly.
     */
    ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }


    /**
     * This is overridden to set <code>updatingListSelectionModel</code>
     * and message super. This is the only place DefaultTreeSelectionModel
     * alters the ListSelectionModel.
     */
    public void resetRowSelection() {
        if (!updatingListSelectionModel) {
            updatingListSelectionModel = true;
            try {
                super.resetRowSelection();
            }
            finally {
                updatingListSelectionModel = false;
            }
        }
        // Notice how we don't message super if
        // updatingListSelectionModel is true. If
        // updatingListSelectionModel is true, it implies the
        // ListSelectionModel has already been updated and the
        // paths are the only thing that needs to be updated.
    }

    /**
     * If <code>updatingListSelectionModel</code> is false, this will
     * reset the selected paths from the selected rows in the list
     * selection model.
     */
    protected void updateSelectedPathsFromSelectedRows() {
        if (!updatingListSelectionModel) {
            updatingListSelectionModel = true;
            try {
                int min = listSelectionModel.getMinSelectionIndex();
                int max = listSelectionModel.getMaxSelectionIndex();

                if (min != -1 && max != -1) {
                    boolean firstHasBeenSet = false; // hack required to clear the selection (as clearSelection does not work)
                    for (int counter = min; counter <= max; counter++) {
                        if (listSelectionModel.isSelectedIndex(counter)) {
                            TreePath selPath = tree.getPathForRow(counter);

                            if (selPath != null) {
                                if (firstHasBeenSet) {
                                    addSelectionPath(selPath);
                                }
                                else {
                                    setSelectionPath(selPath);
                                    firstHasBeenSet = true;
                                }
                            }
                        }
                    }
                }
            }
            finally {
                updatingListSelectionModel = false;
            }
        }
    }
}
