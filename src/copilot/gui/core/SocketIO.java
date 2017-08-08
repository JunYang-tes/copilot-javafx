package copilot.gui.core;


import copilot.gui.modal.Item;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketIO implements Processor {
    static Logger logger = Logger.getLogger("SocketIO");

    private static class Emitter extends Observable {
        @Override
        public void notifyObservers(Object arg) {
            setChanged();
            super.notifyObservers(arg);
        }
    }

    Observable loading;

    private Socket client;
    private int seq;
    private List<Item> result;

    private String keyPrefix = "Alt + ";

    public SocketIO(String url) {
        System.out.println("Connect to " + url);
        loading = new Emitter();
        result = new ArrayList<>();
        try {
            client = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client.connect();
        logger.setLevel(Level.ALL);
        /**
         * let getStringByKey = key-> obj-> obj[key] || ""
         * let getTitle = getStringByKey("title")
         * let getText = getStringByKey("text")
         * let getIcon = getStringByKey("icon")
         */
        Function<String, Function<JSONObject, String>> getStringByKey = (key)
                -> (obj) -> {
            try {
                return obj.getString(key);
            } catch (JSONException e) {
            }
            return "";
        };
        Function<JSONObject, String> getTitle = getStringByKey.apply("title");
        Function<JSONObject, String> getText = getStringByKey.apply("text");
        Function<JSONObject, String> getIcon = getStringByKey.apply("icon");

        client.on("loading", (Object... ret) -> {
            loading.notifyObservers();
        });

        client.on("process", (Object... ret) -> {
            JSONObject obj = (JSONObject) ret[0];
            try {
                if ("result".equals(obj.getString("type"))) {
                    JSONArray data = obj.getJSONArray("data");
                    SocketIO.this.result = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = (JSONObject) data.get(i);
                        String title = getTitle.apply(item);
                        String text = getText.apply(item);
                        String icon = getIcon.apply(item);
//                        try {
//                            title = item.getString("title");
//                        } catch (JSONException e) {
//
//                        }
//                        try {
//                            text = item.getString("text");
//                        } catch (JSONException e) {
//
//                        }
                        Item itemTmp = new Item(icon, title, text);
                        itemTmp.setId(i);
                        if (i < 9) {
                            itemTmp.setKeyCombination(this.keyPrefix + (i + 1));
                        }
                        SocketIO.this.result.add(itemTmp);
                    }
                } else {
                    SocketIO.this.result = new ArrayList<>();
                }

            } catch (JSONException e) {
                SocketIO.this.result = new ArrayList<>();
                e.printStackTrace();
            }
            synchronized (SocketIO.this) {
                SocketIO.this.notify();
            }
        });
        client.on("run", ret -> {
        });
        client.on(Socket.EVENT_DISCONNECT, (Object... ret) -> {
            synchronized (SocketIO.this) {
                SocketIO.this.notify();
            }
        });
    }

    public void onLoading(Observer handler) {

        this.loading.addObserver(handler);
    }

    @Override
    public synchronized List<Item> process(String input) {
        logger.fine("Process " + input);
        JSONStringer json = new JSONStringer();
        try {
            client.emit("process", json.object()
                    .key("seq")
                    .value(this.seq++)
                    .key("data")
                    .object()
                    .key("input")
                    .value(input)
                    .endObject()
                    .endObject().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void exec(Item item) {
        try {
            client.emit("run", new JSONStringer().object()
                    .key("seq")
                    .value(this.seq++)
                    .key("data")
                    .object()
                    .key("idx")
                    .value(item.getId())
                    .endObject()
                    .endObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
