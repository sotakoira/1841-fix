package rat.poison.ui.tabs.aimtabs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import rat.poison.curLocale
import rat.poison.curSettings
import rat.poison.dbg
import rat.poison.toLocale
import rat.poison.ui.changed
import rat.poison.ui.tabs.boneCategories
import rat.poison.ui.tabs.categorySelected
import rat.poison.ui.tabs.gunCategories
import rat.poison.ui.tabs.visualstabs.updateDisableDrawFOV
import rat.poison.ui.uiHelpers.VisCheckBoxCustom
import rat.poison.ui.uiHelpers.VisInputFieldCustom
import rat.poison.ui.uiHelpers.VisSelectBoxCustom
import rat.poison.ui.uiHelpers.VisSliderCustom
import rat.poison.ui.uiHelpers.aimTab.ATabVisCheckBox
import rat.poison.ui.uiHelpers.aimTab.ATabVisSlider
import rat.poison.ui.uiHelpers.binds.VisBindTableCustom
import rat.poison.ui.uiPanels.aimTab
import rat.poison.ui.uiUpdate
import rat.poison.utils.generalUtil.boolToStr
import rat.poison.utils.generalUtil.strToBool

class MainAimTab: Tab(true, false) {
    private val table = VisTable()
    val enableAim = VisCheckBoxCustom("Enable Aim", "ENABLE_AIM")
    val aimToggleKey = VisBindTableCustom("Toggle Aim Key", "AIM_TOGGLE_KEY")
    val activateFromFireKey = VisCheckBoxCustom("Activate From Fire Key", "ACTIVATE_FROM_AIM_KEY")
    val holdAim = VisCheckBoxCustom("Hold Aim", "HOLD_AIM")
    val teammatesAreEnemies = VisCheckBoxCustom("Teammates Are Enemies", "TEAMMATES_ARE_ENEMIES")

    val fovType = VisSelectBoxCustom("Fov Type", "FOV_TYPE", false, true, "DISTANCE", "STATIC")

    val forceAimBoneKey = VisBindTableCustom("Force Aim Bone Key", "FORCE_AIM_BONE_KEY")
    val forceAimKey = VisBindTableCustom("Force Aim Key", "FORCE_AIM_KEY")
    val forceAimAlways = VisCheckBoxCustom("Force Aim Always", "FORCE_AIM_ALWAYS")
    val forceAimThroughWalls = VisCheckBoxCustom("Force Aim Through Walls", "FORCE_AIM_THROUGH_WALLS")

    //Automatic Weapons Collapsible
    val automaticWeaponsCheckBox = VisCheckBoxCustom("Automatic Weapons", "GLOBAL_AUTOMATIC_WEAPONS")
    val automaticWeaponsInput = VisInputFieldCustom("MS Delay", "GLOBAL_AUTO_WEP_DELAY", false)

    val targetSwapDelay = VisSliderCustom("Target Swap Delay", "AIM_TARGET_SWAP_DELAY", 0F, 500F, 10F, true, labelWidth = 200F, barWidth = 250F)

    //Override Weapon Checkbox & Selection Box
    private val categorySelection = VisTable()
    val categorySelectionBox = VisSelectBox<String>()
    val categorySelectLabel = VisLabel("${"Weapon-Category".toLocale()}:")

    val enableAimOnShot = ATabVisCheckBox("Aim On Shot", "_AIM_ONLY_ON_SHOT")
    val enableFactorRecoil = ATabVisCheckBox("Factor Recoil", "_FACTOR_RECOIL")
    val enableFlatAim = ATabVisCheckBox("Write Angles", "_ENABLE_FLAT_AIM")
    val enablePathAim = ATabVisCheckBox("Mouse Movement", "_ENABLE_PATH_AIM")
    val enableScopedOnly = VisCheckBoxCustom("Scoped Only", "SNIPER_ENABLE_SCOPED_ONLY")

    val aimBone = VisSelectBoxCustom("Bone".toLocale(), "_AIM_BONE", useCategory = true, showText = true, items = *boneCategories)
    val forceAimBone = VisSelectBoxCustom("Force-Bone".toLocale(), "_AIM_FORCE_BONE", useCategory = true, showText = true,items = *boneCategories)

