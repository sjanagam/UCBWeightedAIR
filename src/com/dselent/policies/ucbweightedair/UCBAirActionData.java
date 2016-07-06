package com.dselent.policies.ucbweightedair;

import java.util.ArrayList;
import java.util.List;

import com.dselent.policies.Action;

//caller should check for positive return values from functions
class UCBAirActionData
{
	private Action action;
	private List<Double> rewardList;
	
	protected UCBAirActionData(Action action)
	{
		this.action = action;
		rewardList = new ArrayList<Double>();
	}
	
	protected Action getAction()
	{
		return action;
	}
	
	protected void setAction(Action action)
	{
		this.action = action;
	}
		
	protected List<Double> getRewardList()
	{
		return rewardList;
	}
	
	protected void setRewardList(List<Double> rewardList)
	{
		this.rewardList = rewardList;
	}
	
	protected void addReward(double reward)
	{
		rewardList.add(reward);
	}
	
	protected void replaceMissingRewards(double defaultReward)
	{
		double replaceValue = defaultReward;
		double average = calculateAverageReward();
		
		if(average >= 0)
		{
			replaceValue = average;
		}
		
		 for(int i=0; i<rewardList.size(); i++)
		 {
			 Double reward = rewardList.get(i);
			 
			 if(reward < 0)
			 {
				 rewardList.set(i, replaceValue);
			 }
		 }
	}
	
	//ignored missing values (<0)
	protected double calculateTotalReward()
	{
		double totalReward = 0.0;
		
		 for(Double reward : rewardList)
		 {
			 if(reward >= 0)
			 {
				 totalReward = totalReward + reward;
			 }
		 }
		
		return totalReward;
	}
	
	//ignored missing values (<0)
	protected double calculateRewardCount()
	{
		int rewardCount = 0;
		
		 for(Double reward : rewardList)
		 {
			 if(reward >= 0)
			 {
				 rewardCount++;
			 }
		 }
		
		return rewardCount;
	}
	
	protected double calculateAverageReward()
	{
		double average = -1.0;
		
		if(rewardList.size() > 0)
		{
			double totalReward = 0.0;
			int rewardCount = 0;
			
			 for(Double reward : rewardList)
			 {
				 if(reward >= 0)
				 {
					 totalReward = totalReward + reward;
					 rewardCount++;
				 }
			 }
			 
			 average = totalReward / rewardCount;
		}
		return average;
	}
	
	//Functions below assume missing values have been replaced
	
	protected double calculateVariance()
	{
		double variance = -1.0;
		
		if(rewardList.size() > 0)
		{
			double average = calculateAverageReward();
			double sumSquaredDifference = 0.0;
			
			for(Double reward : rewardList)
			{
				sumSquaredDifference = sumSquaredDifference + Math.pow((reward-average), 2);
			}
			
			variance = sumSquaredDifference / rewardList.size();
		}
		
		return variance;
		
	}
	
	protected double calculateExplorationSequence(int timeStep)
	{
		//2log(10logt) <= et <= log(t)
		return 2 * Math.log10(10 * Math.log10(timeStep));
	}
	
	protected double calculateBiasSequence(int timeStep)
	{
		double biasSequence = -1.0;
		double tuningConstant = 0.5; //3.0;
		
		if(rewardList.size() > 0)
		{
			double term1 = (2 * calculateVariance() * calculateExplorationSequence(timeStep)) / rewardList.size();
			double term2 = tuningConstant * calculateExplorationSequence(timeStep) / rewardList.size();
			biasSequence = Math.sqrt(term1) + term2;
		}
		
		return biasSequence;
	}
	
	protected double calculateUCBValue(int timeStep)
	{
		return calculateAverageReward() + calculateBiasSequence(timeStep);
	}
	
}
