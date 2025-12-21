package logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MoveLog {
    private final Path logPath;

    public MoveLog(Path logPath) {
        this.logPath = logPath;
    }

    public Path getLogPath() {
        return logPath;
    }

    public void ensureExists() throws IOException {
        Files.createDirectories(logPath.getParent());
        if (!Files.exists(logPath)) {
            Files.writeString(logPath, "", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    public void clear() throws IOException {
        ensureExists();
        Files.writeString(logPath, "", StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void append(MoveRecord rec) throws IOException {
        ensureExists();
        String line = rec.toLogLine() + "\n";
        Files.writeString(logPath, line, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public List<String> readAllLines() throws IOException {
        ensureExists();
        List<String> lines = Files.readAllLines(logPath, StandardCharsets.UTF_8);
        List<String> cleaned = new ArrayList<>();
        for (String ln : lines) {
            if (ln != null && !ln.trim().isEmpty()) cleaned.add(ln.trim());
        }
        return cleaned;
    }

    public boolean isEmpty() throws IOException {
        return readAllLines().isEmpty();
    }

    public MoveRecord popLast() throws IOException {
        List<String> lines = readAllLines();
        if (lines.isEmpty()) {
            throw new IllegalStateException("No moves to undo.");
        }

        String last = lines.remove(lines.size() - 1);

        StringBuilder sb = new StringBuilder();
        for (String ln : lines) sb.append(ln).append("\n");

        Files.writeString(logPath, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return MoveRecord.parse(last);
    }
}
