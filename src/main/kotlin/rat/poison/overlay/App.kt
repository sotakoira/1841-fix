@file:Suppress("DEPRECATION")
package rat.poison.overlay

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL20.GL_BLEND
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.kotcrab.vis.ui.VisUI
import com.sun.management.OperatingSystemMXBean
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import rat.poison.appless
import rat.poison.curSettings
import rat.poison.dbg
import rat.poison.game.CSGO
import rat.poison.game.entity.shotsFired
import rat.poison.game.me
import rat.poison.game.updateViewMatrix
import rat.poison.haltProcess
import rat.poison.interfaces.IOverlay
import rat.poison.interfaces.IOverlayListener
import rat.poison.jna.enums.AccentStates
import rat.poison.scripts.aim.meDead
import rat.poison.scripts.visuals.espToggleCallback
import rat.poison.settings.DANGER_ZONE
import rat.poison.settings.MENUTOG
import rat.poison.ui.tabs.updateDisableAim
import rat.poison.ui.uiPanels.*
import rat.poison.ui.uiUpdate
import rat.poison.utils.*
import rat.poison.utils.extensions.appendHumanReadableSize
import rat.poison.utils.extensions.roundNDecimals
import rat.poison.utils.generalUtil.strToBool
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureNanoTime

var opened = false
var overlayMenuKey = ObservableBoolean({ keyPressed(curSettings["MENU_KEY"].toInt()) })
var toggleAimKey = ObservableBoolean({ keyPressed(curSettings["AIM_TOGGLE_KEY"].toInt()) })
var visualsToggleKey = ObservableBoolean({ keyPressed(curSettings["VISUALS_TOGGLE_KEY"].toInt()) })


var syncTime = 0L
var glowTime = 0L
var appTime = 0L
var menuTime = 0L
var overlayTime = 0L

object App : ApplicationAdapter() {
    lateinit var sb: SpriteBatch
    lateinit var textRenderer: BitmapFont
    lateinit var shapeRenderer: ShapeRenderer
    private val overlay = Overlay(if (appless) {
        "Counter-Strike: Global Offensive - Direct3D 9"
    } else {
        curSettings["MENU_APP"].replace("\"", "")
    }, "Rat Poison UI", AccentStates.ACCENT_ENABLE_BLURBEHIND)
    lateinit var menuStage: Stage
    lateinit var assetManager: AssetManager
    private lateinit var menuBatch: SpriteBatch
    lateinit var viewport: ScalingViewport
    lateinit var keyProcessor: KeyProcessor
    lateinit var layout: GlyphLayout
    private val bodies = ObjectArrayList<App.() -> Unit>()
    private lateinit var camera: OrthographicCamera

    lateinit var uiMenu: UIMenu
    lateinit var uiBombWindow: UIBombTimer
    lateinit var uiSpecList: UISpectatorList
    lateinit var uiKeybinds: UIKeybinds
    private val sbText = StringBuilder()

    private val osBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
    var haveTarget = false
    private var timer = 0

    override fun create() {

        assetManager = AssetManager()
        assetManager.loadAssets()
        assetManager.updateFonts()

        //Implement key processor for menu
        keyProcessor = KeyProcessor()
        layout = GlyphLayout()

        shapeRenderer = ShapeRenderer().apply { setAutoShapeType(true) }

        uiMenu = UIMenu()
        uiBombWindow = UIBombTimer()
        uiSpecList = UISpectatorList()
        uiKeybinds = UIKeybinds()

        camera = OrthographicCamera()
        sb = SpriteBatch()
        menuBatch = SpriteBatch()
        viewport = ScalingViewport(Scaling.stretch, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)
        menuStage = Stage(viewport, menuBatch) //Main Menu Stage
        menuStage.addActor(uiMenu)

        Gdx.input.inputProcessor = InputMultiplexer(menuStage, keyProcessor)
        camera = OrthographicCamera()
        Gdx.gl.glClearColor(0F, 0F, 0F, 0F)

        //Implement stage for menu

        overlay.start()
    }

