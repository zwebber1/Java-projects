import java.util.ConcurrentModificationException;
import java.util.Iterator;


public class MyHashTable<T extends Comparable<T>> implements Iterable<T> {
 public ChainedArrays<T>[] table;
 public MyListNode<T> beginMarker, endMarker;
 public int tableSize, numelements, modcount;
 

  // Constructor. DEFAULT_TABLE_SIZE is 101
  public MyHashTable( )
  {
   this(101);
  }

  // Construct an instance; the internal table size is is initially the
  // parameter size if size is prime or the next prime number which is
  // greater than size if it is not prime
  @SuppressWarnings("unchecked")
public MyHashTable( int size )
  {
   if(!isPrime(size))
   {
    size = nextPrime(size);
   }
   this.table =  (ChainedArrays<T>[]) new ChainedArrays[size];
   for(int i = 0; i < size; i++)
   {
	   table[i] = new ChainedArrays<T>();
   }
   this.beginMarker = new MyListNode<T>(null, null, null);
   this.endMarker = new MyListNode<T>(null, null, null);
   this.beginMarker.next = endMarker;
   this.endMarker.prev = beginMarker;
   this.tableSize = size;
   this.numelements = 0;
   this.modcount = 0;
   
  }
  
  // Make the hash table logically empty.
  @SuppressWarnings("unchecked")
public void clear( )
  {
   table = new ChainedArrays[tableSize]; 
   tableSize = 0;
   numelements = 0;
   beginMarker.next = endMarker;
   endMarker.prev = beginMarker;
   
  }

  // Helper method for the linked list. Insert a new node after the given node.
  // Returns a reference to the new node
  protected MyListNode<T> insertListNodeAfter(MyListNode<T> pos, T data)
  {
   MyListNode<T> added = new MyListNode<T>(data,null,null);
   added.next = pos.next;
   added.prev = pos;
   pos.next.prev = added;
   pos.next = added;
   return added;
  }

  // Helper method for the linked list. Insert a new node before the given node 
  // Returns a reference to the new node
  protected MyListNode<T> insertListNodeBefore(MyListNode<T> pos, T data)
  {
   MyListNode<T> added = new MyListNode<T>(data,null,null);
   added.next = pos;
   added.prev = pos.prev;
   pos.prev.next = added;
   pos.prev = added;
   return added;
  }

  // Helper method for the linked list. Remove the selected node.
  // Returns a reference to the removed node
  protected MyListNode<T> removeListNode(MyListNode<T> pos)
  {
   pos.prev.next = pos.next;
   pos.next.prev = pos.prev;
   return pos;
  }

  // Return a String representation of the linked list, 
  // Example: 2, 1, 3, 5, 4 
  protected String listNodesToString()
  {
   StringBuilder str = new StringBuilder();
   MyListNode<T> temp = beginMarker.next;
   for(int i = 0; i <= numelements - 1; i++)
   {
    str.append(temp.toString());
    if(i != numelements -1)
     str.append(", ");
    temp = temp.next;
   }
   return str.toString();
  }

  // Insert x into the hash table. If x is already present, then do nothing.
  // Throws IllegalArgumentException if x is null.

public void insert(T x)
  {
   if(x == null)
    throw new IllegalArgumentException();
   MyListNode<T> temp = new MyListNode<T>(x, null, null);
   if(beginMarker.next == endMarker)
   {
	   beginMarker.next = temp;
	   endMarker.prev = temp;
	   temp.next = endMarker;
	   temp.prev = beginMarker;
	   int pos = myhash(temp);
	   if(pos > tableSize)
		   table[pos%tableSize].add(temp.data);
	   else
		   table[pos].add(temp.data);
	   numelements++;
	   modcount++;
	   return;
   }
   else
   {
	   if(this.contains(x))
		   return;
	   endMarker.prev.next = temp;
	   temp.prev = endMarker.prev;
	   temp.next = endMarker;
	   endMarker.prev = temp;
	   if(numelements == tableSize)
		   rehash();
	   int pos = myhash(temp);
	   table[pos].add(temp.data);
	   numelements++;
	   modcount++;
   }
  }

