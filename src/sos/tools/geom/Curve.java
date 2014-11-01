
package sos.tools.geom;

import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

// Referenced classes of package sun.awt.geom:
//			Crossings, Order0, Order1, Order2, 
//			Order3

// flag ACC_SUPER is set
public abstract class Curve {
	// Constants:          358
	// Interfaces:         0
	// Fields:             5
	// Methods:            52
	// Class Attributes:   1


        	public static final int INCREASING = 1;
        	public static final int DECREASING = -1;
        	protected int direction = 0;
        	public static final int RECT_INTERSECTS = 0x80000000;
        	public static final double TMIN = 0.001D;

        	// Decompiling method: insertMove  Signature: (Ljava/util/Vector;DD)V
        	// Max stack: 7, #locals: 5, #params: 5
        	// Code length: 15 bytes, Code offset: 3607
        	// Line Number Table found: 2 entries
        	// Parameter  0 added: Name vector Type Ljava/util/Vector; At 0 15 Range 0 14 Init 0
        	// Parameter  1 added: Name d Type D At 0 15 Range 0 14 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 15 Range 0 14 Init 0
        	// RetValue   5 added: Name <returnValue> Type V At 0 15 Range 0 14 Init 0 fixed
        	public static void insertMove(Vector<Order0> vector, double d, double d1) {
/*  24*/		vector.add(((new Order0(d, d1))));
/*  25*/		/* return; */
        	}

        	// Decompiling method: insertLine  Signature: (Ljava/util/Vector;DDDD)V
        	// Max stack: 12, #locals: 9, #params: 9
        	// Code length: 56 bytes, Code offset: 3664
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name vector Type Ljava/util/Vector; At 0 56 Range 0 55 Init 0
        	// Parameter  1 added: Name d Type D At 0 56 Range 0 55 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 56 Range 0 55 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 56 Range 0 55 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 56 Range 0 55 Init 0
        	// RetValue   9 added: Name <returnValue> Type V At 0 56 Range 0 55 Init 0 fixed
        	public static void insertLine(Vector<Order1> vector, double d, double d1, double d2, double d3) {
/*  31*/		if (d1 < d3)
/*  32*/			vector.add(((new Order1(d, d1, d2, d3, 1))));
/*  35*/		else
/*  35*/		if (d1 > d3)
/*  36*/			vector.add(((new Order1(d2, d3, d, d1, -1))));
/*  42*/		/* return; */
        	}

        	// Decompiling method: insertQuad  Signature: (Ljava/util/Vector;DD[D)V
        	// Max stack: 15, #locals: 8, #params: 6
        	// Code length: 80 bytes, Code offset: 3774
        	// Line Number Table found: 7 entries
        	// Parameter  0 added: Name vector Type Ljava/util/Vector; At 0 80 Range 0 79 Init 0
        	// Parameter  1 added: Name d Type D At 0 80 Range 0 79 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 80 Range 0 79 Init 0
        	// Parameter  5 added: Name ad Type [D At 0 80 Range 0 79 Init 0
        	// RetValue   8 added: Name <returnValue> Type V At 0 80 Range 0 79 Init 0 fixed
        	// LocalVar   6 added: Name d2 Type D At 4 71 Range 4 74 Init 4
        	public static void insertQuad(@SuppressWarnings("rawtypes") Vector vector, double d, double d1, double ad[]) {
/*  48*/		double d2 = ad[3];
/*  49*/		if (d1 > d2) {
/*  50*/			Order2.insert(vector, ad, ad[2], d2, ad[0], ad[1], d, d1, -1);
        		} else {
/*  55*/			if (d1 == d2 && d1 == ad[1])
/*  57*/				return;
/*  59*/			Order2.insert(vector, ad, d, d1, ad[0], ad[1], ad[2], d2, 1);
        		}
/*  65*/		/* return; */
        	}

        	// Decompiling method: insertCubic  Signature: (Ljava/util/Vector;DD[D)V
        	// Max stack: 19, #locals: 8, #params: 6
        	// Code length: 105 bytes, Code offset: 3916
        	// Line Number Table found: 7 entries
        	// Parameter  0 added: Name vector Type Ljava/util/Vector; At 0 105 Range 0 104 Init 0
        	// Parameter  1 added: Name d Type D At 0 105 Range 0 104 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 105 Range 0 104 Init 0
        	// Parameter  5 added: Name ad Type [D At 0 105 Range 0 104 Init 0
        	// RetValue   8 added: Name <returnValue> Type V At 0 105 Range 0 104 Init 0 fixed
        	// LocalVar   6 added: Name d2 Type D At 4 96 Range 4 99 Init 4
        	public static void insertCubic(@SuppressWarnings("rawtypes") Vector vector, double d, double d1, double ad[]) {
/*  71*/		double d2 = ad[5];
/*  72*/		if (d1 > d2) {
/*  73*/			Order3.insert(vector, ad, ad[4], d2, ad[2], ad[3], ad[0], ad[1], d, d1, -1);
        		} else {
/*  79*/			if (d1 == d2 && d1 == ad[1] && d1 == ad[3])
/*  81*/				return;
/*  83*/			Order3.insert(vector, ad, d, d1, ad[0], ad[1], ad[2], ad[3], ad[4], d2, 1);
        		}
/*  90*/		/* return; */
        	}

        	// Decompiling method: pointCrossingsForPath  Signature: (Ljava/awt/geom/PathIterator;DD)I
        	// Max stack: 22, #locals: 19, #params: 5
        	// Code length: 388 bytes, Code offset: 4083
        	// Line Number Table found: 44 entries
        	// Parameter  0 added: Name pathiterator Type Ljava/awt/geom/PathIterator; At 0 388 Range 0 387 Init 0
        	// Parameter  1 added: Name d Type D At 0 388 Range 0 387 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 388 Range 0 387 Init 0
        	// RetValue  19 added: Name <returnValue> Type I At 0 388 Range 0 387 Init 0 fixed
        	// LocalVar   5 added: Name ad Type [D At 15 277 Range 15 291 Init 15
        	// LocalVar   6 added: Name d2 Type D At 48 329 Range 48 376 Init 48
        	// LocalVar   8 added: Name d3 Type D At 54 325 Range 54 378 Init 54
        	// LocalVar  10 added: Name d4 Type D At 58 315 Range 58 372 Init 58
        	// LocalVar  12 added: Name d5 Type D At 62 313 Range 62 374 Init 62
        	// LocalVar  18 added: Name flag Type Z At 65 322 Range 65 386 Init 65
        	// LocalVar  18 chged: Name i Oname flag Type I At 128 2 Range 65 386 Init 65
        	// LocalVar  14 added: Name d6 Type D At 171 28 Range 171 198 Init 171
        	// LocalVar  16 added: Name d7 Type D At 177 26 Range 177 202 Init 177
        	// LocalVar  14 added: Name d8 Type D At 212 37 Range 212 248 Init 212
        	// LocalVar  16 added: Name d9 Type D At 218 35 Range 218 252 Init 218
        	// LocalVar  14 added: Name d10 Type D At 262 45 Range 262 306 Init 262
        	// LocalVar  16 added: Name d11 Type D At 268 43 Range 268 310 Init 268
        	public static int pointCrossingsForPath(PathIterator pathiterator, double d, double d1) {
/* 109*/		if (pathiterator.isDone())
/* 110*/			return 0;
/* 112*/		double ad[] = new double[6];
/* 113*/		if (pathiterator.currentSegment(ad) != 0)
/* 114*/			throw new IllegalPathStateException("missing initial moveto in path definition");
/* 117*/		pathiterator.next();
/* 118*/		double d2 = ad[0];
/* 119*/		double d3 = ad[1];
/* 120*/		double d4 = d2;
/* 121*/		double d5 = d3;
/* 123*/		int i = 0;
/* 124*/		for (; !pathiterator.isDone(); pathiterator.next())
/* 125*/			switch (pathiterator.currentSegment(ad)) {
/* 109*/			default:
        				break;

/* 127*/			case 0: // '\0'
/* 127*/				if (d5 != d3)
/* 128*/					i += pointCrossingsForLine(d, d1, d4, d5, d2, d3);
/* 132*/				d2 = d4 = ad[0];
/* 133*/				d3 = d5 = ad[1];
/* 134*/				break;

/* 136*/			case 1: // '\001'
/* 136*/				double d6 = ad[0];
/* 137*/				double d9 = ad[1];
/* 138*/				i += pointCrossingsForLine(d, d1, d4, d5, d6, d9);
/* 141*/				d4 = d6;
/* 142*/				d5 = d9;
/* 143*/				break;

/* 145*/			case 2: // '\002'
/* 145*/				double d7 = ad[2];
/* 146*/				double d10 = ad[3];
/* 147*/				i += pointCrossingsForQuad(d, d1, d4, d5, ad[0], ad[1], d7, d10, 0);
/* 151*/				d4 = d7;
/* 152*/				d5 = d10;
/* 153*/				break;

/* 155*/			case 3: // '\003'
/* 155*/				double d8 = ad[4];
/* 156*/				double d11 = ad[5];
/* 157*/				i += pointCrossingsForCubic(d, d1, d4, d5, ad[0], ad[1], ad[2], ad[3], d8, d11, 0);
/* 162*/				d4 = d8;
/* 163*/				d5 = d11;
/* 164*/				break;

/* 166*/			case 4: // '\004'
/* 166*/				if (d5 != d3)
/* 167*/					i += pointCrossingsForLine(d, d1, d4, d5, d2, d3);
/* 171*/				d4 = d2;
/* 172*/				d5 = d3;
        				break;
        			}

/* 177*/		if (d5 != d3)
/* 178*/			i += pointCrossingsForLine(d, d1, d4, d5, d2, d3);
/* 182*/		return i;
        	}

