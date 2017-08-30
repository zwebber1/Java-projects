/*
I did the methods at the end of this file marked required.
It was needed for my sparseboard file to work along with several other files provided by the instructor.
*/

import java.util.Iterator;
import java.util.ListIterator;
import java.util.ConcurrentModificationException;

// AdditiveList class implements a doubly-linked list that is Iterable
// and provides a ListIterator.  It provides some functionality that
// java.util.LinkedList provides but adds in the ability to undo() and
// redo() additions to the list but does not support any remove()
// operations, thus the name, "additive list".  The class also
// provides a list iterator implemented as an inner class.
// 
// The provided code is based on Mark Allen Weiss's code
// from Data Structures and Problem Solving Using Java 4th
// edition.
// 
// SOME METHODS REQUIRE DEFINITIONS to complete the implementation and
// are marked REQUIRED.  Methods of the the list iterator are REQUIRED
// as well.


public class AdditiveList<T> implements Iterable<T>{

  // Doubly-linked list node for use internally
  public static class Node<T> {
    public T data;
    public Node<T> prev, next;
    public Node( T d, Node<T> p, Node<T> n ) {
      data = d; prev = p; next = n;
    }
  }

  protected final Node<T> NOT_FOUND = null; // Used to indicate failure to locate an object
  protected int theSize;          // Tracks the size of the list
  protected Node<T> beginMarker;  // Dummy node marking the front of the list
  protected Node<T> endMarker;    // Dummy node marking the back of the list
  protected Node<T> undoTop;
  protected Node<T> undobot;
  protected SimpleStack<T> undo;
  protected SimpleStack<T> redo;
  protected Node<T> redoTop;
  protected Node<T> redobot;
  protected int modCount = 0;      // Tracks modifications for iterators



  // Construct an empty AdditiveList.
  public AdditiveList( ) {
    this.beginMarker = new Node<T>( null, null, null );
    this.endMarker = new Node<T>( null, beginMarker, null );
    this.undoTop = new Node<T>(null, null, null);
    this.undobot = new Node<T>(null, null, undoTop);
    this.redoTop = new Node<T>(null, null, null);
    this.redobot = new Node<T>(null, null, redoTop);
    this.beginMarker.next = endMarker;
    this.undoTop.prev = undobot;
    this.redoTop.prev = redobot;
    this.theSize = 0;
    this.modCount++;
  }
   
  // Returns the number of items in this collection.
  // @return the number of items in this collection.
  public int size( ){
    return this.theSize;
  }
   
   
  // Tests if some item is in this collection.
  // @param x any object.
  // @return true if this collection contains an item equal to x.
  public boolean contains( Object x ){
    return findPos( x ) != NOT_FOUND;
  } 
   
  // Returns the position of first item matching x in this collection,
  // or NOT_FOUND if not found.
  // @param x any object.
  // @return the position of first item matching x in this collection,
  // or NOT_FOUND if not found.
  protected Node<T> findPos( Object x ){
    for( Node<T> p = beginMarker.next; p != endMarker; p = p.next )
      if( x == null ){
        if( p.data == null )
          return p;
      }
      else if( x.equals( p.data ) )
        return p;
              
    return NOT_FOUND;
  }
   
  // Adds an item to this collection, at specified position.
  // Items at or after that position are slid one position higher.
  // @param x any object.
  // @param idx position to add at.
  // @throws IndexOutOfBoundsException if idx is not between 0 and size(), inclusive.
  public void add( int idx, T x ){
    Node<T> p = getNode( idx, 0, size( ) );
    Node<T> newNode = new Node<T>( x, p.prev, p );
    newNode.prev.next = newNode;
    p.prev = newNode; 
    theSize++;
    modCount++;
    Node<T> undo = new Node<T>(null, newNode, null);
    if(undobot.next == undoTop)
    {
    undobot.next = undo;
    undoTop.prev = undo;
    undo.next = undoTop;
    }
    else
    {
    	undo.next = undobot.next;
    	undobot.next = undo;

    }
    redobot.next = redoTop;
    redoTop.prev = redobot;
  }