  // Remove x from the hash table.
  // Throws IllegalArgumentException if x is null.
  public void remove( T x )
  {
   if(x == null)
    throw new IllegalArgumentException();
   if(numelements == 1)
   {
	   if(beginMarker.next.data == x)
	   {
		   table[x.hashCode()].removeFirst();
		   endMarker.prev = beginMarker;
		   beginMarker.next = endMarker;
		   numelements = 0;
	   }
   }	   
   MyListNode<T> temp = beginMarker.next;
   modcount++;
   if(this.contains(x))
   {
    for(int i = 0; i <= tableSize; i++)
    {
     if(temp.data == x)
     {
      numelements--;
      int pos = myhash(temp);
      table[pos].remove(x);
      this.removeListNode(temp);
      return;
     }
     temp = temp.next;
     
    }
   }
  }

  // Return true if x is in the hash table
  // Throws IllegalArgumentException if x is null.
  public boolean contains( T x )
  {
   if(x == null)
    throw new IllegalArgumentException();
   MyListNode<T> temp = beginMarker.next;
   if(temp.next == endMarker)
	   return false;
   while(temp != endMarker)
   {
    if(x.equals(temp.data))
    {
     return true;
    }
    temp = temp.next;
   }
   return false;
   
  }

  // Return an element in the list that equals x, or null if there is no such element.
  // Throws IllegalArgumentException if x is null.
  public T getMatch(T x)
  {
   if(x == null)
    throw new IllegalArgumentException();
   MyListNode<T> temp = beginMarker.next;
   while(temp != endMarker)
   {
    if(x.equals(temp.data))
    {
     return temp.data;
    }
    temp = temp.next;
   }
   return null;
  }

  // Create a pretty representation of the hash table. Does not include the linked list.
  // See the representation of the Table in toString()
  public String tableToString()
  {
   StringBuilder str = new StringBuilder();
   str.append("Table:\n");
   for(int i = 0; i <= tableSize - 1; i++)
   {
    str.append(String.format("%d: %s", i, table[i].toString()));
    str.append("\n");
   }
   return str.toString();
  }

  // Create a pretty representation of the hash table and linked list.
  // Uses toString() of ChainedArrays.
  // May call tableToString() and listNodesToString
  // Example: table size is 3, insertion order: one, three, two
  // Table:
  // 0: | two |
  // 1: | one, three |
  // 2: 
  // Linked List: 
  // one, three, two
  public String toString()
  {
  StringBuilder str = new StringBuilder();
  str.append(this.tableToString());
  str.append("Linked List:\n");
  str.append(this.listNodesToString());
  return str.toString();
  }

  // Increases the size of the table by finding a prime number (nextPrime) at least as large as 
  // twice the table size. Rehashes the elements in the linked list of the hash table.
  @SuppressWarnings("unchecked")
private void rehash( )
  {
   this.tableSize = nextPrime(tableSize*2);
   MyHashTable<T> temp = new MyHashTable<T>(tableSize);
   MyListNode<T> item = beginMarker.next;
   for(int i = 0; i < numelements; i++)
   {
    temp.insert(item.data);
    item = item.next;
   }
   this.table = temp.table;
   
  }

  // internal method for computing the hash value from the hashCode of x.
  private int myhash( MyListNode<T> x ) {
    int hashVal = x.hashCode( );
    hashVal %= tableSize;
    if( hashVal < 0 )
      hashVal += tableSize;
    return hashVal;
  }

  // Internal method to find a prime number at least as large as n. 
  private static int nextPrime( int n ){
    if( n % 2 == 0 )
      n++;
    for( ; !isPrime( n ); n += 2 )
      ;
    return n;
  }