        	// Decompiling method: pointCrossingsForLine  Signature: (DDDDDD)I
        	// Max stack: 8, #locals: 14, #params: 12
        	// Code length: 120 bytes, Code offset: 4681
        	// Line Number Table found: 7 entries
        	// Parameter  0 added: Name d Type D At 0 120 Range 0 119 Init 0
        	// Parameter  2 added: Name d1 Type D At 0 120 Range 0 119 Init 0
        	// Parameter  4 added: Name d2 Type D At 0 120 Range 0 119 Init 0
        	// Parameter  6 added: Name d3 Type D At 0 120 Range 0 119 Init 0
        	// Parameter  8 added: Name d4 Type D At 0 120 Range 0 119 Init 0
        	// Parameter 10 added: Name d5 Type D At 0 120 Range 0 119 Init 0
        	// RetValue  14 added: Name <returnValue> Type I At 0 120 Range 0 119 Init 0 fixed
        	// LocalVar  12 added: Name d6 Type D At 95 5 Range 95 99 Init 95
        	public static int pointCrossingsForLine(double d, double d1, double d2, double d3, 
        			double d4, double d5) {
/* 196*/		if (d1 < d3 && d1 < d5)
/* 196*/			return 0;
/* 197*/		if (d1 >= d3 && d1 >= d5)
/* 197*/			return 0;
/* 199*/		if (d >= d2 && d >= d4)
/* 199*/			return 0;
/* 200*/		if (d < d2 && d < d4)
/* 200*/			return d3 >= d5 ? -1 : 1;
/* 201*/		double d6 = d2 + ((d1 - d3) * (d4 - d2)) / (d5 - d3);
/* 202*/		if (d >= d6)
/* 202*/			return 0;
/* 203*/		else
/* 203*/			return d3 >= d5 ? -1 : 1;
        	}

        	// Decompiling method: pointCrossingsForQuad  Signature: (DDDDDDDDI)I
        	// Max stack: 19, #locals: 25, #params: 17
        	// Code length: 266 bytes, Code offset: 4863
        	// Line Number Table found: 18 entries
        	// Parameter  0 added: Name d Type D At 0 266 Range 0 265 Init 0
        	// Parameter  2 added: Name d1 Type D At 0 266 Range 0 265 Init 0
        	// Parameter  4 added: Name d2 Type D At 0 266 Range 0 265 Init 0
        	// Parameter  6 added: Name d3 Type D At 0 266 Range 0 265 Init 0
        	// Parameter  8 added: Name d4 Type D At 0 266 Range 0 265 Init 0
        	// Parameter 10 added: Name d5 Type D At 0 266 Range 0 265 Init 0
        	// Parameter 12 added: Name d6 Type D At 0 266 Range 0 265 Init 0
        	// Parameter 14 added: Name d7 Type D At 0 266 Range 0 265 Init 0
        	// Parameter 16 added: Name i Type I At 0 266 Range 0 265 Init 0
        	// RetValue  25 added: Name <returnValue> Type I At 0 266 Range 0 265 Init 0 fixed
        	// LocalVar  17 added: Name d8 Type D At 147 83 Range 147 229 Init 147
        	// LocalVar  19 added: Name d9 Type D At 158 74 Range 158 231 Init 158
        	// LocalVar  21 added: Name d10 Type D At 169 82 Range 169 250 Init 169
        	// LocalVar  23 added: Name d11 Type D At 180 73 Range 180 252 Init 180
        	public static int pointCrossingsForQuad(double d, double d1, double d2, double d3, 
        			double d4, double d5, double d6, double d7, int i) {
/* 221*/		if (d1 < d3 && d1 < d5 && d1 < d7)
/* 221*/			return 0;
/* 222*/		if (d1 >= d3 && d1 >= d5 && d1 >= d7)
/* 222*/			return 0;
/* 224*/		if (d >= d2 && d >= d4 && d >= d6)
/* 224*/			return 0;
/* 225*/		if (d < d2 && d < d4 && d < d6) {
/* 226*/			if (d1 >= d3) {
/* 227*/				if (d1 < d7)
/* 227*/					return 1;
        			} else
/* 230*/			if (d1 >= d7)
/* 230*/				return -1;
/* 233*/			return 0;
        		}
/* 236*/		if (i > 52)
/* 236*/			return pointCrossingsForLine(d, d1, d2, d3, d6, d7);
/* 237*/		double d8 = (d2 + d4) / 2D;
/* 238*/		double d9 = (d3 + d5) / 2D;
/* 239*/		double d10 = (d4 + d6) / 2D;
/* 240*/		double d11 = (d5 + d7) / 2D;
/* 241*/		d4 = (d8 + d10) / 2D;
/* 242*/		d5 = (d9 + d11) / 2D;
/* 243*/		if (Double.isNaN(d4) || Double.isNaN(d5))
/* 247*/			return 0;
/* 249*/		else
/* 249*/			return pointCrossingsForQuad(d, d1, d2, d3, d8, d9, d4, d5, i + 1) + pointCrossingsForQuad(d, d1, d4, d5, d10, d11, d6, d7, i + 1);
        	}

        	// Decompiling method: pointCrossingsForCubic  Signature: (DDDDDDDDDDI)I
        	// Max stack: 23, #locals: 33, #params: 21
        	// Code length: 368 bytes, Code offset: 5235
        	// Line Number Table found: 24 entries
        	// Parameter  0 added: Name d Type D At 0 368 Range 0 367 Init 0
        	// Parameter  2 added: Name d1 Type D At 0 368 Range 0 367 Init 0
        	// Parameter  4 added: Name d2 Type D At 0 368 Range 0 367 Init 0
        	// Parameter  6 added: Name d3 Type D At 0 368 Range 0 367 Init 0
        	// Parameter  8 added: Name d4 Type D At 0 368 Range 0 367 Init 0
        	// Parameter 10 added: Name d5 Type D At 0 368 Range 0 367 Init 0
        	// Parameter 12 added: Name d6 Type D At 0 368 Range 0 367 Init 0
        	// Parameter 14 added: Name d7 Type D At 0 368 Range 0 367 Init 0
        	// Parameter 16 added: Name d8 Type D At 0 368 Range 0 367 Init 0
        	// Parameter 18 added: Name d9 Type D At 0 368 Range 0 367 Init 0
        	// Parameter 20 added: Name i Type I At 0 368 Range 0 367 Init 0
        	// RetValue  33 added: Name <returnValue> Type I At 0 368 Range 0 367 Init 0 fixed
        	// LocalVar  21 added: Name d10 Type D At 175 81 Range 175 255 Init 175
        	// LocalVar  23 added: Name d11 Type D At 186 81 Range 186 266 Init 186
        	// LocalVar  25 added: Name d12 Type D At 241 87 Range 241 327 Init 241
        	// LocalVar  27 added: Name d13 Type D At 252 78 Range 252 329 Init 252
        	// LocalVar  29 added: Name d14 Type D At 263 86 Range 263 348 Init 263
        	// LocalVar  31 added: Name d15 Type D At 274 77 Range 274 350 Init 274
        	// LocalVar  21 added: Name d16 Type D At 285 60 Range 285 344 Init 285
        	// LocalVar  23 added: Name d17 Type D At 296 51 Range 296 346 Init 296
        	// LocalVar  21 name d16(D) merged out into d10(D)
        	// LocalVar  23 name d17(D) merged out into d11(D)
        	public static int pointCrossingsForCubic(double d, double d1, double d2, double d3, 
        			double d4, double d5, double d6, double d7, double d8, double d9, int i) {
/* 273*/		if (d1 < d3 && d1 < d5 && d1 < d7 && d1 < d9)
/* 273*/			return 0;
/* 274*/		if (d1 >= d3 && d1 >= d5 && d1 >= d7 && d1 >= d9)
/* 274*/			return 0;
/* 276*/		if (d >= d2 && d >= d4 && d >= d6 && d >= d8)
/* 276*/			return 0;
/* 277*/		if (d < d2 && d < d4 && d < d6 && d < d8) {
/* 278*/			if (d1 >= d3) {
/* 279*/				if (d1 < d9)
/* 279*/					return 1;
        			} else
/* 282*/			if (d1 >= d9)
/* 282*/				return -1;
/* 285*/			return 0;
        		}
/* 288*/		if (i > 52)
/* 288*/			return pointCrossingsForLine(d, d1, d2, d3, d8, d9);
/* 289*/		double d10 = (d4 + d6) / 2D;
/* 290*/		double d11 = (d5 + d7) / 2D;
/* 291*/		d4 = (d2 + d4) / 2D;
/* 292*/		d5 = (d3 + d5) / 2D;
/* 293*/		d6 = (d6 + d8) / 2D;
/* 294*/		d7 = (d7 + d9) / 2D;
/* 295*/		double d12 = (d4 + d10) / 2D;
/* 296*/		double d13 = (d5 + d11) / 2D;
/* 297*/		double d14 = (d10 + d6) / 2D;
/* 298*/		double d15 = (d11 + d7) / 2D;
/* 299*/		d10 = (d12 + d14) / 2D;
/* 300*/		d11 = (d13 + d15) / 2D;
/* 301*/		if (Double.isNaN(d10) || Double.isNaN(d11))
/* 305*/			return 0;
/* 307*/		else
/* 307*/			return pointCrossingsForCubic(d, d1, d2, d3, d4, d5, d12, d13, d10, d11, i + 1) + pointCrossingsForCubic(d, d1, d10, d11, d14, d15, d6, d7, d8, d9, i + 1);
        	}

