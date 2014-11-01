// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 3/29/2011 1:44:31 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) definits fieldsfirst noinners ansi nonlb space lnc safe debugmode 
// Source File Name:   Order3.java
// Class Version:      49.0

package sos.tools.geom;

import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

// Referenced classes of package sun.awt.geom:
//			Curve, Order2

// flag ACC_SUPER is set
final class Order3 extends Curve {
	// Constants:          258
	// Interfaces:         0
	// Fields:             24
	// Methods:            33
	// Class Attributes:   1

	private double x0 = 0;
	private double y0 = 0;
	private double cx0 = 0;
	private double cy0 = 0;
	private double cx1 = 0;
	private double cy1 = 0;
	private double x1 = 0;
	private double y1 = 0;
	private double xmin = 0;
	private double xmax = 0;
	private double xcoeff0 = 0;
	private double xcoeff1 = 0;
	private double xcoeff2 = 0;
	private double xcoeff3 = 0;
	private double ycoeff0 = 0;
	private double ycoeff1 = 0;
	private double ycoeff2 = 0;
	private double ycoeff3 = 0;
	private double TforY1 = 0;
	private double YforT1 = 0;
	private double TforY2 = 0;
	private double YforT2 = 0;
	private double TforY3 = 0;
	private double YforT3 = 0;

	// Decompiling method: insert  Signature: (Ljava/util/Vector;[DDDDDDDDDI)V
	// Max stack: 18, #locals: 23, #params: 19
	// Code length: 257 bytes, Code offset: 2287
	// Line Number Table found: 27 entries
	// Parameter  0 added: Name vector Type Ljava/util/Vector; At 0 257 Range 0 256 Init 0
	// Parameter  1 added: Name ad Type [D At 0 257 Range 0 256 Init 0
	// Parameter  2 added: Name d Type D At 0 257 Range 0 256 Init 0
	// Parameter  4 added: Name d1 Type D At 0 257 Range 0 256 Init 0
	// Parameter  6 added: Name d2 Type D At 0 257 Range 0 256 Init 0
	// Parameter  8 added: Name d3 Type D At 0 257 Range 0 256 Init 0
	// Parameter 10 added: Name d4 Type D At 0 257 Range 0 256 Init 0
	// Parameter 12 added: Name d5 Type D At 0 257 Range 0 256 Init 0
	// Parameter 14 added: Name d6 Type D At 0 257 Range 0 256 Init 0
	// Parameter 16 added: Name d7 Type D At 0 257 Range 0 256 Init 0
	// Parameter 18 added: Name i Type I At 0 257 Range 0 256 Init 0
	// RetValue  23 added: Name <returnValue> Type V At 0 257 Range 0 256 Init 0 fixed
	// LocalVar  19 added: Name j Type I At 12 226 Range 12 237 Init 12
	// LocalVar  20 added: Name d8 Type D At 88 55 Range 88 142 Init 88
	// LocalVar  20 added: Name d9 Type D At 145 7 Range 145 151 Init 145
	// LocalVar  22 added: Name byte0 Type B At 156 97 Range 156 252 Init 156
	// LocalVar  22 chged: Name k Oname byte0 Type I At 172 81 Range 156 252 Init 156
	// LocalVar  20 name d9(D) merged out into d8(D)
	@SuppressWarnings("unchecked")
	public static void insert(@SuppressWarnings("rawtypes") Vector vector, double ad[], double d, double d1, double d2, double d3, double d4, double d5, double d6, double d7, int i) {
		/* 45 */int j = getHorizontalParams(d1, d3, d5, d7, ad);
		/* 46 */if (j == 0) {
			/* 49 */addInstance(vector, d, d1, d2, d3, d4, d5, d6, d7, i);
			/* 50 */return;
		}
		/* 53 */ad[3] = d;
		/* 53 */ad[4] = d1;
		/* 54 */ad[5] = d2;
		/* 54 */ad[6] = d3;
		/* 55 */ad[7] = d4;
		/* 55 */ad[8] = d5;
		/* 56 */ad[9] = d6;
		/* 56 */ad[10] = d7;
		/* 57 */double d8 = ad[0];
		/* 58 */if (j > 1 && d8 > ad[1]) {
			/* 60 */ad[0] = ad[1];
			/* 61 */ad[1] = d8;
			/* 62 */d8 = ad[0];
		}
		/* 64 */split(ad, 3, d8);
		/* 65 */if (j > 1) {
			/* 67 */d8 = (ad[1] - d8) / (1.0D - d8);
			/* 68 */split(ad, 9, d8);
		}
		/* 70 */int k = 3;
		/* 71 */if (i == -1)
			/* 72 */k += j * 6;
		/* 74 */while (j >= 0) {
			/* 75 */addInstance(vector, ad[k + 0], ad[k + 1], ad[k + 2], ad[k + 3], ad[k + 4], ad[k + 5], ad[k + 6], ad[k + 7], i);
			/* 81 */j--;
			/* 82 */if (i == 1)
				/* 83 */k += 6;
			/* 85 */else
				/* 85 */k -= 6;
		}
		/* 88 *//* return; */
	}

