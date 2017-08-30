package Hw4;
import java.util.Comparator;

// Immutable.  Stores 3 strings referred to as entity, relation, and
// property. Each Record has a unique integer ID which is set on
// creation.  All records are made through the factory method
// Record.makeRecord(e,r,p).  Record which have some fields wild are
// created using Record.makeQuery(wild,e,r,p)
public class Record{
	public static int staticID = 0;
	public int ID;
	public String  entity;
	public String relation;
	public String property;
	public static String wildcard;

  // Return the next ID that will be assigned to a Record on a call to
  // makeRecord() or makeQuery()
  public static int nextId()
  {
	  return staticID++;
  }

  // Return a stringy representation of the record. Each string should
  // be RIGHT justified in a field of 8 characters with whitespace
  // padding the left.  Java's String.format() is useful for padding
  // on the left.
  public String toString()
  {
	  StringBuilder str = new StringBuilder();
	  str.append(String.format("%8s %8s %8s ", entity, relation, property));
	  return str.toString();
  }

  // Return true if this Record matches the parameter record r and
  // false otherwise. Two records match if all their fields match.
  // Two fields match if the fields are identical or at least one of
  // the fields is wild.
  public boolean matches(Record r)
  {
	  
	  if(this.entity.equals(r.entity) && this.relation.equals(r.relation) && this.property.equals(r.property))
		  return true;
	  else if(this.entity.equals(r.entity) && this.relation.equals(r.relation) && this.property.equals(wildcard))
		  return true;
	  else if(this.entity.equals(r.entity) && this.relation.equals(wildcard) && this.property.equals(r.property))
		  return true;
	  else if(this.entity.equals(wildcard) && this.relation.equals(r.relation) && this.property.equals(r.property))
		  return true;
	  else if(this.entity.equals(r.entity) && this.relation.equals(wildcard) && this.property.equals(wildcard))
		  return true;
	  else if(this.entity.equals(wildcard) && this.relation.equals(r.relation) && this.property.equals(wildcard))
		  return true;
	  else if(this.entity.equals(wildcard) && this.relation.equals(wildcard) && this.property.equals(r.property))
		  return true;
	  else if(this.entity.equals(wildcard) && this.relation.equals(wildcard) && this.property.equals(wildcard))
		  return true;
	  else if(this.entity.equals(r.entity) && this.relation.equals(r.relation) && r.property.equals(wildcard))
		  return true;
	  else if(this.entity.equals(r.entity) && r.relation.equals(wildcard) && this.property.equals(r.property))
		  return true;
	  else if(r.entity.equals(wildcard) && this.relation.equals(r.relation) && this.property.equals(r.property))
		  return true;
	  else if(this.entity.equals(r.entity) && r.relation.equals(wildcard) && r.property.equals(wildcard))
		  return true;
	  else if(r.entity.equals(wildcard) && this.relation.equals(r.relation) && r.property.equals(wildcard))
		  return true;
	  else if(r.entity.equals(wildcard) && r.relation.equals(wildcard) && this.property.equals(r.property))
		  return true;
	  else if(r.entity.equals(wildcard) && r.relation.equals(wildcard) && r.property.equals(wildcard))
		  return true;
	  return false;
  }

  // Return this record's ID
  public int id()
  {
	  return this.ID;
  }

  // Accessor methods to access the 3 main fields of the record:
  // entity, relation, and property.
  public String entity()
  {
	  return this.entity;
  }

  public String relation()
  {
	  return this.relation;
  }

  public String property()
  {
	  return this.property;
  }

  // Returns true/false based on whether the the three fields are
  // fixed or wild.
  public boolean entityWild()
  {
	  if(this.entity == wildcard)
		  return true;
	  return false;
  }

  public boolean relationWild()
  {
	  if(this.relation == wildcard)
		  return true;
	  return false;
  }

  public boolean propertyWild()
  {
	  if(this.property == wildcard)
		  return true;
	  return false;
  }
  

  // Factory method to create a Record. No public constructor is
  // required.
  public static Record makeRecord(String entity, String relation, String property)
  {
	if(entity == null || relation == null || property == null)
		throw new IllegalArgumentException("Cannot set an element to null...");
	Record rec = new Record();
	rec.entity = entity;
	rec.relation = relation;
	rec.property = property;
	rec.ID = nextId();
	return rec;  
  }
  public static Record makeRecord(String entity, String relation, String property, String wild)
  {
	if(entity == null || relation == null || property == null)
		throw new IllegalArgumentException("Cannot set an element to null...");
	Record rec = new Record();
	rec.entity = entity;
	rec.relation = relation;
	rec.property = property;
	rec.wildcard = wild;
	rec.ID = nextId();
	return rec;  
  }

