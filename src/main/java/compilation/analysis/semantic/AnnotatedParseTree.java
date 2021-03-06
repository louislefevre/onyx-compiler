package compilation.analysis.semantic;

import lombok.Getter;

/**
 * The ParseTree class is used to hold the root AnnotatedStatement generated by the TypeChecker.
 * <p>
 * When the TypeChecker creates a series of AnnotatedStatement objects, the root is held here in the form of
 * an AnnotatedParseTree. Typically, this will be an AnnotatedSourceStatement object, but any child of the
 * AnnotatedStatement interface is valid.
 *
 * @author Louis Lefevre
 * @version 1.0
 * @since 1.0
 */
@Getter
public final class AnnotatedParseTree
{
    private final AnnotatedStatement statement;

    /**
     * Constructs an AnnotatedParseTree for storing an AnnotatedStatement object.
     * <p>
     * The class is initialised solely with parameters passed to it.
     *
     * @param statement The AnnotatedStatement root object to be stored
     */
    public AnnotatedParseTree(AnnotatedStatement statement)
    {
        this.statement = statement;
    }
}
