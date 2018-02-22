package openskins.asm;

import java.util.ArrayList;

public class PatchedClass
{
	public String name;
	public String nice_name;
	public String name_obf;
	public String hash; // Hash of the original class
	public ArrayList<PatchedMethod> methods;
	
	public PatchedClass(String name, String nice_name, String name_obf, String hash, ArrayList<PatchedMethod> methods)
	{
		this.name = name;
		this.nice_name = nice_name;
		this.name_obf = name_obf;
		this.methods = methods;
		this.hash = hash;
	}
}