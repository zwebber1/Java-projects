import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;





// An implemntation of an ExpandbleBoard intended to favor reduced
// memory over speed of operations.  Most operations require O(E) time
// to complete where E is the number of non-fill elements that have
// been set on the board.  Internally, elements are stored in several
// lists sorted in different orders to facilitate the calculation of
// the longest sequence.
// 
// Target Space Complexity: O(E)
//  E: The number of elements that have been set on the board
public class SparseBoard<T> implements ExpandableBoard<T>{
	
	private int mincol, maxcol;
	private int minrow, maxrow;
	private AdditiveList<RowColElem<T>> rowcol;
	private AdditiveList<RowColElem<T>> colrow;
	private AdditiveList<RowColElem<T>> ndiag;
	private AdditiveList<RowColElem<T>> pdiag;
	private AdditiveList<RowColElem<T>> rctemp;
	private AdditiveList<RowColElem<T>> crtemp;
	private AdditiveList<RowColElem<T>> ndtemp;
	private AdditiveList<RowColElem<T>> pdtemp;
	private T fillElem;
	private T newfills;
	private boolean undid;
	private boolean matin;
	

  // Workhorse constructor.  Initially any get() should return the
  // fillElem specified. Set up all internal data structures to
  // facilitate longest sequence retrieval, undo/redo capabilities.
  // The fillElem cannot be null: passing null for this parameter will
  // result in a RuntimeException with the message "Cannot set
  // elements to null".  The runtime of the constructor should not
  // depend on the initial size of the board.
  //
  // Runtime: O(1) (worst-case)
  public SparseBoard(int minRow, int maxRow, int minCol, int maxCol, T fillElem)
  {
	  if(fillElem == null)
		  throw new RuntimeException("Cannot set elements to null");
	  this.minrow = minRow;
	  this.maxrow = maxRow;
	  this.mincol = minCol;
	  this.maxcol = maxCol;
	  this.fillElem = fillElem;
	  this.newfills = fillElem;
	  this.rowcol = new AdditiveList<RowColElem<T>>();
	  this.colrow = new AdditiveList<RowColElem<T>>();
	  this.ndiag = new AdditiveList<RowColElem<T>>();
	  this.pdiag = new AdditiveList<RowColElem<T>>();
	  this.rctemp = new AdditiveList<RowColElem<T>>();
	  this.crtemp = new AdditiveList<RowColElem<T>>();
	  this.ndtemp = new AdditiveList<RowColElem<T>>();
	  this.pdtemp = new AdditiveList<RowColElem<T>>();
	  this.undid = true;
	  this.matin = false;
	  
  }

  // Convenience 1-arg constructor, creates a single cell board with
  // given fill element. The initial extent of the board is a single
  // element at 0,0.  May wish to call the first constructor in this
  // one to minimize code duplication.
  public SparseBoard(T fillElem)
  {
	  this(0, 0, 0, 0, fillElem);
  }

  // Convenience 2-arg constructor, creates a board with given fill
  // element and copies elements from T 2-D array. Assumes upper left
  // is coordinate 0,0 and lower right is size of 2-D array.  The
  // board should not have any undo/redo history but should have a
  // longest sequence calculated from the contents of 2-D array.
  //
  // There is no target complexity for this method. Use of repeated
  // set() calls is suggested to simplify it.
  public SparseBoard(T[][] x, T fillElem)
  {
	  if(fillElem == null)
		  throw new RuntimeException("Cannot set elements to null");
	  this.minrow = 0;
	  this.maxrow = x.length - 1;
	  this.mincol = 0;
	  this.maxcol = x[0].length - 1;
	  this.fillElem = fillElem;
	  this.rowcol = new AdditiveList<RowColElem<T>>();
	  this.colrow = new AdditiveList<RowColElem<T>>();
	  this.ndiag = new AdditiveList<RowColElem<T>>();
	  this.pdiag = new AdditiveList<RowColElem<T>>();
	  this.rctemp = new AdditiveList<RowColElem<T>>();
	  this.crtemp = new AdditiveList<RowColElem<T>>();
	  this.ndtemp = new AdditiveList<RowColElem<T>>();
	  this.pdtemp = new AdditiveList<RowColElem<T>>();
	  this.newfills = fillElem;
	  this.matin = true;
	  this.undid = true;
	  T add;
	  for(int i = 0; i <= maxrow; i++)
	  {
		  for(int j = 0; j <= maxcol; j++)
		  {
			  if(x[i][j].equals(fillElem) == false)
			  {
				  add = x[i][j];
				  set(i, j, add);    //having trouble here when this is called it doesnt compute any of the longest sequences 
				  					//in set. set works fine when called outside this constructor
			  }
		  }
	  }
	  this.rowcol.clearHistory();
	  this.colrow.clearHistory();
	  this.ndiag.clearHistory();
	  this.pdiag.clearHistory();
			  
  }

