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
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.gui.main.IMainWindow;
import org.omegat.util.Log;
import org.omegat.util.StaticUtils;

import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.RelativeDockablePosition;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.SwingUtilities;

/**
 * Plugin entry point, registered in the JAR manifest via OmegaT-Plugins.
 *
 * The dockable panel is created in onApplicationStartup, because the main
 * window does not exist yet when loadPlugins() is called.
 *
 * Plugin listeners fire before MainWindow's own startup listener, which
 * restores the saved desktop layout (uiLayout.xml) and would wipe a panel
 * added directly here. So: register the dockable synchronously (letting a
 * saved layout restore its position), then after the layout restore has run
 * (via invokeLater) add the panel to the desktop if it is still not visible.
 */
public final class GridViewPlugin {

    private GridViewPlugin() {
    }

    public static void loadPlugins() {
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                IMainWindow mainWindow = Core.getMainWindow();
                GridViewPanel panel = new GridViewPanel();
                // Checked BEFORE MainWindow restores the saved layout (its
                // startup listener runs after this one): if the saved layout
                // does not mention our panel, any docked state seen later was
                // forced by OmegaT's ensureDockablesVisible (which docks every
                // registered-but-closed dockable into a zero-width split), not
                // chosen by the user.
                boolean inSavedLayout = savedLayoutMentionsPanel();
                mainWindow.getDesktop().registerDockable(panel);
                SwingUtilities.invokeLater(() -> {
                    DockableState state = mainWindow.getDesktop().getDockableState(panel);
                    try {
                        if (state == null || state.isClosed()) {
                            // Start as a collapsed auto-hide tab at the bottom
                            // border, next to Dictionary/Machine Translation.
                            mainWindow.getDesktop().addHiddenDockable(panel,
                                    RelativeDockablePosition.BOTTOM);
                        } else if (!inSavedLayout && state.isDocked()) {
                            // Undo the invisible zero-width force-dock.
                            mainWindow.getDesktop().setAutoHide(panel, true);
                        }
                    } catch (RuntimeException ex) {
                        Log.log(ex);
                    }
                });
            }

            @Override
            public void onApplicationShutdown() {
            }
        });
    }

    public static void unloadPlugins() {
    }

    private static boolean savedLayoutMentionsPanel() {
        try {
            File layout = new File(StaticUtils.getConfigDir(), "uiLayout.xml");
            return layout.isFile() && new String(Files.readAllBytes(layout.toPath()),
                    StandardCharsets.UTF_8).contains(GridViewPanel.DOCK_KEY);
        } catch (Exception ex) {
            Log.log(ex);
            // When unsure, assume the user has a layout and leave it alone.
            return true;
        }
    }
}
