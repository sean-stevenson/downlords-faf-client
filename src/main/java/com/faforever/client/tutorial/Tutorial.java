package com.faforever.client.tutorial;

import com.faforever.client.map.MapBean;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.OffsetDateTime;
import java.util.Optional;

public class Tutorial {
  private final StringProperty id;
  private final StringProperty description;
  private final StringProperty title;
  private final StringProperty category;
  private final StringProperty thumbnailUrl;
  private final IntegerProperty ordinal;
  private final BooleanProperty launchable;
  private final ObjectProperty<MapBean> mapVersion;
  private final ObjectProperty<OffsetDateTime> createTime;
  private final ObjectProperty<OffsetDateTime> updateTime;

  public Tutorial() {
    id = new SimpleStringProperty();
    description = new SimpleStringProperty();
    title = new SimpleStringProperty();
    category = new SimpleStringProperty();
    thumbnailUrl = new SimpleStringProperty();
    ordinal = new SimpleIntegerProperty();
    launchable = new SimpleBooleanProperty();
    mapVersion = new SimpleObjectProperty<>();
    createTime = new SimpleObjectProperty<>();
    updateTime = new SimpleObjectProperty<>();
  }

  public static Tutorial fromDto(com.faforever.client.api.dto.Tutorial dto) {
    Tutorial tutorial = new Tutorial();

    tutorial.setId(dto.getId());
    tutorial.setDescription(dto.getDescription());
    tutorial.setTitle(dto.getTitle());
    tutorial.setCategory(dto.getCategory());
    tutorial.setThumbnailUrl(dto.getImage());
    tutorial.setOrdinal(dto.getOrdinal());
    tutorial.setLaunchable(dto.isLaunchable());
    Optional.ofNullable(dto.getMapVersion()).ifPresent(mapVersionOptional -> tutorial.setMapVersion(MapBean.fromMapVersionDto(mapVersionOptional)));
    tutorial.setCreateTime(dto.getCreateTime());
    tutorial.setUpdateTime(dto.getUpdateTime());

    return tutorial;
  }

  public String getId() {
    return id.get();
  }

  public void setId(String id) {
    this.id.set(id);
  }

  public StringProperty idProperty() {
    return id;
  }

  public String getDescription() {
    return description.get();
  }

  public void setDescription(String description) {
    this.description.set(description);
  }

  public StringProperty descriptionProperty() {
    return description;
  }

  public String getTitle() {
    return title.get();
  }

  public void setTitle(String title) {
    this.title.set(title);
  }

  public StringProperty titleProperty() {
    return title;
  }

  public String getCategory() {
    return category.get();
  }

  public void setCategory(String category) {
    this.category.set(category);
  }

  public StringProperty categoryProperty() {
    return category;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl.get();
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl.set(thumbnailUrl);
  }

  public StringProperty thumbnailUrlProperty() {
    return thumbnailUrl;
  }

  public int getOrdinal() {
    return ordinal.get();
  }

  public void setOrdinal(int ordinal) {
    this.ordinal.set(ordinal);
  }

  public IntegerProperty ordinalProperty() {
    return ordinal;
  }

  public boolean isLaunchable() {
    return launchable.get();
  }

  public void setLaunchable(boolean launchable) {
    this.launchable.set(launchable);
  }

  public BooleanProperty launchableProperty() {
    return launchable;
  }

  public MapBean getMapVersion() {
    return mapVersion.get();
  }

  public void setMapVersion(MapBean mapBean) {
    this.mapVersion.set(mapBean);
  }

  public ObjectProperty<MapBean> mapVersionProperty() {
    return mapVersion;
  }

  public OffsetDateTime getCreateTime() {
    return createTime.get();
  }

  public void setCreateTime(OffsetDateTime createTime) {
    this.createTime.set(createTime);
  }

  public ObjectProperty<OffsetDateTime> createTimeProperty() {
    return createTime;
  }

  public OffsetDateTime getUpdateTime() {
    return updateTime.get();
  }

  public void setUpdateTime(OffsetDateTime updateTime) {
    this.updateTime.set(updateTime);
  }

  public ObjectProperty<OffsetDateTime> updateTimeProperty() {
    return updateTime;
  }
}
