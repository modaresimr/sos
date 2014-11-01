// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 3/29/2011 1:44:07 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) definits fieldsfirst noinners ansi nonlb space lnc safe debugmode 
// Source File Name:   Order2.java
// Class Version:      49.0

package sos.tools.geom;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

// Referenced classes of package sun.awt.geom:
//			Curve

// flag ACC_SUPER is set
final class Order2 extends Curve {
	// Constants:          177
	// Interfaces:         0
	// Fields:             14
	// Methods:            31
	// Class Attributes:   1


        	private double x0 = 0;
        	private double y0 = 0;
        	private double cx0 = 0;
        	private double cy0 = 0;
        	private double x1 = 0;
        	private double y1 = 0;
        	private double xmin = 0;
        	private double xmax = 0;
        	private double xcoeff0 = 0;
        	private double xcoeff1 = 0;
        	private double xcoeff2 = 0;
        	private double ycoeff0 = 0;
        	private double ycoeff1 = 0;
        	private double ycoeff2 = 0;

        	// Decompiling method: insert  Signature: (Ljava/util/Vector;[DDDDDDDI)V
        	// Max stack: 14, #locals: 20, #params: 15
        	// Code length: 176 bytes, Code offset: 1693
        	// Line Number Table found: 14 entries
        	// Parameter  0 added: Name vector Type Ljava/util/Vector; At 0 176 Range 0 175 Init 0
        	// Parameter  1 added: Name ad Type [D At 0 176 Range 0 175 Init 0
        	// Parameter  2 added: Name d Type D At 0 176 Range 0 175 Init 0
        	// Parameter  4 added: Name d1 Type D At 0 176 Range 0 175 Init 0
        	// Parameter  6 added: Name d2 Type D At 0 176 Range 0 175 Init 0
        	// Parameter  8 added: Name d3 Type D At 0 176 Range 0 175 Init 0
        	// Parameter 10 added: Name d4 Type D At 0 176 Range 0 175 Init 0
        	// Parameter 12 added: Name d5 Type D At 0 176 Range 0 175 Init 0
        	// Parameter 14 added: Name i Type I At 0 176 Range 0 175 Init 0
        	// RetValue  20 added: Name <returnValue> Type V At 0 176 Range 0 175 Init 0 fixed
        	// LocalVar  15 added: Name j Type I At 10 4 Range 10 13 Init 10
        	// LocalVar  16 added: Name d6 Type D At 38 35 Range 38 72 Init 38
        	// LocalVar  18 added: Name byte0 Type B At 87 40 Range 87 126 Init 87
        	// LocalVar  19 added: Name k Type I At 93 74 Range 93 166 Init 93
        	@SuppressWarnings("unchecked")
			public static void insert(@SuppressWarnings("rawtypes") Vector vector, double ad[], double d, double d1, double d2, 
        			double d3, double d4, double d5, int i) {
/*  38*/		int j = getHorizontalParams(d1, d3, d5, ad);
/*  39*/		if (j == 0) {
/*  42*/			addInstance(vector, d, d1, d2, d3, d4, d5, i);
/*  43*/			return;
        		} else {
/*  46*/			double d6 = ad[0];
/*  47*/			ad[0] = d;
/*  47*/			ad[1] = d1;
/*  48*/			ad[2] = d2;
/*  48*/			ad[3] = d3;
/*  49*/			ad[4] = d4;
/*  49*/			ad[5] = d5;
/*  50*/			split(ad, 0, d6);
/*  51*/			byte byte0 = ((byte)(i != 1 ? 4 : 0));
/*  52*/			int k = 4 - byte0;
/*  53*/			addInstance(vector, ad[byte0], ad[byte0 + 1], ad[byte0 + 2], ad[byte0 + 3], ad[byte0 + 4], ad[byte0 + 5], i);
/*  55*/			addInstance(vector, ad[k], ad[k + 1], ad[k + 2], ad[k + 3], ad[k + 4], ad[k + 5], i);
/*  57*/			return;
        		}
        	}

