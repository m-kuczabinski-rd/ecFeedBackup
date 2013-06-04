package com.testify.ecfeed.parsers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;

public class EctParserTest {

	@Test
	public void testParseEctFile() {
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		EcWriter writer = new EcWriter(ostream);
		
		RootNode root = new RootNode("Root");
		ClassNode classNode1 = new ClassNode("com.testify.ecfeed.Class1");
		MethodNode method1_1 = new MethodNode("testMethod1");
		CategoryNode cat_1_1_1 = new CategoryNode("cat1", "int");
		cat_1_1_1.addPartition(new PartitionNode("MIN", Integer.MIN_VALUE));
		cat_1_1_1.addPartition(new PartitionNode("negative", (int)-1));
		cat_1_1_1.addPartition(new PartitionNode("zero",     (int) 0));
		cat_1_1_1.addPartition(new PartitionNode("positive", (int) 1));
		cat_1_1_1.addPartition(new PartitionNode("MAX", Integer.MAX_VALUE));
		method1_1.addCategory(cat_1_1_1);
		CategoryNode cat_1_1_2 = new CategoryNode("cat2", "boolean");
		cat_1_1_2.addPartition(new PartitionNode("true", true));
		cat_1_1_2.addPartition(new PartitionNode("false", false));
		method1_1.addCategory(cat_1_1_2);
		CategoryNode cat_1_1_3 = new CategoryNode("cat3", "double");
		cat_1_1_3.addPartition(new PartitionNode("-INF", Double.NEGATIVE_INFINITY));
		cat_1_1_3.addPartition(new PartitionNode("-MAX", -Double.MAX_VALUE));
		cat_1_1_3.addPartition(new PartitionNode("-1", (double)-1.0));
		cat_1_1_3.addPartition(new PartitionNode("-MIN", -Double.MIN_VALUE));
		cat_1_1_3.addPartition(new PartitionNode("zero", (double)0));
		cat_1_1_3.addPartition(new PartitionNode("MIN", Double.MIN_VALUE));
		cat_1_1_3.addPartition(new PartitionNode("1", (double)1.0));
		cat_1_1_3.addPartition(new PartitionNode("MAX", Double.MAX_VALUE));
		cat_1_1_3.addPartition(new PartitionNode("INF", Double.POSITIVE_INFINITY));
		method1_1.addCategory(cat_1_1_3);
		classNode1.addMethod(method1_1);
		
		
		MethodNode method1_2 = new MethodNode("testMethod2");
		CategoryNode cat_1_2_1 = new CategoryNode("cat1", "byte");
		cat_1_2_1.addPartition(new PartitionNode("MIN", Byte.MIN_VALUE));
		cat_1_2_1.addPartition(new PartitionNode("negative", (byte)-1));
		cat_1_2_1.addPartition(new PartitionNode("zero",     (byte) 0));
		cat_1_2_1.addPartition(new PartitionNode("positive", (byte) 1));
		cat_1_2_1.addPartition(new PartitionNode("MAX", Byte.MAX_VALUE));
		method1_2.addCategory(cat_1_2_1);
		CategoryNode cat_1_2_2 = new CategoryNode("cat1", "long");
		cat_1_2_2.addPartition(new PartitionNode("MIN", Long.MIN_VALUE));
		cat_1_2_2.addPartition(new PartitionNode("negative", (long)-1));
		cat_1_2_2.addPartition(new PartitionNode("zero",     (long) 0));
		cat_1_2_2.addPartition(new PartitionNode("positive", (long) 1));
		cat_1_2_2.addPartition(new PartitionNode("MAX", Long.MAX_VALUE));
		method1_2.addCategory(cat_1_2_2);
		CategoryNode cat_1_2_3 = new CategoryNode("cat2", "float");
		cat_1_2_3.addPartition(new PartitionNode("-INF",  Float.NEGATIVE_INFINITY));
		cat_1_2_3.addPartition(new PartitionNode("-MAX", -Float.MAX_VALUE));
		cat_1_2_3.addPartition(new PartitionNode("-1",   (float)-1.0));
		cat_1_2_3.addPartition(new PartitionNode("-MIN", -Float.MIN_VALUE));
		cat_1_2_3.addPartition(new PartitionNode("zero", (float)0));
		cat_1_2_3.addPartition(new PartitionNode("MIN",   Float.MIN_VALUE));
		cat_1_2_3.addPartition(new PartitionNode("1",    (float)1.0));
		cat_1_2_3.addPartition(new PartitionNode("MAX",   Float.MAX_VALUE));
		cat_1_2_3.addPartition(new PartitionNode("INF",   Float.POSITIVE_INFINITY));
		method1_2.addCategory(cat_1_2_3);
		CategoryNode cat_1_2_4 = new CategoryNode("cat3", "String");
		cat_1_2_4.addPartition(new PartitionNode("null",  null));
		cat_1_2_4.addPartition(new PartitionNode("character",  "a"));
		cat_1_2_4.addPartition(new PartitionNode("characters",  "aA"));
		cat_1_2_4.addPartition(new PartitionNode("empty",  ""));
		cat_1_2_4.addPartition(new PartitionNode("latin",  "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		method1_2.addCategory(cat_1_2_4);
		classNode1.addMethod(method1_2);
		root.addClass(classNode1);
		

		ClassNode classNode2 = new ClassNode("com.testify.ecfeed.Class2");
		MethodNode method_2_1 = new MethodNode("testMethod1");
		CategoryNode cat_2_1_1 = new CategoryNode("cat1", "char");
		cat_2_1_1.addPartition(new PartitionNode("zero", '\u0000'));
		cat_2_1_1.addPartition(new PartitionNode("one",  '\u0001'));
		cat_2_1_1.addPartition(new PartitionNode("a", 'a'));
		cat_2_1_1.addPartition(new PartitionNode("z", 'z'));
		cat_2_1_1.addPartition(new PartitionNode("A", 'A'));
		cat_2_1_1.addPartition(new PartitionNode("Z", 'Z'));
		cat_2_1_1.addPartition(new PartitionNode("non ASCII", '\u00A7'));
		cat_2_1_1.addPartition(new PartitionNode("max", '\uffff'));
		method_2_1.addCategory(cat_2_1_1);

		CategoryNode cat_2_1_2 = new CategoryNode("cat2", "short");
		cat_2_1_2.addPartition(new PartitionNode("MIN", Short.MIN_VALUE));
		cat_2_1_2.addPartition(new PartitionNode("negative", (short)-1));
		cat_2_1_2.addPartition(new PartitionNode("zero",     (short) 0));
		cat_2_1_2.addPartition(new PartitionNode("positive", (short) 1));
		cat_2_1_2.addPartition(new PartitionNode("MAX", Short.MAX_VALUE));
		method_2_1.addCategory(cat_2_1_2);
		classNode2.addMethod(method_2_1);
		root.addClass(classNode2);
		
		writer.writeXmlDocument(root);

		System.out.println("Generated document:\n" + ostream.toString());

		
		InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
		EcParser parser = new EcParser();
		RootNode parsedRoot = parser.parseEctFile(istream);
		
		assertEquals(root, parsedRoot);
	}
}
