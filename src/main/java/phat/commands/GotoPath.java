package phat.commands;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import phat.body.BodiesAppState;
import phat.body.commands.GoToCommand;

import java.util.Vector;

/**
 * Class Go to Path.
 */
public class GotoPath extends PHATCommand implements PHATCommandListener {
    Vector<GoToCommand> gotos = new Vector<GoToCommand>();
    Vector<GoToCommand> gotostorun = null;
    BodiesAppState body;
    boolean loop = false;
    boolean initiated = false;
    private Vector3f[] positions;
    private int resumingIndex;
    private String commandid;
    private GoToCommand currentCommand;

    public GotoPath(BodiesAppState body, String id, Vector3f[] positions, boolean loop) {
        this.body = body;
        gotos = createGoTos(id, positions);
        gotostorun = new Vector<GoToCommand>(gotos);
        this.positions = positions;
        this.commandid = id;
        this.loop = loop;
    }

    Vector<GoToCommand> createGoTos(String id, Vector3f[] positions) {
        Vector<GoToCommand> gotos = new Vector<GoToCommand>();
        for (Vector3f pos : positions) {
            final Vector3f finalPos = pos;
            GoToCommand gt = new GoToCommand(id, new phat.util.Lazy<Vector3f>() {
                @Override
                public Vector3f getLazy() {
                    return finalPos;
                }
            }, this);
            gotos.add(gt);
        }
        return gotos;
    }

    @Override
    public void runCommand(Application app) {
        if (!initiated) {
            // launches the first command. Afterwards, this is useless.
            runNextGoto();
            initiated = true;
        }
    }

    public void resume() {
        if (currentCommand != null && currentCommand.getState().equals(State.Interrupted)) {
            gotostorun = createGoTos(commandid, positions);
            // recreates the structure to recreate the last interrupted command
            for (int k = 0; k < resumingIndex; k++)
                gotostorun.removeElementAt(0);
            resumingIndex=resumingIndex-1;// situation previous to the last command
            runNextGoto();
        }
    }

    @Override
    public void interruptCommand(Application app) {
        if (currentCommand != null)
            currentCommand.interruptCommand(app);
        this.setState(State.Interrupted);
    }

    private void runNextGoto() {
        currentCommand = gotostorun.firstElement();
        resumingIndex++;

        gotostorun.removeElementAt(0);
        if (gotostorun.isEmpty() && loop) {
            gotostorun = createGoTos(commandid, positions);
            body.runCommand(currentCommand);
            resumingIndex = 0;
        } else if (!gotostorun.isEmpty()) {
            body.runCommand(currentCommand);
        } else
            this.setState(State.Success);

    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command.getState().equals(State.Success)) {
            runNextGoto();
        }

    }
}
