package com.iflytek.demo;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * created with idea
 * user:ztwu
 * date:2020/2/7
 * description
 */
public class JenaDemo2 {

    public static void main(String args[])
    {
        String inputFileName = "data/nt/instancetypemultypeperson.nt";

        Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException("File: " + inputFileName + " not found");
        }

        //model.read(in, "","RDF/XML");//根据文件格式选用参数即可解析不同类型
        //model.read(in, "","N3");
        model.read(in, "","TTL");
        System.out.println("开始");
        // list the statements in the graph
        StmtIterator iter = model.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext())
        {
            Statement stmt = iter.nextStatement(); // get next statement
            //Resource subject = stmt.getSubject(); // get the subject
            //Property predicate = stmt.getPredicate(); // get the predicate
            //RDFNode object = stmt.getObject(); // get the object

            String subject = stmt.getSubject().toString(); // get the subject
            String predicate = stmt.getPredicate().toString(); // get the predicate
            RDFNode object = stmt.getObject(); // get the object

            if (object instanceof Resource)
            {
                try {
                    String objecttemp = URLDecoder.decode(object.toString(),"UTF-8");
                    String subjecttemp = URLDecoder.decode(subject.toString(),"UTF-8");
                    String predicatetemp = URLDecoder.decode(predicate.toString(),"UTF-8");
                    System.out.print("主语 " + subjecttemp+"\t");
                    System.out.print(" 谓语 " + predicatetemp+"\t");
                    System.out.println(" 宾语 " + objecttemp);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            else {// object is a literal
//                System.out.print("宾语 \"" + object.toString() + "\"");
            }
//            System.out.println(" .");
        }
    }

}