        	// Decompiling method: rectCrossingsForPath  Signature: (Ljava/awt/geom/PathIterator;DDDD)I
        	// Max stack: 26, #locals: 23, #params: 9
        	// Code length: 457 bytes, Code offset: 5733
        	// Line Number Table found: 44 entries
        	// Parameter  0 added: Name pathiterator Type Ljava/awt/geom/PathIterator; At 0 457 Range 0 456 Init 0
        	// Parameter  1 added: Name d Type D At 0 457 Range 0 456 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 457 Range 0 456 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 457 Range 0 456 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 457 Range 0 456 Init 0
        	// RetValue  23 added: Name <returnValue> Type I At 0 457 Range 0 456 Init 0 fixed
        	// LocalVar   9 added: Name ad Type [D At 31 302 Range 31 332 Init 31
        	// LocalVar  14 added: Name d4 Type D At 65 382 Range 65 446 Init 65
        	// LocalVar  10 added: Name d5 Type D At 67 376 Range 67 442 Init 67
        	// LocalVar  16 added: Name d6 Type D At 74 375 Range 74 448 Init 74
        	// LocalVar  12 added: Name d7 Type D At 76 369 Range 76 444 Init 76
        	// LocalVar  22 added: Name flag Type Z At 79 377 Range 79 455 Init 79
        	// LocalVar  22 chged: Name i Oname flag Type I At 81 2 Range 79 455 Init 79
        	// LocalVar  18 added: Name d8 Type D At 202 31 Range 202 232 Init 202
        	// LocalVar  20 added: Name d9 Type D At 208 29 Range 208 236 Init 208
        	// LocalVar  18 added: Name d10 Type D At 246 40 Range 246 285 Init 246
        	// LocalVar  20 added: Name d11 Type D At 252 38 Range 252 289 Init 252
        	// LocalVar  18 added: Name d12 Type D At 299 48 Range 299 346 Init 299
        	// LocalVar  20 added: Name d13 Type D At 305 46 Range 305 350 Init 305
        	public static int rectCrossingsForPath(PathIterator pathiterator, double d, double d1, double d2, double d3) {
/* 363*/		if (d2 <= d || d3 <= d1)
/* 364*/			return 0;
/* 366*/		if (pathiterator.isDone())
/* 367*/			return 0;
/* 369*/		double ad[] = new double[6];
/* 370*/		if (pathiterator.currentSegment(ad) != 0)
/* 371*/			throw new IllegalPathStateException("missing initial moveto in path definition");
/* 374*/		pathiterator.next();
        		double d6;
/* 376*/		double d4 = d6 = ad[0];
        		double d7;
/* 377*/		double d5 = d7 = ad[1];
        		int i;
/* 378*/		for (i = 0; i != 0x80000000 && !pathiterator.isDone(); pathiterator.next())
/* 380*/			switch (pathiterator.currentSegment(ad)) {
/* 363*/			default:
        				break;

/* 382*/			case 0: // '\0'
/* 382*/				if (d4 != d6 || d5 != d7)
/* 383*/					i = rectCrossingsForLine(i, d, d1, d2, d3, d4, d5, d6, d7);
/* 391*/				d6 = d4 = ad[0];
/* 392*/				d7 = d5 = ad[1];
/* 393*/				break;

/* 395*/			case 1: // '\001'
/* 395*/				double d8 = ad[0];
/* 396*/				double d11 = ad[1];
/* 397*/				i = rectCrossingsForLine(i, d, d1, d2, d3, d4, d5, d8, d11);
/* 402*/				d4 = d8;
/* 403*/				d5 = d11;
/* 404*/				break;

/* 406*/			case 2: // '\002'
/* 406*/				double d9 = ad[2];
/* 407*/				double d12 = ad[3];
/* 408*/				i = rectCrossingsForQuad(i, d, d1, d2, d3, d4, d5, ad[0], ad[1], d9, d12, 0);
/* 414*/				d4 = d9;
/* 415*/				d5 = d12;
/* 416*/				break;

/* 418*/			case 3: // '\003'
/* 418*/				double d10 = ad[4];
/* 419*/				double d13 = ad[5];
/* 420*/				i = rectCrossingsForCubic(i, d, d1, d2, d3, d4, d5, ad[0], ad[1], ad[2], ad[3], d10, d13, 0);
/* 427*/				d4 = d10;
/* 428*/				d5 = d13;
/* 429*/				break;

/* 431*/			case 4: // '\004'
/* 431*/				if (d4 != d6 || d5 != d7)
/* 432*/					i = rectCrossingsForLine(i, d, d1, d2, d3, d4, d5, d6, d7);
/* 438*/				d4 = d6;
/* 439*/				d5 = d7;
        				break;
        			}

/* 446*/		if (i != 0x80000000 && (d4 != d6 || d5 != d7))
/* 447*/			i = rectCrossingsForLine(i, d, d1, d2, d3, d4, d5, d6, d7);
/* 455*/		return i;
        	}

        	// Decompiling method: rectCrossingsForLine  Signature: (IDDDDDDDD)I
        	// Max stack: 8, #locals: 21, #params: 17
        	// Code length: 420 bytes, Code offset: 6400
        	// Line Number Table found: 33 entries
        	// Parameter  0 added: Name i Type I At 0 420 Range 0 419 Init 0
        	// Parameter  1 added: Name d Type D At 0 420 Range 0 419 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 420 Range 0 419 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 420 Range 0 419 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 420 Range 0 419 Init 0
        	// Parameter  9 added: Name d4 Type D At 0 420 Range 0 419 Init 0
        	// Parameter 11 added: Name d5 Type D At 0 420 Range 0 419 Init 0
        	// Parameter 13 added: Name d6 Type D At 0 420 Range 0 419 Init 0
        	// Parameter 15 added: Name d7 Type D At 0 420 Range 0 419 Init 0
        	// RetValue  21 added: Name <returnValue> Type I At 0 420 Range 0 419 Init 0 fixed
        	// LocalVar  17 added: Name d8 Type D At 194 146 Range 194 339 Init 194
        	// LocalVar  19 added: Name d9 Type D At 259 89 Range 259 347 Init 259
        	public static int rectCrossingsForLine(int i, double d, double d1, double d2, double d3, double d4, double d5, double d6, 
        			double d7) {
/* 469*/		if (d5 >= d3 && d7 >= d3)
/* 469*/			return i;
/* 470*/		if (d5 <= d1 && d7 <= d1)
/* 470*/			return i;
/* 471*/		if (d4 <= d && d6 <= d)
/* 471*/			return i;
/* 472*/		if (d4 >= d2 && d6 >= d2) {
/* 478*/			if (d5 < d7) {
/* 481*/				if (d5 <= d1)
/* 481*/					i++;
/* 482*/				if (d7 >= d3)
/* 482*/					i++;
        			} else
/* 483*/			if (d7 < d5) {
/* 486*/				if (d7 <= d1)
/* 486*/					i--;
/* 487*/				if (d5 >= d3)
/* 487*/					i--;
        			}
/* 489*/			return i;
        		}
/* 495*/		if (d4 > d && d4 < d2 && d5 > d1 && d5 < d3 || d6 > d && d6 < d2 && d7 > d1 && d7 < d3)
/* 498*/			return 0x80000000;
/* 502*/		double d8 = d4;
/* 503*/		if (d5 < d1)
/* 504*/			d8 += ((d1 - d5) * (d6 - d4)) / (d7 - d5);
/* 505*/		else
/* 505*/		if (d5 > d3)
/* 506*/			d8 += ((d3 - d5) * (d6 - d4)) / (d7 - d5);
/* 508*/		double d9 = d6;
/* 509*/		if (d7 < d1)
/* 510*/			d9 += ((d1 - d7) * (d4 - d6)) / (d5 - d7);
/* 511*/		else
/* 511*/		if (d7 > d3)
/* 512*/			d9 += ((d3 - d7) * (d4 - d6)) / (d5 - d7);
/* 514*/		if (d8 <= d && d9 <= d)
/* 514*/			return i;
/* 515*/		if (d8 >= d2 && d9 >= d2) {
/* 516*/			if (d5 < d7) {
/* 519*/				if (d5 <= d1)
/* 519*/					i++;
/* 520*/				if (d7 >= d3)
/* 520*/					i++;
        			} else
/* 521*/			if (d7 < d5) {
/* 524*/				if (d7 <= d1)
/* 524*/					i--;
/* 525*/				if (d5 >= d3)
/* 525*/					i--;
        			}
/* 527*/			return i;
        		} else {
/* 529*/			return 0x80000000;
        		}
        	}

