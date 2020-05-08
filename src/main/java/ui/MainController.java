package ui;

import compilation.Pipeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import source.SourceOutput;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController
{
    @FXML private BorderPane mainParent;

    @FXML private CodeArea inputCode;
    @FXML private TextFlow outputText;
    @FXML private Label infoLabel;
    @FXML private TextField tabSizeField;
    private int tabSize;

    @FXML
    void initialize()
    {
        tabSize = 4;
        initialiseCodeArea();
        refreshLineInfo();
    }

    @FXML
    void openRepl() throws IOException
    {
        SceneManager sceneManager = new SceneManager();
        sceneManager.startReplStage();
    }

    @FXML
    void runSource()
    {
        runSourceCode(inputCode, outputText);
    }

    @FXML
    void openHelpInfo() throws IOException
    {
        openGithubLink();
    }

    @FXML
    void updateLineInfo()
    {
        refreshLineInfo();
    }

    @FXML
    void updateTabSize()
    {
        refreshTabSize();
    }

    @FXML
    void limitInputSize()
    {
        int limit = 2;
        if (tabSizeField.getText().length() > limit)
        {
            String s = tabSizeField.getText().substring(0, limit);
            tabSizeField.setText(s);
            tabSizeField.positionCaret(limit);
        }
    }

    private File currentFile;
    @FXML
    void openFileChooser()
    {
        Stage stage = (Stage) mainParent.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.txt", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);

        if(file != null)
        {
            if(file.canRead())
            {
                try{
                    String fileText = Files.readString(file.toPath());
                    inputCode.replaceText(fileText);
                    currentFile = file;
                }
                catch(IOException exception)
                {
                    System.out.println(exception.getMessage());
                }
            }
        }
    }

    @FXML
    void saveFile()
    {
        String text = inputCode.getText();

        File file = currentFile;

        if(file != null)
        {
            if(file.exists() && file.isFile() && file.canWrite())
            {
                try
                {
                    PrintWriter writer = new PrintWriter(file);
                    writer.print(text);
                    writer.close();
                }
                catch (IOException exception)
                {
                    System.out.println(exception.getMessage());
                }
            }
            else
            {
                saveFileAs();
            }
        }
        else
        {
            saveFileAs();
        }

    }

    @FXML
    void saveFileAs()
    {
        String text = inputCode.getText();

        Stage stage = (Stage) mainParent.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(stage);

        if(file != null)
        {
            try
            {
                PrintWriter writer = new PrintWriter(file + ".txt");
                writer.print(text);
                writer.close();
                currentFile = file;
            }
            catch (IOException exception)
            {
                System.out.println(exception.getMessage());
            }
        }

    }

    private void refreshTabSize()
    {
        String text = tabSizeField.getText();
        int size;

        try
        {
            size = Integer.parseInt(text);
            if (size < 1 || size > 99)
                size = tabSize;
        }
        catch (NumberFormatException exception)
        {
            size = tabSize;
            System.out.println(exception.getMessage());
        }

        tabSize = size;
        String sizeText = String.valueOf(size);
        tabSizeField.setText(sizeText);
        tabSizeField.positionCaret(sizeText.length());
    }

    private void refreshLineInfo()
    {
        int lineNum = inputCode.getCurrentParagraph() + 1;
        int columnNum = inputCode.getCaretColumn() + 1;
        String info = String.format("%s:%s", lineNum, columnNum);
        infoLabel.setText(info);
    }

    private void initialiseCodeArea()
    {
        inputCode.setParagraphGraphicFactory(LineNumberFactory.get(inputCode));

        InputMap<KeyEvent> im = InputMap.consume(
                EventPattern.keyPressed(KeyCode.TAB),
                e -> inputCode.replaceSelection(" ".repeat(Math.max(0, tabSize)))
        );
        Nodes.addInputMap(inputCode, im);

        final Pattern whiteSpace = Pattern.compile("^\\s+");
        inputCode.addEventHandler(KeyEvent.KEY_PRESSED, KE ->
        {
            if (KE.getCode() == KeyCode.ENTER)
            {
                int caretPosition = inputCode.getCaretPosition();
                int currentParagraph = inputCode.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(inputCode.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find()) Platform.runLater(() -> inputCode.insertText(caretPosition, m0.group()));
            }
        });
    }

    private void runSourceCode(CodeArea inputCode, TextFlow outputText)
    {
        outputText.getChildren().clear();
        String input = inputCode.getText();

        if (input.isBlank())
            return;

        SourceOutput sourceOutput = readInput(input);

        if (sourceOutput.getResult() == null) // Avoids NullPointerException if input is invalid
            return;

        outputText.getChildren().addAll(sourceOutput.getTextResult());
    }

    private void openGithubLink() throws IOException
    {
        // Only works on Linux
        String url = "https://github.com/louislefevre/onyx-compiler";
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("xdg-open " + url);
    }

    private SourceOutput readInput(String input)
    {
        input += System.getProperty("line.separator"); // Adds extra line separator at end to avoid collision with EOF
        input = input.replaceAll("\011", ""); // Ignores horizontal tabs, breaks line separators otherwise
        String[] lines = input.split(System.getProperty("line.separator")); // Splits each line up to be run individually

        List<String> linesList = new ArrayList<>();
        for (String line : lines)
            if (!line.isBlank()) // Only adds non-blank lines
                linesList.add(line);

        Pipeline pipeline = new Pipeline();
        for (String line : linesList)
            if (!line.isBlank())
                pipeline.compile(line);

        return pipeline.compile(input);
    }
}
