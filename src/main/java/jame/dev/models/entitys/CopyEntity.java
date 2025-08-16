package jame.dev.models.entitys;

import jame.dev.models.enums.EStatusCopy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CopyEntity {
    private Integer id;
    private int idBook;
    private int copyNum;
    private EStatusCopy statusCopy;
}