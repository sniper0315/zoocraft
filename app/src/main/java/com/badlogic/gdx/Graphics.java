/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexArray;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;

/** This interface encapsulates communication with the graphics processor. Depending on the available hardware and the current
 * {@link Application} configuration, access to {@link GL20} and {@link GL30} are provided here.
 * <p>
 * If supported by the backend, this interface lets you query the available display modes (graphics resolution and color depth)
 * and change it.
 * <p>
 * This interface can be used to switch between continuous and non-continuous rendering (see
 * {@link #setContinuousRendering(boolean)}), and to explicitly {@link #requestRendering()}.
 * <p>
 * There are many more utility classes that are not directly generated by the {@link Graphics} interfaces. See {@link VertexArray}
 * , {@link VertexBufferObject}, {@link IndexBufferObject}, {@link Mesh}, {@link ShaderProgram} and {@link FrameBuffer},
 * {@link BitmapFont}, {@link Batch} and so on. All these classes are managed, meaning they don't need to be reloaded on a context
 * loss. Explore the com.badlogic.gdx.graphics package for more classes that might come in handy.
 * @author mzechner */
public interface Graphics {
    /** Enumeration describing different types of {@link Graphics} implementations.
     *
     * @author mzechner */
    enum GraphicsType {
        AndroidGL, LWJGL, WebGL, iOSGL, JGLFW, Mock, LWJGL3
    }

    /** Describe a fullscreen display mode
     *
     * @author mzechner */
    class DisplayMode {
        /** the width in physical pixels **/
        public final int width;
        /** the height in physical pixels **/
        public final int height;
        /** the refresh rate in Hertz **/
        public final int refreshRate;
        /** the number of bits per pixel, may exclude alpha **/
        public final int bitsPerPixel;

        protected DisplayMode (int width, int height, int refreshRate, int bitsPerPixel) {
            this.width = width;
            this.height = height;
            this.refreshRate = refreshRate;
            this.bitsPerPixel = bitsPerPixel;
        }

        public String toString () {
            return width + "x" + height + ", bpp: " + bitsPerPixel + ", hz: " + refreshRate;
        }
    }

    /** Describes a monitor
     *
     * @author badlogic */
    class Monitor {
        public final int virtualX;
        public final int virtualY;
        public final String name;

        protected Monitor (int virtualX, int virtualY, String name) {
            this.virtualX = virtualX;
            this.virtualY = virtualY;
            this.name = name;
        }
    }

    /** Class describing the bits per pixel, depth buffer precision, stencil precision and number of MSAA samples. */
    class BufferFormat {
        /* number of bits per color channel */
        public final int r, g, b, a;
        /* number of bits for depth and stencil buffer */
        public final int depth, stencil;
        /** number of samples for multi-sample anti-aliasing (MSAA) **/
        public final int samples;
        /** whether coverage sampling anti-aliasing is used. in that case you have to clear the coverage buffer as well! */
        public final boolean coverageSampling;

        public BufferFormat (int r, int g, int b, int a, int depth, int stencil, int samples, boolean coverageSampling) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.depth = depth;
            this.stencil = stencil;
            this.samples = samples;
            this.coverageSampling = coverageSampling;
        }

