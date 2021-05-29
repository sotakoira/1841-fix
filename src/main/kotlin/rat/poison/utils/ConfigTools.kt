package rat.poison.utils

import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.dialog.Dialogs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import rat.poison.LOADED_CONFIG
import rat.poison.SETTINGS_DIRECTORY
import rat.poison.curSettings
import rat.poison.overlay.App
import rat.poison.overlay.opened
import rat.poison.toLocale
import rat.poison.ui.tabs.updateWindows
import rat.poison.ui.uiPanels.configsTab
import rat.poison.ui.uiUpdate
import rat.poison.utils.generalUtil.loadSettingsFromFiles
import java.io.File
import java.io.FileReader
import java.nio.file.Files

var saving = false

fun saveDefault() {
    if (!saving) {
        GlobalScope.launch {
            saving = true
            println("\nSaving!\n")
            File(SETTINGS_DIRECTORY).listFiles()?.forEach { file ->
                val sbLines = StringBuilder()
                if (!file.isDirectory) {
                    FileReader(file).readLines().forEach { line ->
                        if (!line.startsWith("import") && !line.startsWith("/") && !line.startsWith(" *") && !line.startsWith("*") && line.trim().isNotEmpty()) {
                            val curLine = line.trim().split(" ".toRegex(), 3) //Separate line into VARIABLE NAME : "=" : VALUE

                            //Add custom checks
                            when {
                                curLine[0] == "HITSOUND_FILE_NAME" -> {
                                    sbLines.append(curLine[0] + " = \"" + curSettings[curLine[0]].replace("\"", "") + "\"\n")
                                }

                                curLine[0] == "FLASH_MAX_ALPHA" -> {
                                    sbLines.append(curLine[0] + " = " + curSettings[curLine[0]].replace("F", "") + "F\n")
                                }

                                else -> {
                                    sbLines.append(curLine[0] + " = " + curSettings[curLine[0]] + "\n")
                                }
                            }

                        }
                        else
                        {
                            sbLines.append(line + "\n")
                        }
                    }
                    Files.delete(file.toPath())
                    Files.createFile(file.toPath())
                    var firstLine = false
                    sbLines.lines().forEach {file.appendText(if (!firstLine) { firstLine = true; it } else if (!it.isBlank()) "\n" + it else "\n")}
                    sbLines.clear()
                }
            }
            println("\nSaving Complete!\n")
            saving = false
        }
    }
}



fun loadCFG(cfgFileName: String, deleteCfgAfterLoad: Boolean = false) {
    if (!saving) {
        cfgFileName.replace(".cfg", "")

        val cfgFile = File("$SETTINGS_DIRECTORY\\CFGS\\$cfgFileName.cfg")
        if (!cfgFile.exists()) {
            Dialogs.showOKDialog(App.menuStage, "Error".toLocale(), "${"FILE_NOT_FOUND_ERROR".toLocale()}\n \"${cfgFileName}\"")
        } else {
            saving = true
            GlobalScope.launch {
                println("Loading\n")
                loadSettingsFromFiles("$SETTINGS_DIRECTORY\\CFGS\\$cfgFileName.cfg", true)
                uiUpdate()
                updateWindows()
                println("\nLoading Complete!\n")
                LOADED_CONFIG = cfgFileName

                if (opened) {
                    App.uiMenu.updateTitle()
                    App.uiMenu.changeAlpha()
                }
                if (deleteCfgAfterLoad) { cfgFile.delete() }
                saving = false
            }
        }
    } else {
        Dialogs.showOKDialog(App.menuStage, "Error".toLocale(), "${"SAVING_NOW_ERROR".toLocale()}\n \"${cfgFileName}\"")
    }
}

fun saveWindows() {
    if (VisUI.isLoaded()) {
        curSettings["BOMB_TIMER_X"] = App.uiBombWindow.x
        curSettings["BOMB_TIMER_Y"] = App.uiBombWindow.y
        curSettings["BOMB_TIMER_ALPHA"] = App.uiBombWindow.color.a

        curSettings["SPECTATOR_LIST_X"] = App.uiSpecList.x
        curSettings["SPECTATOR_LIST_Y"] = App.uiSpecList.y
        curSettings["SPECTATOR_LIST_ALPHA"] = App.uiSpecList.color.a

        curSettings["KEYBINDS_X"] = App.uiKeybinds.x
        curSettings["KEYBINDS_Y"] = App.uiKeybinds.y
        curSettings["KEYBINDS_ALPHA"] = App.uiKeybinds.color.a
    }
}

fun saveCFG(cfgFileName: String) {
    if (!saving) {
        saving = true
        saveWindows()

        println("Saving!")

        val cfgDir = File("$SETTINGS_DIRECTORY\\CFGS")
        if (!cfgDir.exists()) {
            Files.createDirectory(cfgDir.toPath())
        }

        cfgFileName.replace(".cfg", "")

        val cfgFile = File("$SETTINGS_DIRECTORY\\CFGS\\$cfgFileName.cfg")
        if (!cfgFile.exists()) {
            cfgFile.createNewFile()
        }

        val sbLines = StringBuilder()
        File(SETTINGS_DIRECTORY).listFiles()?.forEach { file ->
            if (!file.isDirectory) {
                FileReader(file).readLines().forEach { line ->
                    if (!line.startsWith("import") && !line.startsWith("/") && !line.startsWith(" *") && !line.startsWith("*") && line.trim().isNotEmpty()) {
                        val tempCurLine = line.trim().split(" ".toRegex(), 3) //Separate line into 'VARIABLE=VALUE' //no spaces bc of trim()

                        sbLines.append(tempCurLine[0] + " = " + curSettings[tempCurLine[0]] + "\n") //add spaces back
                    }
                }
            }
        }

        Files.delete(cfgFile.toPath()) //Replace with cfgFile. ??
        Files.createFile(cfgFile.toPath())

        var firstLine = false
        sbLines.lines().forEach {cfgFile.appendText(if (!firstLine) { firstLine = true; it } else if (!it.isBlank()) "\n" + it else "\n")}
        sbLines.clear()

        println("\nSaving Complete!\n")
        if (VisUI.isLoaded()) {
            configsTab.updateCFGList()
        }
        saving = false
    }
}

fun deleteCFG(cfgFileName: String) {
    cfgFileName.replace(".cfg", "")

    val cfgFile = File("$SETTINGS_DIRECTORY\\CFGS\\$cfgFileName.cfg")
    if (cfgFile.exists()) {
        cfgFile.delete()
    }
    if (VisUI.isLoaded()) {
        configsTab.updateCFGList()
    }
    println("Deleted $cfgFileName\n")
}