    override fun render() {
        timer++

        syncTime = TimeUnit.NANOSECONDS.convert(measureNanoTime {
            sync(curSettings["OPENGL_FPS"].toInt())
        }, TimeUnit.NANOSECONDS)

        if (VisUI.isLoaded()) {
            if (!Thread.interrupted()) {
                Gdx.gl.apply {
                    glEnable(GL_BLEND)
                    glDisable(GL20.GL_DEPTH_TEST)
                    glClearColor(0F, 0F, 0F, 0F)
                    glClear(GL20.GL_COLOR_BUFFER_BIT)
                    glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

                    if (!menuStage.root.isVisible) return

                    overlayTime = TimeUnit.NANOSECONDS.convert(measureNanoTime {
                        menuTime = TimeUnit.NANOSECONDS.convert(measureNanoTime {
                            assetManager.updateFonts()
                            if (MENUTOG || appless) {
                                if (curSettings["KEYBINDS"].strToBool()) {
                                    if (!menuStage.actors.contains(uiKeybinds)) {
                                        menuStage.addActor(uiKeybinds)
                                    }
                                } else if (menuStage.actors.contains(uiKeybinds)) {
                                    menuStage.clear() //actors.remove at index doesnt work after 1 loop?
                                }

                                if (!menuStage.actors.contains(uiMenu)) {
                                    menuStage.addActor(uiMenu)
                                }
                            } else if (menuStage.actors.contains(uiMenu) || menuStage.actors.contains(uiKeybinds)) {
                                menuStage.clear()
                            }

                            if (curSettings["ENABLE_BOMB_TIMER"].strToBool() && curSettings["BOMB_TIMER_MENU"].strToBool() && curSettings["ENABLE_ESP"].strToBool()) {
                                if (!menuStage.actors.contains(uiBombWindow)) {
                                    uiBombWindow.updateAlpha()
                                    menuStage.addActor(uiBombWindow)
                                }
                            } else if (menuStage.actors.contains(uiBombWindow)) {
                                menuStage.clear() //actors.remove at index doesnt work after 1 loop?
                            }

                            if (curSettings["SPECTATOR_LIST"].strToBool() && curSettings["ENABLE_ESP"].strToBool()) {
                                if (!menuStage.actors.contains(uiSpecList)) {
                                    uiSpecList.updateAlpha()
                                    menuStage.addActor(uiSpecList)
                                }
                            } else if (menuStage.actors.contains(uiSpecList)) {
                                menuStage.clear() //actors.remove at index doesnt work after 1 loop?
                            }
                        }, TimeUnit.NANOSECONDS)


                        sb.projectionMatrix = menuStage.camera.combined
                        shapeRenderer.projectionMatrix = menuStage.camera.combined
                        menuBatch.projectionMatrix = menuStage.camera.combined
                        uiMenu.changeAlpha()
                        appTime = TimeUnit.NANOSECONDS.convert(measureNanoTime {
                            if (!appless) {
                                updateViewMatrix()
                                if (!haltProcess) {
                                    for (i in 0 until bodies.size) {
                                        bodies[i]()
                                    }
                                }
                            }
                        }, TimeUnit.NANOSECONDS)

                        try { //Draw menu last, on top
                            if (menuBatch.isDrawing) {
                                menuBatch.end()
                            }
                            menuStage.act(Gdx.graphics.deltaTime)
                            menuStage.draw()
                        } catch(e: Exception) {
                            e.printStackTrace()
                        }

                        glFinish()
                    }, TimeUnit.NANOSECONDS)

                    if (curSettings["DEBUG"].strToBool() && !curSettings["DISABLE_DETECTED_FEATURES"].strToBool()) {//Draw Debug
                        //Limit updates
                        if (timer >= curSettings["OPENGL_FPS"].toInt()/4) {
                            val runtime = Runtime.getRuntime()

                            val totalMem = runtime.totalMemory()
                            val freeMem = runtime.freeMemory()
                            val usedMem = totalMem - freeMem

                            val totalPhysMem = osBean.totalPhysicalMemorySize
                            val freePhysMem = osBean.freePhysicalMemorySize

                            val processLoad = osBean.processCpuLoad
                            val systemLoad = osBean.systemCpuLoad

                            sbText.clear()
                            sbText.append("\nIn Game: $inGame")
                            sbText.append("\nShouldPostProcess: $shouldPostProcess")
                            sbText.append("\nMe: $me Dead: $meDead")
                            sbText.append("\nDanger Zone: $DANGER_ZONE")
                            sbText.append("\nShots fired: ${me.shotsFired()}")
                            sbText.append("\n")
                            sbText.append("\nTotal physical mem: ").appendHumanReadableSize(totalPhysMem)
                            sbText.append("\nFree physical mem: ").appendHumanReadableSize(freePhysMem)

                            sbText.append("\nTotal allocated mem: ").appendHumanReadableSize(totalMem)
                            sbText.append("\nFree allocated mem: ").appendHumanReadableSize(freeMem)
                            sbText.append("\nUsed allocated mem: ").appendHumanReadableSize(usedMem)

                            sbText.append("\nProcess load: ").append((processLoad.toFloat() * 100F).roundNDecimals(2)).append("%")
                            sbText.append("\nSystem load: ").append((systemLoad.toFloat() * 100F).roundNDecimals(2)).append("%")

                            sbText.append("\n\nSync took: ").append((syncTime.toFloat() * 0.000001F).roundNDecimals(4)).append(" ms")
                            sbText.append("\nOverlay took: ").append((overlayTime.toFloat() * 0.000001F).roundNDecimals(4)).append(" ms")
                            sbText.append("\n   Menu took: ").append((menuTime.toFloat() * 0.000001F).roundNDecimals(4)).append(" ms")
                            sbText.append("\n   Apps took: ").append((appTime.toFloat() * 0.000001F).roundNDecimals(4)).append(" ms")
                            sbText.append("\n      Glow took: ").append((glowTime.toFloat() * 0.000001F).roundNDecimals(4)).append(" ms")
                            timer = 0
                        }


                        try { //TODO fuck
                            if (sb.isDrawing) {
                                sb.end()
                            }

                            sb.begin()
                            glDisable(GL_BLEND)

                            textRenderer.color = Color.WHITE
                            sb.color = Color.WHITE
                            textRenderer.draw(sb, sbText, CSGO.gameWidth / 3F, CSGO.gameHeight - 100F)

                            sb.end()
                        } catch (e: Exception) {}
                    }
                }

                //Menu Key
                if (!appless) {
                    overlayMenuKey.update()
                    if (overlayMenuKey.justBecameTrue) {
                        MENUTOG = !MENUTOG
                        overlay.clickThrough = !MENUTOG

                        //uiMenu.updateChilds()
                        uiUpdate()

                        if (dbg) println("[DEBUG] Menu Toggled")
                    }
                }

                //Aim Toggle Key
                toggleAimKey.update()
                if (toggleAimKey.justBecameTrue) {
                    aimTab.tAim.enableAim.isChecked = !aimTab.tAim.enableAim.isChecked
                }

                visualsToggleKey.update()
                if (visualsToggleKey.justBecameTrue) {
                    espToggleCallback()
                }


                if (!appless) {
                    val w = overlay.width
                    val h = overlay.height

                    if (menuStage.viewport.screenWidth != w || menuStage.viewport.screenHeight != h) {
                        resize(w, h)
                        menuStage.viewport.update(w, h)
                        if (dbg) println("[DEBUG] Resized Viewports")
                    }
                }
            }
        }

        opened = true
    }