        	// Decompiling method: addInstance  Signature: (Ljava/util/Vector;DDDDDDI)V
        	// Max stack: 16, #locals: 14, #params: 14
        	// Code length: 67 bytes, Code offset: 1959
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name vector Type Ljava/util/Vector; At 0 67 Range 0 66 Init 0
        	// Parameter  1 added: Name d Type D At 0 67 Range 0 66 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 67 Range 0 66 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 67 Range 0 66 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 67 Range 0 66 Init 0
        	// Parameter  9 added: Name d4 Type D At 0 67 Range 0 66 Init 0
        	// Parameter 11 added: Name d5 Type D At 0 67 Range 0 66 Init 0
        	// Parameter 13 added: Name i Type I At 0 67 Range 0 66 Init 0
        	// RetValue  14 added: Name <returnValue> Type V At 0 67 Range 0 66 Init 0 fixed
        	public static void addInstance(Vector<Object> vector, double d, double d1, double d2, double d3, double d4, double d5, int i) {
/*  64*/		if (d1 > d5)
/*  65*/			vector.add(((new Order2(d4, d5, d2, d3, d, d1, -i))));
/*  66*/		else
/*  66*/		if (d5 > d1)
/*  67*/			vector.add(((new Order2(d, d1, d2, d3, d4, d5, i))));
/*  69*/		/* return; */
        	}

        	// Decompiling method: getHorizontalParams  Signature: (DDD[D)I
        	// Max stack: 4, #locals: 11, #params: 7
        	// Code length: 70 bytes, Code offset: 2080
        	// Line Number Table found: 12 entries
        	// Parameter  0 added: Name d Type D At 0 70 Range 0 69 Init 0
        	// Parameter  2 added: Name d1 Type D At 0 70 Range 0 69 Init 0
        	// Parameter  4 added: Name d2 Type D At 0 70 Range 0 69 Init 0
        	// Parameter  6 added: Name ad Type [D At 0 70 Range 0 69 Init 0
        	// RetValue  11 added: Name <returnValue> Type I At 0 70 Range 0 69 Init 0 fixed
        	// LocalVar   7 added: Name d3 Type D At 29 14 Range 29 42 Init 29
        	// LocalVar   9 added: Name d4 Type D At 44 23 Range 44 66 Init 44
        	public static int getHorizontalParams(double d, double d1, double d2, double ad[]) {
/*  95*/		if (d <= d1 && d1 <= d2)
/*  96*/			return 0;
/*  98*/		d -= d1;
/*  99*/		d2 -= d1;
/* 100*/		double d3 = d + d2;
/* 102*/		if (d3 == 0.0D)
/* 103*/			return 0;
/* 105*/		double d4 = d / d3;
/* 107*/		if (d4 <= 0.0D || d4 >= 1.0D) {
/* 108*/			return 0;
        		} else {
/* 110*/			ad[0] = d4;
/* 111*/			return 1;
        		}
        	}

