package sos.base.util.geom;

import java.awt.Polygon;

import sos.base.entities.Area;
/**
 * @author http://java-sl.com/downloads.html
 *
 */
public class RegularPolygon extends Polygon {
	final int x;
	final int y;
	final int r;
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegularPolygon(int x, int y, int r, int vertexCount) {
        this(x, y, r, vertexCount, 0);
        
    }
    public RegularPolygon(int x, int y, int r, int vertexCount, double startAngle) {
        super(getXCoordinates(x, y, r, vertexCount, startAngle)
              ,getYCoordinates(x, y, r, vertexCount, startAngle)
              ,vertexCount);
		this.x = x;
		this.y = y;
		this.r = r;
        
    }

    protected static int[] getXCoordinates(int x, int y, int r, int vertexCount, double startAngle) {
        int res[]=new int[vertexCount];
        double addAngle=2*Math.PI/vertexCount;
        double angle=startAngle;
        for (int i=0; i<vertexCount; i++) {
            res[i]=(int)Math.round(r*Math.cos(angle))+x;
            angle+=addAngle;
        }
        return res;
    }

    protected static int[] getYCoordinates(int x, int y, int r, int vertexCount, double startAngle) {
        int res[]=new int[vertexCount];
        double addAngle=2*Math.PI/vertexCount;
        double angle=startAngle;
        for (int i=0; i<vertexCount; i++) {
            res[i]=(int)Math.round(r*Math.sin(angle))+y;
            angle+=addAngle;
        }
        return res;
    }
	public boolean contains(int[] apexList) {
		for (int i = 0; i < apexList.length; i += 2) {
			if (!contains(apexList[i], apexList[i + 1]))
				return false;
		}
		return true;
	}

	public boolean contains(Area road) {
		if(!contains(road.getShape().getBounds()))
			return false;
		return contains(road.getApexList());
	}
	public int[] getApexes() {
		int[] apexes = new int[npoints * 2];
		for (int i = 0; i < npoints; i++) {
			apexes[i * 2] = xpoints[i];
			apexes[i * 2 + 1] = ypoints[i];
		}
		return apexes;

	}

	
    
}
