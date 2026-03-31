import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.awt.Desktop;
import java.net.URI;

@WebListener
public class AutoOpenBrowserListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 延迟3秒启动，确保服务器完全启动
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                String url = "http://localhost:8080/";
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    Runtime.getRuntime().exec("cmd /c start " + url); // Windows
                    // Runtime.getRuntime().exec("open " + url); // Mac
                    // Runtime.getRuntime().exec("xdg-open " + url); // Linux
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 服务器关闭时的操作
    }
}