  // Access the extent of the board: all explicitly set elements are
  // within the bounds established by these four methods.
  //
  // Target complexity: O(1)
  public int getMinRow()
  {
	  return this.minrow;
  }

  public int getMaxRow()
  {
	  return this.maxrow;
  }

  public int getMinCol()
  {
	  return this.mincol;
  }

  public int getMaxCol()
  {
	  return this.maxcol;
  }

  // Retrieve the fill element for the board.
  //
  // Target complexity: O(1) (worst-case)
  public T getFillElem()
  {
	  return this.newfills;
  }

  // Change the fill element for the board. To make this efficient,
  // only change an internal field which dictates what should be
  // returned when an element that has not been explicitly set is
  // requested via a call to get().
  //
  // Target complexity: O(1) (worst-case)
  public void setFillElem(T f)
  {
	  this.newfills = f;
  }
  private AdditiveList<RowColElem<T>> rowcollong()   //helper method to get the longest rowcol sequence
  {
	  RowColElem<T> comp = new RowColElem<T>(0,0,null);
	  RowColElem<T> comp2 = new RowColElem<T>(0,0,null);
	  RowColElem<T> comp3 = new RowColElem<T>(0,0,null);
	  AdditiveList<RowColElem<T>> longest = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> rclongtemp = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> rclongest = new AdditiveList<RowColElem<T>>();
	  ListIterator<RowColElem<T>> iter = rowcol.listIterator();
	  if(rowcol.isEmpty())
	  {
		  return longest;
	  }
	  if(rowcol.size() == 1)
	  {
		  comp = iter.next();
		  longest.add(comp);
		  return longest;
	  }
	  if(rowcol.size() == 2)
	  {
		  comp = iter.next();
		  longest.add(comp);
		  comp2 = iter.next();
		  if(comp.getElem().equals(comp2.getElem()))
		  {
			  longest.add(comp2);
			  return longest;
		  }
		  else
			  return longest;
	  }
	  else{
	  comp = iter.next();
	  comp2 = iter.next();
	  for(int i = 0; i <= rowcol.size() - 1; i++)
	  {
		  if(comp2.getRow() == comp.getRow() && comp.getElem().equals(comp2.getElem()))
		  {
			  if(comp.getCol() == comp2.getCol() - 1)
			  {
				  comp3 = comp2;
				  rclongtemp.add(comp);
				  comp = comp2;
				  if(iter.hasNext())
				  {
					  comp2 = iter.next();
				  }
			  }
			  else
			  {
				  comp = comp2;
				  if(iter.hasNext())
				  {
					  comp2 = iter.next();
				  }
			  }
		  }
		  else
		  {

			  if(comp == comp3)
			  {
				  rclongtemp.add(comp);
				  if(rclongest.isEmpty())
				  {
					  rclongest = rclongtemp;
				  }
				  if(rclongtemp.size() > rclongest.size())
				  {
					  rclongest = rclongtemp;
				  }
				  rclongtemp = new AdditiveList<RowColElem<T>>();
				  comp = comp2;
				  if(iter.hasNext())
				  {
					  comp2 = iter.next();
				  }
			  }
			  else
			  {
				  comp = comp2;
				  if(iter.hasNext())
				  {
					  comp2 = iter.next();
				  }
			  }
		  }
		  if(i == rowcol.size() - 1)
		  {
			  if(comp == comp3)
			  {
				  rclongtemp.add(comp);
			  }
			  if(rclongest.isEmpty())
			  {
				  rclongest = rclongtemp;
			  }
			  if(rclongtemp.size() > rclongest.size())
			  {
				  rclongest = rclongtemp;
			  }
			  rclongtemp = new AdditiveList<RowColElem<T>>();
		  }
	  } 
	  }
	  return rclongest;
  }
  
