package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginFunc implements Initializable{
	
	private Main m = new Main();
	private PreparedStatement pstmt;
	private ResultSet result;
	
    @FXML
    private Button btnLogin;

    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnPWHint;

    @FXML
    private Button btnRegister;

    @FXML
    private PasswordField pwField;

    @FXML
    private TextField txtUserName;
    
    @FXML
    void login(ActionEvent event) {
    	try {
			pstmt = m.getConn().prepareStatement("SELECT `userName` FROM `game2048` WHERE 1");
			result = pstmt.executeQuery();
			Boolean userExist = false;
			while(result.next()) {
				String name = result.getString(1);
				if(name.equals(txtUserName.getText())){
					userExist = true;
					pstmt = m.getConn().prepareStatement("SELECT `password` FROM `game2048` WHERE `userName` = ?");
					pstmt.setString(1, name);
					result = pstmt.executeQuery();
					result.next();
					if(result.getString("password").equals(pwField.getText())) {
						Main.showMessageDialog("Login succeed.");
						m.setLogin(true);
						m.setUserName(name);
						try {
							PreparedStatement pstmt = m.getConn().prepareStatement("SELECT `highestScore` FROM `game2048` WHERE `userName` = ?");
							pstmt.setString(1, name);
							ResultSet result = pstmt.executeQuery();
							result.next();
							m.setHighestScore(result.getInt(1));							
						}catch(SQLException e) {
							e.printStackTrace();
						}
						m.mainPage_show();
					}else {
						Main.showMessageDialog("Wrong Password：Forget your Password ?");
					}
					break;
				}
			}
			if(!userExist) {
				Main.showMessageDialog("Can't find the user：Create an account ?");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    @FXML
	void password_hint(ActionEvent event) {
		try {
			pstmt = m.getConn().prepareStatement("SELECT `userName` FROM `game2048` WHERE 1");
			result = pstmt.executeQuery();
			Boolean userExist = false;
			while (result.next()) {
				 if (result.getString(1).equals(txtUserName.getText())) {
					userExist = true;
					pstmt = m.getConn().prepareStatement("SELECT `pwHint` FROM `game2048` WHERE `userName` = ?");
					pstmt.setString(1, txtUserName.getText());
					result = pstmt.executeQuery();
					result.next();
					Main.showMessageDialog("Your Password hint is：" + result.getString(1));
				}
			}
			if (txtUserName.getText().isBlank()) {
				Main.showMessageDialog("Please enter username.");
			}else if(!userExist) {
				Main.showMessageDialog("Can't find this user.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @FXML
    void registerPage(ActionEvent event) throws Exception {
    	System.out.println("register");
    	this.registerPage_show();
    }


    @FXML
    void back_MainPage(ActionEvent event) {
    	m.mainPage_show();
    }

	public void registerPage_show() throws Exception {
		try {
			Stage stage = new Stage();
			Parent rootLogin = FXMLLoader.load(getClass().getResource("/RegisterPage.fxml"));
			Scene scene = new Scene(rootLogin);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	    	stage.setScene(scene);
	    	stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
