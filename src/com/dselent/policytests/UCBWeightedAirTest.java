package com.dselent.policytests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.dselent.policies.Action;
import com.dselent.policies.RewardFunction;
import com.dselent.policies.ucbweightedair.UCBWeightedAir;
import com.dselent.util.Pair;

public class UCBWeightedAirTest
{
	private int testRuns;
	private int minUniqueActions;
	private int maxUniqueActions;
	private int minActionHistory;
	private int maxActionHistory;
	private int minRewardFunctions;
	private int maxRewardFunctions;
		
	private Random random;
	
	public UCBWeightedAirTest()
	{
		testRuns = 100000;
		minUniqueActions = 1;
		maxUniqueActions = 100;
		minActionHistory = 0;
		maxActionHistory = 10000;
		minRewardFunctions = 1;
		maxRewardFunctions = 2;
		
		random = new Random(7);
	}
	
	public void runTests()
	{
		for(int i=0; i<testRuns; i++)
		{
			
			int uniqueActionsCount = random.nextInt(maxUniqueActions) + minUniqueActions;
			int actionHistoryCount = random.nextInt(maxActionHistory) + minActionHistory;
			int rewardFunctionCount =  random.nextInt(maxRewardFunctions) + minRewardFunctions;
			
			double[] weights = new double[rewardFunctionCount];
			double[] missingValueDefaults = new double[rewardFunctionCount];
			
			double totalWeight = 0.0;
			
			for(int j=0; j<weights.length; j++)
			{
				weights[j] = random.nextDouble();
				totalWeight = totalWeight + weights[j];
			}
			
			for(int j=0; j<weights.length; j++)
			{
				weights[j] = weights[j] / totalWeight;
			}
			
			for(int j=0; j<missingValueDefaults.length; j++)
			{
				missingValueDefaults[j] = random.nextDouble();
			}
			
			Action[] uniqueActions = new Action[uniqueActionsCount];
			Set<Action> unusedActionSet = new HashSet<Action>();
			Set<Action> usedActionSet = new HashSet<Action>();
			
			for(int j=0; j<uniqueActionsCount; j++)
			{
				uniqueActions[j] = new Action(j);
				unusedActionSet.add(uniqueActions[j]);
			}
			
			RewardFunction[] rewardFunctions = new RewardFunction[rewardFunctionCount];
			
			for(int j=0; j<rewardFunctionCount; j++)
			{
				if(j==0)
				{
					rewardFunctions[j] = new RewardFunction("STUDENT_RATING");
				}
				else
				{
					rewardFunctions[j] = new RewardFunction("NEXT_QUESTION_CORRECTNESS");
				}
			}
			
					
			List<Action> usedActionList = new ArrayList<Action>();
			List<Action> unusedActionList = new ArrayList<Action>();
			Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap = new HashMap<RewardFunction, List<Pair<Action, Double>>>();
			Map<RewardFunction, Double> weightMap = new HashMap<RewardFunction, Double>();
			Map<RewardFunction, Double> missingValueMap = new HashMap<RewardFunction, Double>();
			
			for(int j=0; j<rewardFunctionCount; j++)
			{
				weightMap.put(rewardFunctions[j], weights[j]);
				missingValueMap.put(rewardFunctions[j], missingValueDefaults[j]);
				
				List<Pair<Action, Double>> rewardList = new ArrayList<Pair<Action, Double>>();
				actionHistoryMap.put(rewardFunctions[j], rewardList);
			}
	
			
			
			for(int j=0; j<actionHistoryCount; j++)
			{
				int actionIndex = random.nextInt(uniqueActions.length);
				Action action = uniqueActions[actionIndex];
				
				for(int k=0; k<rewardFunctionCount; k++)
				{
					double reward = random.nextDouble();

					int missing = random.nextInt(100);
					
					if(missing > 80)
					{
						reward = -1.0;
					}
					
					Pair<Action, Double> actionReward = new Pair<Action, Double>(action, reward);
					actionHistoryMap.get(rewardFunctions[k]).add(actionReward);
					
					usedActionSet.add(action);
					unusedActionSet.remove(action);
				}
			}
			
			usedActionList = new ArrayList<Action>(usedActionSet);
			unusedActionList = new ArrayList<Action>(unusedActionSet);
			
			UCBWeightedAir weightedAir = new UCBWeightedAir(random);
		
			@SuppressWarnings("unused")
			Action chosenAction = weightedAir.chooseAction(usedActionList, unusedActionList, actionHistoryMap, weightMap, missingValueMap);
			
			if(i % 1000 == 0)
			{
				System.out.println(i);
			}
		}
		
	}
	