  // Adds all item in given iterable collection to the end of this
  // collection.
  // @param c a collection which can be iterated over
  // @return true.
  public boolean addAll(Iterable<T> c){
    boolean added = false;
    for(T thing : c){
      added |= this.add(thing);
    }
    return added;
  }


  // Adds an item to this collection, at the end.
  // @param x any object.
  // @return true.
  public boolean add( T x ){
    addLast( x );   
    return true;         
  }
   
  // Adds an item to this collection, at front.
  // Other items are slid one position higher.
  // @param x any object.
  public void addFirst( T x ){
    add( 0, x );
  }

  // Adds an item to this collection, at end.
  // @param x any object.
  public void addLast( T x ){
    add( size( ), x );
  }    
   
  // Returns the first item in the list.
  // @throws NoSuchElementException if the list is empty.
  public T getFirst( ){
    if( isEmpty( ) )
      throw new RuntimeException("List is empty");
    return getNode( 0 ).data;    
  }
   
  // Returns the last item in the list.
  // @throws NoSuchElementException if the list is empty.
  public T getLast( ){
    if( isEmpty( ) )
      throw new RuntimeException("List is empty");
    return getNode( size( ) - 1 ).data;    
  }
   
  // Returns the item at position idx.
  // @param idx the index to search in.
  // @throws IndexOutOfBoundsException if index is out of range.
  public T get( int idx ){
    return getNode( idx ).data;
  }
       
  // Gets the Node at position idx, which must range from 0 to size( )-1.
  // @param idx index to search at.
  // @return internal node corrsponding to idx.
  // @throws IndexOutOfBoundsException if idx is not between 0 and size()-1, inclusive.
  protected Node<T> getNode( int idx ){
    return getNode( idx, 0, size( ) - 1 );
  }
   
  // Gets the Node at position idx, which must range from lower to
  // upper. Used for other internal methods.
  // 
  // @param idx index to search at.
  // @param lower lowest valid index.
  // @param upper highest valid index.
  // @return internal node corrsponding to idx.
  // @throws IndexOutOfBoundsException if idx is not between lower and upper, inclusive.
  protected Node<T> getNode( int idx, int lower, int upper ){
    Node<T> p;
    if( idx < lower || idx > upper ){
      throw new IndexOutOfBoundsException( "getNode index: " + idx + "; size: " + size( ) );
    }
    if( idx < size( ) / 2 )
      {
        p = beginMarker.next;
        for( int i = 0; i < idx; i++ )
          p = p.next;            
      }
    else
      {
        p = endMarker;
        for( int i = size( ); i > idx; i-- )
          p = p.prev;
      } 
    return p;
  }
   
   
  // Tests if this collection is empty.
  // @return true if the size of this collection is zero.
  public boolean isEmpty( ){
    return size( ) == 0;
  }
    
  // Return true if items in other collection are equal to items in
  // this collection (same order, and same according to equals).
  // Requires that the list iterators provided by the list be
  // implemented correctly.
  // 
  // @param other Another object, possibly another AdditiveList
  // @return true if this is equal to the other object and fals otherwise
  public boolean equals( Object other ){
    if( other == this )
      return true;
            
    if( ! ( other instanceof AdditiveList ) )
      return false;
        
    AdditiveList rhs = (AdditiveList) other;
    if( size( ) != rhs.size( ) )
      return false;
        
    Iterator<T> lhsItr = this.iterator( );
    Iterator rhsItr = rhs.iterator( );
        
    while( lhsItr.hasNext( ) )
      if( !isEqual( lhsItr.next( ), rhsItr.next( ) ) )
        return false;
                
    return true;            
  }
    
  // Return true if two objects are equal; works if objects can be
  // null.  Used internally for implementation of equals(other).
  protected boolean isEqual( Object lhs, Object rhs ){
    if( lhs == null )
      return rhs == null;
    return lhs.equals( rhs );    
  }

  // Return a hashCode for the list.  Will be discussed later in CS 310
  public final int hashCode( ){
    int hashVal = 1;
       
    for( T obj : this )
      hashVal = 31 * hashVal + ( obj == null ? 0 : obj.hashCode( ) );
       
    return hashVal;
  }
  
