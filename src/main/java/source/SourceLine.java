package source;

import lombok.Getter;

@Getter
public final class SourceLine
{
    private final int start;
    private final int length;
    private final int end;

    public SourceLine(int start, int length)
    {
        this.start = start;
        this.length = length;
        this.end = start + length;
    }
}
