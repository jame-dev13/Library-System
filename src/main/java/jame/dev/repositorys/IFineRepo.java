package jame.dev.repositorys;

import jame.dev.models.entitys.FineEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;

import java.util.Date;
import java.util.List;

public interface IFineRepo {
    List<FineEntity> getAll();
    void save(LoanEntity loan);
    void updateReturnDate(Date date);
    void updateStatus(EStatusLoan status);
}
