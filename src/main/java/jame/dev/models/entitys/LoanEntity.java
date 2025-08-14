package jame.dev.models.entitys;


import jame.dev.models.enums.EStatusLoan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanEntity {
    private Integer id;
    private int idUser;
    private int idCopy;
    private Date loanDate;
    private Date returnDate;
    private EStatusLoan statusLoan;
}