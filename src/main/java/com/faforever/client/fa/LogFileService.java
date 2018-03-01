package com.faforever.client.fa;

import java.nio.file.Path;

public interface LogFileService {
  Path getPathForLog(boolean replay, int id);
}
