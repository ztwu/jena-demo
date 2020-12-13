package com.iflytek.demo;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.*;
import java.net.URLDecoder;
import java.util.Iterator;

public class JenaOwlDemo {

    public void test1() throws IOException {
        //读取本体
        final String SOURCE = "http://ztwu4.iflytek.com/ontology";
        final String NS = SOURCE + "#";
        OntDocumentManager ontDocMgr = new OntDocumentManager();
        // set ontDocMgr's properties here
        ontDocMgr.addAltEntry(SOURCE, "file:data/owl/sanguoevent.owl");
        OntModelSpec ontModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
        ontModelSpec.setDocumentManager(ontDocMgr);
        // asserted ontology
        OntModel baseOnt = ModelFactory.createOntologyModel(ontModelSpec);
        baseOnt.read(SOURCE, "RDF/XML");
        // inferred ontology (after reasoning)
        OntModel infOnt = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, baseOnt);
//        System.out.println(infOnt);
        getData(infOnt);

        //构建本体
        OntClass furniture = baseOnt.createClass(NS+"Furniture");
        OntClass chair = baseOnt.createClass(NS+"Chiar");
        furniture.addSubClass(chair);
        OntClass bed = baseOnt.createClass(NS+"Bed");
        furniture.addSubClass(bed);
        OntClass zhongwen = baseOnt.createClass(NS+"中文");

        //输出owl文件到文件系统
        String filepath = "data/owl/testont.owl";
        FileOutputStream fileOS = new FileOutputStream(filepath);
        RDFWriter rdfWriter = baseOnt.getWriter("RDF/XML");
        rdfWriter.setProperty("showXMLDeclaration","true");
        rdfWriter.setProperty("showDoctypeDeclaration", "true");
        rdfWriter.write(baseOnt, fileOS, null);
        //用writer就不需要用下面的方法了
        //baseOnt.write(fileOS, "RDF/XML");
        fileOS.close();

    }

    public void test2() {
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        try {
            ontModel.read(new FileInputStream("data/owl/sanguoevent.owl"), "");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        ExtendedIterator<OntClass> extiter= ontModel.listClasses();
        while(extiter.hasNext()) {
            OntClass c = extiter.next();
            System.out.println(c.getModel().getGraph().getPrefixMapping().shortForm(c.getURI()));
            if (!c.isAnon()) {
                // 迭代显示当前类的直接父类
                for (Iterator it = c.listSuperClasses(); it.hasNext(); ) {
                    OntClass sp = (OntClass) it.next();
                    String str = c.getModel().getGraph().getPrefixMapping().shortForm(c.getURI()) + "'s superClass is ";//获取uri
                    String strSP = sp.getURI();
                    try {
                        str = str + ":" + strSP.substring(strSP.indexOf('#') + 1);
                        System.out.println("     Class" + str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } // super class ends
                // 迭代显示当前类的直接子类
                for (Iterator it = c.listSubClasses(); it.hasNext(); ) {
                    System.out.print("     Class");
                    OntClass sb = (OntClass) it.next();
                    System.out.println(c.getModel().getGraph().getPrefixMapping().shortForm(c.getURI()) + "'s suberClass is "
                            + sb.getModel().getGraph().getPrefixMapping().shortForm(sb.getURI()));
                }// suber class ends
                // 迭代显示与当前类相关的所有属性
//                for (Iterator ipp = c.listDeclaredProperties(); ipp.hasNext(); ) {
//                    OntProperty p = (OntProperty) ipp.next();
//                    if (p.isObjectProperty()) {
//                        System.out.println("ObjectProperty:" + p.getLocalName());
//                    }
//                }
                getProNum(c,ontModel);
            }
        }
    }

    public static int getProNum(OntClass c, OntModel ontModel) {
        int pNum=0;
        // 迭代显示与当前类相关的所有属性
        String str;
        // 列出所有的对象属性
        System.out.println("类——"+c.getLocalName()+"：的属性有：");
        for (Iterator allobjpry = ontModel.listObjectProperties(); allobjpry.hasNext();) {
            OntProperty objpry = (OntProperty) allobjpry.next();
            System.out.println("测试="+objpry);
            // 属性URI
            String objprystr = objpry.toString();
            // System.out.print("属性URI：" + objprystr + " ");
            str = objprystr.substring(objprystr.indexOf("#") + 1);
            System.out.println("属性值：" + str + " 属性：OP ");
            //列出对象属性的定义域，若定义域中的类与所要找寻属性的类名相同，属性加一
            for (Iterator dom = objpry.listDomain(); dom.hasNext();) {
                OntClass domain = (OntClass) dom.next();
                String s = domain.toString();
                String domainstr = s.substring(s.indexOf("#") + 1);
                if(domainstr.equals(c.getLocalName())) {
                    System.out.print(objpry.getLocalName()+",");
                    pNum++;
                }
            }
        }
        // 列出所有的数据属性
        for (Iterator alldatapry = ontModel.listDatatypeProperties(); alldatapry.hasNext();) {

            OntProperty datapry = (OntProperty) alldatapry.next();
            System.out.println("测试="+datapry);
            getData(datapry.getOntModel());
            // 属性URI
            String dataprystr = datapry.toString();
            // System.out.print("属性URI：" + dataprystr + " ");
            // 属性名
            str = dataprystr.substring(dataprystr.indexOf("#") + 1);
            System.out.println("属性值：" + str + " 属性： DP" );

            // 属性定义域
            //列出数据属性的定义域，若定义域中的类与所要找寻属性的类名相同，属性加一
            for (Iterator dom = datapry.listDomain(); dom.hasNext();) {
                OntClass domain = (OntClass) dom.next();
                String s = domain.toString();
                String domainstr = s.substring(s.indexOf("#") + 1);
                if(domainstr.equals(c.getLocalName())) {
                    System.out.print(datapry.getLocalName()+",");
                    pNum++;
                }
            }
        }
        return pNum;
    }

    public static void getData(OntModel model){
        StmtIterator iter = model.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement(); // get next statement
            //Resource subject = stmt.getSubject(); // get the subject
            //Property predicate = stmt.getPredicate(); // get the predicate
            //RDFNode object = stmt.getObject(); // get the object

            String subject = stmt.getSubject().toString(); // get the subject
            String predicate = stmt.getPredicate().toString(); // get the predicate
            RDFNode object = stmt.getObject(); // get the object

            if (object instanceof Resource) {
                try {
                    String objecttemp = URLDecoder.decode(object.toString(), "UTF-8");
                    String subjecttemp = URLDecoder.decode(subject.toString(), "UTF-8");
                    String predicatetemp = URLDecoder.decode(predicate.toString(), "UTF-8");
                    System.out.print("主语 " + subjecttemp + "\t");
                    System.out.print(" 谓语 " + predicatetemp + "\t");
                    System.out.println(" 宾语 " + objecttemp);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {// object is a literal
//                System.out.print("宾语 \"" + object.toString() + "\"");
            }
        }
    }

    public static void main (String[]args){
        JenaOwlDemo jenaOwlDemo = new JenaOwlDemo();
        try {
            jenaOwlDemo.test1();
//            jenaOwlDemo.test2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
