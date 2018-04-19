package hzlbview;

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

public class hzlbtext {
	private TextField textField;
	private String ysmc;
	private final static int LIST_MAX_SIZE = 6;
	private final static int LIST_CELL_HEIGHT = 24;
	/** �����洢��ʾ ��������Ϣ�б� */
	private ObservableList<String> showCacheDataList = FXCollections.<String> observableArrayList();
	private ObservableList<String> CacheDataList1 = FXCollections.<String> observableArrayList();
	private ObservableList<String> CacheDataList2 = FXCollections.<String> observableArrayList();

	/** �������������� */
	private SimpleStringProperty inputContent = new SimpleStringProperty();

	/** �������ݺ���ʾ��pop */
	private Popup popShowList = new Popup();

	/** �������ݺ���ʾ����ʾ��Ϣ�б� */
	private ListView<String> autoTipList = new ListView<String>();
	
	public hzlbtext(TextField textField,String ysmc)
	{
		this.textField = textField;
		this.ysmc=ysmc;
		this.CacheDataList1.addAll("ר�Һ�","��ͨ��");
		this.CacheDataList2.addAll("��ͨ��");
	}
	
	public void init() {
		setCacheDataList();
		configure();
		showTipPop();
		confListener();
	}
	
	public void setCacheDataList()
	{
		String URL = "jdbc:sqlserver://localhost:1433;DatabaseName=db_hos";
	    String USER="sa";
	    String PASSWORD="l.y53543";
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection con = DriverManager.getConnection(URL,USER,PASSWORD);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("SELECT * FROM T_KSYS WHERE YSMC ='"+ysmc.trim()+"'");
            if(rs.next()) {      
            	if(rs.getBoolean("SFZJ")) {
            		showCacheDataList.clear();
            		showCacheDataList.addAll(CacheDataList1);
            	}
            	else {
            		showCacheDataList.clear();
            		showCacheDataList.addAll(CacheDataList2);
            	}
            }
            rs.close();
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
		if(autoTipList.getSelectionModel().getSelectedItem()!=null)
		inputContent.set(autoTipList.getSelectionModel().getSelectedItem());
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
		if(tipContent!=null) {
		this.setCacheDataList();
		if(!showCacheDataList.isEmpty()) {
			showTipPop();
		} else {
			popShowList.hide();
		}
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
