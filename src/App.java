import javax.swing.SwingUtilities;
import ui.MainFrame;

public class App {
    public static void main(String[] args) {
        // Performance test
        model.Cart.performanceTest(100);
        model.Cart.performanceTest(150);
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
