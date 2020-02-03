import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.*;
import org.apache.jena.vocabulary.RDF;

public class Simpsons {
	
																// Exercise 1 :  Read input
	
// Create Model
	public Model model;
public Simpsons() {
		model = ModelFactory.createDefaultModel();}


// Read File & Return Prefix
String xdfPrefix, rdfPrefix, simPrefix, famPrefix, foafPrefix;

public Simpsons readFile(String infile) {
	model.read( infile, FileUtils.guessLang(infile) );
	xdfPrefix = model.getNsPrefixURI("xdf");
	rdfPrefix = model.getNsPrefixURI("rdf");
	famPrefix = model.getNsPrefixURI("fam");
	simPrefix = model.getNsPrefixURI("sim");
	foafPrefix = model.getNsPrefixURI("foaf");
return this; }


																// Exercise 2 : Adding information
public static Simpsons create() {
	return new Simpsons();
}

//Defining sim:firstname rdf:type foaf:name
public Resource addNewFamillyMember(String name) {
	String firstname = name.split(" ")[0];

	// Adding (name)property
	Resource Simpson = model.createResource( prefix(name, simPrefix) );
	Simpson.addProperty(RDF.type, FOAF.Person);
	Simpson.addProperty(FOAF.name, firstname);
	return Simpson; }

public String prefix(String firstname, String simPrefix) {
	return simPrefix + firstname;
}

//Defining foaf:age
public Resource addNewFamillyMember(String name, Integer age) {
	Resource Simpson = addNewFamillyMember(name);

	// Adding (age)property 
	Property addAge = model.createProperty( prefix("age", foafPrefix) );
	Simpson.addProperty(addAge, age.toString(), XSDDatatype.XSDint);
	return Simpson;}

// Defining Marriage
private void marraige(Resource partnerA, Resource partnerB) {
	Property spouse = model.createProperty( prefix("hasSpouse", famPrefix) ); 
	partnerA.addProperty(spouse, partnerB);
	partnerB.addProperty(spouse, partnerA);
}

//Defining Father Relationship
private void FatherRel(Resource father, Resource child) {
	Property hasFather = model.createProperty( prefix("hasFather", famPrefix) );
	child.addProperty(hasFather, father);
}

// Adding persons to the family with names, ages, spouse and parental relationships

public Simpsons Excersies2() {
	
	addNewFamillyMember("Maggie Simpson", 1);
	Resource Mona = addNewFamillyMember("Mona Simpson", 70);
	Resource Abraham = addNewFamillyMember("Abraham Simpson", 78);
	Resource Herb = addNewFamillyMember("Herb Simpson", 0);
	marraige(Abraham, Mona);
    FatherRel(model.createResource(), Herb);
	
	return this;}

//Locate, read and write information
private Simpsons typeOfPerson() {
	Property ageProperty = model.createProperty( prefix("age", foafPrefix) );
    Iterator<Statement> statements = model.listStatements((Resource) null, ageProperty, (Resource) null);
	
	while(statements.hasNext()) {
		Statement stat = statements.next();
		Literal ageLiteral = (Literal) stat.getObject();
		Integer age = ageLiteral.getInt();
		Resource Simpsons = (Resource) stat.getSubject();
		
		//Check for minors
		typeOfAge(Simpsons, age);
	}
	
	return this;
}

private void typeOfAge(Resource Simpson, Integer age) {
	// New resource
	Resource infant = model.createResource( prefix("Infant", famPrefix) );
	Resource minor = model.createResource( prefix("Minor", famPrefix) );
	Resource old = model.createResource( prefix("Old", famPrefix) );
	
	if (age < 18) {
		Simpson.addProperty(RDF.type, minor);
		
	if (age < 2) {
			Simpson.addProperty(RDF.type, infant);}
	}
	
	if (age > 70) {
		Simpson.addProperty(RDF.type, old); }
}

// Output file
public Simpsons OutFile(String OutFile) {
	    PrintWriter output = null;
		try {
			output = new PrintWriter(OutFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		model.write( output, FileUtils.guessLang(OutFile) );
	return this; }

//Execution 


public static void main(String[] args){
	String inFile, outFile;
	Simpsons simpsons;
	inFile = args[0];
	outFile = args[1];
	
	simpsons = Simpsons.create();
	simpsons.readFile(inFile)
			.Excersies2()
			.typeOfPerson()
			.OutFile(outFile);
}

}
