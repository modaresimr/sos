// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) definits fieldsfirst noinners ansi nonlb space lnc safe debugmode 
// Source File Name:   Order0.java
// Class Version:      49.0

package sos.tools.geom;

import java.awt.geom.Rectangle2D;

// Referenced classes of package sun.awt.geom:
//			Curve, Crossings

// flag ACC_SUPER is set
final class Order0 extends Curve {
	// Constants:          75
	// Interfaces:         0
	// Fields:             2
	// Methods:            25
	// Class Attributes:   1


        	private double x = 0;
        	private double y = 0;

        	// Decompiling method: <init>  Signature: (DD)V
        	// Max stack: 3, #locals: 5, #params: 5
        	// Code length: 16 bytes, Code offset: 806
        	// Line Number Table found: 4 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 16 Range 0 15 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 16 Range 0 15 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 16 Range 0 15 Init 0
        	// RetValue   5 added: Name <returnValue> Type V At 0 16 Range 0 15 Init 0 fixed
        	public Order0(double d, double d1) {
/*  19*/		super(1);
/*  20*/		x = d;
/*  21*/		y = d1;
/*  22*/		/* return; */
        	}

        	// Decompiling method: getOrder  Signature: ()I
        	// Max stack: 1, #locals: 1, #params: 1
        	// Code length: 2 bytes, Code offset: 872
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type I At 0 2 Range 0 1 Init 0 fixed
        	public int getOrder() {
/*  25*/		return 0;
        	}

        	// Decompiling method: getXTop  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 912
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getXTop() {
/*  29*/		return x;
        	}

        	// Decompiling method: getYTop  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 955
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getYTop() {
/*  33*/		return y;
        	}

        	// Decompiling method: getXBot  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 998
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getXBot() {
/*  37*/		return x;
        	}

        	// Decompiling method: getYBot  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1041
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getYBot() {
/*  41*/		return y;
        	}

        	// Decompiling method: getXMin  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1084
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getXMin() {
/*  45*/		return x;
        	}

        	// Decompiling method: getXMax  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1127
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getXMax() {
/*  49*/		return x;
        	}

        	// Decompiling method: getX0  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1170
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getX0() {
/*  53*/		return x;
        	}

        	// Decompiling method: getY0  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1213
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getY0() {
/*  57*/		return y;
        	}

        	// Decompiling method: getX1  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1256
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getX1() {
/*  61*/		return x;
        	}

        	// Decompiling method: getY1  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1299
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getY1() {
/*  65*/		return y;
        	}

        	// Decompiling method: XforY  Signature: (D)D
        	// Max stack: 2, #locals: 3, #params: 3
        	// Code length: 2 bytes, Code offset: 1342
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 2 Range 0 1 Init 0 fixed
        	public double XforY(double d) {
/*  69*/		return d;
        	}

        	// Decompiling method: TforY  Signature: (D)D
        	// Max stack: 2, #locals: 3, #params: 3
        	// Code length: 2 bytes, Code offset: 1382
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 2 Range 0 1 Init 0 fixed
        	public double TforY(double d) {
/*  73*/		return 0.0D;
        	}

        	// Decompiling method: XforT  Signature: (D)D
        	// Max stack: 2, #locals: 3, #params: 3
        	// Code length: 5 bytes, Code offset: 1422
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 5 Range 0 4 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double XforT(double d) {
/*  77*/		return x;
        	}

        	// Decompiling method: YforT  Signature: (D)D
        	// Max stack: 2, #locals: 3, #params: 3
        	// Code length: 5 bytes, Code offset: 1465
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 5 Range 0 4 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 5 Range 0 4 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double YforT(double d) {
/*  81*/		return y;
        	}

        	// Decompiling method: dXforT  Signature: (DI)D
        	// Max stack: 2, #locals: 4, #params: 4
        	// Code length: 2 bytes, Code offset: 1508
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// Parameter  3 added: Name i Type I At 0 2 Range 0 1 Init 0
        	// RetValue   4 added: Name <returnValue> Type D At 0 2 Range 0 1 Init 0 fixed
        	public double dXforT(double d, int i) {
/*  85*/		return 0.0D;
        	}

