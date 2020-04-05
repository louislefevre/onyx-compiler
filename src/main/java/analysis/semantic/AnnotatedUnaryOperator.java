package analysis.semantic;

import lombok.Getter;
import identifiers.ObjectType;
import identifiers.OperatorType;
import identifiers.TokenType;

@Getter
public final class AnnotatedUnaryOperator extends AnnotatedOperator
{
    private final TokenType tokenType;
    private final OperatorType operatorType;
    private final ObjectType operandObjectType;
    private final ObjectType resultObjectType;

    public AnnotatedUnaryOperator(TokenType tokenType, OperatorType operatorType, ObjectType operandObjectType,
                                  ObjectType resultObjectType)
    {
        this.tokenType = tokenType;
        this.operatorType = operatorType;
        this.operandObjectType = operandObjectType;
        this.resultObjectType = resultObjectType;
    }

    public AnnotatedUnaryOperator(TokenType tokenType, OperatorType kind, ObjectType operandObjectType)
    {
        this(tokenType, kind, operandObjectType, operandObjectType);
    }
}
