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
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.gui.main.IMainWindow;
import org.omegat.util.Log;

import com.vlsolutions.swing.docking.AutoHideButtonPanel;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.RelativeDockablePosition;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Plugin entry point, registered in the JAR manifest via OmegaT-Plugins.
 *
 * The dockable panel is created in onApplicationStartup, because the main
 * window does not exist yet when loadPlugins() is called.
 *
 * Placement: a saved layout (uiLayout.xml) that contains the panel restores
 * it wherever the user left it. Otherwise (first install, or after "Restore
 * Main Window") the panel goes to the bottom border as a collapsed auto-hide
 * tab, next to Dictionary/Machine Translation.
 *
 * OmegaT's own startup and reset code calls UIDesignManager.ensureDockablesVisible,
 * which docks every registered-but-closed dockable at a default spot: an
 * invisible zero-width split in OmegaT 6.0, the middle of the window in 6.1.
 * That force-dock is vetoed for this panel and replaced by the auto-hide tab.
 * Undoing it afterwards with setAutoHide is not an option: setAutoHide derives
 * the "restore to" position from the panel's geometry, which is not laid out
 * yet at that point, and saves an out-of-range value (e.g. y=-0.625) that
 * VLDocking 3.1 (OmegaT 6.1) refuses to read back, breaking the whole layout
 * restore on the next start.
 */
public final class GridViewPlugin {

    private static volatile boolean shuttingDown;

    private GridViewPlugin() {
    }

    public static void loadPlugins() {
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                IMainWindow mainWindow = Core.getMainWindow();
                DockingDesktop desktop = mainWindow.getDesktop();
                GridViewPanel panel = new GridViewPanel();
                // Registered here so that MainWindow's own startup listener,
                // which runs after this one, can restore a saved position.
                desktop.registerDockable(panel);
                desktop.addDockableStateWillChangeListener(e -> {
                    DockableState current = e.getCurrentState();
                    DockableState future = e.getFutureState();
                    if (future != null && future.getDockable() == panel && future.isDocked()
                            && (current == null || current.isClosed()) && isForceDock()) {
                        e.cancel();
                        SwingUtilities.invokeLater(() -> addToBottomBorderIfMissing(desktop, panel));
                    }
                });
                // Runs after the layout restore, which happens later in the
                // same startup event.
                SwingUtilities.invokeLater(() -> addToBottomBorderIfMissing(desktop, panel));
                // "Restore Main Window" (and per-project layouts) reload the
                // layout with readXML, which silently drops every dockable the
                // loaded layout does not mention: no state event is fired. The
                // only trace is our component, or our auto-hide button, being
                // removed from its container; re-check after either.
                panel.addHierarchyListener(e -> {
                    if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0
                            && panel.getParent() == null) {
                        SwingUtilities.invokeLater(() -> addToBottomBorderIfMissing(desktop, panel));
                    }
                });
                ContainerAdapter buttonRemoved = new ContainerAdapter() {
                    @Override
                    public void componentRemoved(ContainerEvent e) {
                        SwingUtilities.invokeLater(() -> addToBottomBorderIfMissing(desktop, panel));
                    }
                };
                for (AutoHideButtonPanel border : findBorderPanels(desktop)) {
                    border.addContainerListener(buttonRemoved);
                }
            }

            @Override
            public void onApplicationShutdown() {
                shuttingDown = true;
            }
        });
    }

    public static void unloadPlugins() {
    }

    /** The desktop's auto-hide button bars (one per border). */
    private static List<AutoHideButtonPanel> findBorderPanels(Container root) {
        List<AutoHideButtonPanel> found = new ArrayList<>();
        for (Component child : root.getComponents()) {
            if (child instanceof AutoHideButtonPanel) {
                found.add((AutoHideButtonPanel) child);
            } else if (child instanceof Container) {
                found.addAll(findBorderPanels((Container) child));
            }
        }
        return found;
    }

    /** True if the dock request comes from OmegaT's ensureDockablesVisible. */
    private static boolean isForceDock() {
        for (StackTraceElement frame : new Throwable().getStackTrace()) {
            if ("ensureDockablesVisible".equals(frame.getMethodName())
                    && frame.getClassName().startsWith("org.omegat.")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the panel as a collapsed auto-hide tab at the bottom border unless
     * it is already on the desktop (docked, floating, maximized or
     * auto-hidden). The explicit BOTTOM position is what gets saved, so the
     * layout file stays valid.
     */
    private static void addToBottomBorderIfMissing(DockingDesktop desktop, GridViewPanel panel) {
        if (shuttingDown) {
            return;
        }
        try {
            DockableState state = desktop.getDockableState(panel);
            if (state == null) {
                desktop.registerDockable(panel);
                state = desktop.getDockableState(panel);
            }
            if (state == null || state.isClosed()) {
                desktop.addHiddenDockable(panel, RelativeDockablePosition.BOTTOM);
            }
        } catch (RuntimeException ex) {
            Log.log(ex);
        }
    }
}
