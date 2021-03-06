package ui.main;

import compilation.Compiler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.TextFlow;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import source.SourceOutput;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The CodeAreaManager class is used manage the CodeArea where the user inputs their code.
 *
 * @author Louis Lefevre
 * @version 1.0
 * @since 1.0
 */
final class CodeAreaManager
{
    private final CodeArea codeArea;
    private final TextField tabSizeField;
    private int tabSize;

    /**
     * Constructs a CodeAreaManager for managing a CodeArea object.
     * <p>
     * The TextField containing the size of the tabs, as defined by the user, is read every time the user makes
     * changes to it. These changes are reflected in tabSize.
     *
     * @param codeArea The CodeArea to be managed
     * @param tabSizeField The TextField defining the size of tabs
     * @param tabSize The initial tab size
     */
    public CodeAreaManager(CodeArea codeArea, TextField tabSizeField, int tabSize)
    {
        this.codeArea = codeArea;
        this.tabSizeField = tabSizeField;
        this.tabSize = tabSize;
        initialiseCodeArea();
    }

    /**
     * Compiles the code currently in the CodeArea, and outputs the result.
     * <p>
     * The code is retrieved from the CodeArea and converted into a String, which is then compiled by the Compiler,
     * and the output being returned in the form of a TextFlow object.
     * <p>
     * The parse tree and symbol table are printed to the console when ever this method runs.
     *
     * @return A TextFlow object containing the output
     */
    public TextFlow compileInput()
    {
        String input = codeArea.getText();

        Compiler compiler = new Compiler();
        SourceOutput output = compiler.compile(input);

        compiler.printParseTree();
        compiler.printSymbolTable();
        System.out.println();

        return output.getOutput();
    }

    /**
     * Returns the current line and column of the caret, formatted as a String.
     *
     * @return The current position of the caret
     */
    public String getCaretPosition()
    {
        int lineNum = codeArea.getCurrentParagraph() + 1;
        int columnNum = codeArea.getCaretColumn() + 1;
        return String.format("%s:%s", lineNum, columnNum);
    }

    /**
     * Updates the tab size to reflect the contents of the tabSizeField.
     * <p>
     * The text is retrieved from the Label containing the user-defined tab size. If the input is not an integer,
     * or is larger than 99/less than 1, the tab size is not updated and the tabSizeField text is reset back to its
     * last value.
     */
    public void updateTabSize()
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

    /**
     * Limits how many characters can be displayed in the tab size Label.
     * <p>
     * If the users input is greater than the specified limit, the excess text is removed from the Label.
     *
     * @param charLimit the maximum amount of characters allowed
     */
    public void limitTabSize(int charLimit)
    {
        if (tabSizeField.getText().length() > charLimit)
        {
            String subText = tabSizeField.getText().substring(0, charLimit);
            tabSizeField.setText(subText);
            tabSizeField.positionCaret(charLimit);
        }
    }

    private void initialiseCodeArea()
    {
        addLineNumbers();
        highlightSyntax();
        updateInputMap();
        addEventHandlers();
    }

    private void addLineNumbers()
    {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    }

    private void highlightSyntax()
    {
        SyntaxMatcher.highlightSyntax(codeArea);
    }

    private void updateInputMap()
    {
        InputMap<KeyEvent> inputMap = InputMap.consume(
                EventPattern.keyPressed(KeyCode.TAB),
                e -> codeArea.replaceSelection(" ".repeat(tabSize))
        );
        Nodes.addInputMap(codeArea, inputMap);
    }

    private void addEventHandlers()
    {
        codeArea.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent ->
        {
            int caretPosition = codeArea.getCaretPosition();
            int currentParagraphIndex = codeArea.getCurrentParagraph();

            if (keyEvent.getCode() == KeyCode.ENTER)
            {
                String previousParagraph = codeArea.getParagraph(currentParagraphIndex - 1).getSegments().get(0);

                if (previousParagraph.equals("{"))
                    autoCompleteBraces(caretPosition);

                autoCompleteWhitespace(caretPosition, previousParagraph);
            }
        });

        codeArea.addEventHandler(KeyEvent.KEY_TYPED, keyEvent ->
        {
            int caretPosition = codeArea.getCaretPosition();

            if (keyEvent.getCharacter().equals("("))
                autoCompleteParentheses(caretPosition);
        });
    }

    private void autoCompleteWhitespace(int caretPosition, String previousParagraph)
    {
        Pattern whiteSpace = Pattern.compile("^\\s+");
        Matcher matcher = whiteSpace.matcher(previousParagraph);
        if (matcher.find())
            codeArea.insertText(caretPosition, matcher.group());
    }

    private void autoCompleteBraces(int caretPosition)
    {
        String brace = System.lineSeparator() + "}";
        codeArea.insertText(caretPosition, brace);
        codeArea.insertText(caretPosition, " ".repeat(tabSize));
    }

    private void autoCompleteParentheses(int caretPosition)
    {
        String paren = ")";
        codeArea.insertText(caretPosition, paren);
        codeArea.moveTo(caretPosition);
    }

    private static class SyntaxMatcher
    {
        /* Sourced from https://github.com/FXMisc/RichTextFX */
        private static final String[] KEYWORDS = new String[]{"if", "else", "loop", "from", "to", "and", "or"};
        private static final String[] OPERATORS = new String[]{"\\+", "\\-", "\\*", "\\/", "\\%", "\\^", "\\>", "\\<", "\\=", "\\!"};
        private static final String[] BOOLEANS = new String[]{"true", "false"};

        private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        private static final String BOOLEAN_PATTERN = "\\b(" + String.join("|", BOOLEANS) + ")\\b";
        private static final String OPERATOR_PATTERN = "(" + String.join("|", OPERATORS) + ")";
        private static final String PAREN_PATTERN = "[()]";
        private static final String BRACE_PATTERN = "[{}]";
        private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
        private static final String COMMENT_PATTERN = "#[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

        private static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                + "|(?<BOOLEAN>" + BOOLEAN_PATTERN + ")"
                + "|(?<OPERATOR>" + OPERATOR_PATTERN + ")"
                + "|(?<PAREN>" + PAREN_PATTERN + ")"
                + "|(?<BRACE>" + BRACE_PATTERN + ")"
                + "|(?<STRING>" + STRING_PATTERN + ")"
                + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        );

        private static void highlightSyntax(CodeArea codeArea)
        {
            codeArea.multiPlainChanges()
                    .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText().toLowerCase())));
        }

        private static StyleSpans<Collection<String>> computeHighlighting(String text)
        {
            int lastKwEnd = 0;
            Matcher matcher = PATTERN.matcher(text);
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

            while (matcher.find())
            {
                String styleClass =
                        matcher.group("KEYWORD") != null ? "keyword" :
                        matcher.group("BOOLEAN") != null ? "boolean" :
                        matcher.group("OPERATOR") != null ? "operator" :
                        matcher.group("PAREN") != null ? "paren" :
                        matcher.group("BRACE") != null ? "brace" :
                        matcher.group("STRING") != null ? "string" :
                        matcher.group("COMMENT") != null ? "comment" :
                        null;
                assert styleClass != null;

                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
            spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

            return spansBuilder.create();
        }
    }
}
