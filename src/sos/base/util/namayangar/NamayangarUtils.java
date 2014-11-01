package sos.base.util.namayangar;

 import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Human;
import sos.base.entities.ShapeableObject;
import sos.base.entities.StandardEntity;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class NamayangarUtils {

	/**
	 * 
	 * @param shape
	 * @param t
	 * @return
	 */
	public static Shape transformShape(Shape shape,ScreenTransform t){
		return transformShape(AliGeometryTools.getApexes(shape),t);

	}
	public static Shape transformShape(int []apexes,ScreenTransform t){
		int count = apexes.length / 2;
		int[] xs = new int[count];
		int[] ys = new int[count];
		for (int i = 0; i < count; ++i) {
			xs[i] = t.xToScreen(apexes[i * 2]);
			ys[i] = t.yToScreen(apexes[(i * 2) + 1]);
		}
		return new Polygon(xs, ys, count);
		
	}

	public static Shape transformShape(ShapeableObject shapeableObject, ScreenTransform transform) {
		return transformShape(shapeableObject.getShape(), transform);
	}

	public static void paintPoint2D(Point2D p,ScreenTransform transform,Graphics2D g){
		paintPoint2D(p.getX(), p.getY(), transform, g);
	}
	public static void paintPoint2D(double x,double y,ScreenTransform transform,Graphics2D g){
		int SIZE = 3;
		int x1 = transform.xToScreen(x);
		int y1 = transform.yToScreen(y);
		g.drawLine(x1 - SIZE, y1 - SIZE, x1 + SIZE, y1 + SIZE);
		g.drawLine(x1 - SIZE, y1 + SIZE, x1 + SIZE, y1 - SIZE);
	}
	/**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages. <b>if it is extended from inheritedtype</b>

	 * @param <T>
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<Class<? extends T>> getClasses(String packageName,Class<T> inheritedType){
		ArrayList<Class<? extends T>> result=new ArrayList<Class<? extends T>>();
		ArrayList<Class<?>> classes=new ArrayList<Class<?>>();
		try {
			classes = getClasses(packageName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Class<?> next : classes) {
			Class<?> superClass = next.getSuperclass();
			while(!superClass.equals(Object.class)){
				if(superClass.equals(inheritedType))
					result.add((Class<? extends T>) next);
				superClass=superClass.getSuperclass();
			}
		}
		return result;
	}
	
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static ArrayList<Class<?>> getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

	public static void drawString(String string, Graphics2D g, ScreenTransform transform, StandardEntity element) {
		drawString(string, g, transform, element.getAreaPosition().getX(),element.getAreaPosition().getY());
	}

	public static void drawString(String string, Graphics2D g, ScreenTransform transform, int x, int y) {
		g.drawString(string, transform.xToScreen(x), transform.yToScreen(y));
	}

	public static void drawShape(Shape shape, Graphics2D g, ScreenTransform transform) {
		g.draw(transformShape(shape, transform));
	}

	public static void fillShape(Shape shape, Graphics2D g, ScreenTransform transform) {
		g.fill(transformShape(shape, transform));
	}
	public static void drawLine(StandardEntity realEntity, StandardEntity realEntity2, Graphics2D g, ScreenTransform transform) {
		try{
		drawLine((int)realEntity.getPositionPoint().getX(),(int)realEntity.getPositionPoint().getY(),realEntity2.getAreaPosition().getX(),realEntity2.getAreaPosition().getY(),g,transform);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		}
	public static void drawLine(int x1, int y1, int x2, int y2, Graphics2D g, ScreenTransform transform) {
		Line2D shape = transformLine(x1,y1,x2,y2,transform);
		g.draw(shape);
	}
	public static Line2D transformLine(StandardEntity realEntity, StandardEntity realEntity2,  ScreenTransform transform) {
		return transformLine(realEntity.getAreaPosition().getX(),realEntity.getAreaPosition().getY(),realEntity2.getAreaPosition().getX(),realEntity2.getAreaPosition().getY(),transform);
	}
	public static Line2D transformLine(int x1, int y1, int x2, int y2, ScreenTransform transform) {
		x1=transform.xToScreen(x1);
		y1=transform.yToScreen(y1);
		x2=transform.xToScreen(x2);
		y2=transform.yToScreen(y2);
		return new Line2D.Double(x1, y1, x2, y2) ;
	}

	public static Shape transformEntity(StandardEntity entity, ScreenTransform transform) {
		if (entity instanceof Area) {
			return transformShape(((Area) entity).getShape(), transform);
		} else if (entity instanceof Blockade) {
			return transformShape(((Blockade) entity).getShape(), transform);
		} else if (entity instanceof Human) {
			//		return	transformHumanActualSize((Human) entity, transform);
			return transformHuman((Human) entity, transform);
		} else {
			System.err.println("Unknown Type:"+entity);
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	private static Shape transformHuman(Human h, ScreenTransform t) {
		int SIZE = 10;
		Pair<Integer, Integer> location = null;
		try {
			location = h.getLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (location == null) {
			return null;
		}
		int x = t.xToScreen(location.first());
		int y = t.yToScreen(location.second());
		Shape shape;
		shape = new Ellipse2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);

		return shape;
	}

	public static void drawEntity(StandardEntity entity,Graphics2D g, ScreenTransform transform) {
		g.draw(transformEntity(entity, transform));
	}
	public static void fillEntity(StandardEntity entity,Graphics2D g, ScreenTransform transform) {
		g.fill(transformEntity(entity, transform));
	}
	public static Color randomColor() {
		int c = (int) (Math.random()*255);
		return new Color(Math.abs(c * 25) % 255, Math.abs(17 * c) % 255, Math.abs(34 * c) % 255);
	}

}
