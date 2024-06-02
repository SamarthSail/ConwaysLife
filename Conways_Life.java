import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;


public class Conways_Life extends JFrame implements Runnable, MouseListener, ActionListener {
    private BufferStrategy strategy;
    private Graphics offscreenBuffer;
    private boolean[][][] gameState = new boolean[40][40][2];
    private boolean editing = false;
    private boolean isGameRunning = false;
    private JPanel canvasPanel;
    private JButton runButton;
    
    public Conways_Life () {
        setTitle("Conway's game of life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,850);
    
        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                paintGrid(g);
            }
        };
        
        // Set preferred size for canvasPanel
        canvasPanel.setPreferredSize(new Dimension(800, 800));
        
        canvasPanel.addMouseListener(this);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(this);

        JButton randomButton = new JButton("Random");
        randomButton.addActionListener(this);
        
        JButton saveButton = new JButton("Save");
        randomButton.addActionListener(this);
        
        JButton loadButton = new JButton("Load");
        randomButton.addActionListener(this);
        
        runButton = new JButton("Run");
        runButton.addActionListener(this);
        runButton.setBackground(Color.RED);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editButton);
        buttonPanel.add(randomButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(runButton);
        
        // Set preferred size for buttonPanel
        buttonPanel.setPreferredSize(new Dimension(800, 50));
        
        getContentPane().setPreferredSize(new Dimension(800, 850));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(canvasPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        
        // create and start our animation thread
        Thread t = new Thread(this);
        t.start();
    }

    private void paintGrid(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 800);
        g.setColor(Color.WHITE);
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                if (gameState[x][y][0]) {
                    g.fillRect(x * 20, y * 20, 20, 20);
                }
            }
        }
    }
    
    public void mouseToggle(JButton b){
        if (isGameRunning){
            b.setBackground(Color.GREEN);
        } else{
            b.setBackground(Color.RED);
        }
    }
    
    public void run() {
        while ( 1==1 ) {
            // 1: sleep for 1/10 sec
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { }
            // 2: animate game objects
            if (isGameRunning){
                gameRunning();
            }
            // 3: force an application repaint
            canvasPanel.repaint();
        }
    }
    
    private void gameRunning() {
        int front = 0;
        int back = 1;
        for (int x=0;x<40;x++) {
            for (int y=0;y<40;y++) {
                // count the neighbours of cell x,y
                int liveneighbours=0;
                for (int xx=-1;xx<=1;xx++) {
                    for (int yy=-1;yy<=1;yy++) {
                        if (xx!=0 || yy!=0) {
                            int xPos=x+xx;
                            if (xPos<0)
                                xPos=39;
                            else if (xPos>39)
                                xPos=0;
                            int yPos=y+yy;
                            if (yPos<0)
                                yPos=39;
                            else if (yPos>39)
                                yPos=0;
                            if (gameState[xPos][yPos][front])
                                liveneighbours++;
                        }
                    }   
                }
                // apply rules for cell x,y
                if (gameState[x][y][front]) {
                // cell x,y was alive
                // #1. Any live cell with fewer than two live neighbours dies
                if (liveneighbours<2)
                    gameState[x][y][back] = false;
                // #2. Any live cell with two or three live neighbours lives
                else if (liveneighbours<4)
                    gameState[x][y][back] = true;
                // #3. Any live cell with more than three live neighbours dies
                else
                    gameState[x][y][back] = false;
                }
                else {
                    // cell x,y was dead
                    // #4. Dead cells with three live neighbours become live
                    if (liveneighbours==3)
                        gameState[x][y][back] = true;
                    else
                        gameState[x][y][back] = false;
                }
            }
        }

        // now flip the game state from back to front
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                gameState[x][y][front] = gameState[x][y][back];
            }
        }
    }    
    
    private void randomiseGameState() {
        for (int x=0;x<40;x++) {
            for (int y=0;y<40;y++) {
                gameState[x][y][0]=(Math.random()<0.25);
            }
        }
        canvasPanel.repaint();
    }

    // mouse events which must be implemented for MouseListener
    public void mousePressed(MouseEvent e) {
        if (editing) {
            // determine which cell of the gameState array was clicked on
            int x = e.getX() / 20;
            int y = e.getY() / 20;
            // toggle gameState
            gameState[x][y][0] = !gameState[x][y][0];
            // repaint for changes
            canvasPanel.repaint();
        }
    }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    //

    // button actions here - start for toggling playing boolean and random for toggling randomise boolean
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Edit":
                editing = !editing;
                return; 
            case "Random":
                randomiseGameState();
                return;
            case "Run":
                isGameRunning = !isGameRunning;
                mouseToggle(runButton);
                return;
            case "Save":
                saveGame();
                return;
            case "Load": 
                loadGame();
                return;
        }
    }
    
    private void loadGame() {
        String workingDirectory = System.getProperty("your own directory");
        String filename = workingDirectory+"filename.txt";
        String textinput = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            textinput = reader.readLine();
            reader.close();
        } 
        catch (IOException e) { }
        if (textinput!=null) {
            for (int x=0;x<40;x++) {
                for (int y=0;y<40;y++) {
                    gameState[x][y][0] = (textinput.charAt(x*40+y)=='1');
                }
            }
        }
    }
    
    private void saveGame() {
        // pack gamestate into a string
        String outputtext="";
        for (int x=0;x<40;x++) {
            for (int y=0;y<40;y++) {
                if (gameState[x][y][0])
                    outputtext+="1";
                else
                    outputtext+="0";
            }
        }

        try {
            String workingDirectory = System.getProperty("your own directory");
            String filename = "appropriate directory" + "filename.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(outputtext);
            writer.close();
        }
        catch (IOException e) { }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Conways_Life c = new Conways_Life();
            c.setVisible(true);
        });
    }
}
