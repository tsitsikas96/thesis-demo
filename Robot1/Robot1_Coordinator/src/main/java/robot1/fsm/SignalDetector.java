package robot1.fsm;


import uml4iot.GenericStateMachine.core.MessageQueue;
import uml4iot.GenericStateMachine.core.SMReception;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SignalDetector implements ActionListener{

    public static MessageQueue msgQ;

    public SignalDetector(MessageQueue itsMsgQ) {
        SignalDetector.msgQ = itsMsgQ;
    }

    public void actionPerformed(ActionEvent e) {

    }
}
