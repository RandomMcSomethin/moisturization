package com.random.moisturization.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "moisturization")
public class MoisturizationConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    @ConfigEntry.Category("moisturization.general")
    public transient static MoisturizationConfig instance;

    @ConfigEntry.Category("moisturization.general")
    @ConfigEntry.Gui.Tooltip
    public boolean cropsDie = true;

    @ConfigEntry.Category("moisturization.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 7, min = 1)
    public int waterRange = 1;

    @ConfigEntry.Category("moisturization.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 10, min = 1)
    public int growthReductor = 7;

    @ConfigEntry.Category("moisturization.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
    public int boneMealReductor = 3;

    @ConfigEntry.Category("moisturization.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 100, min = 1)
    public int farmlandDryingRate = 5;

    @ConfigEntry.Category("moisturization.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 7, min = 1)
    public int sprinklerRadius = 2;

    @ConfigEntry.Category("moisturization.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 7, min = 1)
    public int netheriteSprinklerRadius  = 4;
}
