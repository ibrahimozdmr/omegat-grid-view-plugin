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
package org.omegat.plugins.gridview;

import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.core.events.IEntryEventListener;
import org.omegat.core.events.IProjectEventListener;
import org.omegat.gui.editor.IEditor;
import org.omegat.gui.main.DockablePanel;
import org.omegat.util.gui.IPaneMenu;

import com.vlsolutions.swing.docking.DockingConstants;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 * Dockable panel hosting the segment grid.
 */
@SuppressWarnings("serial")
public class GridViewPanel extends DockablePanel implements IPaneMenu {

    public static final String DOCK_KEY = "GRID_VIEW";

    private static final String FILTER_ALL = "Tümü";
    private static final String FILTER_UNTRANSLATED = "Çevrilmemiş";
    private static final String FILTER_TRANSLATED = "Çevrilmiş";

    private final GridViewTableModel model = new GridViewTableModel();
    private final JTable table = new JTable(model);
    private final TableRowSorter<GridViewTableModel> sorter = new TableRowSorter<>(model);
    private final JLabel statusLabel = new JLabel(" ");
    private final JTextField cellEditorField = new JTextField();
    private final JLabel searchLabel = new JLabel("Ara:");
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> filterCombo =
            new JComboBox<>(new String[] {FILTER_ALL, FILTER_UNTRANSLATED, FILTER_TRANSLATED});

    /** True while the table selection is being updated from an editor event,
     * to keep the selection listener from calling gotoEntry back. */
    private boolean syncingSelection;

    private Font baseFont;
    private int fontSizeDelta;
    private boolean wrapEnabled = true;
    private int baseRowHeight;

