package compilation.analysis.semantic;

import lombok.Getter;
import types.AnnotatedExpressionType;
import types.ObjectType;

import static types.AnnotatedExpressionType.ANNOTATED_BINARY_EXPRESSION;

@Getter
public final class AnnotatedBinaryExpression implements AnnotatedExpression
{
    private final AnnotatedExpression leftOperand;
    private final AnnotatedBinaryOperator operator;
    private final AnnotatedExpression rightOperand;
    private final AnnotatedExpressionType expressionType;
    private final ObjectType objectType;

    public AnnotatedBinaryExpression(AnnotatedExpression leftOperand, AnnotatedBinaryOperator operator,
                                     AnnotatedExpression rightOperand)
    {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
        this.expressionType = ANNOTATED_BINARY_EXPRESSION;
        this.objectType = operator.getResultObjectType();
    }
}