	public void test1()
	{
		for(int runs=0; runs<10; runs++)
		{
			double[] weights = {0.4, 0.6};
			double[] missingValueDefaults = {0.5, 0.7};
	
			int uniqueActionsCount = 5;
			
			Action[] uniqueActions = new Action[uniqueActionsCount];
			Set<Action> unusedActionSet = new HashSet<Action>();
			Set<Action> usedActionSet = new HashSet<Action>();
			
			for(int j=0; j<uniqueActionsCount; j++)
			{
				uniqueActions[j] = new Action(j);
				unusedActionSet.add(uniqueActions[j]);
			}
			
			RewardFunction studentRating = new RewardFunction("STUDENT_RATING");
			RewardFunction nextQuestionCorrectness = new RewardFunction("NEXT_QUESTION_CORRECTNESS");
			
			RewardFunction[] rewardFunctions = {studentRating, nextQuestionCorrectness};
							
			List<Action> usedActionList = new ArrayList<Action>();
			List<Action> unusedActionList = new ArrayList<Action>();
			Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap = new HashMap<RewardFunction, List<Pair<Action, Double>>>();
			Map<RewardFunction, Double> weightMap = new HashMap<RewardFunction, Double>();
			Map<RewardFunction, Double> missingValueMap = new HashMap<RewardFunction, Double>();
			
			for(int j=0; j<2; j++)
			{
				weightMap.put(rewardFunctions[j], weights[j]);
				missingValueMap.put(rewardFunctions[j], missingValueDefaults[j]);
				
				List<Pair<Action, Double>> rewardList = new ArrayList<Pair<Action, Double>>();
				actionHistoryMap.put(rewardFunctions[j], rewardList);
			}
	
			
			//History entry 0
			Action action0 = uniqueActions[0];
			Pair<Action, Double> actionRewardRating0 = new Pair<Action, Double>(action0, 1.0);
			Pair<Action, Double> actionRewardNQC0 = new Pair<Action, Double>(action0, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating0);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC0);
			usedActionSet.add(action0);
			unusedActionSet.remove(action0);
							
			//History entry 1
			Action action1 = uniqueActions[0];
			Pair<Action, Double> actionRewardRating1 = new Pair<Action, Double>(action1, 1.0);
			Pair<Action, Double> actionRewardNQC1 = new Pair<Action, Double>(action1, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating1);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC1);
			usedActionSet.add(action1);
			unusedActionSet.remove(action1);
			
