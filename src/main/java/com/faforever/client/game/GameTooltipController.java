package com.faforever.client.game;


import com.faforever.client.fx.Controller;
import com.faforever.client.player.PlayerService;
import com.faforever.client.theme.UiService;
import com.google.common.base.Joiner;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.collections.WeakMapChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class GameTooltipController implements Controller<Node> {

  private final UiService uiService;
  private final PlayerService playerService;

  public TitledPane modsPane;
  public Pane teamsPane;
  public Label modsLabel;
  public VBox gameTooltipRoot;
  private ObservableMap<String, List<String>> lastTeams;
  private ObservableMap<String, String> lastSimMods;
  private WeakMapChangeListener<String, List<String>> teamChangedListener;
  private WeakMapChangeListener<String, String> simModsChangedListener;

  @Inject
  public GameTooltipController(UiService uiService, PlayerService playerService) {
    this.uiService = uiService;
    this.playerService = playerService;
  }

  public void initialize() {
    modsPane.managedProperty().bind(modsPane.visibleProperty());
    teamChangedListener = new WeakMapChangeListener<>(change -> createTeams(change.getMap()));
    simModsChangedListener = new WeakMapChangeListener<>(change -> createModsList(change.getMap()));
  }

  public void setGameInfoBean(Game game) {
    if (lastTeams != null) {
      lastTeams.removeListener(teamChangedListener);
    }

    if (lastSimMods != null) {
      game.getSimMods().removeListener(simModsChangedListener);
    }

    lastSimMods = game.getSimMods();
    lastTeams = game.getTeams();
    createTeams(game.getTeams());
    createModsList(game.getSimMods());
    game.getTeams().addListener(teamChangedListener);

    game.getSimMods().addListener(simModsChangedListener);
  }

  private void createTeams(ObservableMap<? extends String, ? extends List<String>> teamsList) {
    Platform.runLater(() -> {
      synchronized (teamsList) {
        teamsPane.getChildren().clear();
        TeamCardController.createAndAdd(teamsList, playerService, uiService, teamsPane);
      }
    });
  }

  private void createModsList(ObservableMap<? extends String, ? extends String> simMods) {
    String stringSimMods = Joiner.on(System.getProperty("line.separator")).join(simMods.values());
    Platform.runLater(() -> {
      if (simMods.isEmpty()) {
        modsPane.setVisible(false);
        return;
      }

      modsLabel.setText(stringSimMods);
      modsPane.setVisible(true);
    });
  }

  public Node getRoot() {
    return gameTooltipRoot;
  }

}
