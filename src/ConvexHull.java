import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.io.*;

/*********************/
/* class  ConvexHull*/                                                    
/*********************/

/** Uses the incremental algorithm for convex hull building to create convex hulls based on points placed by the user. */
public class ConvexHull extends Applet {
    
    public static final long serialVersionUID = 24362462L;
    Button bClear;
    Button bCH;
    Button bRead;
    Button bQuit;
    static Frame myFrame=null;
    Panel  mainPanel;
    private MyGraphics myG;
    public Color paintColor, bkColor;
    public int borderSize;
    

    public void clearMe() {
	Graphics g = getGraphics();
        Dimension dimension = getSize();
        Color col = g.getColor();
        g.setColor(getBackground());
        g.fillRect(0, 0, dimension.width, dimension.height);
        g.setColor(col);
    }

    public void init() {

	borderSize = 10000;
        paintColor = Color.yellow;
        bkColor    = Color.red;
	setBackground(Color.gray);
        setLayout(new BorderLayout());
        mainPanel  = new Panel();
	mainPanel.setBackground(Color.lightGray);
        Panel panel2 = new Panel();
	panel2.setBackground(Color.lightGray);
	bCH = new Button("Convex Hull");
	panel2.add(bCH);
        bRead = new Button("Read sample points");
	panel2.add(bRead);
        bClear = new Button("Clear all");
	panel2.add(bClear);
        
        if (myFrame != null)
	{
	    bQuit = new Button("Quit");
	    panel2.add(bQuit);
        }
	add("North",  panel2);
	add("South",  mainPanel);
	myG = new MyGraphics(paintColor, Color.pink);

	// Convex Hull button
        bCH.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		Graphics g = getGraphics();
		g.setColor(paintColor);
		myG.hullThePolygon(g);
		g.setPaintMode();
	    }
	});

	// Read Sample Points button
	bRead.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		int x, y;

		File f = new File(".","sample.data");
		if (!f.exists()) throw new Error("Sample Point File Not Found"); 
		else {
		    // get rid of existing polygon
		    myG.clear(mainPanel, getBackground());
		    // clear screen
		    Graphics g = getGraphics();
		    g.setColor(paintColor);
		    clearMe();
		    System.out.println("All points cleared");

		    // read in points from file
		    try {
			Reader r = new BufferedReader(
				   new InputStreamReader(
				   new FileInputStream(f)));
			StreamTokenizer st = new StreamTokenizer(r);
			
			int i=0;
			while (st.nextToken()!=st.TT_EOF) {
			    x = (int)(st.nval);
			    st.nextToken();
			    y = (int)(st.nval);
			    System.out.println("Read point (" + x + " , "+y+")");
			    myG.addPolyPoint(g, x, y);
			}		
		    }
		    catch (Exception exc) {
			System.out.println("ERROR ~~ Cannot read from file "+f);
		    }
		    g.setPaintMode();
		}
	    }
	});
	
	// Clear button
	bClear.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		// get rid of components
		myG.clear(mainPanel, getBackground());
		// clear screen
		Graphics g = getGraphics();
		g.setColor(paintColor);
		clearMe();
		g.setPaintMode();

		System.out.println("All points cleared");
	    }
	});
	
        if (myFrame != null)
        { // Quit button
	    bQuit.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
			System.out.println("Closing application...");
			System.exit(0);
		    }
		});
	} 

	// Click on the canvas listener...
       this.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		Graphics g = getGraphics();
		g.setColor(paintColor);
		int x = e.getX();
		int y = e.getY();
		myG.addPolyPoint(g, x, y);
		g.setPaintMode();

		System.out.println("Added new point ("+x+","+y+")");
	    }
	});
    }

	
	
  /****************************************************************************/
  /* a standard overwriting of update()                                       */
  /****************************************************************************/
  public void update(Graphics g) { 
    paint(g); 
  }
  
  public void paint(Graphics g) {
    paintComponents(g); 
    myG.drawThePolygon(g);
  }


   public static void main(String[] args) {
    ConvexHull myConvexHullApplet = new ConvexHull(); 
    myFrame = new Frame("Convex hull application"); // create frame with title
    // Call applet's init method (since Java App does not
    // call it as a browser automatically does)
    myConvexHullApplet.init();	

    // add applet to the frame
    myFrame.add(myConvexHullApplet, BorderLayout.CENTER);
    myFrame.pack(); // set window to appropriate size (for its elements)
    myFrame.setSize(600, 500);
    myFrame.setVisible(true); // usual step to make frame visible

  } // end main
 
}