	// Decompiling method: addInstance  Signature: (Ljava/util/Vector;DDDDDDDDI)V
	// Max stack: 20, #locals: 18, #params: 18
	// Code length: 75 bytes, Code offset: 2686
	// Line Number Table found: 5 entries
	// Parameter  0 added: Name vector Type Ljava/util/Vector; At 0 75 Range 0 74 Init 0
	// Parameter  1 added: Name d Type D At 0 75 Range 0 74 Init 0
	// Parameter  3 added: Name d1 Type D At 0 75 Range 0 74 Init 0
	// Parameter  5 added: Name d2 Type D At 0 75 Range 0 74 Init 0
	// Parameter  7 added: Name d3 Type D At 0 75 Range 0 74 Init 0
	// Parameter  9 added: Name d4 Type D At 0 75 Range 0 74 Init 0
	// Parameter 11 added: Name d5 Type D At 0 75 Range 0 74 Init 0
	// Parameter 13 added: Name d6 Type D At 0 75 Range 0 74 Init 0
	// Parameter 15 added: Name d7 Type D At 0 75 Range 0 74 Init 0
	// Parameter 17 added: Name i Type I At 0 75 Range 0 74 Init 0
	// RetValue  18 added: Name <returnValue> Type V At 0 75 Range 0 74 Init 0 fixed
	public static void addInstance(Vector<Object> vector, double d, double d1, double d2, double d3, double d4, double d5, double d6, double d7, int i) {
		/* 96 */if (d1 > d7)
			/* 97 */vector.add(((new Order3(d6, d7, d4, d5, d2, d3, d, d1, -i))));
		/* 99 */else
		/* 99 */if (d7 > d1)
			/* 100 */vector.add(((new Order3(d, d1, d2, d3, d4, d5, d6, d7, i))));
		/* 103 *//* return; */
	}

	// Decompiling method: getHorizontalParams  Signature: (DDDD[D)I
	// Max stack: 6, #locals: 14, #params: 9
	// Code length: 140 bytes, Code offset: 2815
	// Line Number Table found: 18 entries
	// Parameter  0 added: Name d Type D At 0 140 Range 0 139 Init 0
	// Parameter  2 added: Name d1 Type D At 0 140 Range 0 139 Init 0
	// Parameter  4 added: Name d2 Type D At 0 140 Range 0 139 Init 0
	// Parameter  6 added: Name d3 Type D At 0 140 Range 0 139 Init 0
	// Parameter  8 added: Name ad Type [D At 0 140 Range 0 139 Init 0
	// RetValue  14 added: Name <returnValue> Type I At 0 140 Range 0 139 Init 0 fixed
	// LocalVar   9 added: Name i Type I At 78 12 Range 78 89 Init 78
	// LocalVar  10 added: Name flag Type Z At 81 58 Range 81 138 Init 81
	// LocalVar  11 added: Name flag1 Type Z At 84 50 Range 84 133 Init 84
	// LocalVar  11 chged: Name j Oname flag1 Type I At 86 2 Range 84 133 Init 84
	// LocalVar  12 added: Name d4 Type D At 98 29 Range 98 126 Init 98
	// LocalVar  10 chged: Name k Oname flag Type I At 114 2 Range 81 138 Init 81
	public static int getHorizontalParams(double d, double d1, double d2, double d3, double ad[]) {
		/* 146 */if (d <= d1 && d1 <= d2 && d2 <= d3)
			/* 147 */return 0;
		/* 149 */d3 -= d2;
		/* 150 */d2 -= d1;
		/* 151 */d1 -= d;
		/* 152 */ad[0] = d1;
		/* 153 */ad[1] = (d2 - d1) * 2D;
		/* 154 */ad[2] = (d3 - d2 - d2) + d1;
		/* 155 */int i = QuadCurve2D.solveQuadratic(ad, ad);
		/* 156 */int j = 0;
		/* 157 */for (int k = 0; k < i; k++) {
			/* 158 */double d4 = ad[k];
			/* 160 */if (d4 <= 0.0D || d4 >= 1.0D)
				/* 161 */continue;
			/* 161 */if (j < k)
				/* 162 */ad[j] = d4;
			/* 164 */j++;
		}

		/* 167 */return j;
	}

