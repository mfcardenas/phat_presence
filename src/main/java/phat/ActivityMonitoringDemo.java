/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.*;
import phat.commands.GotoPath;
import phat.commands.MovArmCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateAccelerometerSensorCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.environment.SpatialEnvironmentAPI;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.accelerometer.XYAccelerationsChart;
import phat.server.ServerAppState;
import phat.server.commands.ActivateAccelerometerServerCommand;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

import java.util.logging.Logger;

/**
 * Activity Monitoring Demo.
 *
 * @author UCM
 */
public class ActivityMonitoringDemo implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(ActivityMonitoringDemo.class.getName());
    BodiesAppState bodiesAppState;
    DevicesAppState devicesAppState;
    ServerAppState serverAppState;

    @Override
    public void init(SimpleApplication app) {
        logger.info("init");
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        SpatialEnvironmentAPI seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2016, 2 ,18, 12, 30, 0);

        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.Basic);

        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House2", HouseFactory.HouseType.BrickHouse60m));

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);
        openObject("Patient1","Fridge1");

        //Se crean los personajes
        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Patient1");

        //Se crean los personajes
        bodiesAppState.createBody(BodiesAppState.BodyType.Young, "Patient2");

        final GotoPath gotopathCommand = new GotoPath(
                bodiesAppState, "Patient2", new Vector3f[] { new Vector3f(2.0f, 0f, 2f),
                new Vector3f(4f, 0, 4f), new Vector3f(4f, 0.0f, 2f), new Vector3f(2, 0, 2) },
                true);

//        (8.1337805, 1.4426634, 7.089235)

        //Se posicionan en la casa
        bodiesAppState.setInSpace("Patient1", "House2", "LivingRoom");

        bodiesAppState.setInSpace("Patient2", "House2", "LivingRoom");

        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);

        // Positions of sensor Acelerometers
        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor1"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient2", "sensor1",
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftHand));

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor2"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient2", "sensor2",
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightHand));

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor3"));
        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor4"));

        serverAppState = new ServerAppState();
        stateManager.attach(serverAppState);

        // Activate Acelerometers
        serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel", "sensor1"));
        serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel", "sensor2"));

        stateManager.attach(new AbstractAppState() {
            PHATApplication app;

            @Override
            public void initialize(AppStateManager asm, Application aplctn) {
                app = (PHATApplication) aplctn;

            }

            float cont = 0f;
            boolean fall = false;
            float timeToChange = 10f;
            boolean init = false;
            boolean traindata = false;

            @Override
            public void update(float f) {
                if (!init && !traindata) {
                    // Grafica del Sensor2
                    AccelerometerControl ac2 = devicesAppState.getDevice("sensor2")
                            .getControl(AccelerometerControl.class);
                    ac2.setMode(AccelerometerControl.AMode.GRAVITY_MODE);
                    XYAccelerationsChart chart2 = new XYAccelerationsChart("Data Accelerations Right Hand", "Local Sensor PHAT-SIM Rigth Hand", "m/s2",
                            "x,y,z");
                    ac2.add(chart2);
                    chart2.showWindow();
                    init = true;

                    // Grafica del Sensor1
                    AccelerometerControl ac1 = devicesAppState.getDevice("sensor1")
                            .getControl(AccelerometerControl.class);
                    ac1.setMode(AccelerometerControl.AMode.GRAVITY_MODE);
                    XYAccelerationsChart chart1 = new XYAccelerationsChart("Data Accelerations Left Hand", "Local Sensor PHAT-SIM Left Hand", "m/s2",
                            "x,y,z");
                    ac1.add(chart1);
                    chart1.showWindow();
                    init = true;

                }

                cont += f;
                if (cont > timeToChange && cont < timeToChange + 1f && !fall) {
                    System.out.println("Change to DrinkStanding:::" + String.valueOf(cont) + "-" + String.valueOf(f));
                    bodiesAppState.runCommand(new PlayBodyAnimationCommand("Patient", "DrinkStanding"));
                    fall = true;
                } else {
                    if (fall && cont > timeToChange + 10f) {
                        System.out.println("Change to WaveAttention:::" + String.valueOf(cont) + "-" + String.valueOf(f));
                        bodiesAppState.runCommand(new PlayBodyAnimationCommand("Patient", "WaveAttention"));
                        fall = false;
                        cont = 0;
                    }
                }
            }
        });

        bodiesAppState.runCommand(new TremblingHandCommand("Patient2", true, true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient2", true, false));

        bodiesAppState.runCommand(new MovArmCommand("Patient1", true, MovArmCommand.LEFT_ARM));
        bodiesAppState.runCommand(new MovArmCommand("Patient2", true, MovArmCommand.LEFT_ARM));

        goCloseToObject("Patient1", "Fridge1");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        bodiesAppState.runCommand(gotopathCommand);
        app.getCamera().setLocation(new Vector3f(7f, 7.25f, 3.1f));
        app.getCamera().setRotation(new Quaternion(0.44760564f, -0.5397514f, 0.4186186f, 0.5771274f));

    }

    /**
     * Go Up Hand
     * @param idPersont person or patient to actions
     * @param door object to close
     */
    private void goCloseToObject(final String idPersont, final String door) {
        logger.info("goToClose: " + idPersont + ", object: " + door);
        GoCloseToObjectCommand gtc = new GoCloseToObjectCommand(idPersont, door, new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if (command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new MovArmCommand(idPersont, false, MovArmCommand.LEFT_ARM));
                    bodiesAppState.runCommand(new AlignWithCommand(idPersont, door));
                    bodiesAppState.runCommand(new CloseObjectCommand(idPersont, door));
                    bodiesAppState.runCommand(new MovArmCommand(idPersont,true, MovArmCommand.LEFT_ARM));
                }
            }
        });
        gtc.setMinDistance(0.1f);
        bodiesAppState.runCommand(gtc);
    }

    /**
     * Open Door Actions.
     * @param idPersont
     * @param door
     */
    private void openObject(final String idPersont, final String door) {
        OpenObjectCommand gtc = new OpenObjectCommand(idPersont, door);
        bodiesAppState.runCommand(gtc);
    }

    /**
     * Main Class, executios Test.
     * @param args
     */
    public static void main(String[] args) {
        ActivityMonitoringDemo app = new ActivityMonitoringDemo();

        PHATApplication phat = new PHATApplication(app);
        phat.setDisplayFps(false);
        phat.setDisplayStatView(false);
        phat.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Activity Monitoring Demo");
        settings.setWidth(640);
        settings.setHeight(580);
        phat.setSettings(settings);
        phat.start();
    }
}