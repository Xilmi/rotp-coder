package rotp.ui.main;

import rotp.model.galaxy.StarSystem;
import rotp.model.game.GameSession;
import rotp.model.game.GovernorOptions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Controls
 * <p>
 * [ X ] Governor on by default
 * [ X ] Automatically send transports
 * [ X ] Turn governor on all colonies
 * BOX Stargates
 * ( o ) Never build automatically
 * ( o ) Rich and Ultra Rich planets
 * ( o ) Always build
 */

public class GovernorOptionsPanel {
    private JFrame frame;
    private JPanel panel;
    private JCheckBox governorDefault;
    private JCheckBox autotransport;
    private JRadioButton gateNever;
    private JRadioButton gateRich;
    private JRadioButton gateAlways;

    public GovernorOptionsPanel(JFrame frame) {
        this.frame = frame;
        panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.red, 1));

        governorDefault = new JCheckBox("Governor is on by default");
        governorDefault.setBorder(new EmptyBorder(10, 10, 10, 10));
        governorDefault.setSelected(GameSession.instance().getGovernorOptions().isGovernorOnByDefault());

        panel.add(governorDefault);
        autotransport = new JCheckBox("Population automatically transported from full colonies");
        autotransport.setBorder(new EmptyBorder(10, 10, 10, 10));
        autotransport.setSelected(GameSession.instance().getGovernorOptions().isAutotransport());
        panel.add(autotransport);

        panel.add(new Box.Filler(new Dimension(100, 5), new Dimension(100, 20), new Dimension(100, 30)));
        JButton allOn = new JButton("All governors ON");
        allOn.addActionListener(e -> {
            for (StarSystem ss : GameSession.instance().galaxy().player().orderedColonies()) {
                if (!ss.isColonized()) {
                    // shouldn't happen
                    continue;
                }
                ss.colony().setGovernor(true);
            }
        });
        panel.add(allOn);
        panel.add(new Box.Filler(new Dimension(100, 5), new Dimension(100, 20), new Dimension(100, 30)));
        JButton allOff = new JButton("All governors OFF");
        allOff.addActionListener(e -> {
            for (StarSystem ss : GameSession.instance().galaxy().player().orderedColonies()) {
                if (!ss.isColonized()) {
                    // shouldn't happen
                    continue;
                }
                ss.colony().setGovernor(false);
            }
        });
        panel.add(allOff);
        panel.add(new Box.Filler(new Dimension(100, 5), new Dimension(100, 20), new Dimension(100, 30)));

        JPanel radioButtons = new JPanel();
        radioButtons.setLayout(new BoxLayout(radioButtons, BoxLayout.PAGE_AXIS));
        radioButtons.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Stargate Options"));
        ButtonGroup gates = new ButtonGroup();
        gateNever = new JRadioButton("Never build stargates");
        gateRich = new JRadioButton("Build stargates on Rich and Ultra Rich planets");
        gateAlways = new JRadioButton("Always build stargates");
        gates.add(gateNever);
        gates.add(gateRich);
        gates.add(gateAlways);
        radioButtons.add(gateNever);
        radioButtons.add(gateRich);
        radioButtons.add(gateAlways);
        switch (GameSession.instance().getGovernorOptions().getGates()) {
            case None:
                gateNever.setSelected(true);
                break;
            case Rich:
                gateRich.setSelected(true);
                break;
            case All:
                gateAlways.setSelected(true);
                break;
        }
        panel.add(radioButtons);

        JPanel okCancelButtons = new JPanel();
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            GameSession.instance().getGovernorOptions().setGovernorOnByDefault(governorDefault.isSelected());
            GameSession.instance().getGovernorOptions().setAutotransport(autotransport.isSelected());
            if (gateNever.isSelected()) {
                GameSession.instance().getGovernorOptions().setGates(GovernorOptions.GatesGovernor.None);
            }
            if (gateRich.isSelected()) {
                GameSession.instance().getGovernorOptions().setGates(GovernorOptions.GatesGovernor.Rich);
            }
            if (gateAlways.isSelected()) {
                GameSession.instance().getGovernorOptions().setGates(GovernorOptions.GatesGovernor.All);
            }
            frame.setVisible(false);
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> {
            frame.setVisible(false);
        });
        okCancelButtons.add(ok);
        okCancelButtons.add(cancel);
        panel.add(okCancelButtons);
    }

    public JPanel getPanel() {
        return panel;
    }

    // for testing
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("GovernorOptions");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //Create and set up the content pane.
                GovernorOptionsPanel newContentPane = new GovernorOptionsPanel(frame);
                newContentPane.getPanel().setOpaque(true); //content panes must be opaque
                frame.setContentPane(newContentPane.getPanel());

                //Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
