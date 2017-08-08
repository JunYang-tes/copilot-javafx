package copilot.gui.core;

import copilot.gui.modal.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class Mock implements Processor{
    @Override
    public List<Item> process(String input) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Item> ret=new ArrayList<>();
        for(int i=0;i<10*Math.random();i++){
            Item item = new Item("","Test "+Math.random(),Math.random()+"");
            ret.add(item);
        }
        return ret;
    }

    @Override
    public void onLoading(Observer observer) {

    }

    @Override
    public void exec(Item item) {

    }
}
