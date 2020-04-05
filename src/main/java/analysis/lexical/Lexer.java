package analysis.lexical;

import errors.ErrorHandler;
import errors.LexicalError;
import identifiers.TokenType;
import util.Utilities;

import java.util.ArrayList;
import java.util.List;

public final class Lexer
{
    private final String inputText;
    private final ErrorHandler errorHandler;
    private int position;

    public Lexer(String inputText, ErrorHandler errorHandler)
    {
        this.inputText = inputText;
        this.errorHandler = errorHandler;
        this.position = 0;
    }

    public List<Token> getTokens()
    {
        return this.lexTokens();
    }

    private List<Token> lexTokens()
    {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do
        {
            token = this.nextToken();
            if (token.getTokenType() != TokenType.BAD_TOKEN &&
                token.getTokenType() != TokenType.WHITE_SPACE_TOKEN)
                tokens.add(token);
        } while (token.getTokenType() != TokenType.EOF_TOKEN);
        return tokens;
    }

    private Token nextToken()
    {
        if (this.position >= this.inputText.length())
            return this.endToken();
        else if (Utilities.isDigit(this.currentChar()))
            return this.numberToken();
        else if (Utilities.isLetter(this.currentChar()))
            return this.letterToken();
        else if (Utilities.isWhitespace(this.currentChar()))
            return this.whitespaceToken();
        return this.symbolToken();
    }

    private Token endToken()
    {
        return new Token(TokenType.EOF_TOKEN, "\0", this.position);
    }

    private Token numberToken()
    {
        int startPos = this.position;
        int value = 0;

        while (Utilities.isDigit(this.currentChar()))
            this.currentPositionThenNext(1);

        String text = this.inputText.substring(startPos, this.position);

        if (Utilities.isParsable(text))
        {
            value = Integer.parseInt(text);
        }
        else
        {
            LexicalError error = LexicalError.invalidInt(text, startPos, this.position - startPos);
            this.errorHandler.addError(error);
        }

        return new Token(TokenType.NUMBER_TOKEN, text, value, startPos);
    }

    private Token letterToken()
    {
        int startPos = this.position;

        // Checks if a number token is present before the letter token.
        int i = -1;
        while (Utilities.isWhitespace(this.peek(i))) i--;
        if (Utilities.isDigit(this.peek(i))) return this.badToken();

        while (Utilities.isLetter(this.currentChar()))
            this.nextPosition();

        String text = this.inputText.substring(startPos, this.position);
        TokenType tokenType = getKeywordToken(text);
        Object tokenValue = getKeywordValue(tokenType);

        return new Token(tokenType, text, tokenValue, startPos);
    }

    private Token whitespaceToken()
    {
        int startPos = this.position;

        while (Utilities.isWhitespace(this.currentChar()))
            this.nextPosition();

        String text = this.inputText.substring(startPos, this.position);

        return new Token(TokenType.WHITE_SPACE_TOKEN, text, startPos);
    }

    private Token symbolToken()
    {
        switch (this.currentChar())
        {
            case Syntax.PLUS:
                return new Token(TokenType.PLUS_TOKEN, this.currentPositionThenNext(1));
            case Syntax.MINUS:
                return new Token(TokenType.MINUS_TOKEN, this.currentPositionThenNext(1));
            case Syntax.STAR:
                return new Token(TokenType.STAR_TOKEN, this.currentPositionThenNext(1));
            case Syntax.SLASH:
                return new Token(TokenType.SLASH_TOKEN, this.currentPositionThenNext(1));
            case Syntax.CARET:
                return new Token(TokenType.CARET_TOKEN, this.currentPositionThenNext(1));
            case Syntax.PERCENT:
                return new Token(TokenType.PERCENT_TOKEN, this.currentPositionThenNext(1));
            case Syntax.OPEN_PARENTHESIS:
                return new Token(TokenType.OPEN_PARENTHESIS_TOKEN, this.currentPositionThenNext(1));
            case Syntax.CLOSE_PARENTHESIS:
                return new Token(TokenType.CLOSE_PARENTHESIS_TOKEN, this.currentPositionThenNext(1));
            case Syntax.AMPERSAND:
                if (this.nextChar().equals(Syntax.AMPERSAND))
                    return new Token(TokenType.AND_TOKEN, this.currentPositionThenNext(2));
                break;
            case Syntax.PIPE:
                if (this.nextChar().equals(Syntax.PIPE))
                    return new Token(TokenType.OR_TOKEN, this.currentPositionThenNext(2));
                break;
            case Syntax.EQUALS:
                if (this.nextChar().equals(Syntax.EQUALS))
                    return new Token(TokenType.EQUALS_EQUALS_TOKEN, this.currentPositionThenNext(2));
                return new Token(TokenType.EQUALS_TOKEN, this.currentPositionThenNext(1));
            case Syntax.NOT:
                if (this.nextChar().equals(Syntax.EQUALS))
                    return new Token(TokenType.NOT_EQUALS_TOKEN, this.currentPositionThenNext(2));
                return new Token(TokenType.NOT_TOKEN, this.position++);
            case Syntax.GREATER:
                if (this.nextChar().equals(Syntax.EQUALS))
                    return new Token(TokenType.GREATER_EQUALS_TOKEN, this.currentPositionThenNext(2));
                return new Token(TokenType.GREATER_TOKEN, this.position++);
            case Syntax.LESS:
                if (this.nextChar().equals(Syntax.EQUALS))
                    return new Token(TokenType.LESS_EQUALS_TOKEN, this.currentPositionThenNext(2));
                return new Token(TokenType.LESS_TOKEN, this.position++);
        }
        return this.badToken();
    }

    private Token badToken()
    {
        LexicalError error = LexicalError.badCharacter(this.currentChar(), this.position, 1);
        this.errorHandler.addError(error);
        return new Token(TokenType.BAD_TOKEN,
                         inputText.substring(Utilities.minimumZero(this.position - 1), this.position),
                         this.currentPositionThenNext(1));
    }

    private String currentChar()
    {
        return this.peek(0);
    }

    private String nextChar()
    {
        return this.peek(1);
    }

    private String peek(int offset)
    {
        int index = this.position + offset;

        if (index >= this.inputText.length() || index < 0)
            return Syntax.ESCAPE;
        return Character.toString(this.inputText.charAt(index));
    }

    private void nextPosition()
    {
        this.position++;
    }

    private int currentPositionThenNext(int increment)
    {
        int currentPos = this.position;
        this.position += increment;
        return currentPos;
    }

    private static TokenType getKeywordToken(String text)
    {
        switch (text)
        {
            case Syntax.TRUE:
                return TokenType.TRUE_KEYWORD_TOKEN;
            case Syntax.FALSE:
                return TokenType.FALSE_KEYWORD_TOKEN;
            default:
                return TokenType.IDENTIFIER_KEYWORD_TOKEN;
        }
    }

    private static Object getKeywordValue(TokenType type)
    {
        switch (type)
        {
            case TRUE_KEYWORD_TOKEN:
                return true;
            case FALSE_KEYWORD_TOKEN:
                return false;
            default:
                return null;
        }
    }
}
