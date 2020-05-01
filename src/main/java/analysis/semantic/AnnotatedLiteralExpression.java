package analysis.semantic;

import identifiers.AnnotatedExpressionType;
import identifiers.ObjectType;
import lombok.Getter;

@Getter
public final class AnnotatedLiteralExpression implements AnnotatedExpression
{
    private final Object value;
    private final AnnotatedExpressionType expressionType;
    private final ObjectType objectType;

    public AnnotatedLiteralExpression(Object value)
    {
        this.value = value;
        this.expressionType = AnnotatedExpressionType.ANNOTATED_LITERAL_EXPRESSION;
        this.objectType = typeOf(value);
    }

    private static ObjectType typeOf(Object object)
    {
        if (object instanceof Integer)
            return ObjectType.INTEGER_OBJECT;
        else if (object instanceof Double)
            return ObjectType.DOUBLE_OBJECT;
        else if (object instanceof String)
            return ObjectType.STRING_OBJECT;
        else if (object instanceof Boolean)
            return ObjectType.BOOLEAN_OBJECT;
        else
            return ObjectType.NULL_OBJECT;
    }
}
