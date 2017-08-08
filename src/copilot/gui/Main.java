package copilot.gui;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;

import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        PlatformImpl.setTaskbarApplication(false);
        Application.launch(CopilotApp.class,args);
    }
}
