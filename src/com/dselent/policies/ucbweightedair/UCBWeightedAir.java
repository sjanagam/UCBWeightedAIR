package com.dselent.policies.ucbweightedair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.dselent.policies.Action;
import com.dselent.policies.Policy;
import com.dselent.policies.RewardFunction;
import com.dselent.util.Pair;

public class UCBWeightedAir extends Policy
{

	public UCBWeightedAir()
	{
		super();

	}

	public UCBWeightedAir(Random random)
	{
		super(random);
	}
		
	/**
	 * 
	 * @param usedActionList list of all possible actions that have been taken already
	 * @param unusedActionList list of all actions that have not been taken yet
	 * @param actionHistoryMap list of all action-reward pairs for all reward functions
	 * @param weightMap desired weighting for each reward function
	 * @param missingValueMap desired values to use when the reward is missing
	 * @return Action
	 */
	@Override
	public Action chooseAction(List<Action> usedActionList, List<Action> unusedActionList, Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap, Map<RewardFunction, Double> weightMap, Map<RewardFunction, Double> missingValueMap)
	{
		Action finalAction = null;
		
		Map<RewardFunction, Map<Action, Double>> ucbValuesMap = new HashMap<RewardFunction, Map<Action, Double>>();
		Map<RewardFunction, Boolean> newActionMap = new HashMap<RewardFunction, Boolean>();
		
		//calculate ucb values for each reward function
		
		Set<RewardFunction> rewardFunctionKeySet = actionHistoryMap.keySet();

		for(RewardFunction rewardFunction : rewardFunctionKeySet)
		{
			List<Pair<Action, Double>> actionHistory = actionHistoryMap.get(rewardFunction);
			UCBAir air = new UCBAir(missingValueMap.get(rewardFunction));
			Map<Action, Double> ucbValues = air.calculateUCBAirValues(usedActionList, actionHistory);
			ucbValuesMap.put(rewardFunction, ucbValues);
			newActionMap.put(rewardFunction, air.isNewAction());
		}
		
		//determine if a new action should be chosen
		
		if(shouldChooseNewAction(unusedActionList, newActionMap, actionHistoryMap, weightMap))
		{
			//randomly pick a new action
			int randomUnusedActionIndex = random.nextInt(unusedActionList.size());
			finalAction = unusedActionList.get(randomUnusedActionIndex);
		}
		else
		{
			//for each action calculate weighted reward
			List<Pair<Action, Double>> rewardList = calculateWeightedRewardValues(usedActionList, actionHistoryMap, ucbValuesMap, weightMap);
			
			//if there are any actions
			if(rewardList.size() > 0)
			{
				//sort the list by value of pair
				//Collections.sort(rewardList, new PairComparator<Action, Double>());
				Collections.sort(rewardList, Comparator.comparing(Pair::getValue2));

				int actionIndex = rewardList.size()-1;
				finalAction = rewardList.get(actionIndex).getValue1();
			}			
		}

		return finalAction;
	}
	
	private boolean shouldChooseNewAction(List<Action> unusedActionList, Map<RewardFunction, Boolean> newActionMap, Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap, Map<RewardFunction, Double> weightMap)
	{
		boolean chooseNewAction = false;
		
		if(!unusedActionList.isEmpty())
		{
			boolean possibleNewAction = false;
			
			//quick check
			
			Set<RewardFunction> newActionKeySet = newActionMap.keySet();
			
			for(RewardFunction rewardFunction : newActionKeySet)
			{
				if(newActionMap.get(rewardFunction))
				{
					possibleNewAction = true;
				}
			}
			
			//long check
			
			if(possibleNewAction)
			{
				Set<RewardFunction> rewardFunctionKeySet = actionHistoryMap.keySet();
				Map<RewardFunction, Integer> countMap = getCountMap(actionHistoryMap);
				Map<RewardFunction, Double> countPercentMap =  new HashMap<RewardFunction, Double>();
				Map<RewardFunction, Double> multipliedWeightsMap = new HashMap<RewardFunction, Double>();
				Map<RewardFunction, Double> normalizedWeightsList =  new HashMap<RewardFunction, Double>();
				
				int totalCount = 0;
				double totalMultipliedWeight = 0.0;
				Double newActionValue = 0.0;
								
				for(RewardFunction rewardFunction : rewardFunctionKeySet)
				{
					totalCount = totalCount + countMap.get(rewardFunction);
				}
				
				for(RewardFunction rewardFunction : rewardFunctionKeySet)
				{
					double countPercent = (countMap.get(rewardFunction)*1.0)/totalCount;
					countPercentMap.put(rewardFunction, countPercent);
				}
			
				for(RewardFunction rewardFunction : rewardFunctionKeySet)
				{
					double multipliedWeight = countPercentMap.get(rewardFunction) * weightMap.get(rewardFunction);
					multipliedWeightsMap.put(rewardFunction, multipliedWeight);
					totalMultipliedWeight = totalMultipliedWeight + multipliedWeight;
				}
				
				for(RewardFunction rewardFunction : rewardFunctionKeySet)
				{
					double normalizedWeight = multipliedWeightsMap.get(rewardFunction) / totalMultipliedWeight;
					normalizedWeightsList.put(rewardFunction, normalizedWeight);
				}
				
				for(RewardFunction rewardFunction : rewardFunctionKeySet)
				{
					if(newActionMap.get(rewardFunction))
					{
						newActionValue = newActionValue + normalizedWeightsList.get(rewardFunction);
					}
				}
				
				if(newActionValue > 0.5 || newActionValue.equals(Double.NaN))
				{
					chooseNewAction = true;
				}
			}	
		}
		
		return chooseNewAction;
	}
	
