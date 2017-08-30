import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChainedArrays<T extends Comparable<T>> 
    implements Iterable<T> {
	
	public ArrayNode<T> head, tail;
	public int nodecount, size, arraysize, modcount;

  // Workhorse constructor.
  ChainedArrays(int capacityOfArrays)
  {
	 this.head = new ArrayNode<T>(null, null, capacityOfArrays);
	 this.tail = new ArrayNode<T>(null, null, capacityOfArrays);
	 this.head.next = tail;
	 this.tail.prev = head;
	 this.nodecount = 0;
	 this.size = 0;
	 this.modcount = 0;
	 this.arraysize = capacityOfArrays;
  }

  // Convenience constructor  
  ChainedArrays()
  {
	  this(16);
  }

  // class ArrayNode<U> was described earlier.
  protected static class ArrayNode<U extends Comparable<U>> {
	  public ArrayNode<U> prev, next;
	  public U[] data;
	  public int numelem;
	  public int arraySize;
	  
	  
	  
	    // Workhorse constructor. Initialize prev and next and the size of the array, and 
	    // create an array of Objects with the specified capacity.
	    // Throws IllegalArgumentException if capacityOfArray < 0.
		@SuppressWarnings("unchecked")
		public ArrayNode(ArrayNode<U> prev, ArrayNode<U> next, int capacityOfArray)
	    {
	    	if(capacityOfArray < 0)
	    		throw new IllegalArgumentException();
	    	 this.prev = prev;
	    	 this.next = next;
	    	 this.data = (U[]) new Comparable[capacityOfArray];
	    	 this.numelem = 0;
	    	 this.arraySize = capacityOfArray;
	    	 
	    	
	    }

	    // Convenience constructor.
	    public ArrayNode(ArrayNode<U> prev, ArrayNode<U> next)
	    {
	    	this(prev, next, 16);
	    }
	    
	    private int BinarySearch(U[] x, U value, int low, int high)
	    {
	    	if(high < low)
	    		return low;
	    	int mid = low +(high-low)/2;
	    	if(value.compareTo(x[mid]) < 0)
	    	{
	    		return(BinarySearch(x, value, low, mid-1));
	    	}
	    	if(value.compareTo(x[mid]) > 0)
	    	{
	    		return(BinarySearch(x, value, mid+1, high));
	    	}
	    	return mid;
	    }
	    
	    private int BinaryAdd(U[] x, U value, int low, int high)
	    {
	
	    	if(high == low)
	    		return low;
	    	if(high < low)
	    		return low;
	    	int mid = low + ((high-low)/2);
	    	if(value.compareTo(x[mid]) < 0)
	    	{
	    		return(BinaryAdd(x, value, low, mid));
	    	}
	    	if(value.compareTo(x[mid]) > 0)
	    	{
	    		return(BinaryAdd(x, value, mid+1, high));
	    	}
	    	return mid;
	    }

	    // Insert in ascending sorted order using binarySearch(). This may require elements 
	    // to be shifted.
	    // Throws IllegalArgumentException if x is null.
	    // Target Complexity: O(n)
	    public void insertSorted(U x)
	    {
	    	if(x == null)
	    		throw new IllegalArgumentException();
	    	if(numelem == 0)
	    	{
	    		data[0] = x;
	    		numelem++;
	    	}
	    	else
	    	{
	    		
	    		int pos = BinaryAdd(this.data, x, 0, numelem);
	    		for(int i = numelem - 1; i >= pos; i--)
	    		{
	    			data[i+1] = data[i];
	    		}
	    		data[pos] = x;
	    		numelem++;
	    	}
	    }

	    // Locate element x using binarySearch() and remove x. This may require elements 
	    // to be shifted.
	    // Returns a reference to the removed element, or null if the element was not removed.
	    // Throws IllegalArgumentException if x is null.
	    // Target Complexity: O(n)
	    public U remove(U x)
	    {
	    	if(x == null)
	    		throw new IllegalArgumentException();
	    	int pos = BinarySearch(data, x, 0, numelem - 1);
	    	if(pos > numelem-1)
	    		return null;
	    	U remove = data[pos];
	    	if(!x.equals(remove))
	    		return null;
	    	numelem--;
	    	for(int i = pos; i <= numelem; i++)
	    	{
	    		if(i == numelem)
	    			data[i] = null;
	    		else
	    			data[i] = data[i+1];
	    	}
	    	if(numelem == 0)
	    	{
	    		this.next.prev = this.prev;
	    		this.prev.next = this.next;
	    	}
	    	return remove;
	    }

	    // Remove the element at index idx. This may require elements to be shifted.
	    // Returns a reference to the removed element.
	    // Throws IndexOutOfBoundsException if idx is out of bounds (less than 0 
	    // or greater than array size -1)
	    // Target Complexity: O(n)
	    public U remove(int idx)
	    {
	    	if(idx > numelem - 1 || idx < 0)
	    		throw new IndexOutOfBoundsException();
	    	U remove = data[idx];
	    	numelem--;
	    	for(int i = idx; i <= numelem; i++)
	    	{
	    		if(i == numelem)
	    			data[i] = null;
	    		else
	    			data[i] = data[i+1];
	    	}
	    	if(numelem == 0)
	    	{
	    		this.next.prev = this.prev;
	    		this.prev.next = this.next;
	    	}
	    	return remove;
	    }

	    // Uses binarySearch() to return the index of x, if x is contained in the array; 
	    // otherwise, return (-(insertion point) - 1). The insertion point is defined as the point 
	    // at which x would be inserted into the array: the index of the first element greater 
	    // than x, or array.length if all elements in the array are less than x. Note that this 
	    // guarantees that the return value will be >= 0 if and only if x is found.
	    // Throws IllegalArgumentException if x is null.
	    // Target Complexity: O(lg n)
	    public int indexOf(U x)
	    {
	    	if(x == null)
	    		throw new IllegalArgumentException();
	    	//int pos = BinarySearch(data, x, 0, numelem - 1);
	    	//if(pos == -1)
	    	//{
	    		int pos = BinaryAdd(data, x, 0, numelem - 1);
	    	//}
	    	return pos;
	    }

	    //Returns the element at the specified index. Throws IndexOutOfBoundsException if idx 
	    // is out of bounds (less than 0 or greater than array size -1)
	    //Target Complexity: O(1)
	    public U get(int idx)
	    {
	    	if(idx > numelem - 1 || idx < 0)
	    		throw new IndexOutOfBoundsException();
	    	return data[idx];
	    }

	    //Returns the first element. Throws IndexOutOfBoundsException if the number 
	    //of elements is 0. May call get(int idx).
	    // Target Complexity: O(1)
	    public U getFirst()
	    {
	    	if(numelem == 0)
	    		throw new IndexOutOfBoundsException();
	    	return data[0];
	    }

	    // Returns the last element. Throws IndexOutOfBoundsException if the number 
	    // of elements is 0. May call get(int idx).
	    // Target Complexity: O(1)
	    public U getLast()
	    {
	    	if(numelem == 0)
	    		throw new IndexOutOfBoundsException();
	    	return data[numelem - 1];
	    }

	    // Remove the element at index 0. This may require elements to be shifted.
	    // Returns a reference to the removed element.
	    // Throws IndexOutOfBoundsException if the number of elements is 0.
	    // Target Complexity: O(n)
	    public U removeFirst()
	    {
	    	if(numelem == 0)
	    		throw new IndexOutOfBoundsException();
	    	int i = 0;
	    	return remove(i);
	    	
	    }

	    // Remove the last element.
	    // Returns a reference to the removed element.
	    // Throws IndexOutOfBoundsException if the number of elements is 0.
	    // Target Complexity: O(1)
	    public U removeLast()
	    {
	    	if(numelem == 0)
	    		throw new IndexOutOfBoundsException();
	    	return this.remove(numelem-1);
	    }

	    // toString() - create a pretty representation of the ArrayNode by showing all of the elements in the array.
	    // Target Complexity: O(n)
	    // Example: four elements in an array of capacity five:1, 2, 4, 5
	    public String toString()
	    {
	    	StringBuilder str = new StringBuilder();
	    	for(int i = 0; i <= numelem - 1; i++)
	    	{
	    		if(i < numelem - 1)
	    			str.append(String.format("%s, ", data[i]));
	    		else
	    			str.append(String.format("%s\n", data[i]));
	    	}
	    	return str.toString();
	    }

	    // Return array of Objects
	    protected Object[] getArray()
	    {
	    	return(data);
	    }

	    // Set array of Objects
	    @SuppressWarnings("unchecked")
		protected void setArray(Object[] array)
	    {
	    	this.data = (U[]) array;
	    }

	    // Return size of array (not length)
	    protected int getArraySize()
	    {
	    	return numelem;
	    }

	    // Set size of array
	    protected void setArraySize(int arraySize)
	    {
	    	this.arraySize = arraySize;
	    }
	  }

  // Make the ChainedArrays logically empty.
  // Target Complexity: O(1)
  // Implementation note: It is not necessary to remove() all the elements; instead, some data
  // members can be reinitialized.
  public void clear()
  {
	modcount++;
	this.size = 0;
	this.nodecount = 0;
	this.head.next = tail;
	this.tail.prev = head;
  }

  // Returns the number of elements in the ChainedArrays
  // Target Complexity: O(1)
  public int size()
  {
	  return size;
  }

  // returns the number of ArrayNodes
  // Target Complexity: O(1)
  public int nodeCount()
  {
	  return nodecount;
  }

  // Returns true if there are no elements in the list
  // Target Complexity: O(1)
  public boolean isEmpty( )
  {
	  if(head.next == tail)
		  return true;
	  return false;
  }

  // Return the first element in the list that equals data, or null 
  // if there is no such element.
  // Throws IllegalArgumentException if x is null.
  // Target Complexity: O(n)
  public T getMatch(T data)
  {
	 if(data == null)
		 throw new IllegalArgumentException();
	 int i = 0;
	 ArrayNode<T> temp = head.next;
	 while(true)
	 {
		 if(temp == tail)
			 return null;
		 else
		 {
			 if(i == arraysize)
			 {
				 temp = temp.next;
				 i = 0;
			 }
			 else
			 {
				 if(temp.data[i] == null)
				 {
					 temp = temp.next;
					 i = 0;
				 }
				 else if(temp.data[i] == data)
					 return data;
				 else
				 {
					 i++;
				 }
			 }
		 }
	 }
  }

  // Returns true if this ChainedArray contains the specified element.
  // Throws IllegalArgumentException if data is null.
  public boolean contains (T data)
  {
	  T contains = getMatch(data);
	  if(contains == null)
		  return false;
	  if(isEqual(contains, data))
		  return true;
	  return false;
  }

  // Inserts data into the list. Parameter data will be inserted into the node 
  // referenced by current, or node current will be split into two nodes, 
  // and data will be inserted into one of these nodes.
  // The rules for splitting a node are described in the implementation section.
  // Implementation Note: Called by add().
  protected void insertWithPossibleSplit(ArrayNode<T> current, T data)
  {
	  if(arraysize == 1)
	  {
		  modcount++;
		  if(current.data[0].compareTo(data) <= 0)
		  {
			  ArrayNode<T> added = new ArrayNode<T>(current, current.next);
			  added.data[0] = data;
			  current.next.prev = added;
			  current.next = added;
			  nodecount++;
			  size++;
		  }
		  else
		  {
			  ArrayNode<T> added = new ArrayNode<T>(current.prev, current);
			  added.data[0] = data;
			  current.prev.next = added;
			  current.prev = added;
			  nodecount++;
			  size++;
		  }
			  
	  }
	  else if(current.numelem < arraysize)
	  {
		  current.insertSorted(data);
		  size++;
	  }
	  else
	  {
		  ArrayNode<T> temp = new ArrayNode<T>(current, current.next, arraysize);
		  current.next.prev = temp;
		  current.next = temp;
		  for(int i = current.numelem/2; i < current.numelem; i++)
		  {
			  temp.insertSorted(current.data[i]);
			  current.data[i] = null;
		  }
		  current.numelem = current.numelem/2;
		  if(data.compareTo(temp.getFirst()) < 0)
			  current.insertSorted(data);
		  else
		  {
			  temp.insertSorted(data);
		  }
		  nodecount++;
		  size++;
	  }
  }

  // Insert data into the list. Returns true if data was added.
  // The rules for finding the node in which to insert data are described 
  // in the implementation section.
  // Throws IllegalArgumentException if data is null.
  public boolean add(T data)
  {
	  modcount++;
	  if(this.isEmpty())
	  {
		   ArrayNode<T> added = new ArrayNode<T>(head, tail, arraysize);
		   head.next = added;
		   tail.prev = added;
		   added.data[0] = data;
		   added.numelem++;
		   size++;
		   nodecount++;
		   return true;
	  }
	  if(arraysize == 1)
	  {
		  ArrayNode<T> current = new ArrayNode<T>(null, null, arraysize);
		  current = head.next;
		  for(int i = 0; i <= nodecount; i++)
		  {
			  if(current.next == tail)
			  {
				  ArrayNode<T> add = new ArrayNode<T>(current, current.next, arraysize);
				  add.data[0] = data;
				  current.next.prev = add;
				  current.next = add;
				  add.numelem++;
				  nodecount++;
				  return true;
			  }
			  else if(current.data[0].compareTo(data) < 0)
			  {
				  current = current.next;
			  }
			  else if(current.data[0].compareTo(data) >= 0)
			  {
				  ArrayNode<T> add = new ArrayNode<T>(current.prev, current, arraysize);
				  add.data[0] = data;
				  current.prev.next = add;
				  current.prev = add;
				  add.numelem++;
				  nodecount++;
				  return true;
			  }
			  
		  }
	  }
	  else
	  {
		  ArrayNode<T> current = new ArrayNode<T>(null, null, arraysize);
		  current = head.next;
		  for(int i = 0; i <= nodecount; i++)
		  {
			  if(current.next == tail && current.prev == head)
			  {
				  insertWithPossibleSplit(current, data); 
				  return true;
			  }
			  if(data.compareTo(current.getFirst()) < 0)
			  {
				  insertWithPossibleSplit(current, data);
				  return true;
			  }
			  if(data.compareTo(current.getLast()) <= 0 && data.compareTo(current.getFirst()) >= 0)
			  {
				  insertWithPossibleSplit(current, data);
				  return true;
			  }
			  if(current.next == tail)
			  {
				  insertWithPossibleSplit(current, data);
				  return true;
			  }
			  if(data.compareTo(current.getLast()) > 0 && data.compareTo(current.next.getFirst()) < 0)
			  {
				  if(current.numelem < current.next.numelem)
				  {
					  insertWithPossibleSplit(current, data);
					  return true;
				  }
				  else
				  {
					  insertWithPossibleSplit(current.next, data);
					  return true;
				  }
			  }
				  current = current.next;
			  }
		  }
	  return false;
	  
  }

  // Inserts all of the elements in the specified collection in the order 
  // that they are returned by the specified collection's Iterator. 
  // Returns true if at least one element was added.
  // Implementation note: May repeatedly call add().
  public boolean addAll(Iterable<T> c)
  {
	  boolean added = false;
	    for(T thing : c){
	      added |= this.add(thing);
	    }
	    return added; 
  }

  // Removes the first occurrence of the specified element if it is present. 
  // Return a reference to the removed element if it is removed. 
  // Compress() the list, if necessary.
  protected T remove(T data)
  {
	  	modcount++;
		ArrayNode<T> temp = head.next;
		if(temp == tail)
			 return null;
		if(arraysize == 1)
		{
			for(int i = 0; i <= nodecount; i++)
			{
				if(temp.data[0].equals(data))
				{ 
					temp.next.prev = temp.prev;
					temp.prev.next = temp.next;
					nodecount--;
					size--;
					return data;
				}
				temp = temp.next;
			}
			return null;
		}
		else
		{
		 while(true)
		 {
			 T remove = temp.remove(data);
			 if(remove != null)
			 {
				 size--;
				 compress();
				 if(size == 0)
				 {
					 head.next = tail;
					 tail.prev = head;
				 }
				 if(temp.numelem == 0)
				 {
					 temp.next.prev = temp.prev;
					 temp.prev.next = temp.next;
					 nodecount--;
				 }
				 return data;
			 }
			 if(remove == null)
				 temp = temp.next;
			 if(temp == tail)
				 return null;
							
			 }
			 
		 }
  }

  // Reduce the amount of allocated space by shifting elements and possibly 
  // removing nodes. No new nodes can be created during compression.
  // The compression procedure is described in the implementation section.
  protected void compress()
  {
	  if(nodecount <= 1)
		  return;
	  
	  ArrayNode<T> add = head.next;
	  ArrayNode<T> compress = add.next;
	  while(size < (nodecount*arraysize*0.5))
	  {
		 if(add.numelem == arraysize)
		 {
			 add = compress;
			 compress = compress.next;
			 if(compress == tail)
				 break;
			 
		 }
		 else
		 {
			 if(compress == tail)
				 break;
			 if(compress.numelem == 0)
			 {
				 compress = compress.next;
			 }
			 else
			 {
				 add.data[add.numelem] = compress.getFirst();
				 add.numelem++;
				 compress.removeFirst();
			 }
		 }
	  }
  }

  // Returns the first item.
  // Throws NoSuchElementException if the ChainedArrays are empty.
  public T getFirst( )
  {
	  if(head.next == tail)
		  throw new NoSuchElementException();
	  else
	  {
		  return head.next.getFirst();
	  }
	  
  }

  // Returns the last item.
  // Throws NoSuchElementException if the ChainedArrays are empty.
  public T getLast( )
  {
	  if(head.next == tail)
		  throw new NoSuchElementException();
	  else
	  {
		  return tail.prev.getLast();
	  }
  }

  // Removes the first item.
  // Return a reference to the removed element if it is removed. 
  // Compress() the list, if necessary.
  // Throws NoSuchElementException if the ChainedArrays are empty.
  public T removeFirst( )
  {
	  if(head.next == tail)
		  throw new NoSuchElementException();
	  if(arraysize == 1)
	  {
		  T remove = head.next.data[0];
		  head.next = head.next.next;
		  head.next.prev = head;
		  nodecount--;
		  size--;
		  modcount++;
		  return remove;
	  }
	  else
	  {
		  T remove = head.next.getFirst();
		  if(remove != null)
		  {
			  compress();
			  size--;
		  }
		  return remove;
	  }
  }

  // Returns the last item.
  // Return a reference to the removed element if it is removed. 
  // Compress() the list, if necessary.
  // Throws NoSuchElementException if the ChainedArrays are empty.
  public T removeLast( )
  {
	  if(head.next == tail)
		  throw new NoSuchElementException();
	  if(arraysize == 1)
	  {
		  T remove = tail.prev.data[0];
		  tail.prev = tail.prev.prev;
		  tail.prev.next = tail;
		  nodecount--;
		  size--;
		  modcount++;
		  return remove;
	  }
	  else
	  {
		  T remove = tail.prev.removeLast();
		  if(remove != null)
		  {
			  compress();
			  size--;
		  }
		  return remove;
	  }
  }
       
  // Gets the Node at position idx, which must range from 0 to numNodes( )-1.
  // Throws IndexOutOfBoundsException if idx is not between 
  //   0 and numNodes()-1, inclusive.
  protected ArrayNode<T> getNode(int idx)
  {
	  if(idx < 0 || idx > nodecount - 1)
		  throw new IndexOutOfBoundsException();
	  else
	  {
		  if(nodecount == 0)
			  return null;
		  ArrayNode<T> temp = head.next;
		  for(int i = 0; i < idx; i++)
		  {
			  temp = temp.next;
		  }
		  return temp;
	  }
	  
  }
   
  // Gets the Node at position idx, which must range in position 
  // from lower to upper.
  // Throws IndexOutOfBoundsException if idx is not between 
  //   lower and upper, inclusive.
  protected ArrayNode<T> getNode( int idx, int lower, int upper)
  {
	  if(idx < lower || idx > upper)
		  throw new IndexOutOfBoundsException();
	  else
	  {
		  if(nodecount == 0)
			  return null;
		  ArrayNode<T> temp = head.next;
		  for(int i = 0; i < idx; i++)
		  {
			  temp = temp.next;
		  }
		  return temp;
	  }
  }

  // Return true if the items in other are equal to the items in
  // this ChainedArrays (same order, and same values according to equals).
  // Requires the provided iterator to be implemented correctly.
  @SuppressWarnings("unchecked")
public boolean equals( Object other )
  {
	  if(other == null)
		  return false;
	  if(!(other instanceof ChainedArrays))
		  return false;
	  ChainedArrays<T> comp = (ChainedArrays<T>) other;
	  Iterator<T> iter1 = this.iterator();
	  Iterator<T> iter2 = comp.iterator();
	  boolean equals = false;
	  while(iter1.hasNext() && iter2.hasNext())
	  {
		  equals = isEqual(iter1.next(), iter2.next());
		  System.out.println(equals);
		  if(equals == false)
			  return false;
		  
	  }
	  if(!iter1.hasNext() && !iter2.hasNext())
		  return true;
	  else
		  return false;
  }
    
  // Return true if two objects are equal; works if objects can be
  // null. Used internally for implementation of equals(other).
  protected boolean isEqual( Object lhs, Object rhs )
  {
	  if(lhs == null || rhs == null)
		  return false;
	  if(lhs.equals(rhs))
		  return true;
	  return false;
		  
  }

  // Returns an iterator over the elements in the proper sequence.
  public Iterator<T> iterator()
  {
	  return new ChainedArraysIterator();
  }

  // Iterator for ChainedArrays. (See the description below.)
  private class ChainedArraysIterator implements Iterator<T> {
	  // Constructor
	  // Target Complexity: O(1)
	  protected T current;
	  protected ArrayNode<T> currnode;
	  protected int curridx;
	  protected int expectedModCount = modcount; 
	  public ChainedArraysIterator()
	  {
		  currnode = head.next;
		  current = getFirst();
		  curridx = 0;
	  }

	  // Returns true if the iterator can be moved to the next() element.
	  public boolean hasNext()
	  {
		  if( expectedModCount != modcount )
		        throw new ConcurrentModificationException( );
		  return currnode != tail;
	  }

	  // Move the iterator forward and return the passed-over element
	  public T next()
	  {
		  if( expectedModCount != modcount )
		        throw new ConcurrentModificationException( );
		  if(!hasNext())
			  throw new RuntimeException("Already at end of list");
		  T next = current;
		  if(curridx+1 >= arraysize)
		  {
			  currnode = currnode.next;
			  current = currnode.data[0];
			  curridx = 0;
			  return next;
		  }
		  else if(currnode.data[curridx+1] == null)
		  {
			  currnode = currnode.next;
			  current = currnode.data[0];
			  curridx = 0;
			  return next;
		  }
		  else
		  {
			  current = currnode.data[curridx+1];
			  curridx++;
			  return next;
		  }
	  }

	  // The following operation is part of the Iterator interface
	  // but is not supported by the iterator. 
	  // Throws an UnsupportedOperationException if invoked.
	  public void remove()
	  {
		  throw new UnsupportedOperationException();  
	  }
	}

  // toString() - create a pretty representation of the ArrayNode.
  // Runtime: O(n)
  // Example: ChainedArray with two nodes of capacity two: | 1, 2 | 3 |
  public String toString()
  {
	  StringBuilder str = new StringBuilder();
	  	 int i = 0;
		 ArrayNode<T> temp = head.next;
		 if(this.isEmpty())
			 return("");
		 str.append("| ");
		 while(true)
		 {
			 if(temp == tail)
				 break;
			 else
			 {
				 if(i == arraysize)
				 {
					 str.append("| ");
					 temp = temp.next;
					 i = 0;
				 }
				 else
				 {
					 if(temp.data[i] == null)
					 {
						 str.append("| ");
						 temp = temp.next;
						 i = 0;
					 }
					 else
					 {
						 if(i != 0)
							 str.append(", ");
						 str.append(String.format("%s", temp.data[i]));
						 i++;
					 }
				 }
			 }
		 }
		 return str.toString();
  }
}