        	// Decompiling method: split  Signature: ([DID)V
        	// Max stack: 6, #locals: 16, #params: 4
        	// Code length: 173 bytes, Code offset: 2232
        	// Line Number Table found: 19 entries
        	// Parameter  0 added: Name ad Type [D At 0 173 Range 0 172 Init 0
        	// Parameter  1 added: Name i Type I At 0 173 Range 0 172 Init 0
        	// Parameter  2 added: Name d Type D At 0 173 Range 0 172 Init 0
        	// RetValue  16 added: Name <returnValue> Type V At 0 173 Range 0 172 Init 0 fixed
        	// LocalVar  12 added: Name d1 Type D At 11 35 Range 11 45 Init 11
        	// LocalVar  14 added: Name d2 Type D At 25 33 Range 25 57 Init 25
        	// LocalVar   8 added: Name d3 Type D At 33 51 Range 33 83 Init 33
        	// LocalVar  10 added: Name d4 Type D At 40 56 Range 40 95 Init 40
        	// LocalVar  12 added: Name d5 Type D At 52 111 Range 52 162 Init 52
        	// LocalVar  14 added: Name d6 Type D At 64 107 Range 64 170 Init 64
        	// LocalVar   4 added: Name d7 Type D At 71 15 Range 71 85 Init 71
        	// LocalVar   6 added: Name d8 Type D At 78 20 Range 78 97 Init 78
        	// LocalVar   4 added: Name d9 Type D At 90 44 Range 90 133 Init 90
        	// LocalVar   6 added: Name d10 Type D At 102 39 Range 102 140 Init 102
        	// LocalVar   8 added: Name d11 Type D At 114 34 Range 114 147 Init 114
        	// LocalVar  10 added: Name d12 Type D At 126 29 Range 126 154 Init 126
        	// LocalVar   4 name d9(D) merged out into d7(D)
        	// LocalVar   6 name d10(D) merged out into d8(D)
        	// LocalVar   8 name d11(D) merged out into d3(D)
        	// LocalVar  10 name d12(D) merged out into d4(D)
        	// LocalVar  12 name d5(D) merged out into d1(D)
        	// LocalVar  14 name d6(D) merged out into d2(D)
        	public static void split(double ad[], int i, double d) {
        		double d5;
/* 122*/		ad[i + 8] = d5 = ad[i + 4];
        		double d6;
/* 123*/		ad[i + 9] = d6 = ad[i + 5];
/* 124*/		double d3 = ad[i + 2];
/* 125*/		double d4 = ad[i + 3];
/* 126*/		d5 = d3 + (d5 - d3) * d;
/* 127*/		d6 = d4 + (d6 - d4) * d;
/* 128*/		double d1 = ad[i + 0];
/* 129*/		double d2 = ad[i + 1];
/* 130*/		d1 += (d3 - d1) * d;
/* 131*/		d2 += (d4 - d2) * d;
/* 132*/		d3 = d1 + (d5 - d1) * d;
/* 133*/		d4 = d2 + (d6 - d2) * d;
/* 134*/		ad[i + 2] = d1;
/* 135*/		ad[i + 3] = d2;
/* 136*/		ad[i + 4] = d3;
/* 137*/		ad[i + 5] = d4;
/* 138*/		ad[i + 6] = d5;
/* 139*/		ad[i + 7] = d6;
/* 140*/		/* return; */
        	}

        	// Decompiling method: <init>  Signature: (DDDDDDI)V
        	// Max stack: 5, #locals: 14, #params: 14
        	// Code length: 160 bytes, Code offset: 2515
        	// Line Number Table found: 20 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 160 Range 0 159 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 160 Range 0 159 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 160 Range 0 159 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 160 Range 0 159 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 160 Range 0 159 Init 0
        	// Parameter  9 added: Name d4 Type D At 0 160 Range 0 159 Init 0
        	// Parameter 11 added: Name d5 Type D At 0 160 Range 0 159 Init 0
        	// Parameter 13 added: Name i Type I At 0 160 Range 0 159 Init 0
        	// RetValue  14 added: Name <returnValue> Type V At 0 160 Range 0 159 Init 0 fixed
        	public Order2(double d, double d1, double d2, double d3, double d4, double d5, int i) {
/* 147*/		super(i);
/* 151*/		if (d3 < d1)
/* 152*/			d3 = d1;
/* 153*/		else
/* 153*/		if (d3 > d5)
/* 154*/			d3 = d5;
/* 156*/		x0 = d;
/* 157*/		y0 = d1;
/* 158*/		cx0 = d2;
/* 159*/		cy0 = d3;
/* 160*/		x1 = d4;
/* 161*/		y1 = d5;
/* 162*/		xmin = Math.min(Math.min(d, d4), d2);
/* 163*/		xmax = Math.max(Math.max(d, d4), d2);
/* 164*/		xcoeff0 = d;
/* 165*/		xcoeff1 = (d2 + d2) - d - d;
/* 166*/		xcoeff2 = (d - d2 - d2) + d4;
/* 167*/		ycoeff0 = d1;
/* 168*/		ycoeff1 = (d3 + d3) - d1 - d1;
/* 169*/		ycoeff2 = (d1 - d3 - d3) + d5;
/* 170*/		/* return; */
        	}

