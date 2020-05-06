package errors;

import identifiers.ErrorType;
import identifiers.ObjectType;
import lombok.Getter;
import source.SourceSpan;

import static identifiers.ErrorType.SEMANTIC_ERROR;

@Getter
public final class SemanticError extends Error
{
    private final ErrorType errorType;

    public SemanticError(SourceSpan span, String errorMessage)
    {
        super(span, errorMessage);
        this.errorType = SEMANTIC_ERROR;
    }

    public static String exceptionOccurred(Exception exception)
    {
        StackTraceElement stackTraceElement = exception.getStackTrace()[0];
        int lineNumber = stackTraceElement.getLineNumber();
        String className = stackTraceElement.getClassName();

        String location = String.format("Line %1s: Semantic exception occurred at %2s.", lineNumber, className);
        String message = exception.getMessage();

        return location + "\n" + message;
    }

    public static SemanticError undefinedUnaryOperator(SourceSpan span, String syntax, ObjectType type)
    {
        String message = String.format("Unary operator '%1s' is not defined for type '%2s'.", syntax, type);
        return new SemanticError(span, message);
    }

    public static SemanticError undefinedBinaryOperator(SourceSpan span, String syntax, ObjectType leftType,
                                                        ObjectType rightType)
    {
        String message = String.format("Binary operator '%1s' is not defined for type '%2s' and '%3s'.", syntax,
                                       leftType, rightType);
        return new SemanticError(span, message);
    }

    public static SemanticError undefinedAssignmentOperator(SourceSpan span, String syntax, ObjectType symbolType,
                                                            ObjectType assignmentType)
    {
        String message = String.format("Assignment operator '%1s' is not defined for type '%2s' and '%3s'.", syntax,
                                       symbolType, assignmentType);
        return new SemanticError(span, message);
    }

    public static SemanticError undefinedIdentifier(SourceSpan span, String syntax)
    {
        String message = String.format("Variable '%s' does not exist.", syntax);
        return new SemanticError(span, message);
    }

    public static SemanticError invalidExpressionTypes(SourceSpan span, ObjectType actualType, ObjectType[] targetTypes)
    {
        String message = String.format("Invalid expression type '%1s', expected '%2s'.", actualType, typesToString(targetTypes));
        return new SemanticError(span, message);
    }

    public static String undefinedExpression(String syntax)
    {
        return String.format("Unexpected expression '%s'", syntax);
    }

    public static String undefinedStatement(String syntax)
    {
        return String.format("Unexpected statement '%s'", syntax);
    }

    @Override
    public String toString()
    {
        return "Semantic Error";
    }

    private static String typesToString(ObjectType[] types)
    {
        ObjectType last = types[0];
        for (ObjectType type : types)
            last = type;

        StringBuilder builder = new StringBuilder();
        for (ObjectType type : types)
        {
            builder.append(type.toString());
            if (type != last)
                builder.append("/");
        }

        return builder.toString();
    }
}
