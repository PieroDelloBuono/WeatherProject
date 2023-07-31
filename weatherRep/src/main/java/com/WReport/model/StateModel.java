package com.WReport.model;

public class StateModel {

	private String state;

	public StateModel() {
	}
	
	public StateModel(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "StateModel [state=" + state + "]";
	}
	
}
