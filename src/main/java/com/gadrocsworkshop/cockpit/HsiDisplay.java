package com.gadrocsworkshop.cockpit;

import com.gadrocsworkshop.dcsbios.DcsBiosDataListener;
import com.gadrocsworkshop.dcsbios.DcsBiosParser;
import com.gadrocsworkshop.dcsbios.DcsBiosSyncListener;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.concurrent.CountDownLatch;

/**
 * Created by ccourtne on 2/8/15.
 */
public class HsiDisplay extends Group implements DcsBiosDataListener, DcsBiosSyncListener {

    private boolean dirty;

    private int dcsHeading;
    private int dcsCourse;
    private int dcsBearing1;
    private int dcsBearing2;
    private int dcsCourseDeviation;
    private int dcsToFlag;
    private int dcsFromFlag;
    private int dcsDeviationFlag;
    private int dcsOffFlag;

    private Rotate headingRotate;
    private Rotate courseRotate;
    private Rotate bearing1Rotate;
    private Rotate bearing2Rotate;
    private Node toFlag;
    private Node fromFlag;
    private Node deviationFlag;
    private Node offFlag;
    private Translate courseTranslate;

    public HsiDisplay(DcsBiosParser parser) {
        createImageRectangle(this, "/Outer Face.png", 0, 0, 640, 480);
        headingRotate = createRotatingNeedle(this, "/Compass Card.png", 320, 240, 365, 365);

        courseRotate = new Rotate(0, 320, 240);
        Group courseGroup = new Group();
        createNeedle(courseGroup, "/Course Card.png", 320, 240, 262, 324, 0, -11.5);
        toFlag = createImageRectangle(courseGroup, "/To Flag.png", 309.5, 185, 21, 18);
        fromFlag = createImageRectangle(courseGroup, "/From Flag.png", 309.5, 290, 21, 18);
        deviationFlag = createImageRectangle(courseGroup, "/Deviation Flag.png", 320-(39/2), 155, 39, 24);
        courseTranslate = createTranslatingNeedle(courseGroup, "/Deviation Needle.png", 320, 240, 8, 209);
        courseGroup.getTransforms().add(courseRotate);
        getChildren().add(courseGroup);

        bearing2Rotate = createRotatingNeedle(this, "/Bearing Needle 2.png", 320, 240, 33, 424, 0, -1);
        bearing1Rotate = createRotatingNeedle(this, "/Bearing Needle 1.png", 320, 240, 22, 440, 0, 0);

        offFlag = createImageRectangle(this, "/Off Flag.png", 640-32-20, 140, 32, 63);

        createImageRectangle(this, "/Lubber Line.png", 320 - (63 / 2), 2.5, 63, 475);
    }

    private Rectangle createImageRectangle(Group group, String imageFile, double x, double y, double width, double height) {
        Image image = new Image(imageFile);
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(new ImagePattern(image));
        group.getChildren().add(rectangle);
        return rectangle;
    }

    private Rectangle createNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        return createNeedle(group, imageFile, centerX, centerY, width, height, 0, 0);
    }

    private Rectangle createNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height, double offsetX, double offsetY) {
        return createImageRectangle(group, imageFile, centerX-(width/2)+offsetX, centerY-(height/2)+offsetY, width, height);
    }

    private Rotate createRotatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        return createRotatingNeedle(group, imageFile, centerX, centerY, width, height, 0, 0);
    }

    private Rotate createRotatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height, double offsetX, double offsetY) {
        Rectangle needle = createNeedle(group, imageFile, centerX, centerY, width, height, offsetX, offsetY);
        Rotate rotate = new Rotate(0, centerX, centerY);
        needle.getTransforms().add(rotate);
        return rotate;
    }

    private Translate createTranslatingNeedle(Group group, String imageFile, double centerX, double centerY, double width, double height) {
        Rectangle needle = createNeedle(group, imageFile, centerX, centerY, width, height);
        Translate translate = new Translate(0, 0);
        needle.getTransforms().add(translate);
        return translate;
    }

    private boolean isDirty() {
        return dirty;
    }

    private void setDirty() {
        dirty = true;
    }

    private void clearDirty() {
        dirty = true;
    }

    @Override
    public void dcsBiosDataWrite(int address, int newValue) {
        if (address == 0x104c) {
            dcsHeading = newValue;
            setDirty();
        }
        else if (address == 0x1054) {
            dcsCourse = newValue;
            setDirty();
        }
        else if (address == 0x1062) {
            dcsCourseDeviation = newValue;
            setDirty();
        }
        else if (address == 0x104e) {
            dcsBearing1 = newValue;
            setDirty();
        }
        else if (address == 0x1050) {
            dcsBearing2 = newValue;
            setDirty();
        }
        else if (address == 0x1064) {
            dcsToFlag = newValue;
            setDirty();
        }
        else if (address == 0x1066) {
            dcsFromFlag = newValue;
            setDirty();
        }
        else if (address == 0x104a) {
            dcsDeviationFlag = newValue;
            setDirty();
        }
        else if (address == 0x1046) {
            dcsOffFlag = newValue;
            setDirty();
        }
    }

    @Override
    public void handleDcsBiosFrameSync() {
        if (isDirty()) {

            // run synchronously on JavaFX thread
            if (Platform.isFxApplicationThread()) {
                updateDisplay();
                return;
            }

            // queue on JavaFX thread and wait for completion
            final CountDownLatch doneLatch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    updateDisplay();
                } finally {
                    doneLatch.countDown();
                }
            });

            try {
                doneLatch.await();
            } catch (InterruptedException e) {
                // ignore exception
            }
        }
    }

    public void updateDisplay() {
        setRotation(dcsHeading, headingRotate);
        setRotation(dcsCourse, courseRotate);
        setRotation(dcsBearing1, bearing1Rotate);
        setRotation(dcsBearing2, bearing2Rotate);
        setXTranslation(dcsCourseDeviation, courseTranslate, 75);
        setFlagVisible(dcsToFlag, toFlag);
        setFlagVisible(dcsFromFlag, fromFlag);
        setFlagVisible(dcsDeviationFlag, deviationFlag);
        setFlagVisible(dcsOffFlag, offFlag);
        clearDirty();
    }

    private void setFlagVisible(int dcsValue, Node flag) {
        flag.setVisible(dcsValue != 0);
    }

    private void setRotation(int dcsValue, Rotate rotation) {
        rotation.setAngle((dcsValue / 65535)*360.0f);
    }

    private void setXTranslation(int dcsValue, Translate translation, double maxDeflection) {
        translation.setX((dcsValue / 65535) * maxDeflection);
    }
}
