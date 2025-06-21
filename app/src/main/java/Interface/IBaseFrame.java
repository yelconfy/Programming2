package Interface;

import Helper.Injector;
import com.example.S1101.project.HomePage;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;

public interface IBaseFrame {

    default void CenterFrame(JFrame frame) {
        SwingUtilities.invokeLater(() -> {
            frame.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = frame.getSize();
            int x = (screenSize.width - frameSize.width) / 2;
            int y = (screenSize.height - frameSize.height) / 2;
            frame.setLocation(x, y);
        });
    }

    /**
     * Closes the current frame and shows the HomePage. Implementing classes
     * must also be a JFrame.
     */
    default void PerformLogout() {
        if (this instanceof JFrame) {
            JFrame frame = (JFrame) this;
            frame.setVisible(false);
            HomePage homePage = Injector.createHomePage();
            homePage.setVisible(true);
        } else {
            throw new IllegalStateException("PerformLogout can only be used in a JFrame class");
        }
    }

}
