package copilot.gui.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import copilot.gui.modal.Item;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.concurrent.ExecutionException;

import static javafx.scene.paint.Color.*;

public class ItemCell extends ListCell<Item> {
    public static final int ICON_WIDTH = 64;
    private static LoadingCache<String, Image> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<String, Image>() {
                @Override
                public Image load(String s) throws Exception {
                    return new Image(s);
                }
            });

    private ImageView icon;
    private Label title;
    private Label description;
    private Label keyCombination;
    private AnchorPane root;

    public ItemCell() {
        root = new AnchorPane();
        root.setPrefHeight(ICON_WIDTH);
        icon = new ImageView();
        icon.setFitHeight(ICON_WIDTH);
        icon.setFitWidth(ICON_WIDTH);
        AnchorPane.setLeftAnchor(icon, 0D);
        AnchorPane.setTopAnchor(icon, 0D);
        AnchorPane.setLeftAnchor(icon, 0D);
        keyCombination = new Label();
        AnchorPane.setRightAnchor(keyCombination, 0D);
        AnchorPane.setBottomAnchor(keyCombination, 0D);

        VBox box = new VBox();
        AnchorPane.setRightAnchor(box, 0D);
        AnchorPane.setTopAnchor(box, 0d);
        AnchorPane.setLeftAnchor(box, 64d);
        AnchorPane.setBottomAnchor(box, 0d);
        title = new Label();
        description = new Label();
        VBox.setVgrow(description, Priority.ALWAYS);
        box.getChildren().addAll(title, description);
        box.getStyleClass().add("cell-right");
        root.getChildren().addAll(icon, box, keyCombination);
        setGraphic(root);
        title.getStyleClass().add("cell-title");
        description.getStyleClass().add("cell-text");
    }

    @Override
    public void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            keyCombination.setText(item.getKeyCombination());
            description.setText(item.getDescription());
            title.setText(item.getTitle());
            String iconUrl = item.getIcon();
            if (iconUrl.startsWith("file://") || iconUrl.startsWith("http://")) {
                try {
                    icon.setImage(
                            cache.get(item.getIcon())
                    );
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    icon.setImage(null);
                }
            } else {
                icon.setImage(null);
            }
            setGraphic(root);
        } else {
            setGraphic(null);
        }
    }
}
