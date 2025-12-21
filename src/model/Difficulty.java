package model;

public enum Difficulty {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private final String folder;

    Difficulty(String folder) {
        this.folder = folder;
    }

    public String folder() {
        return folder;
    }
}