  private AdditiveList<RowColElem<T>> colrowlong() // helper method to get the longest column row sequence
  {
	  RowColElem<T> comp = new RowColElem<T>(0,0,null);
	  RowColElem<T> comp2 = new RowColElem<T>(0,0,null);
	  RowColElem<T> comp3 = new RowColElem<T>(0,0,null);
	  AdditiveList<RowColElem<T>> longest = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> crlongtemp = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> crlongest = new AdditiveList<RowColElem<T>>();
	  ListIterator<RowColElem<T>> iter2 = colrow.listIterator();
	  if(colrow.isEmpty())
	  {
		  return longest;
	  }
	  if(colrow.size() == 1)
	  {
		  comp = iter2.next();
		  longest.add(comp);
		  return longest;
	  }
	  if(colrow.size() == 2)
	  {
		  comp = iter2.next();
		  longest.add(comp);
		  comp2 = iter2.next();
		  if(comp.getElem().equals(comp2.getElem()))
		  {
			  longest.add(comp2);
			  return longest;
		  }
		  else
			  return longest;
	  }
	  else
	  {
	  comp = iter2.next();
	  comp2 = iter2.next();
	  for(int i = 0; i <= colrow.size() - 1; i++)
	  {
		  if(comp2.getCol() == comp.getCol() && comp.getElem().equals(comp2.getElem()))
		  {
			  if(comp.getRow() == comp2.getRow() + 1)
			  {
				  comp3 = comp2;
				  crlongtemp.add(comp);
				  comp = comp2;
				  if(iter2.hasNext())
				  {
					  comp2 = iter2.next();
				  }
			  }
			  else
			  {
				  comp = comp2;
				  if(iter2.hasNext())
				  {
					  comp2 = iter2.next();
				  }
			  }
		  }
		  else
		  {

			  if(comp == comp3)
			  {
				  crlongtemp.add(comp);
				  if(crlongest.isEmpty())
				  {
					  crlongest = crlongtemp;

				  }
				  if(crlongtemp.size() > crlongest.size())
				  {
					  crlongest = crlongtemp;
				  }
				  crlongtemp = new AdditiveList<RowColElem<T>>();
				  comp = comp2;
				  if(iter2.hasNext())
				  {
					  comp2 = iter2.next();
				  }
			  }
			  else
			  {
				  comp = comp2;
				  if(iter2.hasNext())
				  {
					  comp2 = iter2.next();
				  }
			  }
		  }
		  if(i == colrow.size() - 1)
		  {
			  if(comp == comp3)
			  {
				  crlongtemp.add(comp);
			  }
			  if(crlongest.isEmpty())
			  {
				  crlongest = crlongtemp;
			  }
			  if(crlongtemp.size() > crlongest.size())
			  {
				  crlongest = crlongtemp;
			  }
			  crlongtemp = new AdditiveList<RowColElem<T>>();
		  }
	  }
	  }
	  return crlongest;
  }
  private AdditiveList<RowColElem<T>> ndlong() // helper to get the longest diagonal sequence
  {
	  RowColElem<T> comp = new RowColElem<T>(0,0,null);
	  RowColElem<T> comp2 = new RowColElem<T>(0,0,null);
	  RowColElem<T> comp3 = new RowColElem<T>(0,0,null);
	  AdditiveList<RowColElem<T>> longest = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> ndlongtemp = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> ndlongest = new AdditiveList<RowColElem<T>>();
	  ListIterator<RowColElem<T>> iter3 = ndiag.listIterator();
	  if(ndiag.isEmpty())
	  {
		  return longest;
	  }
	  if(ndiag.size() == 1)
	  {
		  comp = iter3.next();
		  longest.add(comp);
		  return longest;
	  }
	  if(ndiag.size() == 2)
	  {
		  comp = iter3.next();
		  longest.add(comp);
		  comp2 = iter3.next();
		  if(comp.getElem().equals(comp2.getElem()))
		  {
			  longest.add(comp2);
			  return longest;
		  }
		  else
			  return longest;
	  }
	  else{
		  comp = iter3.next();
		  comp2 = iter3.next();
		  for(int i = 0; i <= ndiag.size() - 1; i++)
		  {
			  if(comp.getRow() == comp2.getRow() - 1 && comp.getElem().equals(comp2.getElem()))
			  {
				  if(comp.getCol() == comp2.getCol() - 1)
				  {
					  comp3 = comp2;
					  ndlongtemp.add(comp);
					  comp = comp2;
					  if(iter3.hasNext())
					  {
						  comp2 = iter3.next();
					  }
				  }
				  else
				  {
					  comp = comp2;
					  if(iter3.hasNext())
					  {
						  comp2 = iter3.next();
					  }
				  }
			  }
			  else
			  {

				  if(comp == comp3)
				  {
					  ndlongtemp.add(comp);
					  if(ndlongest.isEmpty())
					  {
						  ndlongest = ndlongtemp;

					  }
					  if(ndlongtemp.size() > ndlongest.size())
					  {
						  ndlongest = ndlongtemp;
					  }
					  ndlongtemp = new AdditiveList<RowColElem<T>>();
					  comp = comp2;
					  if(iter3.hasNext())
					  {
						  comp2 = iter3.next();
					  }
				  }
				  else
				  {
					  comp = comp2;
					  if(iter3.hasNext())
					  {
						  comp2 = iter3.next();
					  }
				  }
			  }
			  if(i == ndiag.size() - 1)
			  {
				  if(comp == comp3)
				  {
					  ndlongtemp.add(comp);
				  }
				  if(ndlongest.isEmpty())
				  {
					  ndlongest = ndlongtemp;
				  }
				  if(ndlongtemp.size() > ndlongest.size())
				  {
					  ndlongest = ndlongtemp;
				  }
				  ndlongtemp = new AdditiveList<RowColElem<T>>();
			  }
		  }
	  }
	  return ndlongest;
  }
  private AdditiveList<RowColElem<T>> pdlong()   //helper to get the longest anti diagonal sequence
  {
	  RowColElem<T> comp = new RowColElem<T>(0,0,null);
	  RowColElem<T> comp2 = new RowColElem<T>(0,0,null);
	  RowColElem<T> comp3 = new RowColElem<T>(0,0,null);
	  AdditiveList<RowColElem<T>> longest = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> pdlongtemp = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> pdlongest = new AdditiveList<RowColElem<T>>();
	  ListIterator<RowColElem<T>> iter4 = pdiag.listIterator();
	  if(pdiag.isEmpty())
	  {
		  return longest;
	  }
	  if(pdiag.size() == 1)
	  {
		  comp = iter4.next();
		  longest.add(comp);
		  return longest;
	  }
	  if(pdiag.size() == 2)
	  {
		  comp = iter4.next();
		  longest.add(comp);
		  comp2 = iter4.next();
		  if(comp.getElem().equals(comp2.getElem()))
		  {
			  longest.add(comp2);
			  return longest;
		  }
		  else
			  return longest;
	  }
	  else{
		  comp = iter4.next();
		  comp2 = iter4.next();
		  for(int i = 0; i <= pdiag.size() - 1; i++)
		  {
			  if(comp.getRow() == comp2.getRow() + 1 && comp.getElem().equals(comp2.getElem()))
			  {
				  if(comp.getCol() == comp2.getCol() - 1)
				  {
					  comp3 = comp2;
					  pdlongtemp.add(comp);
					  comp = comp2;
					  if(iter4.hasNext())
					  {
						  comp2 = iter4.next();
					  }
				  }
				  else
				  {
					  comp = comp2;
					  if(iter4.hasNext())
					  {
						  comp2 = iter4.next();
					  }
				  }
			  }
			  else
			  {

				  if(comp == comp3)
				  {
					  pdlongtemp.add(comp);
					  if(pdlongest.isEmpty())
					  {
						  pdlongest = pdlongtemp;

					  }
					  if(pdlongtemp.size() > pdlongest.size())
					  {
						  pdlongest = pdlongtemp;
					  }
					  pdlongtemp = new AdditiveList<RowColElem<T>>();
					  comp = comp2;
					  if(iter4.hasNext())
					  {
						  comp2 = iter4.next();
					  }
				  }
				  else
				  {
					  comp = comp2;
					  if(iter4.hasNext())
					  {
						  comp2 = iter4.next();
					  }
				  }
			  }
			  if(i == pdiag.size() - 1)
			  {
				  if(comp == comp3)
				  {
					  pdlongtemp.add(comp);
				  }
				  if(pdlongest.isEmpty())
				  {
					  pdlongest = pdlongtemp;
				  }
				  if(pdlongtemp.size() > pdlongest.size())
				  {
					  pdlongest = pdlongtemp;
				  }
				  pdlongtemp = new AdditiveList<RowColElem<T>>();
			  }
		  }
		  }
	  
  	return pdlongest;
  }

