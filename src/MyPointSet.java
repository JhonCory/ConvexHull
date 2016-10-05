import java.awt.*;
import java.lang.*;
import java.util.*;
import java.awt.geom.Line2D;

/** Represents the current set of points
  * Has an internal method for calculating the points’ convex hull */
public class MyPointSet extends Vector<MyPoint> {

    private int imin, imax, xmin, xmax;
    private boolean xySorted;
    private Vector<MyPoint>  theHull;
    public static final long serialVersionUID = 24362462L;
    public MyPointSet() {
	xySorted = false;
    }

    public void addPoint(int x, int y) {
	MyPoint p = new MyPoint(x,y);
	addElement(p);
	xySorted = false;
    }

    private int next(int i) {
	return (i = (i+1) % size());
    }

    private int previous(int i) {
	return (i = (i-1+size()) % size());
    }

    private int hullnext(int i) {
	return (i = (i+1) % theHull.size());
    }

    private int hullprevious(int i) {
	return (i = (i-1+theHull.size()) % theHull.size());
    }

    // Sorts the points by increasing x coordinate (and decreasing y coodinate for points with the same x coordinate)
    public void sortByXY() {
	MyPoint p, q;
	boolean clean;

	// Bubble Sort
	for (int i=0; i<size()-1; i++) {
		for (int j=0; j<size()-i-1; j++) {
			p = get(j);
			q = get(j+1);
			if ((p.x > q.x) || (p.x == q.x && p.y < q.y)) {
				// swap the two elements
				removeElementAt(j);
				insertElementAt(p,j+1);
			}
		}
	}

	xySorted = true;

	return;
    }

    private int removeChain(int bottom, int top) {
	// removes the chain between bottom+1 and top-1 inclusive
	// N.B. the size of the hull decreases by 1 at each step
	// returns the index of the last valid element

	int i, howmany;
	MyPoint q;

	if (bottom == top) return bottom; // nothing to remove

	if (bottom < top) {
	    howmany = top-bottom-1;
	    for (i=0; i<howmany; i++) {
		q = theHull.elementAt(bottom+1);
		theHull.removeElementAt(bottom+1);
	    }
	}

	else { // top < bottom so wrap along chain end
	    howmany = theHull.size()-bottom-1;
	    for (i=0; i<howmany; i++) {
		q = theHull.elementAt(bottom+1);
		theHull.removeElementAt(bottom+1);
	    }
	    howmany = top;
	    for (i=0; i<howmany; i++) {
		// could remove top-1 but then need to change top
		q = theHull.elementAt(0);
		theHull.removeElementAt(0);
	    }

	    if (bottom >= theHull.size()) bottom = theHull.size()-1;
	}

	return bottom; // index of last valid element
    }
    






    // Checks whether index i<n. If so, then we can't compute convex hull
    private boolean errorCheck(int i, int n) {
	if (n<=i) {
		System.out.println("\u0007Can't compute convex hull");
	    	return true;
	}
	else {
		return false;
	}
    }


    // Finds the convex hull for the set of points using the incremental algorithm
    private void hullIncremental() {    
	int k,i,n, howmany;
	MyPoint p, q, r;
	MyPoint topelem, nextelem, botelem, prevelem;
	int top, bottom;
	
	n = size();

	if (n<3) return;

	theHull   = new Vector<MyPoint>(n,n);

	if (!xySorted) sortByXY();

	// Provisionally add the first three non-identical non-collinear points
	
    	p = elementAt(0); // Get 1st point

	q = elementAt(1); // Find a non-identical 2nd point
	i = 2;
	while (q.x == p.x && q.y == p.y && i<n) {
		q = elementAt(i);
		i++;
	}
	if (errorCheck(i,n)) return; // Check for error with points
	
	r = elementAt(i); // Find a non-collinear 3rd point
	i = i+1;
	while (p.collinear(q,r) && i<n) {
		r = elementAt(i);
		i++;
	}
	if (errorCheck(i,n)) return;


	// initialise the ends of the chain-to-be-removed 
	// as the rightmost point in the hull
    	// That is, top=bottom= "index of rightmost point in hull"
	top=bottom=0;


	theHull.insertElementAt(p,0); // Insert the points anti-clockwise
	if (r.left(p,q)) {
		theHull.insertElementAt(q,1);
		theHull.insertElementAt(r,2);
		top = 2;	// Set pos of the last-inserted point
		bottom = top;
	} else {
		theHull.insertElementAt(r,1);
		theHull.insertElementAt(q,2);
		top = 1; // Set pos of last-inserted point
		bottom = top;
	}

	// at this point hull is counter-clockwise and 
	// its last point is visible from the next light-source
	
	while (i < n) {
		q = r;
		r = elementAt(i);
		while (r.x == q.x && r.y == q.y && i<n) { // find the next non-identical point, if there is one
			r = elementAt(i);
			i++;
		}
		if (!(r.x == q.x && r.y == q.y)) {
			// Add next point 'r' to the hull
			
			// Find top element
			topelem = theHull.get(top);
			nextelem = theHull.get(hullnext(top));
			while (!r.left(topelem,nextelem)) {
				top = hullnext(top);
				topelem = theHull.get(top);
				nextelem = theHull.get(hullnext(top));
			}		

			// Find bottom element
			botelem = theHull.get(bottom);
			prevelem = theHull.get(hullprevious(bottom));
			while (!prevelem.left(botelem,r)) {
				bottom = hullprevious(bottom);
				botelem = theHull.get(bottom);
				prevelem = theHull.get(hullprevious(bottom));
			}
			
			// Remove lit chain
			bottom = removeChain(bottom, top);

			// Add the next point
			theHull.insertElementAt(r,(bottom+1));

			// Construct next chain starting at last inserted element
			if (top != bottom) top=bottom=bottom+1;
		}
	}	
    }

    // Returns the Convex Hull as a Polygon
    public Polygon hullDraw() {
	int i;
	MyPoint q;
	Polygon p;

	p = new Polygon();
	System.out.println("The current Convex Hull has size "+theHull.size());
	System.out.println("The current Convex Hull is:");
	for (i=0; i<theHull.size(); i++) {
	    q = theHull.elementAt(i);
	    System.out.println("-> ("+q.x+", "+q.y+")");
	    p.addPoint(q.x,q.y);
	}
	System.out.println();
	return p;
    }

    // Computes the Convex Hull and returns it as a Polygon
    public Polygon hullThePolygon() {
	int i;
	Polygon chp;

	sortByXY();
	hullIncremental();
	chp = hullDraw();

	return chp;
    }
}