        	// Decompiling method: getOrder  Signature: ()I
        	// Max stack: 1, #locals: 1, #params: 1
        	// Code length: 2 bytes, Code offset: 2789
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 2 Range 0 1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type I At 0 2 Range 0 1 Init 0 fixed
        	@Override
			public int getOrder() {
/* 173*/		return 2;
        	}

        	// Decompiling method: getXTop  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 2829
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	@Override
			public double getXTop() {
/* 177*/		return x0;
        	}

        	// Decompiling method: getYTop  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 2872
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	@Override
			public double getYTop() {
/* 181*/		return y0;
        	}

        	// Decompiling method: getXBot  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 2915
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	@Override
			public double getXBot() {
/* 185*/		return x1;
        	}

        	// Decompiling method: getYBot  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 2958
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	@Override
			public double getYBot() {
/* 189*/		return y1;
        	}

        	// Decompiling method: getXMin  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 3001
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	@Override
			public double getXMin() {
/* 193*/		return xmin;
        	}

        	// Decompiling method: getXMax  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 3044
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	@Override
			public double getXMax() {
/* 197*/		return xmax;
        	}

        	// Decompiling method: getX0  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 20 bytes, Code offset: 3087
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 20 Range 0 19 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
        	@Override
			public double getX0() {
/* 201*/		return direction != 1 ? x1 : x0;
        	}

        	// Decompiling method: getY0  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 20 bytes, Code offset: 3145
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 20 Range 0 19 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
        	@Override
			public double getY0() {
/* 205*/		return direction != 1 ? y1 : y0;
        	}

        	// Decompiling method: getCX0  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 3203
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getCX0() {
/* 209*/		return cx0;
        	}

        	// Decompiling method: getCY0  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 3246
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
        	public double getCY0() {
/* 213*/		return cy0;
        	}

        	// Decompiling method: getX1  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 20 bytes, Code offset: 3289
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 20 Range 0 19 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
        	@Override
			public double getX1() {
/* 217*/		return direction != -1 ? x1 : x0;
        	}

        	// Decompiling method: getY1  Signature: ()D
        	// Max stack: 2, #locals: 1, #params: 1
        	// Code length: 20 bytes, Code offset: 3347
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 20 Range 0 19 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
        	@Override
			public double getY1() {
/* 221*/		return direction != -1 ? y1 : y0;
        	}

        	// Decompiling method: XforY  Signature: (D)D
        	// Max stack: 4, #locals: 3, #params: 3
        	// Code length: 38 bytes, Code offset: 3405
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 38 Range 0 37 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 38 Range 0 37 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 38 Range 0 37 Init 0 fixed
        	@Override
			public double XforY(double d) {
/* 225*/		if (d <= y0)
/* 226*/			return x0;
/* 228*/		if (d >= y1)
/* 229*/			return x1;
/* 231*/		else
/* 231*/			return XforT(TforY(d));
        	}

        	// Decompiling method: TforY  Signature: (D)D
        	// Max stack: 8, #locals: 3, #params: 3
        	// Code length: 39 bytes, Code offset: 3497
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 39 Range 0 38 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 39 Range 0 38 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 39 Range 0 38 Init 0 fixed
        	@Override
			public double TforY(double d) {
/* 235*/		if (d <= y0)
/* 236*/			return 0.0D;
/* 238*/		if (d >= y1)
/* 239*/			return 1.0D;
/* 241*/		else
/* 241*/			return TforY(d, ycoeff0, ycoeff1, ycoeff2);
        	}