  // Retrieve the longest sequence present on the board. If there is a
  // tie, the earliest longest sequence to appear on the board should
  // be returned.  The list returned should be independent of any
  // internal board data structures so that the list can be changed
  // and not affect the board.  This implies a copy of any internal
  // board lists should be made and returned.  The longest sequence on
  // a board that is filled with only the fill element is the empty
  // list [].
  //
  // Target Complexity: O(L) (worst case)
  // L: length of the longest sequence
  public List< RowColElem<T> > getLongestSequence()
  {
	  RowColElem<T> comp = new RowColElem<T>(0,0,null);
	  AdditiveList<RowColElem<T>> longest = new AdditiveList<RowColElem<T>>();
	  List<RowColElem<T>> finallong = new LinkedList<RowColElem<T>>();
	 
	  if(undid == true || matin == true)
	  {
		  rctemp = rowcollong();
		  crtemp = colrowlong();
		  ndtemp = ndlong();
		  pdtemp = pdlong();
	  }
	  longest = rctemp;
	  if(crtemp.size() > longest.size())
		  longest = crtemp;
	  if(ndtemp.size() > longest.size())
		  longest = ndtemp;
	  if(pdtemp.size() > longest.size())
		  longest = pdtemp;
	  ListIterator<RowColElem<T>> iters = longest.listIterator();
	  while(iters.hasNext())
	  {
		  comp = iters.next();
		  finallong.add(comp);
	  }
	  return finallong;
  }
  

