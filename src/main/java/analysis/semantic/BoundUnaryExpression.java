package analysis.semantic;

import symbols.NodeType;
import lombok.Getter;
import symbols.ObjectType;

public final class BoundUnaryExpression extends BoundExpression
{
    @Getter private final BoundUnaryOperator operator;
    @Getter private final BoundExpression operand;

    public BoundUnaryExpression(BoundUnaryOperator operator, BoundExpression operand)
    {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public NodeType getNodeType()
    {
        return NodeType.UnaryExpression;
    }

    @Override
    public ObjectType getObjectType()
    {
        return this.operator.getResultObjectType();
    }
}
