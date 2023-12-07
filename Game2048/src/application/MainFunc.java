package application;


import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainFunc implements Initializable{
	
	Main m = new Main();
	private ObservableList<Label> bricks = FXCollections.observableArrayList();
	private int score = 0;
	private int success = 0;
	private boolean[] isEnd = new boolean[4];
	
    @FXML
    private GridPane mainPane;
	
    @FXML
    private GridPane brickPane;
    
	@FXML
    private Button btnLoginPage;

    @FXML
    private Button btnRank;

    @FXML
    private Label lblScore;
	
    @FXML
    void loginPage(ActionEvent event) throws Exception {
    	m.rememberBrick(brickPane, bricks, Integer.parseInt(lblScore.getText()));
    	System.out.println("56   rm(login)");
    	if(btnLoginPage.getText().equals("Login")) {
    		
    		m.loginPage_show();
    	}else if(btnLoginPage.getText().equals("User")) {
    		m.userPage_show();
    	}   	
    }

    @FXML
    void rank(ActionEvent event) throws Exception {
    	m.rememberBrick(brickPane, bricks, Integer.parseInt(lblScore.getText()));
    	m.rankPage_show();
    }
    
    @FXML
    void brickMove(KeyEvent event) {
    	if(event.getCode() == KeyCode.W) {
    		this.howBrickMove("up");
    	}else if(event.getCode() == KeyCode.S) {
    		this.howBrickMove("down");
    	}else if(event.getCode() == KeyCode.A) {
    		this.howBrickMove("left");
    	}else if(event.getCode() == KeyCode.D) {
    		this.howBrickMove("right");
    	}
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		System.out.println("rememberList.size = " + Main.getRememberList().size());
		if(Main.getRememberList().size() == 0) {
			this.newBrick();
		}/*else if(Main.getRememberList().size() == 1) {
			if (!Main.isRemainPane()) {
				System.out.println("87    mainReturn == 1   newBrick");
				
			}
		} */else {
			System.out.println("90   int[] = ");
			for (int[] rem : Main.getRememberList()) {
				System.out.print("lbl.text = " + rem[0]);
				System.out.print("   lbl.col = " + rem[1]);
				System.out.print("   lbl.row = " + rem[2]);
				System.out.println(" ");
			}

			for (int[] rm : Main.getRememberList()) {
				Label lbl = new Label(Integer.toString(rm[0]));
				brickPane.add(lbl, rm[1], rm[2]);
				bricks.add(lbl);
				this.lblStyle(lbl);
			}
			//m.setRememberList();

		}
		mainPane.setOnKeyPressed(this::brickMove);
		mainPane.setFocusTraversable(true);
		mainPane.requestFocus();
		if (Main.isLogin()) {
			btnLoginPage.setText("User");
		} else {
			btnLoginPage.setText("Login");
		}
	}
	
	//產生新方塊(2或4)
	public void newBrick() {
		boolean brickExist = false;  //用以確認一開始該格中有無方塊(預設為 無 )
		int goalRow, goalColumn;
		do {
			goalRow = Math.round((float)Math.random()*3);
			goalColumn = Math.round((float)Math.random()*3);
			//確認brickPane中的所有方塊的位置是否會跟新的方塊撞位置 會的話就跳出迴圈並重選一個位置
			for(Label brick:bricks) {
				if(brickPane.getRowIndex(brick) == goalRow && brickPane.getColumnIndex(brick) == goalColumn) {	
					brickExist = true;
					break;
				}else {
					brickExist = false;
				}
			}
		}while(brickExist);
		if(!brickExist) {
			Label lblNewBrick = new Label();;
			if(Math.random() < 0.9) {
				lblNewBrick.setText("2");
			}else {
				lblNewBrick.setText("4");
			}
			brickPane.add(lblNewBrick, goalColumn, goalRow);	
			this.lblStyle(lblNewBrick);	
			bricks.add(lblNewBrick);
		}
	}
	
	//當玩家的遊戲結束(已經無法merge bricks時)
	public void gameEnd() {
		m.setRemainPane(false);
		for(Label brick : bricks) {
			brick.getStyleClass().add("brickEnd");   //讓所有bricks無法再動作
		}
		//如果使用者有登入的話 看是否創下新的最高分 若創下的話要記錄到資料表中
		if(Main.isLogin()) {
			try {
				String userName = Main.getUserName();
				PreparedStatement pstmt = m.getConn().prepareStatement("SELECT `highestScore` FROM `game2048` WHERE `userName` = ?");
				pstmt.setString(1, userName);
				ResultSet result = pstmt.executeQuery();
				result.next();
				if(result.getInt(1) < score) {
					pstmt = m.getConn().prepareStatement("UPDATE `game2048` SET `highestScore`= ? WHERE `userName` = ?");
					pstmt.setInt(1, score);
					pstmt.setString(2, userName);
					pstmt.executeUpdate();
					m.setHighestScore(score);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void howBrickMove(String keyCode) {
		m.setRemainPane(true);
		this.mergeBrick(keyCode, true);
		//mergeBrick之後如果"第一次"得到2048即可得到messageDialogue
		for(Label brick : bricks) {
			if(brick.getText().equals("2048")) {
				success++;
			}
		}		
		if(success == 1) {
			Main.showMessageDialog("Congratulations ! You reach 2048 !");
		}
		this.algoMove(keyCode);
		lblScore.setText(Integer.toString(score));
		if(!Main.isGameEnd()) {
			if(bricks.size() != 16) {
				this.newBrick();
			}			
		}
		//當bricks.size為16時判斷遊戲結束或尚可合併bricks
		if(bricks.size() == 16) {
			for(int i = 0; i < 4; i++) {
				isEnd[i] = true;
			}
			//判斷bricks是否仍可以上 下 左 右鍵合併
			this.mergeBrick("up", false);
			this.mergeBrick("down", false);
			this.mergeBrick("left", false);
			this.mergeBrick("right", false);
			int endCount = 0;
			for(boolean end : isEnd) {
				if(end == true) {
					endCount++;
				}
			}
			//若上下左右皆無法合併的話 遊戲結束 所有的方塊變成灰色
			if(endCount == 4) {
				this.gameEnd();	
				Main.showMessageDialog("The game is end. \nYour score is " + score);
				m.setGameEnd(true);		
			}
		}
	}
	
	private void lblStyle(Label lbl) {
		String style = "brick" + lbl.getText();
		lbl.getStyleClass().add(style);
	}
	
	//移動bricks的方法(無合併)
	private void algoMove(String position) {
		switch(position) {
			case "up":
				for (int i = 0; i < 4; i++) {
					ObservableList<Label> listColumnBrick = FXCollections.observableArrayList();
					for (Label brick : bricks) {
						if(brickPane.getColumnIndex(brick) == i) {
							listColumnBrick.add(brick);
						}
					}
					//分別移動特定一行中的每一格
					boolean[] columnExist = {false, false, false, false};
					int columnBrick = listColumnBrick.size(); //columnBrick = 一行中有幾個方塊
					//將每行中有方塊的格子標為true
					for(Label colBrick : listColumnBrick) {
						for(int k = 0; k < 4; k++) {
							if(brickPane.getRowIndex(colBrick) == k) {
								columnExist[k] = true;
							}
						}					
					}
					if(columnBrick == 3) { //當一行有三個方塊時
						for(int l3 = 0; l3 < 4; l3++) {
							if(columnExist[l3] == false) { //找出一行的哪格沒有方塊
								for(Label colBrick3 : listColumnBrick) {
									int rowIndex3 = brickPane.getRowIndex(colBrick3);
									if(rowIndex3 > l3) {
										brickPane.setRowIndex(colBrick3, (rowIndex3 - 1)); //將空格下面的方塊往上移一格
									}
								}
							}
						}
					}else if(columnBrick == 2) {
						int l2_1 = 0;
						int l2_2 = 0;
						boolean secRound = false;
						for(int l2 = 0; l2 < 4; l2++) {
							if(columnExist[l2] == false) {
								if(!secRound) {
									l2_1 = l2;
									secRound = true;
								}else {
									l2_2 = l2;
								}							
							}
						}
						for(Label colBrick2 : listColumnBrick) {
							int rowIndex2 = brickPane.getRowIndex(colBrick2);
							if(rowIndex2 > l2_2) {
								brickPane.setRowIndex(colBrick2, (rowIndex2 - 2));
							}else if (rowIndex2 > l2_1) {
								brickPane.setRowIndex(colBrick2, (rowIndex2 - 1));
							}
						}
					}else if(columnBrick == 1) {
						Label colBrick1 = listColumnBrick.get(0);
						brickPane.setRowIndex(colBrick1, 0); //將空格下面的方塊往上移		
					}
				}
				break;
			case "down":
				for (int i = 0; i < 4; i++) {
					ObservableList<Label> listColumnBrick = FXCollections.observableArrayList();
					for (Label brick : bricks) {
						if(brickPane.getColumnIndex(brick) == i) {
							listColumnBrick.add(brick);
						}
					}
					//分別移動特定一行中的每一格
					boolean[] columnExist = {false, false, false, false};
					int columnBrick = listColumnBrick.size(); //columnBrick = 一行中有幾個方塊
					//將每行中有方塊的格子標為true
					for(Label colBrick : listColumnBrick) {
						for(int k = 0; k < 4; k++) {
							if(brickPane.getRowIndex(colBrick) == k) {
								columnExist[k] = true;
							}
						}					
					}
					if(columnBrick == 3) { //當一行有三個方塊時
						for(int l3 = 0; l3 < 4; l3++) {
							if(columnExist[l3] == false) { //找出一行的哪格沒有方塊
								for(Label colBrick3 : listColumnBrick) {
									int rowIndex3 = brickPane.getRowIndex(colBrick3);
									if(rowIndex3 < l3) {
										brickPane.setRowIndex(colBrick3, (rowIndex3 + 1)); //將空格下面的方塊往上移一格
									}
								}
							}
						}
					}else if(columnBrick == 2) {
						int l2_1 = 0;
						int l2_2 = 0;
						boolean secRound = false;
						for(int l2 = 0; l2 < 4; l2++) {
							if(columnExist[l2] == false) {
								if(!secRound) {
									l2_2 = l2;
									secRound = true;
								}else {
									l2_1 = l2;
								}							
							}
						}
						for(Label colBrick2 : listColumnBrick) {
							int rowIndex2 = brickPane.getRowIndex(colBrick2);
							if(rowIndex2 < l2_2) {
								brickPane.setRowIndex(colBrick2, (rowIndex2 + 2));
							}else if (rowIndex2 < l2_1) {
								brickPane.setRowIndex(colBrick2, (rowIndex2 + 1));
							}
						}
					}else if(columnBrick == 1) {
						Label colBrick1 = listColumnBrick.get(0);
						brickPane.setRowIndex(colBrick1, 3); //將空格下面的方塊往上移		
					}
				}
				break;
			case "left":
				for (int i = 0; i < 4; i++) {
					ObservableList<Label> listRowBrick = FXCollections.observableArrayList();
					for (Label brick : bricks) {
						if(brickPane.getRowIndex(brick) == i) {
							listRowBrick.add(brick);
						}
					}
					//分別移動特定一列中的每一格
					boolean[] rowExist = {false, false, false, false};
					int rowBrick = listRowBrick.size(); //rowBrick = 一列中有幾個方塊
					//將每列中有方塊的格子標為true
					for(Label rBrick : listRowBrick) {
						for(int k = 0; k < 4; k++) {
							if(brickPane.getColumnIndex(rBrick) == k) {
								rowExist[k] = true;
							}
						}					
					}
					if(rowBrick == 3) { //當一列有三個方塊時
						for(int l3 = 0; l3 < 4; l3++) {
							if(rowExist[l3] == false) { //找出一列的哪格沒有方塊
								for(Label columnBrick3 : listRowBrick) {
									int columnIndex3 = brickPane.getColumnIndex(columnBrick3);
									if(columnIndex3 > l3) {
										brickPane.setColumnIndex(columnBrick3, (columnIndex3 - 1)); //將空格下面的方塊往左移一格
									}
								}
							}
						}
					}else if(rowBrick == 2) {
						int l2_1 = 0;
						int l2_2 = 0;
						boolean secRound = false;
						for(int l2 = 0; l2 < 4; l2++) {
							if(rowExist[l2] == false) {
								if(!secRound) {
									l2_1 = l2;
									secRound = true;
								}else {
									l2_2 = l2;
								}							
							}
						}
						for(Label rowBrick2 : listRowBrick) {
							int columnIndex2 = brickPane.getColumnIndex(rowBrick2);
							if(columnIndex2 > l2_2) {
								brickPane.setColumnIndex(rowBrick2, (columnIndex2 - 2));
							}else if (columnIndex2 > l2_1) {
								brickPane.setColumnIndex(rowBrick2, (columnIndex2 - 1));
							}
						}
					}else if(rowBrick == 1) {
						Label rowBrick1 = listRowBrick.get(0);
						brickPane.setColumnIndex(rowBrick1, 0); //將空格下面的方塊往左移		
					}
				}
				break;
			case "right":
				for (int i = 0; i < 4; i++) {
					ObservableList<Label> listRowBrick = FXCollections.observableArrayList();
					for (Label brick : bricks) {
						if(brickPane.getRowIndex(brick) == i) {
							listRowBrick.add(brick);
						}
					}
					//分別移動特定一行中的每一格
					boolean[] rowExist = {false, false, false, false};
					int rowBrick = listRowBrick.size(); //rowBrick = 一行中有幾個方塊
					//將每行中有方塊的格子標為true
					for(Label rBrick : listRowBrick) {
						for(int k = 0; k < 4; k++) {
							if(brickPane.getColumnIndex(rBrick) == k) {
								rowExist[k] = true;
							}
						}					
					}
					if(rowBrick == 3) { //當一行有三個方塊時
						for(int l3 = 0; l3 < 4; l3++) {
							if(rowExist[l3] == false) { //找出一行的哪格沒有方塊
								for(Label rowBrick3 : listRowBrick) {
									int columnIndex3 = brickPane.getColumnIndex(rowBrick3);
									if(columnIndex3 < l3) {
										brickPane.setColumnIndex(rowBrick3, (columnIndex3 + 1)); //將空格下面的方塊往上移一格
									}
								}
							}
						}
					}else if(rowBrick == 2) {
						int l2_1 = 0;
						int l2_2 = 0;
						boolean secRound = false;
						for(int l2 = 0; l2 < 4; l2++) {
							if(rowExist[l2] == false) {
								if(!secRound) {
									l2_2 = l2;
									secRound = true;
								}else {
									l2_1 = l2;
								}							
							}
						}
						for(Label rowBrick2 : listRowBrick) {
							int columnIndex2 = brickPane.getColumnIndex(rowBrick2);
							if(columnIndex2 < l2_2) {
								brickPane.setColumnIndex(rowBrick2, (columnIndex2 + 2));
							}else if (columnIndex2 < l2_1) {
								brickPane.setColumnIndex(rowBrick2, (columnIndex2 + 1));
							}
						}
					}else if(rowBrick == 1) {
						Label colBrick1 = listRowBrick.get(0);
						brickPane.setColumnIndex(colBrick1, 3); //將空格下面的方塊往上移		
					}
				}
				break;
		}
		//所有brick合併移動完後套上對應的style
		for(Label lbl : bricks) {
			this.lblStyle(lbl);
		}
	}
	
	//移動+合併bricks
	//doMerge == true -> 要做合併格子的動作			doMerge == false -> 單純判斷格子還可否合併 但不合併
	private void mergeBrick(String position, boolean doMerge) {
		this.algoMove(position);
		switch(position) {
			case "up":				
				for(int i = 0; i < 4; i++) {    //一行一行merge(這個迴圈把所有程式碼包起來)
					Label[] lblList = {null, null, null, null};    //第i行的時候每一列有沒有(是哪個label)
					for (Label brick : bricks) {
						if (brickPane.getColumnIndex(brick) == i) {
							for (int h = 0; h < 4; h++) {
								if (brickPane.getRowIndex(brick) == h) {
									lblList[h] = brick;
								}
							}
						}
					}
					for(int j = 0; j < 3; j++) {    //j代表label是第幾列 從第0列數到第2列 (要merge bricks的話最多是第3列併到第2列)
						Label lbl1 = lblList[j];    //要合併的brick
						Label lbl2 = lblList[(j+1)];    //要被合併的brick
					    //如果lbl1和lbl2都有label且為同種brick的話可合併
						if(lbl1 != null && lbl2 != null) {
							int num = Integer.parseInt(lbl1.getText());
							if(num == Integer.parseInt(lbl2.getText())) {
								if(doMerge) {
									this.mergeAction(lblList, lbl1, lbl2, "+", j);
								}else {
									isEnd[0] = false;
								}
							}
						}
					}
				}
				break;
			case "down":
				for(int i = 0; i < 4; i++) {
					Label[] lblList = {null, null, null, null};
					for(Label brick : bricks) {
						if(brickPane.getColumnIndex(brick) == i) {
							for(int h = 0; h < 4; h++) {
								if(brickPane.getRowIndex(brick) == h) {
									lblList[h] = brick;
								}
							}
						}
					}
					for(int j = 3; j > 0; j--) {
						Label lbl1 = lblList[j];
						Label lbl2 = lblList[(j-1)];
						if(lbl1 != null && lbl2 != null) {
							int num = Integer.parseInt(lbl1.getText());
							if(num == Integer.parseInt(lbl2.getText())) {
								if(doMerge) {
									this.mergeAction(lblList, lbl1, lbl2, "-", j);
								}else {
									isEnd[1] = false;
								}
							}
						}
					}
				}
				break;
			case "left":
				for(int i = 0; i < 4; i++) {    //一行一行merge(這個迴圈把所有程式碼包起來)
					Label[] lblList = {null, null, null, null};    //第i行的時候每一列有沒有(是哪個label)
					for (Label brick : bricks) {
						if (brickPane.getRowIndex(brick) == i) {
							for (int h = 0; h < 4; h++) {
								if (brickPane.getColumnIndex(brick) == h) {
									lblList[h] = brick;
								}
							}
						}
					}
					for(int j = 0; j < 3; j++) {    //j代表label是第幾列 從第0列數到第2列 (要merge bricks的話最多是第3列併到第2列)
						Label lbl1 = lblList[j];    //要合併的brick
						Label lbl2 = lblList[(j+1)];    //要被合併的brick
					    //如果lbl1和lbl2都有label且為同種brick的話可合併
						if(lbl1 != null && lbl2 != null) {
							int num = Integer.parseInt(lbl1.getText());
							if(num == Integer.parseInt(lbl2.getText())) {
								if(doMerge) {
									this.mergeAction(lblList, lbl1, lbl2, "+", j);
								}else {
									isEnd[2] = false;
								}
							}
						}
					}
				}
				break;
			case "right":
				for(int i = 0; i < 4; i++) {
					Label[] lblList = {null, null, null, null};
					for(Label brick : bricks) {
						if(brickPane.getRowIndex(brick) == i) {
							for(int h = 0; h < 4; h++) {
								if(brickPane.getColumnIndex(brick) == h) {
									lblList[h] = brick;
								}
							}
						}
					}
					for(int j = 3; j > 0; j--) {
						Label lbl1 = lblList[j];
						Label lbl2 = lblList[(j-1)];
						if(lbl1 != null && lbl2 != null) {
							int num = Integer.parseInt(lbl1.getText());
							if(num == Integer.parseInt(lbl2.getText())) {
								if(doMerge) {
									this.mergeAction(lblList, lbl1, lbl2, "-", j);
								}else {
									isEnd[3] = false;
								}
							}
						}
					}
				}
				break;
		}
	}
	
	//合併格子的動作
	public void mergeAction(Label[] list, Label lbl1, Label lbl2, String symb, int j) {	
		int num = Integer.parseInt(lbl1.getText());
		lbl1.setText(Integer.toString(num*2));
		score += (num*2);
		lbl2.setVisible(false);
		bricks.remove(lbl2);
		if(symb.equals("+")) {
			list[(j+1)] = null;
		}else if(symb.equals("-")) {
			list[(j-1)] = null;
		}
	}
	
}