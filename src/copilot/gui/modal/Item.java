package copilot.gui.modal;


public class Item {
    int id;

    private String icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String title;
    private String description;
    private String keyCombination;

    public String getKeyCombination() {
        return keyCombination;
    }

    public void setKeyCombination(String keyCombination) {
        this.keyCombination = keyCombination;
    }

    public Item(){}
    public Item(String icon,String title,String description){
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString(){
        return title;
    }
}