        	// Decompiling method: TforY  Signature: (DDDD)D
        	// Max stack: 6, #locals: 14, #params: 8
        	// Code length: 177 bytes, Code offset: 3590
        	// Line Number Table found: 22 entries
        	// Parameter  0 added: Name d Type D At 0 177 Range 0 176 Init 0
        	// Parameter  2 added: Name d1 Type D At 0 177 Range 0 176 Init 0
        	// Parameter  4 added: Name d2 Type D At 0 177 Range 0 176 Init 0
        	// Parameter  6 added: Name d3 Type D At 0 177 Range 0 176 Init 0
        	// RetValue  14 added: Name <returnValue> Type D At 0 177 Range 0 176 Init 0 fixed
        	// LocalVar   8 added: Name d4 Type D At 16 18 Range 16 33 Init 16
        	// LocalVar   8 added: Name d5 Type D At 52 11 Range 52 62 Init 52
        	// LocalVar   8 added: Name d6 Type D At 66 18 Range 66 83 Init 66
        	// LocalVar  10 added: Name d7 Type D At 89 36 Range 89 124 Init 89
        	// LocalVar  12 added: Name d8 Type D At 96 18 Range 96 113 Init 96
        	// LocalVar  12 added: Name d9 Type D At 126 18 Range 126 143 Init 126
        	// LocalVar   8 added: Name d10 Type D At 146 14 Range 146 159 Init 146
        	// LocalVar  10 added: Name d11 Type D At 155 7 Range 155 161 Init 155
        	// LocalVar   8 name d6(D) merged out into d5(D)
        	public static double TforY(double d, double d1, double d2, double d3) {
/* 249*/		d1 -= d;
/* 250*/		if (d3 == 0.0D) {
/* 256*/			double d4 = -d1 / d2;
/* 257*/			if (d4 >= 0.0D && d4 <= 1.0D)
/* 258*/				return d4;
        		} else {
/* 262*/			double d5 = d2 * d2 - 4D * d3 * d1;
/* 264*/			if (d5 >= 0.0D) {
/* 265*/				d5 = Math.sqrt(d5);
/* 272*/				if (d2 < 0.0D)
/* 273*/					d5 = -d5;
/* 275*/				double d7 = (d2 + d5) / -2D;
/* 277*/				double d9 = d7 / d3;
/* 278*/				if (d9 >= 0.0D && d9 <= 1.0D)
/* 279*/					return d9;
/* 281*/				if (d7 != 0.0D) {
/* 282*/					double d10 = d1 / d7;
/* 283*/					if (d10 >= 0.0D && d10 <= 1.0D)
/* 284*/						return d10;
        				}
        			}
        		}
/* 321*/		double d6 = d1;
/* 322*/		double d8 = d1 + d2 + d3;
/* 323*/		return 0.0D >= (d6 + d8) / 2D ? 1.0D : 0.0D;
        	}

        	// Decompiling method: XforT  Signature: (D)D
        	// Max stack: 4, #locals: 3, #params: 3
        	// Code length: 19 bytes, Code offset: 3889
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 19 Range 0 18 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 19 Range 0 18 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 19 Range 0 18 Init 0 fixed
        	@Override
			public double XforT(double d) {
/* 327*/		return (xcoeff2 * d + xcoeff1) * d + xcoeff0;
        	}

        	// Decompiling method: YforT  Signature: (D)D
        	// Max stack: 4, #locals: 3, #params: 3
        	// Code length: 19 bytes, Code offset: 3946
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 19 Range 0 18 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 19 Range 0 18 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 19 Range 0 18 Init 0 fixed
        	@Override
			public double YforT(double d) {
/* 331*/		return (ycoeff2 * d + ycoeff1) * d + ycoeff0;
        	}

