package rat.poison.ui

import com.badlogic.gdx.graphics.Color
import rat.poison.curSettings
import rat.poison.dbg
import rat.poison.game.CSGO.csgoEXE
import rat.poison.game.me
import rat.poison.game.netvars.NetVarOffsets.flFlashMaxAlpha
import rat.poison.game.offsets.ClientOffsets.bOverridePostProcessing
import rat.poison.overlay.opened
import rat.poison.scripts.visuals.disableAllEsp
import rat.poison.ui.tabs.*
import rat.poison.ui.uiPanels.mainTabbedPane
import rat.poison.ui.uiPanels.optionsTab
import rat.poison.ui.uiPanels.ranksTab
import rat.poison.ui.uiPanels.visualsTab
import rat.poison.utils.beenFlashed
import rat.poison.utils.generalUtil.strToBool

fun disableDetectedUpdate() {
    if (!opened || uiRefreshing) return

    val bool = optionsTab.disableDetected.isChecked
    var col = Color(255F, 255F, 255F, 1F)
    if (bool) {
        col = Color(105F, 105F, 105F, .2F)
    }

    if (curSettings["ENABLE_ESP"].strToBool()) {
        visualsTab.apply {

            espTabbedPane.disableTab(footStepsEspTab, bool)
            espTabbedPane.disableTab(snaplinesEspTab, bool)
            espTabbedPane.disableTab(indicatorEspTab, bool)

            boxEspTab.skeletonEsp.disable(bool)
            boxEspTab.showTeamSkeleton.disable(bool)
            boxEspTab.showEnemiesSkeleton.disable(bool)

            footStepsEspTab.enableFootSteps.disable(bool)
            footStepsEspTab.footStepType.isDisabled = bool
            footStepsEspTab.footStepUpdateTimer.disable(bool, col)
            footStepsEspTab.footStepTTL.disable(bool, col)
            footStepsEspTab.footStepTeamBox.disable(bool)
            footStepsEspTab.footStepTeamColor.disable(bool)
            footStepsEspTab.footStepEnemyBox.disable(bool)
            footStepsEspTab.footStepEnemyColor.disable(bool)

            snaplinesEspTab.enableSnaplines.disable(bool)
            snaplinesEspTab.snaplinesWidth.disable(bool, col)
            snaplinesEspTab.snaplinesSmokeCheck.disable(bool)
            snaplinesEspTab.enemySnaplines.disable(bool)
            snaplinesEspTab.enemySnaplinesColor.disable(bool)
            snaplinesEspTab.teamSnaplines.disable(bool)
            snaplinesEspTab.teamSnaplinesColor.disable(bool)
            snaplinesEspTab.weaponSnaplines.disable(bool)
            snaplinesEspTab.weaponSnaplinesColor.disable(bool)
            snaplinesEspTab.bombSnaplines.disable(bool)
            snaplinesEspTab.bombSnaplinesColor.disable(bool)
            snaplinesEspTab.bombCarrierSnaplines.disable(bool)
            snaplinesEspTab.bombCarrierSnaplinesColor.disable(bool)

            indicatorEspTab.indicatorEsp.disable(bool)
            indicatorEspTab.indicatorDistance.disable(bool, col)
            indicatorEspTab.indicatorSize.disable(bool, col)
            indicatorEspTab.indicatorSmokeCheck.disable(bool)
            indicatorEspTab.showTeam.disable(bool)
            indicatorEspTab.showEnemies.disable(bool)
            indicatorEspTab.showBomb.disable(bool)
            indicatorEspTab.showBombCarrier.disable(bool)
            indicatorEspTab.showWeapons.disable(bool)
            indicatorEspTab.showGrenades.disable(bool)
            indicatorEspTab.showDefusers.disable(bool)
            indicatorEspTab.indicatorTeamColor.disable(bool)
            indicatorEspTab.indicatorEnemyColor.disable(bool)
            indicatorEspTab.indicatorBombColor.disable(bool)
            indicatorEspTab.indicatorDefuserColor.disable(bool)
            indicatorEspTab.indicatorWeaponColor.disable(bool)
            indicatorEspTab.indicatorGrenadeColor.disable(bool)

        }
    }
    othersTab.apply {
        othersTab.hitSoundCheckBox.disable(bool)
        othersTab.hitSoundVolume.disable(bool, col)
        othersTab.hitSoundBox.isDisabled = bool

        othersTab.killSoundCheckBox.disable(bool)
        othersTab.killSoundVolume.disable(bool, col)
        othersTab.killSoundBox.isDisabled = bool

        othersTab.enableReducedFlash.disable(bool)
        othersTab.flashMaxAlpha.disable(bool, col)

        othersTab.doorSpam.disable(bool)
        othersTab.doorSpamKey.disable(bool, col)

        othersTab.weaponSpam.disable(bool)
        othersTab.weaponSpamKey.disable(bool, col)

        othersTab.enableKillBind.disable(bool)
        othersTab.killBindKey.disable(bool, col)

        othersTab.postProcessingDisable.disable(bool)


    }

    optionsTab.apply {

     optionsTab.debug.disable(bool)
     dbg = !bool

    }

    mainTabbedPane.apply {
        mainTabbedPane.disableTab(ranksTab, bool)
    }

    optionsTab.disableDetected.changed { _, _ ->
        disableAllEsp()
        if (curSettings["DISABLE_POST_PROCESSING"].strToBool()) {
            if (csgoEXE.boolean(bOverridePostProcessing)) {
                csgoEXE[bOverridePostProcessing] = false
            }
        }

        if (beenFlashed) {
            if ((me > 0) && (csgoEXE.float(me + flFlashMaxAlpha) < 255F)) {
                csgoEXE[me + flFlashMaxAlpha] = 255F
                beenFlashed = false
            }
        }
    }

}

