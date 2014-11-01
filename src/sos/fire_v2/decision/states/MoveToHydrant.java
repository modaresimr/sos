package sos.fire_v2.decision.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sos.base.entities.FireBrigade;
import sos.base.entities.Hydrant;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.move.Move;
import sos.base.move.types.StandardMove;
import sos.base.util.SOSActionException;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.decision.FireInformationModel;
import sos.fire_v2.decision.tasks.FireRestTask;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;
import sos.tools.decisionMaker.implementations.targets.EmptyTarget;
import sos.tools.decisionMaker.implementations.tasks.StandardMoveToListTask;

public class MoveToHydrant extends SOSIState<FireInformationModel> {

	private SOSLoggerSystem log;
	private ArrayList<Hydrant> hydrants = new ArrayList<Hydrant>();
	private ArrayList<Hydrant> validHydrants = new ArrayList<Hydrant>();
	private Move move;
	
	public MoveToHydrant(FireInformationModel infoModel) {
		super(infoModel);
		hydrants.addAll(infoModel.getModel().Hydrants());
		validHydrants=getvalidHydrants(hydrants);
		log = new SOSLoggerSystem(infoModel.self().me(), "MoveToHydrant", true, OutputType.File, true, true);
		infoModel.self().sosLogger.addToAllLogType(log);
		move=infoModel.self().move;
		// TODO Auto-generated constructor stub
	}

	@Override
	public SOSTask decide(SOSEventPool eventPool) throws SOSActionException {
		// TODO Auto-generated method stub
		if (infoModel.getModel().refuges().isEmpty()) {
			if (((FireBrigadeAgent) infoModel.getAgent()).me().getWater() == 0) {
				moveToHydrant = true;
				log.info("MoveToHydrant="+moveToHydrant+"------> Water Tank is Empty");
			}

			if (((FireBrigadeAgent) infoModel.getAgent()).me().getWater() >= AbstractFireBrigadeAgent.maxWater) {
				moveToHydrant = false;
				Rest=false;
				validHydrants.clear();
				validHydrants=getvalidHydrants(hydrants);
				log.info("MoveToHydrant="+moveToHydrant+"-------> Water Tank is Full");
			}
			
			if (moveToHydrant) {
				if(Rest){
					log.info("3-");
					log.info("Rest="+Rest+"------- Fire is in Resting");

					if(lastTankWater==(((FireBrigade) infoModel.getAgent().me()).getWater())) {
						log.info("=======>>>> Warning, Water is not filling <<<<<==========");
						Rest=false;
						log.info("              Rest="+Rest+"--------Fire Exit from Resting");
						ArrayList<Hydrant> temp=selectHydrant(validHydrants);
						if (temp.size()>0){
							log.info("selected hydrant is===>"+temp.get(0));
							return new StandardMoveToListTask(temp, infoModel.getTime());
						}
						else
							return new StandardMoveToListTask(infoModel.getModel().Hydrants(), infoModel.getTime());
					}
					lastTankWater=(((FireBrigade) infoModel.getAgent().me()).getWater());
					return new FireRestTask(new EmptyTarget(), infoModel.getTime());
				}
				
				if ((((FireBrigadeAgent) infoModel.getAgent()).me().getPosition() instanceof Hydrant )){
					log.info("2-");
					Rest=true;
					log.info("Rest="+Rest+"------- Fire is in Resting");
					lastTankWater=(((FireBrigade) infoModel.getAgent().me()).getWater());
					return new FireRestTask(new EmptyTarget(), infoModel.getTime());
				}
				else {
					log.info("1- Fire brigade is searching for empty Hydrante");
					if (((FireBrigadeAgent) infoModel.getAgent()).model().Hydrants().size() > 0) { 
						ArrayList<Hydrant> temp=selectHydrant(validHydrants);
						log.info(temp);
						if (temp.size()>0){
							log.info("selected hydrant is===>"+temp.get(0));
							return new StandardMoveToListTask(temp, infoModel.getTime());
						}
						else
							return new StandardMoveToListTask(infoModel.getModel().Hydrants(), infoModel.getTime());
					}
				}
			}

		}
		
		
		
		

		
		return null;
	}

	
	private int lastTankWater=-1;
	private boolean moveToHydrant = false;
	private boolean Rest=false;

	private ArrayList<Hydrant> selectHydrant(ArrayList<Hydrant> ValidHydrants) {
		ArrayList<FireBrigade>visiblefirebrigade = infoModel.self().getVisibleEntities(FireBrigade.class);
		if (visiblefirebrigade.size() == 0)
			return ValidHydrants;
		
		sortHydrant(ValidHydrants);
		HYRANT: for (int i = 0; i < ValidHydrants.size(); i++) {
			for (int j = 0; j < visiblefirebrigade.size(); j++) {
				if (visiblefirebrigade.get(j).getPosition() instanceof Hydrant && visiblefirebrigade.get(j).distance(ValidHydrants.get(i).getAreaPosition()) < 20000) {
					ValidHydrants.remove(i);
					visiblefirebrigade.remove(j);
					i--;
					j--;
					continue HYRANT;
				}

			}
		}
		return ValidHydrants;

	}
	private ArrayList<Hydrant> getvalidHydrants(ArrayList<Hydrant> Hydrants){
		ArrayList<Hydrant> validhydrant = new ArrayList<Hydrant>();
//		select random random hydrant
		int myIndex=infoModel.getModel().me().getID().getValue()%2;
		for (int i = 0; i < Hydrants.size(); i++) {
			if (i%2==myIndex)
				validhydrant.add(Hydrants.get(i));
		}
		return validhydrant;
	}

	private void sortHydrant(ArrayList<Hydrant> hydrants) {
		Collections.sort(hydrants, new Comparator<Hydrant>() {

			@Override
			public int compare(Hydrant o1, Hydrant o2) {
				// TODO Auto-generated method stu
				
				long x1=move.getWeightTo(o1.getAreaPosition(), StandardMove.class);
				long x2=move.getWeightTo(o1.getAreaPosition(), StandardMove.class);
				return (int) (x1 - x2);
			}
		});
	}

	
	@Override
	public void giveFeedbacks(List feedbacks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void skipped() {
		// TODO Auto-generated method stub

	}

	@Override
	public void overTaken() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleEvent(SOSEvent sosEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
