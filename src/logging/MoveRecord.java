package logging;

public class MoveRecord {
    private final int row1;     // 1..9
    private final int col1;     // 1..9
    private final int newVal;   // 0..9
    private final int prevVal;  // 0..9

    public MoveRecord(int row1, int col1, int newVal, int prevVal) {
        if (row1 < 1 || row1 > 9 || col1 < 1 || col1 > 9) {
            throw new IllegalArgumentException("Row/Col must be 1..9");
        }
        if (newVal < 0 || newVal > 9 || prevVal < 0 || prevVal > 9) {
            throw new IllegalArgumentException("Values must be 0..9");
        }
        this.row1 = row1;
        this.col1 = col1;
        this.newVal = newVal;
        this.prevVal = prevVal;
    }

    public int row1() { return row1; }
    public int col1() { return col1; }
    public int newVal() { return newVal; }
    public int prevVal() { return prevVal; }

    public String toLogLine() {
        return "(" + row1 + "," + col1 + "," + newVal + "," + prevVal + ")";
    }

    public static MoveRecord parse(String line) {
        if (line == null) throw new IllegalArgumentException("Null log line.");

        String s = line.trim();
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1).trim();
        }

        String[] parts = s.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid log format: " + line);
        }

        try {
            int r = Integer.parseInt(parts[0].trim());
            int c = Integer.parseInt(parts[1].trim());
            int nv = Integer.parseInt(parts[2].trim());
            int pv = Integer.parseInt(parts[3].trim());
            return new MoveRecord(r, c, nv, pv);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numbers in log: " + line);
        }
    }
}
