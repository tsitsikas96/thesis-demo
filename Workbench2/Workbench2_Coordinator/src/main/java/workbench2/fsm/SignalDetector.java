package workbench2.fsm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uml4iot.GenericStateMachine.core.MessageQueue;
import uml4iot.GenericStateMachine.core.SMReception;


public class SignalDetector implements ActionListener{

    private static MessageQueue msgQ;

    public SignalDetector(MessageQueue itsMsgQ) {
        SignalDetector.msgQ = itsMsgQ;
    }

    public void actionPerformed(ActionEvent e) {   }

}
