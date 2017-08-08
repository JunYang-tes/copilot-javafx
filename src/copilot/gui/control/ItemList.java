package copilot.gui.control;

import copilot.gui.modal.Item;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.List;

public class ItemList extends ListView<Item> {
    public ItemList(IntegerProperty selected, ObservableList<Item> items) {
        super(items);
        this.setCellFactory((view) -> new ItemCell());
        this.itemsProperty().addListener((observable, oldValue, newValue) -> {
            ItemList.this.getSelectionModel().select(0);
        });

        JavaFxObservable.valuesOf(this.itemsProperty())
                .subscribe(s->{
                    System.out.println(s.size());
                });

//        prefHeightProperty().bind(Bindings.size(items).multiply(64));

        selected.addListener((observable, oldValue, newValue) -> {
            ItemList.this.getSelectionModel().select(newValue.intValue());
            ItemList.this.scrollTo(newValue.intValue());
        });
        this.getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    selected.setValue(newValue);
                });
    }

    public ItemList(IntegerProperty selected) {
        this(selected, FXCollections.observableArrayList());
    }

    public void upload(List<Item> list) {
        ObservableList<Item> items = this.getItems();
        items.clear();
        items.addAll(list);
        setPrefHeight(ItemCell.ICON_WIDTH * list.size());
    }
}