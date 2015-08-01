package com.gadrocsworkshop.cockpit;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Display that adds the capability to display images and needles used to render an virtual gauge.
 *
 * Created by Craig Courtney on 3/1/2015.
 */
@SuppressWarnings("SameParameterValue")
public abstract class GaugeDisplay extends Display {

    /** Flag indicating whether this gauge needs to update it's display properties. */
    private boolean dirty;

    private Group rootGroup = new Group();

    @Override
    public Parent getParentNode() {
        return rootGroup;
    }

    /**
     * Returns the root group for this gauge.
     *
     * @return Group element which is used as root to add elements to this gauge.
     */
    protected Group getRootGroup() {
        return rootGroup;
    }

    /**
     * Creates an image based on a rectangle where the image will be rendered.
     *
     * @param group Group that this image will be added to.
     * @param imageFile Path to the image to be displayed.
     * @param x Top left X position this image will be displayed at.
     * @param y Top left Y position this image will be displayed at.
     * @param width Width to display the image.
     * @param height Height to display the image.
     * @return Rectangle object used to display the image.
     */
    protected Rectangle createImageRectangle(Group group, String imageFile, double x, double y, double width, double height) {
        Image image = new Image(imageFile);
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(new ImagePattern(image));
        group.getChildren().add(rectangle);
        return rectangle;
    }

    /**
     * Creates a needle based on an image and center position where the needle will be displayed.
     *
     * @param group Group that this image will be added to.
     * @param imageFile Path to the image to be displayed.
     * @param centerX Center X position where this needle will be displayed.
     * @param centerY Center Y position where this needle will be displayed.
     * @param width Width to display the needle.
     * @param height Height to display the needle.
     * @return Rectangle object used to display the needle.
     */
    private Rectangle createNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        return createNeedle(group, imageFile, centerX, centerY, width, height, 0, 0);
    }

    /**
     *  Creates a needle based on an image, center position and image center offset.
     *
     * @param group Group that this image will be added to.
     * @param imageFile Path to the image to be displayed.
     * @param centerX Center X position where this needle will be displayed.
     * @param centerY Center Y position where this needle will be displayed.
     * @param width Width to display the needle.
     * @param height Height to display the needle.
     * @param offsetX Offset X to account for actual needle center in the image.
     * @param offsetY Offset Y to account for actual needle center in the image.
     * @return Rectangle object used to display the needle.
     */
    protected Rectangle createNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height, double offsetX, double offsetY) {
        return createImageRectangle(group, imageFile, centerX - (width / 2) + offsetX, centerY - (height / 2) + offsetY, width, height);
    }

    /**
     * Creates a needle which will be rotated around the top left of the needle image.
     *
     * @param group Group that this image will be added to.
     * @param imageFile Path to the image to be displayed.
     * @param centerX Center X position where this needle will be displayed.
     * @param centerY Center Y position where this needle will be displayed.
     * @param width Width to display the needle.
     * @param height Height to display the needle.
     * @return Rotation object which can be used to rotated the needle.
     */
    protected Rotate createRotatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        return createRotatingNeedle(group, imageFile, centerX, centerY, width, height, 0, 0);
    }

    /**
     * Creates a needle which will be rotated around a point in the image.
     *
     * @param group Group that this image will be added to.
     * @param imageFile Path to the image to be displayed.
     * @param centerX Center X position where this needle will be displayed.
     * @param centerY Center Y position where this needle will be displayed.
     * @param width Width to display the needle.
     * @param height Height to display the needle.
     * @param offsetX Offset X to account for actual needle center in the image.
     * @param offsetY Offset Y to account for actual needle center in the image.
     * @return Rotation object which can be used to rotated the needle.
     */
    protected Rotate createRotatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height, double offsetX, double offsetY) {
        Rectangle needle = createNeedle(group, imageFile, centerX, centerY, width, height, offsetX, offsetY);
        Rotate rotate = new Rotate(0, centerX, centerY);
        needle.getTransforms().add(rotate);
        return rotate;
    }

    /**
     * Creates a needle which will be horizontally or vertically translated.
     *
     * @param group Group that this image will be added to.
     * @param imageFile Path to the image to be displayed.
     * @param centerX Center X position where this needle will be displayed.
     * @param centerY Center Y position where this needle will be displayed.
     * @param width Width to display the needle.
     * @param height Height to display the needle.
     * @return Translate object used to move the needle.
     */
    protected Translate createTranslatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        Rectangle needle = createNeedle(group, imageFile, centerX, centerY, width, height);
        Translate translate = new Translate(0, 0);
        needle.getTransforms().add(translate);
        return translate;
    }

    /**
     * Flag indicating whether this gauge needs to update it's display properties.
     *
     * @return True if the gauge state has changed since last display update.
     */
    protected boolean isDirty() {
        return dirty;
    }

    /**
     * Should be called when gauge state is updated.
     */
    protected void setDirty() {
        dirty = true;
    }

    /**
     * Should be called after a display update is done and the display matches the gauge state.
     */
    protected void clearDirty() {
        dirty = true;
    }
}
