package it.unibo.oop.reactivegui02;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();

    public ConcurrentGUI(){
        super();
        final JFrame frame=new JFrame();
        final JPanel panel = new JPanel();
        final JButton up=new JButton("up");
        final JButton down=new JButton("down");
        final JButton stop=new JButton("stop");
        frame.add(panel);
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        final Agent agent = new Agent();
        new Thread(agent).start();


        up.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.up();
            }
        });
        down.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.down();
            }
            
        });
        stop.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.stopCounting();
                up.setEnabled(false);
                down.setEnabled(false);
                stop.setEnabled(false);
            }
            
        });
    }

    private final class Agent implements Runnable {

        private volatile boolean stop;
        private int counter = 0;
        private volatile boolean increment = true;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    // The EDT doesn't access `counter` anymore, it doesn't need to be volatile 
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    if(this.increment == true)
                        this.counter++;
                    else
                        this.counter--;
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }

        public void up(){
            this.increment=true;
        }

        public void down(){
            this.increment=false;
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }
    }
}
