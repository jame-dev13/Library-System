package jame.dev.models.entitys;

import jame.dev.models.enums.EStatusCopy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CopyEntity {
    private Integer id;
    private UUID uuid;
    private int idBook;
    private int copyNum;
    private EStatusCopy statusCopy;
}