	// Decompiling method: split  Signature: ([DID)V
	// Max stack: 8, #locals: 20, #params: 4
	// Code length: 285 bytes, Code offset: 3061
	// Line Number Table found: 29 entries
	// Parameter  0 added: Name ad Type [D At 0 285 Range 0 284 Init 0
	// Parameter  1 added: Name i Type I At 0 285 Range 0 284 Init 0
	// Parameter  2 added: Name d Type D At 0 285 Range 0 284 Init 0
	// RetValue  20 added: Name <returnValue> Type V At 0 285 Range 0 284 Init 0 fixed
	// LocalVar  16 added: Name d1 Type D At 12 36 Range 12 47 Init 12
	// LocalVar  18 added: Name d2 Type D At 27 33 Range 27 59 Init 27
	// LocalVar  12 added: Name d3 Type D At 35 89 Range 35 123 Init 35
	// LocalVar  14 added: Name d4 Type D At 42 94 Range 42 135 Init 42
	// LocalVar  16 added: Name d5 Type D At 54 221 Range 54 274 Init 54
	// LocalVar  18 added: Name d6 Type D At 66 217 Range 66 282 Init 66
	// LocalVar   4 added: Name d7 Type D At 73 29 Range 73 101 Init 73
	// LocalVar   6 added: Name d8 Type D At 80 34 Range 80 113 Init 80
	// LocalVar   8 added: Name d9 Type D At 87 39 Range 87 125 Init 87
	// LocalVar  10 added: Name d10 Type D At 94 44 Range 94 137 Init 94
	// LocalVar   4 added: Name d11 Type D At 106 92 Range 106 197 Init 106
	// LocalVar   6 added: Name d12 Type D At 118 87 Range 118 204 Init 118
	// LocalVar   8 added: Name d13 Type D At 130 42 Range 130 171 Init 130
	// LocalVar  10 added: Name d14 Type D At 142 42 Range 142 183 Init 142
	// LocalVar  12 added: Name d15 Type D At 154 105 Range 154 258 Init 154
	// LocalVar  14 added: Name d16 Type D At 166 101 Range 166 266 Init 166
	// LocalVar   8 added: Name d17 Type D At 178 53 Range 178 230 Init 178
	// LocalVar  10 added: Name d18 Type D At 190 57 Range 190 246 Init 190
	// LocalVar   4 name d11(D) merged out into d7(D)
	// LocalVar   6 name d12(D) merged out into d8(D)
	// LocalVar   8 name d13(D) merged out into d9(D)
	// LocalVar   8 name d17(D) merged out into d9(D)
	// LocalVar  10 name d14(D) merged out into d10(D)
	// LocalVar  10 name d18(D) merged out into d10(D)
	// LocalVar  12 name d15(D) merged out into d3(D)
	// LocalVar  14 name d16(D) merged out into d4(D)
	// LocalVar  16 name d5(D) merged out into d1(D)
	// LocalVar  18 name d6(D) merged out into d2(D)
	public static void split(double ad[], int i, double d) {
		double d7;
		/* 178 */ad[i + 12] = d7 = ad[i + 6];
		double d8;
		/* 179 */ad[i + 13] = d8 = ad[i + 7];
		/* 180 */double d5 = ad[i + 4];
		/* 181 */double d6 = ad[i + 5];
		/* 182 */d7 = d5 + (d7 - d5) * d;
		/* 183 */d8 = d6 + (d8 - d6) * d;
		/* 184 */double d1 = ad[i + 0];
		/* 185 */double d2 = ad[i + 1];
		/* 186 */double d3 = ad[i + 2];
		/* 187 */double d4 = ad[i + 3];
		/* 188 */d1 += (d3 - d1) * d;
		/* 189 */d2 += (d4 - d2) * d;
		/* 190 */d3 += (d5 - d3) * d;
		/* 191 */d4 += (d6 - d4) * d;
		/* 192 */d5 = d3 + (d7 - d3) * d;
		/* 193 */d6 = d4 + (d8 - d4) * d;
		/* 194 */d3 = d1 + (d3 - d1) * d;
		/* 195 */d4 = d2 + (d4 - d2) * d;
		/* 196 */ad[i + 2] = d1;
		/* 197 */ad[i + 3] = d2;
		/* 198 */ad[i + 4] = d3;
		/* 199 */ad[i + 5] = d4;
		/* 200 */ad[i + 6] = d3 + (d5 - d3) * d;
		/* 201 */ad[i + 7] = d4 + (d6 - d4) * d;
		/* 202 */ad[i + 8] = d5;
		/* 203 */ad[i + 9] = d6;
		/* 204 */ad[i + 10] = d7;
		/* 205 */ad[i + 11] = d8;
		/* 206 *//* return; */
	}

	// Decompiling method: <init>  Signature: (DDDDDDDDI)V
	// Max stack: 7, #locals: 18, #params: 18
	// Code length: 236 bytes, Code offset: 3496
	// Line Number Table found: 23 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 236 Range 0 235 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 236 Range 0 235 Init 0
	// Parameter  3 added: Name d1 Type D At 0 236 Range 0 235 Init 0
	// Parameter  5 added: Name d2 Type D At 0 236 Range 0 235 Init 0
	// Parameter  7 added: Name d3 Type D At 0 236 Range 0 235 Init 0
	// Parameter  9 added: Name d4 Type D At 0 236 Range 0 235 Init 0
	// Parameter 11 added: Name d5 Type D At 0 236 Range 0 235 Init 0
	// Parameter 13 added: Name d6 Type D At 0 236 Range 0 235 Init 0
	// Parameter 15 added: Name d7 Type D At 0 236 Range 0 235 Init 0
	// Parameter 17 added: Name i Type I At 0 236 Range 0 235 Init 0
	// RetValue  18 added: Name <returnValue> Type V At 0 236 Range 0 235 Init 0 fixed
	public Order3(double d, double d1, double d2, double d3, double d4, double d5, double d6, double d7, int i) {
		/* 214 */super(i);
		/* 218 */if (d3 < d1)
			/* 218 */d3 = d1;
		/* 219 */if (d5 > d7)
			/* 219 */d5 = d7;
		/* 220 */x0 = d;
		/* 221 */y0 = d1;
		/* 222 */cx0 = d2;
		/* 223 */cy0 = d3;
		/* 224 */cx1 = d4;
		/* 225 */cy1 = d5;
		/* 226 */x1 = d6;
		/* 227 */y1 = d7;
		/* 228 */xmin = Math.min(Math.min(d, d6), Math.min(d2, d4));
		/* 229 */xmax = Math.max(Math.max(d, d6), Math.max(d2, d4));
		/* 230 */xcoeff0 = d;
		/* 231 */xcoeff1 = (d2 - d) * 3D;
		/* 232 */xcoeff2 = ((d4 - d2 - d2) + d) * 3D;
		/* 233 */xcoeff3 = d6 - (d4 - d2) * 3D - d;
		/* 234 */ycoeff0 = d1;
		/* 235 */ycoeff1 = (d3 - d1) * 3D;
		/* 236 */ycoeff2 = ((d5 - d3 - d3) + d1) * 3D;
		/* 237 */ycoeff3 = d7 - (d5 - d3) * 3D - d1;
		/* 238 */YforT1 = YforT2 = YforT3 = d1;
		/* 239 *//* return; */
	}

