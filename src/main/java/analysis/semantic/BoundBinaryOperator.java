package analysis.semantic;

import identifiers.OperatorType;
import identifiers.ObjectType;
import identifiers.TokenType;
import lombok.Getter;

public final class BoundBinaryOperator
{
    @Getter private final TokenType tokenType;
    @Getter private final OperatorType operatorType;
    @Getter private final ObjectType leftObjectType;
    @Getter private final ObjectType rightObjectType;
    @Getter private final ObjectType resultObjectType;

    private BoundBinaryOperator(TokenType tokenType, OperatorType operatorType, ObjectType leftObjectType, ObjectType rightObjectType, ObjectType resultObjectType)
    {
        this.tokenType = tokenType;
        this.operatorType = operatorType;
        this.leftObjectType = leftObjectType;
        this.rightObjectType = rightObjectType;
        this.resultObjectType = resultObjectType;
    }

    private BoundBinaryOperator(TokenType tokenType, OperatorType operatorType, ObjectType operandObjectType, ObjectType resultObjectType)
    {
        this(tokenType, operatorType, operandObjectType, operandObjectType, resultObjectType);
    }

    private BoundBinaryOperator(TokenType tokenType, OperatorType operatorType, ObjectType objectType)
    {
        this(tokenType, operatorType, objectType, objectType, objectType);
    }

    private static final BoundBinaryOperator[] operators =
    {
        new BoundBinaryOperator(TokenType.PLUS_TOKEN, OperatorType.ADDITION_OPERATOR, ObjectType.INTEGER_OBJECT),
        new BoundBinaryOperator(TokenType.MINUS_TOKEN, OperatorType.SUBTRACTION_OPERATOR, ObjectType.INTEGER_OBJECT),
        new BoundBinaryOperator(TokenType.STAR_TOKEN, OperatorType.MULTIPLICATION_OPERATOR, ObjectType.INTEGER_OBJECT),
        new BoundBinaryOperator(TokenType.SLASH_TOKEN, OperatorType.DIVISION_OPERATOR, ObjectType.INTEGER_OBJECT),

        new BoundBinaryOperator(TokenType.EQUALS_TOKEN, OperatorType.EQUALS_OPERATOR, ObjectType.BOOLEAN_OBJECT),
        new BoundBinaryOperator(TokenType.NOT_EQUALS_TOKEN, OperatorType.NOT_EQUALS_OPERATOR, ObjectType.BOOLEAN_OBJECT),
        new BoundBinaryOperator(TokenType.AND_TOKEN, OperatorType.AND_OPERATOR, ObjectType.BOOLEAN_OBJECT),
        new BoundBinaryOperator(TokenType.OR_TOKEN, OperatorType.OR_OPERATOR, ObjectType.BOOLEAN_OBJECT),

        new BoundBinaryOperator(TokenType.EQUALS_TOKEN, OperatorType.EQUALS_OPERATOR, ObjectType.INTEGER_OBJECT, ObjectType.BOOLEAN_OBJECT),
        new BoundBinaryOperator(TokenType.NOT_EQUALS_TOKEN, OperatorType.NOT_EQUALS_OPERATOR, ObjectType.INTEGER_OBJECT, ObjectType.BOOLEAN_OBJECT)
    };

    public static BoundBinaryOperator bind(TokenType tokenType, ObjectType leftObjectType, ObjectType rightObjectType)
    {
        for(BoundBinaryOperator operator : operators)
        {
            if(operator.getTokenType() == tokenType && operator.getLeftObjectType() == leftObjectType && operator.getRightObjectType() == rightObjectType)
                return operator;
        }

        return null;
    }
}
