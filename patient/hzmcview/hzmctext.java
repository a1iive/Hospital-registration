package hzmcview;

import java.sql.*;
import java.util.ArrayList;

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

public class hzmctext {
	private ArrayList<hzmcclass> cachelist=new ArrayList<hzmcclass>();
	private TextField textField;
	private String ksmc;
	private String hzlb;
	boolean sfzj;
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
	
	public hzmctext(TextField textField,String ksmc,String hzlb)
	{
		this.textField = textField;
		this.ksmc=ksmc;
		this.hzlb=hzlb;
		this.sfzj=false;
	}
	
	public void init() {
		newCacheDataList("");
		configure();
		showTipPop();
		confListener();
	}
	public void setCacheDataList(String term) {
		showCacheDataList.clear();
		for(int i=0;i<cachelist.size();i++) {
			if(cachelist.get(i).gethzbh().startsWith(term)||cachelist.get(i).getpyzs().startsWith(term)) {
				showCacheDataList.add(cachelist.get(i).gethzbh()+" "+cachelist.get(i).gethzmc()+" "+cachelist.get(i).getpyzs());
			}
		}
	}
	public void newCacheDataList(String term)
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
            
            if(hzlb.equals("ר�Һ�"))sfzj=true;
            else sfzj=false;
            System.out.println(sfzj);
            ResultSet ks = stmt.executeQuery("SELECT * FROM T_KSXX WHERE KSMC ='"+ksmc+"'");
            if(ks.next()) {
            PreparedStatement pstmt=con.prepareStatement("SELECT * FROM T_HZXX WHERE KSBH='"+ks.getString("KSBH")+"' AND SFZJ=?");
            pstmt.setBoolean(1,sfzj);
            ResultSet rs = pstmt.executeQuery();  
            
            //System.out.println("open success");
            while(rs.next()) {
            	String str1=rs.getString("HZBH").trim();
                String str2=rs.getString("HZMC").trim();
            	String str3=rs.getString("PYZS").trim();
            	showCacheDataList.add(str1+" "+str2+" "+str3);
            	cachelist.add(new hzmcclass(str1,str2,str3));
            	}
            rs.close();
            pstmt.close();
            }
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
		inputContent.set(arr[1]);
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