  // Create a record that has some fields wild. Any field that is
  // equal to the first argument wild will be a wild card
  public static Record makeQuery(String wild, String entity, String relation, String property)
  {
	  if(entity == null || relation == null || property == null || wild == null)
			throw new IllegalArgumentException("Cannot set an element to null...");
	  Record rec = new Record();
	  rec.wildcard = wild;
	  rec.entity = entity;
	  rec.relation = relation;
	  rec.property = property;
	  rec.ID = nextId();
	  return rec;
  }
  
  
  public static final Comparator<Record> ERPCompare = new Comparator<Record>()
		 {
	  		@Override
	  		public int compare(Record one, Record two)
	  		{
	  		  
	  		  if(two.entity.equals(two.wildcard) && !one.entity.equals(one.wildcard))
	  			  return 2;
	  		  if(one.entity.equals(one.wildcard) && !two.entity.equals(two.wildcard))
	  			  return -2;
	  		  if(one.entity.compareTo(two.entity) > 0 && !two.entity.equals(two.wildcard) && !one.entity.equals(one.wildcard))
	  			  return 1;
	  		  if(one.entity.compareTo(two.entity) < 0 && !two.entity.equals(two.wildcard) && !one.entity.equals(one.wildcard))
	  			  return -1;
	  		  if(one.entity.compareTo(two.entity) == 0)
	  		  {
	  			  if(two.relation.equals(two.wildcard) && !one.relation.equals(one.wildcard))
	  				  return 2;
	  			  if(one.relation.equals(one.wildcard) && !two.relation.equals(two.wildcard))
	  				  return -2;
	  			  if(one.relation.compareTo(two.relation) > 0 && !two.relation.equals(two.wildcard) && !one.relation.equals(one.wildcard))
	  				  return 1;
	  			  if(one.relation.compareTo(two.relation) < 0 && !two.relation.equals(two.wildcard) && !one.relation.equals(one.wildcard))
	  				  return -1;
	  			  if(one.relation.compareTo(two.relation) == 0)
	  			  {
	  				  if(two.property.equals(two.wildcard))
	  					  return 2;
	  				  if(one.property.equals(one.wildcard))
	  					  return -2;
	  				  if(one.property.compareTo(two.property) > 0)
	  					  return 1;
	  				  if(one.property.compareTo(two.property) < 0)
	  					  return -1;
	  			  }
	  		  }
	  		  return 0;
	  		}
		  };
		  
		  
  public static final Comparator<Record> RPECompare = new Comparator<Record>()
		  {
	  		@Override
	  		public int compare(Record one, Record two)
	  		{
	  		  if(two.relation.equals(two.wildcard) && !one.relation.equals(one.wildcard))
	  			  return 2;
	  		  if(one.relation.equals(one.wildcard) && !two.relation.equals(one.wildcard))
	  			  return -2;
	  		  if(one.relation.compareTo(two.relation) > 0 && !two.relation.equals(two.wildcard) && !one.relation.equals(one.wildcard))
	  			  return 1;
	  		  if(one.relation.compareTo(two.relation) < 0 && !two.relation.equals(two.wildcard) && !one.relation.equals(one.wildcard))
	  			  return -1;
	  		  if(one.relation.compareTo(two.relation) == 0)
	  		  {
	  			  if(two.property.equals(two.wildcard) && !one.property.equals(one.wildcard))
		  			  return 2;
		  		  if(one.property.equals(one.wildcard) && !two.property.equals(two.wildcard))
		  			  return -2;
	  			  if(one.property.compareTo(two.property) > 0 && !two.property.equals(two.wildcard) && !one.property.equals(one.wildcard))
	  				  return 1;
	  			  if(one.property.compareTo(two.property) < 0 && !two.property.equals(two.wildcard) && !one.property.equals(one.wildcard))
	  				  return -1;
	  			  if(one.property.compareTo(two.property) == 0)
	  			  {
	  				  if(two.entity.equals(two.wildcard))
	  					  return 2;
	  	  		      if(one.entity.equals(one.wildcard))
	  	  		    	  return -2;
	  				  if(one.entity.compareTo(two.entity) > 0)
	  					  return 1;
	  				  if(one.entity.compareTo(two.entity) < 0)
	  					  return -1;
	  			  }
	  		  }
	  		  return 0;
	  		}
		  };
		  
		  
  public static final Comparator<Record> PERCompare = new Comparator<Record>()
		{
	  	  @Override
		  public int compare(Record one, Record two)
			  	{
	  		  if(two.property.equals(two.wildcard) && !one.property.equals(one.wildcard))
	  			  return 2;
	  		  if(one.property.equals(one.wildcard) && !two.property.equals(two.wildcard))
	  			  return -2;
			  if(one.property.compareTo(two.property) > 0 && !two.property.equals(two.wildcard) && !one.property.equals(one.wildcard))
				  return 1;
			  if(one.property.compareTo(two.property) < 0 && !two.property.equals(two.wildcard) && !one.property.equals(one.wildcard))
				  return -1;
			  if(one.property.compareTo(two.property) == 0)
			  {
				  if(two.entity.equals(two.wildcard) && !one.entity.equals(one.wildcard))
		  			  return 2;
		  		  if(one.entity.equals(one.wildcard) && !two.entity.equals(two.wildcard))
		  			  return -2;
				  if(one.entity.compareTo(two.entity) > 0 && !two.entity.equals(two.wildcard) && !one.entity.equals(one.wildcard))
					  return 1;
				  if(one.entity.compareTo(two.entity) < 0 && !two.entity.equals(two.wildcard) && !one.entity.equals(one.wildcard))
					  return -1;
				  if(one.entity.compareTo(two.entity) == 0)
				  {
					  if(two.relation.equals(two.wildcard))
			  			  return 2;
			  		  if(one.relation.equals(one.wildcard))
			  			  return -2;
					  if(one.relation.compareTo(two.relation) > 0)
						  return 1;
					  if(one.relation.compareTo(two.relation) < 0)
						  return -1;
						  
				  }
			  }
			  return 0;
			  	}
		};
}