  // Return a string representation of the list. This should match the
  // format of other java lists.
  public String toString( ){
    StringBuilder result = new StringBuilder( "[" );
    for( T obj : this ){
      result.append( obj.toString());
      result.append(", " );
    }
    if(!this.isEmpty()){
      result.delete(result.length()-2,result.length());
    }
    result.append( "]" );
    return result.toString( );
  }    

  ////////////////////////////////////////////////////////////////////////////////
  // Undo/Redo methods
  // 
  // REQUIRED: Undo add() operations that have been done to the list.
  // All history for the list should be tracked so that repeated
  // undo() calls eventually lead back to an empty list.  If no adds
  // remain to be undone, this method should throw a RuntimeException
  // with the message "Undo history is empty"
  //
  // Target Complexity: O(1) (worst case)
  public void undo(){
    if(undobot.next == undoTop)
      throw new RuntimeException("Undo history is empty");
    else
    {
    	Node<T> redo = new Node<T>(null, undobot.next.prev, null);
    	if(redobot.next  == redoTop)
    	{
    		redobot.next = redo;
    		redo.prev.prev.next = redo.prev.next;
    		redo.prev.next.prev = redo.prev.prev;
    		redoTop.prev = redo;
    		redo.next = redoTop;
    		theSize--;
    	}
    	else
    	{
    		redo.prev.prev.next = redo.prev.next;
    		redo.prev.next.prev = redo.prev.prev;
    		redo.next = redobot.next;
    		redobot.next = redo;
    		theSize--;
    	}
    }
    undobot = undobot.next;
    modCount++;
  }

  // REQUIRED: Redo an add which has been undone using undo().  If no
  // adds remain to be redone, this method should throw a
  // RuntimeException with the message "Redo history is empty"
  //
  // Target Complexity: O(1) (worst case)
  public void redo(){
	  if(redobot.next == redoTop)
		  throw new RuntimeException("Redo history is empty");
	  else{
	  Node<T> undo = new Node<T>(null, redobot.next.prev, null);
	  undo.next = undobot.next.next;
	  undobot.next = undo;
	  redobot.next.prev.next.prev = redobot.next.prev;
	  redobot.next.prev.prev.next = redobot.next.prev;
	  redobot.next = redobot.next.next;
	  }
	  theSize++;
	  //redobot = redobot.next;
	  modCount++;
  }

  // REQUIRED: Return true if any adds to the list can be undone and
  // false otherwise.
  public boolean canUndo(){
	  if(undobot.next != undoTop)
		  return true;
	  else
		  return false;
  }

  // REQUIRED: Return true if any adds to the list can be redone and
  // false otherwise.
  public boolean canRedo(){
	  if(redobot.next != redoTop)
		  return true;
	  else
		  return false;
  }

