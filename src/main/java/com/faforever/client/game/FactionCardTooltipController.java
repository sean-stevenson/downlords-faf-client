package com.faforever.client.game;

import com.faforever.client.fx.Controller;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FactionCardTooltipController implements Controller<Node> {

  public Label factionLabel;

  public void setFaction(Faction faction) {
    switch (faction) {
      case AEON:
        factionLabel.setText("\uE900");
        factionLabel.getStyleClass().add("card-aeon");
        break;
      case SERAPHIM:
        factionLabel.setText("\uE903");
        factionLabel.getStyleClass().add("card-seraphim");
        break;
      case CYBRAN:
        factionLabel.setText("\uE902");
        factionLabel.getStyleClass().add("card-cybran");
        break;
      case UEF:
        factionLabel.setText("\uE904");
        factionLabel.getStyleClass().add("card-uef");
        break;
      case NOMAD:
        factionLabel.setText("N");
        factionLabel.getStyleClass().add("card-nomad");
        break;
      default:
        factionLabel.setText("");
        break;
    }
  }

  public Node getRoot() {
    return factionLabel;
  }
}

