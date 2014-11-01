package sos.tools.geom;


// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 3/29/2011 1:43:11 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) definits fieldsfirst noinners ansi nonlb space lnc safe debugmode 
// Source File Name:   Crossings.java
// Class Version:      49.0



// Referenced classes of package sun.awt.geom:
//			Crossings

// flag ACC_SUPER is set
public final class NonZero extends Crossings {
	// Constants:          51
	// Interfaces:         0
	// Fields:             1
	// Methods:            5
	// Class Attributes:   2


        	private int crosscounts[] = null;

        	// Decompiling method: <init>  Signature: (DDDD)V
        	// Max stack: 9, #locals: 9, #params: 9
        	// Code length: 24 bytes, Code offset: 524
        	// Line Number Table found: 3 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Crossings$NonZero; At 0 24 Range 0 23 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 24 Range 0 23 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 24 Range 0 23 Init 0
        	// Parameter  5 added: Name d2 Type D At 0 24 Range 0 23 Init 0
        	// Parameter  7 added: Name d3 Type D At 0 24 Range 0 23 Init 0
        	// RetValue   9 added: Name <returnValue> Type V At 0 24 Range 0 23 Init 0 fixed
        	public NonZero(double d, double d1, double d2, double d3) {
/* 379*/		super(d, d1, d2, d3);
/* 380*/		crosscounts = new int[yranges.length / 2];
/* 381*/		/* return; */
        	}

        	// Decompiling method: covers  Signature: (DD)Z
        	// Max stack: 4, #locals: 10, #params: 5
        	// Code length: 82 bytes, Code offset: 594
        	// Line Number Table found: 13 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Crossings$NonZero; At 0 82 Range 0 81 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 82 Range 0 81 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 82 Range 0 81 Init 0
        	// RetValue  10 added: Name <returnValue> Type Z At 0 82 Range 0 81 Init 0 fixed
        	// LocalVar   5 added: Name flag Type Z At 1 32 Range 1 32 Init 1
        	// LocalVar   5 chged: Name i Oname flag Type I At 3 2 Range 1 32 Init 1
        	// LocalVar   6 added: Name d2 Type D At 22 27 Range 22 48 Init 22
        	// LocalVar   8 added: Name d3 Type D At 34 32 Range 34 65 Init 34
        	public final boolean covers(double d, double d1) {
/* 384*/		int i = 0;
/* 385*/		do {
/* 385*/			if (i >= limit)
/* 386*/				break;
/* 386*/			double d2 = yranges[i++];
/* 387*/			double d3 = yranges[i++];
/* 388*/			if (d < d3) {
/* 391*/				if (d < d2)
/* 392*/					return false;
/* 394*/				if (d1 <= d3)
/* 395*/					return true;
/* 397*/				d = d3;
        			}
        		} while (true);
/* 399*/		return d >= d1;
        	}

        	// Decompiling method: remove  Signature: (I)V
        	// Max stack: 6, #locals: 3, #params: 2
        	// Code length: 60 bytes, Code offset: 762
        	// Line Number Table found: 6 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Crossings$NonZero; At 0 60 Range 0 59 Init 0 fixed
        	// Parameter  1 added: Name i Type I At 0 60 Range 0 59 Init 0
        	// RetValue   3 added: Name <returnValue> Type V At 0 60 Range 0 59 Init 0 fixed
        	// LocalVar   2 added: Name j Type I At 16 38 Range 16 53 Init 16
        	public void remove(int i) {
/* 403*/		limit -= 2;
/* 404*/		int j = limit - i;
/* 405*/		if (j > 0) {
/* 406*/			System.arraycopy(((Object) (yranges)), i + 2, ((Object) (yranges)), i, j);
/* 407*/			System.arraycopy(((Object) (crosscounts)), i / 2 + 1, ((Object) (crosscounts)), i / 2, j / 2);
        		}
/* 411*/		/* return; */
        	}

