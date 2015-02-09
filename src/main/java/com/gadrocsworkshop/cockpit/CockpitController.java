package com.gadrocsworkshop.cockpit;

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
    }

    @Override
    public void stop() throws Exception {
        receiver.stop();
    }
}
