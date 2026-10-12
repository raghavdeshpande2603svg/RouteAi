import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

public class AccessibleCampusNavigation extends JFrame implements ActionListener {

    Label title, subtitle;
    Label startLabel, destinationLabel, accessibilityLabel;
    Label distanceLabel, timeLabel, routeLabel;

    Choice startLocation, destination;

    Checkbox wheelchair, mobilityDifficulty;
    CheckboxGroup accessibilityGroup;

    Button findRoute, clear, viewMap, viewFacilities, exit;

    TextArea routeInformation;

    AccessibleCampusNavigation() {

        setTitle("Accessible Campus Navigation System");
        setSize(750, 650);
        setLayout(null);
        setBackground(Color.white);

        // Title
        title = new Label("ACCESSIBLE CAMPUS NAVIGATION");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(140, 50, 480, 35);
        add(title);

        subtitle = new Label("Find a suitable route around your campus");
        subtitle.setBounds(220, 90, 350, 25);
        add(subtitle);

        // Current Location
        startLabel = new Label("Current Location:");
        startLabel.setBounds(70, 145, 150, 25);
        add(startLabel);

        startLocation = new Choice();
        startLocation.add("Select Starting Location");
        startLocation.add("Main Gate");
        startLocation.add("Library");
        startLocation.add("Academic Building");
        startLocation.add("Canteen");
        startLocation.add("Administrative Office");

        startLocation.setBounds(250, 145, 350, 30);
        add(startLocation);

        // Destination
        destinationLabel = new Label("Destination:");
        destinationLabel.setBounds(70, 190, 150, 25);
        add(destinationLabel);

        destination = new Choice();
        destination.add("Select Destination");
        destination.add("Main Gate");
        destination.add("Library");
        destination.add("Academic Building");
        destination.add("Canteen");
        destination.add("Administrative Office");

        destination.setBounds(250, 190, 350, 30);
        add(destination);

        // Accessibility Requirement
        accessibilityLabel = new Label("Accessibility Requirement:");
        accessibilityLabel.setBounds(70, 235, 180, 25);
        add(accessibilityLabel);

        accessibilityGroup = new CheckboxGroup();

        wheelchair = new Checkbox(
                "Wheelchair User", accessibilityGroup, false);

        mobilityDifficulty = new Checkbox(
                "Mobility Difficulty", accessibilityGroup, false);

        wheelchair.setBounds(250, 235, 160, 25);
        mobilityDifficulty.setBounds(420, 235, 180, 25);

        add(wheelchair);
        add(mobilityDifficulty);

        // Buttons
        findRoute = new Button("Find Route");
        findRoute.setBounds(200, 285, 120, 35);
        findRoute.addActionListener(this);
        add(findRoute);

        clear = new Button("Clear");
        clear.setBounds(350, 285, 100, 35);
        clear.addActionListener(this);
        add(clear);

        // Route Information
        routeLabel = new Label("ROUTE INFORMATION");
        routeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        routeLabel.setBounds(70, 345, 250, 25);
        add(routeLabel);

        routeInformation = new TextArea();
        routeInformation.setBounds(70, 375, 600, 140);
        routeInformation.setEditable(false);
        add(routeInformation);

        // Navigation Buttons.

        viewMap = new Button("View Campus Map");
        viewMap.setBounds(70, 545, 160, 35);
        viewMap.addActionListener(this);
        add(viewMap);

        viewFacilities = new Button("Accessible Facilities");
        viewFacilities.setBounds(270, 545, 170, 35);
        viewFacilities.addActionListener(this);
        add(viewFacilities);

        exit = new Button("Exit");
        exit.setBounds(500, 545, 100, 35);
        exit.addActionListener(this);
        add(exit);

        // Close window using cross button
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == findRoute) {

            String start = startLocation.getSelectedItem();
            String end = destination.getSelectedItem();

            if (start.equals("Select Starting Location")
                    || end.equals("Select Destination")) {

                routeInformation.setText(
                        "Please select both current location and destination.");

            } else if (start.equals(end)) {

                routeInformation.setText(
                        "Current location and destination cannot be the same.");

            } else if (!wheelchair.getState()
                    && !mobilityDifficulty.getState()) {

                routeInformation.setText(
                        "Please select an accessibility requirement.");

            } else {

                String requirement;

                if (wheelchair.getState()) {
                    requirement = "Wheelchair User";
                } else {
                    requirement = "Mobility Difficulty";
                }

                routeInformation.setText(
                        "ACCESSIBLE CAMPUS NAVIGATION\n\n"
                                + "Current Location: " + start + "\n"
                                + "Destination: " + end + "\n"
                                + "Requirement: " + requirement + "\n\n"
                                + "Route calculation will be connected here.\n"
                                + "Campus accessibility data is required "
                                + "to calculate a genuine route.");
            }
        }

        else if (e.getSource() == clear) {

            startLocation.select(0);
            destination.select(0);
            accessibilityGroup.setSelectedCheckbox(null);
            routeInformation.setText("");
        }

        else if (e.getSource() == viewMap) {

            new CampusMap();

            // routeInformation.setText(
            //         "Dummy campus map opened.\n\n"
            //                 + "Green: Sample accessible route\n"
            //                 + "R: Ramp marker\n"
            //                 + "S: Stairs marker\n\n"
            //                 + "This map uses demonstration data only.");
        }

        else if (e.getSource() == viewFacilities) {

            routeInformation.setText(
                    "ACCESSIBLE FACILITIES\n\n"
                            + "1. Ramps\n"
                            + "2. Lifts\n"
                            + "3. Accessible Entrances\n"
                            + "4. Accessible Toilets\n\n"
                            + "Actual facility locations will be added "
                            + "after campus data collection.");
        }

        else if (e.getSource() == exit) {

            dispose();
        }
    }

    public static void main(String[] args) {

        new AccessibleCampusNavigation();
    }
}