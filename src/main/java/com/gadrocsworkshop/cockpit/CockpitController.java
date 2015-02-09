package com.gadrocsworkshop.cockpit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.gadrocsworkshop.dcsbios.DcsBiosUdpReceiver;

/**
 * Created by ccourtne on 2/8/15.
 */
public class CockpitController extends Application {

    private DcsBiosUdpReceiver receiver;
    private AnimationTimer timer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        receiver = new DcsBiosUdpReceiver();

        HsiDisplay root = new HsiDisplay(receiver.getParser());

        primaryStage.setScene(new Scene(root, 640, 480, Color.rgb(26, 26, 26)));
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                root.updateDisplay();
            }
        };

        timer.start();
        receiver.start();
    }

    @Override
    public void stop() throws Exception {
        receiver.stop();
        timer.stop();
    }
}
