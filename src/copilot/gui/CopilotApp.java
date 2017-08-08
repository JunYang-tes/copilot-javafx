package copilot.gui;

import com.sun.deploy.uitoolkit.impl.fx.ui.FXUIFactory;
import copilot.gui.control.ItemCell;
import copilot.gui.control.ItemList;
import copilot.gui.core.Mock;
import copilot.gui.core.Processor;
import copilot.gui.core.SocketIO;
import copilot.gui.modal.Data;
import copilot.gui.modal.Item;
import copilot.gui.util.HotKey;
import copilot.gui.view.CenterViewFactory;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CopilotApp extends Application {

    private ItemList listView;
    private Processor processor;
    private IntegerProperty selectCounter;
    private List<Item> items;

    public CopilotApp() {

        selectCounter = new SimpleIntegerProperty();
        listView = new ItemList(selectCounter);
        items = new ArrayList<>();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(getParameters().getRaw());
        System.out.println(getParameters().getNamed().keySet());
        String url = getParameters().getNamed().get("url");
        if (url == null) {
            url = "http://localhost:9999";
        }
        processor = new SocketIO(url);

        primaryStage.setTitle("Copilot");
        BorderPane root = new BorderPane();
        root.setId("root");
        TextField textField = new TextField();
        Scene scene = new Scene(root);

        AnchorPane north = new AnchorPane();
//        ImageView loading = new ImageView();
        ProgressIndicator loading = new ProgressIndicator();
        loading.setVisible(false);
        AnchorPane.setLeftAnchor(textField, 0D);
        AnchorPane.setTopAnchor(textField, 0D);
        AnchorPane.setRightAnchor(textField, 0D);
        AnchorPane.setBottomAnchor(textField, 0D);

        AnchorPane.setRightAnchor(loading, 0D);
        AnchorPane.setTopAnchor(loading, 0D);
        AnchorPane.setBottomAnchor(loading, 0D);
        north.getChildren().addAll(textField, loading);
        root.setTop(north);

        final int DOWN = 19, UP = 17, ENTER = 0;
        textField.setId("text-field");
        textField.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            int ordinal = code.ordinal();
            if (ordinal == DOWN) {
                if (selectCounter.get() < listView.getItems().size() - 1)
                    selectCounter.set(selectCounter.get() + 1);
            } else if (ordinal == UP) {
                if (selectCounter.get() > 0)
                    selectCounter.set(selectCounter.get() - 1);
            } else if (ordinal == ENTER) {

                if (items.size() > 0) {
                    primaryStage.setIconified(true);
                    processor.exec(items.get(0));
                }
                if (textField.getText().startsWith("quit")) {
                    System.out.println("Quit");
                    System.exit(0);
                }
            }
            if (e.isAltDown()) {
                System.out.println("Ordinal:" + ordinal);
                if (ordinal <= 33 && ordinal >= 25) {
                    int idx = ordinal - 25;
                    if (idx < items.size()) {
                        primaryStage.setIconified(true);
                        processor.exec(items.get(idx));
                    }
                } else if (ordinal == 38) {
                    //Alt + C
                    listView.getItems().clear();
                    listView.setPrefHeight(0);
                    primaryStage.sizeToScene();
                }
            }
        });
        listView.setPrefHeight(0);
        listView.setId("center");

        JavaFxObservable.valuesOf(textField.textProperty())
                .throttleLast(300, TimeUnit.MILLISECONDS)
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .subscribe(s -> {
                    items = processor.process(s);
                    Platform.runLater(() -> {
                        loading.setVisible(false);
                        listView.upload(items);
                        if (items.size() == 0) {
                            listView.setPrefHeight(0);
                        } else {

                            listView.setPrefHeight(ItemCell.ICON_WIDTH * items.size() + 20);
                        }
                        primaryStage.sizeToScene();
                    });
                });
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets()
                .setAll(CopilotApp.class.getResource("default-style.css").toString());
        root.setCenter(listView);
        primaryStage.setScene(scene);
        primaryStage.setMaxHeight(600);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
        primaryStage.setY(200);
        primaryStage.requestFocus();
        textField.requestFocus();


        final Runnable onShow = () -> {
            if (primaryStage.isFocused()) {
                primaryStage.setIconified(true);
            } else {
                textField.selectAll();
                primaryStage.requestFocus();
                textField.requestFocus();
                primaryStage.show();
            }
        };
        final Runnable showLoading = () -> {
            loading.setVisible(true);
        };
        processor.onLoading((s1, s2) -> {
            Platform.runLater(showLoading);
        });


        HotKey.getInstance()
                .register("alt SPACE", (String combination) -> {
                    Platform.runLater(onShow);

                });

    }


}