        	// Decompiling method: rectCrossingsForQuad  Signature: (IDDDDDDDDDDI)I
        	// Max stack: 23, #locals: 30, #params: 22
        	// Code length: 424 bytes, Code offset: 6986
        	// Line Number Table found: 27 entries
        	// Parameter  0 added: Name i Type I At 0 424 Range 0 423 Init 0
        	// Parameter  1 added: Name d Type D At 0 424 Range 0 423 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 424 Range 0 423 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 424 Range 0 423 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 424 Range 0 423 Init 0
        	// Parameter  9 added: Name d4 Type D At 0 424 Range 0 423 Init 0
        	// Parameter 11 added: Name d5 Type D At 0 424 Range 0 423 Init 0
        	// Parameter 13 added: Name d6 Type D At 0 424 Range 0 423 Init 0
        	// Parameter 15 added: Name d7 Type D At 0 424 Range 0 423 Init 0
        	// Parameter 17 added: Name d8 Type D At 0 424 Range 0 423 Init 0
        	// Parameter 19 added: Name d9 Type D At 0 424 Range 0 423 Init 0
        	// Parameter 21 added: Name j Type I At 0 424 Range 0 423 Init 0
        	// RetValue  30 added: Name <returnValue> Type I At 0 424 Range 0 423 Init 0 fixed
        	// LocalVar  22 added: Name d10 Type D At 287 88 Range 287 374 Init 287
        	// LocalVar  24 added: Name d11 Type D At 298 79 Range 298 376 Init 298
        	// LocalVar  26 added: Name d12 Type D At 309 99 Range 309 407 Init 309
        	// LocalVar  28 added: Name d13 Type D At 320 90 Range 320 409 Init 320
        	public static int rectCrossingsForQuad(int i, double d, double d1, double d2, double d3, double d4, double d5, double d6, 
        			double d7, double d8, double d9, int j) {
/* 545*/		if (d5 >= d3 && d7 >= d3 && d9 >= d3)
/* 545*/			return i;
/* 546*/		if (d5 <= d1 && d7 <= d1 && d9 <= d1)
/* 546*/			return i;
/* 547*/		if (d4 <= d && d6 <= d && d8 <= d)
/* 547*/			return i;
/* 548*/		if (d4 >= d2 && d6 >= d2 && d8 >= d2) {
/* 557*/			if (d5 < d9) {
/* 559*/				if (d5 <= d1 && d9 > d1)
/* 559*/					i++;
/* 560*/				if (d5 < d3 && d9 >= d3)
/* 560*/					i++;
        			} else
/* 561*/			if (d9 < d5) {
/* 563*/				if (d9 <= d1 && d5 > d1)
/* 563*/					i--;
/* 564*/				if (d9 < d3 && d5 >= d3)
/* 564*/					i--;
        			}
/* 566*/			return i;
        		}
/* 571*/		if (d4 < d2 && d4 > d && d5 < d3 && d5 > d1 || d8 < d2 && d8 > d && d9 < d3 && d9 > d1)
/* 574*/			return 0x80000000;
/* 578*/		if (j > 52)
/* 579*/			return rectCrossingsForLine(i, d, d1, d2, d3, d4, d5, d8, d9);
/* 583*/		double d10 = (d4 + d6) / 2D;
/* 584*/		double d11 = (d5 + d7) / 2D;
/* 585*/		double d12 = (d6 + d8) / 2D;
/* 586*/		double d13 = (d7 + d9) / 2D;
/* 587*/		d6 = (d10 + d12) / 2D;
/* 588*/		d7 = (d11 + d13) / 2D;
/* 589*/		if (Double.isNaN(d6) || Double.isNaN(d7))
/* 593*/			return 0;
/* 595*/		i = rectCrossingsForQuad(i, d, d1, d2, d3, d4, d5, d10, d11, d6, d7, j + 1);
/* 599*/		if (i != 0x80000000)
/* 600*/			i = rectCrossingsForQuad(i, d, d1, d2, d3, d6, d7, d12, d13, d8, d9, j + 1);
/* 605*/		return i;
        	}

        	// Decompiling method: rectCrossingsForCubic  Signature: (IDDDDDDDDDDDDI)I
        	// Max stack: 27, #locals: 38, #params: 26
        	// Code length: 528 bytes, Code offset: 7552
        	// Line Number Table found: 36 entries
        	// Parameter  0 added: Name i Type I At 0 528 Range 0 527 Init 0
        	// Parameter  1 added: Name d Type D At 0 528 Range 0 527 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 528 Range 0 527 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 528 Range 0 527 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 528 Range 0 527 Init 0
        	// Parameter  9 added: Name d4 Type D At 0 528 Range 0 527 Init 0
        	// Parameter 11 added: Name d5 Type D At 0 528 Range 0 527 Init 0
        	// Parameter 13 added: Name d6 Type D At 0 528 Range 0 527 Init 0
        	// Parameter 15 added: Name d7 Type D At 0 528 Range 0 527 Init 0
        	// Parameter 17 added: Name d8 Type D At 0 528 Range 0 527 Init 0
        	// Parameter 19 added: Name d9 Type D At 0 528 Range 0 527 Init 0
        	// Parameter 21 added: Name d10 Type D At 0 528 Range 0 527 Init 0
        	// Parameter 23 added: Name d11 Type D At 0 528 Range 0 527 Init 0
        	// Parameter 25 added: Name j Type I At 0 528 Range 0 527 Init 0
        	// RetValue  38 added: Name <returnValue> Type I At 0 528 Range 0 527 Init 0 fixed
        	// LocalVar  26 added: Name d12 Type D At 317 81 Range 317 397 Init 317
        	// LocalVar  28 added: Name d13 Type D At 328 81 Range 328 408 Init 328
        	// LocalVar  30 added: Name d14 Type D At 383 92 Range 383 474 Init 383
        	// LocalVar  32 added: Name d15 Type D At 394 83 Range 394 476 Init 394
        	// LocalVar  34 added: Name d16 Type D At 405 103 Range 405 507 Init 405
        	// LocalVar  36 added: Name d17 Type D At 416 94 Range 416 509 Init 416
        	// LocalVar  26 added: Name d18 Type D At 427 77 Range 427 503 Init 427
        	// LocalVar  28 added: Name d19 Type D At 438 68 Range 438 505 Init 438
        	// LocalVar  26 name d18(D) merged out into d12(D)
        	// LocalVar  28 name d19(D) merged out into d13(D)
        	public static int rectCrossingsForCubic(int i, double d, double d1, double d2, double d3, double d4, double d5, double d6, 
        			double d7, double d8, double d9, double d10, double d11, int j) {
/* 622*/		if (d5 >= d3 && d7 >= d3 && d9 >= d3 && d11 >= d3)
/* 623*/			return i;
/* 625*/		if (d5 <= d1 && d7 <= d1 && d9 <= d1 && d11 <= d1)
/* 626*/			return i;
/* 628*/		if (d4 <= d && d6 <= d && d8 <= d && d10 <= d)
/* 629*/			return i;
/* 631*/		if (d4 >= d2 && d6 >= d2 && d8 >= d2 && d10 >= d2) {
/* 640*/			if (d5 < d11) {
/* 642*/				if (d5 <= d1 && d11 > d1)
/* 642*/					i++;
/* 643*/				if (d5 < d3 && d11 >= d3)
/* 643*/					i++;
        			} else
/* 644*/			if (d11 < d5) {
/* 646*/				if (d11 <= d1 && d5 > d1)
/* 646*/					i--;
/* 647*/				if (d11 < d3 && d5 >= d3)
/* 647*/					i--;
        			}
/* 649*/			return i;
        		}
/* 654*/		if (d4 > d && d4 < d2 && d5 > d1 && d5 < d3 || d10 > d && d10 < d2 && d11 > d1 && d11 < d3)
/* 657*/			return 0x80000000;
/* 661*/		if (j > 52)
/* 662*/			return rectCrossingsForLine(i, d, d1, d2, d3, d4, d5, d10, d11);
/* 666*/		double d12 = (d6 + d8) / 2D;
/* 667*/		double d13 = (d7 + d9) / 2D;
/* 668*/		d6 = (d4 + d6) / 2D;
/* 669*/		d7 = (d5 + d7) / 2D;
/* 670*/		d8 = (d8 + d10) / 2D;
/* 671*/		d9 = (d9 + d11) / 2D;
/* 672*/		double d14 = (d6 + d12) / 2D;
/* 673*/		double d15 = (d7 + d13) / 2D;
/* 674*/		double d16 = (d12 + d8) / 2D;
/* 675*/		double d17 = (d13 + d9) / 2D;
/* 676*/		d12 = (d14 + d16) / 2D;
/* 677*/		d13 = (d15 + d17) / 2D;
/* 678*/		if (Double.isNaN(d12) || Double.isNaN(d13))
/* 682*/			return 0;
/* 684*/		i = rectCrossingsForCubic(i, d, d1, d2, d3, d4, d5, d6, d7, d14, d15, d12, d13, j + 1);
/* 688*/		if (i != 0x80000000)
/* 689*/			i = rectCrossingsForCubic(i, d, d1, d2, d3, d12, d13, d16, d17, d8, d9, d10, d11, j + 1);
/* 694*/		return i;
        	}

