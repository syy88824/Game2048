package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UserFunc implements Initializable {
	
	Main m = new Main();
	
    @FXML
    private Button btnBack;

    @FXML
    private Button btnLogout;

    @FXML
    private Label lblHighestScore;

    @FXML
    private Label lblUserName;

    @FXML
    void back_MainPage(ActionEvent event) {
    	m.mainPage_show();
    }

    @FXML
    void logout(ActionEvent event) {
    	m.setLogin(false);
    	m.setUserName(null);
    	m.setHighestScore(0);
    	m.setGameEnd(false);
    	m.mainPage_show();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		String userName = Main.getUserName();
		lblUserName.setText(userName);
		lblHighestScore.setText(Main.getHighestScore());
	}
}

