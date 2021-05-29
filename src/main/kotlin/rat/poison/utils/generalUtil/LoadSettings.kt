package rat.poison.utils.generalUtil

import rat.poison.*
import rat.poison.utils.Settings
import rat.poison.utils.saving
import java.io.File
import java.io.FileReader
import kotlin.text.Charsets.UTF_8

private val keybindsSettings = listOf("AIM_TOGGLE_KEY", "FORCE_AIM_KEY", "FORCE_AIM_BONE_KEY", "TRIGGER_KEY", "VISUALS_TOGGLE_KEY", "D_SPAM_KEY", "W_SPAM_KEY", "MENU_KEY")

fun loadSettingsFromFiles(fileDir: String, specificFile: Boolean = false) {
    println("Loading settings... "  + if (specificFile) { fileDir } else "")
    setupValidSettings()
    val overloadKeybinds = curSettings["OVERLOAD_KEYBINDS"].strToBool()
    settingsLoaded = false
    if (specificFile) {
        FileReader(File(fileDir)).readLines().forEach { line ->
            if (!line.startsWith("import") && !line.startsWith("/") && !line.startsWith("\"") && !line.startsWith(" *") && !line.startsWith("*") && line.trim().isNotEmpty()) {
                val curLine = line.trim().split(" ".toRegex(), 3) //Separate line into VARIABLE NAME : "=" : VALUE

                if (curLine.size == 3) {
                    if (validateSetting(curLine[0], curLine[2]) && (!overloadKeybinds || curLine[0] !in keybindsSettings)) {
                        curSettings[curLine[0]] = curLine[2]
                    }
                } else {
                    println("Invalid Setting: $curLine")
                }
            }
        }
    } else {
        File(fileDir).listFiles()?.forEach { file ->
            if (!file.isDirectory && file.name.contains(".txt")) {
                FileReader(file).readLines().forEach { line ->
                    if (!line.startsWith("import") && !line.startsWith("/") && !line.startsWith(" *") && !line.startsWith("*") && line.trim().isNotEmpty()) {
                        val curLine = line.trim().split(" ".toRegex(), 3) //Separate line into VARIABLE NAME : "=" : VALUE

                        if (curLine.size == 3) {
                            if (validateSetting(curLine[0], curLine[2]) && (!overloadKeybinds || curLine[0] !in keybindsSettings)) {
                                curSettings[curLine[0]] = curLine[2]
                            }
                        } else {
                            println("Invalid Setting: $curLine")
                        }
                    }
                }
            }
        }
    }
    curSettings["OVERLOAD_KEYBINDS"] = overloadKeybinds
    settingsLoaded = true
    println("Settings loaded")
}

fun loadLocale(fileDir: String) {
    if (saving) return
    saving = true
    File(fileDir).readLines(UTF_8).forEach { line ->
        if (!line.startsWith("import") && !line.startsWith("/") && !line.startsWith("\"") && !line.startsWith(" *") && !line.startsWith("*") && line.trim().isNotEmpty()) {
            val curLine = line.trim().split(" ".toRegex(), 3) //Separate line into VARIABLE NAME : "=" : VALUE

            if (curLine.size == 3) {
                curLocale[curLine[0]] = curLine[2]
            } else {
                println("Debug: Locale invalid -- $curLine")
            }
        }
    }
    saving = false
}

