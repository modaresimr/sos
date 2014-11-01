package sos.base.message.structure;

/**
 * 
 * @author Ali
 * 
 */
public class NoiseStructure implements MessageConstants {
			 public NoiseTypeStructure input = new NoiseTypeStructure();
			 public NoiseTypeStructure output = new NoiseTypeStructure();

			 class NoiseTypeStructure {
				  public NoiseUsageTypeStructure dropout = new NoiseUsageTypeStructure();
				  public NoiseUsageTypeStructure failure = new NoiseUsageTypeStructure();

				  @Override
				public String toString() {
						return "[dropout:" + dropout + "],[failure:" + failure + "]";
				  }
			 }

			 class NoiseUsageTypeStructure {
				  public boolean use = false;
				  public double probability = 0d;

				  @Override
				public String toString() {
						return "[use:" + use + ", probability:" + probability + "]";
				  }
			 }

			 @Override
			public String toString() {
				  return "[Noise-->[input:" + input + "]],[Noise-->[output:" + output + "]]";
			 }

		}
