package identifiers;

public enum TokenType
{
    // Data Tokens
    INTEGER_TOKEN,
    DOUBLE_TOKEN,
    STRING_TOKEN,
    IDENTIFIER_TOKEN,

    // Keyword Tokens
    TRUE_KEYWORD_TOKEN,
    FALSE_KEYWORD_TOKEN,
    AND_TOKEN,
    OR_TOKEN,

    // Separator Tokens
    OPEN_BRACE_TOKEN,
    CLOSE_BRACE_TOKEN,
    OPEN_PARENTHESIS_TOKEN,
    CLOSE_PARENTHESIS_TOKEN,

    // Operator Tokens
    PLUS_TOKEN,
    MINUS_TOKEN,
    STAR_TOKEN,
    SLASH_TOKEN,
    PERCENT_TOKEN,
    CARET_TOKEN,
    GREATER_TOKEN,
    GREATER_EQUALS_TOKEN,
    LESS_TOKEN,
    LESS_EQUALS_TOKEN,
    EQUALS_TOKEN,
    EQUALS_EQUALS_TOKEN,
    NOT_TOKEN,
    NOT_EQUALS_TOKEN,

    // Break Tokens
    BAD_TOKEN,
    EOF_TOKEN,

    // Not-parsed Tokens
    COMMENT_TOKEN,
    WHITE_SPACE_TOKEN
}