  // REQUIRED: Clears the history of all adds; subsequently cannot
  // undo or redo until more adds have been made.
  public void clearHistory()
  {
	  undobot.next = undoTop;
	  undoTop.prev = undobot;
	  redobot.next = redoTop;
	  redoTop.prev = redobot;
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Iteration Methods
  // 
  // Obtains an Iterator object used to traverse the
  // collection. Iterators can only move forward with next().
  // 
  // @return an iterator positioned prior to the first element.
  public Iterator<T> iterator( ){
    return new AListIterator( 0 );
  }
   
  // Obtains a ListIterator object used to traverse the collection
  // bidirectionally.
  // 
  // @return an iterator positioned prior to the first element
  public ListIterator<T> listIterator( ){
    return new AListIterator( 0 );
  }

  // Obtains a ListIterator object used to traverse the collection
  // bidirectionally.
  // 
  // @return an iterator positioned prior to the requested element.
  // @param idx the index to start the iterator. Use size() to do complete
  // reverse traversal. Use 0 to do complete forward traversal.
  // @throws IndexOutOfBoundsException if idx is not between 0 and size(), inclusive.
  public ListIterator<T> listIterator( int idx ){
    return new AListIterator( idx );
  }


  // This class implementats the ListIterator interface for the
  // AdditiveList.  It maintains a notion of a current position and
  // implicit reference to the AdditiveList through the syntax
  // AdditiveList.this.  The class REQUIRES several methods to be
  // implemented to complete it and cooperate with the undo/redo
  // functionality of the AdditiveList.  Any external changes to the
  // list associated with the iterator should cause the iterator to
  // throw a ConcurrentModificationException on implemented method
  // calls.  Altering the list in any way other than the iterators
  // .add(x) method should invalidate the iterator.
  public class AListIterator implements ListIterator<T>{
    protected Node<T> current;                 // Current node, return data on call to next()
    protected Node<T> lastVisited = null;      // Used for calls to remove
    protected boolean lastMoveWasPrev = false; // Necessary for implementing previous()
    protected int expectedModCount = modCount; // How many modifications iterator expects
      
    // Construct an iterator
    public AListIterator( int idx ){
      current = AdditiveList.this.getNode( idx, 0, size( ) );  
    }
      
    // Can the iterator be moved to the next() element
    public boolean hasNext( ){
      if( expectedModCount != modCount )
        throw new ConcurrentModificationException( );
      return current != endMarker;
    }
      
    // Move the iterator forward and return the passed-over element
    public T next( ){
      if( expectedModCount != modCount )
        throw new ConcurrentModificationException( );
      if( !hasNext( ) )
        throw new RuntimeException("Already at end of list"); 
      T nextItem = current.data;
      lastVisited = current;
      current = current.next;
      lastMoveWasPrev = false;
      return nextItem;
    }
      
    // REQUIRED: Can the iterator be moved with previous(). If the
    // list has been modified outside this iterator throw a
    // ConcurrentModificationException.
    // 
    // TARGET COMPLEXITY: O(1)
    public boolean hasPrevious( ){
    	if( expectedModCount != modCount )
            throw new ConcurrentModificationException( );
          return current != beginMarker.next;
    }
      
    // REQUIRED: Move the iterator backward and return the passed-over
    // REQUIRED: Move the iterator backward and return the passed-over
    // element.  If the list has been modified outside this iterator
    // throw a ConcurrentModificationException.
    // 
    // TARGET COMPLEXITY: O(1)
    public T previous( ){
        if( expectedModCount != modCount )
            throw new ConcurrentModificationException( );
          if( !hasPrevious( ) )
            throw new RuntimeException("Already at end of list"); 
          current = current.prev;
          //T previtem = current.data;
          lastVisited = current;
          lastMoveWasPrev = true;
          return current.data;
    }         

    // REQUIRED: Add the specified data to the list before the element
    // that would be returned by a call to next(). Ensure that the add
    // can be undone via the lists undo() method.  If the list has
    // been modified outside this iterator throw a
    // ConcurrentModificationException.
    // 
    // TARGET COMPLEXITY: O(1)
    public void add(T x){
    	if(modCount != expectedModCount)
    		throw new ConcurrentModificationException( );
    	Node<T> added = new Node<T>(null,null,null);
    	added.data = x;
    	added.prev = current.prev;    	current.prev.next = added;
    	added.next = current;
    	current.prev = added;

    	Node<T> undo = new Node<T>(null, added, null);
    	if(undobot.next == undoTop)
    		{
    			undobot.next = undo;
    			undoTop.prev = undo;
    			undo.next = undoTop;
    	    }
    	    else
    	    {
    	    	undo.next = undobot.next;
    	    	undobot.next = undo;

    	    }
    	redobot.next = redoTop;
    	redoTop.prev = redobot;
    	AdditiveList.this.theSize++;

    }        

    // The following methods may be optionally implemented but will
    // not be tested and will not garner any additional credit.
    // 
    // OPTIONAL: Return the integer index associated with the element
    // that would be returned by next()
    public int nextIndex(){
      throw new RuntimeException("Implement me for fun");
    }
    // OPTIONAL: Return the integer index associated with the element
    // that would be returned by previous()
    public int previousIndex(){
      throw new RuntimeException("Implement me for fun");
    }

    // The following operations are part of the ListIterator interface
    // but are not supported by AdditiveLists or their iterators. Both
    // will throw UnsupportedOperationException exceptions if invoked.
    public void set(T x){
      throw new UnsupportedOperationException();
    }
    public void remove( ) {
      throw new UnsupportedOperationException();
    }
  }
   
}