        public String toString () {
            return "r: " + r + ", g: " + g + ", b: " + b + ", a: " + a + ", depth: " + depth + ", stencil: " + stencil
                    + ", num samples: " + samples + ", coverage sampling: " + coverageSampling;
        }
    }

    /** Returns whether OpenGL ES 3.0 is available. If it is you can get an instance of {@link GL30} via {@link #getGL30()} to
     * access OpenGL ES 3.0 functionality. Note that this functionality will only be available if you instructed the
     * {@link Application} instance to use OpenGL ES 3.0!
     *
     * @return whether OpenGL ES 3.0 is available */
    boolean isGL30Available ();

    /** @return the {@link GL20} instance */
    GL20 getGL20 ();

    /** @return the {@link GL30} instance or null if not supported */
    GL30 getGL30 ();

    /** Set the GL20 instance **/
    void setGL20 (GL20 gl20);

    /** Set the GL30 instance **/
    void setGL30 (GL30 gl30);

    /** @return the width of the client area in logical pixels. */
    int getWidth ();

    /** @return the height of the client area in logical pixels */
    int getHeight ();

    /** @return the width of the framebuffer in physical pixels */
    int getBackBufferWidth ();

    /** @return the height of the framebuffer in physical pixels */
    int getBackBufferHeight ();

    /** @return amount of pixels per logical pixel (point) */
    float getBackBufferScale ();

    /** @return the inset from the left which avoids display cutouts in logical pixels */
    int getSafeInsetLeft ();

    /** @return the inset from the top which avoids display cutouts in logical pixels */
    int getSafeInsetTop ();

    /** @return the inset from the bottom which avoids display cutouts or floating gesture bars, in logical pixels */
    int getSafeInsetBottom ();

    /** @return the inset from the right which avoids display cutouts in logical pixels */
    int getSafeInsetRight ();

    /** Returns the id of the current frame. The general contract of this method is that the id is incremented only when the
     * application is in the running state right before calling the {@link ApplicationListener#render()} method. Also, the id of
     * the first frame is 0; the id of subsequent frames is guaranteed to take increasing values for 2<sup>63</sup>-1 rendering
     * cycles.
     * @return the id of the current frame */
    long getFrameId ();

    /** @return the time span between the current frame and the last frame in seconds. */
    float getDeltaTime ();

    /** @return the time span between the current frame and the last frame in seconds, without smoothing
     * @deprecated use {@link #getDeltaTime()} instead. */
    @Deprecated
    float getRawDeltaTime ();

    /** @return the average number of frames per second */
    int getFramesPerSecond ();

    /** @return the {@link GraphicsType} of this Graphics instance */
    GraphicsType getType ();

    /** @return the {@link GLVersion} of this Graphics instance */
    GLVersion getGLVersion ();

    /** @return the pixels per inch on the x-axis */
    float getPpiX ();

    /** @return the pixels per inch on the y-axis */
    float getPpiY ();

    /** @return the pixels per centimeter on the x-axis */
    float getPpcX ();

    /** @return the pixels per centimeter on the y-axis. */
    float getPpcY ();

    /** This is a scaling factor for the Density Independent Pixel unit, following the same conventions as
     * android.util.DisplayMetrics#density, where one DIP is one pixel on an approximately 160 dpi screen. Thus on a 160dpi screen
     * this density value will be 1; on a 120 dpi screen it would be .75; etc.
     *
     * If the density could not be determined, this returns a default value of 1.
     *
     * Depending on the underlying platform implementation this might be a relatively expensive operation. Therefore it should not
     * be called continously on each frame.
     *
     * @return the Density Independent Pixel factor of the display. */
    float getDensity ();

    /** Whether the given backend supports a display mode change via calling {@link Graphics#setFullscreenMode(DisplayMode)}
     *
     * @return whether display mode changes are supported or not. */
    boolean supportsDisplayModeChange ();

    /** @return the primary monitor **/
    Monitor getPrimaryMonitor ();

    /** @return the monitor the application's window is located on */
    Monitor getMonitor ();

    /** @return the currently connected {@link Monitor}s */
    Monitor[] getMonitors ();

    /** @return the supported fullscreen {@link DisplayMode}(s) of the monitor the window is on */
    DisplayMode[] getDisplayModes ();

    /** @return the supported fullscreen {@link DisplayMode}s of the given {@link Monitor} */
    DisplayMode[] getDisplayModes (Monitor monitor);

    /** @return the current {@link DisplayMode} of the monitor the window is on. */
    DisplayMode getDisplayMode ();

    /** @return the current {@link DisplayMode} of the given {@link Monitor} */
    DisplayMode getDisplayMode (Monitor monitor);

    /** Sets the window to full-screen mode.
     *
     * @param displayMode the display mode.
     * @return whether the operation succeeded. */
    boolean setFullscreenMode (DisplayMode displayMode);

    /** Sets the window to windowed mode.
     *
     * @param width the width in pixels
     * @param height the height in pixels
     * @return whether the operation succeeded */
    boolean setWindowedMode (int width, int height);

    /** Sets the title of the window. Ignored on Android.
     *
     * @param title the title. */
    void setTitle (String title);

    /** Sets the window decoration as enabled or disabled. On Android, this will enable/disable the menu bar.
     *
     * Note that immediate behavior of this method may vary depending on the implementation. It may be necessary for the window to
     * be recreated in order for the changes to take effect. Consult the documentation for the backend in use for more information.
     *
     * Supported on all GDX desktop backends and on Android (to disable the menu bar).
     *
     * @param undecorated true if the window border or status bar should be hidden. false otherwise. */
    void setUndecorated (boolean undecorated);

    /** Sets whether or not the window should be resizable. Ignored on Android.
     *
     * Note that immediate behavior of this method may vary depending on the implementation. It may be necessary for the window to
     * be recreated in order for the changes to take effect. Consult the documentation for the backend in use for more information.
     *
     * Supported on all GDX desktop backends.
     *
     * @param resizable */
    void setResizable (boolean resizable);

    /** Enable/Disable vsynching. This is a best-effort attempt which might not work on all platforms.
     *
     * @param vsync vsync enabled or not. */
    void setVSync (boolean vsync);

    /** Sets the target framerate for the application when using continuous rendering. Might not work on all platforms. Is not
     * generally advised to be used on mobile platforms.
     *
     * @param fps the targeted fps; default differs by platform */
    public void setForegroundFPS (int fps);

    /** @return the format of the color, depth and stencil buffer in a {@link BufferFormat} instance */
    BufferFormat getBufferFormat ();

    /** @param extension the extension name
     * @return whether the extension is supported */
    boolean supportsExtension (String extension);

    /** Sets whether to render continuously. In case rendering is performed non-continuously, the following events will trigger a
     * redraw:
     *
     * <ul>
     * <li>A call to {@link #requestRendering()}</li>
     * <li>Input events from the touch screen/mouse or keyboard</li>
     * <li>A {@link Runnable} is posted to the rendering thread via {@link Application#postRunnable(Runnable)}. In the case of a
     * multi-window app, all windows will request rendering if a runnable is posted to the application. To avoid this, post a
     * runnable to the window instead.</li>
     * </ul>
     *
     * Life-cycle events will also be reported as usual, see {@link ApplicationListener}. This method can be called from any
     * thread.
     *
     * @param isContinuous whether the rendering should be continuous or not. */
    void setContinuousRendering (boolean isContinuous);

    /** @return whether rendering is continuous. */
    boolean isContinuousRendering ();

    /** Requests a new frame to be rendered if the rendering mode is non-continuous. This method can be called from any thread. */
    void requestRendering ();

    /** Whether the app is fullscreen or not */
    boolean isFullscreen ();

    /** Create a new cursor represented by the {@link com.badlogic.gdx.graphics.Pixmap}. The Pixmap must be in RGBA8888 format,
     * width & height must be powers-of-two greater than zero (not necessarily equal) and of a certain minimum size (32x32 is a
     * safe bet), and alpha transparency must be single-bit (i.e., 0x00 or 0xFF only). This function returns a Cursor object that
     * can be set as the system cursor by calling {@link #setCursor(Cursor)} .
     *
     * @param pixmap the mouse cursor image as a {@link com.badlogic.gdx.graphics.Pixmap}
     * @param xHotspot the x location of the hotspot pixel within the cursor image (origin top-left corner)
     * @param yHotspot the y location of the hotspot pixel within the cursor image (origin top-left corner)
     * @return a cursor object that can be used by calling {@link #setCursor(Cursor)} or null if not supported */
    Cursor newCursor (Pixmap pixmap, int xHotspot, int yHotspot);

    /** Only viable on the lwjgl-backend and on the gwt-backend. Browsers that support cursor:url() and support the png format (the
     * pixmap is converted to a data-url of type image/png) should also support custom cursors. Will set the mouse cursor image to
     * the image represented by the {@link com.badlogic.gdx.graphics.Cursor}. It is recommended to call this function in the main
     * render thread, and maximum one time per frame.
     *
     * @param cursor the mouse cursor as a {@link com.badlogic.gdx.graphics.Cursor} */
    void setCursor (Cursor cursor);

    /** Sets one of the predefined {@link SystemCursor}s */
    void setSystemCursor (SystemCursor systemCursor);
}