  // Retrieve an element at virtual row/col specified. Any row/col may
  // be requested. If it is beyond the extent of the board determined
  // by min/max row/col, the fill element is returned.  If the element
  // has not been explicitly set, the fill element is returned.
  // 
  // Complexity: O(E)
  //  E: The number of elements that have been set on the board
  public T get(int row, int col)
  {
	  T returnval = null;
	  if(row > maxrow || row < minrow || col > maxcol || col < mincol)
		  returnval = newfills;
	  if(rowcol.isEmpty())
		  returnval = newfills;
	  else
	  {
		  RowColElem<T> comp = new RowColElem<T>(0,0,null);
		  ListIterator<RowColElem<T>> iter = rowcol.listIterator();
		  comp = iter.next();
		  for(int i = 0; i <= rowcol.size(); i++)
		  {
			  if(comp.getRow() == row && comp.getCol() == col)
			  {
				  returnval = comp.getElem();
			  }
			  else
			  {
				  if(iter.hasNext())
					  comp = iter.next();
				  else
					  returnval = newfills;
			  }
		  }
	  }
	  return(returnval);
	  
  }

  // Update internals to reflect an increase in the board extents by
  // one row on the bottom.  This method should not change the memory
  // footprint of the SparseBoard.
  // 
  // Target Complexity: O(1) (worst-case)
  public void addRowBottom()
  {
	  this.maxrow++;
  }

  // Update internals to reflect an increase in the board extents by
  // one column on the right.  This method should not change the
  // memory footprint of the SparseBoard.
  //
  // Target Complexity: O(1) (worst-case)
  public void addColRight()
  {
	  this.maxcol++;
  }

  // Perform expansion for the board. Adjust any internal fields so
  // that the board tells the world it is large enough to include the
  // (row,col) position specified.  No new memory should be allocated.
  // Always return 0.
  //
  // Target Complexity: O(1) (worst-case)
  public int expandToInclude(int row, int col)
  {
	  if( row < minrow)
		  minrow = row;
	  if( row > maxrow)
		  maxrow = row;
	  if( col < mincol)
		  mincol = col;
	  if( col > maxcol)
		  maxcol = col;
	  return 0;
  }

