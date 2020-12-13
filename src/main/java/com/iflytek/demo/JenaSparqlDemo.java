package com.iflytek.demo;

import org.apache.jena.query.*;

public class JenaSparqlDemo {
    public static void main(String[] args) {
        String sparqlQueryString1 = "PREFIX dbont: <http://dbpedia.org/ontology/> " +
                "PREFIX dbp: <http://dbpedia.org/property/>" +
                "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>" +
                "   SELECT ?musician  ?place" +
                "   FROM<http://dbpedia.org/resource/Daphne_Oram>" +
                "   WHERE { " +
                "       ?musician dbont:birthPlace ?place ." +
                "   }";

        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.
                sparqlService("http://dbpedia.org/sparql", query);

        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(System.out, results, query);

        qexec.close() ;
    }
}