        	// Decompiling method: dXforT  Signature: (DI)D
        	// Max stack: 4, #locals: 4, #params: 4
        	// Code length: 74 bytes, Code offset: 4003
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 74 Range 0 73 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 74 Range 0 73 Init 0
        	// Parameter  3 added: Name i Type I At 0 74 Range 0 73 Init 0
        	// RetValue   4 added: Name <returnValue> Type D At 0 74 Range 0 73 Init 0 fixed
        	@Override
			public double dXforT(double d, int i) {
/* 335*/		switch (i) {
/* 337*/		case 0: // '\0'
/* 337*/			return (xcoeff2 * d + xcoeff1) * d + xcoeff0;

/* 339*/		case 1: // '\001'
/* 339*/			return 2D * xcoeff2 * d + xcoeff1;

/* 341*/		case 2: // '\002'
/* 341*/			return 2D * xcoeff2;
        		}
/* 343*/		return 0.0D;
        	}

        	// Decompiling method: dYforT  Signature: (DI)D
        	// Max stack: 4, #locals: 4, #params: 4
        	// Code length: 74 bytes, Code offset: 4131
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 74 Range 0 73 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 74 Range 0 73 Init 0
        	// Parameter  3 added: Name i Type I At 0 74 Range 0 73 Init 0
        	// RetValue   4 added: Name <returnValue> Type D At 0 74 Range 0 73 Init 0 fixed
        	@Override
			public double dYforT(double d, int i) {
/* 348*/		switch (i) {
/* 350*/		case 0: // '\0'
/* 350*/			return (ycoeff2 * d + ycoeff1) * d + ycoeff0;

/* 352*/		case 1: // '\001'
/* 352*/			return 2D * ycoeff2 * d + ycoeff1;

/* 354*/		case 2: // '\002'
/* 354*/			return 2D * ycoeff2;
        		}
/* 356*/		return 0.0D;
        	}

        	// Decompiling method: nextVertical  Signature: (DD)D
        	// Max stack: 6, #locals: 7, #params: 5
        	// Code length: 35 bytes, Code offset: 4259
        	// Line Number Table found: 4 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 35 Range 0 34 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 35 Range 0 34 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 35 Range 0 34 Init 0
        	// RetValue   7 added: Name <returnValue> Type D At 0 35 Range 0 34 Init 0 fixed
        	// LocalVar   5 added: Name d2 Type D At 14 18 Range 14 31 Init 14
        	@Override
			public double nextVertical(double d, double d1) {
/* 361*/		double d2 = -xcoeff1 / (2D * xcoeff2);
/* 362*/		if (d2 > d && d2 < d1)
/* 363*/			return d2;
/* 365*/		else
/* 365*/			return d1;
        	}

        	// Decompiling method: enlarge  Signature: (Ljava/awt/geom/Rectangle2D;)V
        	// Max stack: 6, #locals: 4, #params: 2
        	// Code length: 66 bytes, Code offset: 4344
        	// Line Number Table found: 6 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 66 Range 0 65 Init 0 fixed
        	// Parameter  1 added: Name rectangle2d Type Ljava/awt/geom/Rectangle2D; At 0 66 Range 0 65 Init 0
        	// RetValue   4 added: Name <returnValue> Type V At 0 66 Range 0 65 Init 0 fixed
        	// LocalVar   2 added: Name d Type D At 26 21 Range 26 46 Init 26
        	@Override
			public void enlarge(Rectangle2D rectangle2d) {
/* 369*/		rectangle2d.add(x0, y0);
/* 370*/		double d = -xcoeff1 / (2D * xcoeff2);
/* 371*/		if (d > 0.0D && d < 1.0D)
/* 372*/			rectangle2d.add(XforT(d), YforT(d));
/* 374*/		rectangle2d.add(x1, y1);
/* 375*/		/* return; */
        	}

