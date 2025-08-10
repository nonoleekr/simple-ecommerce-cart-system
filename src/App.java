import javax.swing.SwingUtilities;
import ui.LoginFrame;

public class App {
    public static void main(String[] args) {
        // Performance test
        model.Cart.performanceTest(100);
        model.Cart.performanceTest(150);
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
