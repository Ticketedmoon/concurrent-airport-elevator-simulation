//to look at concurrent classes to see what is available, atomic units etc. Graphics?



class elev
{
	String/enum direction = up/down;
	int currentFloor = 0;
	final int maxWeightCapacity = XXX;
	boolean isFull;
	Person Arraylist currentPassengers = []; 
	//cant really use stack as its not necessarily LIFO, but if we are to model over capacity we need to remember the last person in
	
	concurrentQueue requests;
	//or make requests class?
	int elevatorID //if multiple for marks
	boolean outOfOrder;

}

class person
{
	String id
	int Weight??? //might just make standard
	Luggage LuggageID
	int arrivalTime
	int arrivalFloor
	int destFloor
	boolean pickedCorrectButton; //do mathRandom, could be tricky to do without locking
}

class Luggage
{
	String luggageid
	int weight 
}


