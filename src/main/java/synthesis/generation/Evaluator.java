package synthesis.generation;

import analysis.semantic.*;
import errors.ErrorHandler;
import identifiers.OperatorType;

public final class Evaluator
{
    private final AnnotatedParseTree annotatedParseTree;

    public Evaluator(TypeChecker typeChecker)
    {
        this.annotatedParseTree = typeChecker.getAnnotatedParseTree();
    }

    public Object evaluate()
    {
        return this.evaluateErrors(this.annotatedParseTree.getExpression());
    }

    private Object evaluateErrors(BoundExpression expression)
    {
        if(!ErrorHandler.errorsPresent())
        {
            try
            {
                return evaluateExpression(expression);
            }
            catch(Exception error)
            {
                System.out.println(error.getMessage());
            }
        }
        return null;
    }

    private Object evaluateExpression(BoundExpression node) throws Exception
    {
        if(node instanceof BoundLiteralExpression)
            return this.evaluateNumberExpression(node);

        if(node instanceof BoundUnaryExpression)
            return this.evaluateUnaryExpression(node);

        if(node instanceof BoundBinaryExpression)
            return this.evaluateBinaryExpression(node);

        throw new Exception(String.format("Unexpected node '%s'", node.getObjectType()));
    }

    private Object evaluateNumberExpression(BoundExpression node)
    {
        return ((BoundLiteralExpression) node).getValue();
    }

    private Object evaluateUnaryExpression(BoundExpression node) throws Exception
    {
        Object operand = this.evaluateExpression(((BoundUnaryExpression) node).getOperand());
        OperatorType operatorType = ((BoundUnaryExpression) node).getOperator().getOperatorType();

        switch(operatorType)
        {
            case IDENTITY_OPERATOR:
                return operand;
            case NEGATION_OPERATOR:
                return -(int)operand;
            case LOGIC_NEGATION_OPERATOR:
                return !(boolean)operand;
            default:
                throw new Exception(String.format("Unexpected unary operator '%s'", operatorType));
        }
    }

    private Object evaluateBinaryExpression(BoundExpression node) throws Exception
    {
        Object left = this.evaluateExpression(((BoundBinaryExpression) node).getLeftTerm());
        Object right = this.evaluateExpression(((BoundBinaryExpression) node).getRightTerm());
        OperatorType tokenKind = ((BoundBinaryExpression) node).getOperator().getOperatorType();

        switch(tokenKind)
        {
            case ADDITION_OPERATOR:
                return (int)left + (int)right;
            case SUBTRACTION_OPERATOR:
                return (int)left - (int)right;
            case MULTIPLICATION_OPERATOR:
                return (int)left * (int)right;
            case DIVISION_OPERATOR:
                return (int)left / (int)right;
            case POWER_OPERATOR:
                return (int)Math.pow((int)left, (int)right);
            case MODULO_OPERATOR:
                return (int)left % (int)right;
            case AND_OPERATOR:
                return (boolean)left && (boolean)right;
            case OR_OPERATOR:
                return (boolean)left || (boolean)right;
            case EQUALS_EQUALS_OPERATOR:
                return left == right;
            case NOT_EQUALS_OPERATOR:
                return left != right;
            case GREATER_OPERATOR:
                return (int)left > (int)right;
            case LESS_OPERATOR:
                return (int)left < (int)right;
            case GREATER_EQUALS_OPERATOR:
                return (int)left >= (int)right;
            case LESS_EQUALS_OPERATOR:
                return (int)left <= (int)right;
            default:
                throw new Exception(String.format("Unexpected binary operator '%s'", tokenKind));
        }
    }
}