        	// Decompiling method: <init>  Signature: (I)V
        	// Max stack: 2, #locals: 2, #params: 2
        	// Code length: 10 bytes, Code offset: 8258
        	// Line Number Table found: 3 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 10 Range 0 9 Init 0 fixed
        	// Parameter  1 added: Name i Type I At 0 10 Range 0 9 Init 0
        	// RetValue   2 added: Name <returnValue> Type V At 0 10 Range 0 9 Init 0 fixed
        	public Curve(int i) {
/* 697*/		/* super(); */
/* 698*/		direction = i;
/* 699*/		/* return; */
        	}

        	// Decompiling method: getDirection  Signature: ()I
        	// Max stack: 1, #locals: 1, #params: 1
        	// Code length: 5 bytes, Code offset: 8314
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 5 Range 0 4 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type I At 0 5 Range 0 4 Init 0 fixed
        	public final int getDirection() {
/* 702*/		return direction;
        	}

        	// Decompiling method: getWithDirection  Signature: (I)Lsun/awt/geom/Curve;
        	// Max stack: 2, #locals: 2, #params: 2
        	// Code length: 17 bytes, Code offset: 8357
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 17 Range 0 16 Init 0 fixed
        	// Parameter  1 added: Name i Type I At 0 17 Range 0 16 Init 0
        	// RetValue   2 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 17 Range 0 16 Init 0 fixed
        	public final Curve getWithDirection(int i) {
/* 706*/		return direction != i ? getReversedCurve() : this;
        	}

        	// Decompiling method: round  Signature: (D)D
        	// Max stack: 2, #locals: 2, #params: 2
        	// Code length: 2 bytes, Code offset: 8412
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name d Type D At 0 2 Range 0 1 Init 0
        	// RetValue   2 added: Name <returnValue> Type D At 0 2 Range 0 1 Init 0 fixed
        	public static double round(double d) {
/* 711*/		return d;
        	}

        	// Decompiling method: orderof  Signature: (DD)I
        	// Max stack: 4, #locals: 4, #params: 4
        	// Code length: 18 bytes, Code offset: 8452
        	// Line Number Table found: 5 entries
        	// Parameter  0 added: Name d Type D At 0 18 Range 0 17 Init 0
        	// Parameter  2 added: Name d1 Type D At 0 18 Range 0 17 Init 0
        	// RetValue   4 added: Name <returnValue> Type I At 0 18 Range 0 17 Init 0 fixed
        	public static int orderof(double d, double d1) {
/* 715*/		if (d < d1)
/* 716*/			return -1;
/* 718*/		return d <= d1 ? 0 : 1;
        	}

        	// Decompiling method: signeddiffbits  Signature: (DD)J
        	// Max stack: 4, #locals: 4, #params: 4
        	// Code length: 10 bytes, Code offset: 8524
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name d Type D At 0 10 Range 0 9 Init 0
        	// Parameter  2 added: Name d1 Type D At 0 10 Range 0 9 Init 0
        	// RetValue   4 added: Name <returnValue> Type J At 0 10 Range 0 9 Init 0 fixed
        	public static long signeddiffbits(double d, double d1) {
/* 725*/		return Double.doubleToLongBits(d) - Double.doubleToLongBits(d1);
        	}

        	// Decompiling method: diffbits  Signature: (DD)J
        	// Max stack: 4, #locals: 4, #params: 4
        	// Code length: 13 bytes, Code offset: 8572
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name d Type D At 0 13 Range 0 12 Init 0
        	// Parameter  2 added: Name d1 Type D At 0 13 Range 0 12 Init 0
        	// RetValue   4 added: Name <returnValue> Type J At 0 13 Range 0 12 Init 0 fixed
        	public static long diffbits(double d, double d1) {
/* 728*/		return Math.abs(Double.doubleToLongBits(d) - Double.doubleToLongBits(d1));
        	}

        	// Decompiling method: prev  Signature: (D)D
        	// Max stack: 4, #locals: 2, #params: 2
        	// Code length: 10 bytes, Code offset: 8623
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name d Type D At 0 10 Range 0 9 Init 0
        	// RetValue   2 added: Name <returnValue> Type D At 0 10 Range 0 9 Init 0 fixed
        	public static double prev(double d) {
/* 732*/		return Double.longBitsToDouble(Double.doubleToLongBits(d) - 1L);
        	}

        	// Decompiling method: next  Signature: (D)D
        	// Max stack: 4, #locals: 2, #params: 2
        	// Code length: 10 bytes, Code offset: 8671
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name d Type D At 0 10 Range 0 9 Init 0
        	// RetValue   2 added: Name <returnValue> Type D At 0 10 Range 0 9 Init 0 fixed
        	public static double next(double d) {
/* 735*/		return Double.longBitsToDouble(Double.doubleToLongBits(d) + 1L);
        	}

        	// Decompiling method: toString  Signature: ()Ljava/lang/String;
        	// Max stack: 3, #locals: 1, #params: 1
        	// Code length: 128 bytes, Code offset: 8719
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 128 Range 0 127 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type Ljava/lang/String; At 0 128 Range 0 127 Init 0 fixed
        	@Override
			public String toString() {
/* 739*/		return (new StringBuilder()).append("Curve[").append(getOrder()).append(", ").append("(").append(round(getX0())).append(", ").append(round(getY0())).append("), ").append(controlPointString()).append("(").append(round(getX1())).append(", ").append(round(getY1())).append("), ").append(direction != 1 ? "U" : "D").append("]").toString();
        	}

        	// Decompiling method: controlPointString  Signature: ()Ljava/lang/String;
        	// Max stack: 1, #locals: 1, #params: 1
        	// Code length: 3 bytes, Code offset: 8885
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 3 Range 0 2 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type Ljava/lang/String; At 0 3 Range 0 2 Init 0 fixed
        	public String controlPointString() {
/* 749*/		return "";
        	}

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type I At 0 0 Range 0 -1 Init 0 fixed
        	public abstract int getOrder();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getXTop();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getYTop();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getXBot();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getYBot();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getXMin();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getXMax();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getX0();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getY0();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getX1();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double getY1();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 0 Range 0 -1 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double XforY(double d);

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 0 Range 0 -1 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double TforY(double d);

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 0 Range 0 -1 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double XforT(double d);

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 0 Range 0 -1 Init 0
        	// RetValue   3 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double YforT(double d);

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 0 Range 0 -1 Init 0
        	// Parameter  3 added: Name i Type I At 0 0 Range 0 -1 Init 0
        	// RetValue   4 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double dXforT(double d, int i);

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 0 Range 0 -1 Init 0
        	// Parameter  3 added: Name i Type I At 0 0 Range 0 -1 Init 0
        	// RetValue   4 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double dYforT(double d, int i);

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 0 Range 0 -1 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 0 Range 0 -1 Init 0
        	// RetValue   5 added: Name <returnValue> Type D At 0 0 Range 0 -1 Init 0 fixed
        	public abstract double nextVertical(double d, double d1);