			//History entry 2
			Action action2 = uniqueActions[0];
			Pair<Action, Double> actionRewardRating2 = new Pair<Action, Double>(action2, 0.0);
			Pair<Action, Double> actionRewardNQC2 = new Pair<Action, Double>(action2, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating2);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC2);
			usedActionSet.add(action2);
			unusedActionSet.remove(action2);
			
			//History entry 3
			Action action3 = uniqueActions[1];
			Pair<Action, Double> actionRewardRating3 = new Pair<Action, Double>(action3, 0.0);
			Pair<Action, Double> actionRewardNQC3 = new Pair<Action, Double>(action3, 0.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating3);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC3);
			usedActionSet.add(action3);
			unusedActionSet.remove(action3);
			
			//History entry 4
			Action action4 = uniqueActions[1];
			Pair<Action, Double> actionRewardRating4 = new Pair<Action, Double>(action4, 1.0);
			Pair<Action, Double> actionRewardNQC4 = new Pair<Action, Double>(action4, 0.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating4);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC4);
			usedActionSet.add(action4);
			unusedActionSet.remove(action4);
			
			//History entry 5
			Action action5 = uniqueActions[1];
			Pair<Action, Double> actionRewardRating5 = new Pair<Action, Double>(action5, 0.0);
			Pair<Action, Double> actionRewardNQC5 = new Pair<Action, Double>(action5, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating5);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC5);
			usedActionSet.add(action5);
			unusedActionSet.remove(action5);
			
			
			usedActionList = new ArrayList<Action>(usedActionSet);
			unusedActionList = new ArrayList<Action>(unusedActionSet);
			
			UCBWeightedAir weightedAir = new UCBWeightedAir(random);
			
			Action chosenAction = weightedAir.chooseAction(usedActionList, unusedActionList, actionHistoryMap, weightMap, missingValueMap);
			System.out.println(chosenAction);
		}
	}
	
	public void test2()
	{
		for(int runs=0; runs<10; runs++)
		{
			double[] weights = {0.4, 0.6};
			double[] missingValueDefaults = {0.5, 0.7};
	
			int uniqueActionsCount = 5;
			
			Action[] uniqueActions = new Action[uniqueActionsCount];
			Set<Action> unusedActionSet = new HashSet<Action>();
			Set<Action> usedActionSet = new HashSet<Action>();
			
			for(int j=0; j<uniqueActionsCount; j++)
			{
				uniqueActions[j] = new Action(j);
				unusedActionSet.add(uniqueActions[j]);
			}
			
			RewardFunction studentRating = new RewardFunction("STUDENT_RATING");
			RewardFunction nextQuestionCorrectness = new RewardFunction("NEXT_QUESTION_CORRECTNESS");
			
			RewardFunction[] rewardFunctions = {studentRating, nextQuestionCorrectness};
							
			List<Action> usedActionList = new ArrayList<Action>();
			List<Action> unusedActionList = new ArrayList<Action>();
			Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap = new HashMap<RewardFunction, List<Pair<Action, Double>>>();
			Map<RewardFunction, Double> weightMap = new HashMap<RewardFunction, Double>();
			Map<RewardFunction, Double> missingValueMap = new HashMap<RewardFunction, Double>();
			
			for(int j=0; j<2; j++)
			{
				weightMap.put(rewardFunctions[j], weights[j]);
				missingValueMap.put(rewardFunctions[j], missingValueDefaults[j]);
				
				List<Pair<Action, Double>> rewardList = new ArrayList<Pair<Action, Double>>();
				actionHistoryMap.put(rewardFunctions[j], rewardList);
			}
	
			
			//History entry 0
			Action action0 = uniqueActions[0];
			Pair<Action, Double> actionRewardRating0 = new Pair<Action, Double>(action0, 1.0);
			Pair<Action, Double> actionRewardNQC0 = new Pair<Action, Double>(action0, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating0);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC0);
			usedActionSet.add(action0);
			unusedActionSet.remove(action0);
							
			//History entry 1
			Action action1 = uniqueActions[0];
			Pair<Action, Double> actionRewardRating1 = new Pair<Action, Double>(action1, 1.0);
			Pair<Action, Double> actionRewardNQC1 = new Pair<Action, Double>(action1, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating1);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC1);
			usedActionSet.add(action1);
			unusedActionSet.remove(action1);
			
			//History entry 2
			Action action2 = uniqueActions[0];
			Pair<Action, Double> actionRewardRating2 = new Pair<Action, Double>(action2, 1.0);
			Pair<Action, Double> actionRewardNQC2 = new Pair<Action, Double>(action2, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating2);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC2);
			usedActionSet.add(action2);
			unusedActionSet.remove(action2);
			
			//History entry 3
			Action action3 = uniqueActions[1];
			Pair<Action, Double> actionRewardRating3 = new Pair<Action, Double>(action3, 0.0);
			Pair<Action, Double> actionRewardNQC3 = new Pair<Action, Double>(action3, 0.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating3);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC3);
			usedActionSet.add(action3);
			unusedActionSet.remove(action3);
			
			//History entry 4
			Action action4 = uniqueActions[1];
			Pair<Action, Double> actionRewardRating4 = new Pair<Action, Double>(action4, 0.0);
			Pair<Action, Double> actionRewardNQC4 = new Pair<Action, Double>(action4, 0.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating4);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC4);
			usedActionSet.add(action4);
			unusedActionSet.remove(action4);
			
			//History entry 5
			Action action5 = uniqueActions[1];
			Pair<Action, Double> actionRewardRating5 = new Pair<Action, Double>(action5, 0.0);
			Pair<Action, Double> actionRewardNQC5 = new Pair<Action, Double>(action5, 0.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating5);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC5);
			usedActionSet.add(action5);
			unusedActionSet.remove(action5);
			
			//History entry 6
			Action action6 = uniqueActions[2];
			Pair<Action, Double> actionRewardRating6 = new Pair<Action, Double>(action6, 0.0);
			Pair<Action, Double> actionRewardNQC6 = new Pair<Action, Double>(action6, 0.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating6);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC6);
			usedActionSet.add(action6);
			unusedActionSet.remove(action6);
			
			//History entry 7
			Action action7 = uniqueActions[2];
			Pair<Action, Double> actionRewardRating7 = new Pair<Action, Double>(action7, 0.0);
			Pair<Action, Double> actionRewardNQC7 = new Pair<Action, Double>(action7, 0.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating7);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC7);
			usedActionSet.add(action7);
			unusedActionSet.remove(action7);
			
			//History entry 8
			Action action8 = uniqueActions[2];
			Pair<Action, Double> actionRewardRating8 = new Pair<Action, Double>(action8, 1.0);
			Pair<Action, Double> actionRewardNQC8 = new Pair<Action, Double>(action8, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating8);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC8);
			usedActionSet.add(action8);
			unusedActionSet.remove(action8);
			
			//History entry 9
			Action action9 = uniqueActions[2];
			Pair<Action, Double> actionRewardRating9 = new Pair<Action, Double>(action9, 1.0);
			Pair<Action, Double> actionRewardNQC9 = new Pair<Action, Double>(action9, 1.0);
			actionHistoryMap.get(studentRating).add(actionRewardRating9);
			actionHistoryMap.get(nextQuestionCorrectness).add(actionRewardNQC9);
			usedActionSet.add(action9);
			unusedActionSet.remove(action9);
			
			usedActionList = new ArrayList<Action>(usedActionSet);
			unusedActionList = new ArrayList<Action>(unusedActionSet);
			
			UCBWeightedAir weightedAir = new UCBWeightedAir(random);
			
			Action chosenAction = weightedAir.chooseAction(usedActionList, unusedActionList, actionHistoryMap, weightMap, missingValueMap);
			System.out.println(chosenAction);
		}
	}
	
	public void test3()
	{
	
		double[] weights = {0.4, 0.6};
		double[] missingValueDefaults = {0.5, 0.7};
	
		int uniqueActionsCount = 5;
			
		Action[] uniqueActions = new Action[uniqueActionsCount];
		Set<Action> unusedActionSet = new HashSet<Action>();
		Set<Action> usedActionSet = new HashSet<Action>();
			
		for(int j=0; j<uniqueActionsCount; j++)
		{
			uniqueActions[j] = new Action(j);
			unusedActionSet.add(uniqueActions[j]);
		}
		
		double[] rewards1 = new double[uniqueActionsCount];
		
		rewards1[0] = 0.1;
		rewards1[1] = 0.3;
		rewards1[2] = 0.5;
		rewards1[3] = 0.8;
		rewards1[4] = 0.9;
		
		double[] rewards2 = new double[uniqueActionsCount];
		
		rewards2[0] = 0.1;
		rewards2[1] = 0.3;
		rewards2[2] = 0.5;
		rewards2[3] = 0.1;
		rewards2[4] = 0.9;
		
		RewardFunction studentRating = new RewardFunction("STUDENT_RATING");
		RewardFunction nextQuestionCorrectness = new RewardFunction("NEXT_QUESTION_CORRECTNESS");
		RewardFunction[] rewardFunctions = {studentRating, nextQuestionCorrectness};
							
		List<Action> usedActionList = new ArrayList<Action>();
		List<Action> unusedActionList = new ArrayList<Action>();
		Map<RewardFunction, List<Pair<Action, Double>>> actionHistoryMap = new HashMap<RewardFunction, List<Pair<Action, Double>>>();
		Map<RewardFunction, Double> weightMap = new HashMap<RewardFunction, Double>();
		Map<RewardFunction, Double> missingValueMap = new HashMap<RewardFunction, Double>();
			
		for(int j=0; j<2; j++)
		{
			weightMap.put(rewardFunctions[j], weights[j]);
			missingValueMap.put(rewardFunctions[j], missingValueDefaults[j]);
				
			List<Pair<Action, Double>> rewardList = new ArrayList<Pair<Action, Double>>();
			actionHistoryMap.put(rewardFunctions[j], rewardList);
		}

		usedActionList = new ArrayList<Action>(usedActionSet);
		unusedActionList = new ArrayList<Action>(unusedActionSet);
		
		List<Pair<Action, Double>> rewardList1 = new ArrayList<Pair<Action, Double>>();
		List<Pair<Action, Double>> rewardList2 = new ArrayList<Pair<Action, Double>>();
		
		for(int i=0; i<100; i++)
		{
			UCBWeightedAir weightedAir = new UCBWeightedAir(random);
				
			Action chosenAction = weightedAir.chooseAction(usedActionList, unusedActionList, actionHistoryMap, weightMap, missingValueMap);
			System.out.println(chosenAction.getActionId());
			
			//assign a reward to the chosen action and update things (action history, used and unused actions)
			double reward1 = rewards1[chosenAction.getActionId()];
			double reward2 = rewards2[chosenAction.getActionId()];
			
			boolean removed = unusedActionList.remove(chosenAction);
			
			if(removed)
			{
				usedActionList.add(chosenAction);
			}
			
			rewardList1.add(new Pair<Action, Double>(chosenAction, reward1));
			rewardList2.add(new Pair<Action, Double>(chosenAction, reward2));
			
			actionHistoryMap.put(studentRating, rewardList1);
			actionHistoryMap.put(nextQuestionCorrectness, rewardList2);
			
		}
		
	}
	
	public static void main(String args[])
	{
		UCBWeightedAirTest ucbWeightedAirTest = new UCBWeightedAirTest();
		//ucbWeightedAirTest.runTests();
		//ucbWeightedAirTest.test1();
		//ucbWeightedAirTest.test2();
		ucbWeightedAirTest.test3();
	}

}
