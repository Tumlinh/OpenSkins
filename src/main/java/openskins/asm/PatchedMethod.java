package openskins.asm;

import org.objectweb.asm.tree.InsnList;

public class PatchedMethod
{
	public String name;
	public String name_obf;
	public String desc;
	public InsnList instructions;
	
	public PatchedMethod(String name, String name_obf, String desc, InsnList instructions)
	{
		this.name = name;
		this.name_obf = name_obf;
		this.desc = desc; // Obfuscated description
		this.instructions = instructions;
	}
}