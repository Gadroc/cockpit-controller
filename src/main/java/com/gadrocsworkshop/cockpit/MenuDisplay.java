package com.gadrocsworkshop.cockpit;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Menu display which allows interaction via rotaries and selection button.
 *
 * Display which presents an interactive menu.
 */
public abstract class MenuDisplay extends Display {

    private static final Logger LOGGER = Logger.getLogger(MenuDisplay.class.getName());

    private static final Font MENU_TITLE_FONT;
    private static final Font MENU_FONT;

    static {
        MENU_TITLE_FONT = new Font("roboto black", 32.0);
        MENU_FONT = new Font("roboto", 22.0);
    }

    private static class MenuItem {

        private final String title;
        private final MenuValue value;
        private final Text text;
        private final MenuSelectionAction selectionAction;
        private final MenuEncoderAction encoderAction;

        private boolean selected;

        private MenuItem(String title, MenuValue value, MenuSelectionAction selectionAction, MenuEncoderAction encoderAction) {
            this.text = new Text();
            this.text.setFont(MENU_FONT);
            this.text.setFill(Color.WHITE);

            this.title = title;
            this.value = value;
            this.selectionAction = selectionAction;
            this.encoderAction = encoderAction;

            if (value == null) {
                this.text.setText(title);
            } else {
                setItemText(value.getValue());
                value.addListener(this::setItemText);
            }
        }

        public Text getText() {
            return text;
        }

        public MenuSelectionAction getSelectionAction() {
            return selectionAction;
        }

        public MenuEncoderAction getEncoderAction() {
            return encoderAction;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            if (selected) {
                this.text.setFill(Color.GREEN);
            } else {
                this.text.setFill(Color.WHITE);
            }
        }

        private void setItemText(String newValue) {
            this.text.setText(String.format("%s [%s]", this.title, newValue));
        }
    }

    private String title = "";
    private Text titleText;

    private BorderPane layout;
    private List<MenuItem> menuItems = new ArrayList<>();
    private MenuItem selectedItem;
    private VBox menuItemBox;

    public MenuDisplay() {

    }

    public MenuDisplay(String title) {
        this.title = title;
    }

    @Override
    public Parent getParentNode() {
        return layout;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (titleText != null) {
            titleText.setText(title);
        }
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        layout = new BorderPane();
        layout.setLayoutX(120);
        layout.setLayoutY(40);
        layout.setMinSize(400, 400);
        layout.setPrefSize(400, 400);
        layout.setMaxSize(400, 400);
        layout.setStyle("-fx-background-color:blue;-fx-padding: 5px;");

        titleText = new Text(0, 0, getTitle());
        titleText.setFont(MENU_TITLE_FONT);
        titleText.setFill(Color.WHITE);
        titleText.setStyle("-fx-underline: true");
        layout.setTop(titleText);
        BorderPane.setAlignment(titleText, Pos.CENTER);

        menuItemBox = new VBox();
        menuItemBox.setStyle("-fx-padding: 10px;");
        layout.setCenter(menuItemBox);
    }

    protected void addMenuItem(String title, MenuSelectionAction selectionAction) {
        addMenuItem(title, null, selectionAction, null);
    }

    protected void addMenuItem(String title, MenuValue value, MenuSelectionAction selectionAction) {
        addMenuItem(title, value, selectionAction, null);
    }

    protected void addMenuItem(String title, MenuValue value, MenuEncoderAction encoderAction) {
        addMenuItem(title, value, null, encoderAction);
    }

    protected void addMenuItem(String title, MenuValue value, MenuSelectionAction selectionAction, MenuEncoderAction encoderAction) {
        MenuItem item = new MenuItem(title, value, selectionAction, encoderAction);
        if (menuItems.isEmpty()) {
            item.setSelected(true);
            selectedItem = item;
        }
        menuItems.add(item);
        menuItemBox.getChildren().add(item.getText());
    }

    @Override
    public void controlButtonReleased() {
        if (selectedItem != null && selectedItem.getSelectionAction() != null) {
            selectedItem.getSelectionAction().onMenuItemSelected();
        }
    }

    @Override
    public void leftRotaryRotated(RotaryEncoderDirection direction) {
        if (!menuItems.isEmpty() && selectedItem != null) {
            int index = menuItems.indexOf(selectedItem);
            if (direction == RotaryEncoderDirection.CW) {
                index++;
            } else {
                index--;
            }
            if (index < 0) {
                index = menuItems.size()-1;
            }
            if (index >= menuItems.size()) {
                index = 0;
            }
            selectedItem.setSelected(false);
            selectedItem = menuItems.get(index);
            selectedItem.setSelected(true);
        }
    }

    @Override
    public void rightRotaryRotated(RotaryEncoderDirection direction) {
        if (selectedItem != null && selectedItem.getEncoderAction() != null) {
            selectedItem.getEncoderAction().onEncoderRotated(direction);
        }
    }
}
