package integration;

/**
 * Catalog information for the GUI startup as specified in the lab.
 * Contains two boolean flags for the application state.
 */
public class Catalog {
    // True if there is a game in progress (unfinished game exists)
    public boolean current;
    
    // True if there is at least one game available for each difficulty
    public boolean allModesExist;

    /**
     * Constructs a Catalog with the given status flags.
     * @param current Whether an unfinished game exists
     * @param allModesExist Whether all difficulty levels have at least one game
     */
    public Catalog(boolean current, boolean allModesExist) {
        this.current = current;
        this.allModesExist = allModesExist;
    }

    // Getters and setters (optional but recommended for encapsulation)
    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean isAllModesExist() {
        return allModesExist;
    }

    public void setAllModesExist(boolean allModesExist) {
        this.allModesExist = allModesExist;
    }

    @Override
    public String toString() {
        return "Catalog{current=" + current + ", allModesExist=" + allModesExist + "}";
    }
}