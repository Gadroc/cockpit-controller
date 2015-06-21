package com.gadrocsworkshop.cockpit;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Created by Craig Courtney on 3/1/2015.
 */
public abstract class GaugeDisplay extends Display {
    protected boolean dirty;

    protected Rectangle createImageRectangle(Group group, String imageFile, double x, double y, double width, double height) {
        Image image = new Image(imageFile);
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(new ImagePattern(image));
        group.getChildren().add(rectangle);
        return rectangle;
    }

    private Rectangle createNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        return createNeedle(group, imageFile, centerX, centerY, width, height, 0, 0);
    }

    protected Rectangle createNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height, double offsetX, double offsetY) {
        return createImageRectangle(group, imageFile, centerX-(width/2)+offsetX, centerY-(height/2)+offsetY, width, height);
    }

    protected Rotate createRotatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        return createRotatingNeedle(group, imageFile, centerX, centerY, width, height, 0, 0);
    }

    protected Rotate createRotatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height, double offsetX, double offsetY) {
        Rectangle needle = createNeedle(group, imageFile, centerX, centerY, width, height, offsetX, offsetY);
        Rotate rotate = new Rotate(0, centerX, centerY);
        needle.getTransforms().add(rotate);
        return rotate;
    }

    protected Translate createTranslatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        Rectangle needle = createNeedle(group, imageFile, centerX, centerY, width, height);
        Translate translate = new Translate(0, 0);
        needle.getTransforms().add(translate);
        return translate;
    }

    protected boolean isDirty() {
        return dirty;
    }

    protected void setDirty() {
        dirty = true;
    }

    protected void clearDirty() {
        dirty = true;
    }
}
