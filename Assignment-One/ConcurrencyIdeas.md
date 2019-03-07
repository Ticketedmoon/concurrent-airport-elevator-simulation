//to look at concurrent classes to see what is available, atomic units etc. Graphics?


class elev
{
	// Shane: Type string for clarity? but we could do boolean.
	String/enum direction = up/down;
	int currentFloor = 0;
	final int maxWeightCapacity = XXX;
	boolean isFull;

	// Shane: Should this be LIFO? Is the last person in always the first one off?
	// This would only make sense if everyone got off at the same floor.
	Person Arraylist currentPassengers = []; 
	// Cant really use stack as its not necessarily LIFO, but if we are to model over capacity we need to remember the last person in
	
	// Shane: BlockingQueue here potentially
	ConcurrentQueue requests;	

	//or make requests class?
	int elevatorID // if multiple for marks
	boolean outOfOrder;

}

class person
{
	String id
	// Shane: Initially have standard weight but allow for variation later.
	int Weight??? //might just make standard
	Luggage LuggageID
	int arrivalTime
	int arrivalFloor
	int destFloor

	// Shane: Yeah, I think it is a good idea to have these RNG sort of lower probability situations.
	boolean pickedCorrectButton; //do mathRandom, could be tricky to do without locking
}

class Luggage
{
	String luggageid
	int weight 
}


