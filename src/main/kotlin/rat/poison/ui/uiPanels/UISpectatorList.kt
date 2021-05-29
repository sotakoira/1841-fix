package rat.poison.ui.uiPanels

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSlider
import com.kotcrab.vis.ui.widget.VisWindow
import rat.poison.curSettings
import rat.poison.overlay.App.uiSpecList
import rat.poison.overlay.opened
import rat.poison.toLocale
import rat.poison.ui.changed
import kotlin.math.round

lateinit var specListText : VisLabel

//Needs cleanup

class UISpectatorList : VisWindow("Spectator-List".toLocale()) {
    init {
        defaults().left()
        addCloseButton()

        specListText = VisLabel()

        //Create UI_Alpha Slider
        val menuAlphaSlider = VisSlider(0.5F, 1F, 0.05F, true)
        menuAlphaSlider.value = curSettings["SPECTATOR_LIST_ALPHA"].toFloat()
        menuAlphaSlider.changed { _, _ ->
            val alp = (round(menuAlphaSlider.value * 100F) / 100F)
            changeAlpha(alp)
            true
        }

        columnDefaults(2)

        add(specListText).growX().fillX().expandX().expandY().center().top().colspan(1)
        add(menuAlphaSlider).colspan(1).growY()

        pack()

        setSize(300F, 350F)
        setPosition(curSettings["SPECTATOR_LIST_X"].toFloat(), curSettings["SPECTATOR_LIST_Y"].toFloat())
        updateAlpha()
        isResizable = false
    }

    override fun positionChanged() {
        if (opened) {
            curSettings["SPECTATOR_LIST_X"] = uiSpecList.x
            curSettings["SPECTATOR_LIST_Y"] = uiSpecList.y
            super.positionChanged()
        }
    }

    override fun close() {
        curSettings["SPECTATOR_LIST"] = "false"
    }

    fun updateAlpha() {
        changeAlpha(curSettings["SPECTATOR_LIST_ALPHA"].toFloat())
    }

    fun changeAlpha(alpha: Float) {
        color.a = alpha
    }

    fun updatePosition(x: Float, y: Float) {
        setPosition(x, y)
    }
}