        	// Decompiling method: dYforT  Signature: (DI)D
        	// Max stack: 2, #locals: 4, #params: 4
        	// Code length: 2 bytes, Code offset: 1548
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// Parameter  3 added: Name i Type I At 0 2 Range 0 1 Init 0
        	// RetValue   4 added: Name <returnValue> Type D At 0 2 Range 0 1 Init 0 fixed
        	public double dYforT(double d, int i) {
/*  89*/		return 0.0D;
        	}

        	// Decompiling method: nextVertical  Signature: (DD)D
        	// Max stack: 2, #locals: 5, #params: 5
        	// Code length: 2 bytes, Code offset: 1588
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 2 Range 0 1 Init 0
        	// RetValue   5 added: Name <returnValue> Type D At 0 2 Range 0 1 Init 0 fixed
        	public double nextVertical(double d, double d1) {
/*  93*/		return d1;
        	}

        	// Decompiling method: crossingsFor  Signature: (DD)I
        	// Max stack: 1, #locals: 5, #params: 5
        	// Code length: 2 bytes, Code offset: 1628
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 2 Range 0 1 Init 0
        	// RetValue   5 added: Name <returnValue> Type I At 0 2 Range 0 1 Init 0 fixed
        	public int crossingsFor(double d, double d1) {
/*  97*/		return 0;
        	}

        	// Decompiling method: accumulateCrossings  Signature: (Lsun/awt/geom/Crossings;)Z
        	// Max stack: 4, #locals: 2, #params: 2
        	// Code length: 54 bytes, Code offset: 1668
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 54 Range 0 53 Init 0 fixed
        	// Parameter  1 added: Name crossings Type Lsun/awt/geom/Crossings; At 0 54 Range 0 53 Init 0
        	// RetValue   2 added: Name <returnValue> Type Z At 0 54 Range 0 53 Init 0 fixed
        	public boolean accumulateCrossings(Crossings crossings) {
/* 101*/		return x > crossings.getXLo() && x < crossings.getXHi() && y > crossings.getYLo() && y < crossings.getYHi();
        	}

        	// Decompiling method: enlarge  Signature: (Ljava/awt/geom/Rectangle2D;)V
        	// Max stack: 5, #locals: 2, #params: 2
        	// Code length: 13 bytes, Code offset: 1760
        	// Line Number Table found: 2 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 13 Range 0 12 Init 0 fixed
        	// Parameter  1 added: Name rectangle2d Type Ljava/awt/geom/Rectangle2D; At 0 13 Range 0 12 Init 0
        	// RetValue   2 added: Name <returnValue> Type V At 0 13 Range 0 12 Init 0 fixed
        	public void enlarge(Rectangle2D rectangle2d) {
/* 108*/		rectangle2d.add(x, y);
/* 109*/		/* return; */
        	}

        	// Decompiling method: getSubCurve  Signature: (DDI)Lsun/awt/geom/Curve;
        	// Max stack: 1, #locals: 6, #params: 6
        	// Code length: 2 bytes, Code offset: 1815
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 2 Range 0 1 Init 0
        	// Parameter  5 added: Name i Type I At 0 2 Range 0 1 Init 0
        	// RetValue   6 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 2 Range 0 1 Init 0 fixed
        	public Curve getSubCurve(double d, double d1, int i) {
/* 112*/		return ((Curve) (this));
        	}

        	// Decompiling method: getReversedCurve  Signature: ()Lsun/awt/geom/Curve;
        	// Max stack: 1, #locals: 1, #params: 1
        	// Code length: 2 bytes, Code offset: 1855
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 2 Range 0 1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 2 Range 0 1 Init 0 fixed
        	public Curve getReversedCurve() {
/* 116*/		return ((Curve) (this));
        	}

        	// Decompiling method: getSegment  Signature: ([D)I
        	// Max stack: 4, #locals: 2, #params: 2
        	// Code length: 16 bytes, Code offset: 1895
        	// Line Number Table found: 3 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order0; At 0 16 Range 0 15 Init 0 fixed
        	// Parameter  1 added: Name ad Type [D At 0 16 Range 0 15 Init 0
        	// RetValue   2 added: Name <returnValue> Type I At 0 16 Range 0 15 Init 0 fixed
        	public int getSegment(double ad[]) {
/* 120*/		ad[0] = x;
/* 121*/		ad[1] = y;
/* 122*/		return 0;
        	}
}
