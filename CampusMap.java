
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CampusMap extends Frame implements ActionListener {

    private Choice startChoice, targetChoice;
    private Checkbox wheelchair, mobility;
    private CheckboxGroup modeGroup;
    private Button planButton, resetButton, refreshButton;
    private Button treeButton, followButton, stopButton;
    private TextArea output;
    private MapCanvas mapCanvas;

    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final java.util.List<Edge> edges = new ArrayList<>();

    private java.util.List<String> route = new ArrayList<>();
    private java.util.List<String> visited = new ArrayList<>();
    private boolean following = false;

    private static final Color BACKGROUND =
            new Color(238, 244, 237);
    private static final Color ROAD =
            new Color(205, 211, 215);
    private static final Color ROUTE =
            new Color(16, 155, 100);
    private static final Color BUILDING =
            new Color(214, 224, 235);

    static class Node {
        String name;
        int x, y;
        String type;

        Node(String name, int x, int y, String type) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }

    static class Edge {
        String a, b;
        int distance;
        boolean stairs;
        boolean accessible;

        Edge(String a, String b, int distance,
             boolean stairs, boolean accessible) {
            this.a = a;
            this.b = b;
            this.distance = distance;
            this.stairs = stairs;
            this.accessible = accessible;
        }

        String other(String location) {
            return a.equals(location) ? b : a;
        }
    }

    static class State implements Comparable<State> {
        String name;
        int cost;

        State(String name, int cost) {
            this.name = name;
            this.cost = cost;
        }

        public int compareTo(State other) {
            return Integer.compare(cost, other.cost);
        }
    }

    public CampusMap() {
        super("AccessRoute | Outdoor Navigation");

        createMapData();

        setSize(1200, 760);
        setMinimumSize(new Dimension(900, 600));
        setLayout(new BorderLayout(8, 8));
        setBackground(BACKGROUND);

        // Map panel
        Panel mapPanel = new Panel(new BorderLayout(4, 4));
        mapPanel.setBackground(BACKGROUND);

        Label heading = new Label(
                "  ACCESSROUTE  /  OUTDOOR CAMPUS MAP");
        heading.setFont(new Font("Arial", Font.BOLD, 19));
        heading.setForeground(new Color(30, 65, 58));
        mapPanel.add(heading, BorderLayout.NORTH);

        mapCanvas = new MapCanvas();
        mapPanel.add(mapCanvas, BorderLayout.CENTER);

        output = new TextArea(6, 30);
        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 12));
        output.setText(
                "Welcome to AccessRoute.\n"
                + "Select a start and destination, then Plan Route.\n"
                + "All locations and distances are sample data.\n");

        Panel logPanel = new Panel(new BorderLayout());
        Label logHeading = new Label("  ROUTE DETAILS / SEARCH LOG");
        logPanel.add(logHeading, BorderLayout.NORTH);
        logPanel.add(output, BorderLayout.CENTER);
        mapPanel.add(logPanel, BorderLayout.SOUTH);

        add(mapPanel, BorderLayout.CENTER);

        // Right-hand controls
        Panel controls = new Panel(new GridLayout(0, 1, 6, 6));
        controls.setBackground(new Color(248, 250, 248));
        controls.setPreferredSize(new Dimension(255, 600));

        Label controlHeading = new Label("  NAVIGATION");
        controlHeading.setFont(new Font("Arial", Font.BOLD, 18));
        controls.add(controlHeading);

        controls.add(new Label("Start location"));
        startChoice = new Choice();
        for (String name : nodes.keySet()) {
            startChoice.add(name);
        }
        startChoice.select("Main Gate");
        controls.add(startChoice);

        controls.add(new Label("Destination"));
        targetChoice = new Choice();
        for (String name : nodes.keySet()) {
            targetChoice.add(name);
        }
        targetChoice.select("Medical Center");
        controls.add(targetChoice);

        controls.add(new Label("Accessibility profile"));

        modeGroup = new CheckboxGroup();
        wheelchair = new Checkbox("Wheelchair user", modeGroup, true);
        mobility = new Checkbox("Mobility difficulty", modeGroup, false);

        controls.add(wheelchair);
        controls.add(mobility);

        planButton = makeButton("Plan Route");
        controls.add(planButton);

        refreshButton = makeButton("Refresh Map");
        controls.add(refreshButton);

        treeButton = makeButton("Show Search Tree");
        controls.add(treeButton);

        followButton = makeButton("Follow Route");
        controls.add(followButton);

        stopButton = makeButton("Stop Following");
        controls.add(stopButton);

        resetButton = makeButton("Reset");
        controls.add(resetButton);

        controls.add(new Label("MAP LEGEND"));
        controls.add(new Label("Green line: calculated route"));
        controls.add(new Label("Blue marker: start"));
        controls.add(new Label("Orange marker: destination"));
        controls.add(new Label("Red marker: stairs"));
        controls.add(new Label("Green marker: ramp"));

        add(controls, BorderLayout.EAST);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Button makeButton(String label) {
        Button button = new Button(label);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.addActionListener(this);
        return button;
    }

    // Sample outdoor locations, with coordinates on a 1000 x 650 map.
    private void createMapData() {
        addNode("Main Gate", 90, 325, "gate");
        addNode("Bus Stop", 180, 160, "transport");
        addNode("Parking Area", 180, 500, "parking");
        addNode("Library", 360, 150, "building");
        addNode("Academic Block", 500, 300, "building");
        addNode("Canteen", 670, 150, "food");
        addNode("Administrative Office", 690, 410, "building");
        addNode("Sports Ground", 470, 520, "park");
        addNode("Medical Center", 875, 325, "medical");

        // Distances are illustrative metres.
        // The final two arguments indicate stairs and accessibility.
        addEdge("Main Gate", "Bus Stop", 100, false, true);
        addEdge("Main Gate", "Parking Area", 110, false, true);
        addEdge("Bus Stop", "Library", 140, false, true);
        addEdge("Bus Stop", "Academic Block", 230, true, false);
        addEdge("Parking Area", "Academic Block", 220, false, true);
        addEdge("Parking Area", "Sports Ground", 190, false, true);
        addEdge("Library", "Academic Block", 130, false, true);
        addEdge("Library", "Canteen", 220, true, false);
        addEdge("Academic Block", "Canteen", 180, false, true);
        addEdge("Academic Block", "Administrative Office",
                170, false, true);
        addEdge("Academic Block", "Sports Ground", 190, false, true);
        addEdge("Canteen", "Administrative Office", 190, false, true);
        addEdge("Administrative Office", "Sports Ground",
                230, false, true);
        addEdge("Administrative Office", "Medical Center",
                190, false, true);
        addEdge("Canteen", "Medical Center", 260, false, true);
    }

    private void addNode(String name, int x, int y, String type) {
        nodes.put(name, new Node(name, x, y, type));
    }

    private void addEdge(String a, String b, int distance,
                         boolean stairs, boolean accessible) {
        edges.add(new Edge(a, b, distance, stairs, accessible));
    }

    private Edge findEdge(String a, String b) {
        for (Edge edge : edges) {
            if ((edge.a.equals(a) && edge.b.equals(b))
                    || (edge.a.equals(b) && edge.b.equals(a))) {
                return edge;
            }
        }
        return null;
    }

    // Dijkstra's algorithm with accessibility constraints.
    private void calculateRoute() {
        String start = startChoice.getSelectedItem();
        String target = targetChoice.getSelectedItem();

        if (start.equals(target)) {
            output.setText("Choose two different locations.");
            return;
        }

        boolean isWheelchair = wheelchair.getState();

        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>();

        visited.clear();
        route.clear();

        for (String name : nodes.keySet()) {
            distance.put(name, Integer.MAX_VALUE);
        }

        distance.put(start, 0);
        queue.add(new State(start, 0));

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (current.cost != distance.get(current.name)) {
                continue;
            }

            visited.add(current.name);

            if (current.name.equals(target)) {
                break;
            }

            for (Edge edge : edges) {
                if (!edge.a.equals(current.name)
                        && !edge.b.equals(current.name)) {
                    continue;
                }

                if (isWheelchair && (edge.stairs || !edge.accessible)) {
                    continue;
                }

                // Mobility mode prefers accessible paths but can use
                // a marked stair connection at a large additional cost.
                int penalty = (!edge.accessible || edge.stairs) ? 500 : 0;
                int newCost = current.cost + edge.distance + penalty;
                String next = edge.other(current.name);

                if (newCost < distance.get(next)) {
                    distance.put(next, newCost);
                    previous.put(next, current.name);
                    queue.add(new State(next, newCost));
                }
            }
        }

        if (distance.get(target) == Integer.MAX_VALUE) {
            output.setText(
                    "No route satisfies the selected accessibility rules.\n"
                    + "Check the map connections or try another destination.");
            mapCanvas.repaint();
            return;
        }

        String current = target;
        while (current != null) {
            route.add(0, current);
            if (current.equals(start)) {
                break;
            }
            current = previous.get(current);
        }

        int metres = 0;
        int stairsCount = 0;

        for (int i = 0; i < route.size() - 1; i++) {
            Edge edge = findEdge(route.get(i), route.get(i + 1));
            if (edge != null) {
                metres += edge.distance;
                if (edge.stairs) {
                    stairsCount++;
                }
            }
        }

        following = false;
        mapCanvas.repaint();

        output.setText(
                "ROUTE FOUND\n\n"
                + "Start: " + start + "\n"
                + "Destination: " + target + "\n"
                + "Profile: "
                + (isWheelchair ? "Wheelchair" : "Mobility difficulty")
                + "\n\nRoute:\n" + String.join(" -> ", route)
                + "\n\nDistance: " + metres + " m"
                + "\nEstimated time at 40 m/min: "
                + (int) Math.ceil(metres / 40.0) + " min"
                + "\nStair connections: " + stairsCount
                + "\nVisited nodes: " + visited.size()
                + "\n\nDEMO DATA — verify actual campus accessibility.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == planButton) {
            calculateRoute();
        } else if (source == refreshButton) {
            mapCanvas.repaint();
            output.append("\nMap redrawn.");
        } else if (source == treeButton) {
            if (visited.isEmpty()) {
                output.setText("Plan a route first to see visited nodes.");
            } else {
                output.setText("DIJKSTRA SEARCH ORDER\n\n"
                        + String.join(" -> ", visited)
                        + "\n\nThis is the order nodes were visited, "
                        + "not a complete visual search tree.");
            }
        } else if (source == followButton) {
            if (route.isEmpty()) {
                output.setText("Plan a route first.");
            } else {
                following = true;
                mapCanvas.repaint();
                output.setText(
                        "Route-following preview enabled.\n"
                        + "Live GPS tracking is not implemented.");
            }
        } else if (source == stopButton) {
            following = false;
            mapCanvas.repaint();
            output.append("\nRoute-following preview stopped.");
        } else if (source == resetButton) {
            startChoice.select("Main Gate");
            targetChoice.select("Medical Center");
            modeGroup.setSelectedCheckbox(wheelchair);
            route.clear();
            visited.clear();
            following = false;
            mapCanvas.repaint();
            output.setText("Reset complete. Select a route to begin.");
        }
    }

    // Draw a responsive outdoor map.
    class MapCanvas extends Canvas {

        MapCanvas() {
            setBackground(BACKGROUND);
        }

        private int sx(int x) {
            return 30 + x * (getWidth() - 60) / 1000;
        }

        private int sy(int y) {
            return 35 + y * (getHeight() - 70) / 650;
        }

        @Override
        public void paint(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics;

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(BACKGROUND);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Parks / green spaces
            g.setColor(new Color(211, 233, 202));
            g.fillRoundRect(sx(350), sy(430),
                    sx(250) - sx(0), sy(560) - sy(0), 25, 25);
            g.fillRoundRect(sx(735), sy(65),
                    sx(940) - sx(0), sy(180) - sy(0), 25, 25);

            // Draw roads as broad grey paths, then white road centres.
            for (Edge edge : edges) {
                Node a = nodes.get(edge.a);
                Node b = nodes.get(edge.b);

                g.setColor(ROAD);
                g.setStroke(new BasicStroke(13,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(sx(a.x), sy(a.y), sx(b.x), sy(b.y));

                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(2,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(sx(a.x), sy(a.y), sx(b.x), sy(b.y));
            }

            // Calculated route
            if (!route.isEmpty()) {
                g.setColor(ROUTE);
                g.setStroke(new BasicStroke(7,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                for (int i = 0; i < route.size() - 1; i++) {
                    Node a = nodes.get(route.get(i));
                    Node b = nodes.get(route.get(i + 1));
                    g.drawLine(sx(a.x), sy(a.y), sx(b.x), sy(b.y));
                }
            }

            // Locations and labels
            for (Node node : nodes.values()) {
                int x = sx(node.x);
                int y = sy(node.y);

                Color marker = new Color(57, 102, 153);
                if (node.type.equals("park")) {
                    marker = new Color(72, 145, 75);
                } else if (node.type.equals("food")) {
                    marker = new Color(222, 151, 56);
                } else if (node.type.equals("medical")) {
                    marker = new Color(191, 75, 75);
                }

                if (!route.isEmpty() && route.contains(node.name)) {
                    marker = ROUTE;
                }

                g.setColor(Color.WHITE);
                g.fillOval(x - 11, y - 11, 22, 22);
                g.setColor(marker);
                g.fillOval(x - 8, y - 8, 16, 16);

                g.setColor(new Color(40, 50, 55));
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(node.name, x + 12, y - 9);
            }

            // Accessibility markers on restricted edges
            for (Edge edge : edges) {
                Node a = nodes.get(edge.a);
                Node b = nodes.get(edge.b);
                int x = (sx(a.x) + sx(b.x)) / 2;
                int y = (sy(a.y) + sy(b.y)) / 2;

                if (edge.stairs) {
                    g.setColor(new Color(194, 58, 58));
                    g.fillOval(x - 7, y - 7, 14, 14);
                    g.setColor(Color.WHITE);
                    g.drawString("S", x - 4, y + 4);
                } else if (edge.accessible) {
                    g.setColor(new Color(19, 133, 91));
                    g.fillOval(x - 5, y - 5, 10, 10);
                }
            }

            // Selected start and destination
            drawSelectedMarker(g, startChoice.getSelectedItem(),
                    new Color(40, 105, 210));
            drawSelectedMarker(g, targetChoice.getSelectedItem(),
                    new Color(230, 130, 40));

            g.setColor(new Color(60, 75, 70));
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("SCHEMATIC OUTDOOR MAP  |  NOT TO SCALE",
                    12, getHeight() - 10);
        }

        private void drawSelectedMarker(Graphics2D g, String name,
                                        Color color) {
            Node node = nodes.get(name);
            if (node == null) {
                return;
            }

            int x = sx(node.x);
            int y = sy(node.y);

            g.setColor(color);
            g.setStroke(new BasicStroke(3));
            g.drawOval(x - 15, y - 15, 30, 30);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new CampusMap());
    }
}