  // Set element at row/col position to be x. Update internals to
  // reflect that the set may have created a new longest sequence.
  // Also update internals to allow undoSet() to be used and disable
  // redoSet() until a set has been undone.  Once an element is set,
  // it cannot be set again; attempts to do so raise a runtime
  // exception with the message: "Element 4 -2 already set to XX"
  // where the row/col indices and string representation of the
  // element are adjusted to match the call made.  Setting an element
  // to the fill element of board has no effect on the board.  It is
  // not allowed to set elements of the board to be null. Attempting
  // to do so will generate a RuntimeException with the message
  // "Cannot set elements to null"
  //
  // Target Complexity: O(E)
  //  E: The number of elements that have been set on the board
  public void set(int row, int col, T x)
  {
	  if(x == null)
		  throw new RuntimeException("Cannot set elements to null");
	  RowColElem<T> add = new RowColElem<T>(row, col, x);
	  if(x != newfills)
	  {
	  if(row > maxrow)
		  expandToInclude(row, mincol);
	  if(col > maxcol)
		  expandToInclude(minrow, col);
	  if(row < minrow)
		  expandToInclude(row, mincol);
	  if(col < mincol)
		  expandToInclude(minrow, col);
	  }
	  if(rowcol.isEmpty())
	  {
		  rowcol.add(add);
		  colrow.add(add);
		  pdiag.add(add);
		  ndiag.add(add);
		  return;
	  }
	  else
	  {
		  ListIterator<RowColElem<T>> itertemp = rowcol.listIterator();
		  RowColElem<T> already = new RowColElem<T>(0, 0, null);
		  while(itertemp.hasNext())
		  {
			  already = itertemp.next();
			  if(already.getRow() == row && already.getCol() == col)
			  {
				  throw new RuntimeException(String.format("Element %d %d already set to %s", row, col, already.getElem() ));
			  }
		  }
		  boolean added = false;
		  ListIterator<RowColElem<T>> iter = rowcol.listIterator();
		  RowColElem<T> comp = new RowColElem<T>(0, 0, null);
		  comp = iter.next();
		  while(added == false)									//add to rowcol
		  {
			  if(add.getRow() <= comp.getRow())
			  {
				  if(add.getRow() < comp.getRow())
				  {
					  iter.previous();
					  iter.add(add);
					  added = true;
					  break;
				  }
				  if(add.getRow() == comp.getRow())
				  {
					  if(add.getCol() <= comp.getCol())
					  {
						  iter.previous();
						  iter.add(add);
						  added = true;
						  break;
					  }
					  else
					  {  
						  if(iter.hasNext())
						  {
							  comp = iter.next();
						  }
						  else
						  {
							  iter.add(add);
							  added = true;
							  break;
						  } 
					  }
				  }
			  }	  
			  else
			  {
				  if(iter.hasNext())
				  {
					  comp = iter.next();
				  }
				  else
				  {
					  iter.add(add);
					  added = true;
					  break;
				  }
			  }
		  }  
		  added = false;
		  ListIterator<RowColElem<T>> iter2 = colrow.listIterator();
		  comp = iter2.next();
	  while(added == false)										//add to colrow
	  {
		  if(add.getCol() <= comp.getCol())
		  {
			  if(add.getCol() < comp.getCol())
			  {
				  iter2.previous();
				  iter2.add(add);
				  added = true;
				  break;
			  }
			  if(add.getCol() == comp.getCol())
			  {
				  if(add.getRow() <= comp.getRow())
				  {
					  iter2.previous();
					  iter2.add(add);
					  added = true;
					  break;
				  }
				  else
				  {
					  if(iter2.hasNext())
					  {
						  comp = iter2.next();
					  }
					  else
					  {
						  iter2.add(add);
						  added = true;
						  break;
					  } 
				  }
			  }
		  }	  
		  else
		  {
			  if(iter2.hasNext())
			  {
				  comp = iter2.next();
			  }
			  else
			  {
				  iter2.add(add);
				  added = true;
				  break;
			  }
		  }
	  }
	  added = false;
	  ListIterator<RowColElem<T>> iter3 = ndiag.listIterator();
	  comp = iter3.next();
	  while(added == false)
	  {
		  if((add.getRow() - add.getCol()) < (comp.getRow() - comp.getCol()))
		  {
			  if(add.getRow() > comp.getRow())
			  {
				  if(iter3.hasNext())
					  comp = iter3.next();
				  else
				  {
					iter3.add(add);
				  	added = true;
				  	break;
				  }
			  }
			  else
			  {
			  if(iter3.hasNext())
				  comp = iter3.next();
			  else
			  {
				iter3.add(add);
			  	added = true;
			  	break;
			  }
			  }
		  }
		  if((add.getRow() - add.getCol()) > (comp.getRow() - comp.getCol()))
		  {
			  iter3.previous();
			  iter3.add(add);
			  added = true;
			  break;
		  }
		  if((add.getRow() - add.getCol()) == (comp.getRow() - comp.getCol()))
		  {
			  if(add.getRow() < comp.getRow())
			  {
				  iter3.previous();
				  iter3.add(add);
				  added = true;
				  break;
			  }
			  if(add.getRow() > comp.getRow() && add.getCol() == comp.getCol() + 1)
			  {
				  iter3.add(add);
				  added = true;
				  break;
			  }
			  else
			  {
				  if(iter3.hasNext())
					  comp = iter3.next();
				  else
				  {
					iter3.add(add);
				  	added = true;
				  	break;
				  }
			  }
		  }
	  }
	  added = false;
	  ListIterator<RowColElem<T>> iter4 = pdiag.listIterator();
	  comp = iter4.next();
	  while(added == false)
	  {
		  if((add.getRow() + add.getCol()) > (comp.getRow() + comp.getCol()))
		  {
			  if(add.getRow() > comp.getRow())
			  {
				  if(iter4.hasNext())
					  comp = iter4.next();
				  else
				  {
					iter4.add(add);
				  	added = true;
				  	break;
				  }
			  }
			  else
			  {
			  if(iter4.hasNext())
				  comp = iter4.next();
			  else
			  {
				iter4.add(add);
			  	added = true;
			  	break;
			  }
			  }
		  }
		  if((add.getRow() + add.getCol()) < (comp.getRow() + comp.getCol()))
		  {
			  iter4.previous();
			  iter4.add(add);
			  added = true;
			  break;
		  }
		  if((add.getRow() + add.getCol()) == (comp.getRow() + comp.getCol()))
		  {
			  if(add.getRow() > comp.getRow())
			  {
				  iter4.previous();
				  iter4.add(add);
				  added = true;
				  break;
			  }
			  if(add.getRow() < comp.getRow() && add.getCol() == comp.getCol() - 1)
			  {
				  iter4.previous();
				  iter4.add(add);
				  added = true;
				  break;
			  }
			  else
			  {
				  if(iter4.hasNext())
					  comp = iter4.next();
				  else
				  {
					iter4.add(add);
				  	added = true;
				  	break;
				  }
			  }
		  }
	  }
	  }
	  AdditiveList<RowColElem<T>> rcTemp = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> crTemp = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> ndTemp = new AdditiveList<RowColElem<T>>();
	  AdditiveList<RowColElem<T>> pdTemp = new AdditiveList<RowColElem<T>>();
	  rcTemp = rowcollong();
	  if(rcTemp.size() > rctemp.size())
	  {
		  rctemp = rcTemp;
	  }
	  crTemp = colrowlong();
	  if(crTemp.size() > crtemp.size())
	  {
		  crtemp = crTemp;
	  }
	  ndTemp = ndlong();
	  if(ndTemp.size() > ndtemp.size())
	  {
		  ndtemp = ndTemp;
	  }
	  pdTemp = pdlong();
	  if(pdTemp.size() > pdtemp.size())
	  {
		  pdtemp = pdTemp;
	  }
  }

