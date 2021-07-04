package rat.poison.ui

import com.badlogic.gdx.graphics.Color
import rat.poison.curSettings
import rat.poison.overlay.opened
import rat.poison.ui.tabs.boxEspTab
import rat.poison.ui.tabs.espTabbedPane
import rat.poison.ui.tabs.footStepsEspTab
import rat.poison.ui.tabs.othersTab
import rat.poison.ui.uiPanels.mainTabbedPane
import rat.poison.ui.uiPanels.optionsTab
import rat.poison.ui.uiPanels.ranksTab
import rat.poison.ui.uiPanels.visualsTab
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

        }
    }
    othersTab.apply {
        othersTab.enableReducedFlash.disable(bool)
        othersTab.flashMaxAlpha.disable(bool, col)
        othersTab.postProcessingDisable.disable(bool)
    }
    mainTabbedPane.apply {
        mainTabbedPane.disableTab(ranksTab, bool)
    }

}