        	// Decompiling method: getSubCurve  Signature: (DDI)Lsun/awt/geom/Curve;
        	// Max stack: 15, #locals: 12, #params: 6
        	// Code length: 218 bytes, Code offset: 4468
        	// Line Number Table found: 22 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 218 Range 0 217 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 218 Range 0 217 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 218 Range 0 217 Init 0
        	// Parameter  5 added: Name i Type I At 0 218 Range 0 217 Init 0
        	// RetValue  12 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 218 Range 0 217 Init 0 fixed
        	// LocalVar   6 added: Name d2 Type D At 26 143 Range 26 168 Init 26
        	// LocalVar   8 added: Name d3 Type D At 59 112 Range 59 170 Init 59
        	// LocalVar  10 added: Name ad Type [D At 86 120 Range 86 205 Init 86
        	// LocalVar  11 added: Name flag Type Z At 159 49 Range 159 207 Init 159
        	// LocalVar  11 chged: Name byte0 Oname flag Type B At 176 32 Range 159 207 Init 159
        	@Override
			public Curve getSubCurve(double d, double d1, int i) {
        		double d2;
/* 379*/		if (d <= y0) {
/* 380*/			if (d1 >= y1)
/* 381*/				return getWithDirection(i);
/* 383*/			d2 = 0.0D;
        		} else {
/* 385*/			d2 = TforY(d, ycoeff0, ycoeff1, ycoeff2);
        		}
        		double d3;
/* 387*/		if (d1 >= y1)
/* 388*/			d3 = 1.0D;
/* 390*/		else
/* 390*/			d3 = TforY(d1, ycoeff0, ycoeff1, ycoeff2);
/* 392*/		double ad[] = new double[10];
/* 393*/		ad[0] = x0;
/* 394*/		ad[1] = y0;
/* 395*/		ad[2] = cx0;
/* 396*/		ad[3] = cy0;
/* 397*/		ad[4] = x1;
/* 398*/		ad[5] = y1;
/* 399*/		if (d3 < 1.0D)
/* 400*/			split(ad, 0, d3);
        		byte byte0;
/* 403*/		if (d2 <= 0.0D) {
/* 404*/			byte0 = 0;
        		} else {
/* 406*/			split(ad, 0, d2 / d3);
/* 407*/			byte0 = 4;
        		}
/* 409*/		return ((new Order2(ad[byte0 + 0], d, ad[byte0 + 2], ad[byte0 + 3], ad[byte0 + 4], d1, i)));
        	}

        	// Decompiling method: getReversedCurve  Signature: ()Lsun/awt/geom/Curve;
        	// Max stack: 15, #locals: 1, #params: 1
        	// Code length: 37 bytes, Code offset: 4808
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 37 Range 0 36 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 37 Range 0 36 Init 0 fixed
        	@Override
			public Curve getReversedCurve() {
/* 416*/		return ((new Order2(x0, y0, cx0, cy0, x1, y1, -direction)));
        	}

        	// Decompiling method: getSegment  Signature: ([D)I
        	// Max stack: 4, #locals: 2, #params: 2
        	// Code length: 55 bytes, Code offset: 4883
        	// Line Number Table found: 8 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 55 Range 0 54 Init 0 fixed
        	// Parameter  1 added: Name ad Type [D At 0 55 Range 0 54 Init 0
        	// RetValue   2 added: Name <returnValue> Type I At 0 55 Range 0 54 Init 0 fixed
        	@Override
			public int getSegment(double ad[]) {
/* 420*/		ad[0] = cx0;
/* 421*/		ad[1] = cy0;
/* 422*/		if (direction == 1) {
/* 423*/			ad[2] = x1;
/* 424*/			ad[3] = y1;
        		} else {
/* 426*/			ad[2] = x0;
/* 427*/			ad[3] = y0;
        		}
/* 429*/		return 2;
        	}

        	// Decompiling method: controlPointString  Signature: ()Ljava/lang/String;
        	// Max stack: 3, #locals: 1, #params: 1
        	// Code length: 46 bytes, Code offset: 5004
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Order2; At 0 46 Range 0 45 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type Ljava/lang/String; At 0 46 Range 0 45 Init 0 fixed
        	@Override
			public String controlPointString() {
/* 433*/		return (new StringBuilder()).append("(").append(round(cx0)).append(", ").append(round(cy0)).append("), ").toString();
        	}
}
