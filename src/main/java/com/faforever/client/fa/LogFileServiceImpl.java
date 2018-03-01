package com.faforever.client.fa;

import com.faforever.client.i18n.I18n;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.util.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
public class LogFileServiceImpl implements LogFileService{
  private static final String KEY_FOR_REPLAY_LOG="log.file.replay";
  private static final String KEY_FOR_GAME_LOG="log.file.game";

  private final I18n i18n;
  private final PreferencesService preferencesService;
  private final Pattern replayPattern;
  private final Pattern gamePattern;

  public LogFileServiceImpl(I18n i18n, PreferencesService preferencesService) {
    this.i18n = i18n;
    this.preferencesService = preferencesService;
    this.replayPattern = Pattern.compile(i18n.get(KEY_FOR_GAME_LOG,"(.)*"));
    this.gamePattern = Pattern.compile(i18n.get(KEY_FOR_REPLAY_LOG,"(.)*"));
  }

  @PostConstruct
  public void init(){
    try{
      Path fafLogDirectory = preferencesService.getFafLogDirectory();
      for (File logFile : fafLogDirectory.toFile().listFiles()) {
        String fileName = logFile.getName();
        if(replayPattern.matcher(fileName).matches()||gamePattern.matcher(fileName).matches()){
          try {
            BasicFileAttributes attr = Files.readAttributes(logFile.toPath(), BasicFileAttributes.class);
            if(attr.creationTime().toInstant().compareTo(Instant.now().minus(1, ChronoUnit.WEEKS))>0){
              logFile.delete();
            }
          } catch (IOException e) {
            log.warn(MessageFormat.format("Creation date of file {0} could not be read",logFile),e);
          }

        }
      }
    }catch (Exception e){
      log.warn("Deleting old log files failed",e);
    }
  }

  @Override
  public Path getPathForLog(boolean replay, int id) {
    String fileName= i18n.get(replay?KEY_FOR_REPLAY_LOG:KEY_FOR_GAME_LOG,id)+".log";
    return preferencesService.getFafLogDirectory().resolve(fileName);
  }
}
