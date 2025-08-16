package jame.dev.dtos;

import jame.dev.models.enums.EStatusCopy;
import lombok.Builder;

@Builder
public record CopyDto(Integer id,
                      String title,
                      String author,
                      String ISBN,
                      String language,
                      String genre,
                      EStatusCopy status) {
}
