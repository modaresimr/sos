package sos.base.move;

public interface MoveConstants {
	public static final int DIVISION_UNIT = 1;
	public static final long DIVISION_UNIT_FOR_GET = 3000;
	public static final int UNREACHABLE_COST_FOR_GRAPH_WEIGTHING = Integer.MAX_VALUE/4;
	public static final int UNREACHABLE_COST =  (int) ((UNREACHABLE_COST_FOR_GRAPH_WEIGTHING-DIVISION_UNIT_FOR_GET)/DIVISION_UNIT_FOR_GET);
	public static final int TRAFFIC_CHECKING_DISTANCE = 3000;//ALI
	public static final int ENTRACE_DISTANCE_MM= 2000;
	
	public static final int AVERAGE_MOVE_PER_CYCLE=34000*5;//FIXME
}
