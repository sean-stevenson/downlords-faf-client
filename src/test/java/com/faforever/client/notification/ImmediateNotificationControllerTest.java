package com.faforever.client.notification;

import com.faforever.client.fx.WebViewConfigurer;
import com.faforever.client.test.AbstractPlainJavaFxTest;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ImmediateNotificationControllerTest extends AbstractPlainJavaFxTest {

  private ImmediateNotificationController instance;

  @Mock
  private WebViewConfigurer webViewConfigurer;

  @Before
  public void setUp() throws Exception {
    instance = new ImmediateNotificationController(webViewConfigurer);
    loadFxml("theme/immediate_notification.fxml", clazz -> instance);
  }

  @Test
  public void testSetNotificationWithoutActions() {
    ImmediateNotification notification = new ImmediateNotification("title", "text", Severity.INFO);
    instance.setNotification(notification);

    WaitForAsyncUtils.waitForFxEvents();

    assertEquals("title", ((Label) instance.getJfxDialogLayout().getHeading().get(0)).getText());
    assertEquals("text", instance.errorMessageView.getEngine().getDocument().getDocumentElement().getTextContent());
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
    assertEquals("text", instance.errorMessageView.getEngine().getDocument().getDocumentElement().getTextContent());
    assertThat(instance.getJfxDialogLayout().getActions(), hasSize(1));
    assertEquals("actionTitle", ((Button) instance.getJfxDialogLayout().getActions().get(0)).getText());
  }

  @Test
  public void testGetRoot() throws Exception {
    assertThat(instance.getRoot(), is(instance.immediateNotificationRoot));
    // Since the notification layout is displayed in a JFXDialog, the parent isn't expected to be null.
    assertThat(instance.getRoot().getParent(), is(notNullValue()));
  }
}
