package com.faforever.client.main.event;

import com.faforever.client.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ShowProfileEvent {
  private final Player player;
}
