package assignment;

import javax.swing.*;
import java.awt.event.*;

public class JBrainTetris extends JTetris {

    Brain brain;
    int count = 0;
    javax.swing.Timer brainTimer;
    int BRAIN_DELAY = 10;

    // initializes JBrainTetris instance and allows brain to make moves
    JBrainTetris() {
        super();
        brain = new FirstBrain();

        brainTimer = new javax.swing.Timer(BRAIN_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gameOn)
                    tick(brain.nextMove(board));
            }
        });

        brainTimer.start();
    }

    public static void main(String[] args) {
        createGUI(new JBrainTetris());
    }

}
