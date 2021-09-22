package rat.poison.game.offsets

import rat.poison.game.CSGO.engineDLL
import rat.poison.utils.extensions.invoke
import rat.poison.utils.get
import rat.poison.utils.parseOffset

object EngineOffsets {

    val dwClientState by engineDLL(1)(0xA1, 0[4], 0x33, 0xD2, 0x6A, 0, 0x6A, 0, 0x33, 0xC9, 0x89, 0xB0)
    val dwModelPrecache by engineDLL(0x3, subtract = false)(0x0C, 0x3B, 0x81, 0[4], 0x75, 0x11, 0x8B, 0x45, 0x10, 0x83, 0xF8, 0x01, 0x7C, 0x09, 0x50, 0x83)

    val dwGlobalVars by engineDLL(1)(0x68, 0[4], 0x68, 0[4], 0xFF, 0x50, 0x08, 0x85, 0xC0)
    val dwViewAngles by engineDLL(4, subtract = false)(0xF3, 0x0F, 0x11, 0x86, 0[4], 0xF3, 0x0F, 0x10, 0x44, 0x24, 0, 0xF3, 0x0F, 0x11, 0x86)

    val dwSignOnState by engineDLL(2, subtract = false)(0x83, 0xB8, 0[5], 0x0F, 0x94, 0xC0, 0xC3)
    var dwbSendPackets = parseOffset()

    val pStudioModel by engineDLL(0x2)(0x8B, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x0F, 0xB7, 0x80,
            0x00, 0x00, 0x00, 0x00, 0x8B, 0x11, 0x89, 0x45, 0x08, 0x5D, 0xFF, 0x62, 0x38, 0x33, 0xC0)

    val dwModelAmbientMin by engineDLL(4)(0xF3, 0x0F, 0x10, 0x0D, 0[4], 0xF3, 0x0F, 0x11, 0x4C, 0x24, 0, 0x8B, 0x44, 0x24, 0x20, 0x35, 0[4], 0x89, 0x44, 0x24, 0x0C)

    val dwClientState_PlayerInfo by engineDLL(2, subtract = false)(0x8B, 0x89, 0[4], 0x85, 0xC9, 0x0F, 0x84, 0[4], 0x8B, 0x01)
    val dwClientState_State by engineDLL(2, subtract = false)(0x83, 0xB8, 0[5], 0x0F, 0x94, 0xC0, 0xC3)
    val dwClientState_MapDirectory by engineDLL(7, subtract = false)(0xB8, 0[4], 0xC3, 0x05, 0[4], 0xC3)
    val dwClientStateNetChannel by engineDLL(2, subtract = false)(0x8B, 0x8F, 0[4], 0x8B, 0x01, 0x8B, 0x40, 0x18)
    val dwGameDir by engineDLL(1)(0x68, 0[4], 0x8D, 0x85, 0[4], 0x50, 0x68, 0[4], 0x68)
    val dwClientState_LastOutgoingCommand by engineDLL(2, subtract = false)(0x8B, 0x8F, 0[4], 0x8B, 0x87, 0[4], 0x41)
}
