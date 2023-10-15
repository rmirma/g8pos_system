package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;

import java.util.Properties;
import java.util.ResourceBundle;

public class TeamController implements Initializable{
    @FXML
    private Text teamname = new Text();
    private String teamName;

    @FXML
    private Text mail = new Text();
    private String Mail;

    @FXML
    private Text teammembers = new Text();
    private String teamMember1;
    private String teamMember2;
    private String teamMember3;
    private String teamMember4;

    private String teamLogo;
    @FXML
    private ImageView teamlogo;

    public TeamController() throws IOException {

        String filePath = "application.properties";
        Properties pros = new Properties();


        InputStream ip = getClass().getClassLoader().getResourceAsStream("application.properties");

        if (ip != null) {

            pros.load(ip);
            teamName = pros.getProperty("teamName");
            teamMember1 = pros.getProperty("teamMember1");
            teamMember2 = pros.getProperty("teamMember2");
            teamMember3 = pros.getProperty("teamMember3");
            teamMember4 = pros.getProperty("teamMember4");
            Mail = pros.getProperty("teamEmail");
            teamLogo = pros.getProperty("teamLogo");
            ip.close();
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources)  {
        this.teamname.setText(teamName);
        this.teammembers.setText(teamMember1 + "\n" + teamMember2 + "\n" + teamMember3 + "\n" + teamMember4);
        this.mail.setText(Mail);
        this.teamlogo.setImage(new Image(teamLogo));
    }

}