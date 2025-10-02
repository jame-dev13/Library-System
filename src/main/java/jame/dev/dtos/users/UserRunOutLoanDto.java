package jame.dev.dtos.users;

import jame.dev.models.enums.EStatusLoan;
import lombok.Builder;

@Builder
public record UserRunOutLoanDto (int idUser, EStatusLoan status){
}