        	// Decompiling method: crossingsFor  Signature: (DD)I
        	// Max stack: 5, #locals: 5, #params: 5
        	// Code length: 50 bytes, Code offset: 9070
        	// Line Number Table found: 4 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 50 Range 0 49 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 50 Range 0 49 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 50 Range 0 49 Init 0
        	// RetValue   5 added: Name <returnValue> Type I At 0 50 Range 0 49 Init 0 fixed
        	public int crossingsFor(double d, double d1) {
/* 777*/		return d1 < getYTop() || d1 >= getYBot() || d >= getXMax() || d >= getXMin() && d >= XforY(d1) ? 0 : 1;
        	}

        	// Decompiling method: accumulateCrossings  Signature: (Lsun/awt/geom/Crossings;)Z
        	// Max stack: 6, #locals: 26, #params: 2
        	// Code length: 221 bytes, Code offset: 9170
        	// Line Number Table found: 39 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 221 Range 0 220 Init 0 fixed
        	// Parameter  1 added: Name crossings Type Lsun/awt/geom/Crossings; At 0 221 Range 0 220 Init 0
        	// RetValue  26 added: Name <returnValue> Type Z At 0 221 Range 0 220 Init 0 fixed
        	// LocalVar   2 added: Name d Type D At 4 139 Range 4 142 Init 4
        	// LocalVar   4 added: Name d1 Type D At 20 136 Range 20 155 Init 20
        	// LocalVar   6 added: Name d2 Type D At 26 45 Range 26 70 Init 26
        	// LocalVar   8 added: Name d3 Type D At 32 79 Range 32 110 Init 32
        	// LocalVar  10 added: Name d4 Type D At 38 53 Range 38 90 Init 38
        	// LocalVar  12 added: Name d5 Type D At 44 77 Range 44 120 Init 44
        	// LocalVar  16 added: Name d6 Type D At 66 144 Range 66 209 Init 66
        	// LocalVar  14 added: Name d7 Type D At 74 118 Range 74 191 Init 74
        	// LocalVar  20 added: Name d8 Type D At 106 106 Range 106 211 Init 106
        	// LocalVar  18 added: Name d9 Type D At 114 80 Range 114 193 Init 114
        	// LocalVar  22 added: Name flag Type Z At 127 77 Range 127 203 Init 127
        	// LocalVar  23 added: Name flag1 Type Z At 130 19 Range 130 148 Init 130
        	// LocalVar  24 added: Name d10 Type D At 138 16 Range 138 153 Init 138
        	public boolean accumulateCrossings(Crossings crossings) {
/* 786*/		double d = crossings.getXHi();
/* 787*/		if (getXMin() >= d)
/* 788*/			return false;
/* 790*/		double d1 = crossings.getXLo();
/* 791*/		double d2 = crossings.getYLo();
/* 792*/		double d3 = crossings.getYHi();
/* 793*/		double d4 = getYTop();
/* 794*/		double d5 = getYBot();
        		double d6;
        		double d7;
/* 796*/		if (d4 < d2) {
/* 797*/			if (d5 <= d2)
/* 798*/				return false;
/* 800*/			d7 = d2;
/* 801*/			d6 = TforY(d2);
        		} else {
/* 803*/			if (d4 >= d3)
/* 804*/				return false;
/* 806*/			d7 = d4;
/* 807*/			d6 = 0.0D;
        		}
        		double d8;
        		double d9;
/* 809*/		if (d5 > d3) {
/* 810*/			d9 = d3;
/* 811*/			d8 = TforY(d3);
        		} else {
/* 813*/			d9 = d5;
/* 814*/			d8 = 1.0D;
        		}
/* 816*/		boolean flag = false;
/* 817*/		boolean flag1 = false;
/* 819*/		do {
/* 819*/			double d10 = XforT(d6);
/* 820*/			if (d10 < d) {
/* 821*/				if (flag1 || d10 > d1)
/* 822*/					return true;
/* 824*/				flag = true;
        			} else {
/* 826*/				if (flag)
/* 827*/					return true;
/* 829*/				flag1 = true;
        			}
/* 831*/			if (d6 >= d8)
/* 832*/				break;
/* 834*/			d6 = nextVertical(d6, d8);
        		} while (true);
/* 836*/		if (flag)
/* 837*/			crossings.record(d7, d9, direction);
/* 839*/		return false;
        	}

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name rectangle2d Type Ljava/awt/geom/Rectangle2D; At 0 0 Range 0 -1 Init 0
        	// RetValue   2 added: Name <returnValue> Type V At 0 0 Range 0 -1 Init 0 fixed
        	public abstract void enlarge(Rectangle2D rectangle2d);

        	// Decompiling method: getSubCurve  Signature: (DD)Lsun/awt/geom/Curve;
        	// Max stack: 6, #locals: 5, #params: 5
        	// Code length: 11 bytes, Code offset: 9589
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 11 Range 0 10 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 11 Range 0 10 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 11 Range 0 10 Init 0
        	// RetValue   5 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 11 Range 0 10 Init 0 fixed
        	public Curve getSubCurve(double d, double d1) {
/* 845*/		return getSubCurve(d, d1, direction);
        	}

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// RetValue   1 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	public abstract Curve getReversedCurve();

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 0 Range 0 -1 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 0 Range 0 -1 Init 0
        	// Parameter  5 added: Name i Type I At 0 0 Range 0 -1 Init 0
        	// RetValue   6 added: Name <returnValue> Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	public abstract Curve getSubCurve(double d, double d1, int i);