    val aimFov = ATabVisSlider("Aim FOV", "_AIM_FOV", .5F, 90F, .5F, false)
    val aimSpeed = ATabVisSlider("Aim Speed", "_AIM_SPEED", 0F, 10F, 1F, true)
    val aimSmooth = ATabVisSlider("Smoothness", "_AIM_SMOOTHNESS", 1F, 10F, .1F, false)
    val aimAfterShots = ATabVisSlider("Aim After #", "_AIM_AFTER_SHOTS", 0F, 10F, 1F, true)

    //Perfect Aim Collapsible
    val perfectAimCheckBox = VisCheckBox("Enable-Perfect-Aim".toLocale())
    private val perfectAimTable = VisTable()
    val perfectAimCollapsible = CollapsibleWidget(perfectAimTable)
    val perfectAimFov = ATabVisSlider("FOV", "_PERFECT_AIM_FOV", 1F, 90F, .5F, false)
    val perfectAimChance = ATabVisSlider("Chance", "_PERFECT_AIM_CHANCE", 1F, 100F, 1F, true)

    //Advanced Settings Collapsible
    val advancedSettingsCheckBox = VisCheckBox("Advanced-Settings".toLocale())
    private val advancedSettingsTable = VisTable()
    val advancedSettingsCollapsible = CollapsibleWidget(advancedSettingsTable)
    val randomizeX = ATabVisSlider("X Variation", "_RANDOM_X_VARIATION", 0F, 50F, 1F, true)
    val randomizeY = ATabVisSlider("Y Variation", "_RANDOM_Y_VARIATION", 0F, 50F, 1F, true)
    val randomizeDZ = ATabVisSlider("Variation Deadzone", "_VARIATION_DEADZONE", 0F, 100F, 5F, true)
    val advancedRcsX = ATabVisSlider("RCS X", "_AIM_RCS_X", 0.05F, 1F, 0.05F, false)
    val advancedRcsY = ATabVisSlider("RCS Y", "_AIM_RCS_Y", 0.05F, 1F, 0.05F, false)
    val advancedRcsVariation = ATabVisSlider("RCS Variation", "_AIM_RCS_VARIATION", 0F, 1F, 0.05F, false)
    val advancedSpeedDivisor = ATabVisSlider("Mouse Move Divisor", "_AIM_SPEED_DIVISOR", 1F, 10F, 1F, true)