        	// Decompiling method: insert  Signature: (IDDI)V
        	// Max stack: 6, #locals: 10, #params: 7
        	// Code length: 167 bytes, Code offset: 880
        	// Line Number Table found: 16 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Crossings$NonZero; At 0 167 Range 0 166 Init 0 fixed
        	// Parameter  1 added: Name i Type I At 0 167 Range 0 166 Init 0
        	// Parameter  2 added: Name d Type D At 0 167 Range 0 166 Init 0
        	// Parameter  4 added: Name d1 Type D At 0 167 Range 0 166 Init 0
        	// Parameter  6 added: Name j Type I At 0 167 Range 0 166 Init 0
        	// RetValue  10 added: Name <returnValue> Type V At 0 167 Range 0 166 Init 0 fixed
        	// LocalVar   7 added: Name k Type I At 6 116 Range 6 121 Init 6
        	// LocalVar   8 added: Name ad Type [D At 12 81 Range 12 92 Init 12
        	// LocalVar   9 added: Name ai Type [I At 18 90 Range 18 107 Init 18
        	public void insert(int i, double d, double d1, int j) {
/* 414*/		int k = limit - i;
/* 415*/		double ad[] = yranges;
/* 416*/		int ai[] = crosscounts;
/* 417*/		if (limit >= yranges.length) {
/* 418*/			yranges = new double[limit + 10];
/* 419*/			System.arraycopy(((Object) (ad)), 0, ((Object) (yranges)), 0, i);
/* 420*/			crosscounts = new int[(limit + 10) / 2];
/* 421*/			System.arraycopy(((Object) (ai)), 0, ((Object) (crosscounts)), 0, i / 2);
        		}
/* 423*/		if (k > 0) {
/* 424*/			System.arraycopy(((Object) (ad)), i, ((Object) (yranges)), i + 2, k);
/* 425*/			System.arraycopy(((Object) (ai)), i / 2, ((Object) (crosscounts)), i / 2 + 1, k / 2);
        		}
/* 429*/		yranges[i + 0] = d;
/* 430*/		yranges[i + 1] = d1;
/* 431*/		crosscounts[i / 2] = j;
/* 432*/		limit += 2;
/* 433*/		/* return; */
        	}

        	// Decompiling method: record  Signature: (DDI)V
        	// Max stack: 7, #locals: 15, #params: 6
        	// Code length: 357 bytes, Code offset: 1145
        	// Line Number Table found: 45 entries
        	// Parameter  0 added: Name this Type Lsun/awt/geom/Crossings$NonZero; At 0 357 Range 0 356 Init 0 fixed
        	// Parameter  1 added: Name d Type D At 0 357 Range 0 356 Init 0
        	// Parameter  3 added: Name d1 Type D At 0 357 Range 0 356 Init 0
        	// Parameter  5 added: Name i Type I At 0 357 Range 0 356 Init 0
        	// RetValue  15 added: Name <returnValue> Type V At 0 357 Range 0 356 Init 0 fixed
        	// LocalVar   6 added: Name flag Type Z At 8 341 Range 8 348 Init 8
        	// LocalVar   6 chged: Name j Oname flag Type I At 10 2 Range 8 348 Init 8
        	// LocalVar   7 added: Name k Type I At 57 280 Range 57 336 Init 57
        	// LocalVar   8 added: Name d2 Type D At 68 171 Range 68 238 Init 68
        	// LocalVar  10 added: Name d3 Type D At 79 256 Range 79 334 Init 79
        	// LocalVar   8 added: Name d4 Type D At 249 2 Range 249 250 Init 249
        	// LocalVar  12 added: Name l Type I At 256 34 Range 256 289 Init 256
        	// LocalVar  13 added: Name d5 Type D At 264 52 Range 264 315 Init 264
        	// LocalVar   8 added: Name d6 Type D At 317 16 Range 317 332 Init 317
        	// LocalVar   8 name d4(D) merged out into d2(D)
        	// LocalVar   8 name d6(D) merged out into d2(D)
        	public void record(double d, double d1, int i) {
/* 436*/		if (d >= d1)
/* 437*/			return;
        		int j;
/* 439*/		for (j = 0; j < limit && d > yranges[j + 1]; j += 2);
/* 444*/		if (j < limit) {
/* 445*/			int k = crosscounts[j / 2];
/* 446*/			double d2 = yranges[j + 0];
/* 447*/			double d3 = yranges[j + 1];
/* 448*/			if (d3 == d && k == i) {
/* 454*/				if (j + 2 == limit) {
/* 455*/					yranges[j + 1] = d1;
/* 456*/					return;
        				}
/* 458*/				remove(j);
/* 459*/				d = d2;
/* 460*/				k = crosscounts[j / 2];
/* 461*/				d2 = yranges[j + 0];
/* 462*/				d3 = yranges[j + 1];
        			}
/* 464*/			if (d1 < d2) {
/* 466*/				insert(j, d, d1, i);
/* 467*/				return;
        			}
/* 469*/			if (d1 == d2 && k == i) {
/* 471*/				yranges[j] = d;
/* 472*/				return;
        			}
/* 475*/			if (d < d2) {
/* 476*/				insert(j, d, d2, i);
/* 477*/				j += 2;
/* 478*/				d = d2;
        			} else
/* 479*/			if (d2 < d) {
/* 480*/				insert(j, d2, d, k);
/* 481*/				j += 2;
/* 482*/				d2 = d;
        			}
/* 485*/			int l = k + i;
/* 486*/			double d4 = Math.min(d1, d3);
/* 487*/			if (l == 0) {
/* 488*/				remove(j);
        			} else {
/* 490*/				crosscounts[j / 2] = l;
/* 491*/				yranges[j++] = d;
/* 492*/				yranges[j++] = d4;
        			}
/* 494*/			d = d2 = d4;
/* 495*/			if (d2 < d3)
/* 496*/				insert(j, d2, d3, k);
        		}
/* 499*/		if (d < d1)
/* 500*/			insert(j, d, d1, i);
/* 502*/		/* return; */
        	}
}
