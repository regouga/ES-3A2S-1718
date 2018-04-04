package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;

public class ReserveActivityState extends AdventureState {
	public static final int MAX_REMOTE_ERRORS = 5;
	@Override
	public State getState() {
		return State.RESERVE_ACTIVITY;
	}

	@Override
	public void process(Adventure adventure) {
		String reference;
		try {
			Client client =  adventure.getClient();
			reference = ActivityInterface.reserveActivity(adventure.getBegin(), adventure.getEnd(), client.getAge(), client.getNif(), client.getIban());
			adventure.setActivityConfirmation(reference);
		} catch (ActivityException ae) {
			adventure.setState(State.UNDO);
			return;
		} catch (RemoteAccessException rae) {
			incNumOfRemoteErrors();
			if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
				adventure.setState(State.UNDO);
			}
			return;
		}

		if (adventure.getBegin().equals(adventure.getEnd())) {
			if (adventure.needsCar()) {
				
				adventure.setState(State.RENT_VEHICLE);
				System.out.println(adventure.getState());
			} 
			else {
				adventure.addAmount((int)ActivityInterface.getActivityReservationData(reference).getAmount());
				adventure.setState(State.PROCESS_PAYMENT);
		
			}
		}
		else {
			adventure.setState(State.BOOK_ROOM);
		}
	}

}
