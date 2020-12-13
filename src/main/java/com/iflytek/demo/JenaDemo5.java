package com.iflytek.demo;

import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import java.util.Iterator;

/**
 * created with idea
 * user:ztwu
 * date:2020/2/7
 * description
 */
public class JenaDemo5 {

    public static void main(String[] args) {
        //Model：Jena最核心的数据结构，其本质就是Jena中的知识库结构
        //构建一个最简单的Model
        Model myMod = ModelFactory.createDefaultModel();
        //定义例子的命名空间
        String finance = "http://www.example.org/kse/finance#";

        // 创建资源定义个体
        Resource shb= myMod.createResource(finance+ "孙宏斌");
        Resource rczg= myMod.createResource(finance+ "融创中国");

        //定义关系
        Property control= myMod.createProperty(finance+ "执掌");

        // 向模型中加入三元组往知识库中加入三元组
        myMod.add(shb, control, rczg);
        myMod.add(myMod.createResource(finance+"贾跃亭"), control, myMod.createResource(finance+"乐视网"));// 贾跃亭执掌乐视网
        myMod.add(myMod.createResource(finance+"融创中国"), RDF.type, myMod.createResource(finance+"地产公司"));// 融创中国是一种地产公司
        myMod.add(myMod.createResource(finance+"地产公司"), RDFS.subClassOf, myMod.createResource(finance+"公司"));
        myMod.add(myMod.createResource(finance+"公司"), RDFS.subClassOf, myMod.createResource(finance+"法人实体"));
        myMod.add(myMod.createResource(finance+"孙宏斌"), RDF.type, myMod.createResource(finance+"公司"));
        myMod.add(myMod.createResource(finance+"孙宏斌"), RDF.type, myMod.createResource(finance+"人"));
        myMod.add(myMod.createResource(finance+"人"), OWL.disjointWith, myMod.createResource(finance+"公司"));

        // 创建RDFS推理机
        InfModel inf_rdfs= ModelFactory.createRDFSModel(myMod);
        // 创建OWL推理机在普通的Model之上加了一个OWL推理机
        //构建一个含OWL推理功能的Model
        Reasoner reasoner= ReasonerRegistry.getOWLReasoner();
        InfModel inf_owl= ModelFactory.createInfModel(reasoner, myMod);

        // 查询原模型中 地产公司 与 法人实体 之间是否存在上下位关系
        // 查询输入类别s和o之间有无上下位关系
        JenaDemo5.subClassOf(inf_rdfs, myMod.getResource(finance+"地产公司"), myMod.getResource(finance+"法人实体"));
        JenaDemo5.subClassOf(inf_rdfs, myMod.getResource(finance+"融创中国"), myMod.getResource(finance+"法人实体"));

        // 类别补全，输出推理后 融创中国 的类型
        printStatements(inf_owl, rczg, RDF.type, null);

        // 不一致检测通过validate接口检测不一致
        ValidityReport validity= inf_owl.validate();

        if(validity.isValid()) {
            System.out.println("OK");//没有不一致
        } else{
            System.out.println("Conflicts");//存在不一致性
            for(Iterator i= validity.getReports(); i.hasNext(); ) {
                ValidityReport.Report report= (ValidityReport.Report)i.next();
                System.out.println(" - "+ report);
            }
        }
    }

    /**
  * 查询输入资源s与o之间是否存在上下位关系，存在则输出 yes，否则输出 no
  * @param m - 模型
  * @param s - 资源1
  * @param o - 资源2
  */
    public static void subClassOf(Model m, Resource s, Resource o) {
        // 遍历模型中的三元组
        for(StmtIterator i = m.listStatements(s, RDFS.subClassOf, o); i.hasNext(); ) {
            Statement stmt = i.nextStatement();
            System.out.println("yes!");
            return;
        }
        System.out.println("no!");
    }

    /**
   * 输出模型m中满足给定模式的三元组
      */
    public static void printStatements(Model m, Resource s, Property p, Resource o) {
        for (StmtIterator i = m.listStatements(s,p,o); i.hasNext(); ) {
            Statement stmt = i.nextStatement();
            System.out.println(" - " + PrintUtil.print(stmt));
        }
    }

}
