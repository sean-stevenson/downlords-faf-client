package com.faforever.client.notification;

import com.faforever.client.fx.WebViewConfigurer;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.test.AbstractPlainJavaFxTest;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ImmediateNotificationControllerTest extends AbstractPlainJavaFxTest {

  @Rule
  public TemporaryFolder tempDir = new TemporaryFolder();

  private ImmediateNotificationController instance;

  @Mock
  private WebViewConfigurer webViewConfigurer;
  @Mock
  private PreferencesService preferencesService;

  @Before
  public void setUp() throws Exception {
    instance = new ImmediateNotificationController(webViewConfigurer, preferencesService);
    loadFxml("theme/immediate_notification.fxml", clazz -> instance);

    when(preferencesService.getCacheDirectory()).thenReturn(tempDir.getRoot().toPath());
  }

  @Test
  public void testSetNotificationWithoutActions() {
    ImmediateNotification notification = new ImmediateNotification("title", "text", Severity.INFO);
    instance.setNotification(notification);

    WaitForAsyncUtils.waitForFxEvents();

    assertEquals("title", ((Label) instance.getJfxDialogLayout().getHeading().get(0)).getText());
    assertEquals("text\n", instance.errorMessageView.getBrowser().getDocument().getDocumentElement().getTextContent());
    assertThat(instance.getJfxDialogLayout().getActions(), empty());
  }

  @Test
  public void testSetNotificationWithActions() {
    ImmediateNotification notification = new ImmediateNotification("title", "text", Severity.INFO,
        Collections.singletonList(
            new Action("actionTitle")
        ));
    instance.setNotification(notification);

    WaitForAsyncUtils.waitForFxEvents();

    assertEquals("title", ((Label) instance.getJfxDialogLayout().getHeading().get(0)).getText());
    assertEquals("text\n", instance.errorMessageView.getBrowser().getDocument().getDocumentElement().getTextContent());
    assertThat(instance.getJfxDialogLayout().getActions(), hasSize(1));
    assertEquals("actionTitle", ((Button) instance.getJfxDialogLayout().getActions().get(0)).getText());
  }

  @Test
  public void testGetRoot() throws Exception {
    Assert.assertThat(instance.getRoot(), is(instance.immediateNotificationRoot));
    // Since the notification layout is displayed in a JFXDialog, the parent isn't expected to be null.
    Assert.assertThat(instance.getRoot().getParent(), is(notNullValue()));
  }
}