  // Produce copies of the internal lists of the explicitly set
  // elements on the board.  Only elements that have been explictily
  // set should be included. Each method produces a list that is
  // sorted in an order dictated by the method name. The returned
  // lists should be copies so that subsequenent modification to the
  // lists does not affect the board.
  //
  // Target Complexity: O(E) (worst-case)
  public List<RowColElem<T>> elementsInRowColOrder()
  {
	 List<RowColElem<T>> rccop = new LinkedList<RowColElem<T>>();
	  ListIterator<RowColElem<T>> iter3 = rowcol.listIterator();
	  RowColElem<T> comp2 = new RowColElem<T>(0,0,null);
	 while(iter3.hasNext())
	 {
		 comp2 = iter3.next();
		 rccop.add(comp2);
	 }
		 
	  return rccop;
  }

  public List<RowColElem<T>> elementsInColRowOrder()
  {
	  List<RowColElem<T>> crcop = new LinkedList<RowColElem<T>>();
	  ListIterator<RowColElem<T>> iter = colrow.listIterator();
	  RowColElem<T> comp = new RowColElem<T>(0,0,null);
	 while(iter.hasNext())
	 {
		 comp = iter.next();
		 crcop.add(comp);
	 }
		 
	  return crcop;
  }

  public List<RowColElem<T>> elementsInDiagRowOrder()
  {
	  List<RowColElem<T>> ndcop = new LinkedList<RowColElem<T>>();
	  ListIterator<RowColElem<T>> iter = ndiag.listIterator();
	  RowColElem<T> comp = new RowColElem<T>(0,0,null);
	 while(iter.hasNext())
	 {
		 comp = iter.next();
		 ndcop.add(comp);
	 }
		 
	  return ndcop;
  }

  public List<RowColElem<T>> elementsInADiagReverseRowOrder()
  {
	  List<RowColElem<T>> pdcop = new LinkedList<RowColElem<T>>();
	  ListIterator<RowColElem<T>> iter = pdiag.listIterator();
	  RowColElem<T> comp = new RowColElem<T>(0,0,null);
	 while(iter.hasNext())
	 {
		 comp = iter.next();
		 pdcop.add(comp);
	 }
		 
	  return pdcop;
  }

  // Undo an explicit set(row,col,x) operation by changing an element
  // to its previous state.  Repeated calls to undoSet() can be made
  // to restore the board to an earlier state.  Each call to undoSet()
  // enables a call to redoSet() to be made to move forward in the
  // history of the board state. Calls to undoSet() do not change the
  // extent of boards: they do not shrink to a smaller size once grown
  // even after an undo call.  If there are no sets to undo, this
  // method throws a runtime exception with the message
  // "Undo history is empty"
  //
  // Target Complexity: O(1) (worst case)
  public void undoSet()
  {
	  undid = true;
	  rowcol.undo();
	  colrow.undo();
	  pdiag.undo();
	  ndiag.undo();
  }

