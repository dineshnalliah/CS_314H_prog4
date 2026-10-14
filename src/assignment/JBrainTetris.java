package assignment;

import javax.swing.*;
import java.awt.event.*;

public class JBrainTetris extends JTetris {

    Brain brain;
    int count = 0;
    javax.swing.Timer brainTimer;
    final int BRAIN_DELAY = 50;

    JBrainTetris() {
        super();
        brain = new FirstBrain();

        brainTimer = new javax.swing.Timer(BRAIN_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(brain.nextMove(board));
            }
        });

        brainTimer.start();
    }

    public static void main(String[] args) {
        createGUI(new JBrainTetris());
    }

    
}
