package com.dselent.policies.ucbweightedair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dselent.policies.Action;
import com.dselent.util.Pair;

public class UCBAir
{
	private double defaultRewardValue;
	private boolean newAction;
	
	public UCBAir(double defaultRewardValue)
	{
		this.defaultRewardValue = defaultRewardValue;
		newAction = false;
	}
	
	public double getDefaultRewardValue()
	{
		return defaultRewardValue;
	}

	public void setDefaultRewardValue(double defaultRewardValue)
	{
		this.defaultRewardValue = defaultRewardValue;
	}

	public boolean isNewAction()
	{
		return newAction;
	}

	public void setNewAction(boolean newAction)
	{
		this.newAction = newAction;
	}
	
	/**
	 * 
	 * Takes the reward history for the actions and return a list of actions and UCB values
	 * Assumes rewards are (0, 1)
	 * 
	 * @param actionList The list of all possible actions
	 * @param actionHistory The action history = list of actions and prior reward values = past data
	 * @return A map of actions and UCB values
	 */
	public Map<Action, Double> calculateUCBAirValues(List<Action> actionList, List<Pair<Action, Double>> actionHistory)
	{
		newAction = false;
		
		//actionHistory must contain blank rewards which can be interpreted as missing
		//what is missing reward value, -1 will work since rewards must be (0, 1)
		
		Map<Action, Double> ucbValues = new HashMap<Action, Double>();
		
		//number of unique actions tried so far = triedActions.size()
		//time step = actionHistory.size()
		
		//this map only contains actions that have been tried
		Map<Action, UCBAirActionData> actionDataMap = new HashMap<Action, UCBAirActionData>();
		Set<Action> triedActions = new HashSet<Action>();
		
		//create ActionData objects and their map to actions
		
		for(Action action : actionList)
		{
			UCBAirActionData actionData = new UCBAirActionData(action);
			actionDataMap.put(action, actionData);
		}

		//loop through history
		for(Pair<Action, Double> actionReward : actionHistory)
		{
			Action actionKey = actionReward.getValue1();
			Double rewardValue = actionReward.getValue2();
			
			UCBAirActionData actionDataValue = actionDataMap.get(actionKey);
			actionDataValue.addReward(rewardValue);
			triedActions.add(actionKey);
			
		}
		
		//loop through action data and replace all -1.0 (null rewards) with the current average
		//must be done after initial loop
		for(Action triedAction : triedActions)
		{
			UCBAirActionData actionDataValue = actionDataMap.get(triedAction);
			actionDataValue.replaceMissingRewards(defaultRewardValue);
		}
		
		double bestMean = -1.0;
		
		for(Action triedAction : triedActions)
		{
			double reward = actionDataMap.get(triedAction).calculateUCBValue(actionHistory.size());
			ucbValues.put(triedAction, reward);
			
			if(reward > bestMean)
			{
				bestMean = reward;
			}
		}
		
		//if beta = undefined -> new arm
		Double beta = calculateActionBeta(actionDataMap);		
		Double upperBound = Math.pow(actionHistory.size(), beta/(beta+1.0));
		
		if(beta < 1 && bestMean < 1)
		{
			upperBound = Math.pow(actionHistory.size(), beta/2);
		}
		
		if(beta.equals(Double.NaN) || upperBound.equals(Double.NaN) || triedActions.size() < upperBound)
		{
			newAction = true;
		}
				
		return ucbValues;
	}
	
	
	private double calculateActionBeta(Map<Action, UCBAirActionData> actionDataMap)
	{
		//mean of means
		//action means
		//variance of means
		
		double beta = 1.0;
		
		double sum = 0.0;
		double mean = 0.0;
		double variance = 0.0;
		
		Set<Action> actionDataKeys = actionDataMap.keySet();
		
		//mean of actions
		for(Action actionDataKey : actionDataKeys)
		{
			UCBAirActionData actionData = actionDataMap.get(actionDataKey);
			sum = sum + actionData.calculateAverageReward();
		}
		
		mean = sum / actionDataKeys.size();
		
		double sumSquaredDifference = 0.0;
		
		//variance of actions
		for(Action actionDataKey : actionDataKeys)
		{
			UCBAirActionData actionData = actionDataMap.get(actionDataKey);
			sumSquaredDifference = sumSquaredDifference + Math.pow((actionData.calculateAverageReward()-mean), 2);
		}
		
		variance = sumSquaredDifference / actionDataKeys.size();
		
		beta = ( (variance + Math.pow(mean, 2) - mean) * (mean - 1) ) / variance;
		
		return beta;
	}
	

}
