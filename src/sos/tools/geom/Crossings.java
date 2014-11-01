// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 3/29/2011 1:22:10 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) definits fieldsfirst noinners ansi nonlb space lnc safe debugmode 
// Source File Name:   Crossings.java
// Class Version:      49.0

package sos.tools.geom;

import java.awt.geom.PathIterator;
import java.util.Enumeration;
import java.util.Vector;

// Referenced classes of package sun.awt.geom:
//			Curve

// flag ACC_SUPER is set
public abstract class Crossings {
	// Constants: 177
	// Interfaces: 0
	// Fields: 8
	// Methods: 15
	// Class Attributes: 2

	public static final boolean debug = false;
	int limit = 0;
	double yranges[] = null;
	double xlo = 0;
	double ylo = 0;
	double xhi = 0;
	double yhi = 0;
	@SuppressWarnings("rawtypes")
	private Vector tmp = null;

	// Decompiling method: <init> Signature: (DDDD)V
	// Max stack: 3, #locals: 9, #params: 9
	// Code length: 51 bytes, Code offset: 1854
	// Line Number Table found: 9 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 51 Range
	// 0 50 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 51 Range 0 50 Init 0
	// Parameter 3 added: Name d1 Type D At 0 51 Range 0 50 Init 0
	// Parameter 5 added: Name d2 Type D At 0 51 Range 0 50 Init 0
	// Parameter 7 added: Name d3 Type D At 0 51 Range 0 50 Init 0
	// RetValue 9 added: Name <returnValue> Type V At 0 51 Range 0 50 Init 0
	// fixed
	@SuppressWarnings("rawtypes")
	public Crossings(double d, double d1, double d2, double d3) {
		/* 22 *//* super(); */
		/* 17 */limit = 0;
		/* 18 */yranges = new double[10];
		/* 222 */tmp = new Vector();
		/* 23 */xlo = d;
		/* 24 */ylo = d1;
		/* 25 */xhi = d2;
		/* 26 */yhi = d3;
		/* 27 *//* return; */
	}

