package jame.dev.dtos;

import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CopyDto(Integer id,
                      UUID uuid,
                      ELanguage language,
                      String genre,
                      EStatusCopy status) {
}
