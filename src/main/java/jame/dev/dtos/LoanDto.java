package jame.dev.dtos;

import jame.dev.models.enums.EStatusLoan;

public record LoanDto (
        Integer id,
        String userName,
        String bookTitle,
        Integer copyNum,
        EStatusLoan status
){
}
