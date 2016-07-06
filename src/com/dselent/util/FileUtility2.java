package com.dselent.util;

import java.util.ArrayList;
import java.util.List;
import java.io.*;


public class FileUtility2
{

	public static List<String> readStrings(String fileName) throws IOException
	{
		List<String> stringList = new ArrayList<String>();
		
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line;
		
		while((line=bufferedReader.readLine()) != null)
		{
			stringList.add(line);
		}
		
		fileReader.close();
		bufferedReader.close();
		
		return stringList;
	}
	
	public static List<String> readStrings(File fileName) throws IOException
	{
		List<String> stringList = new ArrayList<String>();
		
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line;
		
		while((line=bufferedReader.readLine()) != null)
		{
			stringList.add(line);
		}
		
		fileReader.close();
		bufferedReader.close();
		
		return stringList;
	}	

	public static void writeStrings(String fileName, List<String> stringList) throws IOException
	{
		FileWriter fileWriter = new FileWriter(fileName);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		
		for(int i=0; i<stringList.size(); i++)
		{
			String theString = stringList.get(i).concat("\n");
			bufferedWriter.write(theString, 0, theString.length());
		}
		
		bufferedWriter.flush();
		fileWriter.close();
		bufferedWriter.close();
		
	}
	
	public static void writeStrings(File fileName, List<String> stringList) throws IOException
	{
		FileWriter fileWriter = new FileWriter(fileName);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		
		for(int i=0; i<stringList.size(); i++)
		{
			String theString = stringList.get(i).concat("\n");
			bufferedWriter.write(theString, 0, theString.length());
		}
		

		bufferedWriter.flush();
		fileWriter.close();
		bufferedWriter.close();
	}
	
	
	public static void writeString(String fileName, String theString) throws IOException
	{
		FileWriter fileWriter = new FileWriter(fileName);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		bufferedWriter.write(theString, 0, theString.length());

		bufferedWriter.flush();
		fileWriter.close();
		bufferedWriter.close();
		
	}

	public static void writeString(String fileName, String theString, int offset, int length) throws IOException
	{
		FileWriter fileWriter = new FileWriter(fileName);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		bufferedWriter.write(theString, offset, length);
		
		bufferedWriter.flush();
		fileWriter.close();
		bufferedWriter.close();		
	}

	public static void writeString(File fileName, String theString, boolean append) throws IOException
	{
		FileWriter fileWriter = new FileWriter(fileName, append);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		bufferedWriter.write(theString, 0, theString.length());
		
		bufferedWriter.flush();
		fileWriter.close();
		bufferedWriter.close();		
	}
	
	public static void writeString(File fileName, String theString, int offset, int length) throws IOException
	{
		FileWriter fileWriter = new FileWriter(fileName);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		bufferedWriter.write(theString, offset, length);
		
		bufferedWriter.flush();
		fileWriter.close();
		bufferedWriter.close();
	}
	
	
	/////////////////////////////////////////////////////////////////////
	
	//only works for files of int size
	public static byte[] readBytes(File fileName) throws IOException
	{
		FileInputStream fis = new FileInputStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(fis);

		int fileSize = (int)fileName.length();

		byte[]  bytes = new byte[fileSize];
		bis.read(bytes);

		fis.close();	
		bis.close();

		return bytes;
	}

	//only works for files of int size
	public static byte[] readBytes(String fileName) throws IOException
	{
		FileInputStream fis = new FileInputStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(fis);

		File file = new File(fileName);
		int fileSize = (int)file.length();

		byte[]  bytes = new byte[fileSize];
		bis.read(bytes);

		fis.close();		
		bis.close();

		return bytes;
	}

	//only works for files of int size
	public static byte[] readBytes(File fileName, int offset, int length) throws IOException
	{
		FileInputStream fis = new FileInputStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(fis);

		int fileSize = (int)fileName.length();

		byte[]  bytes = new byte[fileSize];
		bis.read(bytes, offset, length);

		fis.close();		
		bis.close();

		return bytes;
	}

