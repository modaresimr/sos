// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 3/29/2011 1:43:45 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) definits fieldsfirst noinners ansi nonlb space lnc safe debugmode 
// Source File Name:   Order1.java
// Class Version:      49.0

package sos.tools.geom;

import java.awt.geom.Rectangle2D;

// Referenced classes of package sun.awt.geom:
//			Curve, Crossings

// flag ACC_SUPER is set
final class Order1 extends Curve {
	// Constants:          145
	// Interfaces:         0
	// Fields:             6
	// Methods:            25
	// Class Attributes:   1


        	private double x0 = 0;
        	private double y0 = 0;
        	private double x1 = 0;
        	private double y1 = 0;
        	private double xmin = 0;
        	private double xmax = 0;

        	// Decompiling method: <init>  Signature: (DDDDI)V
        	// Max stack: 4, #locals: 10, #params: 10
        	// Code length: 61 bytes, Code offset: 1479
        	// Line Number Table found: 11 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 61 Range 0 60 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 61 Range 0 60 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 61 Range 0 60 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 61 Range 0 60 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 61 Range 0 60 Init 0
        	// Parameter  9 added: Name i Type I At 0 61 Range 0 60 Init 0
        	// RetValue  10 added: Name <returnValue> Type V At 0 61 Range 0 60 Init 0 fixed
        	public Order1(double d, double d1, double d2, double d3, int i) {
/*  26*/		super(i);
/*  27*/		x0 = d;
/*  28*/		y0 = d1;
/*  29*/		x1 = d2;
/*  30*/		y1 = d3;
/*  31*/		if (d < d2) {
/*  32*/			xmin = d;
/*  33*/			xmax = d2;
        		} else {
/*  35*/			xmin = d2;
/*  36*/			xmax = d;
        		}
/*  38*/		/* return; */
        	}

        	// Decompiling method: getOrder  Signature: ()I
        	// Max stack: 1, #locals: 1, #params: 1
        	// Code length: 2 bytes, Code offset: 1618
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 2 Range 0 1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type I At 0 2 Range 0 1 Init 0 fixed
        	public int getOrder() {
/*  41*/		return 1;
        	}

        	// Decompiling method: getXTop  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1658
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getXTop() {
/*  45*/		return x0;
        	}

        	// Decompiling method: getYTop  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1701
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getYTop() {
/*  49*/		return y0;
        	}

        	// Decompiling method: getXBot  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1744
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getXBot() {
/*  53*/		return x1;
        	}

        	// Decompiling method: getYBot  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1787
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getYBot() {
/*  57*/		return y1;
        	}

        	// Decompiling method: getXMin  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1830
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getXMin() {
/*  61*/		return xmin;
        	}

        	// Decompiling method: getXMax  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 1873
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getXMax() {
/*  65*/		return xmax;
        	}

        	// Decompiling method: getX0  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 20 bytes, Code offset: 1916
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 20 Range 0 19 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
        	public double getX0() {
/*  69*/		return direction != 1 ? x1 : x0;
        	}

        	// Decompiling method: getY0  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 20 bytes, Code offset: 1974
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 20 Range 0 19 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
        	public double getY0() {
/*  73*/		return direction != 1 ? y1 : y0;
        	}

        	// Decompiling method: getX1  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 20 bytes, Code offset: 2032
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 20 Range 0 19 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
        	public double getX1() {
/*  77*/		return direction != -1 ? x1 : x0;
        	}

        	// Decompiling method: getY1  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 20 bytes, Code offset: 2090
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 20 Range 0 19 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
        	public double getY1() {
/*  81*/		return direction != -1 ? y1 : y0;
        	}

        	// Decompiling method: XforY  Signature: (D)D
        	// Max stack: 8, #locals: 3, #params: 3
        	// Code length: 72 bytes, Code offset: 2148
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 72 Range 0 71 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 72 Range 0 71 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 72 Range 0 71 Init 0 fixed
        	public double XforY(double d) {
/*  85*/		if (x0 == x1 || d <= y0)
/*  86*/			return x0;
/*  88*/		if (d >= y1)
/*  89*/			return x1;
/*  92*/		else
/*  92*/			return x0 + ((d - y0) * (x1 - x0)) / (y1 - y0);
        	}

