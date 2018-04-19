package tuihaoview;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Window;

public class tuihaotext {
	private TextField textField;
	private String combox;
	private String brbh;
	private final static int LIST_MAX_SIZE = 6;
	private final static int LIST_CELL_HEIGHT = 24;
	/** �����洢��ʾ ��������Ϣ�б� */
	private ObservableList<String> showCacheDataList = FXCollections.<String> observableArrayList();
	
	/** �������������� */
	private SimpleStringProperty inputContent = new SimpleStringProperty();

	/** �������ݺ���ʾ��pop */
	private Popup popShowList = new Popup();

	/** �������ݺ���ʾ����ʾ��Ϣ�б� */
	private ListView<String> autoTipList = new ListView<String>();
	
	public tuihaotext(TextField textField,String combox,String brbh)
	{
		this.textField = textField;
		this.combox=combox;
		this.brbh=brbh;
	}
	
	public void init() {
		setCacheDataList("");
		configure();
		showTipPop();
		confListener();
	}
	
	public void setCacheDataList(String term)
	{
		showCacheDataList.clear();
		String URL = "jdbc:sqlserver://localhost:1433;DatabaseName=db_hos";
	    String USER="sa";
	    String PASSWORD="l.y53543";
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection con = DriverManager.getConnection(URL,USER,PASSWORD);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            if(combox.equals("�Һű��")) {
            	ResultSet rs = stmt.executeQuery("SELECT * FROM T_GHXX WHERE BRBH= '"+brbh+"' AND GHBH LIKE '"+term+"%'");                   
                //System.out.println("open success");
                while(rs.next()) {
                	String str1=rs.getString("GHBH").trim();
                    String str2=rs.getString("HZBH").trim();
                	String str3=rs.getString("YSBH").trim();            	          	
                	showCacheDataList.add(str1+" "+str2+" "+str3);
                	}
                rs.close();
            }
            else if(combox.equals("ҽ�����")){
            	ResultSet rs = stmt.executeQuery("SELECT * FROM T_GHXX WHERE BRBH= '"+brbh+"' AND YSBH LIKE '"+term+"%'");                   
                //System.out.println("open success");
                while(rs.next()) {
                	String str1=rs.getString("GHBH").trim();
                    String str2=rs.getString("HZBH").trim();
                	String str3=rs.getString("YSBH").trim();            	          	
                	showCacheDataList.add(str1+" "+str2+" "+str3);
                	}
                rs.close();
            }
            else if(combox.equals("���ֱ��")) {
            	ResultSet rs = stmt.executeQuery("SELECT * FROM T_GHXX WHERE BRBH= '"+brbh+"' AND HZBH LIKE '"+term+"%'");                   
                //System.out.println("open success");
                while(rs.next()) {
                	String str1=rs.getString("GHBH").trim();
                    String str2=rs.getString("HZBH").trim();
                	String str3=rs.getString("YSBH").trim();            	          	
                	showCacheDataList.add(str1+" "+str2+" "+str3);
                	}
                rs.close();
            }
            else System.out.println("combobox error");           
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		
	}
	/**
	 * 
	 * <p>
	 * �����齨
	 * </p>
	 * 
	 */
	private void configure()
	{
		
		popShowList.setAutoHide(true);
		popShowList.getContent().add(autoTipList);
  		autoTipList.setItems(showCacheDataList);
	}
	/**
	 * 
	 * <p>
	 * ��Ӽ����¼�
	 * </p>
	 * 
	 */
	private void confListener()
	{
		textField.textProperty().bindBidirectional(inputContent);

		inputContent.addListener(new ChangeListener<String>()
		{

			@Override
			public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue)
			{
				configureListContext(newValue);    //���ı��������ݷ����仯ʱ�ᴥ�����¼������ı��������ݽ���ƥ��
			}
		});

		autoTipList.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				selectedItem();
			}
		});

		autoTipList.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if(event.getCode() == KeyCode.ENTER) 
				{
					selectedItem();
				}
			}
		});
	}
	/**
	 * 
	 * <p>
	 * ��ȡѡ�е�list���ݵ������
	 * </p>
	 */
	private void selectedItem() {
		if(autoTipList.getSelectionModel().getSelectedItem()!=null) {
		String []arr=autoTipList.getSelectionModel().getSelectedItem().split("\\s+");
		if(combox.equals("�Һű��"))
			inputContent.set(arr[0]);
		else if(combox.equals("���ֱ��"))
			inputContent.set(arr[1]);
		else if(combox.equals("ҽ�����"))
			inputContent.set(arr[2]);
		else inputContent.set("");
		}
		else inputContent.set("");
		textField.end();
		popShowList.hide();
	}
	
	/**
	 * 
	 * <p>
	 * ���������������������ʾ��Ϣ
	 * </p>
	 * 
	 */
	private void configureListContext(String tipContent)
	{
		this.setCacheDataList(tipContent);
		if(!showCacheDataList.isEmpty()) {
			showTipPop();
		} else {
			popShowList.hide();
		}
	}
	/**
	 * 
	 * <p>
	 * ��ʾpop
	 * </p>
	 */
	public final void showTipPop()
	{
		autoTipList.setPrefWidth(textField.getWidth() - 3);
		if(showCacheDataList.size() < LIST_MAX_SIZE) {
			autoTipList.setPrefHeight(showCacheDataList.size() * LIST_CELL_HEIGHT + 3);
		} else {
			autoTipList.setPrefHeight(LIST_MAX_SIZE * LIST_CELL_HEIGHT + 3);
		}
		Window window = textField.getScene().getWindow();
		Scene scene = textField.getScene();
		Point2D fieldPosition = textField.localToScene(0, 0);
		popShowList.show(window, window.getX() + fieldPosition.getX() + scene.getX(), window.getY() + fieldPosition.getY() + scene.getY() + textField.getHeight());
		autoTipList.getSelectionModel().clearSelection();
		autoTipList.getFocusModel().focus(-1);
	}
}