    override fun dispose() {
        overlay.stop()
    }

    operator fun invoke(body: App.() -> Unit) {
        bodies.add(body)
    }

    fun open() {
        overlay.listener = object : IOverlayListener {
            override fun onTargetAppWindowClosed(overlay: IOverlay) {
                haveTarget = false
                if (opened) {
                    uiMenu.closeMenu()
                }
            }

            override fun onAfterInit(overlay: IOverlay) {
                overlay.clickThrough = !appless
                overlay.protectAgainstScreenshots = false
                haveTarget = true
                updateDisableAim()
            }

            override fun onActive(overlay: IOverlay) {
                MENUTOG = true}
            override fun onPassive(overlay: IOverlay) {
                MENUTOG = false}
            override fun onBackground(overlay: IOverlay) {}
            override fun onForeground(overlay: IOverlay) {}
            override fun onBoundsChange(overlay: IOverlay, x: Int, y: Int, width: Int, height: Int) {}
        }
    }
}

var variableYieldTime = 0.toLong()
var lastSyncTime = 0.toLong()

fun sync(fps: Int) {
    if (fps <= 0) {
        return
    }

    val nanoSec = 1000000000L

    var overSleep = 0.toLong()
    val sleepTime = nanoSec/fps
    val yieldTime = min(sleepTime, variableYieldTime + sleepTime % (1000000L))

    try {
        while(true) {
            val t = System.nanoTime() - lastSyncTime

            if (t < sleepTime - yieldTime) {
                Thread.sleep(1)
            } else if (t < sleepTime) {
                Thread.yield()
            } else {
                overSleep = t - sleepTime
                break
            }
        }
    } catch (ex: InterruptedException) { println("FPS Sync Failure") } finally {
        lastSyncTime = System.nanoTime() - min(overSleep, sleepTime)

        if (overSleep > variableYieldTime) {
            variableYieldTime = min(variableYieldTime + 200 * 1000, sleepTime)
        } else if (overSleep < variableYieldTime - 200 * 1000) {
            variableYieldTime = max(variableYieldTime - 2 * 1000, 0)
        }
    }
}