// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 3/29/2011 1:42:59 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) definits fieldsfirst noinners ansi nonlb space lnc safe debugmode 
// Source File Name:   Crossings.java
// Class Version:      49.0

package sos.tools.geom;

// Referenced classes of package sun.awt.geom:
//			Crossings

// flag ACC_SUPER is set
public final class EvenOdd extends Crossings {
	// Constants: 33
	// Interfaces: 0
	// Fields: 0
	// Methods: 3
	// Class Attributes: 2

	// Decompiling method: <init> Signature: (DDDD)V
	// Max stack: 9, #locals: 9, #params: 9
	// Code length: 11 bytes, Code offset: 388
	// Line Number Table found: 2 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings$EvenOdd; At 0
	// 11 Range 0 10 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 11 Range 0 10 Init 0
	// Parameter 3 added: Name d1 Type D At 0 11 Range 0 10 Init 0
	// Parameter 5 added: Name d2 Type D At 0 11 Range 0 10 Init 0
	// Parameter 7 added: Name d3 Type D At 0 11 Range 0 10 Init 0
	// RetValue 9 added: Name <returnValue> Type V At 0 11 Range 0 10 Init 0
	// fixed
	public EvenOdd(double d, double d1, double d2, double d3) {
		/* 294 */super(d, d1, d2, d3);
		/* 295 *//* return; */
	}

	// Decompiling method: covers Signature: (DD)Z
	// Max stack: 4, #locals: 5, #params: 5
	// Code length: 36 bytes, Code offset: 441
	// Line Number Table found: 1 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings$EvenOdd; At 0
	// 36 Range 0 35 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 36 Range 0 35 Init 0
	// Parameter 3 added: Name d1 Type D At 0 36 Range 0 35 Init 0
	// RetValue 5 added: Name <returnValue> Type Z At 0 36 Range 0 35 Init 0
	// fixed
	public final boolean covers(double d, double d1) {
		/* 298 */return limit == 2 && yranges[0] <= d && yranges[1] >= d1;
	}

	// Decompiling method: record Signature: (DDI)V
	// Max stack: 6, #locals: 20, #params: 6
	// Code length: 370 bytes, Code offset: 515
	// Line Number Table found: 52 entries
	// Parameter 0 added: Name this Type Lsun/awt/geom/Crossings$EvenOdd; At 0
	// 370 Range 0 369 Init 0 fixed
	// Parameter 1 added: Name d Type D At 0 370 Range 0 369 Init 0
	// Parameter 3 added: Name d1 Type D At 0 370 Range 0 369 Init 0
	// Parameter 5 added: Name i Type I At 0 370 Range 0 369 Init 0
	// RetValue 20 added: Name <returnValue> Type V At 0 370 Range 0 369 Init 0
	// fixed
	// LocalVar 6 added: Name flag Type Z At 8 285 Range 8 292 Init 8
	// LocalVar 6 chged: Name j Oname flag Type I At 10 2 Range 8 292 Init 8
	// LocalVar 7 added: Name k Type I At 41 246 Range 41 286 Init 41
	// LocalVar 8 added: Name d2 Type D At 62 71 Range 62 132 Init 62
	// LocalVar 10 added: Name d3 Type D At 74 83 Range 74 156 Init 74
	// LocalVar 12 added: Name d4 Type D At 122 94 Range 122 215 Init 122
	// LocalVar 14 added: Name d5 Type D At 126 102 Range 126 227 Init 126
	// LocalVar 16 added: Name d6 Type D At 146 85 Range 146 230 Init 146
	// LocalVar 18 added: Name d7 Type D At 150 84 Range 150 233 Init 150
	// LocalVar 7 added: Name l Type I At 295 71 Range 295 365 Init 295
	// LocalVar 8 added: Name ad Type [D At 320 18 Range 320 337 Init 320
	// LocalVar 9 hasn't been used
	// LocalVar 7 name l(I) merged out into k(I)
	public void record(double d, double d1, int i) {
		/* 302 */if (d >= d1)
			/* 303 */return;
		int j;
		/* 305 */for (j = 0; j < limit && d > yranges[j + 1]; j += 2)
			;
		/* 310 */int k = j;
		/* 311 */label0:
		/* 311 */do {
			double d2;
			double d3;
			/* 311 */do {
				/* 311 */if (j >= limit)
					/* 312 */break label0;
				/* 312 */d2 = yranges[j++];
				/* 313 */d3 = yranges[j++];
				/* 314 */if (d1 >= d2)
					/* 316 */break;
				/* 316 */yranges[k++] = d;
				/* 317 */yranges[k++] = d1;
				/* 318 */d = d2;
				/* 319 */d1 = d3;
			} while (true);
			double d4;
			double d5;
			/* 324 */if (d < d2) {
				/* 325 */d4 = d;
				/* 326 */d5 = d2;
			} else {
				/* 328 */d4 = d2;
				/* 329 */d5 = d;
			}
			double d6;
			double d7;
			/* 331 */if (d1 < d3) {
				/* 332 */d6 = d1;
				/* 333 */d7 = d3;
			} else {
				/* 335 */d6 = d3;
				/* 336 */d7 = d1;
			}
			/* 338 */if (d5 == d6) {
				/* 339 */d = d4;
				/* 340 */d1 = d7;
			} else {
				/* 342 */if (d5 > d6) {
					/* 343 */d = d6;
					/* 344 */d6 = d5;
					/* 345 */d5 = d;
				}
				/* 347 */if (d4 != d5) {
					/* 348 */yranges[k++] = d4;
					/* 349 */yranges[k++] = d5;
				}
				/* 351 */d = d6;
				/* 352 */d1 = d7;
			}
		} while (d < d1);
		/* 358 */if (k < j && j < limit)
			/* 359 */System.arraycopy(((Object) (yranges)), j,
					((Object) (yranges)), k, limit - j);
		/* 361 */k += limit - j;
		/* 362 */if (d < d1) {
			/* 363 */if (k >= yranges.length) {
				/* 364 */double ad[] = new double[k + 10];
				/* 365 */System.arraycopy(((Object) (yranges)), 0,
						((Object) (ad)), 0, k);
				/* 366 */yranges = ad;
			}
			/* 368 */yranges[k++] = d;
			/* 369 */yranges[k++] = d1;
		}
		/* 371 */limit = k;
		/* 372 *//* return; */
	}
}
