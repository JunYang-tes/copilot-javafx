package copilot.gui.modal;

import java.util.ArrayList;
import java.util.List;

public class Data {
    String type;
    List<?> data;

    public Data(){
        data = new ArrayList<>();
    }

    public int size(){
        return data.size();
    }

    public  Data(String type,List<?> data){
        this.type = type;
        this.data = data;
    }
}
