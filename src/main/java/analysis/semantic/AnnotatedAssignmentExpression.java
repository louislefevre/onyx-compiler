package analysis.semantic;

import identifiers.AnnotatedExpressionType;
import identifiers.ObjectType;
import lombok.Getter;

@Getter
public final class AnnotatedAssignmentExpression implements AnnotatedExpression
{
    private final String name;
    private final AnnotatedExpression expression;
    private final ObjectType objectType;
    private final AnnotatedExpressionType expressionType;

    public AnnotatedAssignmentExpression(String name, AnnotatedExpression expression)
    {
        this.name = name;
        this.expression = expression;
        this.objectType = expression.getObjectType();
        this.expressionType = AnnotatedExpressionType.ANNOTATED_ASSIGNMENT_EXPRESSION;
    }
}
