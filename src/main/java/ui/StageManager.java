package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class StageManager
{
    public void startMain(Stage stage) throws IOException
    {
        String title = "Onyx Compiler";
        String path = "/fxml/main.fxml";
        String styleSheet = "/css/syntax.css";
        showStage(stage, title, path, styleSheet);
    }

    public void startRepl(Stage stage) throws IOException
    {
        String title = "Onyx REPL";
        String path = "/fxml/repl.fxml";
        showStage(stage, title, path, null);
    }

    private void showStage(Stage stage, String title, String path, String styleSheet) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Scene scene = new Scene(root);

        if (styleSheet != null)
            scene.getStylesheets().add(StageManager.class.getResource(styleSheet).toExternalForm());

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}