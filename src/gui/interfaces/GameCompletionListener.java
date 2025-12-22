package gui.interfaces;

public interface GameCompletionListener {
    void onGameCompleted(boolean success, String difficulty);
    void onReturnToMenu();
}