        	// Decompiling method: TforY  Signature: (D)D
        	// Max stack: 6, #locals: 3, #params: 3
        	// Code length: 39 bytes, Code offset: 2274
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 39 Range 0 38 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 39 Range 0 38 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 39 Range 0 38 Init 0 fixed
        	public double TforY(double d) {
/*  96*/		if (d <= y0)
/*  97*/			return 0.0D;
/*  99*/		if (d >= y1)
/* 100*/			return 1.0D;
/* 102*/		else
/* 102*/			return (d - y0) / (y1 - y0);
        	}

        	// Decompiling method: XforT  Signature: (D)D
        	// Max stack: 8, #locals: 3, #params: 3
        	// Code length: 17 bytes, Code offset: 2367
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 17 Range 0 16 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 17 Range 0 16 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 17 Range 0 16 Init 0 fixed
        	public double XforT(double d) {
/* 106*/		return x0 + d * (x1 - x0);
        	}

        	// Decompiling method: YforT  Signature: (D)D
        	// Max stack: 8, #locals: 3, #params: 3
        	// Code length: 17 bytes, Code offset: 2422
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 17 Range 0 16 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 17 Range 0 16 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 17 Range 0 16 Init 0 fixed
        	public double YforT(double d) {
/* 110*/		return y0 + d * (y1 - y0);
        	}

        	// Decompiling method: dXforT  Signature: (DI)D
        	// Max stack: 8, #locals: 4, #params: 4
        	// Code length: 57 bytes, Code offset: 2477
        	// Line Number Table found: 4 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 57 Range 0 56 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 57 Range 0 56 Init 0
        	// Parameter  3 added: Name i Type I At 0 57 Range 0 56 Init 0
        	// RetValue   4 added: Name <returnValue> Type D At 0 57 Range 0 56 Init 0 fixed
        	public double dXforT(double d, int i) {
/* 114*/		switch (i) {
/* 116*/		case 0: // '\0'
/* 116*/			return x0 + d * (x1 - x0);

/* 118*/		case 1: // '\001'
/* 118*/			return x1 - x0;
        		}
/* 120*/		return 0.0D;
        	}

        	// Decompiling method: dYforT  Signature: (DI)D
        	// Max stack: 8, #locals: 4, #params: 4
        	// Code length: 57 bytes, Code offset: 2584
        	// Line Number Table found: 4 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 57 Range 0 56 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 57 Range 0 56 Init 0
        	// Parameter  3 added: Name i Type I At 0 57 Range 0 56 Init 0
        	// RetValue   4 added: Name <returnValue> Type D At 0 57 Range 0 56 Init 0 fixed
        	public double dYforT(double d, int i) {
/* 125*/		switch (i) {
/* 127*/		case 0: // '\0'
/* 127*/			return y0 + d * (y1 - y0);

/* 129*/		case 1: // '\001'
/* 129*/			return y1 - y0;
        		}
/* 131*/		return 0.0D;
        	}

        	// Decompiling method: nextVertical  Signature: (DD)D
        	// Max stack: 2, #locals: 5, #params: 5
        	// Code length: 2 bytes, Code offset: 2691
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 2 Range 0 1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 2 Range 0 1 Init 0
        	// RetValue   5 added: Name <returnValue> Type D At 0 2 Range 0 1 Init 0 fixed
        	public double nextVertical(double d, double d1) {
/* 136*/		return d1;
        	}

