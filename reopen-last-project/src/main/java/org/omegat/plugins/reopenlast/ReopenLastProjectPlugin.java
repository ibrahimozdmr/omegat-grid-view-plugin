/*
 * Reopen Last Project plugin for OmegaT.
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
package org.omegat.plugins.reopenlast;

import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.gui.main.ProjectUICommands;
import org.omegat.gui.preferences.PreferencesControllers;
import org.omegat.util.Log;
import org.omegat.util.OConsts;
import org.omegat.util.Preferences;
import org.omegat.util.RecentProjects;

import java.io.File;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Plugin entry point, registered in the JAR manifest via OmegaT-Plugins.
 *
 * OmegaT keeps a most-recently-used project list (RecentProjects) but never
 * reopens one automatically; this plugin fills that gap, respecting a
 * project passed on the command line and a user-configurable preference
 * (added to Preferences > Plugins as "Son Proje").
 */
public final class ReopenLastProjectPlugin {

    static final String PREF_ENABLED = "reopen_last_project.enabled";

    private ReopenLastProjectPlugin() {
    }

    public static void loadPlugins() {
        PreferencesControllers.addSupplier(ReopenLastProjectPreferencesController::new);

        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                // Nested invokeLater: Main queues its own frame-visible +
                // command-line-project-open Runnable via invokeLater right
                // after firing this very event, so it is still ahead of ours
                // in the EDT queue. Queuing our check here (deferred once
                // more) guarantees it runs after that Runnable has already
                // had its chance to open a CLI-specified project — verified
                // empirically with the Grid View plugin's identical pattern
                // for surviving OmegaT's own startup docking pass.
                SwingUtilities.invokeLater(() -> {
                    if (!isEnabled() || isProjectAlreadyOpen()) {
                        return;
                    }
                    File dir = mostRecentValidProject();
                    if (dir != null) {
                        try {
                            ProjectUICommands.projectOpen(dir);
                        } catch (RuntimeException ex) {
                            Log.log(ex);
                        }
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

    static boolean isEnabled() {
        return Preferences.isPreferenceDefault(PREF_ENABLED, true);
    }

    private static boolean isProjectAlreadyOpen() {
        return Core.getProject() != null && Core.getProject().isProjectLoaded();
    }

    /**
     * The newest recent-project entry, if its folder still exists and still
     * looks like a valid OmegaT project. A moved/deleted project is skipped
     * silently rather than surfacing an error dialog on every startup.
     */
    private static File mostRecentValidProject() {
        List<String> recents = RecentProjects.getRecentProjects();
        if (recents.isEmpty()) {
            return null;
        }
        File dir = new File(recents.get(0));
        File projectFile = new File(dir, OConsts.FILE_PROJECT);
        return dir.isDirectory() && projectFile.isFile() ? dir : null;
    }
}
