package rs.raf.calculator.ast;

/* Location range in a source file.  Inclusive.  */
public record Location(Position start, Position end) {
    /** Constructs the range that spans both {@code this} and {@code other}.  */
    public Location span(Location other) {
	var start = other.start().lessThan(start()) ? other.start() : start();
	var stop = other.end().lessThan(end()) ? end() : other.end();

	return new Location(start, stop);
    }

    public static Location
    makeBuiltinLocation ()
    {
        var start = new Position (1, 0);
        return new Location (start, start);
    }
}
