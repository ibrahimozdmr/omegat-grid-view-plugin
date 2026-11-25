/*
 * Grid View plugin for OmegaT.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.ibrahimozdmr.omegat.gridview;

import org.omegat.core.Core;
import org.omegat.core.data.IProject;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.core.data.TMXEntry;
import org.omegat.gui.editor.IEditor;

import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Read-only table model over the project's segment list.
 *
 * Holds only the unmodifiable List&lt;SourceTextEntry&gt; returned by
 * IProject.getAllEntries(); target text and status are read lazily per cell
 * via getTranslationInfo(), so no per-segment copies are made even for very
 * large projects.
 */
@SuppressWarnings("serial")
class GridViewTableModel extends AbstractTableModel {

    static final int COL_NUM = 0;
    static final int COL_SOURCE = 1;
    static final int COL_TARGET = 2;
    static final int COL_STATUS = 3;

    static final String STATUS_TRANSLATED = "Çevrildi";
    static final String STATUS_UNTRANSLATED = "Çevrilmedi";
    static final String STATUS_ACTIVE = "Aktif";

    private static final String[] COLUMN_NAMES = {"No", "Kaynak", "Hedef", "Durum"};

    private List<SourceTextEntry> entries = Collections.emptyList();
    private int activeEntryNum = -1;

    void setEntries(List<SourceTextEntry> newEntries) {
        entries = newEntries != null ? newEntries : Collections.emptyList();
        activeEntryNum = -1;
        fireTableDataChanged();
    }

    SourceTextEntry getEntryAt(int row) {
        return entries.get(row);
    }

    int getActiveEntryNum() {
        return activeEntryNum;
    }

    /** Marks the editor's active segment; repaints only the affected rows. */
    void setActiveEntryNum(int entryNum) {
        int oldRow = getRowForEntryNum(activeEntryNum);
        activeEntryNum = entryNum;
        int newRow = getRowForEntryNum(entryNum);
        if (oldRow >= 0) {
            fireTableRowsUpdated(oldRow, oldRow);
        }
        if (newRow >= 0 && newRow != oldRow) {
            fireTableRowsUpdated(newRow, newRow);
        }
    }

    /**
     * Entry numbers are sequential in project order, and a single file's
     * entries are a contiguous block of it, so try offset indexing from the
     * first row and fall back to a linear scan.
     */
    int getRowForEntryNum(int entryNum) {
        if (entryNum <= 0 || entries.isEmpty()) {
            return -1;
        }
        int guess = entryNum - entries.get(0).entryNum();
        if (guess >= 0 && guess < entries.size() && entries.get(guess).entryNum() == entryNum) {
            return guess;
        }
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).entryNum() == entryNum) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == COL_NUM ? Integer.class : String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column != COL_TARGET) {
            return false;
        }
        IProject project = Core.getProject();
        return project != null && project.isProjectLoaded();
    }

    /**
     * Commits an edited target cell through the OmegaT editor
     * (gotoEntry + replaceEditText + commitAndLeave), so translation memory,
     * statistics and autosave all work exactly as if the text had been typed
     * in the editor pane.
     */
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (column != COL_TARGET || aValue == null) {
            return;
        }
        SourceTextEntry ste = entries.get(row);
        String newText = aValue.toString();
        TMXEntry current = getTranslationInfo(ste);
        if (current == null) {
            return;
        }
        String oldText = current.isTranslated() ? current.translation : "";
        if (newText.equals(oldText)) {
            return;
        }
        IEditor editor = Core.getEditor();
        if (editor == null) {
            return;
        }
        if (editor.getCurrentEntryNumber() != ste.entryNum()) {
            editor.gotoEntry(ste.entryNum());
            // Activation can fail (e.g. the entry is hidden by an editor
            // filter); don't overwrite whatever segment is really active.
            if (editor.getCurrentEntryNumber() != ste.entryNum()) {
                return;
            }
        }
        editor.replaceEditText(newText);
        editor.commitAndLeave();
        fireTableRowsUpdated(row, row);
    }

    @Override
    public Object getValueAt(int row, int column) {
        SourceTextEntry ste = entries.get(row);
        switch (column) {
        case COL_NUM:
            return ste.entryNum();
        case COL_SOURCE:
            return ste.getSrcText();
        case COL_TARGET:
            TMXEntry target = getTranslationInfo(ste);
            return target != null && target.isTranslated() ? target.translation : "";
        case COL_STATUS:
            if (ste.entryNum() == activeEntryNum) {
                return STATUS_ACTIVE;
            }
            TMXEntry status = getTranslationInfo(ste);
            return status != null && status.isTranslated() ? STATUS_TRANSLATED : STATUS_UNTRANSLATED;
        default:
            return "";
        }
    }

    /** Underlying translated state of a row (independent of the "Aktif" label). */
    boolean isRowTranslated(int row) {
        TMXEntry info = getTranslationInfo(entries.get(row));
        return info != null && info.isTranslated();
    }

    /**
     * The project can be closed between the CLOSE event and a pending repaint,
     * so every lazy read re-checks that a project is still loaded.
     */
    private TMXEntry getTranslationInfo(SourceTextEntry ste) {
        IProject project = Core.getProject();
        if (project == null || !project.isProjectLoaded()) {
            return null;
        }
        return project.getTranslationInfo(ste);
    }
}
