package main.java.codeanalysis;

public class Evaluator
{
    private SyntaxTree syntaxTree;

    public Evaluator(SyntaxTree syntaxTree)
    {
        this.syntaxTree = syntaxTree;
    }

    public int evaluate()
    {
        if(!errorsPresent())
        {
            try{
                return EvaluateExpression(this.syntaxTree.getRoot());
            } catch(Exception error) {
                System.out.println(error.getMessage());
            }
        }
        return 0;
    }

    private boolean errorsPresent()
    {
        if(!this.syntaxTree.getDiagnostics().isEmpty())
        {
            this.syntaxTree.showDiagnostics();
            return true;
        }
        return false;
    }

    private int EvaluateExpression(ExpressionSyntax node) throws Exception
    {
        if(node instanceof NumberExpressionSyntax)
            return (int) ((NumberExpressionSyntax) node).getNumberToken().getValue();

        if(node instanceof BinaryExpressionSyntax)
        {
            int left = EvaluateExpression(((BinaryExpressionSyntax) node).getLeft());
            int right = EvaluateExpression(((BinaryExpressionSyntax) node).getRight());
            SyntaxKind tokenKind = ((BinaryExpressionSyntax) node).getOperatorToken().getKind();

            switch(tokenKind)
            {
                case PlusToken:
                    return left + right;
                case MinusToken:
                    return left - right;
                case StarToken:
                    return left * right;
                case SlashToken:
                    return left / right;
                default:
                    throw new Exception(String.format("Unexpected binary operator '%s'", tokenKind));
            }
        }

        if(node instanceof ParenthesizedExpressionSyntax)
            return EvaluateExpression(((ParenthesizedExpressionSyntax) node).getExpression());

        throw new Exception(String.format("Unexpected node '%s'", node.getKind()));
    }
}