package graphlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.List;

import tree.GraphletTree;

public class GraphletIO {
	
	public static void save(GraphletTree<?,?> tree, String fileName) {
		try (FileOutputStream fos = new FileOutputStream(fileName);
				ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(tree);
		} catch (FileNotFoundException e) {
			// Error in accessing the file
			e.printStackTrace();
		} catch (IOException e) {
			// Error in converting the Student
			e.printStackTrace();
		}
	}

	public static void save(List<AbstractGraphlet<?>> g, String fileName) {
		try (FileOutputStream fos = new FileOutputStream(fileName);
				ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(g);
		} catch (FileNotFoundException e) {
			// Error in accessing the file
			e.printStackTrace();
		} catch (IOException e) {
			// Error in converting the Student
			e.printStackTrace();
		}
	}
	
	public static void save(List<AbstractGraphlet<?>> l) {
		save(l,l.get(0).name()+"s-"+l.get(0).getOrder()+".gpl");
	}
	
	public static void draw(List<AbstractGraphlet<?>> g, String fileName) {
		try {
			PrintWriter pw = new PrintWriter(fileName);
			pw.print("%!PS\n/Times-Roman findfont\n10 scalefont\nsetfont\n");
			for(AbstractGraphlet<?> graphlet:g) {
				pw.println(graphlet.toPS());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<AbstractGraphlet<?>> read(String fileName) {
		List<AbstractGraphlet<?>> g = null;
		if(fileName.substring(fileName.length()-4).equals(".gpl"))
		try (FileInputStream fis = new FileInputStream(fileName); ObjectInputStream ois = new ObjectInputStream(fis);) {
			g = (List<AbstractGraphlet<?>>) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return g;
	}
	
//	public static List<AbstractGraphlet<?>> read(String type, int order){
//		return read(type+ "-")
//	}
//	
//	public static List<Graphlet<?>> getGraphlets(String type, int order){
//		
//	}
	
	
}
