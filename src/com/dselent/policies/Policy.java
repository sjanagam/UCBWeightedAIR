package com.dselent.policies;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dselent.util.Pair;

public abstract class Policy
{
	protected Random random;

	public Policy()
	{
		random = new Random(7);
	}
	
	public Policy(Random random)
	{
		this.random = random;
	}
	
	public Random getRandom()
	{
		return random;
	}

	public void setRandom(Random random)
	{
		this.random = random;
	}
		
	public abstract Action chooseAction(List<Action> usedActionList, List<Action> unusedActionList, Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap, Map<RewardFunction, Double> weightMap, Map<RewardFunction, Double> missingValueMap);

}
