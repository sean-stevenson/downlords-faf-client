import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JxBrowserTest extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Browser browser = new Browser(BrowserType.LIGHTWEIGHT);

    BrowserView browserView = new BrowserView(browser);

    primaryStage.setScene(new Scene(browserView));
  }
}