	// Decompiling method: getOrder  Signature: ()I
	// Max stack: 1, #locals: 1, #params: 1
	// Code length: 2 bytes, Code offset: 3858
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 2 Range 0 1 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type I At 0 2 Range 0 1 Init 0 fixed
	@Override
	public int getOrder() {
		/* 242 */return 3;
	}

	// Decompiling method: getXTop  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 3898
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 5 Range 0 4 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	@Override
	public double getXTop() {
		/* 246 */return x0;
	}

	// Decompiling method: getYTop  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 3941
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 5 Range 0 4 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	@Override
	public double getYTop() {
		/* 250 */return y0;
	}

	// Decompiling method: getXBot  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 3984
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 5 Range 0 4 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	@Override
	public double getXBot() {
		/* 254 */return x1;
	}

	// Decompiling method: getYBot  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 4027
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 5 Range 0 4 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	@Override
	public double getYBot() {
		/* 258 */return y1;
	}

	// Decompiling method: getXMin  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 4070
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 5 Range 0 4 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	@Override
	public double getXMin() {
		/* 262 */return xmin;
	}

	// Decompiling method: getXMax  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 4113
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 5 Range 0 4 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	@Override
	public double getXMax() {
		/* 266 */return xmax;
	}

	// Decompiling method: getX0  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 20 bytes, Code offset: 4156
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 20 Range 0 19 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
	@Override
	public double getX0() {
		/* 270 */return direction != 1 ? x1 : x0;
	}

	// Decompiling method: getY0  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 20 bytes, Code offset: 4214
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 20 Range 0 19 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
	@Override
	public double getY0() {
		/* 274 */return direction != 1 ? y1 : y0;
	}

	// Decompiling method: getCX0  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 20 bytes, Code offset: 4272
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 20 Range 0 19 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
	public double getCX0() {
		/* 278 */return direction != 1 ? cx1 : cx0;
	}

	// Decompiling method: getCY0  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 20 bytes, Code offset: 4330
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 20 Range 0 19 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
	public double getCY0() {
		/* 282 */return direction != 1 ? cy1 : cy0;
	}

	// Decompiling method: getCX1  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 20 bytes, Code offset: 4388
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 20 Range 0 19 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
	public double getCX1() {
		/* 286 */return direction != -1 ? cx1 : cx0;
	}

	// Decompiling method: getCY1  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 20 bytes, Code offset: 4446
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 20 Range 0 19 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
	public double getCY1() {
		/* 290 */return direction != -1 ? cy1 : cy0;
	}

	// Decompiling method: getX1  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 20 bytes, Code offset: 4504
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 20 Range 0 19 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
	@Override
	public double getX1() {
		/* 294 */return direction != -1 ? x1 : x0;
	}

	// Decompiling method: getY1  Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 20 bytes, Code offset: 4562
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 20 Range 0 19 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type D At 0 20 Range 0 19 Init 0 fixed
	@Override
	public double getY1() {
		/* 298 */return direction != -1 ? y1 : y0;
	}

