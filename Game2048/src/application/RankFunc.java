package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class RankFunc implements Initializable{

	private Main m = new Main();

    @FXML
    private Label userInfo_name;

    @FXML
    private Label userInfo_rank;

    @FXML
    private Label userInfo_score;

    @FXML
    private AnchorPane userRank;
    
    @FXML
    private Button btnBack_Rank;

    @FXML
    private Button btnLogin_Rank;
    
    @FXML
    private ListView<String> rankList = new ListView<String>();

    @FXML
    void back_LoginPage(ActionEvent event) throws Exception {
    	m.loginPage_show();
    }

    @FXML
    void back_MainPage(ActionEvent event) {
    	m.mainPage_show();
    }

    public void rankInfo() {
    	ObservableList <String> userList = FXCollections.observableArrayList(); 
    	ArrayList<String> scoreList = new ArrayList<>();
    	try {
    		PreparedStatement pstmt = m.getConn().prepareStatement("SELECT `userName`, `highestScore` FROM `game2048` ORDER BY `highestScore` DESC");
    		ResultSet result = pstmt.executeQuery();
    		while (result.next()) {
    			userList.add(result.getString(1));
    			scoreList.add(result.getString(2));
    		}
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    	rankList.setItems(userList);
    	rankList.setCellFactory(new Callback<ListView<String>, ListCell<String>> () {
			@Override
			public ListCell<String> call(ListView<String> arg0) {
				// TODO Auto-generated method stub
				return new ListCell<String> () {
					@Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                        	int rank = userList.indexOf(item) + 1;
                            if(rank % 2 == 0) {
                            	HBox infoGrey = new HBox();
                            	Label infoGrey_name = new Label(item);
                            	Label infoGrey_score = new Label(scoreList.get(rank-1));
                            	Label infoGrey_rank = new Label("#" + rank);
                            	infoGrey.getStyleClass().add("infoGrey");
                            	infoGrey_name.getStyleClass().add("lblName");
                            	infoGrey_score.getStyleClass().add("lbl");
                            	infoGrey_rank.getStyleClass().add("lbl");
                            	infoGrey.getChildren().addAll(infoGrey_name, infoGrey_score, infoGrey_rank);
                            	setGraphic(infoGrey);
                            }else {
                            	HBox infoGreen = new HBox();
                            	Label infoGreen_name = new Label(item);
                            	Label infoGreen_score = new Label(scoreList.get(rank-1));
                            	Label infoGreen_rank = new Label("#" + rank);
                            	infoGreen.getStyleClass().add("infoGreen");
                            	infoGreen_name.getStyleClass().add("lblName");
                            	infoGreen_score.getStyleClass().add("lbl");
                            	infoGreen_rank.getStyleClass().add("lbl");
                            	infoGreen.getChildren().addAll(infoGreen_name, infoGreen_score, infoGreen_rank);
                            	setGraphic(infoGreen);
                            }
                        }
                    }
				};
			}
    		
    	});
    	rankList.setVisible(true);
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		this.rankInfo();
		if(Main.isLogin()) {
			userInfo_name.setText(Main.getUserName());
			userInfo_score.setText(Main.getHighestScore());
			btnLogin_Rank.setVisible(false);
			userRank.setVisible(true);
		}else {
			btnLogin_Rank.setVisible(true);
			userRank.setVisible(false);
		}
	}

}