        	// Decompiling method: accumulateCrossings  Signature: (Lsun/awt/geom/Crossings;)Z
        	// Max stack: 6, #locals: 18, #params: 2
        	// Code length: 181 bytes, Code offset: 2731
        	// Line Number Table found: 26 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 181 Range 0 180 Init 0 fixed
        	// Parameter  1 added: Name crossings Type Lsun/awt/geom/Crossings; At 0 181 Range 0 180 Init 0
        	// RetValue  18 added: Name <returnValue> Type Z At 0 181 Range 0 180 Init 0 fixed
        	// LocalVar   2 added: Name d Type D At 4 157 Range 4 160 Init 4
        	// LocalVar   4 added: Name d1 Type D At 9 55 Range 9 63 Init 9
        	// LocalVar   6 added: Name d2 Type D At 15 130 Range 15 144 Init 15
        	// LocalVar   8 added: Name d3 Type D At 21 92 Range 21 112 Init 21
        	// LocalVar  12 added: Name d4 Type D At 59 111 Range 59 169 Init 59
        	// LocalVar  10 added: Name d5 Type D At 67 86 Range 67 152 Init 67
        	// LocalVar  16 added: Name d6 Type D At 108 64 Range 108 171 Init 108
        	// LocalVar  14 added: Name d7 Type D At 116 44 Range 116 159 Init 116
        	public boolean accumulateCrossings(Crossings crossings) {
/* 140*/		double d = crossings.getXLo();
/* 141*/		double d1 = crossings.getYLo();
/* 142*/		double d2 = crossings.getXHi();
/* 143*/		double d3 = crossings.getYHi();
/* 144*/		if (xmin >= d2)
/* 145*/			return false;
        		double d4;
        		double d5;
/* 148*/		if (y0 < d1) {
/* 149*/			if (y1 <= d1)
/* 150*/				return false;
/* 152*/			d5 = d1;
/* 153*/			d4 = XforY(d1);
        		} else {
/* 155*/			if (y0 >= d3)
/* 156*/				return false;
/* 158*/			d5 = y0;
/* 159*/			d4 = x0;
        		}
        		double d6;
        		double d7;
/* 161*/		if (y1 > d3) {
/* 162*/			d7 = d3;
/* 163*/			d6 = XforY(d3);
        		} else {
/* 165*/			d7 = y1;
/* 166*/			d6 = x1;
        		}
/* 168*/		if (d4 >= d2 && d6 >= d2)
/* 169*/			return false;
/* 171*/		if (d4 > d || d6 > d) {
/* 172*/			return true;
        		} else {
/* 174*/			crossings.record(d5, d7, direction);
/* 175*/			return false;
        		}
        	}

        	// Decompiling method: enlarge  Signature: (Ljava/awt/geom/Rectangle2D;)V
        	// Max stack: 5, #locals: 2, #params: 2
        	// Code length: 25 bytes, Code offset: 3050
        	// Line Number Table found: 3 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 25 Range 0 24 Init 0 fixed
        	// Parameter  1 added: Name rectangle2d Type Ljava/awt/geom/Rectangle2D; At 0 25 Range 0 24 Init 0
        	// RetValue   2 added: Name <returnValue> Type V At 0 25 Range 0 24 Init 0 fixed
        	public void enlarge(Rectangle2D rectangle2d) {
/* 179*/		rectangle2d.add(x0, y0);
/* 180*/		rectangle2d.add(x1, y1);
/* 181*/		/* return; */
        	}

        	// Decompiling method: getSubCurve  Signature: (DDI)Lsun/awt/geom/Curve;
        	// Max stack: 11, #locals: 14, #params: 6
        	// Code length: 133 bytes, Code offset: 3121
        	// Line Number Table found: 9 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 133 Range 0 132 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 133 Range 0 132 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 133 Range 0 132 Init 0
        	// Parameter  5 added: Name i Type I At 0 133 Range 0 132 Init 0
        	// RetValue  14 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 133 Range 0 132 Init 0 fixed
        	// LocalVar   6 added: Name d2 Type D At 66 44 Range 66 109 Init 66
        	// LocalVar   8 added: Name d3 Type D At 77 36 Range 77 112 Init 77
        	// LocalVar  10 added: Name d4 Type D At 96 27 Range 96 122 Init 96
        	// LocalVar  12 added: Name d5 Type D At 115 11 Range 115 125 Init 115
        	public Curve getSubCurve(double d, double d1, int i) {
/* 184*/		if (d == y0 && d1 == y1)
/* 185*/			return getWithDirection(i);
/* 187*/		if (x0 == x1) {
/* 188*/			return ((Curve) (new Order1(x0, d, x1, d1, i)));
        		} else {
/* 190*/			double d2 = x0 - x1;
/* 191*/			double d3 = y0 - y1;
/* 192*/			double d4 = x0 + ((d - y0) * d2) / d3;
/* 193*/			double d5 = x0 + ((d1 - y0) * d2) / d3;
/* 194*/			return ((Curve) (new Order1(d4, d, d5, d1, i)));
        		}
        	}

        	// Decompiling method: getReversedCurve  Signature: ()Lsun/awt/geom/Curve;
        	// Max stack: 11, #locals: 1, #params: 1
        	// Code length: 29 bytes, Code offset: 3324
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 29 Range 0 28 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 29 Range 0 28 Init 0 fixed
        	public Curve getReversedCurve() {
/* 198*/		return ((Curve) (new Order1(x0, y0, x1, y1, -direction)));
        	}