    public GridViewPanel() {
        super(DOCK_KEY, "Grid View", true);
        setLayout(new BorderLayout());

        // Wires the gear/settings button in the pane title bar.
        getDockKey().putProperty(IPaneMenu.PROPERTY_PANE_MENU_ACTION_LISTENER, this);
        // Without a menu entry to reopen it, a closed panel would be lost;
        // auto-hide covers the "get it out of the way" need instead.
        getDockKey().setCloseEnabled(false);
        getDockKey().setAutoHideBorder(DockingConstants.HIDE_BOTTOM);

        // Header-click sorting is disabled: the grid mirrors document order,
        // and sorting would force a full lazy read of every row.
        for (int i = 0; i < model.getColumnCount(); i++) {
            sorter.setSortable(i, false);
        }
        // Re-evaluate the filter when rows fire update events (translation
        // committed/removed), so rows leave/enter the filtered view live.
        sorter.setSortsOnUpdates(true);
        table.setRowSorter(sorter);
        sorter.addRowSorterListener(e -> {
            if (e.getType() != RowSorterEvent.Type.SORTED) {
                return;
            }
            // Deliberately NO height reset / scrolling here. Any attempt to
            // reset wrapped row heights when a row leaves the filtered view
            // moves the scroll position twice (once for the reset, once when
            // visible rows re-grow on paint) and the view visibly jumps.
            // Leaving heights alone keeps the viewport rock-steady; the cost
            // is cosmetic: a row can inherit its dropped neighbour's height
            // until the next full reset (filter/font/wrap change, reload).
            SwingUtilities.invokeLater(this::updateStatusLabel);
        });

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        // Commit a pending cell edit when focus moves elsewhere (e.g. to the
        // editor pane) instead of silently discarding it.
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        table.setDefaultEditor(String.class, new DefaultCellEditor(cellEditorField));

        table.getColumnModel().getColumn(GridViewTableModel.COL_NUM).setPreferredWidth(50);
        table.getColumnModel().getColumn(GridViewTableModel.COL_NUM).setMaxWidth(80);
        table.getColumnModel().getColumn(GridViewTableModel.COL_SOURCE).setPreferredWidth(320);
        table.getColumnModel().getColumn(GridViewTableModel.COL_TARGET).setPreferredWidth(320);
        table.getColumnModel().getColumn(GridViewTableModel.COL_STATUS).setPreferredWidth(90);
        table.getColumnModel().getColumn(GridViewTableModel.COL_STATUS).setMaxWidth(120);

        WrapCellRenderer wrapRenderer = new WrapCellRenderer();
        table.getColumnModel().getColumn(GridViewTableModel.COL_SOURCE).setCellRenderer(wrapRenderer);
        table.getColumnModel().getColumn(GridViewTableModel.COL_TARGET).setCellRenderer(wrapRenderer);
        table.getColumnModel().getColumn(GridViewTableModel.COL_NUM).setCellRenderer(new NumCellRenderer());
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer();
        statusRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        statusRenderer.setVerticalAlignment(DefaultTableCellRenderer.TOP);
        table.getColumnModel().getColumn(GridViewTableModel.COL_STATUS).setCellRenderer(statusRenderer);

        // Wrapped row heights depend on column width: recompute after resizes.
        table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            @Override
            public void columnMarginChanged(ChangeEvent e) {
                if (wrapEnabled && baseRowHeight > 0) {
                    table.setRowHeight(baseRowHeight);
                }
            }

            @Override
            public void columnAdded(TableColumnModelEvent e) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }
        });

        JPanel topBar = new JPanel();
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));
        topBar.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        topBar.add(searchLabel);
        topBar.add(Box.createHorizontalStrut(6));
        topBar.add(searchField);
        topBar.add(Box.createHorizontalStrut(8));
        filterCombo.setMaximumSize(filterCombo.getPreferredSize());
        topBar.add(filterCombo);
        add(topBar, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilter();
            }
        });
        filterCombo.addActionListener(e -> updateFilter());

        add(new JScrollPane(table), BorderLayout.CENTER);

        statusLabel.setBorder(new EmptyBorder(2, 6, 2, 6));
        add(statusLabel, BorderLayout.SOUTH);
        showNoProjectStatus();

        // Follow OmegaT's application font (Options > Font).
        baseFont = Core.getMainWindow().getApplicationFont();
        CoreEvents.registerFontChangedEventListener(newFont -> {
            baseFont = newFont;
            applyFont();
        });
        applyFont();

        // Fired on the EDT (CoreEvents wraps listeners in invokeLater).
        CoreEvents.registerProjectChangeListener(this::onProjectChanged);

        CoreEvents.registerEntryEventListener(new IEntryEventListener() {
            @Override
            public void onNewFile(String activeFileName) {
            }

            @Override
            public void onEntryActivated(SourceTextEntry newEntry) {
                GridViewPanel.this.onEntryActivated(newEntry);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || syncingSelection) {
                return;
            }
            onRowSelected();
        });

        // Defensive: if a project is somehow already open when the panel is
        // created, populate immediately instead of waiting for the next event.
        if (Core.getProject() != null && Core.getProject().isProjectLoaded()) {
            populateFromProject();
        }
    }

    @Override
    public void populatePaneMenu(JPopupMenu menu) {
        JCheckBoxMenuItem wrapItem = new JCheckBoxMenuItem("Satır kaydırma", wrapEnabled);
        wrapItem.addActionListener(e -> {
            wrapEnabled = wrapItem.isSelected();
            applyFont();
        });
        menu.add(wrapItem);
        menu.addSeparator();
        JMenuItem bigger = new JMenuItem("Yazı boyutunu büyüt");
        bigger.addActionListener(e -> {
            fontSizeDelta++;
            applyFont();
        });
        menu.add(bigger);
        JMenuItem smaller = new JMenuItem("Yazı boyutunu küçült");
        smaller.addActionListener(e -> {
            fontSizeDelta--;
            applyFont();
        });
        menu.add(smaller);
        JMenuItem reset = new JMenuItem("Varsayılan yazı boyutu");
        reset.addActionListener(e -> {
            fontSizeDelta = 0;
            applyFont();
        });
        menu.add(reset);
    }

    private void applyFont() {
        Font font = baseFont != null ? baseFont : table.getFont();
        float size = Math.max(8f, font.getSize() + fontSizeDelta);
        Font applied = font.deriveFont(size);
        table.setFont(applied);
        table.getTableHeader().setFont(applied);
        cellEditorField.setFont(applied);
        statusLabel.setFont(applied);
        searchLabel.setFont(applied);
        searchField.setFont(applied);
        filterCombo.setFont(applied);
        baseRowHeight = table.getFontMetrics(applied).getHeight() + 6;
        // Also clears per-row heights; visible wrapped rows grow back on render.
        table.setRowHeight(baseRowHeight);
    }

    /** Applies the search text + status filter; row heights are view-row based
     * and go stale when rows shift, so they are reset here too. */
    private void updateFilter() {
        String query = searchField.getText().trim();
        String statusChoice = (String) filterCombo.getSelectedItem();
        if (query.isEmpty() && FILTER_ALL.equals(statusChoice)) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(new RowFilter<GridViewTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends GridViewTableModel, ? extends Integer> entry) {
                    int row = entry.getIdentifier();
                    if (FILTER_UNTRANSLATED.equals(statusChoice) && model.isRowTranslated(row)) {
                        return false;
                    }
                    if (FILTER_TRANSLATED.equals(statusChoice) && !model.isRowTranslated(row)) {
                        return false;
                    }
                    if (query.isEmpty()) {
                        return true;
                    }
                    return containsIgnoreCase(String.valueOf(model.getValueAt(row, GridViewTableModel.COL_SOURCE)), query)
                            || containsIgnoreCase(String.valueOf(model.getValueAt(row, GridViewTableModel.COL_TARGET)), query)
                            || String.valueOf(model.getValueAt(row, GridViewTableModel.COL_NUM)).contains(query);
                }
            });
        }
        if (baseRowHeight > 0) {
            table.setRowHeight(baseRowHeight);
        }
        updateStatusLabel();
    }

    /** Case-insensitive both in the default (Turkish) locale and ROOT, so
     * searching "i" also matches "I" in English source text. */
    private static boolean containsIgnoreCase(String text, String query) {
        return text.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault()))
                || text.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }

    /** Table row selected by the user: jump the OmegaT editor to that segment. */
    private void onRowSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0 || Core.getProject() == null || !Core.getProject().isProjectLoaded()) {
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int entryNum = model.getEntryAt(modelRow).entryNum();
        IEditor editor = Core.getEditor();
        if (editor != null && editor.getCurrentEntryNumber() != entryNum) {
            editor.gotoEntry(entryNum);
        }
    }

    /** Editor moved to another segment: highlight and scroll to its row. */
    private void onEntryActivated(SourceTextEntry newEntry) {
        if (newEntry == null) {
            return;
        }
        int entryNum = newEntry.entryNum();
        model.setActiveEntryNum(entryNum);
        int modelRow = model.getRowForEntryNum(entryNum);
        if (modelRow < 0) {
            return;
        }
        int viewRow = table.convertRowIndexToView(modelRow);
        if (viewRow < 0) {
            return;
        }
        syncingSelection = true;
        try {
            table.setRowSelectionInterval(viewRow, viewRow);
            Rectangle cellRect = table.getCellRect(viewRow, 0, true);
            table.scrollRectToVisible(cellRect);
        } finally {
            syncingSelection = false;
        }
    }

    private void onProjectChanged(IProjectEventListener.PROJECT_CHANGE_TYPE eventType) {
        switch (eventType) {
        case CREATE:
        case LOAD:
            populateFromProject();
            break;
        case CLOSE:
            model.setEntries(Collections.emptyList());
            showNoProjectStatus();
            break;
        case MODIFIED:
        case SAVE:
            // Cell values are read lazily from the project, so visible rows
            // only need a repaint to reflect changed translations.
            table.repaint();
            break;
        default:
            break;
        }
    }

    private void populateFromProject() {
        model.setEntries(Core.getProject().getAllEntries());
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        if (model.getRowCount() == 0) {
            showNoProjectStatus();
            return;
        }
        String counts = table.getRowCount() < model.getRowCount()
                ? table.getRowCount() + " / " + model.getRowCount() + " segment"
                : model.getRowCount() + " segment";
        statusLabel.setText(counts
                + " — Hedef hücresini çift tıklayıp düzenleyebilirsiniz; Enter kaydeder.");
    }

    private void showNoProjectStatus() {
        statusLabel.setText("Proje açık değil.");
    }

    /**
     * Multi-line renderer for source/target columns. When wrapping is on it
     * grows the row height to fit the tallest wrapped cell; heights converge
     * because rows are only ever grown here and reset globally elsewhere.
     */
    private class WrapCellRenderer extends JTextArea implements TableCellRenderer {

        WrapCellRenderer() {
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(new EmptyBorder(2, 4, 2, 4));
        }

        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(tbl.getFont());
            setLineWrap(wrapEnabled);
            setText(value != null ? value.toString() : "");
            if (isSelected) {
                setBackground(tbl.getSelectionBackground());
                setForeground(tbl.getSelectionForeground());
            } else {
                setBackground(tbl.getBackground());
                setForeground(tbl.getForeground());
            }
            if (wrapEnabled) {
                int columnWidth = tbl.getColumnModel().getColumn(column).getWidth();
                setSize(columnWidth, Short.MAX_VALUE);
                int neededHeight = getPreferredSize().height;
                if (tbl.getRowHeight(row) < neededHeight) {
                    tbl.setRowHeight(row, neededHeight);
                }
            }
            return this;
        }
    }

    /** Centered, bold segment number with a little breathing room. */
    private static class NumCellRenderer extends DefaultTableCellRenderer {

        NumCellRenderer() {
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(TOP);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            setFont(tbl.getFont().deriveFont(Font.BOLD));
            setBorder(new EmptyBorder(2, 4, 2, 8));
            return this;
        }
    }
}