        	// Decompiling method: compareTo  Signature: (Lsun/awt/geom/Curve;[D)I
        	// Max stack: 31, #locals: 40, #params: 3
        	// Code length: 1013 bytes, Code offset: 9654
        	// Exception table: 1 entries
        	//           start  685 end 722 handler 728 type Throwable
        	// Line Number Table found: 91 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 1013 Range 0 1012 Init 0 fixed
        	// Parameter  1 added: Name curve Type Lsun/awt/geom/Curve; At 0 1013 Range 0 1012 Init 0
        	// Parameter  2 added: Name ad Type [D At 0 1013 Range 0 1012 Init 0
        	// RetValue  40 added: Name <returnValue> Type I At 0 1013 Range 0 1012 Init 0 fixed
        	// LocalVar   3 added: Name d Type D At 3 562 Range 3 564 Init 3
        	// LocalVar   5 added: Name d1 Type D At 7 4 Range 7 10 Init 7
        	// LocalVar   5 added: Name d2 Type D At 25 548 Range 25 572 Init 25
        	// LocalVar   7 added: Name d3 Type D At 215 479 Range 215 693 Init 215
        	// LocalVar   9 added: Name d4 Type D At 223 588 Range 223 810 Init 223
        	// LocalVar  11 added: Name d5 Type D At 257 381 Range 257 637 Init 257
        	// LocalVar  13 added: Name d6 Type D At 290 416 Range 290 705 Init 290
        	// LocalVar  15 added: Name d7 Type D At 298 549 Range 298 846 Init 298
        	// LocalVar  17 added: Name d8 Type D At 332 332 Range 332 663 Init 332
        	// LocalVar  19 added: Name d9 Type D At 366 330 Range 366 695 Init 366
        	// LocalVar  21 added: Name d10 Type D At 374 334 Range 374 707 Init 374
        	// LocalVar  23 added: Name d11 Type D At 388 4 Range 388 391 Init 388
        	// LocalVar  25 added: Name d12 Type D At 402 481 Range 402 882 Init 402
        	// LocalVar  27 added: Name d13 Type D At 417 85 Range 417 501 Init 417
        	// LocalVar  29 added: Name d14 Type D At 436 52 Range 436 487 Init 436
        	// LocalVar  31 added: Name d15 Type D At 442 139 Range 442 580 Init 442
        	// LocalVar  33 added: Name d16 Type D At 513 34 Range 513 546 Init 513
        	// LocalVar  27 added: Name d17 Type D At 641 289 Range 641 929 Init 641
        	// LocalVar  29 added: Name d18 Type D At 649 285 Range 649 933 Init 649
        	// LocalVar  31 added: Name d19 Type D At 657 281 Range 657 937 Init 657
        	// LocalVar  33 added: Name d20 Type D At 667 304 Range 667 970 Init 667
        	// LocalVar  35 added: Name d21 Type D At 675 300 Range 675 974 Init 675
        	// LocalVar  37 added: Name d22 Type D At 683 296 Range 683 978 Init 683
        	// LocalVar  27 added: Name d23 Type D At 995 11 Range 995 1005 Init 995
        	// LocalVar  39 added: Name throwable Type Ljava/lang/Throwable; At 728 19 Range 728 746 Init 728
        	// LocalVar   5 name d2(D) merged out into d1(D)
        	public int compareTo(Curve curve, double ad[]) {
/* 856*/		double d = ad[0];
/* 857*/		double d1 = ad[1];
/* 858*/		d1 = Math.min(Math.min(d1, getYBot()), curve.getYBot());
/* 859*/		if (d1 <= ad[0]) {
/* 860*/			System.err.println((new StringBuilder()).append("this == ").append(((this))).toString());
/* 861*/			System.err.println((new StringBuilder()).append("that == ").append(((curve))).toString());
/* 862*/			System.out.println((new StringBuilder()).append("target range = ").append(ad[0]).append("=>").append(ad[1]).toString());
/* 863*/			throw new InternalError((new StringBuilder()).append("backstepping from ").append(ad[0]).append(" to ").append(d1).toString());
        		}
/* 865*/		ad[1] = d1;
/* 866*/		if (getXMax() <= curve.getXMin())
/* 867*/			return getXMin() != curve.getXMax() ? -1 : 0;
/* 872*/		if (getXMin() >= curve.getXMax())
/* 873*/			return 1;
/* 881*/		double d2 = TforY(d);
/* 882*/		double d3 = YforT(d2);
/* 883*/		if (d3 < d) {
/* 884*/			d2 = refineTforY(d2, d3, d);
/* 885*/			d3 = YforT(d2);
        		}
/* 887*/		double d4 = TforY(d1);
/* 888*/		if (YforT(d4) < d)
/* 889*/			d4 = refineTforY(d4, YforT(d4), d);
/* 892*/		double d5 = curve.TforY(d);
/* 893*/		double d6 = curve.YforT(d5);
/* 894*/		if (d6 < d) {
/* 895*/			d5 = curve.refineTforY(d5, d6, d);
/* 896*/			d6 = curve.YforT(d5);
        		}
/* 898*/		double d7 = curve.TforY(d1);
/* 899*/		if (curve.YforT(d7) < d)
/* 900*/			d7 = curve.refineTforY(d7, curve.YforT(d7), d);
/* 903*/		double d8 = XforT(d2);
/* 904*/		double d9 = curve.XforT(d5);
/* 905*/		double d10 = Math.max(Math.abs(d), Math.abs(d1));
/* 906*/		double d11 = Math.max(d10 * 1E-014D, 1E-300D);
/* 907*/		if (fairlyClose(d8, d9)) {
/* 908*/			double d12 = d11;
/* 909*/			double d15 = Math.min(d11 * 10000000000000D, (d1 - d) * 0.10000000000000001D);
        			double d17;
/* 910*/label0:
/* 910*/			for (d17 = d + d12; d17 <= d1; d17 += d12) {
/* 912*/				if (fairlyClose(XforY(d17), curve.XforY(d17))) {
/* 913*/					if ((d12 *= 2D) > d15)
/* 914*/						d12 = d15;
/* 914*/					continue;
        				}
/* 917*/				d17 -= d12;
/* 919*/				do {
        					double d19;
/* 919*/					do {
/* 919*/						d12 /= 2D;
/* 920*/						d19 = d17 + d12;
/* 921*/						if (d19 <= d17)
/* 922*/							break label0;
        					} while (!fairlyClose(XforY(d19), curve.XforY(d19)));
/* 925*/					d17 = d19;
        				} while (true);
        			}

/* 932*/			if (d17 > d) {
/* 933*/				if (d17 < d1)
/* 934*/					ad[1] = d17;
/* 936*/				return 0;
        			}
        		}
/* 940*/		if (d11 <= 0.0D)
/* 941*/			System.out.println((new StringBuilder()).append("ymin = ").append(d11).toString());
/* 947*/		do {
/* 947*/			if (d2 >= d4 || d5 >= d7)
/* 948*/				break;
/* 948*/			double d13 = nextVertical(d2, d4);
/* 949*/			double d16 = XforT(d13);
/* 950*/			double d18 = YforT(d13);
/* 951*/			double d20 = curve.nextVertical(d5, d7);
/* 952*/			double d21 = curve.XforT(d20);
/* 953*/			double d22 = curve.YforT(d20);
/* 959*/			try {
/* 959*/				if (findIntersect(curve, ad, d11, 0, 0, d2, d8, d3, d13, d16, d18, d5, d9, d6, d20, d21, d22))
/* 962*/					break;
        			}
/* 964*/			catch (Throwable throwable) {
/* 965*/				System.err.println((new StringBuilder()).append("Error: ").append(((throwable))).toString());
/* 966*/				System.err.println((new StringBuilder()).append("y range was ").append(ad[0]).append("=>").append(ad[1]).toString());
/* 967*/				System.err.println((new StringBuilder()).append("s y range is ").append(d3).append("=>").append(d18).toString());
/* 968*/				System.err.println((new StringBuilder()).append("t y range is ").append(d6).append("=>").append(d22).toString());
/* 969*/				System.err.println((new StringBuilder()).append("ymin is ").append(d11).toString());
/* 970*/				return 0;
        			}
/* 972*/			if (d18 < d22) {
/* 973*/				if (d18 > ad[0]) {
/* 974*/					if (d18 < ad[1])
/* 975*/						ad[1] = d18;
/* 975*/					break;
        				}
/* 979*/				d2 = d13;
/* 980*/				d8 = d16;
/* 981*/				d3 = d18;
/* 981*/				continue;
        			}
/* 983*/			if (d22 > ad[0]) {
/* 984*/				if (d22 < ad[1])
/* 985*/					ad[1] = d22;
/* 985*/				break;
        			}
/* 989*/			d5 = d20;
/* 990*/			d9 = d21;
/* 991*/			d6 = d22;
        		} while (true);
/* 994*/		double d14 = (ad[0] + ad[1]) / 2D;
/*1010*/		return orderof(XforY(d14), curve.XforY(d14));
        	}

