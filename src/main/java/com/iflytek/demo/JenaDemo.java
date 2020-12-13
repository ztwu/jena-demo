package com.iflytek.demo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

/**
 * created with idea
 * user:ztwu
 * date:2020/2/4
 * description
 */
public class JenaDemo {
    public static void main(String args[]) throws IOException {
        // some definitions
        String personURI = "http://somewhere/吴彰涛";
        String givenName = "彰涛";
        String familyName = "吴";
        String fullName = givenName + " " + familyName;

        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // create the resource
        // and add the properties cascading style
        Resource johnSmith = model.createResource(personURI)
                .addProperty(VCARD.FN, fullName)
                .addProperty(VCARD.N,
                        model.createResource().
                            addProperty(VCARD.Given, givenName).
                            addProperty(VCARD.Family, familyName));
        model.write(new FileWriter(new File("data/rdf/demo.rdf")));

        // list the statements in the graph
        StmtIterator iter = model.listStatements();
        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject(); // get the subject
            Property predicate = stmt.getPredicate(); // get the predicate
            RDFNode object = stmt.getObject(); // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");
        }
    }
}
