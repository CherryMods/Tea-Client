/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import java.awt.Color;
import java.util.function.BooleanSupplier;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ColorSetting;
import net.wurstclient.settings.EnumSetting;

@SearchTags({ "wurst logo", "top left corner" })
@DontBlock
public final class WurstLogoOtf extends OtherFeature {
    private final CheckboxSetting showBg = new CheckboxSetting("Show BG",
            "Shows the background rectangle behind the logo and text", true);

    private final ColorSetting bgColor = new ColorSetting("Background",
            "Background color.\n"
                    + "Only visible when \u00a76RainbowUI\u00a7r is disabled.",
            Color.WHITE);

    private final ColorSetting txtColor = new ColorSetting("Text", "Text color.", Color.BLACK);

    private final EnumSetting<Visibility> visibility = new EnumSetting<>("Visibility", Visibility.values(),
            Visibility.ALWAYS);

    private final EnumSetting<Format> format = new EnumSetting<>("Format", Format.values(), Format.FULL);

    public WurstLogoOtf() {
        super("WurstLogo", "Shows the Wurst logo and version on the screen.");
        addSetting(showBg);
        addSetting(bgColor);
        addSetting(txtColor);
        addSetting(visibility);
        addSetting(format);
    }

    public boolean isVisible() {
        return visibility.getSelected().isVisible();
    }

    public boolean isBgVisible() {
        return showBg.isChecked();
    }

    public Format getFormat() {
        return format.getSelected();
    }

    public int getBackgroundColor() {
        return bgColor.getColorI(128);
    }

    public int getTextColor() {
        return txtColor.getColorI();
    }

    public static enum Visibility {
        ALWAYS("Always", () -> true),
        NEVER("Never", () -> false),

        ONLY_OUTDATED("Only when outdated",
                () -> WURST.getUpdater().isOutdated());

        private final String name;
        private final BooleanSupplier visible;

        private Visibility(String name, BooleanSupplier visible) {
            this.name = name;
            this.visible = visible;
        }

        public boolean isVisible() {
            return visible.getAsBoolean();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static enum Format {
        FULL("Full"),
        WurstOnly("Wurst Version Only"),
        MCOnly("MC Version Only"),
        NoVersion("No version");

        private final String name;

        private Format(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
