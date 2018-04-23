package com.faforever.client.fx;

import com.faforever.client.theme.UiService;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebViewConfigurer {

  /**
   * This is the member name within the JavaScript code that provides access to the Java callback instance.
   */
  private static final String JAVA_REFERENCE_IN_JAVASCRIPT = "java";
  private static final double ZOOM_STEP = 0.2d;

  private final UiService uiService;
  private final ApplicationContext applicationContext;

  public WebViewConfigurer(UiService uiService, ApplicationContext applicationContext) {
    this.uiService = uiService;
    this.applicationContext = applicationContext;
  }

  public void configureWebView(BrowserView browserView, Logger logger) {
    Browser browser = browserView.getBrowser();
    browser.addConsoleListener(consoleEvent -> logger.debug(consoleEvent.toString()));

    browserView.setScrollEventsHandler(event -> {
      if (event.isControlDown()) {
        browser.setZoomLevel(browser.getZoomLevel() + ZOOM_STEP * Math.signum(event.getDeltaY()));
        return true;
      }
      return false;
    });

    browserView.setOnKeyPressed(event -> {
      if (event.isControlDown() && (event.getCode() == KeyCode.DIGIT0 || event.getCode() == KeyCode.NUMPAD0)) {
        browserView.getBrowser().zoomReset();
      }
    });

    BrowserPreferences preferences = browser.getPreferences();
    preferences.setTransparentBackground(true);
    browser.setPreferences(preferences);

    BrowserCallback browserCallback = applicationContext.getBean(BrowserCallback.class);
    browserCallback.setBrowser(browser);

    EventHandler<MouseEvent> moveHandler = event -> {
      browserCallback.setLastMouseX(event.getScreenX());
      browserCallback.setLastMouseY(event.getScreenY());
    };
    browserView.addEventHandler(MouseEvent.MOUSE_MOVED, moveHandler);

    browser.addLoadListener(new LoadAdapter() {

      @Override
      public void onDocumentLoadedInMainFrame(LoadEvent event) {
        uiService.registerBrowserView(browserView);

        Browser browser = event.getBrowser();
        JSObject window = browser.executeJavaScriptAndReturnValue("window").asObject();
        window.setProperty(JAVA_REFERENCE_IN_JAVASCRIPT, browserCallback);

        List<DOMElement> links = browser.getDocument().findElements(By.tagName("a"));
        for (DOMElement link : links) {
          String href = link.getAttribute("href");
          link.setAttribute("onMouseOver", "java.previewUrl('" + href + "')");
          link.setAttribute("onMouseOut", "java.hideUrlPreview()");
          link.setAttribute("href", "javascript:java.openUrl('" + href + "');");
        }
      }
    });
  }
}
