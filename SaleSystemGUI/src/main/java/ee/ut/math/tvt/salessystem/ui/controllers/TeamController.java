package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;

import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class TeamController implements Initializable{
    @FXML
    private Text teamname = new Text();
    private String teamName;

    @FXML
    private Text leader = new Text();
    private String teamLeader;
    @FXML
    private Text leaderemail = new Text();
    private String teamLeaderEmail;

    @FXML
    private Text teammembers = new Text();
    private String teamMember1;
    private String teamMember2;
    private String teamMember3;
    private String teamMember4;
    private String teamLogo;
    @FXML
    private ImageView teamlogo;
    private static final Logger log = LogManager.getLogger(TeamController.class);

    public TeamController() throws IOException {

        String filePath = "application.properties";
        Properties pros = new Properties();


        InputStream ip = getClass().getClassLoader().getResourceAsStream(filePath);

        try {
            pros.load(ip);
            teamName = pros.getProperty("teamName");
            teamMember1 = pros.getProperty("teamMember1");
            teamMember2 = pros.getProperty("teamMember2");
            teamMember3 = pros.getProperty("teamMember3");
            teamMember4 = pros.getProperty("teamMember4");
            teamLeader = pros.getProperty("teamLeader");
            teamLeaderEmail = pros.getProperty("teamLeaderEmail");
            teamLogo = pros.getProperty("teamLogo");
            ip.close();
        } catch (Exception e){
            log.error(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Error reading 'application.properties' file for Team Tab");
            alert.setContentText("Check if 'application.properties' file exists and is reachable. (src/main/resources/application.properties)");
            alert.showAndWait();
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources)  {
        log.debug("TeamController initialized");
        teamname.setText(teamName);
        teammembers.setText(teamMember1 + "\n" + teamMember2 + "\n" + teamMember3 + "\n" + teamMember4);
        leader.setText(teamLeader);
        leaderemail.setText(teamLeaderEmail);
        teamlogo.setImage(new Image(teamLogo));
    }

}