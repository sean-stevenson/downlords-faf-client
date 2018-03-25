package com.faforever.client.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(of = "id")
@Type("tutorial")
public class Tutorial {
  @Id
  private String id;
  private String description;
  private String title;
  private String category;
  private String image;
  private int ordinal;
  private boolean launchable;
  private MapVersion mapVersion;
  private OffsetDateTime createTime;
  private OffsetDateTime updateTime;
}