	// Decompiling method: TforY  Signature: (D)D
	// Max stack: 15, #locals: 29, #params: 3
	// Code length: 563 bytes, Code offset: 4620
	// Line Number Table found: 54 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 563 Range 0 562 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 563 Range 0 562 Init 0
	// RetValue  29 added: Name <returnValue> Type D At 0 563 Range 0 562 Init 0 fixed
	// LocalVar   3 added: Name d1 Type D At 99 314 Range 99 412 Init 99
	// LocalVar   5 added: Name d2 Type D At 109 306 Range 109 414 Init 109
	// LocalVar   7 added: Name d3 Type D At 122 295 Range 122 416 Init 122
	// LocalVar   9 added: Name flag Type Z At 125 2 Range 125 126 Init 125
	// LocalVar  10 added: Name d4 Type D At 141 265 Range 141 405 Init 141
	// LocalVar  12 added: Name d5 Type D At 172 200 Range 172 371 Init 172
	// LocalVar  14 added: Name d6 Type D At 179 173 Range 179 351 Init 179
	// LocalVar  16 added: Name d7 Type D At 189 165 Range 189 353 Init 189
	// LocalVar  18 added: Name d8 Type D At 196 229 Range 196 424 Init 196
	// LocalVar  22 added: Name d9 Type D At 217 96 Range 217 312 Init 217
	// LocalVar  20 added: Name d10 Type D At 255 307 Range 255 561 Init 255
	// LocalVar  22 added: Name flag1 Type Z At 348 37 Range 348 384 Init 348
	// LocalVar  23 added: Name d11 Type D At 358 16 Range 358 373 Init 358
	// LocalVar  25 added: Name d12 Type D At 381 39 Range 381 419 Init 381
	// LocalVar  27 added: Name d13 Type D At 409 13 Range 409 421 Init 409
	// LocalVar  22 added: Name d14 Type D At 439 20 Range 439 458 Init 439
	// LocalVar  24 added: Name d15 Type D At 442 25 Range 442 466 Init 442
	// LocalVar  26 added: Name d16 Type D At 480 18 Range 480 497 Init 480
	// LocalVar  28 hasn't been used
	@Override
	public double TforY(double d) {
		/* 316 */if (d <= y0)
			/* 316 */return 0.0D;
		/* 317 */if (d >= y1)
			/* 317 */return 1.0D;
		/* 318 */if (d == YforT1)
			/* 318 */return TforY1;
		/* 319 */if (d == YforT2)
			/* 319 */return TforY2;
		/* 320 */if (d == YforT3)
			/* 320 */return TforY3;
		/* 322 */if (ycoeff3 == 0.0D)
			/* 324 */return Order2.TforY(d, ycoeff0, ycoeff1, ycoeff2);
		/* 326 */double d1 = ycoeff2 / ycoeff3;
		/* 327 */double d2 = ycoeff1 / ycoeff3;
		/* 328 */double d3 = (ycoeff0 - d) / ycoeff3;
		/* 329 */@SuppressWarnings("unused")
		boolean flag = false;
		/* 330 */double d4 = (d1 * d1 - 3D * d2) / 9D;
		/* 331 */double d5 = ((2D * d1 * d1 * d1 - 9D * d1 * d2) + 27D * d3) / 54D;
		/* 332 */double d6 = d5 * d5;
		/* 333 */double d7 = d4 * d4 * d4;
		/* 334 */double d8 = d1 / 3D;
		double d9;
		/* 336 */if (d6 < d7) {
			/* 337 */double d10 = Math.acos(d5 / Math.sqrt(d7));
			/* 338 */d4 = -2D * Math.sqrt(d4);
			/* 339 */d9 = refine(d1, d2, d3, d, d4 * Math.cos(d10 / 3D) - d8);
			/* 340 */if (d9 < 0.0D)
				/* 341 */d9 = refine(d1, d2, d3, d, d4 * Math.cos((d10 + 6.2831853071795862D) / 3D) - d8);
			/* 344 */if (d9 < 0.0D)
				/* 345 */d9 = refine(d1, d2, d3, d, d4 * Math.cos((d10 - 6.2831853071795862D) / 3D) - d8);
		} else {
			/* 349 */boolean flag1 = d5 < 0.0D;
			/* 350 */double d12 = Math.sqrt(d6 - d7);
			/* 351 */if (flag1)
				/* 352 */d5 = -d5;
			/* 354 */double d14 = Math.pow(d5 + d12, 0.33333333333333331D);
			/* 355 */if (!flag1)
				/* 356 */d14 = -d14;
			/* 358 */double d16 = d14 != 0.0D ? d4 / d14 : 0.0D;
			/* 359 */d9 = refine(d1, d2, d3, d, (d14 + d16) - d8);
		}
		/* 361 */if (d9 < 0.0D) {
			/* 363 */double d11 = 0.0D;
			/* 364 */double d13 = 1.0D;
			/* 366 */do {
				/* 366 */d9 = (d11 + d13) / 2D;
				/* 367 */if (d9 == d11 || d9 == d13)
					/* 368 */break;
				/* 370 */double d15 = YforT(d9);
				/* 371 */if (d15 < d) {
					/* 372 */d11 = d9;
					/* 372 */continue;
				}
				/* 373 */if (d15 <= d)
					/* 374 */break;
				/* 374 */d13 = d9;
			} while (true);
		}
		/* 380 */if (d9 >= 0.0D) {
			/* 381 */TforY3 = TforY2;
			/* 382 */YforT3 = YforT2;
			/* 383 */TforY2 = TforY1;
			/* 384 */YforT2 = YforT1;
			/* 385 */TforY1 = d9;
			/* 386 */YforT1 = d;
		}
		/* 388 */return d9;
	}

