package com.faforever.client.util;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@UtilityClass
public class JxBrowserUtil {

  private static final String STYLESHEET_ELEMENT_ID = "__dfc-stylesheet";

  /**
   * When loadHTML is used, "file://" can not be used to reference local files. Therefore, we load a temporary local
   * file instead.
   */
  @SneakyThrows
  public void setContent(String chatContainerHtml, Path cacheDirectory, Browser browser) {
    Path file = Files.write(Files.createTempFile(cacheDirectory, "web", ".html"), Collections.singleton(chatContainerHtml));

    browser.loadURL(file.toUri().toString());
  }

  public static void injectStyleSheet(Browser browser, Path styleSheet) {
    DOMElement link = browser.getDocument().createElement("link");
    link.setAttribute("id", STYLESHEET_ELEMENT_ID);
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("type", "text/css");
    link.setAttribute("href", toFileUrl(styleSheet.toAbsolutePath()));
    browser.getDocument().findElement(By.tagName("head")).appendChild(link);
  }

  public static void updateStyleSheet(BrowserView browserView, Path styleSheet) {
    browserView.getBrowser().getDocument()
        .findElement(By.id(STYLESHEET_ELEMENT_ID))
        .setAttribute("href", toFileUrl(styleSheet.toAbsolutePath()));
  }

  @NotNull
  private String toFileUrl(Path path) {
    return "file:/" + path;
  }
}
