package org.rev317.min.callback;

import org.parabot.core.Context;
import org.parabot.core.reflect.RefClass;
import org.rev317.min.Loader;
import org.rev317.min.accessors.Client;
import org.rev317.min.api.events.GameActionEvent;
import org.rev317.min.api.methods.Game;
import org.rev317.min.debug.DActions;
import org.rev317.min.script.ScriptEngine;

/**
 * @author Everel, JKetelaar, Matt123337
 */
public class MenuAction {

    private static final String[][] outputs = {
            {
                    "[index: %d, action1: %d, action2: %d, action3: %d, action4: %d, id: %d]",
                    "[id: %d, action1: %d, action2: %d, action3: %d, action4: %d, index: %d]"
            },
            {
                    "[index: %d, action1: %d, action2: %d, action3: %d, id: %d]",
                    "[id: %d, action1: %d, action2: %d, action3: %d, index: %d]"
            }
    };
    private static int currentOutputIndex = 0;

    public static void intercept() {
        int index = getActionIndex();
        int outputIndex = 0;

        Client client = Loader.getClient();
        int action1 = client.getMenuAction1()[index];
        int action2 = client.getMenuAction2()[index];
        int action3 = client.getMenuAction3()[index];
        int action4 = 0;
        int actionId = client.getMenuActionId()[index];
        if (DActions.debugActions()) {
            if (Game.hasAction4()) {
                action4 = client.getMenuAction4()[index];
                System.out.println(String.format(outputs[0][outputIndex], index, action1, action2, action3, action4, actionId));
            } else {
                System.out.println(String.format(outputs[1][outputIndex], index, action1, action2, action3, actionId));
            }
        }

        final GameActionEvent actionEvent = new GameActionEvent(actionId, action1, action2, action3, action4, index);
        ScriptEngine.getInstance().dispatch(actionEvent);
    }

    /**
     * Sets the current output index
     *
     * @param currentOutputIndex
     */
    public static void setCurrentOutputIndex(int currentOutputIndex) {
        if (currentOutputIndex > outputs.length - 1) {
            currentOutputIndex = 0;
        }
        MenuAction.currentOutputIndex = currentOutputIndex;
    }

    public static String[][] getOutputs() {
        return outputs;
    }

    private static int getActionIndex(){
        RefClass client = new RefClass(Context.getInstance().getClient());

        if (client.getField("TiSK").asInt() != 0) {
            return -1;
        }
        int i = client.getField("gDnF").asInt();
        if ((client.getField("PjW").asInt() == 1) && (client.getField("PJ").asInt() >= 516) && (client.getField("Ca").asInt() >= 160) && (client.getField("PJ").asInt() <= 765) && (client.getField("Ca").asInt() <= 205)) {
            i = 0;
        }
        int j = client.getField("Wytv").asInt();
        int k = client.getField("jNPX").asInt();
        int m = client.getField("Za").asInt();
        int i1 = client.getField("PJ").asInt();
        int i3 = client.getField("Ca").asInt();

        if (client.getField("Jmcq").asBoolean()) {
            int Jmcq = client.getField("Jmcq").asInt();
            switch (Jmcq) {
                case 0:
                    i1 -= 4;
                    i3 -= 4;
                    break;
                case 1:
                    i1 -= 519;
                    i3 -= 168;
                    break;
                case 2:
                    i1 -= 5;
                    i3 -= 338;
                    break;
                case 3:
                    i1 -= 519;
                    i3 += 0;
            }
            int i4 = -1;
            int i11;
            int ot = client.getField("ot").asInt();

            for (int i6 = 0; i6 < ot; i6++)
            {
                i11 = k + 31 + (ot - 1 - i6) * 15;
                if ((i1 > j) && (i1 < j + m) && (i3 > i11 - 13) && (i3 < i11 + 3)) {
                    i4 = i6;
                }
            }

            return i4;
        }

        return -1;
    }
}