  // Internal method to test if a number is prime. Not an efficient algorithm. 
  private static boolean isPrime( int n ) {
    if( n == 2 || n == 3 )
      return true;
    if( n == 1 || n % 2 == 0 )
      return false;
    for( int i = 3; i * i <= n; i += 2 )
      if( n % i == 0 )
        return false;
    return true;
  }

  // Returns true if there are no elements.
  // Target Complexity: O(1)
  public boolean isEmpty( )
  {
   if(beginMarker.next == endMarker)
    return true;
   return false;
  }

  // Returns the number of elements
  //Target Complexity: O(1)
  public int size()
  {
   return numelements;
  }

  // Returns an iterator over the elements in the proper sequence.
  public Iterator<T> iterator()
  {
   return new MyHashTableIterator();
  }

  // Returns the hash table array of Objects
  protected Object[] getTable()
  {
   return table;
  }

  // Internal node class which links all elements in the hash table
  // together. See documentation elsewhere
  public static class MyListNode<U extends Comparable<U>> implements Comparable<MyListNode<U>> {
   public MyListNode<U> next, prev;
   public U data;
   

   // Constructors
   // Target Complexity: O(1)
   public MyListNode(U data, MyListNode<U> prev, MyListNode<U> next)
   {
    this.data = data;
    this.next = next;
    this.prev = prev;
    
   }
   public MyListNode(MyListNode<U> prev, MyListNode<U> next)
   {
    this(null, prev, next);
   }

   //Returns the hashCode() value of data.
   public int hashCode()
   {
    int hash = data.hashCode();
    return hash;
   }

   // Return true if the two objects are equal.
   // Two MyListNode objects are equal if their data values are equal 
   @SuppressWarnings("unchecked")
 public boolean equals(Object other)
   {
	   if(other == null)
		   return false;
	   if(!(other instanceof MyListNode))
		   return false;
	   MyListNode<U> comp = (MyListNode<U>) other;
	   if(isEqual(this.data, comp.data))
		   return true;
	   return false;
   }

   // Return true if two objects are equal; works if objects can be
   // null.  Used internally for implementation of equals(other).
   protected boolean isEqual( Object lhs, Object rhs )
   {
	   if(lhs == null && rhs == null)
		   return true;
	   if(lhs == null || rhs == null)
		   return false;
	   if(lhs.equals(rhs))
		   return true;
	   return false;
   }

   // Compares this object with the specified other object for order. 
   public int compareTo(MyListNode<U> other)
   {
		 	if(this.data.compareTo(other.data) < 0)
		 		return -1;
		 	if(this.data.compareTo(other.data) > 0)
	 			return 1;
	 		return 0;
    
   }

   // toString() - create a pretty representation of the MyListNode.
   // Example: for an integer: 3
   public String toString()
   {
    StringBuilder str = new StringBuilder();
    str.append(this.data);
    return str.toString();
   }
 }

  // Internal class for iteration; see documentation elsewhere
  private class MyHashTableIterator implements Iterator<T> {
   protected MyListNode<T> currnode;
   protected int expectedModCount = modcount;
   
   
   // Constructor
   // Target Complexity: O(1)
   public MyHashTableIterator ()
   {
    this.currnode = beginMarker.next;
    
   }

   // Returns true if the iterator can be moved to the next() element.
   // Target Complexity: O(1)
   public boolean hasNext()
   {
    if( expectedModCount != modcount )
          throw new ConcurrentModificationException( );
    if(currnode.data != null)
    {
    	return true;
    }
    return false;
    
   }

   // Move the iterator forward and return the passed-over element
   // Target Complexity: O(1)
   public T next()
   {
    if( expectedModCount != modcount )
          throw new ConcurrentModificationException( );
    if(!hasNext())
     throw new RuntimeException("Already at end of list");
    T next = currnode.data;
    currnode = currnode.next;
    return next;
   }

   // The following operation is part of the Iterator interface
   // but are not supported by the iterator.
   // Throws UnsupportedOperationException exceptions if invoked.
   public void remove()
   {
    throw new UnsupportedOperationException(); 
   }
 }
}
