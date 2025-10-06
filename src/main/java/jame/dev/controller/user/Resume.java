package jame.dev.controller.user;

import jame.dev.models.enums.EGenre;
import jame.dev.repositorys.Joinable;
import jame.dev.service.HistoryLoanService;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resume {

   @FXML
   private PieChart pieChart;
   @FXML
   private Label labelMsg;
   private final Joinable<EGenre> repo = new HistoryLoanService();

   @FXML
   private void initialize() throws IOException {
      loadChart();
   }

   @FXML
   private void loadChart() {
      List<EGenre> genres = repo.getJoins();
      if(genres.isEmpty()){
         labelMsg.setText("There's no data for show yet.");
         return;
      }
      Map<String, Integer> count = new HashMap<>();
      genres.forEach(genre ->
              count.put(genre.name(), count.getOrDefault(genre.name(), 0) + 1)
      );
      count.forEach((k, v) ->
              pieChart.getData().add(new PieChart.Data(k, v.doubleValue())));
   }

}