	//only works for files of int size
	public static byte[] readBytes(String fileName, int offset, int length) throws IOException
	{
		FileInputStream fis = new FileInputStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(fis);

		File file = new File(fileName);
		int fileSize = (int)file.length();

		byte[]  bytes = new byte[fileSize];
		bis.read(bytes, offset, length);

		fis.close();		
		bis.close();

		return bytes;
	}

	public static void writeBytes(File fileName, byte[] bytes) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(fileName);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		bos.write(bytes);

		bos.flush();
		fos.close();
		bos.close();
	}


	public static void writeBytes(String fileName, byte[] bytes) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(fileName);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		bos.write(bytes);

		bos.flush();
		fos.close();
		bos.close();
	}
	
	public static void writeBytes(File fileName, byte[] bytes, int offset, int length) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(fileName);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		bos.write(bytes, offset, length);

		bos.flush();
		fos.close();
		bos.close();
	}


	public static void writeBytes(String fileName, byte[] bytes, int offset, int length) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(fileName);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		bos.write(bytes, offset, length);

		bos.flush();
		fos.close();
		bos.close();
	}

	public static void writeBytes(String fileName, Object object) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(fileName);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
   		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		byte[] bytes = baos.toByteArray();

		bos.write(bytes);

		bos.flush();
		fos.close();
		bos.close();
	}
	

	/////////////////////////////////////////////////////////////////
	
	
	public static List<Object> readObjects(String filePath) throws IOException, ClassNotFoundException
	{
		List<Object> objectList = new ArrayList<Object>();
		
		FileInputStream fis = new FileInputStream(filePath);
		BufferedInputStream bis = new BufferedInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(bis);

		int numberOfObjects = 0;
		Object object;

		object = ois.readObject();

		if(object != null)
		{
			//assume first object is a number representing the number of objects

			numberOfObjects = (int)object;
		}


		for(int i=0; i<numberOfObjects; i++)
		{
			object = ois.readObject();
			objectList.add(object);
		}

		fis.close();
		bis.close();
		ois.close();
		
		return objectList;
	}
	
	
	public static void writeObjects(String filePath, List<?> objectList) throws IOException, ClassNotFoundException
	{
		FileOutputStream fos = new FileOutputStream(filePath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(bos);

		for(int i=0; i<objectList.size(); i++)
		{
			oos.writeObject(objectList.get(i));
		}
		
		oos.flush();
		fos.close();
		bos.close();
		oos.close();
	}
	

	public static void convertCharset(String inputPath, String outputPath, String charset) throws IOException
	{
		FileReader fileReader = new FileReader(inputPath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
		OutputStreamWriter outputStreamWriter = new  OutputStreamWriter(fileOutputStream, charset);

		String line;
		
		while((line=bufferedReader.readLine()) != null)
		{
			if(line.length() > 0)
			{
				outputStreamWriter.write(line, 0, line.length());
				outputStreamWriter.write("\n");
			}
		}
		
		
		bufferedReader.close();
		outputStreamWriter.close();
	}

	@SuppressWarnings("unused")
	public static void main(String args[]) throws Exception
	{
		List<String> stringTest = FileUtility2.readStrings("Input.csv");
		FileUtility2.writeStrings("TestStringOutput.csv", stringTest);

		List<Object> objectOutputList = new ArrayList<Object>(stringTest);
		objectOutputList.add(0, objectOutputList.size());

		FileUtility2.writeObjects("TestObjectOutput.jobj", objectOutputList);
		List<Object> objectInputList = FileUtility2.readObjects("TestObjectOutput.jobj");

		FileUtility2.writeBytes("TestByteOutput.jobj", objectInputList);
		byte[] bytes = FileUtility2.readBytes("TestByteOutput.jobj");
	}

}