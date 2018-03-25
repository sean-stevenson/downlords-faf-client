package com.faforever.client.tutorial;

import com.faforever.client.fx.AbstractViewController;
import com.faforever.client.i18n.I18n;
import com.faforever.client.main.event.NavigateEvent;
import com.faforever.client.theme.UiService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TutorialController extends AbstractViewController<Node> {
  private final TutorialService tutorialService;
  private final I18n i18n;
  private final UiService uiService;
  public HBox rootPane;
  public VBox tutorialOverviewPane;
  public TutorialDetailController tutorialDetailController;
  private List<TutorialListItemController> tutorialListItemControllers = new ArrayList<>();

  @Inject
  public TutorialController(TutorialService tutorialService, I18n i18n, UiService uiService) {
    this.tutorialService = tutorialService;
    this.i18n = i18n;
    this.uiService = uiService;
  }

  @Override
  public void onDisplay(NavigateEvent navigateEvent) {
    super.onDisplay(navigateEvent);
    tutorialListItemControllers.clear();
    tutorialOverviewPane.getChildren().clear();
    tutorialService.getTutorialsSortedByCategory().thenAccept(this::displayTutorials);
  }

  private void displayTutorials(Map<String, List<Tutorial>> tutorialsByCategory) {
    Platform.runLater(() -> {
      tutorialsByCategory.forEach((s, tutorials) -> {
        addCategory(s);
        addTutorials(tutorials);
      });

      if (tutorialDetailController.getTutorial() == null && !tutorialListItemControllers.isEmpty()) {
        onTutorialClicked(tutorialListItemControllers.get(0));
      }
    });
  }

  private void addTutorials(List<Tutorial> tutorials) {
    tutorials.forEach(tutorial -> {
      TutorialListItemController tutorialListItemController = uiService.loadFxml("theme/tutorial_list_item.fxml");
      tutorialListItemController.setTutorial(tutorial);
      Node root = tutorialListItemController.getRoot();
      tutorialListItemControllers.add(tutorialListItemController);
      tutorialOverviewPane.getChildren().add(root);
      root.setOnMouseClicked(event -> onTutorialClicked(tutorialListItemController));
    });
  }

  private void onTutorialClicked(TutorialListItemController tutorialListItemController) {
    tutorialListItemControllers.forEach(specificTutorialListItemController -> specificTutorialListItemController.getRoot().pseudoClassStateChanged(TutorialListItemController.SELECTED_PSEUDO_CLASS, false));
    tutorialListItemController.getRoot().pseudoClassStateChanged(TutorialListItemController.SELECTED_PSEUDO_CLASS, true);
    tutorialDetailController.setTutorial(tutorialListItemController.getTutorial());
  }

  private void addCategory(String title) {
    TutorialCategoryListItemController tutorialCategoryListItemController = uiService.loadFxml("theme/tutorial_category_list_item.fxml");
    tutorialCategoryListItemController.setCategory(title);
    tutorialOverviewPane.getChildren().add(tutorialCategoryListItemController.getRoot());
  }

  @Override
  public Node getRoot() {
    return rootPane;
  }

}
