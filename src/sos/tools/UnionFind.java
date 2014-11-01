package sos.tools;

/**Ba index az [0..n) kar mikone Set union set mikone ke index x,y ba ham too ye union an ...
 * va in SameSet moshakhas mikone ke aya x, y too ye union hastan ya na ...
 * Size ham Size unioni ke ye index toosh gharar dare ro bar migardoone ... 
 * */

//Angeh*************************************************************************************************************
/** a Disjoint set Data structure ... */
public class UnionFind {
	private short data[];
	
	public UnionFind(int n) {
		data = new short[n + 10];
		for (int i = 0; i < n + 10; ++i)
			data[i] = -1;
	}
	
	// Angeh*************************************************************************************************************
	public boolean setUnion(short x, short y) {
		x = root(x);
		y = root(y);
		if (x != y) {
			if (data[x] > data[y]){
				short temp = x;
				x = y;
				y = temp;
			}
			data[x] += data[y];
			data[y] = x;
		}
		return x != y;
	}
	
	// Angeh*************************************************************************************************************
	private short root(short x) {
		return data[x] < 0 ? x : (data[x] = root(data[x]));
	}
	
	// Angeh*************************************************************************************************************
	public boolean inSameSet(short x, short y) {
		return root(x) == root(y);
	}
	
	// Angeh*************************************************************************************************************
	public int size(short x) {
		return -data[root(x)];
	}
}
