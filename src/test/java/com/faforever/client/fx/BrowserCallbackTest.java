package com.faforever.client.fx;

import com.faforever.client.chat.UrlPreviewResolver;
import com.faforever.client.clan.Clan;
import com.faforever.client.clan.ClanService;
import com.faforever.client.clan.ClanTooltipController;
import com.faforever.client.config.ClientProperties;
import com.faforever.client.i18n.I18n;
import com.faforever.client.player.Player;
import com.faforever.client.player.PlayerService;
import com.faforever.client.replay.ExternalReplayInfoGenerator;
import com.faforever.client.replay.Replay;
import com.faforever.client.replay.ReplayService;
import com.faforever.client.test.AbstractPlainJavaFxTest;
import com.faforever.client.theme.UiService;
import com.google.common.eventbus.EventBus;
import javafx.scene.layout.Pane;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BrowserCallbackTest extends AbstractPlainJavaFxTest {
  private static final String TEST_REPLAY_BASE_URL = "http://test.de/replay/%s/tester";
  private static final String SAMPLE_CLAN_TAG = "xyz";
  private static final long TIMEOUT = 5000;

  private BrowserCallback instance;
  @Mock
  private PlatformService platformService;
  @Mock
  private UrlPreviewResolver urlPreviewResolver;
  @Mock
  private ReplayService replayService;
  @Mock
  private EventBus eventBus;
  @Mock
  private ExternalReplayInfoGenerator exeternalReplayInfoGenerator;
  @Mock
  private ClanService clanService;
  @Mock
  private UiService uiService;
  @Mock
  private PlayerService playerService;
  @Mock
  private I18n i18n;

  @Before
  public void setUp() throws Exception {
    ClientProperties clientProperties = new ClientProperties();
    clientProperties.getVault().setReplayDownloadUrlFormat(TEST_REPLAY_BASE_URL);

    instance = new BrowserCallback(platformService, clientProperties, urlPreviewResolver, replayService, eventBus, exeternalReplayInfoGenerator, clanService, uiService, playerService, i18n);

    Clan clan = new Clan();
    clan.setId("1234");
    when(clanService.getClanByTag(SAMPLE_CLAN_TAG)).thenReturn(completedFuture(Optional.of(clan)));
  }

  @Test
  public void testHideClanInfo() {
    instance.clanInfo(SAMPLE_CLAN_TAG);
    instance.hideClanInfo();
    assertThat(instance.clanInfoPopup, is(CoreMatchers.nullValue()));
  }

  @Test
  public void testShowClanInfo() {
    ClanTooltipController mock = mock(ClanTooltipController.class);
    when(uiService.loadFxml("theme/chat/clan_tooltip.fxml")).thenReturn(mock);
    when(mock.getRoot()).thenReturn(new Pane());

    instance.clanInfo(SAMPLE_CLAN_TAG);
    WaitForAsyncUtils.waitForFxEvents();
    assertThat(instance.clanInfoPopup, CoreMatchers.notNullValue());
  }

  @Test
  public void testShowClanWebsite() {
    Clan clan = new Clan();
    clan.setId("1234");
    clan.setWebsiteUrl("http://example.com");
    instance.showClanWebsite(SAMPLE_CLAN_TAG);

    WaitForAsyncUtils.waitForFxEvents();

    verify(platformService).showDocument(any());
  }

  @Test
  public void testPlayerInfo() {
    String playerName = "somePlayer";
    Player player = new Player(playerName);
    when(playerService.getPlayerForUsername(playerName)).thenReturn(player);

    WaitForAsyncUtils.waitForAsyncFx(TIMEOUT, () -> instance.playerInfo(playerName));

    verify(playerService).getPlayerForUsername(playerName);
  }

  @Test
  public void testHidePlayerInfoDoesNotThrowExceptionWhenNoTooltipDisplayed() {
    instance.hidePlayerInfo();
  }

  @Test
  public void testHidePlayerInfo() {
    String playerName = "somePlayer";
    Player player = new Player(playerName);
    when(playerService.getPlayerForUsername(playerName)).thenReturn(player);

    WaitForAsyncUtils.waitForAsyncFx(TIMEOUT, () -> {
      instance.playerInfo(playerName);
      instance.hidePlayerInfo();
    });
    // I don't see what could be verified here
  }

  @Test
  public void testOpenUrl() {
    String url = "http://www.example.com";

    instance.openUrl(url);

    verify(platformService).showDocument(url);
  }

  @Test
  public void testPreviewUrlReturnsNull() {
    String url = "http://www.example.com";

    when(urlPreviewResolver.resolvePreview(url)).thenReturn(completedFuture(null));

    instance.previewUrl(url);

    verify(urlPreviewResolver).resolvePreview(url);
  }

  @Test
  public void testPreviewUrlReturnsPreview() {
    String url = "http://www.example.com";
    UrlPreviewResolver.Preview preview = mock(UrlPreviewResolver.Preview.class);
    when(urlPreviewResolver.resolvePreview(url)).thenReturn(completedFuture(Optional.of(preview)));

    WaitForAsyncUtils.waitForAsyncFx(1000, () -> instance.previewUrl(url));

    verify(urlPreviewResolver).resolvePreview(url);
    verify(preview).getNode();
  }

  @Test
  public void testHideUrlPreviewNullDoesntThrowException() {
    instance.hideUrlPreview();
  }

  @Test
  public void testHideUrlPreview() {
    String url = "http://www.example.com";
    UrlPreviewResolver.Preview preview = mock(UrlPreviewResolver.Preview.class);
    when(urlPreviewResolver.resolvePreview(url)).thenReturn(completedFuture(Optional.of(preview)));

    WaitForAsyncUtils.waitForAsyncFx(TIMEOUT, () -> {
      instance.previewUrl(url);
      instance.hideUrlPreview();
    });
    // I don't see what could be verified here
  }

  @Test
  public void testOpenReplayUrl() {
    when(replayService.findById(1234)).thenReturn(CompletableFuture.completedFuture(Optional.of(new Replay())));

    instance.openUrl(String.format(TEST_REPLAY_BASE_URL, Integer.toString(1234)));
    verify(replayService).findById(1234);
  }
}
