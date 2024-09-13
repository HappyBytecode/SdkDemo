package anda.travel.driver.widget.layout;

import android.view.View;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

interface IBaseLayout {
    int HIDE_RADIUS_SIDE_NONE = 0;
    int HIDE_RADIUS_SIDE_TOP = 1;
    int HIDE_RADIUS_SIDE_RIGHT = 2;
    int HIDE_RADIUS_SIDE_BOTTOM = 3;
    int HIDE_RADIUS_SIDE_LEFT = 4;

    @IntDef(value = {
            HIDE_RADIUS_SIDE_NONE,
            HIDE_RADIUS_SIDE_TOP,
            HIDE_RADIUS_SIDE_RIGHT,
            HIDE_RADIUS_SIDE_BOTTOM,
            HIDE_RADIUS_SIDE_LEFT})
    @Retention(RetentionPolicy.SOURCE)
    @interface HideRadiusSide {
    }

    /**
     * limit the width of a layout
     *
     * @param widthLimit
     * @return
     */
    boolean setWidthLimit(int widthLimit);

    /**
     * limit the height of a layout
     *
     * @param heightLimit
     * @return
     */
    boolean setHeightLimit(int heightLimit);

    /**
     * See {@link android.view.View#setElevation(float)}
     *
     * @param elevation
     */
    void setShadowElevation(int elevation);

    /**
     * See {@link View#getElevation()}
     *
     * @return
     */
    int getShadowElevation();

    /**
     * set the outline alpha, which will change the shadow
     *
     * @param shadowAlpha
     */
    void setShadowAlpha(float shadowAlpha);

    /**
     * get the outline alpha we set
     *
     * @return
     */
    float getShadowAlpha();

    /**
     * @param shadowColor opaque color
     * @return
     */
    void setShadowColor(int shadowColor);

    /**
     * @return opaque color
     */
    int getShadowColor();

    /**
     * set the layout radius
     *
     * @param radius
     */
    void setRadius(int radius);

    /**
     * set the layout radius with one or none side been hidden
     *
     * @param radius
     * @param hideRadiusSide
     */
    void setRadius(int radius, @BaseLayoutHelper.HideRadiusSide int hideRadiusSide);

    /**
     * get the layout radius
     *
     * @return
     */
    int getRadius();

    /**
     * in some case, we maybe hope the layout only have radius in one side.
     * but there is no convenient way to write the code like canvas.drawPath,
     * so we take another way that hide one radius side
     *
     * @param hideRadiusSide
     */
    void setHideRadiusSide(@HideRadiusSide int hideRadiusSide);

    /**
     * get the side that we have hidden the radius
     *
     * @return
     */
    int getHideRadiusSide();

    /**
     * this method will determine the radius and shadow.
     */
    void setDefaultRadiusAndShadow();

    /**
     * this method will determine the radius and shadow.
     *
     * @param radius
     * @param shadowElevation
     */
    void setRadiusAndShadow(int radius, int shadowElevation);

    /**
     * this method will determine the radius and shadow.
     *
     * @param radius
     * @param shadowElevation
     * @param shadowAlpha
     */
    void setRadiusAndShadow(int radius, int shadowElevation, float shadowAlpha);

    /**
     * this method will determine the radius and shadow with one or none side be hidden
     *
     * @param radius
     * @param hideRadiusSide
     * @param shadowElevation
     * @param shadowAlpha
     */
    void setRadiusAndShadow(int radius, @HideRadiusSide int hideRadiusSide, int shadowElevation, float shadowAlpha);

    /**
     * this method will determine the radius and shadow (support shadowColor if after android 9)with one or none side be hidden
     *
     * @param radius
     * @param hideRadiusSide
     * @param shadowElevation
     * @param shadowColor
     * @param shadowAlpha
     */
    void setRadiusAndShadow(int radius, @HideRadiusSide int hideRadiusSide, int shadowElevation, int shadowColor, float shadowAlpha);

}
