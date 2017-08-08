package copilot.gui.view;

import copilot.gui.control.ItemList;
import copilot.gui.modal.Data;
import javafx.scene.Node;

public class CenterViewFactory {
    ItemList listView;

    public CenterViewFactory() {

    }

    public Node getNode(Data data) {
        return listView;
    }
}