	private List<Pair<Action, Double>> calculateWeightedRewardValues(List<Action> usedActionList, Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap, Map<RewardFunction, Map<Action, Double>> ucbValuesMap, Map<RewardFunction, Double> weightMap)
	{
		//adjusted weight = normalized weight * base weight | for a given action
		//normalized adjusted weight |  for a given action
		//adjusted reward = normalized adjusted weight * ucb value | for a given action
		//final reward = sum of all adjusted rewards
		
		Map<Action, Map<RewardFunction, Integer>> countMap = getCountMapActions(usedActionList, actionHistoryMap);
		Map<Action, Map<RewardFunction, Double>> normalizedCountMap = getNormalizedCountMap(countMap);
		Map<Action, Map<RewardFunction, Double>> adjustedWeightMap = getAdjustedWeightMap(normalizedCountMap, weightMap);
		Map<Action, Map<RewardFunction, Double>> normalizedAdjustedWeightMap = getNormalizedAdjustedWeightMap(adjustedWeightMap);
		Map<Action, Map<RewardFunction, Double>> adjustedRewardMap = getAdjustedRewardMap(normalizedAdjustedWeightMap, ucbValuesMap);
		
		return getWeightedRewardValues(adjustedRewardMap);

	}
	
	private Map<RewardFunction, Integer> getCountMap(Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap)
	{
		Map<RewardFunction, Integer> countMap = new HashMap<RewardFunction, Integer>();
	
		Set<RewardFunction> rewardFunctionKeySet = actionHistoryMap.keySet();
		
		for(RewardFunction rewardFunction : rewardFunctionKeySet)
		{
			List<Pair<Action, Double>> actionHistory = actionHistoryMap.get(rewardFunction);
			int count = 0;
			
			for(Pair<Action, Double> actionReward : actionHistory)
			{
				if(actionReward.getValue2() >= 0)
				{
					count++;
				}
			}
			
			countMap.put(rewardFunction, count);
		}
		
		return countMap;
	}
	
	private Map<Action, Map<RewardFunction, Integer>> getCountMapActions(List<Action> usedActionList, Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap)
	{
		Map<Action, Map<RewardFunction, Integer>> countMap = new HashMap<Action, Map<RewardFunction, Integer>>();
		
		Set<RewardFunction> rewardFunctionKeySet = actionHistoryMap.keySet();
		
		//initialize all counts to zero
		for(Action usedActionAction : usedActionList)
		{
			Map<RewardFunction, Integer> rewardCountMap = new HashMap<RewardFunction, Integer>();
			
			for(RewardFunction rewardFunction : rewardFunctionKeySet)
			{
				rewardCountMap.put(rewardFunction, 0);
			}
			
			countMap.put(usedActionAction, rewardCountMap);
		}
		
		//every action will have counts for all reward functions
		
		for(RewardFunction rewardFunction : rewardFunctionKeySet)
		{
			List<Pair<Action, Double>> actionHistory = actionHistoryMap.get(rewardFunction);
			
			for(Pair<Action, Double> actionReward : actionHistory)
			{
				Action currentAction = actionReward.getValue1();
				
				if(actionReward.getValue2() >= 0)
				{
					int currentCount = countMap.get(currentAction).get(rewardFunction);
					countMap.get(currentAction).put(rewardFunction, ++currentCount);
				}
			}
		}
			
		return countMap;
	}

	private Map<Action, Map<RewardFunction, Double>> getNormalizedCountMap(Map<Action, Map<RewardFunction, Integer>> countMap)
	{
		Map<Action, Map<RewardFunction, Double>> normalizedCountMap = new HashMap<Action, Map<RewardFunction, Double>>();
		
		Set<Action> actionKeySet = countMap.keySet();
		
		for(Action action : actionKeySet)
		{
			Map<RewardFunction, Double> normalizedRewardCountMap = new HashMap<RewardFunction, Double>();
			Map<RewardFunction, Integer> rewardCountMap = countMap.get(action);
			Set<RewardFunction> rewardFunctionKeySet = rewardCountMap.keySet();
			
			double total = 0.0;
			
			for(RewardFunction rewardFunction : rewardFunctionKeySet)
			{
				total = total + rewardCountMap.get(rewardFunction);
			}
			
			for(RewardFunction rewardFunction : rewardFunctionKeySet)
			{
				double normalizedReward = rewardCountMap.get(rewardFunction) / total;
				normalizedRewardCountMap.put(rewardFunction, normalizedReward);
			}
			
			normalizedCountMap.put(action, normalizedRewardCountMap);
		}
		
		return normalizedCountMap;
	}
	
