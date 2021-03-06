package com.gadrocsworkshop.cockpit.displays;

import com.gadrocsworkshop.cockpit.GaugeDisplay;
import com.gadrocsworkshop.cockpit.RotaryEncoderDirection;
import com.gadrocsworkshop.dcsbios.DcsBiosDataListener;
import com.gadrocsworkshop.dcsbios.DcsBiosParser;
import com.gadrocsworkshop.dcsbios.DcsBiosSyncListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.logging.Logger;

/**
 * Displays a digital HSI replicating an AQU-6 from an A-10C
 *
 * Created by Craig Courtney on 2/8/15.
 */
public class HsiDisplay extends GaugeDisplay implements DcsBiosDataListener, DcsBiosSyncListener {

    private static final Logger LOGGER = Logger.getLogger(HsiDisplay.class.getName());

    private int dcsHeading;
    private int dcsCourse;
    private int dcsHeadingBug;
    private int dcsBearing1;
    private int dcsBearing2;
    private int dcsCourseDeviation;
    private int dcsToFlag;
    private int dcsFromFlag;
    private int dcsDeviationFlag;
    private int dcsOffFlag;

    private double headingAngle;
    private double headingBugAngle;
    private double courseAngle;
    private double bearing1Angle;
    private double bearing2Angle;
    private boolean toFlagVisible;
    private boolean fromFlagVisible;
    private boolean deviationFlagVisible;
    private boolean offFlagVisible;
    private double deviationOffset;

    private Rotate headingRotate;
    private Rotate headingBugRotate;
    private Rotate courseRotate;
    private Rotate bearing1Rotate;
    private Rotate bearing2Rotate;
    private Node toFlag;
    private Node fromFlag;
    private Node deviationFlag;
    private Node offFlag;
    private Translate courseTranslate;

    @Override
    public void onInitialize() {
        Group root = this.getRootGroup();

        createImageRectangle(root, "/Outer Face.png", 0, 0, 640, 480);
        headingRotate = createRotatingNeedle(root, "/Compass Card.png", 320, 240, 365, 365);

        courseRotate = new Rotate(0, 320, 240);
        Group courseGroup = new Group();
        createNeedle(courseGroup, "/Course Card.png", 320, 240, 262, 324, 0, -11.5);
        toFlag = createImageRectangle(courseGroup, "/To Flag.png", 309.5, 185, 21, 18);
        fromFlag = createImageRectangle(courseGroup, "/From Flag.png", 309.5, 290, 21, 18);
        deviationFlag = createImageRectangle(courseGroup, "/Deviation Flag.png", 320 - (39 / 2), 155, 39, 24);
        courseTranslate = createTranslatingNeedle(courseGroup, "/Deviation Needle.png", 320, 240, 8, 209);
        courseGroup.getTransforms().add(courseRotate);
        root.getChildren().add(courseGroup);

        headingBugRotate = createRotatingNeedle(root, "/Heading Bug.png", 320, 240, 33, 16, 0, -190);
        bearing2Rotate = createRotatingNeedle(root, "/Bearing Needle 2.png", 320, 240, 33, 424, 0, -1);
        bearing1Rotate = createRotatingNeedle(root, "/Bearing Needle 1.png", 320, 240, 22, 440, 0, 0);

        offFlag = createImageRectangle(root, "/Off Flag.png", 640 - 32 - 20, 140, 32, 63);

        createImageRectangle(root, "/Lubber Line.png", 320 - (63 / 2), 2.5, 63, 475);

        DcsBiosParser parser = getDcsBiosParser();
        parser.addDataListener(this);
        parser.addSyncListener(this);
    }

    @Override
    public void dcsBiosDataWrite(int address, int newValue) {
        if (address == 0x104c) {
            dcsHeading = newValue;
            setDirty();
        }
        else if (address == 0x1052) {
            dcsHeadingBug = newValue;
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
            headingAngle = getRotation(dcsHeading);
            headingBugAngle = getRotation(dcsHeadingBug);
            courseAngle = getRotation(dcsCourse);
            bearing1Angle = getRotation(dcsBearing1);
            bearing2Angle = getRotation(dcsBearing2);
            deviationOffset = getTranslation(dcsCourseDeviation, 75.0f);
            toFlagVisible = getFlagVisible(dcsToFlag);
            fromFlagVisible = getFlagVisible(dcsFromFlag);
            deviationFlagVisible = getFlagVisible(dcsDeviationFlag);
            offFlagVisible = getFlagVisible(dcsOffFlag);
        }
    }

    @Override
    public void onDisplay() {
        LOGGER.fine("Showing HSI");
        super.onDisplay();
    }

    @Override
    public void onUpdateDisplay() {
        if (isDirty()) {
            headingRotate.setAngle(headingAngle);
            headingBugRotate.setAngle(headingBugAngle);
            courseRotate.setAngle(courseAngle);
            bearing1Rotate.setAngle(bearing1Angle);
            bearing2Rotate.setAngle(bearing2Angle);
            courseTranslate.setX(deviationOffset);
            toFlag.setVisible(toFlagVisible);
            fromFlag.setVisible(fromFlagVisible);
            deviationFlag.setVisible(deviationFlagVisible);
            offFlag.setVisible(offFlagVisible);
            clearDirty();
        }
        clearDirty();
    }

    @Override
    public void controlButtonReleased() {
        MainMenuDisplay mainMenu = new MainMenuDisplay();
        controller.initDisplay(mainMenu);
        controller.showDisplay(mainMenu);
    }

    private boolean getFlagVisible(int dcsValue) {
        return dcsValue != 0;
    }

    private double getRotation(int dcsValue) {
        return (dcsValue / 65535f)*360.0f;
    }

    private double getTranslation(int dcsValue, @SuppressWarnings("SameParameterValue") double maxDeflection) {
        return ((dcsValue / 65535f) * maxDeflection*2)-maxDeflection;
    }

    @Override
    public void rightRotaryRotated(RotaryEncoderDirection direction) {
        if (direction == RotaryEncoderDirection.CW) {
            controller.sendCommand("HSI_CRS_KNOB +3200\n");
        }
        else {
            controller.sendCommand("HSI_CRS_KNOB -3200\n");
        }
    }

    @Override
    public void leftRotaryRotated(RotaryEncoderDirection direction) {
        if (direction == RotaryEncoderDirection.CW) {
            controller.sendCommand("HSI_HDG_KNOB +3200\n");
        }
        else {
            controller.sendCommand("HSI_HDG_KNOB -3200\n");
        }
    }
}
