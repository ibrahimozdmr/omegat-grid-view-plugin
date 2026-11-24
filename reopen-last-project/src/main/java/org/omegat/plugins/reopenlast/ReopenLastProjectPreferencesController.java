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

import org.omegat.gui.preferences.BasePreferencesController;
import org.omegat.util.Preferences;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Adds a "Son Proje" tab under Preferences > Plugins (getParentViewClass()
 * defaults to null, which OmegaT's preferences tree resolves to the Plugins
 * node) with a single on/off checkbox for the auto-reopen behavior.
 */
final class ReopenLastProjectPreferencesController extends BasePreferencesController {

    private JPanel panel;
    private JCheckBox enabledCheckBox;

    @Override
    public String toString() {
        return "Son Proje";
    }

    @Override
    public Component getGui() {
        if (panel == null) {
            enabledCheckBox = new JCheckBox("Uygulama açılışında en son kullanılan projeyi otomatik aç");
            panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            panel.add(enabledCheckBox, BorderLayout.NORTH);
            initFromPrefs();
        }
        return panel;
    }

    @Override
    protected void initFromPrefs() {
        enabledCheckBox.setSelected(ReopenLastProjectPlugin.isEnabled());
    }

    @Override
    public void persist() {
        Preferences.setPreference(ReopenLastProjectPlugin.PREF_ENABLED, enabledCheckBox.isSelected());
    }

    @Override
    public void restoreDefaults() {
        enabledCheckBox.setSelected(true);
    }
}
