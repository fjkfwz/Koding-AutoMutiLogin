package net.lxy520.javafx;

import com.alibaba.fastjson.JSON;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.lxy520.config.Setting;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField txtPasswd;
    @FXML
    private TextField txtStart;
    @FXML
    private TextField txtInterval;
    @FXML
    private WebView webView;
    @FXML
    private TextArea logBox;

    private Setting setting = new Setting();
    public Controller() {
        System.out.println("程序启动中......");
    }

    private static  String SETUSERNAME;
    private static    String SETPASSWD;
    private static final   String COMMIT="$(\"button[testpath=login-button]\").trigger(\"submit\");";
    private static final String TRUNON = "$(\"button.kdbutton.turn-on.state-button.solid.green.medium.with-icon\").trigger(\"click\");";
    private static final String CHECKSTATE = "$(\"button.kdbutton.turn-on.state-button.solid.green.medium.with-icon\").prop(\"outerHTML\");";
    private static final String CHECKLOGIN = "$(\"input[name=username][testpath=login-form-username]\").prop(\"outerHTML\");";

    /**
     * 加载用户设置
     */
    public void loadSetting() {
        if (setting!=null){
            try {
                logBox.setText("加载用户设置......\n\n");
                txtUserName.setText(setting.getUserName());
                txtPasswd.setText(setting.getPasswd());
                txtStart.setText(setting.getStart());
                txtInterval.setText(setting.getInterval());
                logBox.appendText("加载用户设置成功\n\n");
            }catch (Exception e1){
                logBox.appendText("加载用户设置失败\n\n");
            }
        }
    }

    /**
     * 读取用户信息
     */
    public void readSetting() {
        try {
            File file = new File("setting.json");
            if (file.exists()) {
                String txtsetting = org.jsoup.Jsoup.parse(file, "utf-8").text();//读取文件
                if (!txtsetting.equals("")) {
                    Setting set = JSON.parseObject(txtsetting, Setting.class);//反序列化
                    if (set != null) {
                        setSetting(set);//设置
                        loadSetting();//加载
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存软件设置
     */
    private void saveSetting() {
        try {//保存程序设置信息
            setting.setUserName(txtUserName.getText());
            setting.setPasswd(txtPasswd.getText());
            setting.setStart(txtStart.getText());
            setting.setInterval(txtInterval.getText());
            String json = JSON.toJSONString(setting);
            ByteBuffer buffer = ByteBuffer.allocate(json.getBytes().length);
            buffer.put(json.getBytes());
            FileOutputStream fout = new FileOutputStream("setting.json");
            FileChannel fc = fout.getChannel();
            buffer.flip();
            fc.write(buffer);
            fc.close();
            fout.close();
            logBox.appendText("保存用户设置成功\n\n");
        } catch (Exception e) {
            logBox.appendText("保存用户设置失败\n\n");
        }
    }

    @FXML
    public void start(ActionEvent e) {
        if (txtUserName==null||"".equals(txtUserName.getText())){
            logBox.appendText("用户名不能为空\n");
            showMessage("提示","用户名不能为空");
           return;
        }
        if (txtPasswd==null||"".equals(txtPasswd.getText())){
            logBox.appendText("密码不能为空\n");
            showMessage("提示","密码不能为空");
            return;
        }
        SETUSERNAME="$(\"input[name=username][testpath=login-form-username]\").val(\""+txtUserName.getText()+"\");";
        SETPASSWD="$(\"input[name=password][testpath=login-form-password]\").val(\""+txtPasswd.getText()+"\");";
        Long start = Long.valueOf(txtStart.getText());
        Long interval = Long.valueOf(txtInterval.getText());
        saveSetting();
        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://koding.com/Login");
        logBox.appendText("系统启动中......\n");
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeAction());
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                webView.getEngine().load("https://koding.com/IDE");
                logBox.appendText("刷新页面......\n\n");
            }
        },1000*60*start,1000*60*interval);
    }

    /**
     * 确认是否要登陆,没有登陆则登陆
     */
    private void checkForLogin() {
        WebEngine webEngine1 = webView.getEngine();
        Object login = webEngine1.executeScript(CHECKLOGIN);
        if (login!=null&&!"".equals(login)&&!"undefined".equals(login)){
            logBox.appendText("登陆中......\n\n");
            webEngine1.executeScript(SETUSERNAME);
            webEngine1.executeScript(SETPASSWD);
            webEngine1.executeScript(COMMIT);
        }
    }

    /**
     * 消息框
     * @param message 消息
     */
    private void showMessage(String title ,String message){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        VBox v = new VBox();
        v.setAlignment(Pos.CENTER);
        Label l = new Label(message);
        l.setPadding(new Insets(0,0,10,0));
        Button btn = new Button("确定");
        btn.setOnAction(e-> stage.close());
        v.getChildren().addAll(l,btn);
        Scene s = new Scene(v,300,100);
        stage.setScene(s);
        stage.setTitle(title);
        stage.show();
    }

    /**
     * 确认VM是否关闭,关闭时启动VM
     */
    private void checkAndRestartVM() {
        WebEngine webEngine1 = webView.getEngine();
        Object trunon = webEngine1.executeScript(CHECKSTATE);
        if (trunon!=null&&!"".equals(trunon)&&!"undefined".equals(trunon)){
            logBox.appendText("重启虚拟机中......\n\n");
            webEngine1.executeScript(TRUNON);
        }
    }


    /**
     * 临听网页加载
     */
    private class ChangeAction implements ChangeListener{
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            logBox.appendText("页面有变动,刷新中.\n");
            if (newValue == Worker.State.SUCCEEDED) {
                checkForLogin();//确认是否要登陆,没有登陆则登陆
                checkAndRestartVM();//确认VM是否关闭,关闭时启动VM
            }
        }
    }

    /**
     * 配置信息
     * @param setting
     */
    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}
