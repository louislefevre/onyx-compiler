package analysis.lexical;

import identifiers.TokenType;
import org.junit.jupiter.api.Test;
import utilities.TestHub;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest
{
    @Test
    public void lexerIdentifiesTokens()
    {
        String message = "Failed to return correct inbuilt token - TokenType mismatch at: ";
        HashMap<String, TokenType> tokenTypes = TestHub.tokenTypeCollection();

        tokenTypes.forEach((input, expected) -> {
            TokenType actual = tokenTypeOf(input);
            assertEquals(expected, actual, message + input);
        });
    }

    @Test
    public void lexerReturnsCorrectAmountOfTokens()
    {
        String message = "Failed to return correct token amount - Incorrect amount at: ";
        HashMap<String, Object> binaries = TestHub.binaryCollections();

        binaries.forEach((input, redundant) -> {
            int actual = amountOfTokens(input);
            int expected = 4;
            assertEquals(expected, actual, message + input);
        });
    }

    private static TokenType tokenTypeOf(String input)
    {
        Lexer lexer = TestHub.createLexer(input);
        return lexer.getTokens().get(0).getTokenType();
    }

    private static int amountOfTokens(String input)
    {
        Lexer lexer = TestHub.createLexer(input);
        return lexer.getTokens().size();
    }
}