	// Decompiling method: refine  Signature: (DDDDD)D
	// Max stack: 6, #locals: 26, #params: 11
	// Code length: 242 bytes, Code offset: 5433
	// Line Number Table found: 34 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 242 Range 0 241 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 242 Range 0 241 Init 0
	// Parameter  3 added: Name d1 Type D At 0 242 Range 0 241 Init 0
	// Parameter  5 added: Name d2 Type D At 0 242 Range 0 241 Init 0
	// Parameter  7 added: Name d3 Type D At 0 242 Range 0 241 Init 0
	// Parameter  9 added: Name d4 Type D At 0 242 Range 0 241 Init 0
	// RetValue  26 added: Name <returnValue> Type D At 0 242 Range 0 241 Init 0 fixed
	// LocalVar  11 added: Name d5 Type D At 28 116 Range 28 143 Init 28
	// LocalVar  13 added: Name d6 Type D At 40 123 Range 40 162 Init 40
	// LocalVar  15 added: Name d7 Type D At 43 128 Range 43 170 Init 43
	// LocalVar  17 added: Name d8 Type D At 57 2 Range 57 58 Init 57
	// LocalVar  19 added: Name d9 Type D At 61 2 Range 61 62 Init 61
	// LocalVar  21 added: Name flag Type Z At 64 12 Range 64 75 Init 64
	// LocalVar  22 added: Name d10 Type D At 88 23 Range 88 110 Init 88
	// LocalVar  22 added: Name d11 Type D At 123 24 Range 123 146 Init 123
	// LocalVar  24 added: Name d12 Type D At 149 34 Range 149 182 Init 149
	// LocalVar  22 added: Name flag1 Type Z At 224 2 Range 224 225 Init 224
	// LocalVar  23 hasn't been used
	public double refine(double d, double d1, double d2, double d3, double d4) {
		/* 394 */if (d4 < -0.10000000000000001D || d4 > 1.1000000000000001D)
			/* 395 */return -1D;
		/* 397 */double d5 = YforT(d4);
		double d6;
		double d7;
		/* 399 */if (d5 < d3) {
			/* 400 */d6 = d4;
			/* 401 */d7 = 1.0D;
		} else {
			/* 403 */d6 = 0.0D;
			/* 404 */d7 = d4;
		}
		/* 406 */@SuppressWarnings("unused")
		double d8 = d4;
		/* 407 */@SuppressWarnings("unused")
		double d9 = d5;
		/* 408 */boolean flag = true;
		/* 409 */do {
			/* 409 */if (d5 == d3)
				/* 410 */break;
			/* 410 */if (!flag) {
				/* 411 */double d10 = (d6 + d7) / 2D;
				/* 412 */if (d10 == d6 || d10 == d7)
					/* 413 */break;
				/* 415 */d4 = d10;
			} else {
				/* 417 */double d11 = dYforT(d4, 1);
				/* 418 */if (d11 == 0.0D) {
					/* 419 */flag = false;
					/* 420 */continue;
				}
				/* 422 */double d12 = d4 + (d3 - d5) / d11;
				/* 423 */if (d12 == d4 || d12 <= d6 || d12 >= d7) {
					/* 424 */flag = false;
					/* 425 */continue;
				}
				/* 427 */d4 = d12;
			}
			/* 429 */d5 = YforT(d4);
			/* 430 */if (d5 < d3) {
				/* 431 */d6 = d4;
				/* 431 */continue;
			}
			/* 432 */if (d5 <= d3)
				/* 433 */break;
			/* 433 */d7 = d4;
		} while (true);
		/* 438 */@SuppressWarnings("unused")
		boolean flag1 = false;
		/* 462 */return d4 <= 1.0D ? d4 : -1D;
	}

	// Decompiling method: XforY  Signature: (D)D
	// Max stack: 4, #locals: 3, #params: 3
	// Code length: 38 bytes, Code offset: 5845
	// Line Number Table found: 5 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 38 Range 0 37 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 38 Range 0 37 Init 0
	// RetValue   3 added: Name <returnValue> Type D At 0 38 Range 0 37 Init 0 fixed
	@Override
	public double XforY(double d) {
		/* 466 */if (d <= y0)
			/* 467 */return x0;
		/* 469 */if (d >= y1)
			/* 470 */return x1;
		/* 472 */else
			/* 472 */return XforT(TforY(d));
	}

	// Decompiling method: XforT  Signature: (D)D
	// Max stack: 4, #locals: 3, #params: 3
	// Code length: 26 bytes, Code offset: 5937
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 26 Range 0 25 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 26 Range 0 25 Init 0
	// RetValue   3 added: Name <returnValue> Type D At 0 26 Range 0 25 Init 0 fixed
	@Override
	public double XforT(double d) {
		/* 476 */return ((xcoeff3 * d + xcoeff2) * d + xcoeff1) * d + xcoeff0;
	}

	// Decompiling method: YforT  Signature: (D)D
	// Max stack: 4, #locals: 3, #params: 3
	// Code length: 26 bytes, Code offset: 6001
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 26 Range 0 25 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 26 Range 0 25 Init 0
	// RetValue   3 added: Name <returnValue> Type D At 0 26 Range 0 25 Init 0 fixed
	@Override
	public double YforT(double d) {
		/* 480 */return ((ycoeff3 * d + ycoeff2) * d + ycoeff1) * d + ycoeff0;
	}

