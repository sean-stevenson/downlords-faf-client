package com.faforever.client.news;

import com.faforever.client.fx.AbstractViewController;
import com.faforever.client.fx.WebViewConfigurer;
import com.faforever.client.i18n.I18n;
import com.faforever.client.main.event.NavigateEvent;
import com.faforever.client.main.event.ShowLadderMapsEvent;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.theme.UiService;
import com.faforever.client.util.JxBrowserUtil;
import com.google.common.eventbus.EventBus;
import com.google.common.io.CharStreams;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class NewsController extends AbstractViewController<Node> {

  private static final ClassPathResource NEWS_DETAIL_HTML_RESOURCE = new ClassPathResource("/theme/news_detail.html");
  private final PreferencesService preferencesService;
  private final I18n i18n;
  private final NewsService newsService;
  private final UiService uiService;
  private final EventBus eventBus;
  private final WebViewConfigurer webViewConfigurer;
  public Pane newsRoot;
  public Pane newsListPane;
  public BrowserView newsDetailWebView;
  public Button showLadderMapsButton;

  @Inject
  public NewsController(PreferencesService preferencesService, I18n i18n, NewsService newsService, UiService uiService, EventBus eventBus, WebViewConfigurer webViewConfigurer) {
    this.preferencesService = preferencesService;
    this.i18n = i18n;
    this.newsService = newsService;
    this.uiService = uiService;
    this.eventBus = eventBus;
    this.webViewConfigurer = webViewConfigurer;
  }

  @Override
  public void initialize() {
    super.initialize();
    webViewConfigurer.configureWebView(newsDetailWebView, log);
  }

  @Override
  public void onDisplay(NavigateEvent navigateEvent) {
    if (!newsListPane.getChildren().isEmpty()) {
      return;
    }
    showLadderMapsButton.managedProperty().bind(showLadderMapsButton.visibleProperty());
    showLadderMapsButton.setVisible(false);

    boolean firstItemSelected = false;

    List<NewsItem> newsItems = newsService.fetchNews();
    for (NewsItem newsItem : newsItems) {
      NewsListItemController newsListItemController = uiService.loadFxml("theme/news_list_item.fxml");
      newsListItemController.setNewsItem(newsItem);
      newsListItemController.setOnItemSelectedListener((item) -> {
        newsListPane.getChildren().forEach(node -> node.pseudoClassStateChanged(NewsListItemController.SELECTED_PSEUDO_CLASS, false));
        displayNewsItem(item);
        newsListItemController.getRoot().pseudoClassStateChanged(NewsListItemController.SELECTED_PSEUDO_CLASS, true);
      });

      newsListPane.getChildren().add(newsListItemController.getRoot());

      if (!firstItemSelected) {
        preferencesService.getPreferences().getNews().setLastReadNewsUrl(newsItem.getLink());
        preferencesService.storeInBackground();
        newsListItemController.onMouseClicked();
        firstItemSelected = true;
      }
    }
  }

  private void displayNewsItem(NewsItem newsItem) {
    showLadderMapsButton.setVisible(newsItem.getNewsCategory().equals(NewsCategory.LADDER));
    eventBus.post(new UnreadNewsEvent(false));

    try (Reader reader = new InputStreamReader(NEWS_DETAIL_HTML_RESOURCE.getInputStream())) {
      String html = CharStreams.toString(reader).replace("{title}", newsItem.getTitle())
          .replace("{content}", newsItem.getContent())
          .replace("{authored}", i18n.get("news.authoredFormat", newsItem.getAuthor(), newsItem.getDate()));

      Platform.runLater(() -> JxBrowserUtil.setContent(html, preferencesService.getCacheDirectory(), newsDetailWebView.getBrowser()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Node getRoot() {
    return newsRoot;
  }

  public void showLadderMaps() {
    eventBus.post(new ShowLadderMapsEvent());
  }
}