  // Redo a set that was undone via undoSet().  Every call to
  // undoSet() moves backward in the history of the board state and
  // enables a corresponding call to redoSet() which will move forward
  // in the history.  At any point, a call to set(row,col,x) will
  // erase 'future' history that can be redone via redoSet().  If
  // there are no moves that can be redone because of a call to set()
  // or undoSet() has not been called, this method generates a
  // RuntimeException with the message "Redo history is empty".
  //
  // Target Complexity: O(1)
  public void redoSet()
  {
	  rowcol.redo();
	  colrow.redo();
	  pdiag.redo();
	  ndiag.redo();
  }

  // toString() - create a pretty representation of board.
  //
  // Examples:
  //   |  1|  2|  3|
  //   +---+---+---+
  // 1 |   |   |   |
  //   +---+---+---+
  // 2 |   |   |   |
  //   +---+---+---+
  // 3 |   |   |   |
  //   +---+---+---+
  //
  //    | -4| -3| -2| -1|  0|  1|  2|  3|
  //    +---+---+---+---+---+---+---+---+
  // -2 |  A|   |   |   |   |   |   |   |
  //    +---+---+---+---+---+---+---+---+
  // -1 |   |   |  B|   |   |   |   |   |
  //    +---+---+---+---+---+---+---+---+
  //  0 |   |   |   |   |   |   |   |   |
  //    +---+---+---+---+---+---+---+---+
  //  1 |   |   |   |   |   |  A|   |   |
  //    +---+---+---+---+---+---+---+---+
  //  2 |   |   |   |   |   |   |   |   |
  //    +---+---+---+---+---+---+---+---+
  //  3 |   |   |   |   |   |   |   |   |
  //    +---+---+---+---+---+---+---+---+
  //
  // Target Complexity: O(R*C)
  //   R: number of rows
  //   C: number of columns
  // 
  // Constraint: No array or arraylist allocation is allowed in this
  // method.
  // 
  // Note: to adhere to target runtime complexity employ a
  // StringBuilder and an iterator over an internal list of the boards
  // set elements.
  public String toString()
  {
	  T elem = null;
	  boolean getnext = true;
	  StringBuilder str = new StringBuilder();
	  List<RowColElem<T>> temp = new LinkedList<RowColElem<T>>();
	  temp = elementsInRowColOrder();
	  ListIterator<RowColElem<T>> iter = temp.listIterator();
	  RowColElem<T> comp = new RowColElem<T>(0 ,0 ,elem);
	  if(!rowcol.isEmpty())
	  {
		  comp = iter.next();
	  }
	  else
	  {
		  getnext = false;
	  }
	  for(int col1 = mincol; col1 <= maxcol; col1++)
	  {
	   if(col1 == mincol)
	    str.append("    ");
	   str.append(String.format("|%3d", col1));
	  }
	  str.append("|");
	  str.append('\n');
	  for(int k = mincol; k <= maxcol; k++)
	  {
	   if(k == mincol)
	    str.append("    ");
	   str.append("+---");
	  }
	  str.append("+");
	  str.append('\n');
	  for(int row = minrow; row <= maxrow; row++)
	  {
	   boolean firstloop = true;
	   for(int col = mincol; col <= maxcol;)
	   {
	    
	    if(firstloop == true)
	    {

	        str.append(String.format("%3d",row));
	        str.append(" ");
	        firstloop = false;
	    }
	    else
	    {
	     str.append("|");
	     if(comp.getCol() == col && comp.getRow() == row && getnext == true)
		  {
	    	 if(comp.getElem().equals(fillElem))
	    	 {
	    		 str.append(String.format("%3s", newfills));
	    	 }
	    	 else
	    	 {
			  str.append(String.format("%3s", comp.getElem()));
	    	 }
			  if(iter.hasNext())
			  {
				  comp = iter.next();
				  getnext = true;
			  }
			  else
			  {
				  getnext = false;
			  }
			  	
			  
		  }
		  else
		  {
			  str.append(String.format("%3s", newfills));
		  }
	     col++;
	    }
	    
	   }
	   str.append("|");
	   str.append('\n');
	   for(int f = mincol; f <= maxcol; f++)
	   {
	    if(f == mincol)
	     str.append("    ");
	    str.append("+---");
	   }
	   str.append("+");
	   str.append('\n');
	  }
	  return str.toString();
  }

}