        	// Decompiling method: compareTo  Signature: (Lsun/awt/geom/Curve;[D)I
        	// Max stack: 6, #locals: 18, #params: 3
        	// Code length: 347 bytes, Code offset: 3391
        	// Line Number Table found: 28 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 347 Range 0 346 Init 0 fixed
        	// Parameter  1 added: Name curve Type Lsun/awt/geom/Curve; At 0 347 Range 0 346 Init 0
        	// Parameter  2 added: Name ad Type [D At 0 347 Range 0 346 Init 0
        	// RetValue  18 added: Name <returnValue> Type I At 0 347 Range 0 346 Init 0 fixed
        	// LocalVar   3 added: Name order1 Type Lsun/awt/geom/Order1; At 18 320 Range 18 337 Init 18
        	// LocalVar   4 added: Name d Type D At 162 76 Range 162 237 Init 162
        	// LocalVar   6 added: Name d1 Type D At 173 79 Range 173 251 Init 173
        	// LocalVar   8 added: Name d2 Type D At 184 65 Range 184 248 Init 184
        	// LocalVar  10 added: Name d3 Type D At 195 46 Range 195 240 Init 195
        	// LocalVar  12 added: Name d4 Type D At 208 52 Range 208 259 Init 208
        	// LocalVar  16 added: Name d5 Type D At 254 4 Range 254 257 Init 254
        	// LocalVar  14 added: Name d6 Type D At 261 40 Range 261 300 Init 261
        	public int compareTo(Curve curve, double ad[]) {
/* 202*/		if (!(curve instanceof Order1))
/* 203*/			return super.compareTo(curve, ad);
/* 205*/		Order1 order1 = (Order1)curve;
/* 206*/		if (ad[1] <= ad[0])
/* 207*/			throw new InternalError("yrange already screwed up...");
/* 209*/		ad[1] = Math.min(Math.min(ad[1], y1), order1.y1);
/* 210*/		if (ad[1] <= ad[0])
/* 211*/			throw new InternalError((new StringBuilder()).append("backstepping from ").append(ad[0]).append(" to ").append(ad[1]).toString());
/* 213*/		if (xmax <= order1.xmin)
/* 214*/			return xmin != order1.xmax ? -1 : 0;
/* 216*/		if (xmin >= order1.xmax)
/* 217*/			return 1;
/* 251*/		double d = x1 - x0;
/* 252*/		double d1 = y1 - y0;
/* 253*/		double d2 = order1.x1 - order1.x0;
/* 254*/		double d3 = order1.y1 - order1.y0;
/* 255*/		double d4 = d2 * d1 - d * d3;
        		double d5;
/* 257*/		if (d4 != 0.0D) {
/* 258*/			double d6 = ((x0 - order1.x0) * d1 * d3 - y0 * d * d3) + order1.y0 * d2 * d1;
/* 261*/			d5 = d6 / d4;
/* 262*/			if (d5 <= ad[0]) {
/* 265*/				d5 = Math.min(y1, order1.y1);
        			} else {
/* 268*/				if (d5 < ad[1])
/* 270*/					ad[1] = d5;
/* 273*/				d5 = Math.max(y0, order1.y0);
        			}
        		} else {
/* 279*/			d5 = Math.max(y0, order1.y0);
        		}
/* 281*/		return orderof(XforY(d5), order1.XforY(d5));
        	}

        	// Decompiling method: getSegment  Signature: ([D)I
        	// Max stack: 4, #locals: 2, #params: 2
        	// Code length: 41 bytes, Code offset: 3884
        	// Line Number Table found: 6 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order1; At 0 41 Range 0 40 Init 0 fixed
        	// Parameter  1 added: Name ad Type [D At 0 41 Range 0 40 Init 0
        	// RetValue   2 added: Name <returnValue> Type I At 0 41 Range 0 40 Init 0 fixed
        	public int getSegment(double ad[]) {
/* 285*/		if (direction == 1) {
/* 286*/			ad[0] = x1;
/* 287*/			ad[1] = y1;
        		} else {
/* 289*/			ad[0] = x0;
/* 290*/			ad[1] = y0;
        		}
/* 292*/		return 1;
        	}
}