    init {

        //Create Category Selector Box
        val itemsArray = Array<String>()
        for (i in gunCategories) {
            if (dbg && curLocale[i].isBlank()) {
                println("[DEBUG] ${curSettings["CURRENT_LOCALE"]} $i is missing!")
            }

            itemsArray.add(curLocale[i])
        }
        categorySelectionBox.items = itemsArray

        categorySelectionBox.selectedIndex = 0
        categorySelected = gunCategories[categorySelectionBox.selectedIndex]
        categorySelection.add(categorySelectLabel).padRight(200F-categorySelectLabel.width)
        categorySelection.add(categorySelectionBox)

        categorySelectionBox.changed { _, _ ->
            categorySelected = gunCategories[categorySelectionBox.selectedIndex]
            aimTab.tTrig.categorySelectionBox.selectedIndex = gunCategories.indexOf(categorySelected)
            aimTab.tBacktrack.categorySelectionBox.selectedIndex = gunCategories.indexOf(categorySelected)

            if (categorySelected == "SNIPER") {
                enableScopedOnly.color = Color(255F, 255F, 255F, 1F)
                enableScopedOnly.isDisabled = false
            } else {
                enableScopedOnly.color = Color(255F, 255F, 255F, 0F)
                enableScopedOnly.isDisabled = true
            }

            if (categorySelected == "RIFLE" || categorySelected == "SMG") {
                aimAfterShots.disable(false, Color(255F, 255F, 255F, 1F))
            } else {
                aimAfterShots.disable(true, Color(255F, 255F, 255F, 0F))
            }

            uiUpdate()
            true
        }

        //Disable on start, default is pistol
        aimAfterShots.disable(true, Color(255F, 255F, 255F, 0F))

        //Create Scoped Only Toggle
        enableScopedOnly.isChecked = curSettings["SNIPER_ENABLE_SCOPED_ONLY"].strToBool()
        enableScopedOnly.color = Color(255F, 255F, 255F, 0F)
        enableScopedOnly.isDisabled = true

        //Create Perfect Aim Collapsible Check Box
        perfectAimCheckBox.isChecked = curSettings[categorySelected + "_PERFECT_AIM"].strToBool()
        perfectAimCheckBox.changed { _, _ ->
            curSettings[categorySelected + "_PERFECT_AIM"] = perfectAimCheckBox.isChecked.boolToStr()
            perfectAimCollapsible.setCollapsed(!perfectAimCollapsible.isCollapsed, true)
        }

        perfectAimCollapsible.setCollapsed(!curSettings[categorySelected + "_PERFECT_AIM"].strToBool(), true)
        perfectAimTable.add(perfectAimFov).left().row()
        perfectAimTable.add(perfectAimChance).left().row()
        //End Perfect Aim Collapsible Check Box

        //Create Advanced Aim Settings Collapsible
        advancedSettingsCheckBox.isChecked = curSettings[categorySelected + "_ADVANCED_SETTINGS"].strToBool()
        advancedSettingsCheckBox.changed { _, _ ->
            curSettings[categorySelected + "_ADVANCED_SETTINGS"] = advancedSettingsCheckBox.isChecked.boolToStr()
            advancedSettingsCollapsible.setCollapsed(!advancedSettingsCollapsible.isCollapsed, true)
        }

        fovType.changed {_, _ ->
            updateDisableDrawFOV()
        }

        advancedSettingsCollapsible.setCollapsed(!curSettings[categorySelected + "_ADVANCED_SETTINGS"].strToBool(), true)

        advancedSettingsTable.add(randomizeX).left().row()
        advancedSettingsTable.add(randomizeY).left().row()
        advancedSettingsTable.add(randomizeDZ).left().row()
        advancedSettingsTable.add(advancedRcsX).left().row()
        advancedSettingsTable.add(advancedRcsY).left().row()
        advancedSettingsTable.add(advancedRcsVariation).left().row()
        advancedSettingsTable.add(advancedSpeedDivisor).left().row()
        //End

        //Default menu size is 500
        //Texts are 200
        //Sliders are 250
        //Leaves 25 for left and right side to center
        table.padLeft(25F)
        table.padRight(25F)

        //Add all items to label for tabbed pane content

        table.add(enableAim).left().row()
        table.add(aimToggleKey).left().row()

        table.add(activateFromFireKey).left().row()
        table.add(teammatesAreEnemies).left().row()
        table.add(holdAim).left().row()
        table.add(targetSwapDelay).left().row()
        table.add(fovType).left().row()

        table.addSeparator()

        table.add(forceAimBoneKey).left().row()
        table.add(forceAimKey).left().row()
        table.add(forceAimAlways).left().row()
        table.add(forceAimThroughWalls).left().row()

        table.addSeparator()

        table.add(automaticWeaponsCheckBox).left().row()
        table.add(automaticWeaponsInput).left().row()

        table.addSeparator()

        table.add(categorySelection).left().row()
        table.add(enableFactorRecoil).left().row()
        table.add(enableAimOnShot).left().row()
        table.add(enableFlatAim).left().row()
        table.add(enablePathAim).left().row()
        table.add(enableScopedOnly).left().row() //SNIPER selection only
        table.add(aimBone).left().row()
        table.add(forceAimBone).left().row()
        table.add(aimSpeed).left().row()
        table.add(aimFov).left().row()
        table.add(aimSmooth).left().row()
        table.add(aimAfterShots).left().row() //RIFLE & SMG selection only
        table.add(perfectAimCheckBox).left().row()
        table.add(perfectAimCollapsible).left().row()
        table.add(advancedSettingsCheckBox).left().row()
        table.add(advancedSettingsCollapsible).left().row()
    }
    override fun getTabTitle(): String {
        return "Aimbot".toLocale()
    }

    override fun getContentTable(): Table {
        return table
    }
}