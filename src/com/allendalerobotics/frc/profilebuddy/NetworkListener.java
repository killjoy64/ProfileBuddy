package com.allendalerobotics.frc.profilebuddy;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NetworkListener implements Runnable {

    private Thread thread;
    private boolean running;
    private String address;
    private String tableName;
    private int msDelay;

    private OnReceiveListener receiveListener;

    public NetworkListener(String address, String tableName, int msDelay) {
        this.thread = new Thread(this, "NetworkUpdater");
        this.running = false;
        this.address = address;
        this.tableName = tableName;
        this.msDelay = msDelay;
    }

    public void start(OnReceiveListener receiveListener) {
        this.receiveListener = receiveListener;
        this.thread.start();
        this.running = true;
    }

    public void kill() {
        try {
            this.running = false;
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        System.out.println("Started NetworkListener thread.");

        NetworkTableInstance instance = NetworkTableInstance.getDefault();
        instance.startClient(this.address);

        while (running) {
            NetworkTable table = instance.getTable(this.tableName);

            if (instance.isConnected()) {
                double x = table.getEntry(Config.ROBOT_X_COORDINATE).getDouble(0.0);
                double y = table.getEntry(Config.ROBOT_Y_COORDINATE).getDouble(0.0);
                double vel = table.getEntry(Config.ROBOT_VELOCITY).getDouble(0.0);

                this.receiveListener.onRobotCoordinateReceive(x, y, vel);
            }

            try {
                Thread.sleep(this.msDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        NetworkTableInstance.getDefault().stopClient();

    }
}

interface OnReceiveListener {
    void onRobotCoordinateReceive(double x, double y, double vel);
}