	// Decompiling method: dXforT  Signature: (DI)D
	// Max stack: 6, #locals: 4, #params: 4
	// Code length: 116 bytes, Code offset: 6065
	// Line Number Table found: 6 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 116 Range 0 115 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 116 Range 0 115 Init 0
	// Parameter  3 added: Name i Type I At 0 116 Range 0 115 Init 0
	// RetValue   4 added: Name <returnValue> Type D At 0 116 Range 0 115 Init 0 fixed
	@Override
	public double dXforT(double d, int i) {
		/* 484 */switch (i) {
		/* 486 */case 0: // '\0'
			/* 486 */
			return ((xcoeff3 * d + xcoeff2) * d + xcoeff1) * d + xcoeff0;

			/* 488 */case 1: // '\001'
			/* 488 */
			return (3D * xcoeff3 * d + 2D * xcoeff2) * d + xcoeff1;

			/* 490 */case 2: // '\002'
			/* 490 */
			return 6D * xcoeff3 * d + 2D * xcoeff2;

			/* 492 */case 3: // '\003'
			/* 492 */
			return 6D * xcoeff3;
		}
		/* 494 */return 0.0D;
	}

	// Decompiling method: dYforT  Signature: (DI)D
	// Max stack: 6, #locals: 4, #params: 4
	// Code length: 116 bytes, Code offset: 6239
	// Line Number Table found: 6 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 116 Range 0 115 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 116 Range 0 115 Init 0
	// Parameter  3 added: Name i Type I At 0 116 Range 0 115 Init 0
	// RetValue   4 added: Name <returnValue> Type D At 0 116 Range 0 115 Init 0 fixed
	@Override
	public double dYforT(double d, int i) {
		/* 499 */switch (i) {
		/* 501 */case 0: // '\0'
			/* 501 */
			return ((ycoeff3 * d + ycoeff2) * d + ycoeff1) * d + ycoeff0;

			/* 503 */case 1: // '\001'
			/* 503 */
			return (3D * ycoeff3 * d + 2D * ycoeff2) * d + ycoeff1;

			/* 505 */case 2: // '\002'
			/* 505 */
			return 6D * ycoeff3 * d + 2D * ycoeff2;

			/* 507 */case 3: // '\003'
			/* 507 */
			return 6D * ycoeff3;
		}
		/* 509 */return 0.0D;
	}

	// Decompiling method: nextVertical  Signature: (DD)D
	// Max stack: 7, #locals: 8, #params: 5
	// Code length: 87 bytes, Code offset: 6413
	// Line Number Table found: 7 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 87 Range 0 86 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 87 Range 0 86 Init 0
	// Parameter  3 added: Name d1 Type D At 0 87 Range 0 86 Init 0
	// RetValue   8 added: Name <returnValue> Type D At 0 87 Range 0 86 Init 0 fixed
	// LocalVar   5 added: Name ad Type [D At 32 43 Range 32 74 Init 32
	// LocalVar   6 added: Name i Type I At 41 9 Range 41 49 Init 41
	// LocalVar   7 added: Name flag Type Z At 44 38 Range 44 81 Init 44
	// LocalVar   7 chged: Name j Oname flag Type I At 46 2 Range 44 81 Init 44
	@Override
	public double nextVertical(double d, double d1) {
		/* 514 */double ad[] = {
		/* 514 */xcoeff1, 2D * xcoeff2, 3D * xcoeff3 };
		/* 515 */int i = QuadCurve2D.solveQuadratic(ad, ad);
		/* 516 */for (int j = 0; j < i; j++)
			/* 517 */if (ad[j] > d && ad[j] < d1)
				/* 518 */d1 = ad[j];

		/* 521 */return d1;
	}

	// Decompiling method: enlarge  Signature: (Ljava/awt/geom/Rectangle2D;)V
	// Max stack: 7, #locals: 7, #params: 2
	// Code length: 115 bytes, Code offset: 6562
	// Line Number Table found: 10 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 115 Range 0 114 Init 0 fixed
	// Parameter  1 added: Name rectangle2d Type Ljava/awt/geom/Rectangle2D; At 0 115 Range 0 114 Init 0
	// RetValue   7 added: Name <returnValue> Type V At 0 115 Range 0 114 Init 0 fixed
	// LocalVar   2 added: Name ad Type [D At 44 17 Range 44 60 Init 44
	// LocalVar   3 added: Name i Type I At 50 7 Range 50 56 Init 50
	// LocalVar   4 added: Name flag Type Z At 52 47 Range 52 98 Init 52
	// LocalVar   4 chged: Name j Oname flag Type I At 54 2 Range 52 98 Init 52
	// LocalVar   5 added: Name d Type D At 64 26 Range 64 89 Init 64
	@Override
	public void enlarge(Rectangle2D rectangle2d) {
		/* 525 */rectangle2d.add(x0, y0);
		/* 526 */double ad[] = {
		/* 526 */xcoeff1, 2D * xcoeff2, 3D * xcoeff3 };
		/* 527 */int i = QuadCurve2D.solveQuadratic(ad, ad);
		/* 528 */for (int j = 0; j < i; j++) {
			/* 529 */double d = ad[j];
			/* 530 */if (d > 0.0D && d < 1.0D)
				/* 531 */rectangle2d.add(XforT(d), YforT(d));
		}

		/* 534 */rectangle2d.add(x1, y1);
		/* 535 *//* return; */
	}

