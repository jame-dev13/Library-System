package jame.dev.models.entitys;


import jame.dev.models.enums.EStatusLoan;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanEntity {
    private Integer id;
    @NonNull private UUID uuid;
    @NonNull private int idUser;
    @NonNull private int idCopy;
    @NonNull private LocalDate loanDate;
    @NonNull private LocalDate returnDate;
    @NonNull private EStatusLoan statusLoan;
}