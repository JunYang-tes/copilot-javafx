package copilot.gui.core;

import copilot.gui.modal.Item;

import java.util.List;
import java.util.Observer;

public interface Processor {
    List<Item> process(String input);

    void onLoading(Observer observer);

    void exec(Item item);
}
