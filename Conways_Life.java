import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;

public class Conways_Life extends JFrame implements Runnable, MouseListener, ActionListener {
    private BufferStrategy strategy;
    private Graphics offscreenBuffer;
    private boolean[][][] gameState = new boolean[40][40][2]; // 3D array to hold the game state
    private boolean editing = false; // Editing mode toggle
    private boolean isGameRunning = false; // Game running state
    private JPanel canvasPanel; // Panel for drawing the grid
    private JButton runButton; // Button to start/stop the game

    public Conways_Life() {
        setTitle("Conway's game of life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 850);

        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                paintGrid(g); // Custom painting of the grid
            }
        };
        
        canvasPanel.setPreferredSize(new Dimension(800, 800));
        canvasPanel.addMouseListener(this); // Add mouse listener for interaction

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(this);

        JButton randomButton = new JButton("Random");
        randomButton.addActionListener(this);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this); // Fixed: changed from randomButton to saveButton

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(this); // Fixed: changed from randomButton to loadButton

        runButton = new JButton("Run");
        runButton.addActionListener(this);
        runButton.setBackground(Color.RED); // Set initial button color

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editButton);
        buttonPanel.add(randomButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(runButton);

        getContentPane().setPreferredSize(new Dimension(800, 850));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(canvasPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.NORTH);

        // Start the animation thread
        Thread t = new Thread(this);
        t.start();
    }

    private void paintGrid(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 800); // Clear the panel
        g.setColor(Color.WHITE);
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                if (gameState[x][y][0]) {
                    g.fillRect(x * 20, y * 20, 20, 20); // Draw live cells
                }
            }
        }
    }
    
    public void mouseToggle(JButton b) {
        b.setBackground(isGameRunning ? Color.GREEN : Color.RED); // Change button color based on game state
    }
    
    public void run() {
        while (true) {
            try {
                Thread.sleep(100); // Sleep for 1/10 sec
            } catch (InterruptedException e) { }
            if (isGameRunning) {
                gameRunning(); // Update game state if running
            }
            canvasPanel.repaint(); // Repaint the panel
        }
    }
    
    private void gameRunning() {
        int front = 0; // Current state
        int back = 1;  // Next state
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                int liveneighbours = 0; // Count live neighbors
                for (int xx = -1; xx <= 1; xx++) {
                    for (int yy = -1; yy <= 1; yy++) {
                        if (xx != 0 || yy != 0) { // Skip the cell itself
                            int xPos = (x + xx + 40) % 40; // Wrap around
                            int yPos = (y + yy + 40) % 40; // Wrap around
                            if (gameState[xPos][yPos][front])
                                liveneighbours++;
                        }
                    }   
                }
                // Apply rules for Conway's Game of Life
                if (gameState[x][y][front]) {
                    // Cell is alive
                    if (liveneighbours < 2 || liveneighbours > 3) 
                        gameState[x][y][back] = false; // Dies
                    else 
                        gameState[x][y][back] = true; // Lives
                } else {
                    // Cell is dead
                    gameState[x][y][back] = (liveneighbours == 3); // Becomes alive
                }
            }
        }
        // Update game state for the next iteration
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                gameState[x][y][front] = gameState[x][y][back];
            }
        }
    }    
    
    private void randomiseGameState() {
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                gameState[x][y][0] = (Math.random() < 0.25); // Randomly populate cells
            }
        }
        canvasPanel.repaint(); // Repaint after randomization
    }

    // Mouse event implementations
    public void mousePressed(MouseEvent e) {
        if (editing) {
            int x = e.getX() / 20; // Determine cell coordinates
            int y = e.getY() / 20;
            gameState[x][y][0] = !gameState[x][y][0]; // Toggle cell state
            canvasPanel.repaint(); // Repaint for changes
        }
    }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }

    // Button actions
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Edit":
                editing = !editing; // Toggle editing mode
                return; 
            case "Random":
                randomiseGameState(); // Randomize the game state
                return;
            case "Run":
                isGameRunning = !isGameRunning; // Start/stop the game
                mouseToggle(runButton); // Update button color
                return;
            case "Save":
                saveGame(); // Save current game state
                return;
            case "Load": 
                loadGame(); // Load game state from file
                return;
        }
    }
    
    private void loadGame() {
        String filename = "path_to_your_file/filename.txt"; // Specify file path
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String textinput = reader.readLine(); // Read game state from file
            for (int x = 0; x < 40; x++) {
                for (int y = 0; y < 40; y++) {
                    gameState[x][y][0] = (textinput.charAt(x * 40 + y) == '1'); // Set cell state
                }
            }
        } catch (IOException e) { }
    }
    
    private void saveGame() {
        StringBuilder outputtext = new StringBuilder();
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                outputtext.append(gameState[x][y][0] ? '1' : '0'); // Build game state string
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("path_to_your_file/filename.txt"))) {
            writer.write(outputtext.toString()); // Write to file
        } catch (IOException e) { }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Conways_Life c = new Conways_Life();
            c.setVisible(true); // Make the window visible
        });
    }
}
