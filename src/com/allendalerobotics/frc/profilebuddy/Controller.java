package com.allendalerobotics.frc.profilebuddy;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final double pxPerIn = 1.851190476190476;
    private final double robotWidth = 33.0;
    private final double robotHeight = 28.0;

    @FXML private Canvas fieldCanvas;
    @FXML private ImageView fieldImageView;
    @FXML private Button chooseBtn;
    @FXML private Button listenBtn;
    @FXML private Button noListenBtn;
    @FXML private TextField addressTxt;
    @FXML private TextField tableTxt;

    private GraphicsContext graphics;
    private Image fieldImage;
    private NetworkListener networkListener;
    private boolean choosingLocation;
    private double startX;
    private double startY;

    private Properties properties;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.graphics = this.fieldCanvas.getGraphicsContext2D();
        this.fieldImageView.setImage(new Image(this.getClass().getResource("/Field.png").toString()));
        this.fieldImage = this.fieldImageView.getImage();
        this.chooseBtn.setDisable(false);
        this.listenBtn.setDisable(true);
        this.noListenBtn.setDisable(true);
        this.choosingLocation = false;

        try {
            this.properties = new Properties();
            this.properties.load(new FileInputStream("robot_config.properties"));
            this.addressTxt.setText(this.properties.getProperty(Config.CONFIG_ADDRESS));
            this.tableTxt.setText(this.properties.getProperty(Config.CONFIG_TABLE));
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    @FXML
    public void startListening() {

        if (this.addressTxt.getText().length() <= 1 || this.tableTxt.getText().length() <= 1) {
            return;
        }

        if (this.properties != null) {
            try {
                FileWriter writer = new FileWriter("robot_config.properties");
                properties.setProperty(Config.CONFIG_ADDRESS, this.addressTxt.getText());
                properties.setProperty(Config.CONFIG_TABLE, this.tableTxt.getText());
                properties.store(writer, "Properties file to persist data storage.");
                System.out.println("Storing properties...");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.networkListener = new NetworkListener(this.addressTxt.getText(), this.tableTxt.getText(), 100);
        ProfileBuddy.getMainStage().setOnCloseRequest((event) -> this.networkListener.kill());

        double widthRatio = (this.fieldImage.getWidth() / this.fieldImageView.getFitWidth());
        double heightRatio = (this.fieldImage.getHeight() / this.fieldImageView.getFitHeight());
        double inPerPxX = pxPerIn/widthRatio;
        double inPerPxY = pxPerIn/heightRatio;

        graphics.setStroke(Color.RED);
        graphics.setLineWidth(3.0);
        graphics.beginPath();
        graphics.moveTo(startX+((robotWidth*inPerPxX)/2), startY+((robotHeight*inPerPxY)/2));
        graphics.stroke();
        this.networkListener.start((x, y, vel) -> {

            if (x > 0.0 && y > 0.0) {
                System.out.println("New Values: " + x + " | " + y);
            }

            graphics.lineTo(startX+((robotWidth*inPerPxX)/2)+(inPerPxX*x),
                    startY+((robotHeight*inPerPxY)/2) + (inPerPxY*y));
            graphics.stroke();
        });

        this.listenBtn.setDisable(true);
        this.noListenBtn.setDisable(false);
    }

    @FXML
    public void stopListening() {
        if (this.networkListener != null) {
            this.networkListener.kill();
        }
        this.listenBtn.setDisable(false);
        this.noListenBtn.setDisable(true);
    }

    @FXML
    public void drawRectangle(MouseEvent event) {
        if (this.choosingLocation) {
            double widthRatio = (this.fieldImage.getWidth() / this.fieldImageView.getFitWidth());
            double heightRatio = (this.fieldImage.getHeight() / this.fieldImageView.getFitHeight());
            double inPerPxX = pxPerIn/widthRatio;
            double inPerPxY = pxPerIn/heightRatio;
            this.startX = event.getX();
            this.startY = event.getY()-((robotHeight*inPerPxY)/2);

            graphics.setFill(Color.PINK);
            graphics.clearRect(0, 0, this.fieldCanvas.getWidth(), this.fieldCanvas.getHeight());
            graphics.fillRect(this.startX, this.startY, robotWidth*inPerPxX, robotHeight*inPerPxY);

            this.listenBtn.setDisable(false);
            this.choosingLocation = false;
        }
    }

    @FXML
    public void chooseLocation() {
        this.listenBtn.setDisable(true);
        this.choosingLocation = true;
    }

}
