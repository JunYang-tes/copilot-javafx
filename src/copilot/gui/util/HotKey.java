package copilot.gui.util;

import com.tulskiy.keymaster.common.Provider;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javax.swing.*;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class HotKey extends Observable {
    private static HotKey instance = new HotKey();
    private Provider provider;

    private HotKey() {
        provider = Provider.getCurrentProvider(false);
    }


    public void register(final String keyStroke, OnKeyCom callback) {
        this.provider.register(KeyStroke.getKeyStroke(keyStroke), hotKey -> {
            System.out.println(hotKey.keyStroke.toString());
            callback.call(keyStroke);
        });
    }

    public static HotKey getInstance() {
        return instance;
    }

    public interface OnKeyCom {
        void call(String keyStroke);
    }

}
