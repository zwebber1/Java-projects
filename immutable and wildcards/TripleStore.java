package Hw4;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// Three-column database that supports query, add, and remove in
// logarithmic time.
public class TripleStore{
	public String wild;
	public TreeSet<Record> ERP;
	public TreeSet<Record> RPE;
	public TreeSet<Record> PER;

  // Create an empty TripleStore. Initializes storage trees
  public TripleStore()
  {
	  this.ERP = new TreeSet<Record>(Record.ERPCompare);
	  this.RPE = new TreeSet<Record>(Record.RPECompare);
	  this.PER = new TreeSet<Record>(Record.PERCompare);
	  this.wild= "*";
  }

  // Access the current wild card string for this TripleStore which
  // may be used to match multiple records during a query() or
  // remove() calll
  public String getWild()
  {
	  return wild;
  }

  // Set the current wild card string for this TripleStore
  public void setWild(String w)
  {
	  this.wild = w;
  }

  // Ensure that a record is present in the TripleStore by adding it
  // if necessary.  Returns true if the addition is made, false if the
  // Record was not added because it was a duplicate of an existing
  // entry. A Record with any fields may be added to the TripleStore
  // including a Record with fields that are equal to the
  // TripleStore's current wild card.
  // 
  // Target Complexity: O(log N)
  // N: number of records in the TripleStore
  public boolean add(String entity, String relation, String property)
  {
	  Record erp = Record.makeRecord(entity, relation, property);
	  //Record rpe = Record.makeRecord(relation, property, entity);
	  //Record per = Record.makeRecord(property, entity, relation);
	  erp.wildcard = wild;
	  if(ERP.contains(erp))
		  return false;
	  ERP.add(erp);
	  RPE.add(erp);
	  PER.add(erp);
	  return true;
	  
  }

  // Return a List of the Records that match the given query. If no
  // Records match, the returned list should be empty. If a String
  // matching the TripleStore's current wild card is used for one of
  // the fields of the query, multiple Records may be returned in the
  // match.  An appropriate tree must be selected and searched
  // correctly in order to meet the target complexity.
  // 
  // TARGET COMPLEXITY: O(K + log N) 
  // K: the number of matching records 
  // N: the number of records in the triplestore.
  public List<Record> query(String entity, String relation, String property)
  {
	  List<Record> list = new ArrayList<Record>();
	  Record query = Record.makeQuery(wild, entity, relation, property);
	  if(entity == wild || relation == wild || property == wild)
	  {
		  if(entity.equals(wild) && relation.equals(wild) && property.equals(wild))
		  {
			  Iterator<Record> iter = ERP.iterator();
			  while(iter.hasNext())
				  list.add(iter.next());
			  return list;
		  }
		  else if(entity.equals(wild) && relation.equals(wild))
		  {
			  Set<Record> set = PER.tailSet(query);
			  if(set.size() == 0)
				  set = PER.headSet(query);
			  Iterator<Record> iter = set.iterator();
			  while(iter.hasNext())
			  {
				  Record rec = iter.next();	
				  if(query.matches(rec))
						  {
					  			list.add(rec);
						  }
			  }
			  return list;
		  }
		  else if(entity.equals(wild) && property.equals(wild))
		  {
			  Set<Record> set = RPE.tailSet(query);
			  Iterator<Record> iter = set.iterator();
			  while(iter.hasNext())
			  {
				  Record rec = iter.next();	
				  if(query.matches(rec))
						  {
					  			list.add(rec);
						  }
			  }
			  return list;
		  }
		  else if(relation.equals(wild) && property.equals(wild))
		  {
			  Set<Record> set = ERP.tailSet(query);
			  if(set.size() == 0)
				  set = ERP.headSet(query);
			  System.out.println(set);
			  Iterator<Record> iter = set.iterator();
			  while(iter.hasNext())
			  {
				  Record rec = iter.next();	
				  if(query.matches(rec))
						  {
					  			list.add(rec);
						  }
			  }
			  return list;
		  }
		  else if(entity == wild)
		  {
			  Set<Record> set = RPE.tailSet(query);
			  Iterator<Record> iter = set.iterator();
			  while(iter.hasNext())
			  {
				  Record rec = iter.next();	
				  if(query.matches(rec))
						  {
					  			list.add(rec);
						  }
			  }
		  }
		  else if(relation == wild)
		  {
			  Set<Record> set = PER.tailSet(query);
			  Iterator<Record> iter = set.iterator();
			  while(iter.hasNext())
			  {
				  Record rec = iter.next();	
				  if(query.matches(rec))
						  {
					  			list.add(rec);
						  }
			  }
			  return list;
		  }
		  else if(property == wild)
		  {
			  Set<Record> set = ERP.tailSet(query);
			  Iterator<Record> iter = set.iterator();
			  while(iter.hasNext())
			  {
				  Record rec = iter.next();	
				  if(query.matches(rec))
						  {
					  			list.add(rec);
						  }
			  }
			  return list;
		  }
		  return list;
	  }
	  else
	  {
		  Record rec = Record.makeRecord(entity, relation, property);
		  if(ERP.contains(rec))
		  {
			  list.add(rec);
			  return list;
		  }
		  else
			  return list;
	  }
	  
  }

  // Remove elements from the TripleStore that match the parameter
  // query. If no Records match, no Records are removed.  Any of the
  // fields given may be the TripleStore's current wild card which may
  // lead to multiple Records bein matched and removed. Return the
  // number of records that are removed from the TripleStore.
  // 
  // TARGET COMPLEXITY: O(K * log N)
  // K: the number of matching records 
  // N: the number of records in the triplestore.
  public int remove(String e, String r, String p)
  {
	  int removed = 0;
	  List<Record> remove = new ArrayList<Record>();
	  remove = query(e,r,p);
	  ERP.removeAll(remove);
	  RPE.removeAll(remove);
	  PER.removeAll(remove);
	  removed = remove.size();
	  return removed;
  }

  // Produce a String representation of the TripleStore. Each Record
  // is formatted with its toString() method on its own line. Records
  // must be shown sorted by Entity, Relation, Property in the
  // returned String. 
  // 
  // TARGET COMPLEXITY: O(N)
  // N: the number of records stored in the TripleStore
  public String toString()
  {
	  Iterator<Record> iter = ERP.iterator();
	  StringBuilder str = new StringBuilder();
	  while(iter.hasNext())
	  {
		  str.append(iter.next());
		  str.append("\n");
	  }
	  return str.toString();
  }

}
