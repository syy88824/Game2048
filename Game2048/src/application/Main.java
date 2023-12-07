package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
	private static Stage stage;
	private static String userName;
	private static boolean isLogin, gameEnd, remainPane = false;
	private static int highestScore, returnScore = 0;
	private static ArrayList<int[]> rememberList = new ArrayList<>();
	
	public static boolean isRemainPane() {
		return remainPane;
	}

	public static void setRemainPane(boolean remainPane) {
		Main.remainPane = remainPane;
	}
	
	public static int getReturnScore() {
		return returnScore;
	}

	public void setReturnScore() {
		returnScore = 0;
	}

	public static ArrayList<int[]> getRememberList() {
		return rememberList;
	}

	public void setRememberList() {
		for(int [] rem : rememberList) {
			rememberList.remove(rem);
		}
	}

	public static boolean isGameEnd() {
		return gameEnd;
	}

	public void setGameEnd(boolean gameEnd) {
		this.gameEnd = gameEnd;
	}

	public static String getHighestScore() {
		String value = Integer.toString(highestScore);
		return value;
	}

	public void setHighestScore(int highestScore) {
		this.highestScore = highestScore;
	}

	public static boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public Connection getConn() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/111306062?useSSL=false", "root",
				"syy88824");
		return conn;
	}

	public void setUserName(String name) {
		this.userName = name;
	}

	public static String getUserName() {
		return userName;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		try {
			primaryStage.setTitle("2048");
			primaryStage.getIcons().add(new Image("https://dl2.macupdate.com/images/icons256/50935.png?d=1488812134"));
			stage = primaryStage;
			this.mainPage_show();
		} catch (Exception e) {
			Main.showMessageDialog(e.getMessage());
		}
	}

	public void mainPage_show() {
		try {
			Parent rootMain = FXMLLoader.load(getClass().getResource("/MainPage.fxml"));
			Scene scene = new Scene(rootMain);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	public void loginPage_show() throws Exception {
		try {
			Parent rootLogin = FXMLLoader.load(getClass().getResource("/LoginPage.fxml"));
			Scene scene = new Scene(rootLogin);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rankPage_show() throws Exception {
		try {
			Parent rootLogin = FXMLLoader.load(getClass().getResource("/RankPage.fxml"));
			Scene scene = new Scene(rootLogin);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void userPage_show() throws Exception {
		try {
			Parent rootUser = FXMLLoader.load(getClass().getResource("/UserPage.fxml"));
			Scene scene = new Scene(rootUser);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void rememberBrick(GridPane brickPane, ObservableList<Label> bricks, int score) {
		System.out.println("599    rememberBrick action");
		System.out.println("600    bricks.size = " + bricks.size() + "   ( = rememberList.size)");
		returnScore = score;
		for(Label brick : bricks) {
			int brickNum = Integer.parseInt(brick.getText());
			int brickCol = brickPane.getColumnIndex(brick);
			int brickRow = brickPane.getRowIndex(brick);
			System.out.println("Main141   brickNum = " + brickNum + "   brickCol = " + brickCol + "   brickRow = " + brickRow);
			int[] brickList = {brickNum, brickCol, brickRow};
			rememberList.add(brickList);
		}
		System.out.println("609    rememberList.size = " + rememberList.size());
	}
	
	public static void showMessageDialog(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Message");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
