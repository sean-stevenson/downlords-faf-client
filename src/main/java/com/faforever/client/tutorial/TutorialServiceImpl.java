package com.faforever.client.tutorial;

import com.faforever.client.remote.FafService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class TutorialServiceImpl implements TutorialService {
  private final FafService fafService;

  @Inject
  public TutorialServiceImpl(FafService fafService) {
    this.fafService = fafService;
  }

  @Override
  public CompletableFuture<Map<String, List<Tutorial>>> getTutorialsSortedByCategory() {
    return CompletableFuture.supplyAsync(() -> {
      Map<String, List<Tutorial>> result = new HashMap<>();
      try {
        List<Tutorial> tutorials = fafService.getTutorials().get();
        for (Tutorial tutorial : tutorials) {
          if (!result.containsKey(tutorial.getCategory())) {
            result.put(tutorial.getCategory(), new ArrayList<>(Collections.singletonList(tutorial)));
          } else {
            result.get(tutorial.getCategory()).add(tutorial);
          }
        }

      } catch (InterruptedException | ExecutionException e) {
        log.error("Something went wrong when processing Tutorials", e);
      }
      return result;
    });
  }
}
