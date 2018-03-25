package com.faforever.client.tutorial;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface TutorialService {
  CompletableFuture<Map<String, List<Tutorial>>> getTutorialsSortedByCategory();
}