//fuck a beat i was tryna beat a case
//bitch i did the race
//vroom
fun validateSetting(settingName: String, value: String): Boolean {
    var valid = true
    val inpValue = value.toUpperCase()


    if (!validSettingsMap["SKIP"].stringToList().contains(settingName)) {
        if (settingName.contains("AIM") && settingName.contains("BONE") && !settingName.contains("KEY")) { //Bones
            if (!validSettingsMap["BONE"].stringToList().contains(inpValue)) {
                valid = false
            }
        } else if (settingName.contains("BOX") && settingName.contains("POS")) {
            if (!validSettingsMap["BOX_POS"].stringToList().contains(inpValue)) {
                valid = false
            }
        } else if (settingName.contains("GLOW") && settingName.contains("TYPE")) {
            if (!validSettingsMap["GLOW_TYPE"].stringToList().contains(inpValue)) {
                valid = false
            }
        } else if (settingName.contains("FOV_TYPE")) {
            if (!validSettingsMap["FOV_TYPE"].stringToList().contains(inpValue)) {
                valid = false
            }
        } else if (value.contains("oWeapon")) {
            val size = value.replace("oWeapon(", "").replace(")", "").split(", ").size
            if (size < oWeaponSize) {
                //replace with valid
                var tStr = value
                tStr = tStr.replace("oWeapon(", "").replace(")", "")
                val tSA = tStr.split(", ") //temp String Array
                val weapon = oWeapon()
                weapon.apply {
                    tOverride = if (size > 1) tSA.pull(0).safeToBool(defaultValue = tOverride) else tOverride
                    tFRecoil = if (size > 2) tSA.pull(1).safeToBool(defaultValue = tFRecoil) else tFRecoil
                    tOnShot = if (size > 3) tSA.pull(2).safeToBool(defaultValue = tOnShot) else tOnShot
                    tFlatAim = if (size > 4) tSA.pull(3).safeToBool(defaultValue = tFlatAim) else tFlatAim
                    tPathAim = if (size > 5) tSA.pull(4).safeToBool(defaultValue = tPathAim) else tPathAim
                    tAimBone = if (size > 6) tSA.pull(5).safeToInt(defaultValue = tAimBone) else tAimBone
                    tForceBone = if (size > 7) tSA.pull(6).safeToInt(defaultValue = tForceBone) else tForceBone
                    tAimFov = if (size > 8) tSA.pull(7).safeToFloat(defaultValue = tAimFov) else tAimFov
                    tAimSpeed = if (size > 9) tSA.pull(8).safeToInt(defaultValue = tAimSpeed) else tAimSpeed
                    tAimSmooth = if (size > 10) tSA.pull(9).safeToFloat(defaultValue = tAimSmooth) else tAimSmooth
                    tPerfectAim = if (size > 11) tSA.pull(10).safeToBool(defaultValue = tPerfectAim) else tPerfectAim
                    tPAimFov = if (size > 12) tSA.pull(11).safeToFloat(defaultValue = tPAimFov) else tPAimFov
                    tPAimChance = if (size > 13) tSA.pull(12).safeToInt(defaultValue = tPAimChance) else tPAimChance
                    tScopedOnly = if (size > 14) tSA.pull(13).safeToBool(defaultValue = tScopedOnly) else tScopedOnly
                    tAimAfterShots = if (size > 15) tSA.pull(14).safeToInt(defaultValue = tAimAfterShots) else tAimAfterShots
                    tBoneTrig = if (size > 16) tSA.pull(15).safeToBool(defaultValue = tBoneTrig) else tBoneTrig
                    tBTrigAim = if (size > 17) tSA.pull(16).safeToBool(defaultValue = tBTrigAim) else tBTrigAim
                    tBTrigInCross = if (size > 18) tSA.pull(17).safeToBool(defaultValue = tBTrigInCross) else tBTrigInCross
                    tBTrigInFov = if (size > 19) tSA.pull(18).safeToBool(defaultValue = tBTrigInFov) else tBTrigInFov
                    tBTrigBacktrack = if (size > 20) tSA.pull(19).safeToBool(defaultValue = tBTrigBacktrack) else tBTrigBacktrack
                    tBTrigFov = if (size > 21) tSA.pull(20).safeToFloat(defaultValue = tBTrigFov) else tBTrigFov
                    tBTrigInitDelay = if (size > 22) tSA.pull(21).safeToInt(defaultValue = tBTrigInitDelay) else tBTrigInitDelay
                    tBTrigPerShotDelay = if (size > 23) tSA.pull(22).safeToInt(defaultValue = tBTrigPerShotDelay) else tBTrigPerShotDelay
                    tBacktrack = if (size > 24) tSA.pull(23).safeToBool(defaultValue = tBacktrack) else tBacktrack
                    tBTMS = if (size > 25) tSA.pull(24).safeToInt(defaultValue = tBTMS) else tBTMS
                    tAutowep = if (size > 26) tSA.pull(25).safeToBool(defaultValue = tAutowep) else tAutowep
                    tAutowepDelay = if (size >= 27) tSA.pull(26).safeToInt(defaultValue = tAutowepDelay) else tAutowepDelay
                }
                curSettings[settingName] = weapon.toString()
                return false
            }
        }
    }

    if (!valid) {
        println("Debug: Setting invalid: $settingName has incorrect value of $value")
    }
    return valid
}

val validSettingsMap = Settings()
fun setupValidSettings() {
    validSettingsMap["SKIP"] = listOf("AIM_BONE", "FORCE_AIM_BONE")
    validSettingsMap["BONE"] = listOf("HEAD", "NECK", "CHEST", "STOMACH", "NEAREST", "PELVIS", "RANDOM") //crashes when replacing to boneCategories
    validSettingsMap["BOX_POS"] = listOf("LEFT", "RIGHT", "TOP", "BOTTOM")
    validSettingsMap["GLOW_TYPE"] = listOf("NORMAL", "MODEL", "VISIBLE", "VISIBLE-FLICKER")
    validSettingsMap["FOV_TYPE"] = listOf("STATIC", "DISTANCE")
}

//move elsewhere
fun String.stringToList(): List<String> {
    val list = mutableListOf<String>()
    val strList = this.replace("[", "").replace("]", "").replace(",", "").split(" ")

    for (i in strList) {
        list.add(i)
    }

    return list
}