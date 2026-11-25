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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User interface texts. OmegaT picks its interface language from the JVM
 * default locale (system language, or -Duser.language in OmegaT.l4J.ini),
 * so following the same locale keeps the panel in OmegaT's language.
 * Languages without their own Bundle_xx.properties fall back to English.
 */
final class Messages {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "io.github.ibrahimozdmr.omegat.gridview.Bundle", Locale.getDefault(),
            Messages.class.getClassLoader());

    private Messages() {
    }

    static String get(String key) {
        return BUNDLE.getString(key);
    }

    static String format(String key, Object... args) {
        return new MessageFormat(get(key), BUNDLE.getLocale()).format(args);
    }
}
