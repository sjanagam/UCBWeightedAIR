package com.dselent.policies;

public class Action implements Comparable<Action>
{
	private int actionId;
	
	public Action(int actionId)
	{
		this.actionId = actionId;
	}

	public int getActionId()
	{
		return actionId;
	}

	public void setActionId(int actionId)
	{
		this.actionId = actionId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + actionId;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (actionId != other.actionId)
			return false;
		return true;
	}

	@Override
	public int compareTo(Action o)
	{
		int compareValue = 0;
		
		if(o == null)
		{
			throw new NullPointerException("Cannot compare to null");
		}
		else
		{
			Integer myId = new Integer(this.getActionId());
			Integer theirId = new Integer(o.getActionId());
			
			compareValue = myId.compareTo(theirId);
		}
		
		return compareValue;
	}

	@Override
	public String toString()
	{
		return "Action [actionId=" + actionId + "]";
	}
	
	


}
