package jame.dev.service;

import jame.dev.models.entitys.FineEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.IFineRepo;

import java.util.Date;
import java.util.List;

public class LoanService implements IFineRepo {

    @Override
    public List<FineEntity> getAll() {
        return List.of();
    }

    @Override
    public void save(LoanEntity loan) {

    }

    @Override
    public void updateReturnDate(Date date) {

    }

    @Override
    public void updateStatus(EStatusLoan status) {

    }
}