	// Decompiling method: getXLo Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 1975
	// Line Number Table found: 1 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 5 Range 0
	// 4 Init 0 fixed
	// RetValue 1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	public final double getXLo() {
		/* 30 */return xlo;
	}

	// Decompiling method: getYLo Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 2018
	// Line Number Table found: 1 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 5 Range 0
	// 4 Init 0 fixed
	// RetValue 1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	public final double getYLo() {
		/* 34 */return ylo;
	}

	// Decompiling method: getXHi Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 2061
	// Line Number Table found: 1 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 5 Range 0
	// 4 Init 0 fixed
	// RetValue 1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	public final double getXHi() {
		/* 38 */return xhi;
	}

	// Decompiling method: getYHi Signature: ()D
	// Max stack: 2, #locals: 1, #params: 1
	// Code length: 5 bytes, Code offset: 2104
	// Line Number Table found: 1 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 5 Range 0
	// 4 Init 0 fixed
	// RetValue 1 added: Name <returnValue> Type D At 0 5 Range 0 4 Init 0 fixed
	public final double getYHi() {
		/* 42 */return yhi;
	}

	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 0 Range 0
	// -1 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 0 Range 0 -1 Init 0
	// Parameter 3 added: Name d1 Type D At 0 0 Range 0 -1 Init 0
	// Parameter 5 added: Name i Type I At 0 0 Range 0 -1 Init 0
	// RetValue 6 added: Name <returnValue> Type V At 0 0 Range 0 -1 Init 0
	// fixed
	public abstract void record(double d, double d1, int i);

	// Decompiling method: print Signature: ()V
	// Max stack: 5, #locals: 2, #params: 1
	// Code length: 129 bytes, Code offset: 2155
	// Line Number Table found: 7 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 129 Range
	// 0 128 Init 0 fixed
	// RetValue 2 added: Name <returnValue> Type V At 0 129 Range 0 128 Init 0
	// fixed
	// LocalVar 1 added: Name flag Type Z At 54 63 Range 54 116 Init 54
	// LocalVar 1 chged: Name i Oname flag Type I At 55 1 Range 54 116 Init 54
	public void print() {
		/* 48 */System.out.println("Crossings [");
		/* 49 */System.out.println((new StringBuilder()).append("  bounds = [").append(ylo).append(", ").append(yhi).append("]").toString());
		/* 50 */for (int i = 0; i < limit; i += 2)
			/* 51 */System.out.println((new StringBuilder()).append("  [").append(yranges[i]).append(", ").append(yranges[i + 1]).append("]").toString());

		/* 53 */System.out.println("]");
		/* 54 *//* return; */
	}

	// Decompiling method: isEmpty Signature: ()Z
	// Max stack: 1, #locals: 1, #params: 1
	// Code length: 13 bytes, Code offset: 2346
	// Line Number Table found: 1 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 13 Range
	// 0 12 Init 0 fixed
	// RetValue 1 added: Name <returnValue> Type Z At 0 13 Range 0 12 Init 0
	// fixed
	public final boolean isEmpty() {
		/* 57 */return limit == 0;
	}

	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 0 Range 0
	// -1 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 0 Range 0 -1 Init 0
	// Parameter 3 added: Name d1 Type D At 0 0 Range 0 -1 Init 0
	// RetValue 5 added: Name <returnValue> Type Z At 0 0 Range 0 -1 Init 0
	// fixed
	public abstract boolean covers(double d, double d1);

	// Decompiling method: findCrossings Signature:
	// (Ljava/util/Vector;DDDD)Lsun/awt/geom/Crossings;
	// Max stack: 10, #locals: 12, #params: 9
	// Code length: 61 bytes, Code offset: 2405
	// Line Number Table found: 8 entries
	// Parameter 0 added: Name vector Type Ljava/util/Vector; At 0 61 Range 0 60
	// Init 0
	// Parameter 1 added: Name d Type D At 0 61 Range 0 60 Init 0
	// Parameter 3 added: Name d1 Type D At 0 61 Range 0 60 Init 0
	// Parameter 5 added: Name d2 Type D At 0 61 Range 0 60 Init 0
	// Parameter 7 added: Name d3 Type D At 0 61 Range 0 60 Init 0
	// RetValue 12 added: Name <returnValue> Type Lsun/awt/geom/Crossings; At 0
	// 61 Range 0 60 Init 0 fixed
	// LocalVar 9 added: Name crossings$evenodd Type
	// Lsun/awt/geom/Crossings$EvenOdd; At 13 47 Range 13 59 Init 13
	// LocalVar 10 added: Name enumeration Type Ljava/util/Enumeration; At 19 14
	// Range 19 32 Init 19
	// LocalVar 11 added: Name curve Type Lsun/awt/geom/Curve; At 41 4 Range 41
	// 44 Init 41
	public static Crossings findCrossings(@SuppressWarnings("rawtypes") Vector vector, double d, double d1, double d2, double d3) {
		/* 66 */EvenOdd crossings$evenodd = new EvenOdd(d, d1, d2, d3);
		/* 67 */for (@SuppressWarnings("rawtypes")
		Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();) {
			/* 69 */Curve curve = (Curve) enumeration.nextElement();
			/* 70 */if (curve.accumulateCrossings(((crossings$evenodd))))
				/* 71 */return null;
		}

		/* 77 */return ((crossings$evenodd));
	}

	// Decompiling method: findCrossings Signature:
	// (Ljava/awt/geom/PathIterator;DDDD)Lsun/awt/geom/Crossings;
	// Max stack: 10, #locals: 24, #params: 9
	// Code length: 358 bytes, Code offset: 2532
	// Line Number Table found: 47 entries
	// Parameter 0 added: Name pathiterator Type Ljava/awt/geom/PathIterator; At
	// 0 358 Range 0 357 Init 0
	// Parameter 1 added: Name d Type D At 0 358 Range 0 357 Init 0
	// Parameter 3 added: Name d1 Type D At 0 358 Range 0 357 Init 0
	// Parameter 5 added: Name d2 Type D At 0 358 Range 0 357 Init 0
	// Parameter 7 added: Name d3 Type D At 0 358 Range 0 357 Init 0
	// RetValue 24 added: Name <returnValue> Type Lsun/awt/geom/Crossings; At 0
	// 358 Range 0 357 Init 0 fixed
	// LocalVar 9 added: Name crossings$evenodd Type
	// Lsun/awt/geom/Crossings$EvenOdd; At 25 332 Range 25 356 Init 25
	// LocalVar 9 chged: Name obj Oname crossings$evenodd Type
	// Ljava/lang/Object; At 43 314 Range 25 356 Init 25
	// LocalVar 10 added: Name ad Type [D At 49 218 Range 49 266 Init 49
	// LocalVar 11 added: Name d4 Type D At 52 293 Range 52 344 Init 52
	// LocalVar 13 added: Name d5 Type D At 55 292 Range 55 346 Init 55
	// LocalVar 15 added: Name d6 Type D At 58 283 Range 58 340 Init 58
	// LocalVar 17 added: Name d7 Type D At 61 282 Range 61 342 Init 61
	// LocalVar 23 added: Name i Type I At 80 4 Range 80 83 Init 80
	// LocalVar 19 added: Name d8 Type D At 171 28 Range 171 198 Init 171
	// LocalVar 21 added: Name d9 Type D At 177 26 Range 177 202 Init 177
	// LocalVar 19 added: Name d10 Type D At 212 26 Range 212 237 Init 212
	// LocalVar 21 added: Name d11 Type D At 218 24 Range 218 241 Init 218
	// LocalVar 19 added: Name d12 Type D At 251 26 Range 251 276 Init 251
	// LocalVar 21 added: Name d13 Type D At 257 24 Range 257 280 Init 257
	public static Crossings findCrossings(PathIterator pathiterator, double d, double d1, double d2, double d3) {
		/* 85 */@SuppressWarnings("unused")
		PathIterator _tmp = pathiterator;
		Object obj;
		/* 85 */if (pathiterator.getWindingRule() == 0)
			/* 86 */obj = ((new EvenOdd(d, d1, d2, d3)));
		/* 88 */else
			/* 88 */obj = ((new NonZero(d, d1, d2, d3)));
		/* 104 */double ad[] = new double[23];
		/* 105 */double d4 = 0.0D;
		/* 106 */double d5 = 0.0D;
		/* 107 */double d6 = 0.0D;
		/* 108 */double d7 = 0.0D;
		/* 110 */for (; !pathiterator.isDone(); pathiterator.next()) {
			/* 111 */int i = pathiterator.currentSegment(ad);
			/* 112 */switch (i) {
			/* 85 */default:
				break;

			/* 114 */case 0: // '\0'
				/* 114 */
				if (d5 != d7 && ((Crossings) (obj)).accumulateLine(d6, d7, d4, d5))
					/* 117 */return null;
				/* 119 */
				d4 = d6 = ad[0];
				/* 120 */
				d5 = d7 = ad[1];
				/* 121 */break;

			/* 123 */case 1: // '\001'
				/* 123 */
				double d8 = ad[0];
				/* 124 */
				double d11 = ad[1];
				/* 125 */
				if (((Crossings) (obj)).accumulateLine(d6, d7, d8, d11))
					/* 126 */return null;
				/* 128 */
				d6 = d8;
				/* 129 */
				d7 = d11;
				/* 130 */break;

			/* 132 */case 2: // '\002'
				/* 132 */
				double d9 = ad[2];
				/* 133 */
				double d12 = ad[3];
				/* 134 */
				if (((Crossings) (obj)).accumulateQuad(d6, d7, ad))
					/* 135 */return null;
				/* 137 */
				d6 = d9;
				/* 138 */
				d7 = d12;
				/* 139 */break;

			/* 141 */case 3: // '\003'
				/* 141 */
				double d10 = ad[4];
				/* 142 */
				double d13 = ad[5];
				/* 143 */
				if (((Crossings) (obj)).accumulateCubic(d6, d7, ad))
					/* 144 */return null;
				/* 146 */
				d6 = d10;
				/* 147 */
				d7 = d13;
				/* 148 */break;

			/* 150 */case 4: // '\004'
				/* 150 */
				if (d5 != d7 && ((Crossings) (obj)).accumulateLine(d6, d7, d4, d5))
					/* 153 */return null;
				/* 155 */
				d6 = d4;
				/* 156 */
				d7 = d5;
				break;
			}
		}

		/* 161 */if (d5 != d7 && ((Crossings) (obj)).accumulateLine(d6, d7, d4, d5))
			/* 163 */return null;
		/* 169 */else
			/* 169 */return ((Crossings) (obj));
	}

	// Decompiling method: accumulateLine Signature: (DDDD)Z
	// Max stack: 10, #locals: 9, #params: 9
	// Code length: 31 bytes, Code offset: 3112
	// Line Number Table found: 3 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 31 Range
	// 0 30 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 31 Range 0 30 Init 0
	// Parameter 3 added: Name d1 Type D At 0 31 Range 0 30 Init 0
	// Parameter 5 added: Name d2 Type D At 0 31 Range 0 30 Init 0
	// Parameter 7 added: Name d3 Type D At 0 31 Range 0 30 Init 0
	// RetValue 9 added: Name <returnValue> Type Z At 0 31 Range 0 30 Init 0
	// fixed
	public boolean accumulateLine(double d, double d1, double d2, double d3) {
		/* 175 */if (d1 <= d3)
			/* 176 */return accumulateLine(d, d1, d2, d3, 1);
		/* 178 */else
			/* 178 */return accumulateLine(d2, d3, d, d1, -1);
	}

	// Decompiling method: accumulateLine Signature: (DDDDI)Z
	// Max stack: 6, #locals: 22, #params: 10
	// Code length: 225 bytes, Code offset: 3189
	// Line Number Table found: 24 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 225 Range
	// 0 224 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 225 Range 0 224 Init 0
	// Parameter 3 added: Name d1 Type D At 0 225 Range 0 224 Init 0
	// Parameter 5 added: Name d2 Type D At 0 225 Range 0 224 Init 0
	// Parameter 7 added: Name d3 Type D At 0 225 Range 0 224 Init 0
	// Parameter 9 added: Name i Type I At 0 225 Range 0 224 Init 0
	// RetValue 22 added: Name <returnValue> Type Z At 0 225 Range 0 224 Init 0
	// fixed
	// LocalVar 18 added: Name d4 Type D At 78 67 Range 78 144 Init 78
	// LocalVar 20 added: Name d5 Type D At 84 64 Range 84 147 Init 84
	// LocalVar 10 added: Name d6 Type D At 109 84 Range 109 192 Init 109
	// LocalVar 12 added: Name d7 Type D At 115 101 Range 115 215 Init 115
	// LocalVar 14 added: Name d8 Type D At 150 53 Range 150 202 Init 150
	// LocalVar 16 added: Name d9 Type D At 156 62 Range 156 217 Init 156
	public boolean accumulateLine(double d, double d1, double d2, double d3, int i) {
		/* 186 */if (yhi <= d1 || ylo >= d3)
			/* 187 */return false;
		/* 189 */if (d >= xhi && d2 >= xhi)
			/* 190 */return false;
		/* 192 */if (d1 == d3)
			/* 193 */return d >= xlo || d2 >= xlo;
		/* 196 */double d8 = d2 - d;
		/* 197 */double d9 = d3 - d1;
		double d4;
		double d5;
		/* 198 */if (d1 < ylo) {
			/* 199 */d4 = d + ((ylo - d1) * d8) / d9;
			/* 200 */d5 = ylo;
		} else {
			/* 202 */d4 = d;
			/* 203 */d5 = d1;
		}
		double d6;
		double d7;
		/* 205 */if (yhi < d3) {
			/* 206 */d6 = d + ((yhi - d1) * d8) / d9;
			/* 207 */d7 = yhi;
		} else {
			/* 209 */d6 = d2;
			/* 210 */d7 = d3;
		}
		/* 212 */if (d4 >= xhi && d6 >= xhi)
			/* 213 */return false;
		/* 215 */if (d4 > xlo || d6 > xlo) {
			/* 216 */return true;
		} else {
			/* 218 */record(d5, d7, i);
			/* 219 */return false;
		}
	}

	// Decompiling method: accumulateQuad Signature: (DD[D)Z
	// Max stack: 7, #locals: 8, #params: 6
	// Code length: 274 bytes, Code offset: 3544
	// Line Number Table found: 21 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 274 Range
	// 0 273 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 274 Range 0 273 Init 0
	// Parameter 3 added: Name d1 Type D At 0 274 Range 0 273 Init 0
	// Parameter 5 added: Name ad Type [D At 0 274 Range 0 273 Init 0
	// RetValue 8 added: Name <returnValue> Type Z At 0 274 Range 0 273 Init 0
	// fixed
	// LocalVar 6 added: Name enumeration Type Ljava/util/Enumeration; At 227 14
	// Range 227 240 Init 227
	// LocalVar 7 added: Name curve Type Lsun/awt/geom/Curve; At 249 4 Range 249
	// 252 Init 249
	public boolean accumulateQuad(double d, double d1, double ad[]) {
		/* 225 */if (d1 < ylo && ad[1] < ylo && ad[3] < ylo)
			/* 226 */return false;
		/* 228 */if (d1 > yhi && ad[1] > yhi && ad[3] > yhi)
			/* 229 */return false;
		/* 231 */if (d > xhi && ad[0] > xhi && ad[2] > xhi)
			/* 232 */return false;
		/* 234 */if (d < xlo && ad[0] < xlo && ad[2] < xlo) {
			/* 235 */if (d1 < ad[3])
				/* 236 */record(Math.max(d1, ylo), Math.min(ad[3], yhi), 1);
			/* 237 */else
			/* 237 */if (d1 > ad[3])
				/* 238 */record(Math.max(ad[3], ylo), Math.min(d1, yhi), -1);
			/* 240 */return false;
		}
		/* 242 */Curve.insertQuad(tmp, d, d1, ad);
		/* 243 */for (@SuppressWarnings("rawtypes")
		Enumeration enumeration = tmp.elements(); enumeration.hasMoreElements();) {
			/* 245 */Curve curve = (Curve) enumeration.nextElement();
			/* 246 */if (curve.accumulateCrossings(this))
				/* 247 */return true;
		}

		/* 250 */tmp.clear();
		/* 251 */return false;
	}

	// Decompiling method: accumulateCubic Signature: (DD[D)Z
	// Max stack: 7, #locals: 8, #params: 6
	// Code length: 313 bytes, Code offset: 3936
	// Line Number Table found: 20 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings; At 0 313 Range
	// 0 312 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 313 Range 0 312 Init 0
	// Parameter 3 added: Name d1 Type D At 0 313 Range 0 312 Init 0
	// Parameter 5 added: Name ad Type [D At 0 313 Range 0 312 Init 0
	// RetValue 8 added: Name <returnValue> Type Z At 0 313 Range 0 312 Init 0
	// fixed
	// LocalVar 6 added: Name enumeration Type Ljava/util/Enumeration; At 266 14
	// Range 266 279 Init 266
	// LocalVar 7 added: Name curve Type Lsun/awt/geom/Curve; At 288 4 Range 288
	// 291 Init 288
	public boolean accumulateCubic(double d, double d1, double ad[]) {
		/* 255 */if (d1 < ylo && ad[1] < ylo && ad[3] < ylo && ad[5] < ylo)
			/* 258 */return false;
		/* 260 */if (d1 > yhi && ad[1] > yhi && ad[3] > yhi && ad[5] > yhi)
			/* 263 */return false;
		/* 265 */if (d > xhi && ad[0] > xhi && ad[2] > xhi && ad[4] > xhi)
			/* 268 */return false;
		/* 270 */if (d < xlo && ad[0] < xlo && ad[2] < xlo && ad[4] < xlo) {
			/* 273 */if (d1 <= ad[5])
				/* 274 */record(Math.max(d1, ylo), Math.min(ad[5], yhi), 1);
			/* 276 */else
				/* 276 */record(Math.max(ad[5], ylo), Math.min(d1, yhi), -1);
			/* 278 */return false;
		}
		/* 280 */Curve.insertCubic(tmp, d, d1, ad);
		/* 281 */for (@SuppressWarnings("rawtypes")
		Enumeration enumeration = tmp.elements(); enumeration.hasMoreElements();) {
			/* 283 */Curve curve = (Curve) enumeration.nextElement();
			/* 284 */if (curve.accumulateCrossings(this))
				/* 285 */return true;
		}

		/* 288 */tmp.clear();
		/* 289 */return false;
	}
}