        	// Decompiling method: findIntersect  Signature: (Lsun/awt/geom/Curve;[DDIIDDDDDDDDDDDD)Z
        	// Max stack: 31, #locals: 53, #params: 31
        	// Code length: 1076 bytes, Code offset: 11073
        	// Line Number Table found: 75 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 1076 Range 0 1075 Init 0 fixed
        	// Parameter  1 added: Name curve Type Lsun/awt/geom/Curve; At 0 1076 Range 0 1075 Init 0
        	// Parameter  2 added: Name ad Type [D At 0 1076 Range 0 1075 Init 0
        	// Parameter  3 added: Name d Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter  5 added: Name i Type I At 0 1076 Range 0 1075 Init 0
        	// Parameter  6 added: Name j Type I At 0 1076 Range 0 1075 Init 0
        	// Parameter  7 added: Name d1 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter  9 added: Name d2 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 11 added: Name d3 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 13 added: Name d4 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 15 added: Name d5 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 17 added: Name d6 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 19 added: Name d7 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 21 added: Name d8 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 23 added: Name d9 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 25 added: Name d10 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 27 added: Name d11 Type D At 0 1076 Range 0 1075 Init 0
        	// Parameter 29 added: Name d12 Type D At 0 1076 Range 0 1075 Init 0
        	// RetValue  53 added: Name <returnValue> Type Z At 0 1076 Range 0 1075 Init 0 fixed
        	// LocalVar  31 added: Name d13 Type D At 77 510 Range 77 586 Init 77
        	// LocalVar  33 added: Name d14 Type D At 85 504 Range 85 588 Init 85
        	// LocalVar  35 added: Name d15 Type D At 93 498 Range 93 590 Init 93
        	// LocalVar  37 added: Name d16 Type D At 194 302 Range 194 495 Init 194
        	// LocalVar  39 added: Name d17 Type D At 202 296 Range 202 497 Init 202
        	// LocalVar  41 added: Name d18 Type D At 210 290 Range 210 499 Init 210
        	// LocalVar  31 added: Name d19 Type D At 641 178 Range 641 818 Init 641
        	// LocalVar  33 added: Name d20 Type D At 649 172 Range 649 820 Init 649
        	// LocalVar  35 added: Name d21 Type D At 657 166 Range 657 822 Init 657
        	// LocalVar  31 added: Name d22 Type D At 845 81 Range 845 925 Init 845
        	// LocalVar  33 added: Name d23 Type D At 852 79 Range 852 930 Init 852
        	// LocalVar  35 added: Name d24 Type D At 859 51 Range 859 909 Init 859
        	// LocalVar  37 added: Name d25 Type D At 866 49 Range 866 914 Init 866
        	// LocalVar  39 added: Name d26 Type D At 873 60 Range 873 932 Init 873
        	// LocalVar  41 added: Name d27 Type D At 880 48 Range 880 927 Init 880
        	// LocalVar  43 added: Name d28 Type D At 893 12 Range 893 904 Init 893
        	// LocalVar  45 added: Name d29 Type D At 906 31 Range 906 936 Init 906
        	// LocalVar  47 added: Name d30 Type D At 922 50 Range 922 971 Init 922
        	// LocalVar  49 added: Name d31 Type D At 938 47 Range 938 984 Init 938
        	// LocalVar  47 added: Name d32 Type D At 979 54 Range 979 1032 Init 979
        	// LocalVar  49 added: Name d33 Type D At 992 47 Range 992 1038 Init 992
        	// LocalVar  51 added: Name d34 Type D At 1047 24 Range 1047 1070 Init 1047
        	// LocalVar  47 name d32(D) merged out into d30(D)
        	// LocalVar  49 name d33(D) merged out into d31(D)
        	public boolean findIntersect(Curve curve, double ad[], double d, int i, int j, double d1, double d2, double d3, double d4, 
        			double d5, double d6, double d7, double d8, double d9, double d10, double d11, 
        			double d12) {
/*1035*/		if (d3 > d12 || d9 > d6)
/*1036*/			return false;
/*1038*/		if (Math.min(d2, d5) > Math.max(d8, d11) || Math.max(d2, d5) < Math.min(d8, d11))
/*1041*/			return false;
/*1047*/		if (d4 - d1 > 0.001D) {
/*1048*/			double d13 = (d1 + d4) / 2D;
/*1049*/			double d16 = XforT(d13);
/*1050*/			double d19 = YforT(d13);
/*1051*/			if (d13 == d1 || d13 == d4) {
/*1052*/				System.out.println((new StringBuilder()).append("s0 = ").append(d1).toString());
/*1053*/				System.out.println((new StringBuilder()).append("s1 = ").append(d4).toString());
/*1054*/				throw new InternalError("no s progress!");
        			}
/*1056*/			if (d10 - d7 > 0.001D) {
/*1057*/				double d22 = (d7 + d10) / 2D;
/*1058*/				double d24 = curve.XforT(d22);
/*1059*/				double d26 = curve.YforT(d22);
/*1060*/				if (d22 == d7 || d22 == d10) {
/*1061*/					System.out.println((new StringBuilder()).append("t0 = ").append(d7).toString());
/*1062*/					System.out.println((new StringBuilder()).append("t1 = ").append(d10).toString());
/*1063*/					throw new InternalError("no t progress!");
        				}
/*1065*/				if (d19 >= d9 && d26 >= d3 && findIntersect(curve, ad, d, i + 1, j + 1, d1, d2, d3, d13, d16, d19, d7, d8, d9, d22, d24, d26))
/*1069*/					return true;
/*1072*/				if (d19 >= d26 && findIntersect(curve, ad, d, i + 1, j + 1, d1, d2, d3, d13, d16, d19, d22, d24, d26, d10, d11, d12))
/*1076*/					return true;
/*1079*/				if (d26 >= d19 && findIntersect(curve, ad, d, i + 1, j + 1, d13, d16, d19, d4, d5, d6, d7, d8, d9, d22, d24, d26))
/*1083*/					return true;
/*1086*/				if (d6 >= d26 && d12 >= d19 && findIntersect(curve, ad, d, i + 1, j + 1, d13, d16, d19, d4, d5, d6, d22, d24, d26, d10, d11, d12))
/*1090*/					return true;
        			} else {
/*1094*/				if (d19 >= d9 && findIntersect(curve, ad, d, i + 1, j, d1, d2, d3, d13, d16, d19, d7, d8, d9, d10, d11, d12))
/*1098*/					return true;
/*1101*/				if (d12 >= d19 && findIntersect(curve, ad, d, i + 1, j, d13, d16, d19, d4, d5, d6, d7, d8, d9, d10, d11, d12))
/*1105*/					return true;
        			}
        		} else
/*1109*/		if (d10 - d7 > 0.001D) {
/*1110*/			double d14 = (d7 + d10) / 2D;
/*1111*/			double d17 = curve.XforT(d14);
/*1112*/			double d20 = curve.YforT(d14);
/*1113*/			if (d14 == d7 || d14 == d10) {
/*1114*/				System.out.println((new StringBuilder()).append("t0 = ").append(d7).toString());
/*1115*/				System.out.println((new StringBuilder()).append("t1 = ").append(d10).toString());
/*1116*/				throw new InternalError("no t progress!");
        			}
/*1118*/			if (d20 >= d3 && findIntersect(curve, ad, d, i, j + 1, d1, d2, d3, d4, d5, d6, d7, d8, d9, d14, d17, d20))
/*1122*/				return true;
/*1125*/			if (d6 >= d20 && findIntersect(curve, ad, d, i, j + 1, d1, d2, d3, d4, d5, d6, d14, d17, d20, d10, d11, d12))
/*1129*/				return true;
        		} else {
/*1134*/			double d15 = d5 - d2;
/*1135*/			double d18 = d6 - d3;
/*1136*/			double d21 = d11 - d8;
/*1137*/			double d23 = d12 - d9;
/*1138*/			double d25 = d8 - d2;
/*1139*/			double d27 = d9 - d3;
/*1140*/			double d28 = d21 * d18 - d23 * d15;
/*1141*/			if (d28 != 0.0D) {
/*1142*/				double d29 = 1.0D / d28;
/*1143*/				double d30 = (d21 * d27 - d23 * d25) * d29;
/*1144*/				double d31 = (d15 * d27 - d18 * d25) * d29;
/*1145*/				if (d30 >= 0.0D && d30 <= 1.0D && d31 >= 0.0D && d31 <= 1.0D) {
/*1146*/					d30 = d1 + d30 * (d4 - d1);
/*1147*/					d31 = d7 + d31 * (d10 - d7);
/*1148*/					if (d30 < 0.0D || d30 > 1.0D || d31 < 0.0D || d31 > 1.0D)
/*1149*/						System.out.println("Uh oh!");
/*1151*/					double d32 = (YforT(d30) + curve.YforT(d31)) / 2D;
/*1152*/					if (d32 <= ad[1] && d32 > ad[0]) {
/*1153*/						ad[1] = d32;
/*1154*/						return true;
        					}
        				}
        			}
        		}
/*1160*/		return false;
        	}

        	// Decompiling method: refineTforY  Signature: (DDD)D
        	// Max stack: 4, #locals: 13, #params: 7
        	// Code length: 77 bytes, Code offset: 12483
        	// Line Number Table found: 12 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 77 Range 0 76 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 77 Range 0 76 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 77 Range 0 76 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 77 Range 0 76 Init 0
        	// RetValue  13 added: Name <returnValue> Type D At 0 77 Range 0 76 Init 0 fixed
        	// LocalVar   7 added: Name d3 Type D At 1 72 Range 1 72 Init 1
        	// LocalVar   9 added: Name d4 Type D At 11 55 Range 11 65 Init 11
        	// LocalVar  11 added: Name d5 Type D At 37 21 Range 37 57 Init 37
        	public double refineTforY(double d, double d1, double d2) {
/*1164*/		double d3 = 1.0D;
/*1166*/		do {
/*1166*/			double d4 = (d + d3) / 2D;
/*1167*/			if (d4 == d || d4 == d3)
/*1168*/				return d3;
/*1170*/			double d5 = YforT(d4);
/*1171*/			if (d5 < d2) {
/*1172*/				d = d4;
/*1173*/				d1 = d5;
        			} else
/*1174*/			if (d5 > d2)
/*1175*/				d3 = d4;
/*1177*/			else
/*1177*/				return d3;
        		} while (true);
        	}

        	// Decompiling method: fairlyClose  Signature: (DD)Z
        	// Max stack: 6, #locals: 5, #params: 5
        	// Code length: 31 bytes, Code offset: 12642
        	// Line Number Table found: 1 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 31 Range 0 30 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 31 Range 0 30 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 31 Range 0 30 Init 0
        	// RetValue   5 added: Name <returnValue> Type Z At 0 31 Range 0 30 Init 0 fixed
        	public boolean fairlyClose(double d, double d1) {
/*1183*/		return Math.abs(d - d1) < Math.max(Math.abs(d), Math.abs(d1)) * 1E-010D;
        	}

        	// Parameter  0 added: Name this Type Lsun/awt/geom/Curve; At 0 0 Range 0 -1 Init 0 fixed
        	// Parameter  1 added: Name ad Type [D At 0 0 Range 0 -1 Init 0
        	// RetValue   2 added: Name <returnValue> Type I At 0 0 Range 0 -1 Init 0 fixed
        	public abstract int getSegment(double ad[]);
}