	// Decompiling method: getSubCurve  Signature: (DDI)Lsun/awt/geom/Curve;
	// Max stack: 19, #locals: 13, #params: 6
	// Code length: 229 bytes, Code offset: 6751
	// Line Number Table found: 24 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 229 Range 0 228 Init 0 fixed
	// Parameter  1 added: Name d Type D At 0 229 Range 0 228 Init 0
	// Parameter  3 added: Name d1 Type D At 0 229 Range 0 228 Init 0
	// Parameter  5 added: Name i Type I At 0 229 Range 0 228 Init 0
	// RetValue  13 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 229 Range 0 228 Init 0 fixed
	// LocalVar   6 added: Name ad Type [D At 29 187 Range 29 215 Init 29
	// LocalVar   7 added: Name d2 Type D At 36 128 Range 36 163 Init 36
	// LocalVar   9 added: Name d3 Type D At 43 123 Range 43 165 Init 43
	// LocalVar  11 added: Name d4 Type D At 121 8 Range 121 128 Init 121
	// LocalVar  11 added: Name flag Type Z At 154 64 Range 154 217 Init 154
	// LocalVar  11 chged: Name byte0 Oname flag Type B At 172 46 Range 154 217 Init 154
	// LocalVar  12 hasn't been used
	@Override
	public Curve getSubCurve(double d, double d1, int i) {
		/* 538 */if (d <= y0 && d1 >= y1)
			/* 539 */return getWithDirection(i);
		/* 541 */double ad[] = new double[14];
		/* 543 */double d2 = TforY(d);
		/* 544 */double d3 = TforY(d1);
		/* 545 */ad[0] = x0;
		/* 546 */ad[1] = y0;
		/* 547 */ad[2] = cx0;
		/* 548 */ad[3] = cy0;
		/* 549 */ad[4] = cx1;
		/* 550 */ad[5] = cy1;
		/* 551 */ad[6] = x1;
		/* 552 */ad[7] = y1;
		/* 553 */if (d2 > d3) {
			/* 568 */double d4 = d2;
			/* 569 */d2 = d3;
			/* 570 */d3 = d4;
		}
		/* 572 */if (d3 < 1.0D)
			/* 573 */split(ad, 0, d3);
		byte byte0;
		/* 576 */if (d2 <= 0.0D) {
			/* 577 */byte0 = 0;
		} else {
			/* 579 */split(ad, 0, d2 / d3);
			/* 580 */byte0 = 6;
		}
		/* 582 */return ((new Order3(ad[byte0 + 0], d, ad[byte0 + 2], ad[byte0 + 3], ad[byte0 + 4], ad[byte0 + 5], ad[byte0 + 6], d1, i)));
	}

	// Decompiling method: getReversedCurve  Signature: ()Lsun/awt/geom/Curve;
	// Max stack: 19, #locals: 1, #params: 1
	// Code length: 45 bytes, Code offset: 7110
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 45 Range 0 44 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 45 Range 0 44 Init 0 fixed
	@Override
	public Curve getReversedCurve() {
		/* 590 */return ((new Order3(x0, y0, cx0, cy0, cx1, cy1, x1, y1, -direction)));
	}

	// Decompiling method: getSegment  Signature: ([D)I
	// Max stack: 4, #locals: 2, #params: 2
	// Code length: 97 bytes, Code offset: 7193
	// Line Number Table found: 14 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 97 Range 0 96 Init 0 fixed
	// Parameter  1 added: Name ad Type [D At 0 97 Range 0 96 Init 0
	// RetValue   2 added: Name <returnValue> Type I At 0 97 Range 0 96 Init 0 fixed
	@Override
	public int getSegment(double ad[]) {
		/* 594 */if (direction == 1) {
			/* 595 */ad[0] = cx0;
			/* 596 */ad[1] = cy0;
			/* 597 */ad[2] = cx1;
			/* 598 */ad[3] = cy1;
			/* 599 */ad[4] = x1;
			/* 600 */ad[5] = y1;
		} else {
			/* 602 */ad[0] = cx1;
			/* 603 */ad[1] = cy1;
			/* 604 */ad[2] = cx0;
			/* 605 */ad[3] = cy0;
			/* 606 */ad[4] = x0;
			/* 607 */ad[5] = y0;
		}
		/* 609 */return 3;
	}

	// Decompiling method: controlPointString  Signature: ()Ljava/lang/String;
	// Max stack: 3, #locals: 1, #params: 1
	// Code length: 81 bytes, Code offset: 7380
	// Line Number Table found: 1 entries
	// Parameter  0 added: Name this Type Lsun/awt/geom/Order3; At 0 81 Range 0 80 Init 0 fixed
	// RetValue   1 added: Name <returnValue> Type Ljava/lang/String; At 0 81 Range 0 80 Init 0 fixed
	@Override
	public String controlPointString() {
		/* 613 */return (new StringBuilder()).append("(").append(round(getCX0())).append(", ").append(round(getCY0())).append("), ").append("(").append(round(getCX1())).append(", ").append(round(getCY1())).append("), ").toString();
	}
}
