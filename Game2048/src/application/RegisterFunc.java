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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterFunc implements Initializable{
	
	private Main m = new Main();
	
    @FXML
    private Button btnRegister_R;

    @FXML
    private TextField txtPWHint;

    @FXML
    private PasswordField pwField_R;

    @FXML
    private PasswordField rePWField;

    @FXML
    private TextField txtUserName_R;

    @FXML
    void register(ActionEvent event) {
    	try {
	    	PreparedStatement pstmt = m.getConn().prepareStatement("SELECT `userName` FROM `game2048` WHERE 1");
	    	ResultSet result = pstmt.executeQuery();
	    	Boolean userExist = false;
	    	while(result.next()) {
	    		if(result.getString(1).equals(txtUserName_R.getText())) {
	    			userExist = true;
	    			break;
	    		}
	    	}
	    	if(txtUserName_R.getText().isBlank()) {
	    		Main.showMessageDialog("Please enter username.");
	    	}else if(userExist) {
	    		Main.showMessageDialog("This username already exist, please enter another username.");
	    	}else {
	    		if(pwField_R.getText().isBlank()) {
	    			Main.showMessageDialog("Please enter password.");
	    		}else if(pwField_R.getText().length() > 8) {
	    			Main.showMessageDialog("Password can't exceed 8 words.");
	    		}else {
	    			if(!rePWField.getText().equals(pwField_R.getText())) {
	    				Main.showMessageDialog("Reentered password isn't equal to password.");
	    			}else {
	    				if(txtPWHint.getText().isBlank()) {
	    					Main.showMessageDialog("Please enter password hint.");
	    				}else {
	    					try {
								pstmt = m.getConn().prepareStatement("INSERT INTO `game2048`(`userName`, `password`, `pwHint`) VALUES (?, ?, ?)");
								pstmt.setString(1, txtUserName_R.getText());
								pstmt.setString(2, pwField_R.getText());
								pstmt.setString(3, txtPWHint.getText());
								pstmt.executeUpdate();
								Main.showMessageDialog("Registered succeed.");							
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	    				}
	    			}
	    		}
	    	}
    	}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		btnRegister_R.setOnAction(this :: register);
	}

}