	private Map<Action, Map<RewardFunction, Double>> getAdjustedWeightMap(Map<Action, Map<RewardFunction, Double>> normalizedCountMap, Map<RewardFunction, Double> weightMap)
	{
		Map<Action, Map<RewardFunction, Double>> adjustedWeightMap = new HashMap<Action, Map<RewardFunction, Double>>();
		
		Set<Action> actionKeySet = normalizedCountMap.keySet();
		
		for(Action action : actionKeySet)
		{
			Map<RewardFunction, Double> adjustedRewardWeightMap = new HashMap<RewardFunction, Double>();
			Map<RewardFunction, Double> normalizedWeightMap = normalizedCountMap.get(action);
			Set<RewardFunction> rewardFunctionKeySet = normalizedWeightMap.keySet();
						
			for(RewardFunction rewardFunction : rewardFunctionKeySet)
			{
				double adjustedWeight = normalizedWeightMap.get(rewardFunction) * weightMap.get(rewardFunction);
				adjustedRewardWeightMap.put(rewardFunction, adjustedWeight);
			}
			
			adjustedWeightMap.put(action, adjustedRewardWeightMap);
		}
		
		return adjustedWeightMap;
	}
	
	private Map<Action, Map<RewardFunction, Double>> getNormalizedAdjustedWeightMap(Map<Action, Map<RewardFunction, Double>> adjustedWeightMap)
	{
		Map<Action, Map<RewardFunction, Double>> normalizedAdjustedWeightMap = new HashMap<Action, Map<RewardFunction, Double>>();
		
		Set<Action> actionKeySet = adjustedWeightMap.keySet();
		
		for(Action action : actionKeySet)
		{
			Map<RewardFunction, Double> normalizedRewardAdjustedWeightMap = new HashMap<RewardFunction, Double>();
			Map<RewardFunction, Double> adjustedRewardWeightMap = adjustedWeightMap.get(action);
			Set<RewardFunction> rewardFunctionKeySet = adjustedRewardWeightMap.keySet();
			
			double total = 0.0;
			
			for(RewardFunction rewardFunction : rewardFunctionKeySet)
			{
				total = total + adjustedRewardWeightMap.get(rewardFunction);
			}
			
			for(RewardFunction rewardFunction : rewardFunctionKeySet)
			{
				double normalizedReward = adjustedRewardWeightMap.get(rewardFunction) / total;
				normalizedRewardAdjustedWeightMap.put(rewardFunction, normalizedReward);
			}
			
			normalizedAdjustedWeightMap.put(action, normalizedRewardAdjustedWeightMap);
		}
		
		return normalizedAdjustedWeightMap;
	}
	
	private Map<Action, Map<RewardFunction, Double>> getAdjustedRewardMap(Map<Action, Map<RewardFunction, Double>> normalizedAdjustedWeightMap, Map<RewardFunction, Map<Action, Double>> ucbValuesMap)
	{
		Map<Action, Map<RewardFunction, Double>> adjustedRewardMap = new HashMap<Action, Map<RewardFunction, Double>>();
		
		Set<Action> actionKeySet = normalizedAdjustedWeightMap.keySet();
		
		for(Action action : actionKeySet)
		{
			Map<RewardFunction, Double> adjustedRewardWeightMap = new HashMap<RewardFunction, Double>();
			Map<RewardFunction, Double> normalizedAdjustedRewardWeightMap = normalizedAdjustedWeightMap.get(action);
			Set<RewardFunction> rewardFunctionKeySet = normalizedAdjustedRewardWeightMap.keySet();
						
			for(RewardFunction rewardFunction : rewardFunctionKeySet)
			{
				double adjustedReward = 0.0;
				
				if(ucbValuesMap.get(rewardFunction).get(action) != null)
				{
					adjustedReward = normalizedAdjustedRewardWeightMap.get(rewardFunction) * ucbValuesMap.get(rewardFunction).get(action);
				}
				
				adjustedRewardWeightMap.put(rewardFunction, adjustedReward);
			}
			
			adjustedRewardMap.put(action, adjustedRewardWeightMap);
		}
		
		return adjustedRewardMap;
	}
	
	private List<Pair<Action, Double>> getWeightedRewardValues(Map<Action, Map<RewardFunction, Double>> adjustedRewardMap)
	{
		List<Pair<Action, Double>> weightedRewardValues = new ArrayList<Pair<Action, Double>>();
		
		Set<Action> actionKeySet = adjustedRewardMap.keySet();
		
		for(Action action : actionKeySet)
		{
			Set<RewardFunction> rewardFunctionKeySet = adjustedRewardMap.get(action).keySet();
			
			double totalReward = 0.0;
			
			for(RewardFunction rewardFunction : rewardFunctionKeySet)
			{
				totalReward = totalReward + adjustedRewardMap.get(action).get(rewardFunction);
			}
			
			Pair<Action, Double> actionReward = new Pair<Action, Double>(action, totalReward);
			weightedRewardValues.add(actionReward);
		}
		
		return weightedRewardValues;
	}
}
